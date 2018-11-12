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
 * Created by aotulana on 11/12/2018.
 */
public class DifferentiateTest extends TestBase {

    //Before tests are run, the base URI is initialized
    @BeforeTest
    public void setBaseURI() throws IOException {
        initializeBaseURI();
    }

    @BeforeMethod
    public void uniqueIDForEachMethod() {
        generateUniqueID();
    }

    @Test
    public void testDifferOfEqualSidesShouldReturnEqual() {
        setSideValue(999, "left", "continuous12345");
        setSideValue(999, "right", "continuous12345");
        differentiateSides(999);
        convertResponseToJson(response); //Convert raw response to JSON format
        type = jSONResponse.getString("type");
        Assert.assertEquals(type, "EQUAL");
    }

    @Test
    public void testDifferAgainstNullLeftSideShouldReturnDifferentLength() {
        setSideValue(id, "right", "comingoverthere");
        differentiateSides(id);
        convertResponseToJson(response); //Convert raw response to JSON format
        detail = jSONResponse.getString("detail");
        type = jSONResponse.getString("type");
        Assert.assertEquals(detail, "Left side contains no value.");
        Assert.assertEquals(type, "DIFFERENT_LENGTH");
    }

    @Test
    public void testDifferAgainstNullRightSideShouldReturnDifferentLength() {
        setSideValue(id, "left", "comingoverthere");
        differentiateSides(id);
        convertResponseToJson(response); //Convert raw response to JSON format
        detail = jSONResponse.getString("detail");
        type = jSONResponse.getString("type");
        Assert.assertEquals(detail, "Right side contains no value.");
        Assert.assertEquals(type, "DIFFERENT_LENGTH");
    }

    @Test
    public void testDifferForDifferentLengthShouldReturnDifferentLength() {
        setSideValue(id, "left", "Marvelous Things!!");
        setSideValue(id, "right", "Marvelous");
        differentiateSides(id);
        convertResponseToJson(response);
        type = jSONResponse.getString("type");
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
        convertResponseToJson(response); //Convert raw response to JSON format
        detail = jSONResponse.getString("detail");
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
