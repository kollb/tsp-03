package selection;

import base.Population;
import base.Tour;
import main.Configuration;

import java.util.*;

public class RouletteWheelSelection implements ISelection {

    public ArrayList<Tour> doSelection(Population population) throws PopulationTooSmallException {

        if (population.getTours().size() < Configuration.instance.ROULETTE_WHEEL_SELECT_COUNT) {
            throw new PopulationTooSmallException();
        }

        ArrayList<Tour> result = new ArrayList<>();

        for (int i = 0; i < Configuration.instance.ROULETTE_WHEEL_SELECT_COUNT;) {
            Tour select = doSingleSelection(population);

            if (!result.contains(select)) {
                result.add(select);
                i++;
            }
        }

        return result;
    }

    public Tour doSingleSelection(Population population) {
        double chance = Configuration.instance.random.nextDouble();

        SortedMap<Double, Tour> probabilityMap = getProbabilityTourMap(population);
        for (double key : probabilityMap.keySet()) {
            if (chance <= key) {
                return probabilityMap.get(key);
            }
        }
        return probabilityMap.get(probabilityMap.lastKey());
    }

    public String toString() {
        return getClass().getSimpleName();
    }

    private static double getFitnessSum(Population population) {
        double fitnessSum = 0;

        for (Tour tour : population.getTours()) {
            fitnessSum += tour.getFitness();
        }

        return fitnessSum;
    }

    private static SortedMap<Double, Tour> getProbabilityTourMap(Population population) {
        final double fitnessSum = getFitnessSum(population);
        SortedMap<Double, Tour> tourMap = new TreeMap<>();

        double probability = 0;
        for (Tour tour : population.getTours()) {
            probability += tour.getFitness()/fitnessSum;
            tourMap.put(probability, tour);
        }

        return tourMap;
    }
}