package quiz; // declares this class belongs to the quiz package

import static org.junit.jupiter.api.Assertions.*; // imports all assertion methods like assertEquals, assertTrue, etc.

import java.util.Map; // imports the Map interface used for score frequency results

import org.junit.jupiter.api.Test; // imports the @Test annotation to mark test methods

// defines the test class for testing Name, SKMCompetitor, CompetitorList, and QuizData
public class CompetitorListTest {

    // ======================== Name Class Tests ========================

    @Test // marks this method as a test case
    // Tests that a Name with two parts (first and last) returns correct full name, initials, and toString
    public void testNameTwoParts() {
        Name name = new Name("Alice", "Green"); // creates a Name object with first name "Alice" and last name "Green"
        assertEquals("Alice Green", name.getFullName()); // checks that the full name is "Alice Green"
        assertEquals("AG", name.getInitials()); // checks that the initials are "AG"
        assertEquals("Alice Green", name.toString()); // checks that toString also returns "Alice Green"
    }

    @Test // marks this method as a test case
    // Tests that a Name with three parts (first, middle, last) returns correct full name and initials
    public void testNameThreeParts() {
        Name name = new Name("Keith", "John", "Talbot"); // creates a Name with first, middle, and last name
        assertEquals("Keith John Talbot", name.getFullName()); // checks that the full name includes all three parts
        assertEquals("KJT", name.getInitials()); // checks that the initials are "KJT"
    }

    @Test // marks this method as a test case
    // Tests that the setter methods for first, middle, and last name work correctly
    public void testNameSetters() {
        Name name = new Name("Alice", "Green"); // creates a Name with first and last name
        name.setFirstName("Bob"); // changes the first name to "Bob"
        assertEquals("Bob", name.getFirstName()); // checks that the first name was updated to "Bob"
        name.setMiddleName("James"); // sets the middle name to "James"
        assertEquals("James", name.getMiddleName()); // checks that the middle name was set to "James"
        name.setLastName("Smith"); // changes the last name to "Smith"
        assertEquals("Smith", name.getLastName()); // checks that the last name was updated to "Smith"
        assertEquals("Bob James Smith", name.getFullName()); // checks the full name reflects all changes
        assertEquals("BJS", name.getInitials()); // checks the initials reflect the updated names
    }

    // ======================== SKMCompetitor Class Tests ========================

    @Test // marks this method as a test case
    // Tests creating a competitor with the basic constructor (id, name, level) and verifying default values
    public void testCompetitorBasicConstructor() {
        // creates a competitor with id 200, name "Alice Green", and level "Beginner"
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        assertEquals(200, c.getCompetitorId()); // checks that the competitor id is 200
        assertEquals("Beginner", c.getLevel()); // checks that the level is "Beginner"
        assertEquals("Alice Green", c.getCompetitorName().getFullName()); // checks that the full name is correct
        assertEquals("", c.getCountry()); // checks that the default country is an empty string
    }

    @Test // marks this method as a test case
    // Tests creating a competitor with a country and updating it with the setter
    public void testCompetitorWithCountry() {
        // creates a competitor with id 210, name, level "Advanced", and country "Nepal"
        SKMCompetitor c = new SKMCompetitor(210, new Name("Alice", "Green"), "Advanced", "Nepal");
        assertEquals("Nepal", c.getCountry()); // checks that the country is "Nepal"
        c.setCountry("UK"); // changes the country to "UK"
        assertEquals("UK", c.getCountry()); // checks that the country was updated to "UK"
    }

    @Test // marks this method as a test case
    // Tests creating a competitor with scores and retrieving them correctly
    public void testCompetitorWithScores() {
        int[] scores = {4, 3, 5, 2, 4}; // defines an array of 5 scores
        // creates a competitor with id 200, name, level, and scores
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", scores);
        int[] retrieved = c.getScoreArray(); // retrieves the score array from the competitor
        assertEquals(5, retrieved.length); // checks that there are exactly 5 scores
        assertEquals(4, retrieved[0]); // checks that the first score is 4
        assertEquals(3, retrieved[1]); // checks that the second score is 3
        assertEquals(5, retrieved[2]); // checks that the third score is 5
        assertEquals(2, retrieved[3]); // checks that the fourth score is 2
        assertEquals(4, retrieved[4]); // checks that the fifth score is 4
    }

