package base;

import java.util.*;

public class Population {
    private ArrayList<Tour> tours;

    public ArrayList<Tour> getTours(){
        return tours;
    }

    public Population() {
        this.tours = new ArrayList<>();
    }

    public Population addTour(Tour tour){
        tours.add(tour);
        return this;
    }

    public void setTours(ArrayList<Tour> tours){
        this.tours=tours;
    }
}
