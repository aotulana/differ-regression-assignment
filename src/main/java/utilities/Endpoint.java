package utilities;

/**
 * The class defines the endpoint paths.
 * A method returns a path for specific endpoint.
 *
 * @author Adebowale Otulana
 */
public class Endpoint {

    private static String path;

    /**
     * Initializes the path for side service
     *
     * @return Side Service path
     */
    public static String sidePath() {
        path = "/{id}/{side}";
        return path;
    }

    /**
     * Initializes the path for differ service
     *
     * @return Differ Service path
     */
    public static String diffSidesPath() {
        path = "/{id}";
        return path;
    }
}
