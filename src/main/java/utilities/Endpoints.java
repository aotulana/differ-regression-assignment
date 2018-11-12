package utilities;

/**
 * Created by aotulana on 11/12/2018.
 *
 * The class defines the endpoint paths.
 * A method returns a path for specific endpoint.
 */
public class Endpoints {

    private static String path;

    public static String setSideValuePath() {
        path = "/diffassign/v1/diff/{id}/{side}";
        return path;
    }

    public static String diffSidesPath() {
        path = "/diffassign/v1/diff/{id}";
        return path;
    }
}
