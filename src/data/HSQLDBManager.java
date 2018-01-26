package data;

import main.Configuration;
import statistics.Statistics;

import java.sql.*;

public enum HSQLDBManager {
    instance;

    private Connection connection;
    private String driverName = "jdbc:hsqldb:";
    private String username = "sa";
    private String password = "";

    public void startup() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            String databaseURL = driverName + Configuration.instance.databaseFile;
            connection = DriverManager.getConnection(databaseURL, username, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void init() {
        dropTable();
        createTable();
    }

    public synchronized void update(String sqlStatement) {
        try {
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate(sqlStatement);
            if (result == -1)
                System.out.println("error executing " + sqlStatement);
            statement.close();
        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
        }
    }

    public void dropTable() {
        System.out.println("--- dropTable");

        StringBuilder sqlStringBuilder = new StringBuilder();
        sqlStringBuilder.append("DROP TABLE data");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder.toString());

        update(sqlStringBuilder.toString());
    }

    public void createTable() {
        StringBuilder sqlStringBuilder = new StringBuilder();
        sqlStringBuilder.append("CREATE TABLE data ").append(" ( ");
        sqlStringBuilder.append("id BIGINT NOT NULL").append(",");
        sqlStringBuilder.append("test VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("PRIMARY KEY (id)");
        sqlStringBuilder.append(" )");
        update(sqlStringBuilder.toString());


        sqlStringBuilder = new StringBuilder();
        sqlStringBuilder.append("CREATE TABLE iterations ").append(" ( ");
        sqlStringBuilder.append("id VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("iterationid  VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("fitnessvalue VARCHAR(20) NOT NULL");
        sqlStringBuilder.append(" )");
        update(sqlStringBuilder.toString());
    }

    public void addScenario(String scenarioId, String selectionType, String crossoverType, String mutationType, double crossoverProbability, double mutationProbability) {
        String statement = "INSERT INTO scenarios(id, selectionType, crossoverType, mutationType, crossoverProbability, mutationProbability) VALUES (" +
                "'" + scenarioId + "'," +
                "'" + selectionType + "'," +
                "'" + crossoverType + "'," +
                "'" + mutationType + "'," +
                crossoverProbability + "," +
                mutationProbability + ");";
        update(statement);
    }

    public void addFitnessToScenario(String id, int iterationid, double fitnessvalue) {
        String statement = "INSERT INTO iterations(id, iterationid, fitnessvalue) VALUES (" +
                "'" + id + "'," +
                "'" + iterationid + "'," +
                "'" + fitnessvalue + ");";
        update(statement);
    }

    public void writeCsv(String scenarioId) {
        Statistics stat = new Statistics();
        StringBuilder sqlStringBuilder = new StringBuilder();
        sqlStringBuilder.append("SELECT * FROM iterations WHERE id='");
        sqlStringBuilder.append(scenarioId).append("'");
        sqlStringBuilder.append(" )");
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sqlStringBuilder.toString());
            int size = 0;
            if (result != null) {
                result.beforeFirst();
                result.last();
                size = result.getRow();
            }
            double[] output = null;
            if (result.next()) {
                for (int j = 0; j < size; j++) {
                    output[j] = result.getDouble(j + 1);
                    System.out.println("SQL Table iteration: " + output[j]);
                }

            }
            stat.writeCSVFile(scenarioId, output);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String buildSQLStatement(long id, String test) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO data (id,test) VALUES (");
        stringBuilder.append(id).append(",");
        stringBuilder.append("'").append(test).append("'");
        stringBuilder.append(")");
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }

    public void insert(String test) {
        update(buildSQLStatement(System.nanoTime(), test));
    }

    public void shutdown() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("SHUTDOWN");
            connection.close();
        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
        }
    }
}