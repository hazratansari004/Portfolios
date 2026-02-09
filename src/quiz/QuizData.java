
package quiz;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides 25 quiz questions across 5 attempts (5 questions per attempt).
 * Topics cover general knowledge, science, geography, technology, and sports.
 *
 * @author Sailesh Kumar Mandal
 */
public class QuizData {

    public static List<QuizQuestion> getAllQuestions() {
        List<QuizQuestion> questions = new ArrayList<>();

        // Attempt 1: Beginner (Questions 1-5)
        questions.add(new QuizQuestion(
            "What is the capital of France?",
            "Berlin", "Madrid", "Paris", "Rome", 2));
        questions.add(new QuizQuestion(
            "Which planet is known as the Red Planet?",
            "Venus", "Mars", "Jupiter", "Saturn", 1));
        questions.add(new QuizQuestion(
            "What is the largest ocean on Earth?",
            "Atlantic", "Indian", "Arctic", "Pacific", 3));
        questions.add(new QuizQuestion(
            "How many continents are there?",
            "5", "6", "7", "8", 2));
        questions.add(new QuizQuestion(
            "What gas do plants absorb from the atmosphere?",
            "Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen", 2));

        // Attempt 2: Elementary (Questions 6-10)
        questions.add(new QuizQuestion(
            "Who painted the Mona Lisa?",
            "Michelangelo", "Leonardo da Vinci", "Raphael", "Donatello", 1));
        questions.add(new QuizQuestion(
            "What is the chemical symbol for water?",
            "O2", "CO2", "H2O", "NaCl", 2));
        questions.add(new QuizQuestion(
            "Which country is known as the Land of the Rising Sun?",
            "China", "South Korea", "Thailand", "Japan", 3));
        questions.add(new QuizQuestion(
            "What is the smallest prime number?",
            "0", "1", "2", "3", 2));
        questions.add(new QuizQuestion(
            "In which year did World War II end?",
            "1943", "1944", "1945", "1946", 2));

        // Attempt 3: Intermediate (Questions 11-15)
        questions.add(new QuizQuestion(
            "What is the powerhouse of the cell?",
            "Nucleus", "Ribosome", "Mitochondria", "Golgi Body", 2));
        questions.add(new QuizQuestion(
            "Who wrote 'Romeo and Juliet'?",
            "Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain", 1));
        questions.add(new QuizQuestion(
            "What is the speed of light approximately?",
            "300,000 km/s", "150,000 km/s", "500,000 km/s", "100,000 km/s", 0));
        questions.add(new QuizQuestion(
            "Which element has the atomic number 1?",
            "Helium", "Hydrogen", "Lithium", "Carbon", 1));
        questions.add(new QuizQuestion(
            "What is the largest planet in our solar system?",
            "Saturn", "Neptune", "Jupiter", "Uranus", 2));

        // Attempt 4: Advanced (Questions 16-20)
        questions.add(new QuizQuestion(
            "What programming language was developed by James Gosling?",
            "Python", "C++", "Java", "Ruby", 2));
        questions.add(new QuizQuestion(
            "In which year was the World Wide Web invented?",
            "1985", "1989", "1991", "1995", 1));
        questions.add(new QuizQuestion(
            "What does CPU stand for?",
            "Central Processing Unit", "Central Program Utility",
            "Computer Personal Unit", "Central Processor Unifier", 0));
        questions.add(new QuizQuestion(
            "Which data structure uses FIFO?",
            "Stack", "Queue", "Tree", "Graph", 1));
        questions.add(new QuizQuestion(
            "What is the binary representation of the decimal number 10?",
            "1010", "1100", "1001", "1110", 0));

        // Attempt 5: Expert (Questions 21-25)
        questions.add(new QuizQuestion(
            "Who is known as the father of computer science?",
            "Albert Einstein", "Alan Turing", "Nikola Tesla", "Isaac Newton", 1));
        questions.add(new QuizQuestion(
            "What does HTML stand for?",
            "Hyper Text Markup Language", "High Tech Modern Language",
            "Hyper Transfer Markup Language", "Home Tool Markup Language", 0));
        questions.add(new QuizQuestion(
            "Which sorting algorithm has the best average time complexity?",
            "Bubble Sort", "Selection Sort", "Merge Sort", "Insertion Sort", 2));
        questions.add(new QuizQuestion(
            "What is the time complexity of binary search?",
            "O(n)", "O(n log n)", "O(log n)", "O(1)", 2));
        questions.add(new QuizQuestion(
            "In OOP, what does encapsulation mean?",
            "Hiding implementation details", "Inheriting from parent class",
            "Overriding methods", "Creating multiple objects", 0));

        return questions;
    }

    /** Returns questions for a specific attempt (1-5). */
    public static List<QuizQuestion> getQuestionsForLevel(int level) {
        List<QuizQuestion> all = getAllQuestions();
        int start = (level - 1) * 5;
        int end = Math.min(start + 5, all.size());
        return all.subList(start, end);
    }

    /** Returns the attempt name for a given attempt number. */
    public static String getLevelName(int level) {
        switch (level) {
            case 1: return "Attempt 1";
            case 2: return "Attempt 2";
            case 3: return "Attempt 3";
            case 4: return "Attempt 4";
            case 5: return "Attempt 5";
            default: return "Unknown";
        }
    }
}
