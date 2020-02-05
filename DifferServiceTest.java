package requests;

import hook.TestBase;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import responseModels.LengthTypeDetailResponse;
import responseModels.ErrorResponse;
import responseModels.LengthTypeResponse;
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
    public void uniqueIDForEachMethod() {
        generateID();
    }

    @Test
    public void when_EqualSidesAreDiffed_Expect_Equal() {
        //Set ID, side and value for the left side
        setSideValue(999, "left", "\"" + encodeInBase64("continuous12345") + "\"");

        //Set ID, side and value for the left side
        setSideValue(999, "right", "\"" + encodeInBase64("continuous12345") + "\"");

        //Diff the sides
        Response response = differentiateSides(999);

        //Deserialize to a "Unequal Length Response" Object
        LengthTypeResponse lengthTypeResponse = response.as(LengthTypeResponse.class);

        //Get Type from response
        String type;
        type = lengthTypeResponse.getType();

        //Verify that the type is EQUAL
        Assert.assertEquals(type, "EQUAL");
    }

    @Test
    public void when_SidesAreDiffedAndLeftSideIsNull_Expect_DifferentLengthAndLeftHasNoValue() {
        //Set ID, side and value for right side
        setSideValue(id, "right", "\"" + encodeInBase64("comingoverthere") + "\"");

        //Diff the sides
        Response response = differentiateSides(id);

        //Deserialize to a "Type-Detail Response" Object
        LengthTypeDetailResponse lengthTypeDetailResponse = response.as(LengthTypeDetailResponse.class);

        //Get Detail from response
        String detail;
        detail = lengthTypeDetailResponse.getDetail();

        //Get Type from response
        String type;
        type = lengthTypeDetailResponse.getType();

        //Verify that the detail is 'Left side contains no value.'
        Assert.assertEquals(detail, "Left side contains no value.");

        //Verify that the type is DIFFERENT_LENGTH
        Assert.assertEquals(type, "DIFFERENT_LENGTH");
    }

    //@Test
    public void when_SidesAreDiffedAndRightSideIsNull_Expect_DifferentLengthAndRightHasNoValue() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "\"" + encodeInBase64("comingoverthere") + "\"");

        //Diff the sides
        Response response = differentiateSides(id);

        //Deserialize to a "Type-Detail Response" Object
        LengthTypeDetailResponse lengthTypeDetailResponse = response.as(LengthTypeDetailResponse.class);

        //Get Detail from response
        String detail;
        detail = lengthTypeDetailResponse.getDetail();

        //Get Type from response
        String type;
        type = lengthTypeDetailResponse.getType();

        //Verify that the detail is 'Right side contains no value.'
        Assert.assertEquals(detail, "Right side contains no value.");

        //Verify that the type is DIFFERENT_LENGTH
        Assert.assertEquals(type, "DIFFERENT_LENGTH");
    }

    @Test
    public void when_SidesOfDifferentLengthsAreDiffed_Expect_DifferentLength() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "\"" + encodeInBase64("Marvelous Things!!") + "\"");

        //Set ID, side and value for right side
        setSideValue(id, "right", "\"" + encodeInBase64("Marvelous") + "\"");

        //Diff the sides
        Response response = differentiateSides(id);

        //Deserialize to a "Unequal Length Response" Object
        LengthTypeResponse lengthTypeResponse = response.as(LengthTypeResponse.class);

        //Get Type from response
        String type;
        type = lengthTypeResponse.getType();

        //Verify that the type is DIFFERENT_LENGTH
        Assert.assertEquals(type, "DIFFERENT_LENGTH");
    }

    @Test
    public void when_IDThatIsNotInitializedIsDiffed_Expect_IDNotInitialized() {
        //Diff the sides without setting sides
        Response response = differentiateSides(id);

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Deserialize to an "Error Response" Object
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        //Get Error Code from response
        int errorCode;
        errorCode = errorResponse.getErrorCode();

        //Get Error Message
        String errorMessage;
        errorMessage = errorResponse.getErrorMessage();

        //Verify that the errorCode is 404
        Assert.assertEquals(errorCode, 404);

        //Verify that the HTTP Status Code is 404
        Assert.assertEquals(statusCode, 404);

        //Verify that the errorMessage is 'ID <id> not initialized.'
        Assert.assertEquals(errorMessage, "ID " + id + " not initialized.");
    }

    @Test
    public void when_SidesOfSameLengthButDifferentCharactersAreDiffed_Expect_PositionOfDifferentCharacters() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "\"" + encodeInBase64("Long string to test the position of different characters.") + "\"");

        //Set ID, side and value for right side
        setSideValue(id, "right", "\"" + encodeInBase64("Long strung to test the position of different characters!") + "\"");

        //Diff the sides
        Response response = differentiateSides(id);

        //Deserialize to a "Type-Detail Response" Object
        LengthTypeDetailResponse lengthTypeDetailResponse = response.as(LengthTypeDetailResponse.class);

        //Get Detail from response
        String detail;
        detail = lengthTypeDetailResponse.getDetail();

        //Get Type from response
        String type;
        type = lengthTypeDetailResponse.getType();

        //Verify that the detail is 'Values are different on char(s) [11] [75].'
        Assert.assertEquals(detail, "Values are different on char(s) [11] [75].");

        //Verify that the type is DIFFERENT_CHARS
        Assert.assertEquals(type, "DIFFERENT_CHARS");
    }

    @Test
    public void when_SidesOfSameLengthButDifferentSuccessiveCharactersAreDiffed_Expect_RangedPositionOfDifferentCharacters() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "\"" + encodeInBase64("Marvelous") + "\"");

        //Set ID, side and value for right side
        setSideValue(id, "right", "\"" + encodeInBase64("12345abcd") + "\"");

        //Diff the sides
        Response response = differentiateSides(id);

        //Deserialize to a "Type-Detail Response" Object
        LengthTypeDetailResponse lengthTypeDetailResponse = response.as(LengthTypeDetailResponse.class);

        //Get Detail from response
        String detail;
        detail = lengthTypeDetailResponse.getDetail();

        //Get Type from response
        String type;
        type = lengthTypeDetailResponse.getType();

        //Verify that the detail is 'Values are different on char(s) [0-5] [7-11].'
        Assert.assertEquals(detail, "Values are different on char(s) [0-5] [7-11].");

        //Verify that the type is DIFFERENT_CHARS
        Assert.assertEquals(type, "DIFFERENT_CHARS");
    }

    @Test
    public void when_DiffRequestIsDelete_Expect_405MethodNotAllowed() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "Marvelous Things!!");

        //Set ID, side and value for right side
        setSideValue(id, "right", "Marvelous");

        //Diff the sides with DELETE method
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
