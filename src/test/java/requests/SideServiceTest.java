package requests;

import hook.TestBase;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import utilities.Endpoints;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static utilities.TestUtililities.*;

/**
 * This class contains all tests for the Side Service.
 * Each test method sends an HTTP request to the side service
 * and they run independently, not relying on result from other test methods
 *
 * @author Adebowale Otulana
 */
public class SideServiceTest extends TestBase {

    private RequestSpecification requestSpecification;
    private Response response;

    /**
     * Before the tests, it initializes the base URI which will be used by each test method.
     *
     * @throws IOException
     */
    @BeforeTest
    public void setBaseURI() throws IOException {
        initializeBaseURI();
    }

    @BeforeMethod
    public void beforeTest() {
        requestSpecification = given();

    }

    @Test
    public void when_BodyDataIsNotBase64Encoded_Expect_DataNotBase64Exception() {
        //Send non Base64 encoded data
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",10).
                        pathParam("side", "left").
                        body("\"abujfdbfjawsasd\""). //Data not base64 encoded
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();

        //Convert RESTAssured raw response to JSON format
        getResponseBody(response);

        //Get errorCode from the JSON response
        errorCode = jSONResponse.getInt("errorCode");

        //Get errorMessage from the JSON response
        errorMessage = jSONResponse.getString("errorMessage");

        //Verify that the errorCode is 415
        Assert.assertEquals(errorCode, Integer.valueOf(415));

        //Verify that the errorMessage is 'Data in body not Base64 formatted.'
        Assert.assertEquals(errorMessage, "Data in body not Base64 formatted.");
    }

    @Test
    public void when_UpIsEnteredAsSide_Expect_SideNameNotSupportedException() {
        //Send undefined side (e.g. up)
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",1).
                        pathParam("side", "up"). //Undefined side
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(501). //Verify HTTP Status Code
                extract().
                        response();

        //Convert RESTAssured raw response to JSON format
        getResponseBody(response);

        //Get errorCode from the JSON response
        errorCode = jSONResponse.getInt("errorCode");

        //Get errorMessage from the JSON response
        errorMessage = jSONResponse.getString("errorMessage");

        //Verify that the errorCode is 501
        Assert.assertEquals(errorCode, Integer.valueOf(501));

        //Verify that the errorMessage is 'This side is not supported, please use either 'left' or 'right'.'
        Assert.assertEquals(errorMessage, "This side is not supported, please use either 'left' or 'right'.");
    }

    @Test
    public void when_OnlyWhiteSpacesIsEnteredAsSide_Expect_SideNameNotSupportedException() {
        //Send white spaces as side
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",1).
                        pathParam("side", "   "). //White spaces as side parameter
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(501). //Verify HTTP Status Code
                extract().
                        response();

        //Convert RESTAssured raw response to JSON format
        getResponseBody(response);

        //Get errorCode from the JSON response
        errorCode = jSONResponse.getInt("errorCode");

        //Get errorMessage from the JSON response
        errorMessage = jSONResponse.getString("errorMessage");

        //Verify that the errorCode is 501
        Assert.assertEquals(errorCode, Integer.valueOf(501));

        //Verify that the errorMessage is 'This side is not supported, please use either 'left' or 'right'.'
        Assert.assertEquals(errorMessage, "This side is not supported, please use either 'left' or 'right'.");
    }

