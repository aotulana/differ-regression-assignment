package requests;

import hook.TestBase;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import responseModels.ErrorResponse;
import responseModels.EqualDiffSuccessResponse;
import responseModels.UnequalDiffSuccessResponse;
import utilities.Endpoints;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static utilities.TestUtililities.*;

/**
 * This class contains all tests for the Differ Service
 *
 * @author Adebowale Otulana
 */
public class DifferServiceTest extends TestBase {

    private RequestSpecification requestSpecification;

    /**
     * Before the tests, it initializes the base URI
     * which will be used by each test method.
     *
     * @throws IOException
     */
    @BeforeTest
    public void setBaseURI() throws IOException {
        initializeBaseURI();
    }

    /**
     * Generates unique ID before each test method is run
     * The generated ID is always positive
     */
    @BeforeMethod
    public void beforeMethod() {
        generateID();
        requestSpecification = given();
    }

    @Test
    public void when_EqualSidesAreDiffed_Expect_Equal() {
        //Set ID, side and value for the left side
        setSideValue(999, "left", "continuous12345");

        //Set ID, side and value for the left side
        setSideValue(999, "right", "continuous12345");

        //Diff the sides
        differentiateSides(999);

        //Get response body
        ResponseBody body = response.getBody();
        EqualDiffSuccessResponse responseBody = body.as(EqualDiffSuccessResponse.class);

        //Verify that the type is EQUAL
        Assert.assertEquals(responseBody.type, "EQUAL");
    }

    @Test
    public void when_SidesAreDiffedAndLeftSideIsNull_Expect_DifferentLengthAndLeftHasNoValue() {
        //Set ID, side and value for right side
        setSideValue(id, "right", "comingoverthere");

        //Diff the sides
        differentiateSides(id);

        //Get response body
        ResponseBody body = response.getBody();
        UnequalDiffSuccessResponse responseBody = body.as(UnequalDiffSuccessResponse.class);

        //Verify that the detail is 'Left side contains no value.'
        Assert.assertEquals(responseBody.detail, "Left side contains no value.");

        //Verify that the type is DIFFERENT_LENGTH
        Assert.assertEquals(responseBody.type, "DIFFERENT_LENGTH");
    }

    @Test
    public void when_SidesAreDiffedAndRightSideIsNull_Expect_DifferentLengthAndRightHasNoValue() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "comingoverthere");

        //Diff the sides
        differentiateSides(id);

        //Get response body
        ResponseBody body = response.getBody();
        UnequalDiffSuccessResponse responseBody = body.as(UnequalDiffSuccessResponse.class);

        //Verify that the detail is 'Right side contains no value.'
        Assert.assertEquals(responseBody.detail, "Right side contains no value.");

        //Verify that the type is DIFFERENT_LENGTH
        Assert.assertEquals(responseBody.type, "DIFFERENT_LENGTH");
    }

    @Test
    public void when_SidesOfDifferentLengthsAreDiffed_Expect_DifferentLength() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "Marvelous Things!!");

        //Set ID, side and value for right side
        setSideValue(id, "right", "Marvelous");

        //Diff the sides
        differentiateSides(id);

        //Get response body
        ResponseBody body = response.getBody();
        UnequalDiffSuccessResponse responseBody = body.as(UnequalDiffSuccessResponse.class);

        //Verify that the type is DIFFERENT_LENGTH
        Assert.assertEquals(responseBody.type, "DIFFERENT_LENGTH");
    }

    @Test
    public void when_IDThatIsNotInitializedIsDiffed_Expect_IDNotInitialized() {
        //Diff the sides without setting sides
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",id). //Sets ID
                when().
                        get(Endpoints.GET_DIFF).
                then().
                        assertThat().
                            statusCode(404). //Verify HTTP Status Code from response
                            contentType(ContentType.JSON). //Verify Content Type of response
                extract().
                        response();

        //Get response body
        ResponseBody body = response.getBody();
        ErrorResponse responseBody = body.as(ErrorResponse.class);

        //Verify that the errorCode is 404
        Assert.assertEquals(responseBody.errorCode, "404");

        //Verify that the errorMessage is 'ID <id> not initialized.'
        Assert.assertEquals(responseBody.errorMessage, "ID " + id + " not initialized.");
    }

    @Test
    public void when_SidesOfSameLengthButDifferentCharactersAreDiffed_Expect_PositionOfDifferentCharacters() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "Long string to test the position of different characters.");

        //Set ID, side and value for right side
        setSideValue(id, "right", "Long strung to test the position of different characters!");

        //Diff the sides
        differentiateSides(id);

        //Get response body
        ResponseBody body = response.getBody();
        UnequalDiffSuccessResponse responseBody = body.as(UnequalDiffSuccessResponse.class);

        //Verify that the detail is 'Values are different on char(s) [11] [75].'
        Assert.assertEquals(responseBody.detail, "Values are different on char(s) [11] [75].");

        //Verify that the type is DIFFERENT_CHARS
        Assert.assertEquals(responseBody.type, "DIFFERENT_CHARS");
    }

    @Test
    public void when_SidesOfSameLengthButDifferentSuccessiveCharactersAreDiffed_Expect_RangedPositionOfDifferentCharacters() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "Marvelous");

        //Set ID, side and value for right side
        setSideValue(id, "right", "12345abcd");

        //Diff the sides
        differentiateSides(id);

        //Get response body
        ResponseBody body = response.getBody();
        UnequalDiffSuccessResponse responseBody = body.as(UnequalDiffSuccessResponse.class);

        //Verify that the detail is 'Values are different on char(s) [0-5] [7-11].'
        Assert.assertEquals(responseBody.detail, "Values are different on char(s) [0-5] [7-11].");

        //Verify that the type is DIFFERENT_CHARS
        Assert.assertEquals(responseBody.type, "DIFFERENT_CHARS");
    }

    @Test
    public void when_DiffRequestIsDelete_Expect_405MethodNotAllowed() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "Marvelous Things!!");

        //Set ID, side and value for right side
        setSideValue(id, "right", "Marvelous");

        //Diff the sides with DELETE method
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",id). //Sets ID
                when().
                        delete(Endpoints.GET_DIFF).//DELETE method
                then().
                        assertThat().
                            statusCode(405). //Verify HTTP Status Code from response
                extract().
                        response();
    }
}
