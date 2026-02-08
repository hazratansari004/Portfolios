package quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles MySQL database connection and CRUD operations for competitors.
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
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
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
     * @param c the competitor to insert
     * @return true if insertion was successful
     */
    public boolean insertCompetitor(HACompetitor c) {
        if (!isConnected()) return false;
        String sql = "INSERT INTO Competitors "
                + "(CompetitorID, FirstName, MiddleName, LastName, Level, Country, "
                + "Score1, Score2, Score3, Score4, Score5) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, c.getCompetitorId());
            pstmt.setString(2, c.getCompetitorName().getFirstName());
            pstmt.setString(3, c.getCompetitorName().getMiddleName());
            pstmt.setString(4, c.getCompetitorName().getLastName());
            pstmt.setString(5, c.getLevel());
            pstmt.setString(6, c.getCountry());
            int[] scores = c.getScoreArray();
            for (int i = 0; i < 5; i++) {
                pstmt.setInt(7 + i, scores[i]);
            }
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting competitor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all competitors from the database.
     * @return list of HACompetitor objects
     */
    public List<HACompetitor> getAllCompetitors() {
        List<HACompetitor> list = new ArrayList<>();
        if (!isConnected()) return list;
        String sql = "SELECT * FROM Competitors ORDER BY CompetitorID";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                HACompetitor c = resultSetToCompetitor(rs);
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving competitors: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves a single competitor by ID from the database.
     * @param id the competitor ID
     * @return HACompetitor object, or null if not found
     */
    public HACompetitor getCompetitorById(int id) {
        if (!isConnected()) return null;
        String sql = "SELECT * FROM Competitors WHERE CompetitorID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetToCompetitor(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving competitor: " + e.getMessage());
        }
        return null;
    }

    /**
     * Deletes a competitor from the database by ID.
     * @param id the competitor ID
     * @return true if deletion was successful
     */
    public boolean deleteCompetitor(int id) {
        if (!isConnected()) return false;
        String sql = "DELETE FROM Competitors WHERE CompetitorID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting competitor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing competitor in the database.
     * @param c the competitor with updated data
     * @return true if update was successful
     */
    public boolean updateCompetitor(HACompetitor c) {
        if (!isConnected()) return false;
        String sql = "UPDATE Competitors SET FirstName=?, MiddleName=?, LastName=?, "
                + "Level=?, Country=?, Score1=?, Score2=?, Score3=?, Score4=?, Score5=? "
                + "WHERE CompetitorID=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, c.getCompetitorName().getFirstName());
            pstmt.setString(2, c.getCompetitorName().getMiddleName());
            pstmt.setString(3, c.getCompetitorName().getLastName());
            pstmt.setString(4, c.getLevel());
            pstmt.setString(5, c.getCountry());
            int[] scores = c.getScoreArray();
            for (int i = 0; i < 5; i++) {
                pstmt.setInt(6 + i, scores[i]);
            }
            pstmt.setInt(11, c.getCompetitorId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating competitor: " + e.getMessage());
            return false;
        }
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

    /**
     * Converts a ResultSet row to an HACompetitor object.
     */
    private HACompetitor resultSetToCompetitor(ResultSet rs) throws SQLException {
        int id = rs.getInt("CompetitorID");
        String firstName = rs.getString("FirstName");
        String middleName = rs.getString("MiddleName");
        String lastName = rs.getString("LastName");
        String level = rs.getString("Level");
        String country = rs.getString("Country");
        int[] scores = new int[5];
        for (int i = 0; i < 5; i++) {
            scores[i] = rs.getInt("Score" + (i + 1));
        }

        Name name;
        if (middleName != null && !middleName.isEmpty()) {
            name = new Name(firstName, middleName, lastName);
        } else {
            name = new Name(firstName, lastName);
        }
        return new HACompetitor(id, name, level, country, scores);
    }
}
