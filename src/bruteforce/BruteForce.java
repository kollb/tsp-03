package bruteforce;

import base.City;
import base.Population;
import base.Tour;
import main.Configuration;
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
        ArrayList<Tour> cityListClone=new ArrayList<>();

        for(int i=0;i<cityArrayList.size();i++) {

            int random1 = mtwister.nextInt(0, cityArrayList.size()-2);
            int random2 = mtwister.nextInt(0, cityArrayList.size()-2);

            Collections.swap(cityArrayList,random1,random2);

            newTour.addCity(cityArrayList.get(random1));
            newTour.addCity(cityArrayList.get(random2));
        }
        return newTour;
    }


    public int getPopulationSizeQuarter(){
        ArrayList<Tour> populationTours=population.getTours();

        return populationTours.size()/4;
    }

    public double getFitnessTop25(){
        double populationFitness=1000000;

        ArrayList<Tour> populationTours=population.getTours();
        int quarter=getPopulationSizeQuarter();
        for(int i=0;i<quarter;i++){
            Tour tour=populationTours.get(i);
            if(tour.getFitness()< populationFitness){
                populationFitness = tour.getFitness();

            }
        }


        return populationFitness;
    }

    public double getFitnessLast25(){
        double populationFitness=0;

        ArrayList<Tour> populationTours=population.getTours();
        int quarter=getPopulationSizeQuarter();

        for(Tour tour:populationTours){
            int counter=quarter*3;
            while(counter<populationTours.size()){
                populationFitness=populationFitness+tour.getFitness();
                counter++;
            }
        }

        return populationFitness;
    }

    public double getFitnessMid50(){
        double populationFitness=0;

        ArrayList<Tour> populationTours=population.getTours();
        int quarter=getPopulationSizeQuarter();

        for(Tour tour:populationTours){
            int counter=quarter;
            while(counter<populationTours.size()-quarter){
                populationFitness=populationFitness+tour.getFitness();
                counter++;
            }
        }

        return populationFitness;
    }

    public double getFitnessAll() {
        double populationFitness = 0;

        ArrayList<Tour> populationTours = population.getTours();

        for (Tour tour : populationTours) {
            populationFitness = populationFitness + tour.getFitness();
        }

        return populationFitness;
    }

    public double getBestResult(Population population){
        double bestResult=90000000;
        for (Tour tour : population.getTours()){
            if(tour.getFitness()< bestResult){
                bestResult = tour.getFitness();
            }
        }
        return bestResult;
    }

    public void evaluateFitness(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Choose your evaluation option:      ");
        System.out.println("1. Get Top 25% Percentile Fitnessdata");
        System.out.println("2. Get Last 25% Percentile Fitnessdata ");
        System.out.println("3. Get  Mid-50% Percentile Fitnessdata");
        System.out.println("4. Get All Fitnessdata");
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
                System.out.println("Fitness data All:  "+ getFitnessAll());
                break;
            default:
                System.out.println("Wrong Option");
        }
    }
}