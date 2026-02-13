package quiz; // declares this class belongs to the 'quiz' package

import java.util.ArrayList; // import ArrayList class for storing competitors in a resizable list
import java.util.HashMap; // import HashMap class for creating key-value pairs (used for score frequency)
import java.util.List; // import List interface as the type for our competitor collection
import java.util.Map; // import Map interface as the type for score frequency mapping

/**
 * Manages a list of competitors and provides summary/statistics methods.
 * <p>
 * This class maintains an in-memory list of {@link SKMCompetitor} instances
 * and optionally integrates with a MySQL-backed {@link DatabaseConnection}.
 * When a database connection is initialized via {@link #initDatabase()},
 * competitors are loaded from the database and subsequently persisted when
 * new competitors are added or removed.
 * </p>
 *
 * @author Sailesh Kumar Mandal
 * @since 1.0
 * @see SKMCompetitor
 * @see DatabaseConnection
 */
public class CompetitorList { // defines the CompetitorList class which manages a collection of competitors
    private List<SKMCompetitor> competitors; // list to store all competitor objects in memory
    private DatabaseConnection dbConnection; // reference to the database connection object
    private boolean dbConnected; // flag to track whether the database is currently connected

    /**
     * Creates an empty CompetitorList with no active database connection.
     *
     * @since 1.0
     */
    public CompetitorList() { // constructor - creates a new empty CompetitorList
        competitors = new ArrayList<>(); // initialise the competitors list as an empty ArrayList
        dbConnection = null; // no database connection initially
        dbConnected = false; // database is not connected at start
    } // end of constructor

    /**
     * Initializes the database connection and loads existing competitors from
     * the persistent store into memory.
     * <p>
     * This method will attempt to connect to the database, create the
     * required table (if necessary) and then load the persisted competitors
     * into the internal list. If any step fails the connection will be
     * closed and this method will return {@code false}.
     * </p>
     *
     * @return {@code true} if the database connection was established, the
     *         table was created or already existed, and competitors were
     *         loaded; {@code false} otherwise.
     * @since 1.0
     * @see #loadFromDatabase()
     */
    public boolean initDatabase() { // method to set up the database connection and load data
        dbConnection = new DatabaseConnection(); // create a new DatabaseConnection object
        if (dbConnection.connect()) { // try to connect to the database; if successful...
            if (dbConnection.createTable()) { // try to create the table; if successful...
                dbConnected = true; // mark that the database is now connected
                loadFromDatabase(); // load all existing competitors from the database into memory
                return true; // return true indicating successful initialisation
            } // end of createTable check
            dbConnection.closeConnection(); // if table creation failed, close the connection
        } // end of connect check
        dbConnected = false; // mark that the database is not connected
        return false; // return false indicating initialisation failed
    } // end of initDatabase method

    /**
     * Loads all competitors from the database into the in-memory list.
     * <p>
     * This helper is private because callers should use {@link #refreshFromDatabase}
     * when reloading is required from outside this class.
     * </p>
     *
     * @since 1.0
     */
    private void loadFromDatabase() { // private helper method to load competitors from the database
        if (dbConnected) { // only proceed if the database is connected
            List<SKMCompetitor> dbCompetitors = dbConnection.getAllCompetitors(); // fetch all competitors from DB
            competitors.clear(); // remove all current competitors from the in-memory list
            competitors.addAll(dbCompetitors); // add all database competitors to the in-memory list
        } // end of dbConnected check
    } // end of loadFromDatabase method

    /**
     * Refreshes the internal in-memory list of competitors from the database.
     * <p>
     * This is a public wrapper around the private {@link #loadFromDatabase()}
     * method to allow external callers to request a refresh when a database
     * connection is active.
     * </p>
     *
     * @since 1.0
     */
    public void refreshFromDatabase() { // public method that allows external code to reload data from DB
        loadFromDatabase(); // delegates to the private loadFromDatabase method
    } // end of refreshFromDatabase method

    /**
     * Returns whether this {@code CompetitorList} currently has an active
     * database connection.
     *
     * @return {@code true} if connected to the database; {@code false}
     *         otherwise.
     * @since 1.0
     */
    public boolean isDatabaseConnected() { // getter method to check if database is connected
        return dbConnected; // returns true if connected, false otherwise
    } // end of isDatabaseConnected method

