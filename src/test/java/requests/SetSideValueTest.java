package requests;

import hook.TestBase;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static utilities.TestUtililities.*;
import static utilities.Endpoints.setSideValuePath;

/**
 * This class contains all tests for the "Set Side Value" endpoint
 *
 * @author Adebowale Otulana
 */
public class SetSideValueTest extends TestBase {

    //Before tests are run, the base URI is initialized
    @BeforeTest
    public void setBaseURI() throws IOException {
        initializeBaseURI();
    }

    @Test
    public void testNonBase64DataShouldReturnDataNotBase64Exception() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",10).
                        pathParam("side", "left").
                        body("\"abujfdbfjawsasd\""). //Data not base64 encoded
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();

        jSONResponse = rawResponseToJson(response); //Convert raw response to JSON format
        errorCode = jSONResponse.getInt("errorCode"); //Get error code from response JSON
        errorMessage = jSONResponse.getString("errorMessage"); //Get error message from response JSON
        Assert.assertEquals(errorCode, Integer.valueOf(415));
        Assert.assertEquals(errorMessage, "Data in body not Base64 formatted.");
    }

    @Test
    public void testUndefinedSideShouldReturnSideNameNotSupportedException() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",1).
                        pathParam("side", "up"). //Undefined (up) side
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(501). //Verify HTTP Status Code
                extract().
                        response();

        jSONResponse = rawResponseToJson(response); //Converts raw response to JSON format
        errorCode = jSONResponse.getInt("errorCode"); //Get error code from JSON response
        errorMessage = jSONResponse.getString("errorMessage"); //Get error message from response JSON
        Assert.assertEquals(errorCode, Integer.valueOf(501));
        Assert.assertEquals(errorMessage, "This side is not supported, please use either 'left' or 'right'.");
    }

    @Test
    public void testEmptyStringSideShouldReturnSideNameNotSupportedException() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",99).
                        pathParam("side", ""). //Empty String side parameter
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(405). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void testXmlContentTypeShouldReturnUnsupportedMediaType() {
        response =
                given().
                        contentType(ContentType.XML). //XML Content Type
                        pathParam("id",64).
                        pathParam("side", "right").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void testTextContentTypeShouldReturnUnsupportedMediaType() {
        response =
                given().
                        contentType(ContentType.TEXT). //Text Content Type
                        pathParam("id",71).
                        pathParam("side", "right").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void testHtmlContentTypeShouldReturnUnsupportedMediaType() {
        response =
                given().
                        contentType(ContentType.HTML). //HTML Content Type
                        pathParam("id",34).
                        pathParam("side", "right").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void testNoContentTypeShouldReturnUnsupportedMediaType() {
        response =
                given().
                        //contentType(ContentType.JSON).    //No Content Type
                        pathParam("id",18).
                        pathParam("side", "right").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void testPutMethodShouldReturnMethodNotAllowed() {
        response =
                given().
                        pathParam("id",19).
                        pathParam("side", "left").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        put(setSideValuePath()). //PUT method instead of POST
                then().
                        assertThat().
                            statusCode(405). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void testNonIntegerIDShouldReturnMethodNotAllowed() {
        response =
                given().
                        pathParam("id","invalid"). //Invalid ID
                        pathParam("side", "right").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        put(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(405). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void testEmptyBodyShouldReturnBadRequest() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",22).
                        pathParam("side", "right").
                        body(""). //Empty Body
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(400). //Verify HTTP Status Code
                            contentType(ContentType.JSON).
                extract().
                        response();

        jSONResponse = rawResponseToJson(response); //Convert raw response to JSON format
        errorCode = jSONResponse.getInt("errorCode"); //Get error code from response JSON
        errorMessage = jSONResponse.getString("errorMessage"); //Get error message from response JSON
        Assert.assertEquals(errorCode, Integer.valueOf(400));
        Assert.assertEquals(errorMessage, "Value in request body cannot be empty.");
    }

    @Test
    public void testLeftSideShouldReturnAcceptedLeftSideBase64Data() {
        setSideValue(10, "left", "123456789000"); //Value entered is encoded in the method

        jSONResponse = rawResponseToJson(response); //Convert raw response to JSON format
        leftValue = jSONResponse.getString("left"); //Get left value from response
        Assert.assertEquals(leftValue, encodeInBase64("123456789000"));
    }

    @Test
    public void testRightSideShouldReturnAcceptedRightSideBase64Data() {
        setSideValue(21, "right", "12345abcd"); //Value entered is encoded by the method

        jSONResponse = rawResponseToJson(response); //Convert raw response to JSON format
        rightValue = jSONResponse.getString("right"); //Get right value from response
        Assert.assertEquals(rightValue, encodeInBase64("12345abcd"));
    }

    @Test
    public void testLeftandRideSidesShouldReturnAcceptedDataForBothSides() {
        //Generate unique ID using timestamp and convert to long
        String timestamp = String.format("%ts",new Date());
        long id = Long.parseLong(timestamp);

        //Set the same unique ID for both left and right sides
        setSideValue(id, "left", "adebowale");
        setSideValue(id, "right", "formatting12345");

        JsonPath rightSideResponse =  rawResponseToJson(response); //Convert right side response to JSON format
        leftValue = rightSideResponse.getString("left"); //Get left value from right side response
        rightValue = rightSideResponse.getString("right"); //Get right value from right side response

        Assert.assertEquals(leftValue, encodeInBase64("adebowale"));
        Assert.assertEquals(rightValue, encodeInBase64("formatting12345"));
    }
}
