package main;

import crossover.ICrossover;
import mutation.IMutation;
import selection.ISelection;
import data.HSQLDBManager;

public class LoadAlgorithm {
    Scenario scenario;
    ICrossover crossover;
    ISelection selection;
    IMutation mutation;

    public LoadAlgorithm(Scenario scenario){
        this.scenario = scenario;
        //HSQLDBManager.instance.addScenario();

    }

}
