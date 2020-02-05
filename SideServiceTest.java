package requests;

import hook.TestBase;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import responseModels.LeftAndRightSideSuccessResponse;
import responseModels.LeftSideSuccessResponse;
import responseModels.RightSideSuccessResponse;
import responseModels.ErrorResponse;
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
    public static void beforeTest() {

    }

    @Test
    public void when_BodyDataIsNotBase64Encoded_Expect_DataNotBase64Exception() {
        //Send non Base64 encoded data
        Response response = setSideValue(10, "left", "\"abujfdbfjawsasd\"");

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Deserialize to a "Side Error Response" Object
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        //Get Error Code from response
        int errorCode;
        errorCode = errorResponse.getErrorCode();

        //Get Error Message from response
        String errorMessage;
        errorMessage = errorResponse.getErrorMessage();

        //Verify that HTTP Status is 415
        Assert.assertEquals(statusCode, 415);

        //Verify that errorCode is 415
        Assert.assertEquals(errorCode, 415);

        //Verify that errorMessage is 'Data in body not Base64 formatted.'
        Assert.assertEquals(errorMessage, "Data in body not Base64 formatted.");
    }

    @Test
    public void when_UpIsEnteredAsSide_Expect_SideNameNotSupportedException() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send undefined side (e.g. up)
        Response response = setSideValue(1, "up", bodyBase64String);

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Deserialize to a "Side Error Response" Object
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        //Get Error Code from response
        int errorCode;
        errorCode = errorResponse.getErrorCode();

        //Get Error Message from response
        String errorMessage;
        errorMessage = errorResponse.getErrorMessage();

        //Verify that HTTP Status Code is 501
        Assert.assertEquals(statusCode, 501);

        //Verify that the errorCode is 501
        Assert.assertEquals(errorCode, 501);

        //Verify that the errorMessage is 'This side is not supported, please use either 'left' or 'right'.'
        Assert.assertEquals(errorMessage, "This side is not supported, please use either 'left' or 'right'.");
    }

    @Test
    public void when_OnlyWhiteSpacesIsEnteredAsSide_Expect_SideNameNotSupportedException() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send white spaces as side
        Response response = setSideValue(1, "      ", bodyBase64String);

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Deserialize to a "Side Error Response" Object
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        //Get Error Code from response
        int errorCode;
        errorCode = errorResponse.getErrorCode();

        //Ger Error Message from response
        String errorMessage;
        errorMessage = errorResponse.getErrorMessage();

        //Verify that HTTP Status Code is 501
        Assert.assertEquals(statusCode, 501);

        //Verify that errorCode is 501
        Assert.assertEquals(errorCode, 501);

        //Verify that errorMessage is 'This side is not supported, please use either 'left' or 'right'.'
        Assert.assertEquals(errorMessage, "This side is not supported, please use either 'left' or 'right'.");
    }

    @Test
    public void when_EmptyStringIsPassedAsSide_Expect_SideNameNotSupportedException() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send empty string as side
        Response response = setSideValue(99, "", bodyBase64String);

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Verify that HTTP Status Code is 405
        Assert.assertEquals(statusCode, 405);
    }

    @Test
    public void when_EmptyStringIsEnteredAsID_Expect_404NotFound() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send empty string as ID
        Response response = setSideValue("", "left", bodyBase64String);

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Verify that HTTP StatusCode is 404
        Assert.assertEquals(statusCode, 404);
    }

    /**
     * It is assumed that a string of white spaces only is not a valid ID
     */
    //@Test
    public void when_OnlyWhiteSpacesIsEnteredAsID_Expect_404NotFound() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send white spaces as ID
        Response response = setSideValue("    ", "left", bodyBase64String);

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Verify that HTTP StatusCode is 404
        Assert.assertEquals(statusCode, 404);
    }

    /**
     * It is assumed that a negative value is not a valid ID
     */
    //@Test
    public void when_NegativeValueIsEnteredAsID_Expect_404NotFound() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send negative value as ID
        Response response = setSideValue(-1, "left", bodyBase64String);

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Verify that HTTP StatusCode is 404
        Assert.assertEquals(statusCode, 404);
    }

    /**
     * It is assumed that an alphanumeric value is not a valid ID
     */
    @Test
    public void when_AlphanumericValueIsEnteredAsID_Expect_404NotFound() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send alphanumeric value as ID
        Response response = setSideValue("ab12", "left", bodyBase64String);

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Verify that HTTP StatusCode is 404
        Assert.assertEquals(statusCode, 404);
    }

    @Test
    public void when_SideRequestContentTypeIsXml_Expect_415UnsupportedMediaType() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send XML Content Type
        given().
                contentType(ContentType.XML). //XML Content Type
                pathParam("id",64).
                pathParam("side", "right").
                body(bodyBase64String).
        when().
                post(Endpoints.POST_SIDE).
        then().
                assertThat().
                    statusCode(415); //Verify HTTP Status Code
    }

    @Test
    public void when_SideRequestContentTypeIsText_Expect_415UnsupportedMediaType() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send Text Content Type
        given().
                contentType(ContentType.TEXT). //Text Content Type
                pathParam("id",71).
                pathParam("side", "right").
                body(bodyBase64String).
        when().
                post(Endpoints.POST_SIDE).
        then().
                assertThat().
                    statusCode(415); //Verify HTTP Status Code
    }

    @Test
    public void when_SideRequestContentTypeIsHTML_Expect_415UnsupportedMediaType() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send HTML Content Type
        given().
                contentType(ContentType.HTML). //HTML Content Type
                pathParam("id",34).
                pathParam("side", "right").
                body(bodyBase64String).
        when().
                post(Endpoints.POST_SIDE).
        then().
                assertThat().
                    statusCode(415); //Verify HTTP Status Code
    }

    @Test
    public void when_SideRequestHasNoContentType_Expect_415UnsupportedMediaType() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send No Content Type
        given().
                //No Content Type
                pathParam("id",18).
                pathParam("side", "right").
                body(bodyBase64String).
        when().
                post(Endpoints.POST_SIDE).
        then().
                assertThat().
                    statusCode(415); //Verify HTTP Status Code
    }

    @Test
    public void when_SideRequestMethodIsPUT_Expect_405MethodNotAllowed() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send PUT request
        given().
                pathParam("id",19).
                pathParam("side", "left").
                body(bodyBase64String).
        when().
                put(Endpoints.POST_SIDE). //PUT method instead of POST
        then().
                assertThat().
                    statusCode(405); //Verify HTTP Status Code
    }

    @Test
    public void when_AlphabetOnlyIsEnteredAsID_Expect_404NotFound() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("abujfdbfjawsasd") + "\"";

        //Send alphabets as ID
        Response response = setSideValue("invalid", "right", bodyBase64String);

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Verify that HTTP Status Code is 404
        Assert.assertEquals(statusCode, 404);
    }

    @Test
    public void when_BodyIsNotPassedInSideRequest_Expect_400BadRequest() {
        //Do not include body in request
        Response response = setSideValue(22, "right");

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Deserialize to a "Side Error Response" Object
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        //Get Error Code from response
        int errorCode;
        errorCode = errorResponse.getErrorCode();

        //Get Error Message from response
        String errorMessage;
        errorMessage = errorResponse.getErrorMessage();

        //Verify that HTTP Status Code is 400
        Assert.assertEquals(statusCode, 400);

        //Verify that errorCode is 400
        Assert.assertEquals(errorCode, 400);

        //Verify that the errorMessage is 'Value in request body cannot be empty.'
        Assert.assertEquals(errorMessage, "Value in request body cannot be empty.");
    }

    /**
     * It is assumed that empty string in the body of the request should return a bad request
     */
    @Test
    public void when_EmptyStringIsPassedAsBody_Expect_400BadRequest() {
        //Send empty string as the body
        Response response = setSideValue(22,"right", "");

        //Get HTTP Status Code from response
        int statusCode;
        statusCode = response.getStatusCode();

        //Deserialize to a "Side Error Response" Object
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        //Get error code from response
        int errorCode;
        errorCode = errorResponse.getErrorCode();

        //Get error message from response
        String errorMessage;
        errorMessage = errorResponse.getErrorMessage();

        //Verify that HTTP Status Code is 400
        Assert.assertEquals(statusCode, 400);

        //Verify that the errorCode is 400
        Assert.assertEquals(errorCode, 400);

        //Verify that the errorMessage is 'Value in request body cannot be empty.'
        Assert.assertEquals(errorMessage, "Value in request body cannot be empty.");
    }

    @Test
    public void when_ValidLeftSideRequestIsMade_Expect_AcceptedLeftSideBase64Data() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("123456789000") + "\"";

        //Set ID, side and base64 encoded value
        Response response = setSideValue(10, "left", bodyBase64String);

        //Deserialize to a "Left Side Success Response" Object
        LeftSideSuccessResponse leftSideSuccessResponse = response.as(LeftSideSuccessResponse.class);

        //Get left value from the right side response
        String leftValue;
        leftValue = leftSideSuccessResponse.getLeft();

        //Verify that the left value is base64 encoded equivalence of '123456789000'
        Assert.assertEquals(leftValue, encodeInBase64("123456789000"));
    }

    @Test
    public void when_ValidRightSideRequestIsMade_Expect_AcceptedRightSideBase64Data() {
        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("12345abcd") + "\"";

        //Set ID, side and base64 encoded value
        Response response = setSideValue(21, "right", bodyBase64String);

        //Deserialize to a "Right Side Success Response" Object
        RightSideSuccessResponse rightSideSuccessResponse = response.as(RightSideSuccessResponse.class);

        //Get right value from the right side response
        String rightValue;
        rightValue = rightSideSuccessResponse.getRight();

        //Verify that the right value is base64 encoded equivalence of '12345abcd'
        Assert.assertEquals(rightValue, encodeInBase64("12345abcd"));
    }

    @Test
    public void when_LeftAndRightSideRequestsAreMadeWithSameID_Expect_AcceptedDataForLeftAndRight() {
        //Generate ID
        generateID();

        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("adebowale") + "\"";

        //Set generated ID, side and base64 encoded value
        setSideValue(id, "left", bodyBase64String);

        bodyBase64String = "\"" + encodeInBase64("formatting12345") + "\"";

        //Set generated ID, side and base64 encoded value
        //Value is encoded by the method
        Response response = setSideValue(id, "right", bodyBase64String);

        //Deserialize to a "Left and Right Side Success Response" Object
        LeftAndRightSideSuccessResponse leftAndRightSideSuccessResponse = response.as(LeftAndRightSideSuccessResponse.class);

        //Get left value from response
        String leftValue;
        leftValue = leftAndRightSideSuccessResponse.getLeft();

        //Get right value from response
        String rightValue;
        rightValue = leftAndRightSideSuccessResponse.getRight();

        //Verify that the left value is base64 encoded equivalence of 'adebowale'
        Assert.assertEquals(leftValue, encodeInBase64("adebowale"));

        //Verify that the right value is base64 encoded equivalence of 'formatting12345'
        Assert.assertEquals(rightValue, encodeInBase64("formatting12345"));
    }

    @Test
    public void when_SameSideRequestsAreMadeWithSameID_Expect_UpdatedData() {
        //Generate ID
        generateID();

        //Encode Body in Base 64
        String bodyBase64String;
        bodyBase64String = "\"" + encodeInBase64("Lifted up") + "\"";

        //Set generated ID, side and base64 encoded value
        setSideValue(id, "right", bodyBase64String);

        bodyBase64String = "\"" + encodeInBase64("Dropped down") + "\"";

        //Set generated ID, side and non base64 encoded value
        //Value is encoded by the method
        Response response = setSideValue(id, "right", bodyBase64String);

        //Deserialize to a "Right Side Success Response" Object
        RightSideSuccessResponse rightSideSuccessResponse = response.as(RightSideSuccessResponse.class);

        //Get Right from response
        String rightValue;
        rightValue = rightSideSuccessResponse.getRight();

        //Verify that the updated right value is base64 encoded equivalence of 'Dropped down'
        Assert.assertEquals(rightValue, encodeInBase64("Dropped down"));
    }
}