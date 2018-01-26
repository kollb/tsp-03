package mutation;

import base.City;
import base.Tour;
import main.Configuration;
import main.Scenario;
import random.MersenneTwisterFast;

import java.util.ArrayList;

public class HeuristicMutation implements IMutation {
    public ArrayList<Tour> doMutation(ArrayList<Tour> tours) {
        MersenneTwisterFast randomGenerator = (MersenneTwisterFast) Configuration.instance.random;
        Scenario scenario = new Scenario();                                 //TODO

        for(Tour tour:tours) {
            if (randomGenerator.nextBoolean(scenario.getMutationRatio())) {
                ArrayList<City> cities = tour.getCities();
                int lambda = randomGenerator.nextInt(10) + 1;               //TODO check if n = 10 is an appropriate value
                ArrayList<Integer> allNumbers = new ArrayList<Integer>();
                for (int i = 0; i < cities.size(); i++) {
                    allNumbers.add(i);
                }
                ArrayList<Integer> positions = new ArrayList<Integer>();
                for (int i = 0; i < lambda; i++) {
                    positions.add(allNumbers.remove(randomGenerator.nextInt(allNumbers.size())));
                }
                ArrayList<City> targets = new ArrayList<City>();
                for (Integer position : positions) {
                    targets.add(cities.get(position));
                }
                ArrayList<ArrayList<City>> permutations = permutation(targets);

                ArrayList<Tour> possibleTours = new ArrayList<Tour>();

                for (ArrayList<City> onePermutation : permutations) {
                    for (Integer position : positions) {
                        cities.set(position, onePermutation.remove(0));
                    }
                    Tour oneTour = new Tour();
                    oneTour.setCities(cities);
                    possibleTours.add(oneTour);
                }

                double minimumDistance = tour.getFitness();
                for (Tour eachTour : possibleTours) {
                    if (eachTour.getFitness() <= minimumDistance) {
                        minimumDistance = eachTour.getFitness();
                        tour = eachTour;
                    }
                }
            }
        }

        return tours;
    }

    public ArrayList<ArrayList<City>> permutation(ArrayList<City> nums) {
        ArrayList<ArrayList<City>> accum = new ArrayList<ArrayList<City>>();
        permutation(accum, new ArrayList<City>(), nums);
        return accum;
    }

    private static void permutation(ArrayList<ArrayList<City>> accum, ArrayList<City> prefix, ArrayList<City> nums) {
        int n = nums.size();
        if (n == 0) {
            accum.add(prefix);
        } else {
            for (int i = 0; i < n; ++i) {
                ArrayList<City> newPrefix = new ArrayList<City>();
                newPrefix.addAll(prefix);
                newPrefix.add(nums.get(i));
                ArrayList<City> numsLeft = new ArrayList<City>();
                numsLeft.addAll(nums);
                numsLeft.remove(i);
                permutation(accum, newPrefix, numsLeft);
            }
        }
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}