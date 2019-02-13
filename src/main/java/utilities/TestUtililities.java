package utilities;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Base64;
import java.util.Random;

import static io.restassured.RestAssured.given;

/**
 * This class contains static utility variables
 * and methods that are reused across the project
 *
 * @author Adebowale Otulana
 */
public class TestUtililities {

    public static long id;
    public static Response response;

    /**
     * This utility method performs Base64 encode operation using RFC4648 encoder.
     * This is based on the assumption that the Base64 encoded data required by the
     * endpoints should be in this format.
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
     * This method creates a valid HTTP POST request for the side service.
     * It sets the ID, side and Base64 encoded data.
     * It verifies the HTTP status code and Content Type of the response.
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
                        body("\"" + encodeInBase64(value) + "\""). //Sets Base64 encoded value
                when().
                        post(Endpoints.POST_SIDE).
                then().
                        log().all().
                extract().
                        response();
    }

    /**
     * This method creates a valid HTTP GET request to differ sides.
     * It also verifies the HTTP status code and Content Type of the response
     *
     * @param id
     *        A valid side ID
     */
    public static void differentiateSides(long id) {
        response =
                given().
                        contentType(ContentType.JSON).
                        pathParam("id",id). //Sets ID
                when().
                        get(Endpoints.GET_DIFF).
                then().
                        log().all().
                extract().
                        response();
    }

    /**
     * This method generates random long values that are used as unique side IDs.
     * It ensures that the generated values are positive.
     */
    public static void generateID() {
        Random randomID = new Random();
        id = randomID.nextLong() & Long.MAX_VALUE; //Positive random IDs
    }
}
