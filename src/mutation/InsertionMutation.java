package mutation;

import base.City;
import base.Tour;
import random.MersenneTwisterFast;

import java.util.ArrayList;

public class InsertionMutation implements IMutation {
    public Tour doMutation(Tour tour) {
        MersenneTwisterFast randomGenerator = new MersenneTwisterFast();
        int selectionPoint = randomGenerator.nextInt(tour.getSize());

        ArrayList<City> cities = tour.getCities();
        City target = cities.remove(selectionPoint);
        int insertionPoint;
        do{
            insertionPoint = randomGenerator.nextInt(cities.size());
        } while(insertionPoint == selectionPoint);
        cities.add(insertionPoint, target);

        tour.setCities(cities);

        return tour;
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}