package keen.lib;

/**
 * Created by apple on 05/02/17.
 */

public class IncompatibleRatioException extends RuntimeException {
    public IncompatibleRatioException() {
        super("Can't perform Ken Burns effect on rects with distinct aspect ratios!");
    }
}
