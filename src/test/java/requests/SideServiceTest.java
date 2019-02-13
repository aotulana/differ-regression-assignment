package requests;

import hook.TestBase;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import responseModels.*;
import utilities.Endpoints;

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

    @BeforeMethod
    public void beforeMethod() {
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

        //Get response body
        ResponseBody body = response.getBody();
        ErrorResponse responseBody = body.as(ErrorResponse.class);

        //Verify that the errorCode is 415
        Assert.assertEquals(responseBody.errorCode, "415");

        //Verify that the errorMessage is 'Data in body not Base64 formatted.'
        Assert.assertEquals(responseBody.errorMessage, "Data in body not Base64 formatted.");
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

        //Get response body
        ResponseBody body = response.getBody();
        ErrorResponse responseBody = body.as(ErrorResponse.class);

        //Verify that the errorCode is 501
        Assert.assertEquals(responseBody.errorCode, "501");

        //Verify that the errorMessage is 'This side is not supported, please use either 'left' or 'right'.'
        Assert.assertEquals(responseBody.errorMessage, "This side is not supported, please use either 'left' or 'right'.");
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

        //Get response body
        ResponseBody body = response.getBody();
        ErrorResponse responseBody = body.as(ErrorResponse.class);

        //Verify that the errorCode is 501
        Assert.assertEquals(responseBody.errorCode, "501");

        //Verify that the errorMessage is 'This side is not supported, please use either 'left' or 'right'.'
        Assert.assertEquals(responseBody.errorMessage, "This side is not supported, please use either 'left' or 'right'.");
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

        //Get response body
        ResponseBody body = response.getBody();
        ErrorResponse responseBody = body.as(ErrorResponse.class);

        //Verify that the errorCode is 400
        Assert.assertEquals(responseBody.errorCode, "400");

        //Verify that the errorMessage is 'Value in request body cannot be empty.'
        Assert.assertEquals(responseBody.errorMessage, "Value in request body cannot be empty.");
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

        //Get response body
        ResponseBody body = response.getBody();
        ErrorResponse responseBody = body.as(ErrorResponse.class);

        //Verify that the errorCode is 400
        Assert.assertEquals(responseBody.errorCode, "400");

        //Verify that the errorMessage is 'Value in request body cannot be empty.'
        Assert.assertEquals(responseBody.errorMessage, "Value in request body cannot be empty.");
    }

    @Test
    public void when_ValidLeftSideRequestIsMade_Expect_AcceptedLeftSideBase64Data() {
        //Set ID, side and non base64 encoded value
        //Value is encoded by the method
        setSideValue(10, "left", "123456789000");

        //Get response body
        ResponseBody body = response.getBody();
        LeftSideSuccessResponse responseBody = body.as(LeftSideSuccessResponse.class);

        //Verify that the left value is base64 encoded equivalence of '123456789000'
        Assert.assertEquals(responseBody.left, encodeInBase64("123456789000"));
    }

    @Test
    public void when_ValidRightSideRequestIsMade_Expect_AcceptedRightSideBase64Data() {
        //Set ID, side and non base64 encoded value
        //Value is encoded by the method
        setSideValue(21, "right", "12345abcd");

        //Get response body
        ResponseBody body = response.getBody();
        RightSideSuccessResponse responseBody = body.as(RightSideSuccessResponse.class);

        //Verify that the right value is base64 encoded equivalence of '12345abcd'
        Assert.assertEquals(responseBody.right, encodeInBase64("12345abcd"));
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

        //Get response body
        ResponseBody body = response.getBody();
        LeftAndRightSideSuccessResponse responseBody = body.as(LeftAndRightSideSuccessResponse.class);

        //Verify that the left value is base64 encoded equivalence of 'adebowale'
        Assert.assertEquals(responseBody.left, encodeInBase64("adebowale"));

        //Verify that the right value is base64 encoded equivalence of 'formatting12345'
        Assert.assertEquals(responseBody.right, encodeInBase64("formatting12345"));
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

        //Get response body
        ResponseBody body = response.getBody();
        RightSideSuccessResponse responseBody = body.as(RightSideSuccessResponse.class);

        //Verify that the updated right value is base64 encoded equivalence of 'Dropped down'
        Assert.assertEquals(responseBody.right, encodeInBase64("Dropped down"));
    }
}
