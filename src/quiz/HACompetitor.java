package quiz;

/**
 * Represents a competitor in the quiz competition.
 * Stores competitor details including ID, name, level, age, and scores.
 */
public class HACompetitor {
    private int competitorId;
    private Name competitorName;
    private String level;       // Beginner, Intermediate, Advanced
    private int age;            // Extra attribute
    private int[] scores;       // Array of 5 scores (one per level)

    public HACompetitor(int competitorId, Name competitorName, String level, int age) {
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.level = level;
        this.age = age;
        this.scores = new int[5];
    }

    public HACompetitor(int competitorId, Name competitorName, String level, int age, int[] scores) {
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.level = level;
        this.age = age;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
        int count = 0;
        for (int s : scores) {
            if (s > 0) {
                sum += s;
                count++;
            }
        }
        if (count == 0) return 0.0;
        return Math.round((sum / (double) count) * 10.0) / 10.0;
    }

    /**
     * Returns full details of the competitor as a formatted string.
     */
    public String getFullDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Competitor number ").append(competitorId);
        sb.append(", name ").append(competitorName.getFullName());
        sb.append(", age ").append(age).append(".\n");
        sb.append(competitorName.getFirstName()).append(" is a ").append(level);
        sb.append(" and received these scores: ");
        for (int i = 0; i < scores.length; i++) {
            sb.append(scores[i]);
            if (i < scores.length - 1) sb.append(", ");
        }
        sb.append(".\nThis gives ");
        sb.append(competitorName.getFirstName().toLowerCase().endsWith("a") ? "her" : "him");
        sb.append(" an overall score of ").append(getOverallScore()).append(".");
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
