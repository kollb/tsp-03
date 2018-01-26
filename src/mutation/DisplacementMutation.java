package mutation;

import base.City;
import base.Tour;
import main.Configuration;
import main.Scenario;
import random.MersenneTwisterFast;

import java.util.ArrayList;

public class DisplacementMutation implements IMutation {
    @Override
    public ArrayList<Tour> doMutation(ArrayList<Tour> tours) {
        MersenneTwisterFast randomGenerator =  Configuration.instance.randomForMutation;
        Scenario scenario = new Scenario();                                 //TODO

        for(Tour tour:tours) {
            if (randomGenerator.nextBoolean(scenario.getMutationRatio())) {
                int startPoint = randomGenerator.nextInt(tour.getSize());
                int endPoint = randomGenerator.nextInt(tour.getSize());
                if (startPoint > endPoint) {
                    int temp = startPoint;
                    startPoint = endPoint;
                    endPoint = temp;
                }
                ArrayList<City> cities = tour.getCities();
                ArrayList<City> displaced = new ArrayList<City>();
                for (int i = startPoint; i <= endPoint; i++) {
                    displaced.add(cities.remove(startPoint));
                }
                int insertionPoint;
                do {
                    insertionPoint = randomGenerator.nextInt(cities.size());
                } while (insertionPoint == startPoint);
                cities.addAll(insertionPoint, displaced);

                tour.setCities(cities);
            }
        }

        return tours;
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}