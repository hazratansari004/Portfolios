package quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class CompetitorList {
    private List<SKMCompetitor> competitors;
    private DatabaseConnection dbConnection;
    private boolean dbConnected;

    /**
     * Creates an empty CompetitorList with no active database connection.
     *
     * @since 1.0
     */
    public CompetitorList() {
        competitors = new ArrayList<>();
        dbConnection = null;
        dbConnected = false;
    }

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
    public boolean initDatabase() {
        dbConnection = new DatabaseConnection();
        if (dbConnection.connect()) {
            if (dbConnection.createTable()) {
                dbConnected = true;
                loadFromDatabase();
                return true;
            }
            dbConnection.closeConnection();
        }
        dbConnected = false;
        return false;
    }

    /**
     * Loads all competitors from the database into the in-memory list.
     * <p>
     * This helper is private because callers should use {@link #refreshFromDatabase}
     * when reloading is required from outside this class.
     * </p>
     *
     * @since 1.0
     */
    private void loadFromDatabase() {
        if (dbConnected) {
            List<SKMCompetitor> dbCompetitors = dbConnection.getAllCompetitors();
            competitors.clear();
            competitors.addAll(dbCompetitors);
        }
    }

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
    public void refreshFromDatabase() {
        loadFromDatabase();
    }

    /**
     * Returns whether this {@code CompetitorList} currently has an active
     * database connection.
     *
     * @return {@code true} if connected to the database; {@code false}
     *         otherwise.
     * @since 1.0
     */
    public boolean isDatabaseConnected() {
        return dbConnected;
    }

    /**
     * Adds a competitor to the in-memory list and persists it to the
     * database if a connection is active.
     *
     * @param c the competitor to add; must not be {@code null}
     * @since 1.0
     * @see DatabaseConnection#insertCompetitor(SKMCompetitor)
     */
    public void addCompetitor(SKMCompetitor c) {
        if (dbConnected) {
            dbConnection.insertCompetitor(c);
        }
        competitors.add(c);
    }

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
    public List<SKMCompetitor> getCompetitors() {
        return competitors;
    }

    /**
     * Looks up a competitor by their numeric identifier.
     *
     * @param id the competitor id to search for
     * @return the {@link SKMCompetitor} with the matching id, or {@code null}
     *         if no such competitor exists.
     * @since 1.0
     */
    public SKMCompetitor getCompetitorById(int id) {
        for (SKMCompetitor c : competitors) {
            if (c.getCompetitorId() == id) {
                return c;
            }
        }
        return null;
    }

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
    public boolean removeCompetitorById(int id) {
        for (int i = 0; i < competitors.size(); i++) {
            if (competitors.get(i).getCompetitorId() == id) {
                if (dbConnected) {
                    dbConnection.deleteCompetitor(id);
                }
                competitors.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the competitor with the highest overall score.
     *
     * @return the top-performing {@link SKMCompetitor}, or {@code null} if the
     *         list is empty.
     * @since 1.0
     */
    public SKMCompetitor getTopPerformer() {
        SKMCompetitor top = null;
        double best = -1;
        for (SKMCompetitor c : competitors) {
            if (c.getOverallScore() > best) {
                best = c.getOverallScore();
                top = c;
            }
        }
        return top;
    }

    /**
     * Returns the total number of competitors currently managed.
     *
     * @return the number of competitors.
     * @since 1.0
     */
    public int getTotalCompetitors() {
        return competitors.size();
    }

    /**
     * Builds a frequency map of all individual scores across all competitors.
     *
     * @return a {@link Map} where the keys are individual scores (for
     *         example 0-5) and values are the number of occurrences of that
     *         score across all competitors.
     * @since 1.0
     */
    public Map<Integer, Integer> getScoreFrequency() {
        Map<Integer, Integer> freq = new HashMap<>();
        for (SKMCompetitor c : competitors) {
            for (int s : c.getScoreArray()) {
                freq.put(s, freq.getOrDefault(s, 0) + 1);
            }
        }
        return freq;
    }

    /**
     * Generates a full text report of all competitors including a table of
     * competitors with details, the top performer, and statistical summaries.
     *
     * @return a formatted {@link String} containing the complete report.
     * @since 1.0
     */
    public String generateReport() {
        StringBuilder sb = new StringBuilder();

        // Competitor Table Header
        sb.append(String.format("%-15s %-20s %-15s %-12s %-20s %-10s%n",
                "Competitor ID", "Name", "Level", "Country", "Scores", "Overall"));
        sb.append("=".repeat(92)).append("\n");

        for (SKMCompetitor c : competitors) {
            sb.append(String.format("%-15d %-20s %-15s %-12s %-20s %-10.1f%n",
                    c.getCompetitorId(),
                    c.getCompetitorName().getFullName(),
                    c.getLevel(),
                    c.getCountry(),
                    c.getScoresString(),
                    c.getOverallScore()));
        }

        // Top performer
        SKMCompetitor top = getTopPerformer();
        if (top != null) {
            sb.append("\nTop Performer:\n");
            sb.append(top.getFullDetails()).append("\n");
        }

        // Statistics
        sb.append("\nStatistical Summary:\n");
        sb.append("Total number of competitors: ").append(getTotalCompetitors()).append("\n");
        if (top != null) {
            sb.append("Competitor with the highest score: ")
              .append(top.getCompetitorName().getFullName())
              .append(" with an overall score of ").append(top.getOverallScore()).append("\n");
        }

        // Frequency
        Map<Integer, Integer> freq = getScoreFrequency();
        sb.append("Frequency of individual scores:\n");
        sb.append("Score:     ");
        for (int i = 0; i <= 5; i++) {
            if (freq.containsKey(i)) {
                sb.append(String.format("%-6d", i));
            }
        }
        sb.append("\nFrequency: ");
        for (int i = 0; i <= 5; i++) {
            if (freq.containsKey(i)) {
                sb.append(String.format("%-6d", freq.get(i)));
            }
        }
        sb.append("\n");

        return sb.toString();
    }

    /**
     * Generates the next available competitor ID by finding the current
     * maximum ID and adding one. IDs start from 200.
     *
     * @return the next available competitor id.
     * @since 1.0
     */
    public int getNextId() {
        int maxId = 199;
        for (SKMCompetitor c : competitors) {
            if (c.getCompetitorId() > maxId) {
                maxId = c.getCompetitorId();
            }
        }
        return maxId + 1;
    }

    /**
     * Closes the database connection if one is currently open.
     *
     * @since 1.0
     */
    public void closeDatabase() {
        if (dbConnection != null) {
            dbConnection.closeConnection();
        }
    }
}
