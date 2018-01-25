package test.selection;

import java.util.Random;

/**
 * Created by npor1112 on 25.01.2018.
 */
public class PseudoRandom extends Random {
    int[] intArr = {0, 1, 2};
    double[] doubleArr = {0.2, 0.5, 0.7};

    public PseudoRandom(int[] intArr) {
        this.intArr = intArr;
    }

    public PseudoRandom(int[] intArr, double[] doubleArr) {
        this.intArr = intArr;
        this.doubleArr = doubleArr;
    }

    public PseudoRandom(double[] doubleArr) {
        this.doubleArr = doubleArr;
    }
}
