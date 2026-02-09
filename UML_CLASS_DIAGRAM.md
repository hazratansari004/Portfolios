# UML Class Diagram

## Mermaid Code

Copy **everything** between the triple backticks into [Mermaid Live Editor](https://mermaid.live) to view the diagram.

```mermaid
classDiagram

    class Name {
        -String firstName
        -String middleName
        -String lastName
        +Name(firstName, lastName)
        +Name(firstName, middleName, lastName)
        +getFirstName() String
        +setFirstName(firstName)
        +getMiddleName() String
        +setMiddleName(middleName)
        +getLastName() String
        +setLastName(lastName)
        +getFullName() String
        +getInitials() String
        +toString() String
    }

    class SKMCompetitor {
        -int competitorId
        -Name competitorName
        -String level
        -String country
        -int[] scores
        +SKMCompetitor(id, name, level)
        +SKMCompetitor(id, name, level, scores)
        +SKMCompetitor(id, name, level, country)
        +SKMCompetitor(id, name, level, country, scores)
        +getCompetitorId() int
        +setCompetitorId(id)
        +getCompetitorName() Name
        +setCompetitorName(name)
        +getLevel() String
        +setLevel(level)
        +getCountry() String
        +setCountry(country)
        +getScoreArray() int[]
        +setScores(scores)
        +setScore(index, value)
        +getOverallScore() double
        +getFullDetails() String
        +getShortDetails() String
        +getScoresString() String
        +saveToDatabase(conn) boolean
        +updateInDatabase(conn) boolean
        +deleteFromDatabase(conn) boolean
        +readFromDatabase(conn, id) SKMCompetitor
        +readAllFromDatabase(conn) List
    }

    class QuizQuestion {
        -String question
        -String[] options
        -int correctAnswerIndex
        +QuizQuestion(question, optA, optB, optC, optD, correct)
        +getQuestion() String
        +getOptions() String[]
        +getCorrectAnswerIndex() int
        +isCorrect(selectedIndex) boolean
    }

    class QuizData {
        +getAllQuestions() List
        +getQuestionsForLevel(level) List
        +getLevelName(level) String
    }

    class CompetitorList {
        -List competitors
        -DatabaseConnection dbConnection
        -boolean dbConnected
        +CompetitorList()
        +initDatabase() boolean
        -loadFromDatabase()
        +refreshFromDatabase()
        +isDatabaseConnected() boolean
        +addCompetitor(c)
        +getCompetitors() List
        +getCompetitorById(id) SKMCompetitor
        +removeCompetitorById(id) boolean
        +getTopPerformer() SKMCompetitor
        +getTotalCompetitors() int
        +getScoreFrequency() Map
        +generateReport() String
        +getNextId() int
        +closeDatabase()
    }

    class DatabaseConnection {
        -Connection connection
        -String DB_URL
        -String DB_USER
        -String DB_PASSWORD
        +getStaticConnection() Connection
        +DatabaseConnection()
        +connect() boolean
        +getConnection() Connection
        +isConnected() boolean
        +createTable() boolean
        +insertCompetitor(c) boolean
        +getAllCompetitors() List
        +getCompetitorById(id) SKMCompetitor
        +deleteCompetitor(id) boolean
        +updateCompetitor(c) boolean
        +closeConnection()
        -resultSetToCompetitor(rs) SKMCompetitor
    }

    class JFrame {
        <<Java Swing>>
    }

    class Manager {
        -CompetitorList competitorList
        -CardLayout cardLayout
        -JPanel mainPanel
        -JTextField txtFirstName
        -JTextField txtMiddleName
        -JTextField txtLastName
        -JTextField txtCountry
        -JComboBox cmbLevel
        -JLabel lblQuestion
        -JLabel lblTimer
        -JLabel lblProgress
        -JLabel lblScore
        -JRadioButton[] optionButtons
        -ButtonGroup optionGroup
        -JButton btnNext
        -SKMCompetitor currentCompetitor
        -int currentLevel
        -int currentQuestionInLevel
        -int levelCorrect
        -int totalCorrect
        -int[] levelScores
        -Timer questionTimer
        -int timeRemaining
        +Manager()
        -createWelcomePanel() JPanel
        -createRegistrationPanel() JPanel
        -createQuizPanel() JPanel
        -startQuiz()
        -loadQuestion()
        -handleAnswer()
        -showLevelComplete()
        -finishQuiz()
        -showManageCompetitors()
        -showReportPanel()
        -createCompetitorTablePanel() JPanel
        -createTopPerformerPanel() JPanel
        -createStatisticsPanel() JPanel
        -createSearchPanel() JPanel
        -createFullReportPanel() JPanel
        -createStyledButton(text, bg) JButton
        +main(args)
    }

    SKMCompetitor *-- Name : has-a
    CompetitorList *-- SKMCompetitor : manages
    CompetitorList o-- DatabaseConnection : uses
    Manager *-- CompetitorList : contains
    Manager o-- SKMCompetitor : currentCompetitor
    Manager --|> JFrame : extends
    QuizData ..> QuizQuestion : creates
    DatabaseConnection ..> SKMCompetitor : reads-writes
```

## Associations Key

| Relationship | Type | Description |
|---|---|---|
| `SKMCompetitor` → `Name` | **Composition** | Each competitor has exactly one Name object |
| `CompetitorList` → `SKMCompetitor` | **Composition** | List manages zero or more competitors |
| `CompetitorList` → `DatabaseConnection` | **Aggregation** | List optionally uses a database connection |
| `Manager` → `CompetitorList` | **Composition** | Manager contains one competitor list |
| `Manager` → `SKMCompetitor` | **Aggregation** | Manager tracks current competitor during quiz |
| `Manager` → `JFrame` | **Inheritance** | Manager extends JFrame |
| `QuizData` → `QuizQuestion` | **Dependency** | QuizData creates QuizQuestion instances |
| `DatabaseConnection` → `SKMCompetitor` | **Dependency** | DatabaseConnection reads/writes competitors |
