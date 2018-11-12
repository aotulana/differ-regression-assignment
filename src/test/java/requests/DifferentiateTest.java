package requests;

import hook.TestBase;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static utilities.TestUtililities.*;

/**
 * Created by aotulana on 11/12/2018.
 */
public class DifferentiateTest extends TestBase {

    //Before tests are run, the base URI is initialized
    @BeforeTest
    public void setBaseURI() throws IOException {
        initializeBaseURI();
    }

    @Test
    public void testDifferOfEqualSidesShouldReturnEqual() {
        setSideValue(999, "left", "continuous12345");
        setSideValue(999, "right", "continuous12345");

        differentiateSides(999);
        jSONResponse = rawResponseToJson(response); //Convert raw response to JSON format
        type = jSONResponse.getString("type");
        Assert.assertEquals(type, "EQUAL");
    }
}
