package quiz;

/**
 * Simple test class to validate core functionality of the quiz application.
 * Tests Name, HACompetitor, CompetitorList, and QuizData classes.
 */
public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    private static void test(String name, boolean condition) {
        if (condition) {
            System.out.println("  PASS: " + name);
            passed++;
        } else {
            System.out.println("  FAIL: " + name);
            failed++;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Name Class Tests ===");
        testNameClass();

        System.out.println("\n=== HACompetitor Class Tests ===");
        testCompetitorClass();

        System.out.println("\n=== CompetitorList Class Tests ===");
        testCompetitorList();

        System.out.println("\n=== QuizData Tests ===");
        testQuizData();

        System.out.println("\n=== Results ===");
        System.out.println("Passed: " + passed + ", Failed: " + failed);
        System.exit(failed > 0 ? 1 : 0);
    }

    private static void testNameClass() {
        Name n1 = new Name("Alice", "Green");
        test("Full name (two parts)", n1.getFullName().equals("Alice Green"));
        test("Initials (two parts)", n1.getInitials().equals("AG"));

        Name n2 = new Name("Keith", "John", "Talbot");
        test("Full name (three parts)", n2.getFullName().equals("Keith John Talbot"));
        test("Initials (three parts)", n2.getInitials().equals("KJT"));

        n2.setFirstName("Bob");
        test("setFirstName", n2.getFirstName().equals("Bob"));
        test("toString", n1.toString().equals("Alice Green"));
    }

    private static void testCompetitorClass() {
        Name name = new Name("Alice", "Green");
        HACompetitor c = new HACompetitor(200, name, "Beginner", 21);
        test("getCompetitorId", c.getCompetitorId() == 200);
        test("getLevel", c.getLevel().equals("Beginner"));
        test("getAge", c.getAge() == 21);
        test("getName", c.getCompetitorName().getFullName().equals("Alice Green"));

        // Test with scores
        int[] scores = {4, 3, 5, 2, 4};
        c.setScores(scores);
        int[] retrieved = c.getScoreArray();
        test("getScoreArray length", retrieved.length == 5);
        test("getScoreArray values", retrieved[0] == 4 && retrieved[1] == 3 && retrieved[2] == 5);

        // Overall score: average of 4,3,5,2,4 = 18/5 = 3.6
        test("getOverallScore", c.getOverallScore() == 3.6);

        // Full details
        String full = c.getFullDetails();
        test("getFullDetails contains ID", full.contains("200"));
        test("getFullDetails contains name", full.contains("Alice Green"));
        test("getFullDetails contains level", full.contains("Beginner"));
        test("getFullDetails contains score", full.contains("3.6"));

        // Short details
        String shortD = c.getShortDetails();
        test("getShortDetails format", shortD.equals("CN 200 (AG) has an overall score of 3.6."));

        // Test scores string
        test("getScoresString", c.getScoresString().equals("4 3 5 2 4"));

        // Test setters
        c.setCompetitorId(201);
        test("setCompetitorId", c.getCompetitorId() == 201);
        c.setLevel("Advanced");
        test("setLevel", c.getLevel().equals("Advanced"));
        c.setAge(25);
        test("setAge", c.getAge() == 25);
    }

    private static void testCompetitorList() {
        CompetitorList list = new CompetitorList();
        test("empty list size", list.getTotalCompetitors() == 0);
        test("getNextId initial", list.getNextId() == 200);

        HACompetitor c1 = new HACompetitor(200, new Name("Alice", "Green"), "Beginner", 21,
                new int[]{4, 3, 5, 2, 4});
        HACompetitor c2 = new HACompetitor(201, new Name("Bob", "Brown"), "Intermediate", 25,
                new int[]{3, 4, 4, 5, 4});
        HACompetitor c3 = new HACompetitor(202, new Name("Carol", "White"), "Advanced", 30,
                new int[]{5, 5, 4, 4, 5});

        list.addCompetitor(c1);
        list.addCompetitor(c2);
        list.addCompetitor(c3);

        test("list size after adds", list.getTotalCompetitors() == 3);
        test("getCompetitorById found", list.getCompetitorById(201) == c2);
        test("getCompetitorById not found", list.getCompetitorById(999) == null);
        test("getNextId after adds", list.getNextId() == 203);

        // Top performer should be Carol (4.6)
        HACompetitor top = list.getTopPerformer();
        test("getTopPerformer", top == c3);
        test("top performer score", top.getOverallScore() == 4.6);

        // Score frequency
        java.util.Map<Integer, Integer> freq = list.getScoreFrequency();
        test("frequency contains scores", freq.containsKey(4) && freq.containsKey(5));

        // Report
        String report = list.generateReport();
        test("report contains header", report.contains("Competitor ID"));
        test("report contains competitor", report.contains("Alice Green"));
        test("report contains stats", report.contains("Total number of competitors: 3"));
    }

    private static void testQuizData() {
        test("total questions", QuizData.getAllQuestions().size() == 25);
        test("level 1 questions", QuizData.getQuestionsForLevel(1).size() == 5);
        test("level 5 questions", QuizData.getQuestionsForLevel(5).size() == 5);
        test("level name 1", QuizData.getLevelName(1).equals("Beginner"));
        test("level name 3", QuizData.getLevelName(3).equals("Intermediate"));
        test("level name 5", QuizData.getLevelName(5).equals("Expert"));

        // Test question structure
        QuizQuestion q = QuizData.getAllQuestions().get(0);
        test("question has text", q.getQuestion() != null && !q.getQuestion().isEmpty());
        test("question has 4 options", q.getOptions().length == 4);
        test("correct answer check", q.isCorrect(q.getCorrectAnswerIndex()));
        test("wrong answer check", !q.isCorrect((q.getCorrectAnswerIndex() + 1) % 4));
    }
}
