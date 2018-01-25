package test.crossover;

import base.City;
import org.junit.Test;
import base.Tour;

public class OrderedCrossover {

    crossover.OrderedCrossover od = new crossover.OrderedCrossover();
    @Test
    public void testOrderCrossover() {

        Tour tour1 = new Tour();
        Tour tour2 = new Tour();

        for (int i = 1; i < 281; i++){
            tour1.addCity(new City(i,i,i));
        }
        for (int i = 279; i >= 0; i--){
            tour2.addCity(tour1.getCity(i));
        }
        System.out.println(tour1.toString());
        System.out.println(tour2.toString());
        System.out.println(od.doCrossover(tour1, tour2).toString());
}
}