package requests;

import hook.TestBase;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static utilities.TestUtililities.*;
import static utilities.Endpoints.setSideValuePath;

/**
 * This class contains all tests for the Side Service.
 * Each test method sends an HTTP request to the side service
 * and they run independently, not relying on result from other test methods
 *
 * @author Adebowale Otulana
 */
public class SideServiceTest extends TestBase {

    /**
     * Before the tests, it initializes the base URI which will be used by each test method.
     *
     * @throws IOException
     */
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

        convertResponseToJson(response); //Convert raw response to JSON format
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

        convertResponseToJson(response); //Converts raw response to JSON format
        errorCode = jSONResponse.getInt("errorCode"); //Get error code from JSON response
        errorMessage = jSONResponse.getString("errorMessage"); //Get error message from response JSON
        Assert.assertEquals(errorCode, Integer.valueOf(501));
        Assert.assertEquals(errorMessage, "This side is not supported, please use either 'left' or 'right'.");
    }

    @Test
    public void testWhiteSpacesSideParameterShouldReturnSideNameNotSupportedException() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",1).
                        pathParam("side", "   "). //White spaces as side parameter
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(501). //Verify HTTP Status Code
                extract().
                        response();

        convertResponseToJson(response); //Converts raw response to JSON format
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
    public void testEmptyStringIDShouldReturnNotFound() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",""). //Empty String ID parameter
                        pathParam("side", "left").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(404). //Verify HTTP Status Code
                extract().
                        response();
    }

    /**
     * It is assumed that a string of white spaces only is not a valid ID
     */
    @Test
    public void testWhiteSpacesIDShouldReturnNotFound() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id","   "). //White spaces ID parameter
                        pathParam("side", "left").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(404). //Verify HTTP Status Code
                extract().
                        response();
    }

    /**
     * It is assumed that a negative value is not a valid ID
     */
    @Test //Verifies passing negative value as ID returns 404 - Not found
    public void testNegativeIDShouldReturnNotFound() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",-1). //Negative ID
                        pathParam("side", "left").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(404). //Verify HTTP Status Code
                extract().
                        response();
    }

    /**
     * It is assumed that an alphanumeric value is not a valid ID
     */
    @Test
    public void testAlphanumericIDShouldReturnNotFound() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id","ab12"). //Alphanumeric ID
                        pathParam("side", "left").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(404). //Verify HTTP Status Code
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
                        //No Content Type
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
    public void testAlphabetIDShouldReturnMethodNotAllowed() {
        response =
                given().
                        pathParam("id","invalid"). //Alphabet ID
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
    public void testNoBodyShouldReturnBadRequest() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",22).
                        pathParam("side", "right").
                        //No Body
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(400). //Verify HTTP Status Code
                            contentType(ContentType.JSON).
                extract().
                        response();

        convertResponseToJson(response); //Convert raw response to JSON format
        errorCode = jSONResponse.getInt("errorCode"); //Get error code from response JSON
        errorMessage = jSONResponse.getString("errorMessage"); //Get error message from response JSON
        Assert.assertEquals(errorCode, Integer.valueOf(400));
        Assert.assertEquals(errorMessage, "Value in request body cannot be empty.");
    }

    /**
     * It is assumed that empty string in the body of the request should return a bad request
     */
    @Test
    public void testEmptyStringBodyShouldReturnBadRequest() {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",22).
                        pathParam("side", "right").
                        body("\"\"").
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(400). //Verify HTTP Status Code
                            contentType(ContentType.JSON).
                extract().
                        response();

        convertResponseToJson(response); //Convert raw response to JSON format
        errorCode = jSONResponse.getInt("errorCode"); //Get error code from response JSON
        errorMessage = jSONResponse.getString("errorMessage"); //Get error message from response JSON
        Assert.assertEquals(errorCode, Integer.valueOf(400));
        Assert.assertEquals(errorMessage, "Value in request body cannot be empty.");
    }

    @Test
    public void testLeftSideShouldReturnAcceptedLeftSideBase64Data() {
        setSideValue(10, "left", "123456789000"); //Value entered is encoded in the method
        convertResponseToJson(response); //Convert raw response to JSON format
        leftValue = jSONResponse.getString("left"); //Get left value from response
        Assert.assertEquals(leftValue, encodeInBase64("123456789000"));
    }

    @Test
    public void testRightSideShouldReturnAcceptedRightSideBase64Data() {
        setSideValue(21, "right", "12345abcd"); //Value entered is encoded by the method
        convertResponseToJson(response); //Convert raw response to JSON format
        rightValue = jSONResponse.getString("right"); //Get right value from response
        Assert.assertEquals(rightValue, encodeInBase64("12345abcd"));
    }

    @Test
    public void testLeftandRideSidesShouldReturnAcceptedDataForBothSides() {
        //Set the same ID for both left and right sides
        generateUniqueID();
        setSideValue(id, "left", "adebowale");
        setSideValue(id, "right", "formatting12345");
        convertResponseToJson(response); //Convert right side response to JSON format
        leftValue = jSONResponse.getString("left"); //Get left value from right side response
        rightValue = jSONResponse.getString("right"); //Get right value from right side response
        Assert.assertEquals(leftValue, encodeInBase64("adebowale"));
        Assert.assertEquals(rightValue, encodeInBase64("formatting12345"));
    }

    @Test
    public void testSameIDOnSameSideShouldReturnUpdatedValue() {
        generateUniqueID();
        setSideValue(id, "right", "Lifted up");
        setSideValue(id, "right", "Dropped down");
        convertResponseToJson(response); //Convert right side response to JSON format
        rightValue = jSONResponse.getString("right"); //Get right value from right side response
        Assert.assertEquals(rightValue, encodeInBase64("Dropped down"));
    }
}
