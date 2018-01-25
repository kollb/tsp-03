package base;

import java.util.ArrayList;

public class Population {
    private ArrayList<Tour> tours;

    public Population() {
        this.tours = new ArrayList<>();
    }

    public ArrayList<Tour> getTours() {
        return tours;
    }

    public Population addTour(Tour tour) {
        tours.add(tour);
        return this;
    }
}