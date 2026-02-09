package quiz;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * JUnit 5 tests for the CompetitorList class.
 *
 * @author Sailesh Kumar Mandal
 */
class CompetitorListTest {

    @Test
    void testAddGetNextIdAndTotal() {
        CompetitorList list = new CompetitorList();
        assertEquals(0, list.getTotalCompetitors(), "new list should be empty");
        assertEquals(200, list.getNextId(), "initial next id should be 200");

        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[] {4,3,5,2,4});
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[] {3,4,4,5,4});

        list.addCompetitor(c1);
        list.addCompetitor(c2);

        assertEquals(2, list.getTotalCompetitors(), "list size should be 2 after adds");
        assertSame(c2, list.getCompetitorById(201), "should return the exact competitor object by id");
        assertNull(list.getCompetitorById(999), "unknown id should return null");
        assertEquals(202, list.getNextId(), "next id should advance after adds");
    }

    @Test
    void testRemoveCompetitor() {
        CompetitorList list = new CompetitorList();
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[] {4,3,5,2,4});
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[] {3,4,4,5,4});
        list.addCompetitor(c1);
        list.addCompetitor(c2);

        assertTrue(list.removeCompetitorById(201), "existing competitor should be removable");
        assertEquals(1, list.getTotalCompetitors(), "size should decrease after remove");
        assertNull(list.getCompetitorById(201), "removed competitor should no longer be found");

        assertFalse(list.removeCompetitorById(999), "removing non-existent id should return false");
    }

    @Test
    void testTopPerformerAndFrequency() {
        CompetitorList list = new CompetitorList();
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[] {4,3,5,2,4}); // avg 3.6
        SKMCompetitor c2 = new SKMCompetitor(201, new Name("Bob", "Brown"), "Intermediate", new int[] {3,4,4,5,4}); // avg 4.0
        SKMCompetitor c3 = new SKMCompetitor(202, new Name("Carol", "White"), "Advanced", new int[] {5,5,4,4,5}); // avg 4.6

        list.addCompetitor(c1);
        list.addCompetitor(c2);
        list.addCompetitor(c3);

        SKMCompetitor top = list.getTopPerformer();
        assertNotNull(top, "top performer should not be null when list has competitors");
        assertEquals(202, top.getCompetitorId(), "Carol (id 202) should be top performer");
        assertEquals(4.6, top.getOverallScore(), 0.0001, "top performer overall score should be 4.6");

        Map<Integer, Integer> freq = list.getScoreFrequency();
        assertNotNull(freq, "score frequency map should not be null");
        assertTrue(freq.containsKey(4), "frequency map should contain score 4");
        assertTrue(freq.containsKey(5), "frequency map should contain score 5");
    }

    @Test
    void testTopPerformerEmptyList() {
        CompetitorList list = new CompetitorList();
        assertNull(list.getTopPerformer(), "top performer should be null for empty list");
    }

    @Test
    void testGenerateReport() {
        CompetitorList list = new CompetitorList();
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[] {4,3,5,2,4});
        list.addCompetitor(c1);

        String report = list.generateReport();
        assertNotNull(report, "generated report should not be null");
        assertTrue(report.contains("200"), "report should contain competitor ID");
        assertTrue(report.contains("Alice Green"), "report should contain competitor name");
        assertTrue(report.contains("Top Performer"), "report should contain top performer section");
        assertTrue(report.contains("Statistical Summary"), "report should contain statistics section");
    }

    @Test
    void testGetCompetitorsList() {
        CompetitorList list = new CompetitorList();
        SKMCompetitor c1 = new SKMCompetitor(200, new Name("Alice", "Green"), "Beginner", new int[] {4,3,5,2,4});
        list.addCompetitor(c1);

        assertNotNull(list.getCompetitors(), "getCompetitors should not return null");
        assertEquals(1, list.getCompetitors().size(), "getCompetitors should have 1 element");
    }

    @Test
    void testIsDatabaseConnectedDefault() {
        CompetitorList list = new CompetitorList();
        assertFalse(list.isDatabaseConnected(),
                "new CompetitorList should not be connected to database by default");
    }
}
