package bruteforce;

import base.City;
import base.Population;
import base.Tour;
import data.InstanceReader;
import data.TSPLIBReader;
import main.Configuration;
import main.Scenario;
import random.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

public class BruteForce {
    /*
    private TSPLIBReader tspReader;
    private InstanceReader instReader;
    private ArrayList<Double> resultList;
    */

    private MersenneTwisterFast mtwister = (MersenneTwisterFast) Configuration.instance.random;
    private Population population=new Population();

    /*
    public BruteForce(TSPLIBReader x, InstanceReader y) {
        this.tspReader = x;
        this.instReader = y;
    }
    */

    public Population createPermutations(ArrayList<City> cityList, long permutationsNumber) {
        HashSet<Tour> tours = new HashSet<>();
        long counter=0;
        while(counter < permutationsNumber) {
            Tour newTour = generateTour(cityList);
            if (tours.add(newTour)) {
                counter++;
            }
        }
        ArrayList<Tour> PopulationTours = new ArrayList<>(tours);

        this.population.setTours(PopulationTours);

        return this.population;
    }

    public Tour generateTour(ArrayList<City> cityArrayList){

        Tour newTour=new Tour();
        HashSet<Tour> cityListClone=new HashSet<>();

        int random=0;

        for(int i=0;i<cityArrayList.size();i++) {
            do {
                random = mtwister.nextInt(0, cityArrayList.size()-1);
            } while (newTour.containsCity(cityArrayList.get(random)));
            newTour.addCity(cityArrayList.get(random));
        }

        return newTour;
    }


    public int getPopulationSizeQuarter(){
        ArrayList<Tour> populationTours=population.getTours();

        return populationTours.size()/4;
    }

    public double getFitnessTop25(){
        double bestResult=1000000;

        ArrayList<Tour> populationTours=population.getTours();
        int quarter=getPopulationSizeQuarter();
        for(int i=0;i<quarter;i++){
            Tour tour=populationTours.get(i);
            if(tour.getFitness()< bestResult){
                bestResult = tour.getFitness();
            }
        }


        return bestResult;
    }

    public double getFitnessLast25(){
        double bestResult=10000;

        ArrayList<Tour> populationTours=population.getTours();
        int quarter=getPopulationSizeQuarter();

        for(Tour tour:populationTours){
            int counter=quarter*3;
            while(counter<populationTours.size()){
                if(tour.getFitness()< bestResult){
                    bestResult = tour.getFitness();
                }
                counter++;
            }
        }

        return bestResult;
    }

    public double getFitnessMid50(){
        double bestResult=100000;

        ArrayList<Tour> populationTours=population.getTours();
        int quarter=getPopulationSizeQuarter();

        for(Tour tour:populationTours){
            int counter=quarter;
            while(counter<populationTours.size()-quarter){
                if(tour.getFitness()< bestResult){
                    bestResult = tour.getFitness();
                }
                counter++;
            }
        }

        return bestResult;
    }

    public double getFitnessAll(Population population) {
        double bestResult = 100000;

        ArrayList<Tour> populationTours = population.getTours();

        for (Tour tour : populationTours) {
            if(tour.getFitness()< bestResult){
                bestResult = tour.getFitness();
            }
        }

        return bestResult;
    }



    public void evaluateFitness(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Choose your evaluation option:      ");
        System.out.println("1. Get Best of Top 25% Percentile Fitnessdata");
        System.out.println("2. Get Best of  Last 25% Percentile Fitnessdata ");
        System.out.println("3. Get Best of  Mid-50% Percentile Fitnessdata");
        System.out.println("4. Get Best of All Fitnessdata");
        String option = scan.nextLine();

        switch (option){
            case "1":
                System.out.println("Top 25% Percentile Fitness: "+getFitnessTop25());
                break;
            case "2":
                System.out.println("Last 25% Percentile Fitness "+getFitnessLast25());
                break;
            case "3":
                System.out.println("Mid-50% Percentile Fitness "+getFitnessMid50());
                break;
            case "4":
                System.out.println("Fitness data All:  "+ getFitnessAll(population));
                break;
            default:
                System.out.println("Wrong Option");
        }
    }

    public static void main (String args[]){
        BruteForce bruteForce=new BruteForce();
        Population population=new Population();
        ArrayList<City> availableCities;
        InstanceReader instanceReader = new InstanceReader(Configuration.instance.dataFilePath);
        instanceReader.open();
        TSPLIBReader tspLibReader = new TSPLIBReader(instanceReader);
        availableCities = tspLibReader.getCities();
        Scenario scenario=new Scenario();
        long permutationsNumber = scenario.getMaximumNumberOfEvaluations();
        population=bruteForce.createPermutations(availableCities,permutationsNumber);
        System.out.println(bruteForce.getFitnessAll(population));
    }
}