    @Test
    public void when_EmptyStringIsPassedAsSide_Expect_SideNameNotSupportedException() {
        //Send empty string as side
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",99).
                        pathParam("side", ""). //Empty String side parameter
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(405). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void when_EmptyStringIsEnteredAsID_Expect_404NotFound() {
        //Send empty string as ID
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",""). //Empty String ID parameter
                        pathParam("side", "left").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
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
    public void when_OnlyWhiteSpacesIsEnteredAsID_Expect_404NotFound() {
        //Send white spaces as ID
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id","   "). //White spaces ID parameter
                        pathParam("side", "left").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(404). //Verify HTTP Status Code
                extract().
                        response();
    }

    /**
     * It is assumed that a negative value is not a valid ID
     */
    @Test
    public void when_NegativeValueIsEnteredAsID_Expect_404NotFound() {
        //Send negative value as ID
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",-1). //Negative ID
                        pathParam("side", "left").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
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
    public void when_AlphanumericValueIsEnteredAsID_Expect_404NotFound() {
        //Send alphanumeric value as ID
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id","ab12"). //Alphanumeric ID
                        pathParam("side", "left").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(404). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void when_SideRequestContentTypeIsXml_Expect_415UnsupportedMediaType() {
        //Send XML Content Type
        response =
                given().
                        contentType(ContentType.XML). //XML Content Type
                        pathParam("id",64).
                        pathParam("side", "right").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void when_SideRequestContentTypeIsText_Expect_415UnsupportedMediaType() {
        //Send Text Content Type
        response =
                given().
                        contentType(ContentType.TEXT). //Text Content Type
                        pathParam("id",71).
                        pathParam("side", "right").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void when_SideRequestContentTypeIsHTML_Expect_415UnsupportedMediaType() {
        //Send HTML Content Type
        response =
                given().
                        contentType(ContentType.HTML). //HTML Content Type
                        pathParam("id",34).
                        pathParam("side", "right").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void when_SideRequestHasNoContentType_Expect_415UnsupportedMediaType() {
        //Send No Content Type
        response =
                given().
                        //No Content Type
                        pathParam("id",18).
                        pathParam("side", "right").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void when_SideRequestMethodIsPUT_Expect_405MethodNotAllowed() {
        //Send PUT request
        response =
                given().
                        pathParam("id",19).
                        pathParam("side", "left").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        put(Endpoints.POST_SIDE). //PUT method instead of POST
                then().
                        assertThat().
                            statusCode(405). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void when_AlphabetOnlyIsEnteredAsID_Expect_415UnsupportedMediaType() {
        //Send alphabets as ID
        response =
                given().
                        pathParam("id","invalid"). //Alphabet ID
                        pathParam("side", "right").
                        body("\"" + encodeInBase64("abujfdbfjawsasd") + "\"").
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(415). //Verify HTTP Status Code
                extract().
                        response();
    }

    @Test
    public void when_BodyIsNotPassedInSideRequest_Expect_400BadRequest() {
        //Do not include body in request
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",22).
                        pathParam("side", "right").
                        //No Body
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(400). //Verify HTTP Status Code
                            contentType(ContentType.JSON).
                extract().
                        response();

        //Convert RESTAssured raw response to JSON format
        getResponseBody(response);

        //Get errorCode from the JSON response
        errorCode = jSONResponse.getInt("errorCode");

        //Get errorMessage from the JSON response
        errorMessage = jSONResponse.getString("errorMessage");

        //Verify that the errorCode is 400
        Assert.assertEquals(errorCode, Integer.valueOf(400));

        //Verify that the errorMessage is 'Value in request body cannot be empty.'
        Assert.assertEquals(errorMessage, "Value in request body cannot be empty.");
    }

    /**
     * It is assumed that empty string in the body of the request should return a bad request
     */
    @Test
    public void when_EmptyStringIsPassedAsBody_Expect_400BadRequest() {
        //Send empty string as the body
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",22).
                        pathParam("side", "right").
                        body("\"\""). //Empty string
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        assertThat().
                            statusCode(400). //Verify HTTP Status Code
                            contentType(ContentType.JSON).
                extract().
                        response();

        //Convert RESTAssured raw response to JSON format
        getResponseBody(response);

        //Get errorCode from the JSON response
        errorCode = jSONResponse.getInt("errorCode");

        //Get errorMessage from the JSON response
        errorMessage = jSONResponse.getString("errorMessage");

        //Verify that the errorCode is 400
        Assert.assertEquals(errorCode, Integer.valueOf(400));

        //Verify that the errorMessage is 'Value in request body cannot be empty.'
        Assert.assertEquals(errorMessage, "Value in request body cannot be empty.");
    }

    @Test
    public void when_ValidLeftSideRequestIsMade_Expect_AcceptedLeftSideBase64Data() {
        //Set ID, side and non base64 encoded value
        //Value is encoded by the method
        setSideValue(10, "left", "123456789000");

        //Convert RESTAssured raw response to JSON format
        getResponseBody(response);

        //Get left value from response
        leftValue = jSONResponse.getString("left");

        //Verify that the left value is base64 encoded equivalence of '123456789000'
        Assert.assertEquals(leftValue, encodeInBase64("123456789000"));
    }

    @Test
    public void when_ValidRightSideRequestIsMade_Expect_AcceptedRightSideBase64Data() {
        //Set ID, side and non base64 encoded value
        //Value is encoded by the method
        setSideValue(21, "right", "12345abcd");

        //Convert RESTAssured raw response to JSON format
        getResponseBody(response);

        //Get right value from response
        rightValue = jSONResponse.getString("right"); //Get right value from response

        //Verify that the right value is base64 encoded equivalence of '12345abcd'
        Assert.assertEquals(rightValue, encodeInBase64("12345abcd"));
    }

    @Test
    public void when_LeftAndRightSideRequestsAreMadeWithSameID_Expect_AcceptedDataForLeftAndRight() {
        //Generate ID
        generateID();

        //Set generated ID, side and non base64 encoded value
        //Value is encoded by the method
        setSideValue(id, "left", "adebowale");

        //Set generated ID, side and non base64 encoded value
        //Value is encoded by the method
        setSideValue(id, "right", "formatting12345");

        //Convert RESTAssured raw response from right side to JSON format
        getResponseBody(response);

        //Get left value from the right side response
        leftValue = jSONResponse.getString("left");

        //Get right value from the right side response
        rightValue = jSONResponse.getString("right");

        //Verify that the left value is base64 encoded equivalence of 'adebowale'
        Assert.assertEquals(leftValue, encodeInBase64("adebowale"));

        //Verify that the right value is base64 encoded equivalence of 'formatting12345'
        Assert.assertEquals(rightValue, encodeInBase64("formatting12345"));
    }

    @Test
    public void when_SameSideRequestsAreMadeWithSameID_Expect_UpdatedData() {
        //Generate ID
        generateID();

        //Set generated ID, side and non base64 encoded value
        //Value is encoded by the method
        setSideValue(id, "right", "Lifted up");

        //Set generated ID, side and non base64 encoded value
        //Value is encoded by the method
        setSideValue(id, "right", "Dropped down");

        //Convert RESTAssured raw response from the updated right side to JSON format
        getResponseBody(response);

        //Get right value from the updated right side response
        rightValue = jSONResponse.getString("right");

        //Verify that the updated right value is base64 encoded equivalence of 'Dropped down'
        Assert.assertEquals(rightValue, encodeInBase64("Dropped down"));
    }
}
