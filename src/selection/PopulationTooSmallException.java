package selection;

/**
 * Created by chag0709 on 26.01.18 - Kaufland Informationssysteme 2018.
 */
public class PopulationTooSmallException extends Exception {

    private static final String ERROR = "Tour size is smaller than the configured Select Count.";

    public PopulationTooSmallException() {
        super(ERROR);
    }

    public PopulationTooSmallException(String message) {
        super(message);
    }
}
