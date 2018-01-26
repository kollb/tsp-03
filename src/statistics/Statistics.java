package statistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Statistics implements IStatistics {

    private String globalPath = "O://Software Engineering/01_teil_01/03_implementierung/03_training/02_workspace/_templates/01_tsp/src/statistics/";
    private String[] scenarios;

    private boolean median = false;
    private boolean mean = false;
    private boolean sd = false;
    private boolean quantile25 = false;
    private boolean quantile75 = false;
    private boolean quantile2575 = false;
    private boolean range = false;
    private boolean iqr = false;

    private boolean bar = false;
    private boolean box = false;
    private boolean dot = false;
    private boolean hist = false;
    private boolean strip = false;

    private boolean test = false;

    private boolean mff = false;

    // TODO: IntelliJ sagt 13 mal in dieser Datei: "String concatenation '+' in loop".
    // TODO: Wir sollten überall StringBuilder verwenden. IntelliJ kann das automatisch ändern.

    public void start(String... args) {
        if (args[0].equals("-d")) {
            int index = 0;
            while (args.length != (index + 2) && args[index + 1].charAt(0) != '-') index++;
            scenarios = new String[index];
            for (int i = 0; i < index; i++) scenarios[i] = args[i + 1];
            index++;

            //case -m
            if (args.length != (index + 2) && args[index].equals("-m")) {
                while (args.length != (index + 2) && args[index + 1].charAt(0) != '-') {
                    index++;
                    switch (args[index]) {
                        case "median":
                            median = true;
                            break;
                        case "mean":
                            mean = true;
                            break;
                        case "sd":
                            sd = true;
                            break;
                        case "quantile=0.25":
                            quantile25 = true;
                            break;
                        case "quantile=0.25,0.75":
                            quantile75 = true;
                            break;
                        case "quantile=0.25-0.75":
                            quantile2575 = true;
                            break;
                        case "range":
                            range = true;
                            break;
                        case "iqr":
                            iqr = true;
                            break;
                        default:
                            break;
                    }
                }
                index++;
            }

            //case -p
            if (args.length != (index + 2) && args[index].equals("-p")) {
                while (args.length != (index + 2) && args[index + 1].charAt(0) != '-') {
                    index++;
                    switch (args[index]) {
                        case "bar":
                            bar = true;
                            break;
                        case "box":
                            box = true;
                            break;
                        case "dot":
                            dot = true;
                            break;
                        case "hist":
                            hist = true;
                            break;
                        case "strip":
                            strip = true;
                            break;
                        default:
                            break;
                    }
                }
                index++;
            }

            //case -t
            if (args.length != (index + 2) && args[index].equals("-t")) {
                test = true;
                index++;
            }

            //case -a
            if (args.length != (index + 2) && args[index].equals("-mff")) {
                mff = true;
            }

            buildMeasureRFile();
            buildBarPlotFile();
            buildBoxPlotRFile();
            buildDotPlotRFile();
            buildStripChartRFile();
            buildTTestRFile();
            buildHistogramRFile();
            buildMostFrequentFitnessValuesRFile();
        }
    }

    public void writeCSVFile(String scenarioNumber, double[] values) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "scenario_" + scenarioNumber + ".csv")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (double value : values) {
            writer.format("" + value);
            writer.println();
        }
        writer.close();
    }

    public void buildMeasureRFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "measures.R")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.format("cat(rep(\"\\n\",64))\n\n" +
                "setwd(\"" + globalPath + "\")\n" +
                "getwd()\n\n");

        for (int i = 0; i < scenarios.length; i++) {
            writer.format("s" + i + " <- as.numeric(read.csv(\"scenario_" + scenarios[i] + ".csv\",header=FALSE))\n");
        }

        //writes median
        if (median) {
            writer.format("\n# median\n");
            String strMedian;
            if (scenarios.length == 1) strMedian = "median(s01)";
            else {
                strMedian = "c(";
                for (int i = 0; i < (scenarios.length - 1); i++) {
                    strMedian = strMedian + "median(s" + scenarios[i] + "),";
                }
                strMedian = strMedian + "median(s" + scenarios[scenarios.length - 1] + "))";
            }
            writer.format(strMedian + "\n");
        }

        //writes mean
        if (mean) {
            writer.format("\n# mean\n");
            String strMean;
            if (scenarios.length == 1) strMean = "round(mean(s01),digits = 2)";
            else {
                strMean = "c(";
                for (int i = 0; i < (scenarios.length - 1); i++) {
                    strMean = strMean + "round(mean(s" + scenarios[i] + "),digits = 2),";
                }
                strMean = strMean + "round(mean(s" + scenarios[scenarios.length - 1] + "),digits = 2))";
            }
            writer.format(strMean + "\n");
        }

        //writes sd
        if (sd) {
            writer.format("\n# sd\n");
            String strSd;
            if (scenarios.length == 1) strSd = "round(sd(s01),digits = 2)";
            else {
                strSd = "c(";
                for (int i = 0; i < (scenarios.length - 1); i++) {
                    strSd = strSd + "round(sd(s" + scenarios[i] + "),digits = 2),";
                }
                strSd = strSd + "round(sd(s" + scenarios[scenarios.length - 1] + "), digits = 2))";
            }
            writer.format(strSd + "\n");
        }

        //writes quantile
        if (quantile2575 || quantile25 || quantile75) {
            writer.format("\n# quantile\n");

            if (quantile25) {
                String strQuantile;
                if (scenarios.length == 1) strQuantile = "quantile(s01,0.25)";
                else {
                    strQuantile = "c(";
                    for (int i = 0; i < (scenarios.length - 1); i++) {
                        strQuantile = strQuantile + "quantile(s" + scenarios[i] + ",0.25),";
                    }
                    strQuantile = strQuantile + "quantile(s" + scenarios[scenarios.length - 1] + ",0.25))";
                }
                writer.format(strQuantile + "\n\n");
            }

            if (quantile75) {
                for (String scenario : scenarios) {
                    writer.format("quantile(s" + scenario + ",probs = c(0.25,0.75))\n");
                }
                writer.format("\n");
            }

            if (quantile2575) {
                for (String scenario : scenarios) {
                    writer.format("quantile(s" + scenario + ",probs = c(0.25,0.50,0.75))\n");
                }
            }
        }

        if (range) {
            //writes range
            writer.format("\n# range\n");
            for (String scenario : scenarios) {
                writer.format("c(max(s" + scenario + ") − min(s" + scenario + "))\n");
            }
        }

        if (iqr) {
            //writes interquartile range
            writer.format("\n# interquartile range\n");
            String strIR;
            if (scenarios.length == 1) strIR = "quantile(s01,0.75) - quantile(s01,0.25)";
            else {
                strIR = "c(";
                for (int i = 0; i < (scenarios.length - 1); i++) {
                    strIR = strIR + "quantile(s" + scenarios[i] + ",0.75) - quantile(s" + scenarios[i] + ",0.25),";
                }
                strIR = strIR + "quantile(s" + scenarios.length + ",0.75) - quantile(s" + scenarios.length + ",0.25))";
            }
            writer.format(strIR + "\n");
        }

        //closes writer
        writer.close();
    }

    public void buildBarPlotFile() {
        if (bar) {
            double fitness[] = {1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5};
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "barplot_scenario.R")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.format("setwd(\"" + globalPath + "\")\n" +
                    "getwd()\n\n");
            writer.format("pdf(\"plots/example_scenario_01.pdf\",height = 10,width = 10,paper = \"A4r\")\n\n");

            for (int i = 0; i < (scenarios.length - 1); i++) {
                writer.format("s" + scenarios[i] + " <- c(");
                for (double fitnes : fitness) {
                    writer.format("round(" + fitness[fitness.length - 1] + "/" + fitnes + ",digits=2)*100,");
                }
                writer.format("round(" + fitness[fitness.length - 1] + "/" + fitness[fitness.length - 1] + ",digits=2)*100)\n\n");
            }

            String strScenarios = "";
            for (int i = 0; i < (scenarios.length - 1); i++) strScenarios = strScenarios + "s" + scenarios[i] + ",";
            writer.format("barplot(" + strScenarios + "ylim=c(0,100),col=\"black\",ylab = \"solution quality (%%)\",xlab = \"iterations\",width = 0.1,main = \"Genetic Algorithms - TSP280 - Scenario 1\")\n\n");

            writer.format("dev.off()");
            writer.close();
        }
    }

    public void buildBoxPlotRFile() {
        if (box) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "boxplot_scenario.R")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.format("cat(rep(\"\\n\",64))\n\n" +
                    "setwd(\"" + globalPath + "\")\n" +
                    "getwd()\n\n");

            for (int i = 0; i < (scenarios.length - 1); i++) {
                writer.format("s" + i + " <- as.numeric(read.csv(\"scenario_" + scenarios[i] + ".csv\",header=FALSE))\n");
            }
            String count = "";
            String scount = "";
            String sccount = "";

            for (int i = 0; i < (scenarios.length - 1); i++) {
                count = count + "_" + scenarios[i];
                scount = scount + "s" + scenarios[i] + ",";
                sccount = sccount + "\"Scenario " + scenarios[i] + "\",";
            }
            scount = scount.substring(0, scount.length() - 1);
            sccount = sccount.substring(0, sccount.length() - 1);


            writer.format("\npdf(\"plots/example_boxplot_scenario" + count + ".pdf\",height = 10,width = 10,paper = \"A4r\")\n\n");

            writer.format("boxplot(" + scount + ",ylab = \"distance\",names=c(" + sccount + "),main = \"Genetic Algorithms - TSP280\")\n\n");

            writer.format("dev.off()");
            writer.close();
        }
    }

    public void buildDotPlotRFile() {
        if (dot) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "dplot.R")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.format("cat(rep(\"\\n\",64))\n\n" +
                    "setwd(\"" + globalPath + "\")\n" +
                    "getwd()\n\n");

            for (int i = 0; i < (scenarios.length - 1); i++) {
                writer.format("s" + scenarios[i] + " <- as.numeric(read.csv(\"scenario_" + scenarios[i] + ".csv\",header=FALSE))\n");
            }

            writer.format("\npdf(\"plots/example_scenario01_dotplot.pdf\",height = 10,width = 10,paper = \"A4r\")\n\n");

            String strScenarios = "";
            for (int i = 0; i < (scenarios.length - 1); i++) strScenarios = strScenarios + "s" + scenarios[i] + ",";
            writer.format("plot(" + strScenarios + "col=\"black\",ylab = \"distance\",xlab = \"iterations\",cex = 0.1,main = \"Genetic Algorithms - TSP280 - Scenario 01\")\n\n");

            writer.format("dev.off()");
            writer.close();
        }
    }

    public void buildStripChartRFile() {
        if (strip) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "stripchart.R")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.format("cat(rep(\"\\n\",64))\n\n" +
                    "setwd(\"" + globalPath + "\")\n" +
                    "getwd()\n\n");

            for (int i = 0; i < (scenarios.length - 1); i++) {
                writer.format("s" + scenarios[i] + " <- as.numeric(read.csv(\"scenario_" + scenarios[i] + ".csv\",header=FALSE))\n");
            }

            writer.format("\npdf(\"plots/example_scenario01_stripchart.pdf\",height = 10,width = 10,paper = \"A4r\")\n\n");

            String strScenarios = "";
            for (int i = 0; i < (scenarios.length - 1); i++) strScenarios = strScenarios + "s" + scenarios[i] + ",";
            writer.format("stripchart(" + strScenarios + "xlim=c(2500,5000),main = \"Genetic Algorithms - TSP280 - Scenario 01\",method=\"stack\")\n\n");

            writer.format("dev.off()");

            writer.close();
        }
    }

    public void buildTTestRFile() {
        if (test) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "ttest.R")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.format("cat(rep(\"\\n\",64))\n\n" +
                    "setwd(\"" + globalPath + "\")\n" +
                    "getwd()\n\n");

            for (int i = 0; i < (scenarios.length - 1); i++) {
                writer.format("s" + scenarios[i] + " <- as.numeric(read.csv(\"scenario_" + scenarios[i] + ".csv\",header=FALSE))\n");
            }

            String strMean;
            if (scenarios.length == 1) {
                strMean = "mean(s01)";
            } else {
                strMean = "c(";
                for (int i = 0; i < (scenarios.length - 1); i++) {
                    strMean = strMean + "mean(s" + scenarios[i] + "),";
                }
                strMean = strMean + "mean(s" + scenarios[scenarios.length - 1] + "))";
            }

            String strScenarios = "";
            for (int i = 0; i < (scenarios.length - 1); i++) {
                strScenarios = strScenarios + "s" + scenarios[i] + ",";
            }
            writer.format("\ntest(" + strScenarios + " s" + scenarios[scenarios.length - 1] + ")");

            writer.close();
        }
    }

    public void buildHistogramRFile() {
        if (hist) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "histogram.R")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.format("cat(rep(\"\\n\",64))\n\n" +
                    "setwd(\"" + globalPath + "\")\n" +
                    "getwd()\n\n");

            for (int i = 0; i < (scenarios.length - 1); i++) {
                writer.format("s" + scenarios[i] + " <- as.numeric(read.csv(\"scenario_" + scenarios[i] + ".csv\",header=FALSE))\n");
            }

            writer.format("\npdf(\"plots/example_scenario01_histogram.pdf\",height = 10,width = 10,paper = \"A4r\")\n\n");

            String strScenarios = "";
            for (int i = 0; i < (scenarios.length - 1); i++) strScenarios = strScenarios + "s" + scenarios[i] + ",";
            writer.format("hist(" + strScenarios + "xlim=c(2500,5000),ylim=c(0,200),xlab = \"distance\",breaks=100,main = \"Genetic Algorithms - TSP280\")\n\n");

            writer.format("dev.off()");

            writer.close();
        }
    }

    public void buildMostFrequentFitnessValuesRFile() {
        if (mff) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(globalPath + "mff.R")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.format("cat(rep(\"\\n\",64))\n\n" +
                    "setwd(\"" + globalPath + "\")\n" +
                    "getwd()\n\n");

            for (int i = 0; i < (scenarios.length - 1); i++) {
                writer.format("s" + scenarios[i] + " <- as.numeric(read.csv(\"scenario_" + scenarios[i] + ".csv\",header=FALSE))\n");
            }

            writer.format("\n# most frequent fitness\n");
            for (int i = 0; i < (scenarios.length - 1); i++)
                writer.format("sort(table(s" + scenarios[i] + "),decreasing=TRUE)[1]\n");

            writer.close();
        }
    }

}
