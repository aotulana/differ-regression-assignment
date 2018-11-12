package utilities;

import hook.TestBase;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;

/**
 * Created by Adebowale on 10/11/2018.
 */
public class TestUtil extends TestBase {

    //Before tests are run, initialize the base URI
    @BeforeSuite
    public void setBaseURI() throws IOException {
        initializeBaseURI();
    }
}
