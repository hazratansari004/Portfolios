package quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles MySQL database connection and CRUD operations for competitors.
 *
 * @author Sailesh Kumar Mandal
 * Database: CompetitionDB
 * Table: Competitors (CompetitorID, FirstName, MiddleName, LastName, Level, Country,
 *                      Score1, Score2, Score3, Score4, Score5)
 *
 * Requires MySQL JDBC driver (mysql-connector-java) on the classpath.
 * Configure DB_URL, DB_USER, and DB_PASSWORD before use.
 */
public class DatabaseConnection {

    // Database configuration — update these for your MySQL setup
    // For production, use environment variables or a config file instead
    private static final String DB_URL = System.getenv("DB_URL") != null
            ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/CompetitionDB";
    private static final String DB_USER = System.getenv("DB_USER") != null
            ? System.getenv("DB_USER") : "root";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null
            ? System.getenv("DB_PASSWORD") : "";

    private Connection connection;

    /**
     * Static convenience method to get a database connection.
     * Matches the coursework specification pattern.
     * @return a new Connection to the CompetitionDB database
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getStaticConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            String msg = "MySQL JDBC Driver not found. Please add MySQL Connector/J to your project's classpath.\n"
                       + "Download it from: https://dev.mysql.com/downloads/connector/j/\n"
                       + "In Eclipse: Project -> Properties -> Java Build Path -> Libraries -> Add External JARs...\n"
                       + "Or when running from terminal, include the jar on the classpath, e.g.:\n"
                       + "  javac -cp .:mysql-connector-java-8.x.xx.jar quiz/Manager.java\n"
                       + "  java -cp .:mysql-connector-java-8.x.xx.jar quiz.Manager";
            throw new SQLException(msg, e);
        }
    }

    /** Creates a new DatabaseConnection instance. */
    public DatabaseConnection() {
        this.connection = null;
    }

    /**
     * Establishes a connection to the MySQL database.
     * @return true if connection is successful, false otherwise
     */
    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please add MySQL Connector/J to your project's classpath.\n"
                    + "Download it from: https://dev.mysql.com/downloads/connector/j/\n"
                    + "In Eclipse: Project -> Properties -> Java Build Path -> Libraries -> Add External JARs...\n"
                    + "Or when running from terminal, include the jar on the classpath, e.g.:\n"
                    + "  javac -cp .:mysql-connector-java-8.x.xx.jar quiz/Manager.java\n"
                    + "  java -cp .:mysql-connector-java-8.x.xx.jar quiz.Manager");
            return false;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns the current database connection.
     * @return Connection object, or null if not connected
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Checks if the database connection is active.
     * @return true if connected
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Creates the Competitors table if it does not already exist.
     * @return true if table creation was successful or table already exists
     */
    public boolean createTable() {
        if (!isConnected()) return false;
        String sql = "CREATE TABLE IF NOT EXISTS Competitors ("
                + "CompetitorID INT PRIMARY KEY, "
                + "FirstName VARCHAR(50) NOT NULL, "
                + "MiddleName VARCHAR(50) DEFAULT '', "
                + "LastName VARCHAR(50) NOT NULL, "
                + "Level VARCHAR(20) NOT NULL, "
                + "Country VARCHAR(50) DEFAULT '', "
                + "Score1 INT DEFAULT 0, "
                + "Score2 INT DEFAULT 0, "
                + "Score3 INT DEFAULT 0, "
                + "Score4 INT DEFAULT 0, "
                + "Score5 INT DEFAULT 0"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a competitor into the database.
     * Delegates to SKMCompetitor.saveToDatabase().
     * @param c the competitor to insert
     * @return true if insertion was successful
     */
    public boolean insertCompetitor(SKMCompetitor c) {
        if (!isConnected()) return false;
        return c.saveToDatabase(connection);
    }

    /**
     * Retrieves all competitors from the database.
     * Delegates to SKMCompetitor.readAllFromDatabase().
     * @return list of SKMCompetitor objects
     */
    public List<SKMCompetitor> getAllCompetitors() {
        if (!isConnected()) return new ArrayList<>();
        return SKMCompetitor.readAllFromDatabase(connection);
    }

    /**
     * Retrieves a single competitor by ID from the database.
     * Delegates to SKMCompetitor.readFromDatabase().
     * @param id the competitor ID
     * @return SKMCompetitor object, or null if not found
     */
    public SKMCompetitor getCompetitorById(int id) {
        if (!isConnected()) return null;
        return SKMCompetitor.readFromDatabase(connection, id);
    }

    /**
     * Deletes a competitor from the database by ID.
     * @param id the competitor ID
     * @return true if deletion was successful
     */
    public boolean deleteCompetitor(int id) {
        if (!isConnected()) return false;
        // Create a temporary competitor to use its deleteFromDatabase method
        SKMCompetitor temp = new SKMCompetitor(id, new Name("", ""), "");
        return temp.deleteFromDatabase(connection);
    }

    /**
     * Updates an existing competitor in the database.
     * Delegates to SKMCompetitor.updateInDatabase().
     * @param c the competitor with updated data
     * @return true if update was successful
     */
    public boolean updateCompetitor(SKMCompetitor c) {
        if (!isConnected()) return false;
        return c.updateInDatabase(connection);
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
