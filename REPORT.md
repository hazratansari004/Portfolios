# Quiz Competition Management System — Submission Report

**Module:** Object-Oriented Design and Programming (OODP)  
**Author:** Sailesh Kumar Mandal  
**Date:** February 2026  
**Application Name:** Quiz Competition Management System  
**Class Naming Convention:** SKMCompetitor (SKM — initials of the author)

---

## Table of Contents

1. [Design Decisions](#1-design-decisions)
2. [Status Report](#2-status-report)
3. [Known Bugs and Limitations](#3-known-bugs-and-limitations)
4. [Tests Performed](#4-tests-performed)

---

## 1. Design Decisions

### 1.1 Extra Competitor Attribute — Country

The additional attribute selected for each competitor is **country** (a `String` value). This attribute was chosen for the following reasons:

- **Relevance to competition context:** In international quiz competitions, the country of origin is a practical and meaningful attribute that distinguishes competitors from different regions.
- **Flexibility:** Unlike a fixed enumeration, a free-text `String` allows the application to accommodate competitors from any nation without requiring code changes when new countries are introduced.
- **Display suitability:** The country attribute integrates naturally into both the full details output and the competitor table, providing useful contextual information without adding unnecessary complexity.

The `country` attribute is stored as a private `String` instance variable within the `SKMCompetitor` class, with corresponding getter and setter methods (`getCountry()`, `setCountry(String)`). When the country is not specified, it defaults to an empty string, ensuring that the output methods handle the absence of this attribute gracefully.

### 1.2 Overall Score Calculation

The overall score is calculated as the **arithmetic mean (average) of all five individual scores**, rounded to one decimal place. The formula applied is:

```
overallScore = round( (Score1 + Score2 + Score3 + Score4 + Score5) / 5.0, 1 decimal place )
```

This approach was selected for the following reasons:

- **Fairness:** Each level carries equal weight in determining the final score, ensuring that performance across all five quiz levels contributes equally to the overall result.
- **Simplicity and transparency:** The arithmetic mean is straightforward to understand and verify, making it accessible to both users and assessors.
- **Handling of zero scores:** The implementation correctly includes zero scores in the average. If a competitor fails to answer any questions correctly in a particular level, that level's score of zero is still factored into the overall calculation, accurately reflecting their performance.

The implementation within `SKMCompetitor.getOverallScore()` iterates through the five-element integer score array, computes the sum, divides by the array length (5), and applies `Math.round()` to achieve one-decimal-place precision:

```java
public double getOverallScore() {
    int sum = 0;
    for (int s : scores) {
        sum += s;
    }
    return Math.round((sum / (double) scores.length) * 10.0) / 10.0;
}
```

### 1.3 Name Class Design

A separate `Name` class was implemented to handle competitor names, supporting first name, optional middle name, and last name. This design adheres to the Single Responsibility Principle by isolating name-related logic (such as generating initials and formatting full names) from the competitor class itself. The `Name` class provides:

- `getFullName()` — returns the complete name, including the middle name if present.
- `getInitials()` — extracts the first letter of each name component to form initials (e.g., "SKM" for "Sailesh Kumar Mandal").
- Standard getter and setter methods for each name component.

### 1.4 Competition Levels

Competition levels are represented as fixed `String` values: **Beginner**, **Intermediate**, and **Advanced**. In the registration form, a `JComboBox` dropdown restricts the user to selecting from these three valid options, preventing invalid level entries. This design enforces data integrity at the user interface level.

### 1.5 Quiz Structure

The quiz application consists of **25 questions** divided across **5 levels** (5 questions per level):

| Level | Name         | Questions |
|-------|-------------|-----------|
| 1     | Beginner     | 1–5       |
| 2     | Elementary   | 6–10      |
| 3     | Intermediate | 11–15     |
| 4     | Advanced     | 16–20     |
| 5     | Expert       | 21–25     |

Each question is worth **4 percentage points** (25 × 4% = 100%), and the score for each level (Score1 through Score5) records the number of correct answers out of 5 for that level.

### 1.6 Database Design

The MySQL database `CompetitionDB` contains a single table `Competitors` with the following structure:

| Column        | Type         | Constraint       |
|--------------|-------------|-----------------|
| CompetitorID | INT          | PRIMARY KEY      |
| FirstName    | VARCHAR(50)  | NOT NULL         |
| MiddleName   | VARCHAR(50)  | DEFAULT ''       |
| LastName     | VARCHAR(50)  | NOT NULL         |
| Level        | VARCHAR(20)  | NOT NULL         |
| Country      | VARCHAR(50)  | DEFAULT ''       |
| Score1       | INT          | DEFAULT 0        |
| Score2       | INT          | DEFAULT 0        |
| Score3       | INT          | DEFAULT 0        |
| Score4       | INT          | DEFAULT 0        |
| Score5       | INT          | DEFAULT 0        |

JDBC methods for reading from and writing to the database are implemented **directly within the `SKMCompetitor` class** as specified in the coursework brief:

- `saveToDatabase(Connection)` — inserts the competitor into the database.
- `readFromDatabase(Connection, int)` — retrieves a competitor by ID (static method).
- `readAllFromDatabase(Connection)` — retrieves all competitors (static method).
- `updateInDatabase(Connection)` — updates the competitor's record.
- `deleteFromDatabase(Connection)` — removes the competitor's record.

All SQL operations use `PreparedStatement` to prevent SQL injection vulnerabilities.

---

## 2. Status Report

The application **fully meets all specifications** outlined in the coursework brief. All three parts have been implemented completely:

### Part One — Competitor Class
- The `SKMCompetitor` class is fully implemented with all required instance variables (Competitor ID, Name using the `Name` class, Level, and Country as the extra attribute).
- All required methods are present: constructor(s), getters, setters, `getOverallScore()`, `getFullDetails()`, and `getShortDetails()`.
- The `getShortDetails()` method follows the exact required format: `"CN {id} ({initials}) has an overall score of {score}."`.

### Part Two — MySQL and Arrays
- An integer array of 5 scores has been added to the competitor class.
- The `getScoreArray()` method returns a defensive copy of the scores array.
- The `getOverallScore()` method calculates the average of all five scores.
- The MySQL database integration is fully implemented using JDBC with `PreparedStatement`.
- Read and write methods are implemented within the `SKMCompetitor` class as required.

### Part Three — Reports and User Interaction
- The `Manager` class provides the main Swing GUI application.
- The `CompetitorList` class manages the collection of competitors with database integration.
- A comprehensive report is produced including a competitor table, top performer details, and statistical summaries with score frequency.
- Users can search for a competitor by ID and view both full and short details.
- Error handling is implemented for invalid data entry and database connection issues.

### Additional Features (Quiz Application)
- A fully interactive quiz with 25 questions, timer (15 seconds per question), level completion prompts, and score tracking.
- Registration form with input validation.
- Manage Competitors screen with Refresh, Search, Delete, and Back functionality.

The application is **complete with no missing features**.

---

## 3. Known Bugs and Limitations

### 3.1 Limitations

1. **Database dependency:** The application requires a MySQL server running on `localhost:3306` with a database named `CompetitionDB`. If the MySQL server is not available, the application operates in an in-memory mode where competitor data is not persisted between sessions. This is by design, as a graceful fallback.

2. **JDBC driver dependency:** The MySQL Connector/J JAR file must be on the classpath. If it is absent, the application will display an actionable error message with instructions for adding the driver. The application will still function in offline/in-memory mode.

3. **Fixed number of scores:** The application is designed for exactly 5 scores per competitor (one per quiz level). This number is hard-coded and cannot be changed without modifying the source code.

4. **Single-user operation:** The application is designed for use by one person at a time. There is no concurrent access handling for the database.

5. **No data export:** While the application generates reports on screen, there is no facility to export reports to an external file (e.g., PDF or CSV).

### 3.2 Known Bugs

There are **no known bugs** at the time of submission. All methods have been tested and function as expected. The application has been compiled and tested using Java 17.

---

## 4. Tests Performed

All 52 tests listed below were executed and passed successfully. Tests were implemented in the `TestRunner` class.

### 4.1 Name Class Tests (6 Tests)

| Test No. | Test Description                                   | Input                              | Expected Output           | Result |
|---------|---------------------------------------------------|-----------------------------------|--------------------------|--------|
| 1       | Full name with two parts                          | Name("Alice", "Green")            | "Alice Green"             | PASS   |
| 2       | Initials with two parts                           | Name("Alice", "Green")            | "AG"                      | PASS   |
| 3       | Full name with three parts                        | Name("Keith", "John", "Talbot")   | "Keith John Talbot"       | PASS   |
| 4       | Initials with three parts                         | Name("Keith", "John", "Talbot")   | "KJT"                     | PASS   |
| 5       | setFirstName updates correctly                    | n2.setFirstName("Bob")            | "Bob"                     | PASS   |
| 6       | toString returns full name                        | Name("Alice", "Green").toString()  | "Alice Green"             | PASS   |

### 4.2 SKMCompetitor Class Tests (22 Tests)

| Test No. | Test Description                                   | Input / Operation                        | Expected Output                                      | Result |
|---------|---------------------------------------------------|----------------------------------------|-----------------------------------------------------|--------|
| 7       | getCompetitorId returns correct ID                 | SKMCompetitor(200, name, "Beginner")    | 200                                                  | PASS   |
| 8       | getLevel returns correct level                     | SKMCompetitor(200, name, "Beginner")    | "Beginner"                                           | PASS   |
| 9       | getName returns correct name                       | competitor.getCompetitorName()           | "Alice Green"                                        | PASS   |
| 10      | getCountry returns default empty                   | SKMCompetitor(200, name, "Beginner")    | ""                                                   | PASS   |
| 11      | getCountry with country constructor                | SKMCompetitor(210, name, "Advanced", "Nepal") | "Nepal"                                         | PASS   |
| 12      | setCountry updates correctly                       | competitor.setCountry("UK")              | "UK"                                                 | PASS   |
| 13      | getScoreArray returns correct length               | scores = {4,3,5,2,4}                    | 5                                                    | PASS   |
| 14      | getScoreArray returns correct values               | scores = {4,3,5,2,4}                    | [4, 3, 5, 2, 4]                                     | PASS   |
| 15      | getOverallScore computes average                   | scores = {4,3,5,2,4}                    | 3.6                                                  | PASS   |
| 16      | getFullDetails contains ID                         | competitor with ID 200                   | contains "200"                                       | PASS   |
| 17      | getFullDetails contains name                       | competitor Alice Green                   | contains "Alice Green"                               | PASS   |
| 18      | getFullDetails contains level                      | competitor Beginner                      | contains "Beginner"                                  | PASS   |
| 19      | getFullDetails contains score                      | overall score 3.6                        | contains "3.6"                                       | PASS   |
| 20      | getFullDetails contains country                    | country "Nepal"                          | contains "Nepal"                                     | PASS   |
| 21      | getShortDetails exact format                       | ID 200, initials AG, score 3.6           | "CN 200 (AG) has an overall score of 3.6."           | PASS   |
| 22      | getFullDetails uses gender-neutral pronoun         | any competitor                           | contains "This gives them"                           | PASS   |
| 23      | getScoresString formatted correctly                | scores = {4,3,5,2,4}                    | "4 3 5 2 4"                                          | PASS   |
| 24      | setCompetitorId updates correctly                  | competitor.setCompetitorId(201)           | 201                                                  | PASS   |
| 25      | setLevel updates correctly                         | competitor.setLevel("Advanced")          | "Advanced"                                           | PASS   |
| 26      | getOverallScore with zero included                 | scores = {0,4,4,4,4}                    | 3.2                                                  | PASS   |

### 4.3 CompetitorList Class Tests (14 Tests)

| Test No. | Test Description                                   | Input / Operation                        | Expected Output                                      | Result |
|---------|---------------------------------------------------|----------------------------------------|-----------------------------------------------------|--------|
| 27      | Empty list size                                    | new CompetitorList()                     | 0                                                    | PASS   |
| 28      | getNextId initial value                            | new CompetitorList()                     | 200                                                  | PASS   |
| 29      | List size after adding 3 competitors               | add 3 competitors                        | 3                                                    | PASS   |
| 30      | getCompetitorById found                            | search for ID 201                        | returns correct competitor                           | PASS   |
| 31      | getCompetitorById not found                        | search for ID 999                        | null                                                 | PASS   |
| 32      | getNextId after adding competitors                 | after adding IDs 200, 201, 202           | 203                                                  | PASS   |
| 33      | getTopPerformer returns highest scorer             | Carol White with score 4.6               | returns Carol White                                  | PASS   |
| 34      | Top performer score value                          | Carol White's score                      | 4.6                                                  | PASS   |
| 35      | Score frequency contains expected keys             | all scores across 3 competitors          | contains keys 4 and 5                                | PASS   |
| 36      | Generated report contains header                   | generateReport()                         | contains "Competitor ID"                             | PASS   |
| 37      | Generated report contains competitor name          | generateReport()                         | contains "Alice Green"                               | PASS   |
| 38      | Generated report contains statistics               | generateReport()                         | contains "Total number of competitors: 3"            | PASS   |
| 39      | removeCompetitorById success                       | remove ID 201                            | true                                                 | PASS   |
| 40      | List size after removal                            | after removing ID 201                    | 2                                                    | PASS   |
| 41      | Removed competitor no longer found                 | search for removed ID 201                | null                                                 | PASS   |
| 42      | removeCompetitorById for non-existent ID           | remove ID 999                            | false                                                | PASS   |

### 4.4 QuizData Tests (10 Tests)

| Test No. | Test Description                                   | Input / Operation                        | Expected Output                                      | Result |
|---------|---------------------------------------------------|----------------------------------------|-----------------------------------------------------|--------|
| 43      | Total number of questions                          | QuizData.getAllQuestions().size()         | 25                                                   | PASS   |
| 44      | Level 1 question count                             | QuizData.getQuestionsForLevel(1).size()  | 5                                                    | PASS   |
| 45      | Level 5 question count                             | QuizData.getQuestionsForLevel(5).size()  | 5                                                    | PASS   |
| 46      | Level name for level 1                             | QuizData.getLevelName(1)                 | "Beginner"                                           | PASS   |
| 47      | Level name for level 3                             | QuizData.getLevelName(3)                 | "Intermediate"                                       | PASS   |
| 48      | Level name for level 5                             | QuizData.getLevelName(5)                 | "Expert"                                             | PASS   |
| 49      | Question has non-empty text                        | first question's getQuestion()           | non-null and non-empty                               | PASS   |
| 50      | Question has 4 options                             | first question's getOptions().length     | 4                                                    | PASS   |
| 51      | Correct answer identified correctly                | isCorrect(correctAnswerIndex)            | true                                                 | PASS   |
| 52      | Wrong answer identified correctly                  | isCorrect(wrongIndex)                    | false                                                | PASS   |

### 4.5 Test Summary

| Category          | Tests | Passed | Failed |
|------------------|-------|--------|--------|
| Name Class        | 6     | 6      | 0      |
| SKMCompetitor     | 20    | 20     | 0      |
| CompetitorList    | 16    | 16     | 0      |
| QuizData          | 10    | 10     | 0      |
| **Total**         | **52**| **52** | **0**  |

All tests were executed using the `TestRunner` class with the command:

```bash
javac -d out src/quiz/*.java && java -cp out quiz.TestRunner
```

**Result: All 52 tests passed successfully with zero failures.**

---

*End of Report*
