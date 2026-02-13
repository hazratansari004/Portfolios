package quiz; // declares this class belongs to the "quiz" package

/**
 * Represents a single quiz question with four answer options.
 */
public class QuizQuestion { // defines the QuizQuestion class
    private String question; // stores the question text
    private String[] options; // array holding the four answer options
    private int correctAnswerIndex; // stores the index (0-3) of the correct answer

    // constructor that takes the question text, four options, and the correct answer index
    public QuizQuestion(String question, String optA, String optB, String optC, String optD, int correctAnswerIndex) {
        this.question = question; // assigns the question text to the instance variable
        this.options = new String[]{optA, optB, optC, optD}; // creates an array from the four option parameters
        this.correctAnswerIndex = correctAnswerIndex; // stores which option index is correct
    }

    public String getQuestion() { // getter method that returns the question text
        return question; // returns the question string
    }

    public String[] getOptions() { // getter method that returns the options array
        return options; // returns the array of four answer options
    }

    public int getCorrectAnswerIndex() { // getter method that returns the correct answer index
        return correctAnswerIndex; // returns the index of the correct answer
    }

    public boolean isCorrect(int selectedIndex) { // checks if the selected answer is correct
        return selectedIndex == correctAnswerIndex; // returns true if selected index matches the correct one
    }
} // end of QuizQuestion class
