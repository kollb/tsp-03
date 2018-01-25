package mutation;

import base.City;
import base.Tour;
import random.MersenneTwisterFast;

public class ExchangeMutation implements IMutation {
    public Tour doMutation(Tour tour) {
        MersenneTwisterFast random = new MersenneTwisterFast();
        if(random.nextBoolean(1.0)) { //TODO
            int arrayLength = tour.getSize();
            int random1 = random.nextInt(0,arrayLength);
            int random2 = random.nextInt(0,arrayLength);
            City tempCity1 = tour.getCity(random1);
            City tempCity2 = tour.getCity(random2);
            tour.addCity(random1,tempCity2);
            tour.addCity(random2,tempCity1);
            System.out.println(tour.getCities().toString());
        }
        return tour;
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}