package statistics;

public interface IStatistics {
    void writeCSVFile();
    void buildMeasureRFile();
    void buildBarPlotFile();
    void buildBoxPlotRFile();
    void buildDotPlotRFile();
    void buildHistogramRFile();
    void buildStripChartRFile();
    void buildTTestRFile();
    void buildMostFrequentFitnessValuesRFile();
}