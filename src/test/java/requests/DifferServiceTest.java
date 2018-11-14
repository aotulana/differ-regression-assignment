package requests;

import hook.TestBase;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static utilities.Endpoints.diffSidesPath;
import static utilities.TestUtililities.*;

/**
 * This class contains all tests for the Differ Service
 *
 * @author Adebowale Otulana
 */
public class DifferServiceTest extends TestBase {

    /**
     * Before the tests, it initializes the base URI which will be used by each test method.
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
        generateUniqueID();
    }

    @Test
    public void testDifferOfEqualSidesShouldReturnEqual() {
        //Set ID, side and value for the left side
        setSideValue(999, "left", "continuous12345");

        //Set ID, side and value for the left side
        setSideValue(999, "right", "continuous12345");

        //Diff the sides
        differentiateSides(999);

        //Convert raw response to JSON format
        convertResponseToJson(response);

        //Get type from the JSON response
        type = jSONResponse.getString("type");

        //Verify that the type is EQUAL
        Assert.assertEquals(type, "EQUAL");
    }

    @Test
    public void testDifferAgainstNullLeftSideShouldReturnDifferentLength() {
        //Set ID, side and value for right side
        setSideValue(id, "right", "comingoverthere");

        //Diff the sides
        differentiateSides(id);

        //Convert RESTAssured raw response to JSON format
        convertResponseToJson(response);

        //Get detail from the JSON response
        detail = jSONResponse.getString("detail");

        //Get type from the JSON response
        type = jSONResponse.getString("type");

        //Verify that the detail is 'Left side contains no value.'
        Assert.assertEquals(detail, "Left side contains no value.");

        //Verify that the type is DIFFERENT_LENGTH
        Assert.assertEquals(type, "DIFFERENT_LENGTH");
    }

    @Test
    public void testDifferAgainstNullRightSideShouldReturnDifferentLength() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "comingoverthere");

        //Diff the sides
        differentiateSides(id);

        //Convert RESTAssured raw response to JSON format
        convertResponseToJson(response);

        //Get detail from the JSON response
        detail = jSONResponse.getString("detail");

        //Get type from the JSON response
        type = jSONResponse.getString("type");

        //Verify that the detail is 'Right side contains no value.'
        Assert.assertEquals(detail, "Right side contains no value.");

        //Verify that the type is DIFFERENT_LENGTH
        Assert.assertEquals(type, "DIFFERENT_LENGTH");
    }

    @Test
    public void testDifferForDifferentLengthShouldReturnDifferentLength() {
        //Set ID, side and value for left side
        setSideValue(id, "left", "Marvelous Things!!");

        //Set ID, side and value for right side
        setSideValue(id, "right", "Marvelous");

        //Diff the sides
        differentiateSides(id);

        //Convert RESTAssured raw response to JSON format
        convertResponseToJson(response);

        //Get type from the JSON response
        type = jSONResponse.getString("type");

        //Verify that the type is DIFFERENT_LENGTH
        Assert.assertEquals(type, "DIFFERENT_LENGTH");
    }

    @Test
    public void testDifferNotInitializedIDShouldReturnNotFound() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",id). //Sets ID
                when().
                        get(diffSidesPath()).
                then().
                        assertThat().
                            statusCode(404). //Verify HTTP Status Code from response
                            contentType(ContentType.JSON). //Verify Content Type of response
                extract().
                        response();

        convertResponseToJson(response); //Convert raw response to JSON format
        errorCode = jSONResponse.getInt("errorCode");
        errorMessage = jSONResponse.getString("errorMessage");
        Assert.assertEquals(errorCode, Integer.valueOf(404));
        Assert.assertEquals(errorMessage, "ID " + id + " not initialized.");
    }

    @Test
    public void test1DifferForDifferentCharactersShouldReturnLocationOfCharacters() {
        setSideValue(id, "left", "Long string to test the position of different characters.");
        setSideValue(id, "right", "Long strung to test the position of different characters!");
        differentiateSides(id);
        convertResponseToJson(response); //Convert raw response to JSON format
        detail = jSONResponse.getString("detail");
        type = jSONResponse.getString("type");
        Assert.assertEquals(detail, "Values are different on char(s) [11] [75].");
        Assert.assertEquals(type, "DIFFERENT_CHARS");
    }

    @Test
    public void test2DifferForDifferentCharactersShouldReturnLocationOfCharacters() {
        setSideValue(id, "left", "Marvelous");
        setSideValue(id, "right", "12345abcd");
        differentiateSides(id);
        convertResponseToJson(response); //Convert raw RESTAssured response to JSON format
        detail = jSONResponse.getString("detail"); //Get detail key in the JSON response
        type = jSONResponse.getString("type");
        Assert.assertEquals(detail, "Values are different on char(s) [0-5] [7-11].");
        Assert.assertEquals(type, "DIFFERENT_CHARS");
    }

    @Test
    public void testDeleteMethodOnDifferShouldReturnMethodNotAllowed() {
        setSideValue(id, "left", "Marvelous Things!!");
        setSideValue(id, "right", "Marvelous");
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",id). //Sets ID
                when().
                        delete(diffSidesPath()).
                then().
                        assertThat().
                            statusCode(405). //Verify HTTP Status Code from response
                extract().
                        response();
    }
}
