package quiz;

/**
 * Represents a competitor in the quiz competition.
 * Stores competitor details including ID, name, level, country, and scores.
 */
public class HACompetitor {
    private int competitorId;
    private Name competitorName;
    private String level;       // Beginner, Intermediate, Advanced
    private String country;     // Extra attribute
    private int[] scores;       // Array of 5 scores (one per level)

    public HACompetitor(int competitorId, Name competitorName, String level) {
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.level = level;
        this.country = "";
        this.scores = new int[5];
    }

    public HACompetitor(int competitorId, Name competitorName, String level, int[] scores) {
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.level = level;
        this.country = "";
        setScores(scores);
    }

    public HACompetitor(int competitorId, Name competitorName, String level, String country) {
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.level = level;
        this.country = country;
        this.scores = new int[5];
    }

    public HACompetitor(int competitorId, Name competitorName, String level, String country, int[] scores) {
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
}
