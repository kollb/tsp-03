package selection;

import base.Population;
import base.Tour;

public interface ISelection {
    Tour doSelection(Population population);
}