    @Test // marks this method as a test case
    // Tests that the overall score is calculated correctly as the average of all scores
    public void testOverallScore() {
        // Average of 4,3,5,2,4 = 18/5 = 3.6
        int[] scores = {4, 3, 5, 2, 4}; // defines scores that average to 3.6
        // creates a competitor with the given scores
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", scores);
        assertEquals(3.6, c.getOverallScore()); // checks that the overall score (average) is 3.6
    }

    @Test // marks this method as a test case
    // Tests that the overall score handles a zero score correctly
    public void testOverallScoreWithZero() {
        // Average of 0,4,4,4,4 = 16/5 = 3.2
        int[] scores = {0, 4, 4, 4, 4}; // defines scores including a zero, averaging to 3.2
        // creates a competitor with the given scores
        SKMCompetitor c = new SKMCompetitor(300, new Name("Test", "User"), "Beginner", scores);
        assertEquals(3.2, c.getOverallScore()); // checks that the overall score is 3.2 even with a zero
    }

    @Test // marks this method as a test case
    // Tests that getFullDetails returns a string containing all expected competitor information
    public void testFullDetails() {
        int[] scores = {4, 3, 5, 2, 4}; // defines the scores for the competitor
        // creates a competitor with id 200, name, level, and scores
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", scores);
        String full = c.getFullDetails(); // gets the full details string for the competitor
        assertTrue(full.contains("200")); // checks that the full details contain the competitor id
        assertTrue(full.contains("Alice Green")); // checks that the full details contain the name
        assertTrue(full.contains("Beginner")); // checks that the full details contain the level
        assertTrue(full.contains("3.6")); // checks that the full details contain the overall score
        assertTrue(full.contains("This gives them")); // checks that the full details contain the expected phrase
    }

    @Test // marks this method as a test case
    // Tests that getFullDetails includes country and name when a country is provided
    public void testFullDetailsWithCountry() {
        int[] scores = {3, 4, 3, 4, 3}; // defines the scores for the competitor
        // creates a competitor with id 205, name, level "Intermediate", country "Nepal", and scores
        SKMCompetitor c = new SKMCompetitor(205, new Name("Bob", "Smith"), "Intermediate", "Nepal", scores);
        String full = c.getFullDetails(); // gets the full details string for the competitor
        assertTrue(full.contains("Nepal")); // checks that the full details contain the country "Nepal"
        assertTrue(full.contains("Bob Smith")); // checks that the full details contain the name "Bob Smith"
    }

    @Test // marks this method as a test case
    // Tests that getShortDetails returns the correctly formatted short summary string
    public void testShortDetails() {
        int[] scores = {4, 3, 5, 2, 4}; // defines the scores for the competitor
        // creates a competitor with id 200, name, level, and scores
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", scores);
        // checks that the short details match the expected format with initials and overall score
        assertEquals("CN 200 (AG) has an overall score of 3.6.", c.getShortDetails());
    }

    @Test // marks this method as a test case
    // Tests that getScoresString returns scores as a space-separated string
    public void testScoresString() {
        int[] scores = {4, 3, 5, 2, 4}; // defines the scores for the competitor
        // creates a competitor with id 200, name, level, and scores
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", scores);
        assertEquals("4 3 5 2 4", c.getScoresString()); // checks that scores are formatted as "4 3 5 2 4"
    }

    @Test // marks this method as a test case
    // Tests that the setter methods for competitor id and level work correctly
    public void testCompetitorSetters() {
        // creates a competitor with id 200, name, and level "Beginner"
        SKMCompetitor c = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner");
        c.setCompetitorId(201); // changes the competitor id to 201
        assertEquals(201, c.getCompetitorId()); // checks that the id was updated to 201
        c.setLevel("Advanced"); // changes the level to "Advanced"
        assertEquals("Advanced", c.getLevel()); // checks that the level was updated to "Advanced"
    }

