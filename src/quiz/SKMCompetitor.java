package quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a competitor in the quiz competition.
 * Stores competitor details including ID, name, level, country, and scores.
 * Includes methods to read from and write to the MySQL database using JDBC.
 *
 * @author Sailesh Kumar Mandal
 */
public class SKMCompetitor {
    private int competitorId;
    private Name competitorName;
    private String level;       // Beginner, Intermediate, Advanced
    private String country;     // Extra attribute
    private int[] scores;       // Array of 5 scores (one per level)

    public SKMCompetitor(int competitorId, Name competitorName, String level) {
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.level = level;
        this.country = "";
        this.scores = new int[5];
    }

    public SKMCompetitor(int competitorId, Name competitorName, String level, int[] scores) {
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.level = level;
        this.country = "";
        setScores(scores);
    }

    public SKMCompetitor(int competitorId, Name competitorName, String level, String country) {
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.level = level;
        this.country = country;
        this.scores = new int[5];
    }

    public SKMCompetitor(int competitorId, Name competitorName, String level, String country, int[] scores) {
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.level = level;
        this.country = country;
        setScores(scores);
    }

    // Getters and Setters
    public int getCompetitorId() {
        return competitorId;
    }

    public void setCompetitorId(int competitorId) {
        this.competitorId = competitorId;
    }

    public Name getCompetitorName() {
        return competitorName;
    }

    public void setCompetitorName(Name competitorName) {
        this.competitorName = competitorName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int[] getScoreArray() {
        return scores.clone();
    }

    public void setScores(int[] scores) {
        if (scores != null && scores.length == 5) {
            this.scores = scores.clone();
        } else {
            this.scores = new int[5];
        }
    }

    public void setScore(int index, int value) {
        if (index >= 0 && index < 5) {
            scores[index] = value;
        }
    }

    /**
     * Calculates the overall score as the average of all 5 scores,
     * rounded to one decimal place.
     */
    public double getOverallScore() {
        int sum = 0;
        for (int s : scores) {
            sum += s;
        }
        return Math.round((sum / (double) scores.length) * 10.0) / 10.0;
    }

    /**
     * Returns full details of the competitor as a formatted string.
     */
    public String getFullDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Competitor number ").append(competitorId);
        sb.append(", name ").append(competitorName.getFullName());
        if (!country.isEmpty()) {
            sb.append(", country ").append(country);
        }
        sb.append(".\n");
        sb.append(competitorName.getFirstName()).append(" is a ").append(level);
        sb.append(" and received these scores: ");
        for (int i = 0; i < scores.length; i++) {
            sb.append(scores[i]);
            if (i < scores.length - 1) sb.append(", ");
        }
        sb.append(".\nThis gives them an overall score of ").append(getOverallScore()).append(".");
        return sb.toString();
    }

    /**
     * Returns short details: competitor number, initials, and overall score.
     */
    public String getShortDetails() {
        return "CN " + competitorId + " (" + competitorName.getInitials() + ") has an overall score of " + getOverallScore() + ".";
    }

    /** Returns scores as a formatted string for table display. */
    public String getScoresString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scores.length; i++) {
            sb.append(scores[i]);
            if (i < scores.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    // ==================== DATABASE METHODS (JDBC) ====================

    /**
     * Saves this competitor to the MySQL database.
     * @param conn an active JDBC Connection to CompetitionDB
     * @return true if the insert was successful, false otherwise
     */
    public boolean saveToDatabase(Connection conn) {
        if (conn == null) return false;
        String sql = "INSERT INTO Competitors "
                + "(CompetitorID, FirstName, MiddleName, LastName, Level, Country, "
                + "Score1, Score2, Score3, Score4, Score5) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, competitorId);
            pstmt.setString(2, competitorName.getFirstName());
            pstmt.setString(3, competitorName.getMiddleName());
            pstmt.setString(4, competitorName.getLastName());
            pstmt.setString(5, level);
            pstmt.setString(6, country);
            for (int i = 0; i < 5; i++) {
                pstmt.setInt(7 + i, scores[i]);
            }
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving competitor to database: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates this competitor's data in the MySQL database.
     * @param conn an active JDBC Connection to CompetitionDB
     * @return true if the update was successful, false otherwise
     */
    public boolean updateInDatabase(Connection conn) {
        if (conn == null) return false;
        String sql = "UPDATE Competitors SET FirstName=?, MiddleName=?, LastName=?, "
                + "Level=?, Country=?, Score1=?, Score2=?, Score3=?, Score4=?, Score5=? "
                + "WHERE CompetitorID=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, competitorName.getFirstName());
            pstmt.setString(2, competitorName.getMiddleName());
            pstmt.setString(3, competitorName.getLastName());
            pstmt.setString(4, level);
            pstmt.setString(5, country);
            for (int i = 0; i < 5; i++) {
                pstmt.setInt(6 + i, scores[i]);
            }
            pstmt.setInt(11, competitorId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating competitor in database: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes this competitor from the MySQL database.
     * @param conn an active JDBC Connection to CompetitionDB
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteFromDatabase(Connection conn) {
        if (conn == null) return false;
        String sql = "DELETE FROM Competitors WHERE CompetitorID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, competitorId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting competitor from database: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads a single competitor from the MySQL database by ID.
     * @param conn an active JDBC Connection to CompetitionDB
     * @param id the CompetitorID to look up
     * @return the SKMCompetitor object, or null if not found
     */
    public static SKMCompetitor readFromDatabase(Connection conn, int id) {
        if (conn == null) return null;
        String sql = "SELECT * FROM Competitors WHERE CompetitorID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return fromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading competitor from database: " + e.getMessage());
        }
        return null;
    }

    /**
     * Reads all competitors from the MySQL database.
     * @param conn an active JDBC Connection to CompetitionDB
     * @return a list of all SKMCompetitor objects in the database
     */
    public static List<SKMCompetitor> readAllFromDatabase(Connection conn) {
        List<SKMCompetitor> list = new ArrayList<>();
        if (conn == null) return list;
        String sql = "SELECT * FROM Competitors ORDER BY CompetitorID";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error reading competitors from database: " + e.getMessage());
        }
        return list;
    }

    /**
     * Helper method to create an SKMCompetitor from a database ResultSet row.
     * @param rs the ResultSet positioned at a valid row
     * @return an SKMCompetitor populated from the row data
     */
    private static SKMCompetitor fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("CompetitorID");
        String firstName = rs.getString("FirstName");
        String middleName = rs.getString("MiddleName");
        String lastName = rs.getString("LastName");
        String lvl = rs.getString("Level");
        String ctry = rs.getString("Country");
        int[] sc = new int[5];
        for (int i = 0; i < 5; i++) {
            sc[i] = rs.getInt("Score" + (i + 1));
        }
        Name name;
        if (middleName != null && !middleName.isEmpty()) {
            name = new Name(firstName, middleName, lastName);
        } else {
            name = new Name(firstName, lastName);
        }
        return new SKMCompetitor(id, name, lvl, ctry, sc);
    }
}
