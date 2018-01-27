package main;

import base.City;
import base.Population;
import base.Tour;
import bruteforce.BruteForce;
import crossover.*;
import data.HSQLDBManager;
import data.InstanceReader;
import data.TSPLIBReader;
import mutation.*;
import selection.ISelection;
import selection.PopulationTooSmallException;
import selection.RouletteWheelSelection;
import selection.TournamentSelection;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Application {
    private static ArrayList<City> availableCities;
    private double[][] distances;

    private double lastResult;
    private int lastResutCounter = 0;

    private ISelection selection;
    private ICrossover crossover;
    private IMutation mutation;

    public void startupHSQLDB() {
        HSQLDBManager.instance.startup();
        HSQLDBManager.instance.init();
    }

    public void shutdownHSQLDB() {
        HSQLDBManager.instance.shutdown();
    }

    public void printMatrix(double[][] matrix) {
        DecimalFormat decimalFormat = new DecimalFormat("000.00");

        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < matrix[0].length; columnIndex++)
                System.out.print(decimalFormat.format(matrix[rowIndex][columnIndex]) + "\t");
            System.out.println();
        }
    }

    public void loadData() {
        System.out.println("--- GeneticAlgorithm.loadData()");
        InstanceReader instanceReader = new InstanceReader(Configuration.instance.dataFilePath);
        instanceReader.open();
        TSPLIBReader tspLibReader = new TSPLIBReader(instanceReader);

        availableCities = tspLibReader.getCities();
        System.out.println("availableCities (size) : " + availableCities.size());

        distances = tspLibReader.getDistances();
        //printMatrix(distances);

        instanceReader.close();

        System.out.println();
    }

    public Scenario[] initConfiguration() {
        System.out.println("--- GeneticAlgorithm.initConfiguration()");
        System.out.println();
        Scenario[] scenarios = null;
        try {
            scenarios = new XMLParser(new File("configuration/genetic_algorithm_tsp.xml")).getScenarios();

            if (scenarios == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Szenario-Datei nicht gefunden");
            System.exit(1);
        }
        System.out.println("Es wurden " + scenarios.length + " Szenarien geladen. ");
        return scenarios;
    }

    public void execute(Scenario[] scenarios, Population population) {
        BruteForce bruteForce = new BruteForce();
        for (Scenario scenario : scenarios) {

            switch (scenario.getSelection()) {
                case TOURNAMENT:
                    selection = new TournamentSelection();
                    break;
                case ROULETTE_WHEEL:
                    selection = new RouletteWheelSelection();
                    break;
            }
            switch (scenario.getCrossover()) {
                case CYCLE:
                    crossover = new CycleCrossover();
                    break;
                case ORDERED:
                    crossover = new OrderedCrossover();
                    break;
                case POSITION:
                    crossover = new PositionBasedCrossover();
                    break;
                case HEURISTIC:
                    crossover = new HeuristicCrossover();
                    break;
                case PARTIALLY_MATCHED:
                    crossover = new PartiallyMatchedCrossover();
                    break;
                case SUB_TOUR_EXCHANGE:
                    crossover = new SubTourExchangeCrossover();
                    break;
            }
            switch (scenario.getMutation()) {
                case HEURISTIC:
                    mutation = new HeuristicMutation();
                    break;
                case DISPLACEMENT:
                    mutation = new DisplacementMutation();
                    break;
                case EXCHANGE:
                    mutation = new ExchangeMutation();
                    break;
                case INSERTION:
                    mutation = new InsertionMutation();
                    break;
                case INVERSION:
                    mutation = new InversionMutation();
                    break;
            }



            for (int i = 0; i < 10; i++) {
                double result = 0;
                if(gosForward(result)&&isSolutionQualityReached(result)){
                ArrayList<Tour> newPopulation;
                ArrayList<Tour> parents;

                newPopulation = population.getTours();

                try {
                    parents = selection.doSelection(population);

/*
                    for (int j = 1; j < 26; j = j + 2) {
                        Tour child1 = crossover.doCrossover(parents.get(j), parents.get(j - 1));
                        Tour child2 = crossover.doCrossover(parents.get(j), parents.get(j - 1));
                        System.out.println("JUSTUS");
                        newPopulation.add(child1);
                        newPopulation.add(child2);
                        System.out.println("JUSTUuuuuuuuuuuuuuuS");
                    }
*/


                    newPopulation = mutation.doMutation(newPopulation,scenario.getMutationRatio());

                    population.setTours(newPopulation);
                    result = bruteForce.getBestResult(population);

                    HSQLDBManager.instance.addFitnessToScenario(scenario.getId(), i, result);
                    //HSQLDBManager.instance.writeCsv(scenario.getId());
                    System.out.println(scenario.getId() + ", " + i + ", " + result);

                } catch (PopulationTooSmallException e) {
                    e.printStackTrace();
                }
            }

            }
        }
    }

    public boolean gosForward(double result) {
        if (Math.abs(lastResult - result) < 0.00001) {
            lastResutCounter++;
        } else {
            lastResutCounter = 0;
        }
        if (lastResutCounter >= 1000) {
            return false;
        }
        this.lastResult = result;
        return true;
    }

    private boolean isSolutionQualityReached(double quality) {
        return (quality * 0.95) <= 2579;
    }

    public double getFitnessAll(Population population) {
        double populationFitness = 0;

        ArrayList<Tour> populationTours = population.getTours();

        for (Tour tour : populationTours) {
            populationFitness = populationFitness + tour.getFitness();
        }

        return populationFitness;
    }

    public static void main(String... args) {
        long permutationsNumber = Long.parseUnsignedLong("280");
        Application application = new Application();
        application.startupHSQLDB();
        application.loadData();
        BruteForce bruteForce = new BruteForce();
        Population population = bruteForce.createPermutations(availableCities, permutationsNumber);
        Scenario[] scenarios = application.initConfiguration();
        application.execute(scenarios, population);
        //bruteForce.evaluateFitness();
        application.shutdownHSQLDB();
    }
}