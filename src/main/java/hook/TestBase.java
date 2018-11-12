package hook;

import io.restassured.RestAssured;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Adebowale on 10/11/2018.
 */
public class TestBase {
    /*
        This is assuming that this project might not be run on the same machine as the application under test.
        Hence the HOST will be set from a property file
     */
    public static Properties environment;

    public void initializeBaseURI() throws IOException {

        //Read file within project and get HOST
        environment = new Properties();
        FileInputStream environmentFile = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/environment.properties");
        environment.load(environmentFile);

        //Set base URI
        RestAssured.baseURI = environment.getProperty("HOST");
    }

}
