package mutation;

import base.City;
import base.Tour;
import random.MersenneTwisterFast;

import java.util.ArrayList;

public class DisplacementMutation implements IMutation {
    public Tour doMutation(Tour tour) {

        MersenneTwisterFast randomGenerator = new MersenneTwisterFast();
        int startPoint = randomGenerator.nextInt(tour.getSize());
        int endPoint = randomGenerator.nextInt(tour.getSize());
        if(startPoint > endPoint) {
            int temp = startPoint;
            startPoint = endPoint;
            endPoint = temp;
        }
        ArrayList<City> cities = tour.getCities();
        ArrayList<City> displaced = new ArrayList<City>();
        for(int i = startPoint; i <= endPoint; i++) {
            displaced.add(cities.remove(i));
        }
        int insertionPoint;
        do{
            insertionPoint = randomGenerator.nextInt(cities.size());
        } while(insertionPoint == startPoint);
        cities.addAll(insertionPoint, displaced);

        tour.setCities(cities);

        return tour;
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}