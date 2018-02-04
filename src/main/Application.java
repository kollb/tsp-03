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
import random.MersenneTwisterFast;
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
        int iterationsMax = Configuration.instance.iterationsMaximum;

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

            System.out.println("-------------------");
            System.out.println(" Scenario: "+ scenario.getId());
            System.out.println(" Crossover Type: "+ scenario.getCrossover());
            System.out.println(" Crossover Ratio: "+ scenario.getCrossoverRatio());
            System.out.println(" Mutation Type: "+ scenario.getMutation());
            System.out.println(" Mutation Ratio: "+ scenario.getMutationRatio());
            System.out.println(" Selection Type: "+ scenario.getSelection());
            System.out.println("-------------------");
            long startTime = System.currentTimeMillis();
            double bestResult = 80000;
            int iterationsCounter=1;
            double bruteForceResult=0;
            if(scenario.getIsEvaluated()){
                long permutationsNumber = Long.parseUnsignedLong("400000");
                bruteForceResult = bruteForceResult(permutationsNumber);
            }

                do {
                    double result = 0;
                    ArrayList<Tour> newPopulation;
                    ArrayList<Tour> parents;

                    newPopulation = population.getTours();
                    try {
                        parents = selection.doSelection(population);
                        MersenneTwisterFast mtwister=(MersenneTwisterFast) Configuration.instance.random;
                        if(mtwister.nextBoolean(scenario.getCrossoverRatio())){
                            for (int j = 1; j < 26; j = j + 2) {
                                Tour child1 = crossover.doCrossover(parents.get(j), parents.get(j - 1));
                                Tour child2 = crossover.doCrossover(parents.get(j), parents.get(j - 1));
                                newPopulation.add(child1);
                                newPopulation.add(child2);
                            }
                        }

                        newPopulation = mutation.doMutation(newPopulation, scenario.getMutationRatio());

                        population.setTours(newPopulation);
                        result = this.getBestResult(population);

                        if (result < bestResult) {
                            bestResult = result;
                        }
                        HSQLDBManager.instance.addFitnessToScenario(scenario.getId(), iterationsCounter, result);
/*                    if (i == 750) {
                        HSQLDBManager.instance.checkTable(i);
                    }*/
                        lastResult = result;
                        if (iterationsCounter == 1) {
                            System.out.println("Iteration: " + iterationsCounter + " Starting with value: " + Math.round(result));
                        }
                        if (iterationsCounter % (iterationsMax/4) == 0) {
                            System.out.println("Iteration: " + iterationsCounter + " Best value: " + Math.round(bestResult) + " Population Size: " + newPopulation.size());
                        }
                        if (iterationsCounter == iterationsMax) {
                            HSQLDBManager.instance.writeCsv(scenario.getId(), iterationsCounter);
                        }

                    } catch (PopulationTooSmallException e) {
                        e.printStackTrace();
                    }
                    if (iterationsCounter == iterationsMax) {
                        long endTime = System.currentTimeMillis();
                        System.out.println("Runtime " + (endTime - startTime) / 1000 + " Seconds");
                    }
                    iterationsCounter++;

                } while (!isSolutionQualityReached(bestResult) && iterationsCounter <= iterationsMax);
                System.out.println("BruteForce Best Result : "+Math.round(bruteForceResult)+" Algorithm Best Result "+Math.round(bestResult));

        }
    }

    public double getBestResult(Population population){
        double bestResult=1000000;
        for (Tour tour : population.getTours()){
            if(tour.getFitness()< bestResult){
                bestResult = tour.getFitness();
            }
        }
        return bestResult;
    }

    public int getWorstResultIndex(Population population){
        double worstResult=0;
        int counter=0;
        for (Tour tour : population.getTours()){
            if(tour.getFitness()> worstResult) {
                worstResult = tour.getFitness();
            }
        }
        for(Tour tour : population.getTours()){
            if(tour.getFitness()==worstResult){
                return counter;
            }
            else counter++;
        }
        return 0;
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

    public double bruteForceResult(long permutationsNumber){
        BruteForce bruteForce = new BruteForce();
        Population population = bruteForce.createPermutations(availableCities,permutationsNumber);
        return bruteForce.getFitnessAll(population);
    }

    public static void main(String... args) {
        long permutationsNumber = Long.parseUnsignedLong("100");
        Application application = new Application();
        application.startupHSQLDB();
        application.loadData();
        BruteForce bruteForce = new BruteForce();
        Population population = bruteForce.createPermutations(availableCities, permutationsNumber);
        Scenario[] scenarios = application.initConfiguration();
        application.execute(scenarios, population);
       // bruteForce.evaluateFitness();
        application.shutdownHSQLDB();
    }
}
