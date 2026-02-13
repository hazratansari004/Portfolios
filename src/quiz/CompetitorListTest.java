package quiz;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class CompetitorListTest {

    // ======================== Name Class Tests ========================

    @Test
    public void testNameTwoParts() {
        Name name = new Name("Alice", "Green");
        assertEquals("Alice Green", name.getFullName());
        assertEquals("AG", name.getInitials());
        assertEquals("Alice Green", name.toString());
    }

    @Test
    public void testNameThreeParts() {
        Name name = new Name("Keith", "John", "Talbot");
        assertEquals("Keith John Talbot", name.getFullName());
        assertEquals("KJT", name.getInitials());
    }

    @Test
    public void testNameSetters() {
        Name name = new Name("Alice", "Green");
        name.setFirstName("Bob");
        assertEquals("Bob", name.getFirstName());
        name.setMiddleName("James");
        assertEquals("James", name.getMiddleName());
        name.setLastName("Smith");
        assertEquals("Smith", name.getLastName());
        assertEquals("Bob James Smith", name.getFullName());
        assertEquals("BJS", name.getInitials());
    }

    // ======================== SKMCompetitor Class Tests ========================

    @Test
    public void testCompetitorBasicConstructor() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        assertEquals(200, c.getCompetitorId());
        assertEquals("Beginner", c.getLevel());
        assertEquals("Alice Green", c.getCompetitorName().getFullName());
        assertEquals("", c.getCountry());
    }

    @Test
    public void testCompetitorWithCountry() {
        SKMCompetitor c = new SKMCompetitor(210, new Name("Alice", "Green"), "Advanced", "Nepal");
        assertEquals("Nepal", c.getCountry());
        c.setCountry("UK");
        assertEquals("UK", c.getCountry());
    }

    @Test
    public void testCompetitorWithScores() {
        int[] scores = {4, 3, 5, 2, 4};
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", scores);
        int[] retrieved = c.getScoreArray();
        assertEquals(5, retrieved.length);
        assertEquals(4, retrieved[0]);
        assertEquals(3, retrieved[1]);
        assertEquals(5, retrieved[2]);
        assertEquals(2, retrieved[3]);
        assertEquals(4, retrieved[4]);
    }

    @Test
    public void testOverallScore() {
        // Average of 4,3,5,2,4 = 18/5 = 3.6
        int[] scores = {4, 3, 5, 2, 4};
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", scores);
        assertEquals(3.6, c.getOverallScore());
    }

    @Test
    public void testOverallScoreWithZero() {
        // Average of 0,4,4,4,4 = 16/5 = 3.2
        int[] scores = {0, 4, 4, 4, 4};
        SKMCompetitor c = new SKMCompetitor(300, new Name("Test", "User"), "Beginner", scores);
        assertEquals(3.2, c.getOverallScore());
    }

    @Test
    public void testFullDetails() {
        int[] scores = {4, 3, 5, 2, 4};
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", scores);
        String full = c.getFullDetails();
        assertTrue(full.contains("200"));
        assertTrue(full.contains("Alice Green"));
        assertTrue(full.contains("Beginner"));
        assertTrue(full.contains("3.6"));
        assertTrue(full.contains("This gives them"));
    }

    @Test
    public void testFullDetailsWithCountry() {
        int[] scores = {3, 4, 3, 4, 3};
        SKMCompetitor c = new SKMCompetitor(205, new Name("Bob", "Smith"), "Intermediate", "Nepal", scores);
        String full = c.getFullDetails();
        assertTrue(full.contains("Nepal"));
        assertTrue(full.contains("Bob Smith"));
    }

    @Test
    public void testShortDetails() {
        int[] scores = {4, 3, 5, 2, 4};
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", scores);
        assertEquals("CN 200 (AG) has an overall score of 3.6.", c.getShortDetails());
    }

    @Test
    public void testScoresString() {
        int[] scores = {4, 3, 5, 2, 4};
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", scores);
        assertEquals("4 3 5 2 4", c.getScoresString());
    }

    @Test
    public void testCompetitorSetters() {
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        c.setCompetitorId(201);
        assertEquals(201, c.getCompetitorId());
        c.setLevel("Advanced");
        assertEquals("Advanced", c.getLevel());
    }

    // ======================== CompetitorList Class Tests ========================

    @Test
    public void testEmptyList() {
        CompetitorList list = new CompetitorList();
        assertEquals(0, list.getTotalCompetitors());
        assertEquals(200, list.getNextId());
        assertNull(list.getTopPerformer());
    }

    @Test
    public void testAddAndRetrieve() {
        CompetitorList list = new CompetitorList();
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[]{4, 3, 5, 2, 4});
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[]{3, 4, 4, 5, 4});

        list.addCompetitor(c1);
        list.addCompetitor(c2);

        assertEquals(2, list.getTotalCompetitors());
        assertSame(c2, list.getCompetitorById(201));
        assertNull(list.getCompetitorById(999));
        assertEquals(202, list.getNextId());
    }

    @Test
    public void testRemoveCompetitor() {
        CompetitorList list = new CompetitorList();
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[]{4, 3, 5, 2, 4});
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[]{3, 4, 4, 5, 4});
        list.addCompetitor(c1);
        list.addCompetitor(c2);

        assertTrue(list.removeCompetitorById(201));
        assertEquals(1, list.getTotalCompetitors());
        assertNull(list.getCompetitorById(201));
        assertFalse(list.removeCompetitorById(999));
    }

    @Test
    public void testTopPerformer() {
        CompetitorList list = new CompetitorList();
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[]{4, 3, 5, 2, 4});
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[]{3, 4, 4, 5, 4});
        SKMCompetitor c3 = new SKMCompetitor(202, new Name("Carol", "White"), "Advanced", new int[]{5, 5, 4, 4, 5});

        list.addCompetitor(c1);
        list.addCompetitor(c2);
        list.addCompetitor(c3);

        SKMCompetitor top = list.getTopPerformer();
        assertNotNull(top);
        assertEquals(202, top.getCompetitorId());
        assertEquals(4.6, top.getOverallScore(), 0.0001);
    }

    @Test
    public void testScoreFrequency() {
        CompetitorList list = new CompetitorList();
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[]{4, 3, 5, 2, 4});
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[]{3, 4, 4, 5, 4});
        SKMCompetitor c3 = new SKMCompetitor(202, new Name("Carol", "White"), "Advanced", new int[]{5, 5, 4, 4, 5});

        list.addCompetitor(c1);
        list.addCompetitor(c2);
        list.addCompetitor(c3);

        Map<Integer, Integer> freq = list.getScoreFrequency();
        assertEquals(1, freq.get(2));
        assertEquals(2, freq.get(3));
        assertEquals(7, freq.get(4));
        assertEquals(5, freq.get(5));
    }

    @Test
    public void testGenerateReport() {
        CompetitorList list = new CompetitorList();
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[]{4, 3, 5, 2, 4});
        list.addCompetitor(c1);

        String report = list.generateReport();
        assertTrue(report.contains("Competitor ID"));
        assertTrue(report.contains("Alice Green"));
        assertTrue(report.contains("Top Performer"));
        assertTrue(report.contains("Statistical Summary"));
        assertTrue(report.contains("Total number of competitors: 1"));
    }

    // ======================== QuizData Class Tests ========================

    @Test
    public void testQuizDataQuestionCount() {
        assertEquals(5, QuizData.getQuestionsForLevel(1).size());
        assertEquals(5, QuizData.getQuestionsForLevel(2).size());
        assertEquals(5, QuizData.getQuestionsForLevel(3).size());
        assertEquals(5, QuizData.getQuestionsForLevel(4).size());
        assertEquals(5, QuizData.getQuestionsForLevel(5).size());
    }

    @Test
    public void testQuizDataAttemptNames() {
        assertEquals("Attempt 1", QuizData.getLevelName(1));
        assertEquals("Attempt 3", QuizData.getLevelName(3));
        assertEquals("Attempt 5", QuizData.getLevelName(5));
    }

    @Test
    public void testQuizDataQuestionStructure() {
        QuizQuestion q = QuizData.getQuestionsForLevel(1).get(0);
        assertNotNull(q.getQuestion());
        assertFalse(q.getQuestion().isEmpty());
        String[] options = q.getOptions();
        assertEquals(4, options.length);
        assertTrue(q.getCorrectAnswerIndex() >= 0 && q.getCorrectAnswerIndex() <= 3);
    }
}