package quiz; // Declares this class belongs to the 'quiz' package

/**
 * Represents a competitor in the quiz competition.
 * Stores competitor details including ID, name, level, country, and scores.
 */
public class SKMCompetitor { // Defines the SKMCompetitor class
    private int competitorId; // Stores the unique ID of the competitor
    private Name competitorName; // Stores the competitor's name as a Name object
    private String level;       // Stores the competitor's level: Beginner, Intermediate, or Advanced
    private String country;     // Stores the competitor's country (extra attribute)
    private int[] scores;       // Array of 5 scores (one per level)

    // Constructor that creates a competitor with ID, name, and level (no scores)
    public SKMCompetitor(int competitorId, Name competitorName, String level) {
        this.competitorId = competitorId; // Sets the competitor's ID
        this.competitorName = competitorName; // Sets the competitor's name
        this.level = level; // Sets the competitor's level
        this.country = ""; // Sets country to empty string by default
        this.scores = new int[5]; // Initialises scores array with 5 zeros
    }

    // Constructor that creates a competitor with ID, name, level, and scores
    public SKMCompetitor(int competitorId, Name competitorName, String level, int[] scores) {
        this.competitorId = competitorId; // Sets the competitor's ID
        this.competitorName = competitorName; // Sets the competitor's name
        this.level = level; // Sets the competitor's level
        this.country = ""; // Sets country to empty string by default
        setScores(scores); // Sets scores using the setter which validates input
    }

    // Constructor that creates a competitor with ID, name, level, and country
    public SKMCompetitor(int competitorId, Name competitorName, String level, String country) {
        this.competitorId = competitorId; // Sets the competitor's ID
        this.competitorName = competitorName; // Sets the competitor's name
        this.level = level; // Sets the competitor's level
        this.country = country; // Sets the competitor's country
        this.scores = new int[5]; // Initialises scores array with 5 zeros
    }

    // Constructor that creates a competitor with all fields: ID, name, level, country, and scores
    public SKMCompetitor(int competitorId, Name competitorName, String level, String country, int[] scores) {
        this.competitorId = competitorId; // Sets the competitor's ID
        this.competitorName = competitorName; // Sets the competitor's name
        this.level = level; // Sets the competitor's level
        this.country = country; // Sets the competitor's country
        setScores(scores); // Sets scores using the setter which validates input
    }

    // Getters and Setters
    public int getCompetitorId() { // Returns the competitor's ID
        return competitorId; // Returns the stored competitor ID value
    }

    public void setCompetitorId(int competitorId) { // Sets the competitor's ID to a new value
        this.competitorId = competitorId; // Updates the competitor ID field
    }

    public Name getCompetitorName() { // Returns the competitor's Name object
        return competitorName; // Returns the stored Name object
    }

    public void setCompetitorName(Name competitorName) { // Sets the competitor's name to a new Name object
        this.competitorName = competitorName; // Updates the competitor name field
    }

    public String getLevel() { // Returns the competitor's level
        return level; // Returns the stored level string
    }

    public void setLevel(String level) { // Sets the competitor's level to a new value
        this.level = level; // Updates the level field
    }

    public String getCountry() { // Returns the competitor's country
        return country; // Returns the stored country string
    }

    public void setCountry(String country) { // Sets the competitor's country to a new value
        this.country = country; // Updates the country field
    }

    public int[] getScoreArray() { // Returns a copy of the scores array
        return scores.clone(); // Returns a clone to prevent external modification of the original array
    }

    public void setScores(int[] scores) { // Sets all 5 scores at once with validation
        if (scores != null && scores.length == 5) { // Checks if scores is not null and has exactly 5 elements
            this.scores = scores.clone(); // Copies the input array to prevent external modification
        } else { // If input is invalid
            this.scores = new int[5]; // Initialises scores to an array of 5 zeros as fallback
        }
    }

    public void setScore(int index, int value) { // Sets a single score at a specific index
        if (index >= 0 && index < 5) { // Checks that the index is within valid range (0-4)
            scores[index] = value; // Updates the score at the given index
        }
    }

    /**
     * Calculates the overall score as the average of all 5 scores,
     * rounded to one decimal place.
     */
    public double getOverallScore() { // Calculates and returns the average of all scores
        int sum = 0; // Initialises sum to 0 to accumulate total of all scores
        for (int s : scores) { // Loops through each score in the scores array
            sum += s; // Adds the current score to the running total
        }
        // Divides sum by number of scores, rounds to 1 decimal place, and returns the result
        return Math.round((sum / (double) scores.length) * 10.0) / 10.0;
    }

    /**
     * Returns full details of the competitor as a formatted string.
     */
    public String getFullDetails() { // Builds and returns a detailed string about the competitor
        StringBuilder sb = new StringBuilder(); // Creates a StringBuilder to efficiently build the output string
        sb.append("Competitor number ").append(competitorId); // Adds competitor number to the string
        sb.append(", name ").append(competitorName.getFullName()); // Adds the competitor's full name
        if (!country.isEmpty()) { // Checks if the country field is not empty
            sb.append(", country ").append(country); // Adds the country if it exists
        }
        sb.append(".\n"); // Adds a period and newline to end the first line
        // Adds the competitor's first name, level, and a label for the scores
        sb.append(competitorName.getFirstName()).append(" is a ").append(level);
        sb.append(" and received these scores: "); // Adds the scores label text
        for (int i = 0; i < scores.length; i++) { // Loops through each score
            sb.append(scores[i]); // Adds the current score to the string
            if (i < scores.length - 1) sb.append(", "); // Adds a comma separator between scores (not after last)
        }
        // Adds the overall score at the end of the string
        sb.append(".\nThis gives them an overall score of ").append(getOverallScore()).append(".");
        return sb.toString(); // Converts the StringBuilder to a String and returns it
    }

    /**
     * Returns short details: competitor number, initials, and overall score.
     */
    public String getShortDetails() { // Returns a short summary string with ID, initials, and overall score
        // Builds and returns a short string with competitor number, initials, and overall score
        return "CN " + competitorId + " (" + competitorName.getInitials() + ") has an overall score of " + getOverallScore() + ".";
    }

    /** Returns scores as a formatted string for table display. */
    public String getScoresString() { // Returns all scores as a space-separated string
        StringBuilder sb = new StringBuilder(); // Creates a StringBuilder to build the scores string
        for (int i = 0; i < scores.length; i++) { // Loops through each score in the array
            sb.append(scores[i]); // Adds the current score to the string
            if (i < scores.length - 1) sb.append(" "); // Adds a space between scores (not after last)
        }
        return sb.toString(); // Converts the StringBuilder to a String and returns it
    }
} // End of SKMCompetitor class

