package quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages a list of competitors and provides summary/statistics methods.
 */
public class CompetitorList {
    private List<HACompetitor> competitors;

    public CompetitorList() {
        competitors = new ArrayList<>();
    }

    public void addCompetitor(HACompetitor c) {
        competitors.add(c);
    }

    public List<HACompetitor> getCompetitors() {
        return competitors;
    }

    public HACompetitor getCompetitorById(int id) {
        for (HACompetitor c : competitors) {
            if (c.getCompetitorId() == id) {
                return c;
            }
        }
        return null;
    }

    /** Returns the competitor with the highest overall score. */
    public HACompetitor getTopPerformer() {
        HACompetitor top = null;
        double best = -1;
        for (HACompetitor c : competitors) {
            if (c.getOverallScore() > best) {
                best = c.getOverallScore();
                top = c;
            }
        }
        return top;
    }

    /** Returns total number of competitors. */
    public int getTotalCompetitors() {
        return competitors.size();
    }

    /** Returns a frequency map of all individual scores. */
    public Map<Integer, Integer> getScoreFrequency() {
        Map<Integer, Integer> freq = new HashMap<>();
        for (HACompetitor c : competitors) {
            for (int s : c.getScoreArray()) {
                freq.put(s, freq.getOrDefault(s, 0) + 1);
            }
        }
        return freq;
    }

    /** Generates a full text report of all competitors. */
    public String generateReport() {
        StringBuilder sb = new StringBuilder();

        // Competitor Table Header
        sb.append(String.format("%-15s %-20s %-15s %-12s %-20s %-10s%n",
                "Competitor ID", "Name", "Level", "Country", "Scores", "Overall"));
        sb.append("=".repeat(92)).append("\n");

        for (HACompetitor c : competitors) {
            sb.append(String.format("%-15d %-20s %-15s %-12s %-20s %-10.1f%n",
                    c.getCompetitorId(),
                    c.getCompetitorName().getFullName(),
                    c.getLevel(),
                    c.getCountry(),
                    c.getScoresString(),
                    c.getOverallScore()));
        }

        // Top performer
        HACompetitor top = getTopPerformer();
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

    /** Generates the next available competitor ID. */
    public int getNextId() {
        int maxId = 199;
        for (HACompetitor c : competitors) {
            if (c.getCompetitorId() > maxId) {
                maxId = c.getCompetitorId();
            }
        }
        return maxId + 1;
    }
}
