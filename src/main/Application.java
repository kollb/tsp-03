package main;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import base.City;
import crossover.ICrossover;
import data.HSQLDBManager;
import data.InstanceReader;
import data.TSPLIBReader;
import mutation.IMutation;
import selection.ISelection;

public class Application {
    private ArrayList<City> availableCities;
    private double[][] distances;

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
        printMatrix(distances);

        instanceReader.close();

        System.out.println();
    }

    public Scenario[] initConfiguration() {
        System.out.println("--- GeneticAlgorithm.initConfiguration()");
        System.out.println();
        Scenario[] scenarios = null;
        try {
            scenarios = new XMLParser(new File("configuration/genetic_algorithm_tsp.xml")).getScenarios();

            if (scenarios == null){
                throw new Exception();
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Szenario-Datei nicht gefunden");
            System.exit(1);
        }
        System.out.println("Es wurden "+ scenarios.length+" Szenarien geladen. ");
        HSQLDBManager.instance.insert("Test");
        HSQLDBManager.instance.checkTable();
        return scenarios;
    }

    public void execute() {
        System.out.println("--- GeneticAlgorithm.execute()");
        HSQLDBManager.instance.insert("hello world");
    }

    public static void main(String... args) {
        Application application = new Application();
        application.startupHSQLDB();
        application.loadData();
        application.initConfiguration();
        application.execute();
        application.shutdownHSQLDB();
    }
}