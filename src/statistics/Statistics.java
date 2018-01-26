package statistics;

import java.io.*;

public class Statistics implements IStatistics {

    private String globalPath = "O://Software Engineering/01_teil_01/03_implementierung/03_training/02_workspace/_templates/01_tsp/src/statistics/";
    private int countScenario = 2;

    public void writeCSVFile() {
        for(int counter = 1; counter <= countScenario; counter++) {
            String[] split = {};
            try {
                java.io.BufferedReader FileReader =
                        new java.io.BufferedReader(
                                new java.io.FileReader(
                                        new java.io.File("O:/Software Engineering/01_teil_01/03_implementierung/03_training/01_aufgaben/statistical_analysis/daten/example_data_scenario_01.csv")
                                )
                        );

                String zeile = "";

                while (null != (zeile = FileReader.readLine())) {
                    split = zeile.split(",");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "scenario_" + counter + ".csv")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < split.length; i++) {
                writer.format(split[i]);
                writer.println();
            }
            writer.close();
        }
    }

    public void buildMeasureRFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter( new BufferedWriter( new FileWriter( globalPath + "measures.R" ) ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.format("cat(rep(\"\\n\",64))\n\n" +
                "setwd(\"" + globalPath + "\")\n" +
                "getwd()\n\n");

        for(int i = 1; i <= countScenario; i++){
            writer.format("s" + i + " <- as.numeric(read.csv(\"daten/example_data_scenario_" + i + ".csv\",header=FALSE))\n");
        }

        //writes median
        writer.format("\n# median\n");
        String strMedian = "";
        if(countScenario == 1) strMedian = "median(s1)";
        else{
            strMedian = "c(";
            for(int i = 1; i < countScenario; i++){
                strMedian = strMedian + "median(s" + i + "),";
            }
            strMedian = strMedian + "median(s" + countScenario + "))";
        }
        writer.format(strMedian + "\n");

        //writes mean
        writer.format("\n# mean\n");
        String strMean = "";
        if(countScenario == 1) strMean = "round(mean(s1),digits = 2)";
        else{
            strMean = "c(";
            for(int i = 1; i < countScenario; i++){
                strMean = strMean + "round(mean(s" + i + "),digits = 2),";
            }
            strMean = strMean + "round(mean(s" + countScenario + "),digits = 2))";
        }
        writer.format(strMean + "\n");

        //writes sd
        writer.format("\n# sd\n");
        String strSd = "";
        if(countScenario == 1) strSd = "round(sd(s1),digits = 2)";
        else{
            strSd = "c(";
            for(int i = 1; i < countScenario; i++){
                strSd = strSd + "round(sd(s" + i + "),digits = 2),";
            }
            strSd = strSd + "round(sd(s" + countScenario + "), digits = 2))";
        }
        writer.format(strSd + "\n");

        //writes quantile
        writer.format("\n# quantile\n");
        String strQuantile = "";
        if(countScenario == 1) strQuantile = "quantile(s1,0.25)";
        else{
            strQuantile = "c(";
            for(int i = 1; i < countScenario; i++){
                strQuantile = strQuantile + "quantile(s" + i + ",0.25),";
            }
            strQuantile = strQuantile + "quantile(s" + countScenario + ",0.25))";
        }
        writer.format(strQuantile + "\n\n");
        for(int i = 1; i <= countScenario; i++) writer.format("quantile(s" + i + ",probs = c(0.25,0.75))\n");
        writer.format("\n");
        for(int i = 1; i <= countScenario; i++) writer.format("quantile(s" + i + ",probs = c(0.25,0.50,0.75))\n");

        //writes range
        writer.format("\n# range\n");
        for(int i = 1; i <= countScenario; i++) writer.format("c(max(s" + i + ") − min(s" + i + "))\n");

        //writes interquartile range
        writer.format("\n# interquartile range\n");
        String strIR = "";
        if(countScenario == 1) strIR = "quantile(s1,0.75) - quantile(s1,0.25)";
        else{
            strIR = "c(";
            for(int i = 1; i < countScenario; i++){
                strIR = strIR + "quantile(s" + i + ",0.75) - quantile(s" + i + ",0.25),";
            }
            strIR = strIR + "quantile(s" + countScenario + ",0.75) - quantile(s" + countScenario + ",0.25))";
        }
        writer.format(strIR + "\n");

        //closes writer
        writer.close();
    }

    public void buildBarPlotFile() {
            double fitness[] = {1,2,3,4,5,6,7,8,1,2,3,4,5};
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "barplot_scenario.R")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.format("setwd(\"" + globalPath + "\")\n" +
                    "getwd()\n\n");
            writer.format("pdf(\"plots/example_scenario_01.pdf\",height = 10,width = 10,paper = \"A4r\")\n\n");

            for(int i = 1; i <= countScenario; i++) {
                writer.format("s" + i + " <- c(");
                for (int j = 0; j < fitness.length; j++) {
                    writer.format("round(" + fitness[fitness.length - 1] + "/" + fitness[j] + ",digits=2)*100,");
                }
                writer.format("round(" + fitness[fitness.length - 1] + "/" + fitness[fitness.length - 1] + ",digits=2)*100)\n\n");
            }

            String strScenarios = "";
            for(int i = 1; i <= countScenario; i++) strScenarios = strScenarios + "s" + i + ",";
            writer.format("barplot(" + strScenarios + "ylim=c(0,100),col=\"black\",ylab = \"solution quality (%%)\",xlab = \"iterations\",width = 0.1,main = \"Genetic Algorithms - TSP280 - Scenario 1\")\n\n");

            writer.format("dev.off()");
            writer.close();
    }

