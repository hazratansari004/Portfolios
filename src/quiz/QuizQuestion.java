package quiz;

/**
 * Represents a single quiz question with four answer options.
 *
 * @author Sailesh Kumar Mandal
 */
public class QuizQuestion {
    private String question;
    private String[] options;
    private int correctAnswerIndex; // 0-3

    public QuizQuestion(String question, String optA, String optB, String optC, String optD, int correctAnswerIndex) {
        this.question = question;
        this.options = new String[]{optA, optB, optC, optD};
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctAnswerIndex;
    }
}
