package hook;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import utilities.ExtentReporterNG;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Listeners(ExtentReporterNG.class)

/**
 * @author Adebowale Otulana
 */
public class TestBase {
    /**
     * This is assuming that this project might not be run on the same machine as the application under test.
     * Hence the HOST will be set from the environment property file in the resources folder of the project.
     **/
    public static Properties environment;

    /**
     * Before the test suite, it initializes the base URI
     * which will be used by all test methods.
     *
     * @throws IOException
     */
    @BeforeSuite (alwaysRun = true)
    public void initializeBaseURI() throws IOException {

        /*//Read file from location within project and get the defined HOST
        environment = new Properties();
        FileInputStream environmentFile = new FileInputStream(System.getProperty("user.dir")
                + "/src/main/resources/environment.properties"); //Location of the property file
        environment.load(environmentFile);

        //Set base URI
        //RestAssured.baseURI = environment.getProperty("HOST");*/

        //Set base URI
        //RestAssured.baseURI = System.getProperty("server.host");
        RestAssured.baseURI = "http://localhost";

        //Set base port
        String port = System.getProperty("server.port");
        //RestAssured.port = Integer.valueOf(port);
        RestAssured.port = 8081;

        //Set base path
        RestAssured.basePath = "/diffassign/v1/diff";
    }

}