    /**
     * Adds a competitor to the in-memory list and persists it to the
     * database if a connection is active.
     *
     * @param c the competitor to add; must not be {@code null}
     * @since 1.0
     * @see DatabaseConnection#insertCompetitor(SKMCompetitor)
     */
    public void addCompetitor(SKMCompetitor c) { // method to add a new competitor
        if (dbConnected) { // if the database is connected...
            dbConnection.insertCompetitor(c); // save the competitor to the database
        } // end of dbConnected check
        competitors.add(c); // add the competitor to the in-memory list
    } // end of addCompetitor method

    /**
     * Returns the internal list of competitors.
     * <p>
     * The returned list is a live, modifiable reference to the internal
     * storage. Mutating this list will affect the state of this
     * {@code CompetitorList} instance.
     * </p>
     *
     * @return a {@link List} containing all {@link SKMCompetitor} instances
     *         currently managed by this object.
     * @since 1.0
     */
    public List<SKMCompetitor> getCompetitors() { // getter method that returns the list of all competitors
        return competitors; // returns the live reference to the internal competitors list
    } // end of getCompetitors method

    /**
     * Looks up a competitor by their numeric identifier.
     *
     * @param id the competitor id to search for
     * @return the {@link SKMCompetitor} with the matching id, or {@code null}
     *         if no such competitor exists.
     * @since 1.0
     */
    public SKMCompetitor getCompetitorById(int id) { // method to find a competitor by their ID number
        for (SKMCompetitor c : competitors) { // loop through each competitor in the list
            if (c.getCompetitorId() == id) { // if this competitor's ID matches the one we're looking for
                return c; // return the matching competitor
            } // end of ID check
        } // end of for loop
        return null; // return null if no competitor with that ID was found
    } // end of getCompetitorById method

    /**
     * Removes a competitor by their id.
     * <p>
     * If the database is connected, the competitor is also deleted from the
     * persistent store.
     * </p>
     *
     * @param id the id of the competitor to remove
     * @return {@code true} if a competitor with the given id was found and
     *         removed; {@code false} otherwise.
     * @since 1.0
     */
    public boolean removeCompetitorById(int id) { // method to remove a competitor by their ID
        for (int i = 0; i < competitors.size(); i++) { // loop through the list using an index
            if (competitors.get(i).getCompetitorId() == id) { // if the competitor at index i has the matching ID
                if (dbConnected) { // if database is connected...
                    dbConnection.deleteCompetitor(id); // delete the competitor from the database too
                } // end of dbConnected check
                competitors.remove(i); // remove the competitor from the in-memory list
                return true; // return true indicating successful removal
            } // end of ID check
        } // end of for loop
        return false; // return false if no competitor with that ID was found
    } // end of removeCompetitorById method

    /**
     * Returns the competitor with the highest overall score.
     *
     * @return the top-performing {@link SKMCompetitor}, or {@code null} if the
     *         list is empty.
     * @since 1.0
     */
    public SKMCompetitor getTopPerformer() { // method to find the competitor with the highest overall score
        SKMCompetitor top = null; // variable to hold the best competitor found so far
        double best = -1; // variable to track the highest score; starts at -1 so any score is higher
        for (SKMCompetitor c : competitors) { // loop through all competitors
            if (c.getOverallScore() > best) { // if this competitor's score is higher than the current best
                best = c.getOverallScore(); // update the best score
                top = c; // update the top competitor reference
            } // end of score comparison
        } // end of for loop
        return top; // return the competitor with the highest score (or null if list is empty)
    } // end of getTopPerformer method

    /**
     * Returns the total number of competitors currently managed.
     *
     * @return the number of competitors.
     * @since 1.0
     */
    public int getTotalCompetitors() { // method to get the total number of competitors
        return competitors.size(); // returns the size of the competitors list
    } // end of getTotalCompetitors method

    /**
     * Builds a frequency map of all individual scores across all competitors.
     *
     * @return a {@link Map} where the keys are individual scores (for
     *         example 0-5) and values are the number of occurrences of that
     *         score across all competitors.
     * @since 1.0
     */
    public Map<Integer, Integer> getScoreFrequency() { // method to count how often each score appears
        Map<Integer, Integer> freq = new HashMap<>(); // create a HashMap to store score -> count pairs
        for (SKMCompetitor c : competitors) { // loop through each competitor
            for (int s : c.getScoreArray()) { // loop through each individual score of this competitor
                freq.put(s, freq.getOrDefault(s, 0) + 1); // increment the count for this score (default 0 if new)
            } // end of inner for loop (scores)
        } // end of outer for loop (competitors)
        return freq; // return the completed frequency map
    } // end of getScoreFrequency method

