package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import main.Configuration;

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
            connection = DriverManager.getConnection(databaseURL,username,password);
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

        sqlStringBuilder.append("CREATE TABLE scenarios ").append(" ( ");
        sqlStringBuilder.append("id VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("crossovertype VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("crossoverratio FLOAT(1,4) NOT NULL").append(",");
        sqlStringBuilder.append("mutationtype  VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("mutationratio FLOAT(1,4) NOT NULL").append(",");
        sqlStringBuilder.append("selection VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("buildstatistics VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("isevaluated VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("evaluation VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("maximumnumberofevaluations VARCHAR(20) NOT NULL");
        sqlStringBuilder.append(" )");
        update(sqlStringBuilder.toString());

        sqlStringBuilder.append("CREATE TABLE iteration ").append(" ( ");
        sqlStringBuilder.append("id VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("scenario VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("iterationid VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder.append("fitness  VARCHAR(20) NOT NULL");
        sqlStringBuilder.append(" )");
        update(sqlStringBuilder.toString());
    }

    public void addScenario(String id, String selectionType, String crossoverType, double crossoverratio,String mutationType,double mutationratio, boolean buildstatistics ,boolean isEvaluated,long maximumnumberofevaluations, double mutationProbability) {
        String statement = "INSERT INTO scenarios(id, selectionType, crossoverType, mutationType, crossoverProbability, mutationProbability) VALUES (" +
                "'" + id + "'," +
                "'" + crossoverType + "'," +
                "'" + crossoverratio + "'," +
                "'" + mutationType + "'," +
                "'" + mutationratio + "'," +
                "'" + selectionType + "'," +
                "'" + buildstatistics + "'," +
                "'" + isEvaluated + "'," +
                "'" + maximumnumberofevaluations + "," +
                mutationProbability + ");";
        update(statement);
    }

    public void addFitnessInIteration(String id,String scenario,String iterationid,int fitness){
        String statement = "INSERT INTO iteration(id, scenario, iterationid, fitness) VALUES (" +
                "'" + id + "'," +
                "'" + scenario + "'," +
                "'" + iterationid + "'," +
                "'" + fitness + ");";
        update(statement);
    }

    public String buildSQLStatement(long id,String test) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO data (id,test) VALUES (");
        stringBuilder.append(id).append(",");
        stringBuilder.append("'").append(test).append("'");
        stringBuilder.append(")");
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }

    public void insert(String test) {
        update(buildSQLStatement(System.nanoTime(),test));
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