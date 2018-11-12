package utilities;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static utilities.Endpoints.diffSidesPath;
import static utilities.Endpoints.setSideValuePath;

/**
 * Created by Adebowale on 10/11/2018.
 *
 * This class contains static utility variables and methods that are reused across the project
 */
public class TestUtililities {

    public static Response response;
    public static Integer errorCode;
    public static String errorMessage;
    public static String leftValue;
    public static String rightValue;
    public static String type;
    public static JsonPath jSONResponse;

    /**
     * This method does the conversion of RESTAssured response to JSON format
     * in order to be able to read and verify the keys and values
     *
     * @return  A JsonPath equivalence of the RESTAssured response
     */
    public static JsonPath rawResponseToJson(Response r)
    {
        String response = r.asString();
        JsonPath responseString = new JsonPath(response);
        return responseString;
    }

    /**
     * This utility method performs Base64 encode operation using RFC4648 encoder.
     * This is on the assumption that the Base64 encoded data required by the endpoints should be in this format
     *
     * @param s
     *        String to be encoded
     *
     * @return A Base64 encoded string
     **/
    public static String encodeInBase64(String s) {
        byte[] base64EncodedData = Base64.getEncoder().encode(s.getBytes());
        String base64DataString = new String(base64EncodedData);
        return base64DataString;
    }

    /**
     * This method sets the ID, side and value for a post request for side service
     *
     * @param id
     *        Identifies a side in the list of all sides
     *
     * @param side
     *        Left or Right
     *
     * @param value
     *        Request Body Data
     **/
    public static void setSideValue(long id, String side, String value) {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",id). //Sets ID
                        pathParam("side", side). //Sets side
                        body("\"" + encodeInBase64(value) + "\""). //Sets encoded value
                when().
                        post(setSideValuePath()).
                then().
                        assertThat().
                            statusCode(200). //Verify HTTP Status Code from response
                            contentType(ContentType.JSON). //Verify Content Type of response
                extract().
                        response();
    }

    public static void differentiateSides(long id) {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",id). //Sets ID
                when().
                        get(diffSidesPath()).
                then().
                        assertThat().
                            statusCode(200). //Verify HTTP Status Code from response
                            contentType(ContentType.JSON). //Verify Content Type of response
                extract().
                        response();
    }
}