    // ======================== CompetitorList Class Tests ========================

    @Test // marks this method as a test case
    // Tests that a newly created CompetitorList is empty with correct defaults
    public void testEmptyList() {
        CompetitorList list = new CompetitorList(); // creates a new empty CompetitorList
        assertEquals(0, list.getTotalCompetitors()); // checks that the list has 0 competitors
        assertEquals(200, list.getNextId()); // checks that the next available id is 200
        assertNull(list.getTopPerformer()); // checks that there is no top performer in an empty list
    }

    @Test // marks this method as a test case
    // Tests adding competitors and retrieving them by id, and checking the next id
    public void testAddAndRetrieve() {
        CompetitorList list = new CompetitorList(); // creates a new empty CompetitorList
        // creates first competitor with id 200 and scores
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[]{4, 3, 5, 2, 4});
        // creates second competitor with id 201 and scores
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[]{3, 4, 4, 5, 4});

        list.addCompetitor(c1); // adds the first competitor to the list
        list.addCompetitor(c2); // adds the second competitor to the list

        assertEquals(2, list.getTotalCompetitors()); // checks that the list now has 2 competitors
        assertSame(c2, list.getCompetitorById(201)); // checks that retrieving id 201 returns the exact same c2 object
        assertNull(list.getCompetitorById(999)); // checks that a non-existent id returns null
        assertEquals(202, list.getNextId()); // checks that the next available id is 202
    }

    @Test // marks this method as a test case
    // Tests removing a competitor by id and verifying the list is updated
    public void testRemoveCompetitor() {
        CompetitorList list = new CompetitorList(); // creates a new empty CompetitorList
        // creates first competitor with id 200 and scores
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[]{4, 3, 5, 2, 4});
        // creates second competitor with id 201 and scores
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[]{3, 4, 4, 5, 4});
        list.addCompetitor(c1); // adds the first competitor to the list
        list.addCompetitor(c2); // adds the second competitor to the list

        assertTrue(list.removeCompetitorById(201)); // checks that removing id 201 returns true (success)
        assertEquals(1, list.getTotalCompetitors()); // checks that only 1 competitor remains
        assertNull(list.getCompetitorById(201)); // checks that id 201 is no longer in the list
        assertFalse(list.removeCompetitorById(999)); // checks that removing a non-existent id returns false
    }

    @Test // marks this method as a test case
    // Tests that getTopPerformer returns the competitor with the highest overall score
    public void testTopPerformer() {
        CompetitorList list = new CompetitorList(); // creates a new empty CompetitorList
        // creates competitor with id 200, scores averaging 3.6
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[]{4, 3, 5, 2, 4});
        // creates competitor with id 201, scores averaging 4.0
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[]{3, 4, 4, 5, 4});
        // creates competitor with id 202, scores averaging 4.6 (highest)
        SKMCompetitor c3 = new SKMCompetitor(202, new Name("Carol", "White"), "Advanced", new int[]{5, 5, 4, 4, 5});

        list.addCompetitor(c1); // adds the first competitor to the list
        list.addCompetitor(c2); // adds the second competitor to the list
        list.addCompetitor(c3); // adds the third competitor to the list

        SKMCompetitor top = list.getTopPerformer(); // gets the competitor with the highest overall score
        assertNotNull(top); // checks that a top performer was found (not null)
        assertEquals(202, top.getCompetitorId()); // checks that the top performer is Carol (id 202)
        assertEquals(4.6, top.getOverallScore(), 0.0001); // checks the top score is 4.6 with a small tolerance
    }

    @Test // marks this method as a test case
    // Tests that getScoreFrequency returns the correct count of how often each score appears
    public void testScoreFrequency() {
        CompetitorList list = new CompetitorList(); // creates a new empty CompetitorList
        // creates competitor with id 200 and scores {4,3,5,2,4}
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[]{4, 3, 5, 2, 4});
        // creates competitor with id 201 and scores {3,4,4,5,4}
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[]{3, 4, 4, 5, 4});
        // creates competitor with id 202 and scores {5,5,4,4,5}
        SKMCompetitor c3 = new SKMCompetitor(202, new Name("Carol", "White"), "Advanced", new int[]{5, 5, 4, 4, 5});

        list.addCompetitor(c1); // adds the first competitor to the list
        list.addCompetitor(c2); // adds the second competitor to the list
        list.addCompetitor(c3); // adds the third competitor to the list

        Map<Integer, Integer> freq = list.getScoreFrequency(); // gets a map of each score to its frequency count
        assertEquals(1, freq.get(2)); // checks that score 2 appears 1 time across all competitors
        assertEquals(2, freq.get(3)); // checks that score 3 appears 2 times across all competitors
        assertEquals(7, freq.get(4)); // checks that score 4 appears 7 times across all competitors
        assertEquals(5, freq.get(5)); // checks that score 5 appears 5 times across all competitors
    }

    @Test // marks this method as a test case
    // Tests that generateReport produces a report string containing all expected sections
    public void testGenerateReport() {
        CompetitorList list = new CompetitorList(); // creates a new empty CompetitorList
        // creates a competitor with id 200, name, level, and scores
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[]{4, 3, 5, 2, 4});
        list.addCompetitor(c1); // adds the competitor to the list

        String report = list.generateReport(); // generates the full report as a string
        assertTrue(report.contains("Competitor ID")); // checks the report contains the header "Competitor ID"
        assertTrue(report.contains("Alice Green")); // checks the report contains the competitor's name
        assertTrue(report.contains("Top Performer")); // checks the report contains the "Top Performer" section
        // checks the report contains the "Statistical Summary" section
        assertTrue(report.contains("Statistical Summary"));
        // checks the report shows the correct total number of competitors
        assertTrue(report.contains("Total number of competitors: 1"));
    }

    // ======================== QuizData Class Tests ========================

    @Test // marks this method as a test case
    // Tests that each quiz level (1 through 5) has exactly 5 questions
    public void testQuizDataQuestionCount() {
        assertEquals(5, QuizData.getQuestionsForLevel(1).size()); // checks level 1 has 5 questions
        assertEquals(5, QuizData.getQuestionsForLevel(2).size()); // checks level 2 has 5 questions
        assertEquals(5, QuizData.getQuestionsForLevel(3).size()); // checks level 3 has 5 questions
        assertEquals(5, QuizData.getQuestionsForLevel(4).size()); // checks level 4 has 5 questions
        assertEquals(5, QuizData.getQuestionsForLevel(5).size()); // checks level 5 has 5 questions
    }

    @Test // marks this method as a test case
    // Tests that getLevelName returns the correct attempt name string for each level number
    public void testQuizDataAttemptNames() {
        assertEquals("Attempt 1", QuizData.getLevelName(1)); // checks that level 1 is named "Attempt 1"
        assertEquals("Attempt 3", QuizData.getLevelName(3)); // checks that level 3 is named "Attempt 3"
        assertEquals("Attempt 5", QuizData.getLevelName(5)); // checks that level 5 is named "Attempt 5"
    }

    @Test // marks this method as a test case
    // Tests that a quiz question has valid structure: non-empty text, 4 options, and a valid answer index
    public void testQuizDataQuestionStructure() {
        QuizQuestion q = QuizData.getQuestionsForLevel(1).get(0); // gets the first question from level 1
        assertNotNull(q.getQuestion()); // checks that the question text is not null
        assertFalse(q.getQuestion().isEmpty()); // checks that the question text is not empty
        String[] options = q.getOptions(); // gets the array of answer options
        assertEquals(4, options.length); // checks that there are exactly 4 answer options
        // checks that the correct answer index is between 0 and 3 (valid option index)
        assertTrue(q.getCorrectAnswerIndex() >= 0 && q.getCorrectAnswerIndex() <= 3);
    }
}
