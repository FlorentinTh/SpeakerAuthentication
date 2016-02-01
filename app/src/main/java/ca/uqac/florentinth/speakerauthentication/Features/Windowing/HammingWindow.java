package ca.uqac.florentinth.speakerauthentication.Features.Windowing;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class HammingWindow extends Window {

    private static final Map<Integer, double[]> FACTORS_BY_WINDOW_SIZE = new HashMap<>();

    public HammingWindow(int size) {
        super(size);
    }

    @Override
    protected double[] getPrecomputedFactors(int size) {
        synchronized(HammingWindow.class) {
            double[] factors;

            if(FACTORS_BY_WINDOW_SIZE.containsKey(size)) {
                factors = FACTORS_BY_WINDOW_SIZE.get(size);
            } else {
                factors = new double[size];
                int sizeMinusOne = size - 1;

                for(int i = 0; i < size; i++) {
                    factors[i] = 0.54d - (0.46d * Math.cos((2 * Math.PI * i) / sizeMinusOne));
                }

                FACTORS_BY_WINDOW_SIZE.put(size, factors);
            }

            return factors;
        }
    }
}