    /**
     * Generates a human-readable text report of all competitors and
     * statistical summaries.
     *
     * @return a multi-line {@link String} containing the competitor table,
     *         top performer summary and statistical information.
     * @since 1.0
     */
    public String generateReport() { // method to create a full text report of all competitors and stats
        StringBuilder sb = new StringBuilder(); // create a StringBuilder to efficiently build the report string

        // Competitor Table Header
        // format the table header with column names, each with a fixed width
        sb.append(String.format("%-15s %-20s %-15s %-12s %-20s %-10s%n",
                "Competitor ID", "Name", "Level", "Country", "Scores", "Overall"));
        sb.append("=".repeat(92)).append("\n"); // add a line of 92 '=' characters as a separator

        for (SKMCompetitor c : competitors) { // loop through each competitor to add their row
            // format each competitor's data into a table row with fixed-width columns
            sb.append(String.format("%-15d %-20s %-15s %-12s %-20s %-10.1f%n",
                    c.getCompetitorId(), // competitor's unique ID number
                    c.getCompetitorName().getFullName(), // competitor's full name
                    c.getLevel(), // competitor's level (e.g. beginner, intermediate)
                    c.getCountry(), // competitor's country
                    c.getScoresString(), // competitor's scores as a formatted string
                    c.getOverallScore())); // competitor's calculated overall score
        } // end of competitor table loop

        // Top performer
        SKMCompetitor top = getTopPerformer(); // get the competitor with the highest score
        if (top != null) { // if there is at least one competitor
            sb.append("\nTop Performer:\n"); // add a section heading for the top performer
            sb.append(top.getFullDetails()).append("\n"); // add the full details of the top performer
        } // end of top performer check

        // Statistics
        sb.append("\nStatistical Summary:\n"); // add a section heading for statistics
        // append the total number of competitors
        sb.append("Total number of competitors: ").append(getTotalCompetitors()).append("\n");
        if (top != null) { // if there is a top performer to display
            // append the name and score of the highest-scoring competitor
            sb.append("Competitor with the highest score: ")
              .append(top.getCompetitorName().getFullName())
              .append(" with an overall score of ").append(top.getOverallScore()).append("\n");
        } // end of top performer stats check

        // Frequency
        Map<Integer, Integer> freq = getScoreFrequency(); // get the frequency map of all scores
        sb.append("Frequency of individual scores:\n"); // add a heading for the frequency table
        sb.append("Score:     "); // start the score labels row
        for (int i = 0; i <= 5; i++) { // loop through possible scores 0 to 5
            if (freq.containsKey(i)) { // only include scores that actually appear
                sb.append(String.format("%-6d", i)); // append the score number with fixed width
            } // end of containsKey check
        } // end of score labels loop
        sb.append("\nFrequency: "); // start the frequency values row
        for (int i = 0; i <= 5; i++) { // loop through possible scores 0 to 5 again
            if (freq.containsKey(i)) { // only include scores that actually appear
                sb.append(String.format("%-6d", freq.get(i))); // append the frequency count with fixed width
            } // end of containsKey check
        } // end of frequency values loop
        sb.append("\n"); // add a final newline

        return sb.toString(); // convert the StringBuilder to a String and return the complete report
    } // end of generateReport method

    /**
     * Generates the next available competitor ID.
     * <p>
     * The method scans existing competitor ids and returns one greater than
     * the current maximum. The initial base is 199, so the first generated
     * id when the list is empty will be 200.
     * </p>
     *
     * @return the next numeric competitor id to use.
     * @since 1.0
     */
    public int getNextId() { // method to generate the next available competitor ID
        int maxId = 199; // start with base ID 199 so first generated ID will be 200
        for (SKMCompetitor c : competitors) { // loop through all competitors
            if (c.getCompetitorId() > maxId) { // if this competitor's ID is higher than current max
                maxId = c.getCompetitorId(); // update maxId to this competitor's ID
            } // end of ID comparison
        } // end of for loop
        return maxId + 1; // return one more than the highest existing ID
    } // end of getNextId method

    /**
     * Closes the currently open database connection, if any.
     * <p>
     * This method is safe to call even if no connection exists.
     * </p>
     *
     * @since 1.0
     */
    public void closeDatabase() { // method to safely close the database connection
        if (dbConnection != null) { // only attempt to close if a connection object exists
            dbConnection.closeConnection(); // close the database connection
        } // end of null check
    } // end of closeDatabase method
} // end of CompetitorList class