    public void buildBoxPlotRFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter( new BufferedWriter( new FileWriter( globalPath + "boxplot_scenario.R" ) ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.format("cat(rep(\"\\n\",64))\n\n" +
                "setwd(\"" + globalPath + "\")\n" +
                "getwd()\n\n");

        for(int i = 1; i <= countScenario; i++){
            writer.format("s" + i + " <- as.numeric(read.csv(\"daten/example_data_scenario_" + i + ".csv\",header=FALSE))\n");
        }
        String count = "";
        String scount = "";
        String sccount = "";

        for (int i = 1; i <= countScenario; i++){
            count = count + "_" + i;
            scount = scount + "s" + i + ",";
            sccount = sccount + "\"Scenario " + i + "\",";
        }
        scount = scount.substring(0, scount.length()-1);
        sccount = sccount.substring(0, sccount.length()-1);


        writer.format("\npdf(\"plots/example_boxplot_scenario" + count + ".pdf\",height = 10,width = 10,paper = \"A4r\")\n\n");

        writer.format("boxplot(" + scount + ",ylab = \"distance\",names=c(" + sccount + "),main = \"Genetic Algorithms - TSP280\")\n\n");

        writer.format("dev.off()");
        writer.close();
    }

    public void buildDotPlotRFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter( new BufferedWriter( new FileWriter( globalPath + "dplot.R" ) ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.format("cat(rep(\"\\n\",64))\n\n" +
                "setwd(\"" + globalPath + "\")\n" +
                "getwd()\n\n");

        for(int i = 1; i <= countScenario; i++){
            writer.format("s" + i + " <- as.numeric(read.csv(\"daten/example_data_scenario_" + i + ".csv\",header=FALSE))\n");
        }

        writer.format("\npdf(\"plots/example_scenario01_dotplot.pdf\",height = 10,width = 10,paper = \"A4r\")\n\n");

        String strScenarios = "";
        for(int i = 1; i <= countScenario; i++) strScenarios = strScenarios + "s" + i + ",";
        writer.format("plot(" + strScenarios + "col=\"black\",ylab = \"distance\",xlab = \"iterations\",cex = 0.1,main = \"Genetic Algorithms - TSP280 - Scenario 01\")\n\n");

        writer.format("dev.off()");
        writer.close();
    }

    public void buildStripChartRFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter( new BufferedWriter( new FileWriter( globalPath + "stripchart.R" ) ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.format("cat(rep(\"\\n\",64))\n\n" +
                "setwd(\"" + globalPath + "\")\n" +
                "getwd()\n\n");

        for(int i = 1; i <= countScenario; i++){
            writer.format("s" + i + " <- as.numeric(read.csv(\"daten/example_data_scenario_" + i + ".csv\",header=FALSE))\n");
        }

        writer.format("\npdf(\"plots/example_scenario01_stripchart.pdf\",height = 10,width = 10,paper = \"A4r\")\n\n");

        String strScenarios = "";
        for(int i = 1; i <= countScenario; i++) strScenarios = strScenarios + "s" + i + ",";
        writer.format("stripchart(" + strScenarios + "xlim=c(2500,5000),main = \"Genetic Algorithms - TSP280 - Scenario 01\",method=\"stack\")\n\n");

        writer.format("dev.off()");

        writer.close();
    }

    public void buildTTestRFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter( new BufferedWriter( new FileWriter( globalPath + "ttest.R" ) ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.format("cat(rep(\"\\n\",64))\n\n" +
                "setwd(\"" + globalPath + "\")\n" +
                "getwd()\n\n");

        for(int i = 1; i <= countScenario; i++){
            writer.format("s" + i + " <- as.numeric(read.csv(\"daten/example_data_scenario_" + i + ".csv\",header=FALSE))\n");
        }

        String strMean = "";
        if(countScenario == 1) strMean = "mean(s1)";
        else{
            strMean = "c(";
            for(int i = 1; i < countScenario; i++){
                strMean = strMean + "mean(s" + i + "),";
            }
            strMean = strMean + "mean(s" + countScenario + "))";
        }

        String strScenarios = "";
        for(int i = 1; i < countScenario; i++) strScenarios = strScenarios + "s" + i + ",";
        writer.format("\ntest(" + strScenarios + " s" + countScenario + ")");

        writer.close();
    }

    public void buildHistogramRFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter( new BufferedWriter( new FileWriter( globalPath + "histogram.R" ) ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.format("cat(rep(\"\\n\",64))\n\n" +
                "setwd(\"" + globalPath + "\")\n" +
                "getwd()\n\n");

        for(int i = 1; i <= countScenario; i++){
            writer.format("s" + i + " <- as.numeric(read.csv(\"daten/example_data_scenario_" + i + ".csv\",header=FALSE))\n");
        }

        writer.format("\npdf(\"plots/example_scenario01_histogram.pdf\",height = 10,width = 10,paper = \"A4r\")\n\n");

        String strScenarios = "";
        for(int i = 1; i <= countScenario; i++) strScenarios = strScenarios + "s" + i + ",";
        writer.format("hist(" + strScenarios + ",xlim=c(2500,5000),ylim=c(0,200),xlab = \"distance\",breaks=100,main = \"Genetic Algorithms - TSP280\")\n\n");

        writer.format("dev.off()");

        writer.close();
    }

    public void buildMostFrequentFitnessValuesRFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter( new BufferedWriter( new FileWriter( globalPath + "mff.R" ) ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.format("cat(rep(\"\\n\",64))\n\n" +
                "setwd(\"" + globalPath + "\")\n" +
                "getwd()\n\n");

        for(int i = 1; i <= countScenario; i++){
            writer.format("s" + i + " <- as.numeric(read.csv(\"daten/example_data_scenario_" + i + ".csv\",header=FALSE))\n");
        }

        writer.format("# most frequent fitness\n\n");
        for(int i = 1; i <= countScenario; i++) writer.format("sort(table(s" + i + "),decreasing=TRUE)[1]");

        writer.close();
    }

}
