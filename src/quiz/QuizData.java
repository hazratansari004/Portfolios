
package quiz; // declares this class belongs to the 'quiz' package

import java.util.ArrayList; // import ArrayList class for creating resizable lists
import java.util.List; // import List interface to use as the return type

/**
 * Provides 25 quiz questions across 5 attempts (5 questions per attempt).
 * Topics cover general knowledge, science, geography, technology, and sports.
 *
 * @author Sailesh Kumar Mandal
 */
public class QuizData { // defines the QuizData class that stores all quiz questions

    public static List<QuizQuestion> getAllQuestions() { // static method that returns all 25 quiz questions as a list
        List<QuizQuestion> questions = new ArrayList<>(); // creates an empty ArrayList to hold QuizQuestion objects

        // Attempt 1: Beginner (Questions 1-5)
        // Q1: Geography - asks about the capital city of France (answer: Paris, index 2)
        questions.add(new QuizQuestion(
            "What is the capital of France?",
            "Berlin", "Madrid", "Paris", "Rome", 2));
        // Q2: Science - asks which planet is called the Red Planet (answer: Mars, index 1)
        questions.add(new QuizQuestion(
            "Which planet is known as the Red Planet?",
            "Venus", "Mars", "Jupiter", "Saturn", 1));
        // Q3: Geography - asks about the biggest ocean (answer: Pacific, index 3)
        questions.add(new QuizQuestion(
            "What is the largest ocean on Earth?",
            "Atlantic", "Indian", "Arctic", "Pacific", 3));
        // Q4: General knowledge - asks total number of continents (answer: 7, index 2)
        questions.add(new QuizQuestion(
            "How many continents are there?",
            "5", "6", "7", "8", 2));
        // Q5: Science - asks what gas plants absorb (answer: Carbon Dioxide, index 2)
        questions.add(new QuizQuestion(
            "What gas do plants absorb from the atmosphere?",
            "Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen", 2));

        // Attempt 2: Elementary (Questions 6-10)
        // Q6: Art - asks who painted the Mona Lisa (answer: Leonardo da Vinci, index 1)
        questions.add(new QuizQuestion(
            "Who painted the Mona Lisa?",
            "Michelangelo", "Leonardo da Vinci", "Raphael", "Donatello", 1));
        // Q7: Chemistry - asks for the chemical symbol of water (answer: H2O, index 2)
        questions.add(new QuizQuestion(
            "What is the chemical symbol for water?",
            "O2", "CO2", "H2O", "NaCl", 2));
        // Q8: Geography - asks which country is the Land of the Rising Sun (answer: Japan, index 3)
        questions.add(new QuizQuestion(
            "Which country is known as the Land of the Rising Sun?",
            "China", "South Korea", "Thailand", "Japan", 3));
        // Q9: Math - asks for the smallest prime number (answer: 2, index 2)
        questions.add(new QuizQuestion(
            "What is the smallest prime number?",
            "0", "1", "2", "3", 2));
        // Q10: History - asks when World War II ended (answer: 1945, index 2)
        questions.add(new QuizQuestion(
            "In which year did World War II end?",
            "1943", "1944", "1945", "1946", 2));

        // Attempt 3: Intermediate (Questions 11-15)
        // Q11: Biology - asks about the powerhouse of the cell (answer: Mitochondria, index 2)
        questions.add(new QuizQuestion(
            "What is the powerhouse of the cell?",
            "Nucleus", "Ribosome", "Mitochondria", "Golgi Body", 2));
        // Q12: Literature - asks who wrote Romeo and Juliet (answer: Shakespeare, index 1)
        questions.add(new QuizQuestion(
            "Who wrote 'Romeo and Juliet'?",
            "Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain", 1));
        // Q13: Physics - asks about the speed of light (answer: 300,000 km/s, index 0)
        questions.add(new QuizQuestion(
            "What is the speed of light approximately?",
            "300,000 km/s", "150,000 km/s", "500,000 km/s", "100,000 km/s", 0));
        // Q14: Chemistry - asks which element has atomic number 1 (answer: Hydrogen, index 1)
        questions.add(new QuizQuestion(
            "Which element has the atomic number 1?",
            "Helium", "Hydrogen", "Lithium", "Carbon", 1));
        // Q15: Astronomy - asks about the largest planet (answer: Jupiter, index 2)
        questions.add(new QuizQuestion(
            "What is the largest planet in our solar system?",
            "Saturn", "Neptune", "Jupiter", "Uranus", 2));

        // Attempt 4: Advanced (Questions 16-20)
        // Q16: Technology - asks which language James Gosling created (answer: Java, index 2)
        questions.add(new QuizQuestion(
            "What programming language was developed by James Gosling?",
            "Python", "C++", "Java", "Ruby", 2));
        // Q17: Technology - asks when the World Wide Web was invented (answer: 1989, index 1)
        questions.add(new QuizQuestion(
            "In which year was the World Wide Web invented?",
            "1985", "1989", "1991", "1995", 1));
        // Q18: Technology - asks what CPU stands for (answer: Central Processing Unit, index 0)
        questions.add(new QuizQuestion(
            "What does CPU stand for?",
            "Central Processing Unit", "Central Program Utility",
            "Computer Personal Unit", "Central Processor Unifier", 0));
        // Q19: Data Structures - asks which structure uses FIFO order (answer: Queue, index 1)
        questions.add(new QuizQuestion(
            "Which data structure uses FIFO?",
            "Stack", "Queue", "Tree", "Graph", 1));
        // Q20: Binary - asks for binary of decimal 10 (answer: 1010, index 0)
        questions.add(new QuizQuestion(
            "What is the binary representation of the decimal number 10?",
            "1010", "1100", "1001", "1110", 0));

        // Attempt 5: Expert (Questions 21-25)
        // Q21: CS History - asks about the father of computer science (answer: Alan Turing, index 1)
        questions.add(new QuizQuestion(
            "Who is known as the father of computer science?",
            "Albert Einstein", "Alan Turing", "Nikola Tesla", "Isaac Newton", 1));
        // Q22: Web - asks what HTML stands for (answer: Hyper Text Markup Language, index 0)
        questions.add(new QuizQuestion(
            "What does HTML stand for?",
            "Hyper Text Markup Language", "High Tech Modern Language",
            "Hyper Transfer Markup Language", "Home Tool Markup Language", 0));
        // Q23: Algorithms - asks which sort has best average complexity (answer: Merge Sort, index 2)
        questions.add(new QuizQuestion(
            "Which sorting algorithm has the best average time complexity?",
            "Bubble Sort", "Selection Sort", "Merge Sort", "Insertion Sort", 2));
        // Q24: Algorithms - asks time complexity of binary search (answer: O(log n), index 2)
        questions.add(new QuizQuestion(
            "What is the time complexity of binary search?",
            "O(n)", "O(n log n)", "O(log n)", "O(1)", 2));
        // Q25: OOP - asks what encapsulation means (answer: Hiding implementation details, index 0)
        questions.add(new QuizQuestion(
            "In OOP, what does encapsulation mean?",
            "Hiding implementation details", "Inheriting from parent class",
            "Overriding methods", "Creating multiple objects", 0));

        return questions; // returns the complete list of 25 questions
    } // end of getAllQuestions method

    /** Returns questions for a specific attempt (1-5). */
    public static List<QuizQuestion> getQuestionsForLevel(int level) { // method to get 5 questions for a given attempt level
        List<QuizQuestion> all = getAllQuestions(); // fetch all 25 questions
        int start = (level - 1) * 5; // calculate starting index (e.g., level 1 starts at 0, level 2 at 5)
        int end = Math.min(start + 5, all.size()); // calculate ending index, capped at list size to avoid overflow
        return all.subList(start, end); // returns a sublist of 5 questions for the requested level
    } // end of getQuestionsForLevel method

    /** Returns the attempt name for a given attempt number. */
    public static String getLevelName(int level) { // method that returns the display name for each attempt level
        switch (level) { // switch statement to match the level number
            case 1: return "Attempt 1"; // return name for level 1
            case 2: return "Attempt 2"; // return name for level 2
            case 3: return "Attempt 3"; // return name for level 3
            case 4: return "Attempt 4"; // return name for level 4
            case 5: return "Attempt 5"; // return name for level 5
            default: return "Unknown"; // return "Unknown" if level is out of range
        } // end of switch
    } // end of getLevelName method
} // end of QuizData class
