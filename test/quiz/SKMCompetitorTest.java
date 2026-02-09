package quiz;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 tests for the SKMCompetitor class.
 *
 * @author Sailesh Kumar Mandal
 */
class SKMCompetitorTest {

    @Test
    void testGetCompetitorId() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        assertEquals(200, c.getCompetitorId(),
                "Competitor ID should be 200");
    }

    @Test
    void testGetLevel() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        assertEquals("Beginner", c.getLevel(),
                "Level should be 'Beginner'");
    }

    @Test
    void testGetName() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        assertEquals("Alice Green", c.getCompetitorName().getFullName(),
                "Name should be 'Alice Green'");
    }

    @Test
    void testDefaultCountryIsEmpty() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        assertEquals("", c.getCountry(),
                "Default country should be empty string");
    }

    @Test
    void testCountryConstructor() {
        SKMCompetitor c = new SKMCompetitor(210, new Name("Bob", "Smith"), "Advanced", "Nepal");
        assertEquals("Nepal", c.getCountry(),
                "Country should be 'Nepal' when set via constructor");
    }

    @Test
    void testSetCountry() {
        SKMCompetitor c = new SKMCompetitor(210, new Name("Bob", "Smith"), "Advanced", "Nepal");
        c.setCountry("UK");
        assertEquals("UK", c.getCountry(),
                "Country should update to 'UK' after setCountry");
    }

    @Test
    void testSetScoresAndGetScoreArray() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        int[] scores = {4, 3, 5, 2, 4};
        c.setScores(scores);
        int[] retrieved = c.getScoreArray();
        assertEquals(5, retrieved.length,
                "Score array should have length 5");
        assertEquals(4, retrieved[0], "Score at index 0 should be 4");
        assertEquals(3, retrieved[1], "Score at index 1 should be 3");
        assertEquals(5, retrieved[2], "Score at index 2 should be 5");
    }

    @Test
    void testOverallScoreCalculation() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner",
                new int[]{4, 3, 5, 2, 4});
        // Average of 4,3,5,2,4 = 18/5 = 3.6
        assertEquals(3.6, c.getOverallScore(), 0.0001,
                "Overall score should be 3.6 (average of 4,3,5,2,4)");
    }

    @Test
    void testOverallScoreWithZero() {
        SKMCompetitor c = new SKMCompetitor(300, new Name("Test", "User"), "Beginner",
                new int[]{0, 4, 4, 4, 4});
        // Average of 0,4,4,4,4 = 16/5 = 3.2
        assertEquals(3.2, c.getOverallScore(), 0.0001,
                "Overall score should be 3.2 when one score is zero");
    }

    @Test
    void testFullDetailsContainsId() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner",
                new int[]{4, 3, 5, 2, 4});
        assertTrue(c.getFullDetails().contains("200"),
                "Full details should contain competitor ID '200'");
    }

    @Test
    void testFullDetailsContainsName() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner",
                new int[]{4, 3, 5, 2, 4});
        assertTrue(c.getFullDetails().contains("Alice Green"),
                "Full details should contain name 'Alice Green'");
    }

    @Test
    void testFullDetailsContainsLevel() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner",
                new int[]{4, 3, 5, 2, 4});
        assertTrue(c.getFullDetails().contains("Beginner"),
                "Full details should contain level 'Beginner'");
    }

    @Test
    void testFullDetailsContainsScore() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner",
                new int[]{4, 3, 5, 2, 4});
        assertTrue(c.getFullDetails().contains("3.6"),
                "Full details should contain overall score '3.6'");
    }

    @Test
    void testFullDetailsContainsCountry() {
        SKMCompetitor c = new SKMCompetitor(205, new Name("Bob", "Smith"), "Intermediate",
                "Nepal", new int[]{3, 4, 3, 4, 3});
        assertTrue(c.getFullDetails().contains("Nepal"),
                "Full details should contain country 'Nepal'");
    }

    @Test
    void testShortDetailsFormat() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner",
                new int[]{4, 3, 5, 2, 4});
        assertEquals("CN 200 (AG) has an overall score of 3.6.",
                c.getShortDetails(),
                "Short details should follow exact format");
    }

    @Test
    void testFullDetailsGenderNeutral() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner",
                new int[]{4, 3, 5, 2, 4});
        assertTrue(c.getFullDetails().contains("This gives them"),
                "Full details should use gender-neutral pronoun 'them'");
    }

    @Test
    void testGetScoresString() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner",
                new int[]{4, 3, 5, 2, 4});
        assertEquals("4 3 5 2 4", c.getScoresString(),
                "Scores string should be space-separated");
    }

    @Test
    void testSetCompetitorId() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        c.setCompetitorId(201);
        assertEquals(201, c.getCompetitorId(),
                "Competitor ID should update to 201 after setCompetitorId");
    }

    @Test
    void testSetLevel() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        c.setLevel("Advanced");
        assertEquals("Advanced", c.getLevel(),
                "Level should update to 'Advanced' after setLevel");
    }

    @Test
    void testSetScore() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner",
                new int[]{1, 1, 1, 1, 1});
        c.setScore(2, 5);
        assertEquals(5, c.getScoreArray()[2],
                "Score at index 2 should be 5 after setScore(2, 5)");
    }
}
