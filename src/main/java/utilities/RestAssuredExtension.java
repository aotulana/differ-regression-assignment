package utilities;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Created by aotulana on 2/12/2019.
 */
public class RestAssuredExtension {

    public static RequestSpecification request;

    public RestAssuredExtension() {
        //Arrange
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri("http://localhost:8081");
        builder.setContentType(ContentType.JSON);
        RequestSpecification requestSpec = builder.build();
        request = given().spec(requestSpec);
    }
}
