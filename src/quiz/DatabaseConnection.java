package quiz; // declares this class belongs to the "quiz" package

import java.sql.*; // imports all classes from java.sql package for database operations
import java.util.ArrayList; // imports ArrayList class for storing lists of competitors
import java.util.List; // imports List interface used as the return type for collections

/**
 * Handles MySQL database connection and CRUD operations for competitors.
 */
public class DatabaseConnection { // defines the DatabaseConnection class as public

    // database URL pointing to MySQL server on localhost, port 3306, database "CompetitionDB"
    private static final String DB_URL = "jdbc:mysql://localhost:3306/CompetitionDB";
    private static final String DB_USER = "root"; // database username set to "root"
    private static final String DB_PASSWORD = ""; // database password set to empty string (no password)

    private Connection connection; // instance variable to hold the database connection object

    /**
     * Static convenience method to get a database connection.
     * Matches the coursework specification pattern.
     * @return a new Connection to the CompetitionDB database
     * @throws SQLException if the connection cannot be established
     */
    // static method that returns a new database connection without needing an instance
    public static Connection getStaticConnection() throws SQLException {
        try { // start try block to handle potential errors
            Class.forName("com.mysql.cj.jdbc.Driver"); // loads the MySQL JDBC driver class into memory
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); // creates and returns a new connection
        } catch (ClassNotFoundException e) { // catches error if MySQL driver JAR is missing
            // build a helpful error message telling the user how to add the MySQL driver
            String msg = "MySQL JDBC Driver not found. Please add MySQL Connector/J to your project's classpath.\n"
                       + "Download it from: https://dev.mysql.com/downloads/connector/j/\n"
                       + "In Eclipse: Project -> Properties -> Java Build Path -> Libraries -> Add External JARs...\n"
                       + "Or when running from terminal, include the jar on the classpath, e.g.:\n"
                       + "  javac -cp .:mysql-connector-java-8.x.xx.jar quiz/Manager.java\n"
                       + "  java -cp .:mysql-connector-java-8.x.xx.jar quiz.Manager";
            throw new SQLException(msg, e); // wraps the error in an SQLException and throws it
        }
    } // end of getStaticConnection method

    /** Creates a new DatabaseConnection instance. */
    public DatabaseConnection() { // constructor for DatabaseConnection class
        this.connection = null; // initialises the connection to null (not connected yet)
    } // end of constructor

    /**
     * Establishes a connection to the MySQL database.
     * @return true if connection is successful, false otherwise
     */
    public boolean connect() { // method to connect to the database, returns true if successful
        try { // start try block to catch connection errors
            Class.forName("com.mysql.cj.jdbc.Driver"); // loads the MySQL JDBC driver class
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); // opens connection to the database
            return true; // return true indicating connection was successful
        } catch (ClassNotFoundException e) { // catches error if the MySQL driver is not found
            // prints a detailed error message with instructions to add the MySQL driver
            System.err.println("MySQL JDBC Driver not found. Please add MySQL Connector/J to your project's classpath.\n"
                    + "Download it from: https://dev.mysql.com/downloads/connector/j/\n"
                    + "In Eclipse: Project -> Properties -> Java Build Path -> Libraries -> Add External JARs...\n"
                    + "Or when running from terminal, include the jar on the classpath, e.g.:\n"
                    + "  javac -cp .:mysql-connector-java-8.x.xx.jar quiz/Manager.java\n"
                    + "  java -cp .:mysql-connector-java-8.x.xx.jar quiz.Manager");
            return false; // return false because driver was not found
        } catch (SQLException e) { // catches SQL errors like wrong URL, credentials, or server down
            System.err.println("Database connection failed: " + e.getMessage()); // prints the SQL error message
            return false; // return false because connection failed
        }
    } // end of connect method

    /**
     * Returns the current database connection.
     * @return Connection object, or null if not connected
     */
    public Connection getConnection() { // getter method to return the connection object
        return connection; // returns the current database connection (may be null)
    } // end of getConnection method

    /**
     * Checks if the database connection is active.
     * @return true if connected
     */
    public boolean isConnected() { // checks if the database connection is still active
        try { // try block in case isClosed() throws an exception
            return connection != null && !connection.isClosed(); // returns true if connection exists and is not closed
        } catch (SQLException e) { // catches any SQL exception from isClosed()
            return false; // returns false if an error occurs while checking
        }
    } // end of isConnected method

    /**
     * Creates the Competitors table if it does not already exist.
     * @return true if table creation was successful or table already exists
     */
    public boolean createTable() { // method to create the Competitors table in the database
        if (!isConnected()) return false; // if not connected to database, return false immediately
        // SQL statement to create Competitors table only if it doesn't already exist
        String sql = "CREATE TABLE IF NOT EXISTS Competitors ("
                + "CompetitorID INT PRIMARY KEY, " // CompetitorID is the primary key (unique identifier)
                + "FirstName VARCHAR(50) NOT NULL, " // first name column, max 50 chars, cannot be null
                + "MiddleName VARCHAR(50) DEFAULT '', " // middle name column, defaults to empty string
                + "LastName VARCHAR(50) NOT NULL, " // last name column, max 50 chars, cannot be null
                + "Level VARCHAR(20) NOT NULL, " // competition level column, cannot be null
                + "Country VARCHAR(50) DEFAULT '', " // country column, defaults to empty string
                + "Score1 INT DEFAULT 0, " // first score, defaults to 0
                + "Score2 INT DEFAULT 0, " // second score, defaults to 0
                + "Score3 INT DEFAULT 0, " // third score, defaults to 0
                + "Score4 INT DEFAULT 0, " // fourth score, defaults to 0
                + "Score5 INT DEFAULT 0" // fifth score, defaults to 0
                + ")"; // closing parenthesis of CREATE TABLE statement
        try (Statement stmt = connection.createStatement()) { // creates a Statement object, auto-closed after use
            stmt.execute(sql); // executes the CREATE TABLE SQL statement
            return true; // returns true if table was created successfully
        } catch (SQLException e) { // catches any SQL error during table creation
            System.err.println("Error creating table: " + e.getMessage()); // prints the error message
            return false; // returns false because table creation failed
        }
    } // end of createTable method

    /**
     * Inserts a competitor into the database.
     * @param c the competitor to insert
     * @return true if insertion was successful
     */
    public boolean insertCompetitor(SKMCompetitor c) { // method to insert a competitor into the database
        if (!isConnected()) return false; // if not connected, return false immediately
        // SQL INSERT statement with ? placeholders for prepared statement parameters
        String sql = "INSERT INTO Competitors "
                + "(CompetitorID, FirstName, MiddleName, LastName, Level, Country, "
                + "Score1, Score2, Score3, Score4, Score5) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // creates a PreparedStatement to safely insert data (prevents SQL injection)
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, c.getCompetitorId()); // sets parameter 1 to the competitor's ID
            pstmt.setString(2, c.getCompetitorName().getFirstName()); // sets parameter 2 to first name
            pstmt.setString(3, c.getCompetitorName().getMiddleName()); // sets parameter 3 to middle name
            pstmt.setString(4, c.getCompetitorName().getLastName()); // sets parameter 4 to last name
            pstmt.setString(5, c.getLevel()); // sets parameter 5 to the competition level
            pstmt.setString(6, c.getCountry()); // sets parameter 6 to the country
            int[] scores = c.getScoreArray(); // gets the array of 5 scores from the competitor
            for (int i = 0; i < 5; i++) { // loops through each of the 5 scores
                pstmt.setInt(7 + i, scores[i]); // sets parameters 7-11 to each score value
            }
            pstmt.executeUpdate(); // executes the INSERT statement to add the row to the database
            return true; // returns true because insertion was successful
        } catch (SQLException e) { // catches any SQL error during insertion
            System.err.println("Error inserting competitor: " + e.getMessage()); // prints error message
            return false; // returns false because insertion failed
        }
    } // end of insertCompetitor method

    /**
     * Retrieves all competitors from the database.
     * @return list of SKMCompetitor objects
     */
    public List<SKMCompetitor> getAllCompetitors() { // method to retrieve all competitors from the database
        List<SKMCompetitor> list = new ArrayList<>(); // creates an empty list to store competitors
        if (!isConnected()) return list; // if not connected, return the empty list
        String sql = "SELECT * FROM Competitors ORDER BY CompetitorID"; // SQL query to get all rows ordered by ID
        // creates Statement and executes query; both are auto-closed after the try block
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { // loops through each row in the result set
                SKMCompetitor c = resultSetToCompetitor(rs); // converts the current row to a competitor object
                list.add(c); // adds the competitor to the list
            }
        } catch (SQLException e) { // catches any SQL error during retrieval
            System.err.println("Error retrieving competitors: " + e.getMessage()); // prints error message
        }
        return list; // returns the list of all competitors
    } // end of getAllCompetitors method

    /**
     * Retrieves a single competitor by ID from the database.
     * @param id the competitor ID
     * @return SKMCompetitor object, or null if not found
     */
    public SKMCompetitor getCompetitorById(int id) { // method to find one competitor by their ID
        if (!isConnected()) return null; // if not connected, return null
        String sql = "SELECT * FROM Competitors WHERE CompetitorID = ?"; // SQL query with placeholder for ID
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) { // creates a prepared statement
            pstmt.setInt(1, id); // sets the first parameter to the competitor ID we are searching for
            try (ResultSet rs = pstmt.executeQuery()) { // executes the query and gets the result set
                if (rs.next()) { // checks if a matching row was found
                    return resultSetToCompetitor(rs); // converts the row to a competitor object and returns it
                }
            }
        } catch (SQLException e) { // catches any SQL error during retrieval
            System.err.println("Error retrieving competitor: " + e.getMessage()); // prints error message
        }
        return null; // returns null if no competitor was found with that ID
    } // end of getCompetitorById method

    /**
     * Deletes a competitor from the database by ID.
     * @param id the competitor ID
     * @return true if deletion was successful
     */
    public boolean deleteCompetitor(int id) { // method to delete a competitor by their ID
        if (!isConnected()) return false; // if not connected, return false
        String sql = "DELETE FROM Competitors WHERE CompetitorID = ?"; // SQL DELETE statement with placeholder
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) { // creates a prepared statement
            pstmt.setInt(1, id); // sets the parameter to the ID of the competitor to delete
            int rows = pstmt.executeUpdate(); // executes the DELETE and gets number of rows affected
            return rows > 0; // returns true if at least one row was deleted
        } catch (SQLException e) { // catches any SQL error during deletion
            System.err.println("Error deleting competitor: " + e.getMessage()); // prints error message
            return false; // returns false because deletion failed
        }
    } // end of deleteCompetitor method

    /**
     * Updates an existing competitor in the database.
     * @param c the competitor with updated data
     * @return true if update was successful
     */
    public boolean updateCompetitor(SKMCompetitor c) { // method to update an existing competitor's data
        if (!isConnected()) return false; // if not connected, return false
        // SQL UPDATE statement to modify all fields for a given CompetitorID
        String sql = "UPDATE Competitors SET FirstName=?, MiddleName=?, LastName=?, "
                + "Level=?, Country=?, Score1=?, Score2=?, Score3=?, Score4=?, Score5=? "
                + "WHERE CompetitorID=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) { // creates a prepared statement
            pstmt.setString(1, c.getCompetitorName().getFirstName()); // sets parameter 1 to first name
            pstmt.setString(2, c.getCompetitorName().getMiddleName()); // sets parameter 2 to middle name
            pstmt.setString(3, c.getCompetitorName().getLastName()); // sets parameter 3 to last name
            pstmt.setString(4, c.getLevel()); // sets parameter 4 to competition level
            pstmt.setString(5, c.getCountry()); // sets parameter 5 to country
            int[] scores = c.getScoreArray(); // gets the array of 5 scores
            for (int i = 0; i < 5; i++) { // loops through each of the 5 scores
                pstmt.setInt(6 + i, scores[i]); // sets parameters 6-10 to each score value
            }
            pstmt.setInt(11, c.getCompetitorId()); // sets parameter 11 to the competitor ID (WHERE clause)
            int rows = pstmt.executeUpdate(); // executes the UPDATE and gets number of rows affected
            return rows > 0; // returns true if at least one row was updated
        } catch (SQLException e) { // catches any SQL error during update
            System.err.println("Error updating competitor: " + e.getMessage()); // prints error message
            return false; // returns false because update failed
        }
    } // end of updateCompetitor method

    /**
     * Closes the database connection.
     */
    public void closeConnection() { // method to close the database connection
        if (connection != null) { // only attempt to close if connection exists
            try { // try block to handle potential errors during closing
                connection.close(); // closes the database connection
            } catch (SQLException e) { // catches any SQL error during close
                System.err.println("Error closing connection: " + e.getMessage()); // prints error message
            }
        }
    } // end of closeConnection method

    /**
     * Converts a ResultSet row to an SKMCompetitor object.
     */
    // private helper method that converts a database row (ResultSet) into an SKMCompetitor object
    private SKMCompetitor resultSetToCompetitor(ResultSet rs) throws SQLException {
        int id = rs.getInt("CompetitorID"); // reads the CompetitorID column from the current row
        String firstName = rs.getString("FirstName"); // reads the FirstName column
        String middleName = rs.getString("MiddleName"); // reads the MiddleName column
        String lastName = rs.getString("LastName"); // reads the LastName column
        String level = rs.getString("Level"); // reads the Level column
        String country = rs.getString("Country"); // reads the Country column
        int[] scores = new int[5]; // creates an array to hold 5 scores
        for (int i = 0; i < 5; i++) { // loops through scores 1 to 5
            scores[i] = rs.getInt("Score" + (i + 1)); // reads each Score column (Score1, Score2, etc.)
        }

        Name name; // declares a Name variable to hold the competitor's name
        if (middleName != null && !middleName.isEmpty()) { // checks if middle name exists
            name = new Name(firstName, middleName, lastName); // creates Name with first, middle, and last
        } else { // if no middle name
            name = new Name(firstName, lastName); // creates Name with just first and last
        }
        return new SKMCompetitor(id, name, level, country, scores); // creates and returns the competitor object
    } // end of resultSetToCompetitor method
} // end of DatabaseConnection class
