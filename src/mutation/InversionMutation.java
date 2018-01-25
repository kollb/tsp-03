package mutation;

import base.City;
import base.Tour;
import random.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.Collections;

public class InversionMutation implements IMutation {
    public Tour doMutation(Tour tour) {
        MersenneTwisterFast random = new MersenneTwisterFast();

        if(random.nextBoolean(1.0)) { //TODO
            int arrayLength = tour.getSize();
            int random1 = random.nextInt(0,arrayLength);
            int random2 = random.nextInt(0,arrayLength);
            int small = Math.min(random1,random2);
            int large = Math.max(random1,random2);
            ArrayList<City> tourTemp = new ArrayList<City>();
            int temp = 0;
            for (int i = small; i <=large; i++){
                tourTemp.add(temp,tour.getCity(i));
                temp++;
            }
            Collections.reverse(tourTemp);
            int sizeTemp = tourTemp.size();
            int start = small;
            for (int i = 0; i < sizeTemp; i++){
                tour.addCity(start,tourTemp.get(i));
                start++;
            }
            System.out.println(tour.getCities().toString());
        }

        return null;
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}