package test.selection;

import base.City;
import base.Population;
import base.Tour;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class RouletteWheelSelection {

    @Test
    public void testDoSelection() {
        Tour[] tour = { new Tour(), new Tour(), new Tour() };
        City[] cities = { new City(1, 10, 10), new City(2, 20, 20), new City(3, 30, 30), new City(4, 40, 40)};

        tour[0].addCity(cities[0]);
        tour[0].addCity(cities[1]);
        tour[0].addCity(cities[2]);
        tour[0].addCity(cities[3]);

        tour[1].addCity(cities[2]);
        tour[1].addCity(cities[1]);
        tour[1].addCity(cities[0]);
        tour[1].addCity(cities[3]);

        tour[2].addCity(cities[3]);
        tour[2].addCity(cities[2]);
        tour[2].addCity(cities[0]);
        tour[2].addCity(cities[1]);

        Population pop = new Population();
        pop.addTour(tour[0]).addTour(tour[1]).addTour(tour[2]);

        selection.RouletteWheelSelection rws = new selection.RouletteWheelSelection();

        ArrayList<Tour> test = rws.doSelection(pop);

        test.forEach(tour1-> System.out.println(tour1.toString()));
    }
}