package base;


import java.util.HashSet;

public class Population {
    private HashSet<Tour> tours;


    public HashSet<Tour> getTours(){
        return tours;
    }

    public Population addTour(Tour tour) {
        tours.add(tour);
        return this;
    }

    public void setTours(HashSet<Tour> tours){
        this.tours=tours;
    }
}
