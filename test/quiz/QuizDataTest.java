package quiz;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * JUnit 5 tests for the QuizData class.
 *
 * @author Sailesh Kumar Mandal
 */
class QuizDataTest {

    @Test
    void testTotalQuestionCount() {
        List<QuizQuestion> all = QuizData.getAllQuestions();
        assertEquals(25, all.size(),
                "Total question count should be 25");
    }

    @Test
    void testQuestionsPerAttempt() {
        for (int attempt = 1; attempt <= 5; attempt++) {
            List<QuizQuestion> questions = QuizData.getQuestionsForLevel(attempt);
            assertEquals(5, questions.size(),
                    "Attempt " + attempt + " should have exactly 5 questions");
        }
    }

    @Test
    void testAttemptNames() {
        assertEquals("Attempt 1", QuizData.getLevelName(1),
                "Attempt 1 name should be 'Attempt 1'");
        assertEquals("Attempt 3", QuizData.getLevelName(3),
                "Attempt 3 name should be 'Attempt 3'");
        assertEquals("Attempt 5", QuizData.getLevelName(5),
                "Attempt 5 name should be 'Attempt 5'");
    }

    @Test
    void testInvalidAttemptName() {
        assertEquals("Unknown", QuizData.getLevelName(0),
                "Invalid attempt should return 'Unknown'");
        assertEquals("Unknown", QuizData.getLevelName(6),
                "Attempt 6 should return 'Unknown'");
    }

    @Test
    void testQuestionHasFourOptions() {
        List<QuizQuestion> all = QuizData.getAllQuestions();
        for (QuizQuestion q : all) {
            String[] options = q.getOptions();
            assertEquals(4, options.length,
                    "Each question should have exactly 4 options");
        }
    }

    @Test
    void testQuestionHasValidAnswer() {
        List<QuizQuestion> all = QuizData.getAllQuestions();
        for (QuizQuestion q : all) {
            int answer = q.getCorrectAnswerIndex();
            assertTrue(answer >= 0 && answer <= 3,
                    "Correct answer index should be between 0 and 3, got: " + answer);
        }
    }

    @Test
    void testQuestionTextNotEmpty() {
        List<QuizQuestion> all = QuizData.getAllQuestions();
        for (QuizQuestion q : all) {
            assertNotNull(q.getQuestion(), "Question text should not be null");
            assertFalse(q.getQuestion().isEmpty(), "Question text should not be empty");
        }
    }

    @Test
    void testFirstQuestionContent() {
        List<QuizQuestion> attempt1 = QuizData.getQuestionsForLevel(1);
        assertEquals("What is the capital of France?", attempt1.get(0).getQuestion(),
                "First question of Attempt 1 should be about the capital of France");
    }
}
