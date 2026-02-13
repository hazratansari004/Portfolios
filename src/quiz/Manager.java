package quiz; // declares this class belongs to the "quiz" package

import javax.swing.*; // imports all Swing GUI components (JFrame, JPanel, JButton, etc.)
import javax.swing.border.EmptyBorder; // imports EmptyBorder for adding padding around components
import javax.swing.border.LineBorder; // imports LineBorder for drawing line borders around components
import javax.swing.table.DefaultTableModel; // imports table model used to populate JTable with data
import java.awt.*; // imports AWT classes for layout, color, font, cursor, etc.
import java.awt.event.ActionEvent; // imports ActionEvent used in timer and button event handling
import java.awt.event.ActionListener; // imports ActionListener interface for handling button clicks
import java.util.List; // imports List interface for storing collections of quiz questions
import java.util.Map; // imports Map interface for key-value pairs (score frequency)
import java.util.TreeMap; // imports TreeMap which sorts keys automatically (for sorted score display)

/**
 * Main Quiz Application with Java Swing GUI.
 * Acts as both the quiz runner and competition manager.
 * 25 questions across 5 levels, each question worth 4%.
 * Scores are recorded per level (Score1-Score5), and results are
 * displayed in a competitor report with statistics.
 */
public class Manager extends JFrame { // main class that extends JFrame to create the GUI window

    private CompetitorList competitorList; // stores and manages all competitors
    private CardLayout cardLayout; // layout manager that switches between different panels (screens)
    private JPanel mainPanel; // the container panel that holds all switchable screens

    // Registration panel components
    private JTextField txtFirstName, txtMiddleName, txtLastName, txtCountry; // text fields for user input on registration
    private JComboBox<String> cmbLevel; // dropdown to select difficulty level

    // Quiz panel components
    private JLabel lblLevel, lblQuestion, lblTimer, lblProgress, lblScore; // labels showing quiz info
    private JRadioButton[] optionButtons; // array of 4 radio buttons for answer choices
    private ButtonGroup optionGroup; // groups radio buttons so only one can be selected at a time
    private JButton btnNext; // button to submit answer and go to next question

    // Quiz state
    private SKMCompetitor currentCompetitor; // the competitor currently taking the quiz
    private int currentLevel; // current attempt/level number (1-5)
    private int currentQuestionInLevel; // which question within the current level (0-4)
    private int levelCorrect; // number of correct answers in the current level
    private int totalCorrect; // total correct answers across all levels
    private int[] levelScores; // array storing the score for each of the 5 levels
    private Timer questionTimer; // Swing timer that counts down seconds per question
    private int timeRemaining; // seconds left for the current question
    private static final int TIME_PER_QUESTION = 15; // constant: 15 seconds allowed per question
    private static final int ADVANCED_THRESHOLD = 80; // minimum % to be classified as Advanced
    private static final int INTERMEDIATE_THRESHOLD = 50; // minimum % to be classified as Intermediate

    // Color scheme - clean white theme
    private static final Color BG_WHITE = Color.WHITE; // main background color
    private static final Color TEXT_DARK = new Color(33, 33, 33); // dark color for primary text
    private static final Color TEXT_SECONDARY = new Color(100, 100, 100); // gray color for secondary text
    private static final Color ACCENT_BLUE = new Color(41, 128, 185); // blue used for action buttons
    private static final Color ACCENT_GREEN = new Color(39, 174, 96); // green used for success/start buttons
    private static final Color ACCENT_RED = new Color(192, 57, 43); // red used for delete/warning elements
    private static final Color BORDER_LIGHT = new Color(220, 220, 220); // light gray for borders
    private static final Color BG_LIGHT_GRAY = new Color(245, 245, 245); // very light gray for bottom bars

    public Manager() { // constructor - sets up the entire application
        competitorList = new CompetitorList(); // create a new list to hold all competitors
        levelScores = new int[5]; // initialize array for 5 level scores

        // Try to connect to MySQL database
        boolean dbOk = competitorList.initDatabase(); // attempt database connection
        if (dbOk) { // if connection succeeded
            System.out.println("Connected to CompetitionDB database successfully."); // print success message
        } else { // if connection failed
            System.out.println("Database not available. Running in offline mode (data stored in memory only)."); // print fallback message
        }

        setTitle("Quiz Competition Management"); // set the window title
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // dispose window resources on close
        // Use a modest default size; user can maximize manually
        setSize(800, 600); // set initial window size to 800x600 pixels
        setLocationRelativeTo(null); // center the window on screen
        // Allow window to be resized so it can be maximized
        setResizable(true); // allow the user to resize the window

        cardLayout = new CardLayout(); // create CardLayout to switch between screens
        mainPanel = new JPanel(cardLayout); // create main panel using CardLayout

        mainPanel.add(createWelcomePanel(), "WELCOME"); // add the welcome screen with key "WELCOME"
        mainPanel.add(createRegistrationPanel(), "REGISTER"); // add registration screen with key "REGISTER"
        mainPanel.add(createQuizPanel(), "QUIZ"); // add quiz screen with key "QUIZ"
        // Level complete and report panels are created dynamically

        getContentPane().add(mainPanel); // add the main panel to the JFrame's content pane
        cardLayout.show(mainPanel, "WELCOME"); // show the welcome screen first

        // Close database connection on window close
        addWindowListener(new java.awt.event.WindowAdapter() { // add a listener for window events
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) { // called when window is closed
                competitorList.closeDatabase(); // close the database connection
                System.exit(0); // terminate the application
            }
        });
    }

    // ==================== WELCOME PANEL ====================
    private JPanel createWelcomePanel() { // creates and returns the welcome screen panel
        JPanel panel = new JPanel(new BorderLayout()); // create panel with BorderLayout
        panel.setBackground(BG_WHITE); // set background to white

        JPanel center = new JPanel(); // create center container panel
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS)); // stack components vertically
        center.setBackground(BG_WHITE); // set background to white
        center.setBorder(new EmptyBorder(60, 50, 50, 50)); // add padding around the center panel

        JLabel title = new JLabel("Welcome to Quiz Competition Management"); // create title label
        title.setFont(new Font("Arial", Font.BOLD, 32)); // set large bold font for title
        title.setForeground(TEXT_DARK); // set title text color to dark
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // center the title horizontally

        JLabel subtitle = new JLabel("Test your knowledge across 5 attempts!"); // create subtitle label
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16)); // set medium font for subtitle
        subtitle.setForeground(TEXT_SECONDARY); // set subtitle text color to gray
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT); // center the subtitle horizontally

        // create info label with HTML formatting for multi-line quiz details
        JLabel info = new JLabel(
        	    "<html><center>" +
        	    "25 Questions<br>" +
        	    "5 Attempts<br>" +
        	    "15 seconds per question<br>" +
        	    "Each question is worth 4% of your total score" +
        	    "</center></html>"
        	);

        	info.setFont(new Font("Arial", Font.PLAIN, 13)); // set small font for info text
        	info.setHorizontalAlignment(JLabel.CENTER); // center the info text horizontally
        	info.setAlignmentX(Component.CENTER_ALIGNMENT); // center the label within the layout


        // Separator
        JSeparator sep = new JSeparator(); // create a horizontal line separator
        sep.setMaximumSize(new Dimension(400, 1)); // limit separator width to 400 pixels
        sep.setAlignmentX(Component.CENTER_ALIGNMENT); // center the separator

        JButton btnStart = new JButton("Start Quiz"); // create the "Start Quiz" button
        btnStart.setFont(new Font("Arial", Font.BOLD, 15)); // set bold font for button text
        btnStart.setBackground(ACCENT_GREEN); // set green background color
        btnStart.setForeground(Color.WHITE); // set white text color
        btnStart.setFocusPainted(false); // remove focus highlight border
        btnStart.setBorderPainted(false); // remove default button border
        btnStart.setOpaque(true); // make background color visible
        btnStart.setCursor(new Cursor(Cursor.HAND_CURSOR)); // show hand cursor on hover
        btnStart.setPreferredSize(new Dimension(250, 42)); // set preferred button size
        btnStart.setMaximumSize(new Dimension(300, 42)); // set maximum button size
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT); // center the button
        // when clicked, switch to the registration screen
        btnStart.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));

        JButton btnManageCompetitors = new JButton("Manage Competitors"); // create manage button
        btnManageCompetitors.setFont(new Font("Arial", Font.BOLD, 15)); // set bold font
        btnManageCompetitors.setBackground(ACCENT_BLUE); // set blue background
        btnManageCompetitors.setForeground(Color.WHITE); // set white text
        btnManageCompetitors.setFocusPainted(false); // remove focus highlight
        btnManageCompetitors.setBorderPainted(false); // remove border
        btnManageCompetitors.setOpaque(true); // make background visible
        btnManageCompetitors.setCursor(new Cursor(Cursor.HAND_CURSOR)); // hand cursor on hover
        btnManageCompetitors.setPreferredSize(new Dimension(250, 42)); // set preferred size
        btnManageCompetitors.setMaximumSize(new Dimension(300, 42)); // set max size
        btnManageCompetitors.setAlignmentX(Component.CENTER_ALIGNMENT); // center the button
        // when clicked, open the manage competitors screen
        btnManageCompetitors.addActionListener(e -> showManageCompetitors());

        JButton btnReports = new JButton("Reports"); // create reports button
        btnReports.setFont(new Font("Arial", Font.BOLD, 15)); // set bold font
        btnReports.setBackground(TEXT_DARK); // set dark background
        btnReports.setForeground(Color.WHITE); // set white text
        btnReports.setFocusPainted(false); // remove focus highlight
        btnReports.setBorderPainted(false); // remove border
        btnReports.setOpaque(true); // make background visible
        btnReports.setCursor(new Cursor(Cursor.HAND_CURSOR)); // hand cursor on hover
        btnReports.setPreferredSize(new Dimension(250, 42)); // set preferred size
        btnReports.setMaximumSize(new Dimension(300, 42)); // set max size
        btnReports.setAlignmentX(Component.CENTER_ALIGNMENT); // center the button
        // when clicked, open the reports screen
        btnReports.addActionListener(e -> showReportPanel());

        center.add(title); // add title to center panel
        center.add(Box.createVerticalStrut(10)); // add 10px vertical space
        center.add(subtitle); // add subtitle below title
        center.add(Box.createVerticalStrut(8)); // add 8px vertical space
        center.add(info); // add info label below subtitle
        center.add(Box.createVerticalStrut(20)); // add 20px vertical space
        center.add(sep); // add horizontal separator line
        center.add(Box.createVerticalStrut(30)); // add 30px vertical space
        center.add(btnStart); // add Start Quiz button
        center.add(Box.createVerticalStrut(10)); // add 10px vertical space
        center.add(btnManageCompetitors); // add Manage Competitors button
        center.add(Box.createVerticalStrut(10)); // add 10px vertical space
        center.add(btnReports); // add Reports button

        // Database status indicator
        center.add(Box.createVerticalStrut(15)); // add 15px vertical space before status
        String dbStatus = competitorList.isDatabaseConnected() // check if DB is connected
                ? "Database: Connected (MySQL)" // show connected status
                : "Database: Offline (In-Memory)"; // show offline status

        panel.add(center, BorderLayout.CENTER); // add center panel to the main panel's center
        return panel; // return the completed welcome panel
    }

    // ==================== REGISTRATION PANEL ====================
    private JPanel createRegistrationPanel() { // creates the registration form screen
        JPanel panel = new JPanel(new BorderLayout()); // create panel with BorderLayout
        panel.setBackground(BG_WHITE); // set white background

        JLabel header = new JLabel("Competitor Registration", SwingConstants.CENTER); // create header label
        header.setFont(new Font("Arial", Font.BOLD, 26)); // set large bold font
        header.setForeground(TEXT_DARK); // set dark text color
        header.setBorder(new EmptyBorder(30, 0, 20, 0)); // add top and bottom padding
        panel.add(header, BorderLayout.NORTH); // place header at the top of the panel

        JPanel form = new JPanel(new GridBagLayout()); // create form panel with GridBagLayout for grid alignment
        form.setBackground(BG_WHITE); // set white background

        // First Name label
        JLabel lblFirstName = new JLabel("First Name:"); // create label for first name field
        lblFirstName.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        lblFirstName.setForeground(TEXT_DARK); // set text color
        GridBagConstraints gbcLblFirstName = new GridBagConstraints(); // create layout constraints
        gbcLblFirstName.insets = new Insets(8, 10, 8, 10); // set padding around the label
        gbcLblFirstName.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcLblFirstName.gridx = 0; // place in column 0
        gbcLblFirstName.gridy = 0; // place in row 0
        form.add(lblFirstName, gbcLblFirstName); // add label to form at specified position

        // First Name field
        // Use explicit JTextField instantiation instead of createStyledTextField
        txtFirstName = new JTextField(20); // create text field with 20 columns width
        txtFirstName.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        txtFirstName.setBorder(BorderFactory.createCompoundBorder( // create compound border
                new LineBorder(BORDER_LIGHT, 1, true), // outer: thin rounded line border
                new EmptyBorder(5, 8, 5, 8))); // inner: padding inside the text field
        GridBagConstraints gbcTxtFirstName = new GridBagConstraints(); // create layout constraints
        gbcTxtFirstName.insets = new Insets(8, 10, 8, 10); // set padding
        gbcTxtFirstName.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcTxtFirstName.gridx = 1; // place in column 1
        gbcTxtFirstName.gridy = 0; // place in row 0
        form.add(txtFirstName, gbcTxtFirstName); // add text field to form

        // Middle Name label
        JLabel lblMiddleName = new JLabel("Middle Name:"); // create label for middle name field
        lblMiddleName.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        lblMiddleName.setForeground(TEXT_DARK); // set text color
        GridBagConstraints gbcLblMiddleName = new GridBagConstraints(); // create layout constraints
        gbcLblMiddleName.insets = new Insets(8, 10, 8, 10); // set padding
        gbcLblMiddleName.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcLblMiddleName.gridx = 0; // place in column 0
        gbcLblMiddleName.gridy = 1; // place in row 1
        form.add(lblMiddleName, gbcLblMiddleName); // add label to form

        // Middle Name field
        // Use explicit JTextField instantiation instead of createStyledTextField
        txtMiddleName = new JTextField(20); // create text field with 20 columns width
        txtMiddleName.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        txtMiddleName.setBorder(BorderFactory.createCompoundBorder( // create compound border
                new LineBorder(BORDER_LIGHT, 1, true), // outer: thin rounded line border
                new EmptyBorder(5, 8, 5, 8))); // inner: padding inside the text field
        GridBagConstraints gbcTxtMiddleName = new GridBagConstraints(); // create layout constraints
        gbcTxtMiddleName.insets = new Insets(8, 10, 8, 10); // set padding
        gbcTxtMiddleName.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcTxtMiddleName.gridx = 1; // place in column 1
        gbcTxtMiddleName.gridy = 1; // place in row 1
        form.add(txtMiddleName, gbcTxtMiddleName); // add text field to form

        // Last Name label
        JLabel lblLastName = new JLabel("Last Name:"); // create label for last name field
        lblLastName.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        lblLastName.setForeground(TEXT_DARK); // set text color
        GridBagConstraints gbcLblLastName = new GridBagConstraints(); // create layout constraints
        gbcLblLastName.insets = new Insets(8, 10, 8, 10); // set padding
        gbcLblLastName.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcLblLastName.gridx = 0; // place in column 0
        gbcLblLastName.gridy = 2; // place in row 2
        form.add(lblLastName, gbcLblLastName); // add label to form

        // Last Name field
        // Use explicit JTextField instantiation instead of createStyledTextField
        txtLastName = new JTextField(20); // create text field with 20 columns width
        txtLastName.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        txtLastName.setBorder(BorderFactory.createCompoundBorder( // create compound border
                new LineBorder(BORDER_LIGHT, 1, true), // outer: thin rounded line border
                new EmptyBorder(5, 8, 5, 8))); // inner: padding inside the text field
        GridBagConstraints gbcTxtLastName = new GridBagConstraints(); // create layout constraints
        gbcTxtLastName.insets = new Insets(8, 10, 8, 10); // set padding
        gbcTxtLastName.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcTxtLastName.gridx = 1; // place in column 1
        gbcTxtLastName.gridy = 2; // place in row 2
        form.add(txtLastName, gbcTxtLastName); // add text field to form

        // Country label
        JLabel lblCountry = new JLabel("Country:"); // create label for country field
        lblCountry.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        lblCountry.setForeground(TEXT_DARK); // set text color
        GridBagConstraints gbcLblCountry = new GridBagConstraints(); // create layout constraints
        gbcLblCountry.insets = new Insets(8, 10, 8, 10); // set padding
        gbcLblCountry.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcLblCountry.gridx = 0; // place in column 0
        gbcLblCountry.gridy = 3; // place in row 3
        form.add(lblCountry, gbcLblCountry); // add label to form

        // Country field
        // Use explicit JTextField instantiation instead of createStyledTextField
        txtCountry = new JTextField(20); // create text field with 20 columns width
        txtCountry.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        txtCountry.setBorder(BorderFactory.createCompoundBorder( // create compound border
                new LineBorder(BORDER_LIGHT, 1, true), // outer: thin rounded line border
                new EmptyBorder(5, 8, 5, 8))); // inner: padding inside the text field
        GridBagConstraints gbcTxtCountry = new GridBagConstraints(); // create layout constraints
        gbcTxtCountry.insets = new Insets(8, 10, 8, 10); // set padding
        gbcTxtCountry.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcTxtCountry.gridx = 1; // place in column 1
        gbcTxtCountry.gridy = 3; // place in row 3
        form.add(txtCountry, gbcTxtCountry); // add text field to form

        // Attempt label (was 'Level:')
        JLabel lblLevel = new JLabel("Level:"); // create label for level selection
        lblLevel.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        lblLevel.setForeground(TEXT_DARK); // set text color
        GridBagConstraints gbcLblLevel = new GridBagConstraints(); // create layout constraints
        gbcLblLevel.insets = new Insets(8, 10, 8, 10); // set padding
        gbcLblLevel.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcLblLevel.gridx = 0; // place in column 0
        gbcLblLevel.gridy = 4; // place in row 4
        form.add(lblLevel, gbcLblLevel); // add label to form

        // Level combo box: only show Beginner / Intermediate / Advanced for registration
        // create dropdown with three difficulty options
        cmbLevel = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Advanced"});
        cmbLevel.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        cmbLevel.setBackground(BG_WHITE); // set white background
        GridBagConstraints gbcCmbLevel = new GridBagConstraints(); // create layout constraints
        gbcCmbLevel.insets = new Insets(8, 10, 8, 10); // set padding
        gbcCmbLevel.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcCmbLevel.gridx = 1; // place in column 1
        gbcCmbLevel.gridy = 4; // place in row 4
        form.add(cmbLevel, gbcCmbLevel); // add dropdown to form

        // Begin Quiz button
        JButton btnRegister = new JButton("Begin Quiz"); // create the Begin Quiz button
        btnRegister.setFont(new Font("Arial", Font.BOLD, 15)); // set bold font
        btnRegister.setBackground(ACCENT_GREEN); // set green background
        btnRegister.setForeground(Color.WHITE); // set white text
        btnRegister.setFocusPainted(false); // remove focus highlight
        btnRegister.setBorderPainted(false); // remove border
        btnRegister.setOpaque(true); // make background visible
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR)); // hand cursor on hover
        btnRegister.setPreferredSize(new Dimension(250, 42)); // set preferred size
        btnRegister.setMaximumSize(new Dimension(300, 42)); // set max size
        GridBagConstraints gbcBtnRegister = new GridBagConstraints(); // create layout constraints
        gbcBtnRegister.insets = new Insets(8, 10, 8, 10); // set padding
        gbcBtnRegister.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcBtnRegister.gridx = 0; // start in column 0
        gbcBtnRegister.gridy = 5; // place in row 5
        gbcBtnRegister.gridwidth = 2; // span across 2 columns
        form.add(btnRegister, gbcBtnRegister); // add button to form
        btnRegister.addActionListener(e -> startQuiz()); // when clicked, start the quiz

        // Back button
        JButton btnBack = new JButton("Back"); // create the Back button
        btnBack.setFont(new Font("Arial", Font.BOLD, 15)); // set bold font
        btnBack.setBackground(TEXT_SECONDARY); // set gray background
        btnBack.setForeground(Color.WHITE); // set white text
        btnBack.setFocusPainted(false); // remove focus highlight
        btnBack.setBorderPainted(false); // remove border
        btnBack.setOpaque(true); // make background visible
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR)); // hand cursor on hover
        btnBack.setPreferredSize(new Dimension(250, 42)); // set preferred size
        btnBack.setMaximumSize(new Dimension(300, 42)); // set max size
        GridBagConstraints gbcBtnBack = new GridBagConstraints(); // create layout constraints
        gbcBtnBack.insets = new Insets(8, 10, 8, 10); // set padding
        gbcBtnBack.fill = GridBagConstraints.HORIZONTAL; // stretch horizontally
        gbcBtnBack.gridx = 0; // start in column 0
        gbcBtnBack.gridy = 6; // place in row 6
        gbcBtnBack.gridwidth = 2; // span across 2 columns
        form.add(btnBack, gbcBtnBack); // add button to form
        // when clicked, go back to the welcome screen
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));

        panel.add(form, BorderLayout.CENTER); // add form to the center of the panel
        return panel; // return the completed registration panel
    }

    private JTextField createStyledTextField(int columns) { // helper method to create a styled text field
        JTextField tf = new JTextField(columns); // create text field with given column width
        tf.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        tf.setBorder(BorderFactory.createCompoundBorder( // create compound border
                new LineBorder(BORDER_LIGHT, 1, true), // outer: thin rounded line border
                new EmptyBorder(5, 8, 5, 8))); // inner: padding inside the text field
        return tf; // return the styled text field
    }

    // ==================== QUIZ PANEL ====================
    private JPanel createQuizPanel() { // creates the quiz question screen
        JPanel panel = new JPanel(new BorderLayout()); // create panel with BorderLayout
        panel.setBackground(BG_WHITE); // set white background

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout()); // create top bar panel
        topBar.setBackground(BG_WHITE); // set white background
        topBar.setBorder(BorderFactory.createCompoundBorder( // create compound border
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT), // bottom border line only
                new EmptyBorder(12, 20, 12, 20))); // inner padding

        // Show only the attempt number in the quiz header (no level name)
        lblLevel = new JLabel("Attempt 1"); // label showing current attempt number
        lblLevel.setFont(new Font("Arial", Font.BOLD, 16)); // set bold font
        lblLevel.setForeground(TEXT_DARK); // set dark text color

        lblTimer = new JLabel("Time: 15s"); // label showing countdown timer
        lblTimer.setFont(new Font("Arial", Font.BOLD, 16)); // set bold font
        lblTimer.setForeground(TEXT_DARK); // set dark text color

        lblProgress = new JLabel("Q1/25 (0%)"); // label showing question progress
        lblProgress.setFont(new Font("Arial", Font.BOLD, 13)); // set bold font
        lblProgress.setForeground(TEXT_SECONDARY); // set gray text color
        lblProgress.setHorizontalAlignment(SwingConstants.CENTER); // center text in label

        topBar.add(lblLevel, BorderLayout.WEST); // place attempt label on the left
        topBar.add(lblProgress, BorderLayout.CENTER); // place progress label in the center
        topBar.add(lblTimer, BorderLayout.EAST); // place timer label on the right
        panel.add(topBar, BorderLayout.NORTH); // add top bar to the top of the panel

        // Question area
        JPanel questionArea = new JPanel(); // create panel for question and options
        questionArea.setLayout(new BoxLayout(questionArea, BoxLayout.Y_AXIS)); // stack vertically
        questionArea.setBackground(BG_WHITE); // set white background
        questionArea.setBorder(new EmptyBorder(30, 50, 20, 50)); // add padding around question area

        lblQuestion = new JLabel("Question text here"); // label to display the question text
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 18)); // set large bold font
        lblQuestion.setForeground(TEXT_DARK); // set dark text color
        lblQuestion.setAlignmentX(Component.LEFT_ALIGNMENT); // align left
        questionArea.add(lblQuestion); // add question label to panel
        questionArea.add(Box.createVerticalStrut(25)); // add 25px space below question

        optionButtons = new JRadioButton[4]; // create array for 4 answer radio buttons
        optionGroup = new ButtonGroup(); // create button group to allow only one selection
        for (int i = 0; i < 4; i++) { // loop to create each of the 4 option buttons
            optionButtons[i] = new JRadioButton("Option " + (i + 1)); // create radio button with placeholder text
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
            optionButtons[i].setBackground(BG_WHITE); // set white background
            optionButtons[i].setForeground(TEXT_DARK); // set dark text color
            optionButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT); // align left
            optionGroup.add(optionButtons[i]); // add to button group for mutual exclusion
            questionArea.add(optionButtons[i]); // add radio button to question area
            questionArea.add(Box.createVerticalStrut(10)); // add 10px space between options
        }

        panel.add(questionArea, BorderLayout.CENTER); // add question area to center of panel

        // Bottom bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER)); // create bottom bar with centered flow
        bottomBar.setBackground(BG_LIGHT_GRAY); // set light gray background
        bottomBar.setBorder(new EmptyBorder(10, 0, 10, 0)); // add vertical padding

        lblScore = new JLabel("Score: 0%"); // label showing current total score
        lblScore.setFont(new Font("Arial", Font.BOLD, 15)); // set bold font
        lblScore.setForeground(TEXT_DARK); // set dark text color

        btnNext = createStyledButton("Next", ACCENT_BLUE); // create blue "Next" button
        btnNext.addActionListener(e -> handleAnswer()); // when clicked, submit the selected answer

        bottomBar.add(lblScore); // add score label to bottom bar
        bottomBar.add(Box.createHorizontalStrut(30)); // add 30px horizontal space
        bottomBar.add(btnNext); // add Next button to bottom bar

        panel.add(bottomBar, BorderLayout.SOUTH); // add bottom bar to the bottom of the panel

        return panel; // return the completed quiz panel
    }

    // ==================== QUIZ LOGIC ====================
    private void startQuiz() { // called when user clicks "Begin Quiz" on registration form
        String firstName = txtFirstName.getText().trim(); // get first name from text field
        String middleName = txtMiddleName.getText().trim(); // get middle name from text field
        String lastName = txtLastName.getText().trim(); // get last name from text field
        String country = txtCountry.getText().trim(); // get country from text field
        String selectedAttemptOrLevel = (String) cmbLevel.getSelectedItem(); // get selected level from dropdown

        if (firstName.isEmpty() || lastName.isEmpty()) { // check if required fields are empty
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (First Name, Last Name).",
                    "Validation Error", JOptionPane.ERROR_MESSAGE); // show error dialog
            return; // stop and don't proceed
        }

        Name name; // declare a Name object
        if (middleName.isEmpty()) { // if no middle name was entered
            name = new Name(firstName, lastName); // create Name with first and last only
        } else { // if middle name was entered
            name = new Name(firstName, middleName, lastName); // create Name with all three parts
        }
        int id = competitorList.getNextId(); // get the next available competitor ID

        // Determine internal level number (1..5) from registration selection.
        // Registration shows only Beginner / Intermediate / Advanced. Map as:
        // Beginner -> 1, Intermediate -> 3, Advanced -> 4 (matches QuizData names).
        int attemptNumber = 1; // default attempt number is 1
        if (selectedAttemptOrLevel != null) { // if a level was selected
            String s = selectedAttemptOrLevel.trim(); // trim whitespace from selection
            // Backwards-compatible: allow values like "Attempt N"
            if (s.toLowerCase().startsWith("attempt")) { // check if selection starts with "attempt"
                try { // try to parse the attempt number
                    String digits = s.replaceAll("\\D+", ""); // extract only digits
                    if (!digits.isEmpty()) attemptNumber = Integer.parseInt(digits); // parse the number
                } catch (NumberFormatException ex) { // if parsing fails
                    attemptNumber = 1; // default to attempt 1
                }
            } else { // if selection is a level name
                // Map level name to internal level index
                if (s.equalsIgnoreCase("Beginner")) attemptNumber = 1; // Beginner maps to level 1
                else if (s.equalsIgnoreCase("Intermediate")) attemptNumber = 3; // Intermediate maps to level 3
                else if (s.equalsIgnoreCase("Advanced")) attemptNumber = 4; // Advanced maps to level 4
                else attemptNumber = 1; // default to level 1
            }
            if (attemptNumber < 1) attemptNumber = 1; // clamp minimum to 1
            if (attemptNumber > 5) attemptNumber = 5; // clamp maximum to 5
        }
        String selectedLevel = QuizData.getLevelName(attemptNumber); // get the level name for this attempt number

        currentCompetitor = new SKMCompetitor(id, name, selectedLevel, country); // create new competitor object
        currentLevel = attemptNumber; // set the starting level
        currentQuestionInLevel = 0; // start at the first question in the level
        levelCorrect = 0; // reset correct count for current level
        totalCorrect = 0; // reset total correct count
        levelScores = new int[5]; // reset all level scores to zero

        cardLayout.show(mainPanel, "QUIZ"); // switch to the quiz screen
        loadQuestion(); // load the first question
    }

    private void loadQuestion() { // loads the current question and displays it on screen
        int globalIndex = (currentLevel - 1) * 5 + currentQuestionInLevel; // calculate global question number (0-24)
        List<QuizQuestion> levelQuestions = QuizData.getQuestionsForLevel(currentLevel); // get questions for current level
        QuizQuestion q = levelQuestions.get(currentQuestionInLevel); // get the specific question object

        // Display only the attempt number while playing (no level name)
        lblLevel.setText("Attempt " + currentLevel); // update attempt label
        // set question text with HTML wrapping to handle long text
        lblQuestion.setText("<html><body style='width:600px'>" + (globalIndex + 1) + ". " + q.getQuestion() + "</body></html>");

        String[] opts = q.getOptions(); // get the 4 answer options for this question
        for (int i = 0; i < 4; i++) { // loop through each option button
            optionButtons[i].setText(opts[i]); // set the option text
            optionButtons[i].setEnabled(true); // enable the radio button
        }
        optionGroup.clearSelection(); // clear any previously selected option
        btnNext.setEnabled(true); // enable the Next button

        int percent = globalIndex * 4; // calculate progress percentage (each question = 4%)
        lblProgress.setText("Q" + (globalIndex + 1) + "/25 (" + percent + "%)"); // update progress label
        lblScore.setText("Score: " + (totalCorrect * 4) + "%"); // update score label

        // Start timer
        timeRemaining = TIME_PER_QUESTION; // reset timer to 15 seconds
        lblTimer.setText("Time: " + timeRemaining + "s"); // display initial time
        lblTimer.setForeground(TEXT_DARK); // set timer text to normal dark color
        if (questionTimer != null) { // if a previous timer exists
            questionTimer.stop(); // stop it
        }
        questionTimer = new Timer(1000, new ActionListener() { // create new timer that fires every 1 second
            @Override
            public void actionPerformed(ActionEvent e) { // called every second
                timeRemaining--; // decrease time by 1 second
                lblTimer.setText("Time: " + timeRemaining + "s"); // update timer display
                if (timeRemaining <= 5) { // if 5 or fewer seconds remain
                    lblTimer.setForeground(ACCENT_RED); // change timer color to red as warning
                } else { // if more than 5 seconds remain
                    lblTimer.setForeground(TEXT_DARK); // keep timer color dark
                }
                if (timeRemaining <= 0) { // if time has run out
                    questionTimer.stop(); // stop the timer
                    handleAnswer(); // auto-submit the answer (time expired)
                }
            }
        });
        questionTimer.start(); // start the countdown timer
    }

    private void handleAnswer() { // called when user clicks Next or time runs out
        if (questionTimer != null) { // if timer is running
            questionTimer.stop(); // stop the timer
        }

        // Check if an answer was selected
        int selected = -1; // -1 means no answer selected
        for (int i = 0; i < 4; i++) { // loop through all 4 option buttons
            if (optionButtons[i].isSelected()) { // check if this button is selected
                selected = i; // store the selected option index
                break; // stop checking once found
            }
        }

        // User must select an answer before proceeding (unless time ran out)
        if (selected == -1 && timeRemaining > 0) { // if no answer selected and time hasn't expired
            JOptionPane.showMessageDialog(this,
                    "Please select an answer before proceeding.",
                    "No Answer Selected", JOptionPane.WARNING_MESSAGE); // show warning dialog
            // Restart the timer since we stopped it
            if (questionTimer != null) { // if timer exists
                questionTimer.start(); // restart it
            }
            return; // don't proceed further
        }

        List<QuizQuestion> levelQuestions = QuizData.getQuestionsForLevel(currentLevel); // get current level's questions
        QuizQuestion q = levelQuestions.get(currentQuestionInLevel); // get the current question

        if (selected >= 0 && q.isCorrect(selected)) { // if an answer was selected and it is correct
            levelCorrect++; // increment level correct count
            totalCorrect++; // increment total correct count
        }

        // Disable options after answering
        for (int i = 0; i < 4; i++) { // loop through all option buttons
            optionButtons[i].setEnabled(false); // disable each button to prevent changes
        }

        currentQuestionInLevel++; // move to the next question in the level

        if (currentQuestionInLevel >= 5) { // if all 5 questions in this level are done
            // Level complete
            levelScores[currentLevel - 1] = levelCorrect; // save the level score
            showLevelComplete(); // show the level completion screen
        } else { // if more questions remain in this level
            loadQuestion(); // load the next question
        }
    }

    private void showLevelComplete() { // displays the screen shown after completing a level
        // Remove old level-complete panel if it exists
        for (Component c : mainPanel.getComponents()) { // loop through all panels in mainPanel
            if ("LEVEL_COMPLETE".equals(c.getName())) { // find the old level-complete panel
                mainPanel.remove(c); // remove it to avoid duplicates
                break; // stop searching
            }
        }

        JPanel panel = new JPanel(); // create a new panel for level completion
        panel.setName("LEVEL_COMPLETE"); // set name so it can be found and replaced later
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // stack components vertically
        panel.setBackground(BG_WHITE); // set white background
        panel.setBorder(new EmptyBorder(80, 50, 50, 50)); // add generous padding

        JLabel lblDone = new JLabel("Attempt " + currentLevel + " Completed!"); // label showing level done
        lblDone.setFont(new Font("Arial", Font.BOLD, 32)); // set large bold font
        lblDone.setForeground(ACCENT_GREEN); // set green color for success
        lblDone.setAlignmentX(Component.CENTER_ALIGNMENT); // center the label

        JLabel lblLevelScore = new JLabel("You got " + levelCorrect + " out of 5 correct"); // show level score
        lblLevelScore.setFont(new Font("Arial", Font.PLAIN, 20)); // set medium font
        lblLevelScore.setForeground(TEXT_DARK); // set dark text color
        lblLevelScore.setAlignmentX(Component.CENTER_ALIGNMENT); // center the label

        JLabel lblTotal = new JLabel("Total progress: " + (totalCorrect * 4) + "%"); // show total % progress
        lblTotal.setFont(new Font("Arial", Font.PLAIN, 16)); // set font size
        lblTotal.setForeground(TEXT_SECONDARY); // set gray text color
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT); // center the label

        panel.add(Box.createVerticalStrut(30)); // add 30px vertical space
        panel.add(lblDone); // add "Completed!" label
        panel.add(Box.createVerticalStrut(20)); // add 20px vertical space
        panel.add(lblLevelScore); // add level score label
        panel.add(Box.createVerticalStrut(10)); // add 10px vertical space
        panel.add(lblTotal); // add total progress label
        panel.add(Box.createVerticalStrut(40)); // add 40px vertical space

        if (currentLevel < 5) { // if there are more levels to play
            // Only show the next attempt number (no level name)
            JLabel lblNext = new JLabel("Next: Attempt " + (currentLevel + 1)); // show next attempt info
            lblNext.setFont(new Font("Arial", Font.ITALIC, 15)); // set italic font
            lblNext.setForeground(TEXT_SECONDARY); // set gray text color
            lblNext.setAlignmentX(Component.CENTER_ALIGNMENT); // center the label
            panel.add(lblNext); // add next attempt label
            panel.add(Box.createVerticalStrut(20)); // add 20px vertical space

            // create button to continue to next attempt
            JButton btnContinue = createStyledButton("Continue to Attempt " + (currentLevel + 1), ACCENT_BLUE);
            btnContinue.setAlignmentX(Component.CENTER_ALIGNMENT); // center the button
            btnContinue.addActionListener(e -> { // when clicked, proceed to next level
                currentLevel++; // increment to next level
                currentQuestionInLevel = 0; // reset question counter
                levelCorrect = 0; // reset level correct count
                cardLayout.show(mainPanel, "QUIZ"); // switch back to quiz screen
                loadQuestion(); // load first question of new level
            });
            panel.add(btnContinue); // add continue button
        } else { // if this was the last level (level 5)
            // Quiz finished
            JLabel lblFinished = new JLabel("Quiz Complete! Final Score: " + (totalCorrect * 4) + "%"); // show final score
            lblFinished.setFont(new Font("Arial", Font.BOLD, 20)); // set bold font
            lblFinished.setForeground(ACCENT_GREEN); // set green color
            lblFinished.setAlignmentX(Component.CENTER_ALIGNMENT); // center the label
            panel.add(lblFinished); // add finished label
            panel.add(Box.createVerticalStrut(20)); // add 20px vertical space

            JButton btnFinish = createStyledButton("View Results", ACCENT_BLUE); // create View Results button
            btnFinish.setAlignmentX(Component.CENTER_ALIGNMENT); // center the button
            btnFinish.addActionListener(e -> finishQuiz()); // when clicked, finish quiz and show results
            panel.add(btnFinish); // add finish button
        }

        mainPanel.add(panel, "LEVEL_COMPLETE"); // add panel to CardLayout with key "LEVEL_COMPLETE"
        cardLayout.show(mainPanel, "LEVEL_COMPLETE"); // switch to show the level complete screen
    }

    private void finishQuiz() { // called when all 25 questions are completed
        // Determine level based on total score
        String level; // variable to hold the final level classification
        int totalPercent = totalCorrect * 4; // calculate total percentage score
        if (totalPercent >= ADVANCED_THRESHOLD) level = "Advanced"; // 80%+ = Advanced
        else if (totalPercent >= INTERMEDIATE_THRESHOLD) level = "Intermediate"; // 50-79% = Intermediate
        else level = "Beginner"; // below 50% = Beginner

        currentCompetitor.setLevel(level); // set the competitor's final level
        currentCompetitor.setScores(levelScores); // save all 5 level scores to competitor
        competitorList.addCompetitor(currentCompetitor); // add competitor to the list (and database)

        showReportPanel(); // navigate to the report screen
    }

    // ==================== MANAGE COMPETITORS ====================
    private void showManageCompetitors() { // displays the competitor management screen
        // Remove old panel
        for (Component c : mainPanel.getComponents()) { // loop through existing panels
            if ("MANAGE".equals(c.getName())) { // find the old manage panel
                mainPanel.remove(c); // remove it to refresh data
                break; // stop searching
            }
        }

        JPanel panel = new JPanel(new BorderLayout()); // create panel with BorderLayout
        panel.setName("MANAGE"); // set name for identification
        panel.setBackground(BG_WHITE); // set white background

        JLabel header = new JLabel("Manage Competitors", SwingConstants.CENTER); // create header label
        header.setFont(new Font("Arial", Font.BOLD, 26)); // set large bold font
        header.setForeground(TEXT_DARK); // set dark text color
        header.setBorder(new EmptyBorder(15, 0, 10, 0)); // add top and bottom padding
        panel.add(header, BorderLayout.NORTH); // place header at the top

        // Table
        JPanel tableWrapper = new JPanel(new BorderLayout()); // create wrapper panel for the table
        tableWrapper.setBackground(BG_WHITE); // set white background
        tableWrapper.setBorder(new EmptyBorder(10, 15, 10, 15)); // add padding around table

        // define column names for the competitor table
        String[] columns = {"ID", "Name", "Level", "Country", "Score1", "Score2", "Score3", "Score4", "Score5", "Overall"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { // create table model with no rows
            @Override
            public boolean isCellEditable(int row, int col) { return false; } // make all cells read-only
        };

        for (SKMCompetitor c : competitorList.getCompetitors()) { // loop through all competitors
            int[] scores = c.getScoreArray(); // get the competitor's scores array
            model.addRow(new Object[]{ // add a row with competitor data
                c.getCompetitorId(), // column: ID
                c.getCompetitorName().getFullName(), // column: full name
                c.getLevel(), // column: level
                c.getCountry(), // column: country
                scores[0], scores[1], scores[2], scores[3], scores[4], // columns: 5 individual scores
                c.getOverallScore() // column: overall score
            });
        }

        JTable table = new JTable(model); // create JTable with the populated model
        table.setFont(new Font("Arial", Font.PLAIN, 13)); // set table font
        table.setRowHeight(26); // set row height in pixels
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13)); // set header font to bold
        table.setGridColor(BORDER_LIGHT); // set grid line color
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); // allow only one row selection

        if (competitorList.getTotalCompetitors() == 0) { // if there are no competitors
            JLabel empty = new JLabel("No competitors yet. Take the quiz first!", SwingConstants.CENTER); // show message
            empty.setFont(new Font("Arial", Font.ITALIC, 15)); // set italic font
            empty.setForeground(TEXT_SECONDARY); // set gray color
            tableWrapper.add(empty, BorderLayout.CENTER); // add message to center
        } else { // if there are competitors
            tableWrapper.add(new JScrollPane(table), BorderLayout.CENTER); // add scrollable table
        }

        panel.add(tableWrapper, BorderLayout.CENTER); // add table wrapper to center of panel

        // Bottom bar with action buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8)); // create bottom bar with centered flow
        bottom.setBackground(BG_LIGHT_GRAY); // set light gray background
        bottom.setBorder(new EmptyBorder(8, 0, 8, 0)); // add vertical padding

        JButton btnRefresh = createStyledButton("Refresh", ACCENT_BLUE); // create blue Refresh button
        btnRefresh.setPreferredSize(new Dimension(140, 42)); // set preferred size
        btnRefresh.setMaximumSize(new Dimension(140, 42)); // set max size
        btnRefresh.addActionListener(e -> showManageCompetitors()); // when clicked, reload this screen

        JButton btnSearch = createStyledButton("Search", ACCENT_GREEN); // create green Search button
        btnSearch.setPreferredSize(new Dimension(140, 42)); // set preferred size
        btnSearch.setMaximumSize(new Dimension(140, 42)); // set max size
        btnSearch.addActionListener(e -> { // when clicked, search for a competitor
            String input = JOptionPane.showInputDialog(this,
                    "Enter Competitor ID to search:", "Search Competitor", JOptionPane.QUESTION_MESSAGE); // show input dialog
            if (input == null || input.trim().isEmpty()) return; // if cancelled or empty, do nothing
            try { // try to parse the ID
                int id = Integer.parseInt(input.trim()); // parse the entered ID as integer
                SKMCompetitor found = competitorList.getCompetitorById(id); // search for competitor by ID
                if (found != null) { // if competitor was found
                    StringBuilder sb = new StringBuilder(); // build details string
                    sb.append("Full Details:\n"); // add section header
                    sb.append(found.getFullDetails()); // add full details
                    sb.append("\n\nShort Details:\n"); // add section header
                    sb.append(found.getShortDetails()); // add short details
                    JOptionPane.showMessageDialog(this, sb.toString(),
                            "Competitor Found", JOptionPane.INFORMATION_MESSAGE); // show details dialog
                    // Highlight the row in the table
                    for (int row = 0; row < model.getRowCount(); row++) { // loop through table rows
                        if ((int) model.getValueAt(row, 0) == id) { // find the matching row
                            table.setRowSelectionInterval(row, row); // select that row
                            table.scrollRectToVisible(table.getCellRect(row, 0, true)); // scroll to make it visible
                            break; // stop searching
                        }
                    }
                } else { // if competitor was not found
                    JOptionPane.showMessageDialog(this,
                            "No competitor found with ID " + id + ".",
                            "Not Found", JOptionPane.WARNING_MESSAGE); // show not found dialog
                }
            } catch (NumberFormatException ex) { // if ID is not a valid number
                JOptionPane.showMessageDialog(this,
                        "Invalid ID. Please enter a numeric value.",
                        "Error", JOptionPane.ERROR_MESSAGE); // show error dialog
            }
        });

        JButton btnDelete = createStyledButton("Delete", ACCENT_RED); // create red Delete button
        btnDelete.setPreferredSize(new Dimension(140, 42)); // set preferred size
        btnDelete.setMaximumSize(new Dimension(140, 42)); // set max size
        btnDelete.addActionListener(e -> { // when clicked, delete the selected competitor
            int selectedRow = table.getSelectedRow(); // get the currently selected row
            if (selectedRow == -1) { // if no row is selected
                JOptionPane.showMessageDialog(this,
                        "Please select a competitor from the table to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE); // show warning
                return; // stop here
            }
            int id = (int) model.getValueAt(selectedRow, 0); // get the ID from the selected row
            String name = (String) model.getValueAt(selectedRow, 1); // get the name from the selected row
            int confirm = JOptionPane.showConfirmDialog(this, // ask for confirmation
                    "Are you sure you want to delete competitor " + name + " (ID: " + id + ")?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) { // if user confirmed deletion
                competitorList.removeCompetitorById(id); // remove competitor from list and database
                showManageCompetitors(); // refresh the manage screen
            }
        });

        JButton btnBack = createStyledButton("Back", TEXT_DARK); // create dark Back button
        btnBack.setPreferredSize(new Dimension(140, 42)); // set preferred size
        btnBack.setMaximumSize(new Dimension(140, 42)); // set max size
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME")); // when clicked, go to welcome screen

        bottom.add(btnRefresh); // add Refresh button to bottom bar
        bottom.add(btnSearch); // add Search button to bottom bar
        bottom.add(btnDelete); // add Delete button to bottom bar
        bottom.add(btnBack); // add Back button to bottom bar

        panel.add(bottom, BorderLayout.SOUTH); // add bottom bar to the south of the panel

        mainPanel.add(panel, "MANAGE"); // add panel to CardLayout with key "MANAGE"
        cardLayout.show(mainPanel, "MANAGE"); // switch to show the manage screen
    }

    // ==================== REPORT PANEL ====================
    private void showReportPanel() { // displays the reports screen with tabbed sections
        // Remove old report panel
        for (Component c : mainPanel.getComponents()) { // loop through existing panels
            if ("REPORT".equals(c.getName())) { // find the old report panel
                mainPanel.remove(c); // remove it to refresh data
                break; // stop searching
            }
        }

        JPanel panel = new JPanel(new BorderLayout()); // create panel with BorderLayout
        panel.setName("REPORT"); // set name for identification
        panel.setBackground(BG_WHITE); // set white background

        // Header
        JLabel header = new JLabel("Reports", SwingConstants.CENTER); // create header label
        header.setFont(new Font("Arial", Font.BOLD, 26)); // set large bold font
        header.setForeground(TEXT_DARK); // set dark text color
        header.setBorder(new EmptyBorder(15, 0, 10, 0)); // add vertical padding
        panel.add(header, BorderLayout.NORTH); // place header at the top

        // Tabbed pane for sections
        JTabbedPane tabs = new JTabbedPane(); // create tabbed pane to hold multiple report views
        tabs.setFont(new Font("Arial", Font.PLAIN, 13)); // set tab label font
        tabs.setBackground(BG_WHITE); // set white background

        // Tab 1: Competitor Table
        tabs.addTab("Competitors", createCompetitorTablePanel()); // add competitor table tab

        // Tab 2: Top Performer
        tabs.addTab("Top Performer", createTopPerformerPanel()); // add top performer tab

        // Tab 3: Statistics
        tabs.addTab("Statistics", createStatisticsPanel()); // add statistics tab

        // Tab 4: Search Competitor
        tabs.addTab("Search", createSearchPanel()); // add search tab

        // Tab 5: Generate Full Report
        tabs.addTab("Full Report", createFullReportPanel()); // add full report tab

        panel.add(tabs, BorderLayout.CENTER); // add tabbed pane to center of panel

        // Bottom bar
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER)); // create bottom bar with centered layout
        bottom.setBackground(BG_LIGHT_GRAY); // set light gray background
        bottom.setBorder(new EmptyBorder(8, 0, 8, 0)); // add vertical padding

        JButton btnHome = createStyledButton("Home", TEXT_DARK); // create dark Home button
        btnHome.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME")); // when clicked, go to welcome screen
        bottom.add(btnHome); // add Home button to bottom bar

        panel.add(bottom, BorderLayout.SOUTH); // add bottom bar to the south

        mainPanel.add(panel, "REPORT"); // add panel to CardLayout with key "REPORT"
        cardLayout.show(mainPanel, "REPORT"); // switch to show the report screen
    }

    private JPanel createCompetitorTablePanel() { // creates the competitor table tab content
        JPanel panel = new JPanel(new BorderLayout()); // create panel with BorderLayout
        panel.setBackground(BG_WHITE); // set white background
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // add padding

        // define column names for the table
        String[] columns = {"ID", "Name", "Level", "Country", "Score1", "Score2", "Score3", "Score4", "Score5", "Overall"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { // create table model with no initial rows
            @Override
            public boolean isCellEditable(int row, int col) { return false; } // make all cells read-only
        };

        for (SKMCompetitor c : competitorList.getCompetitors()) { // loop through all competitors
            int[] scores = c.getScoreArray(); // get the scores array for this competitor
            model.addRow(new Object[]{ // add a row with all competitor data
                c.getCompetitorId(), // column: ID
                c.getCompetitorName().getFullName(), // column: full name
                c.getLevel(), // column: level
                c.getCountry(), // column: country
                scores[0], scores[1], scores[2], scores[3], scores[4], // columns: 5 level scores
                c.getOverallScore() // column: overall score
            });
        }

        JTable table = new JTable(model); // create JTable with the data model
        table.setFont(new Font("Arial", Font.PLAIN, 13)); // set table font
        table.setRowHeight(26); // set row height in pixels
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13)); // set bold header font
        table.setGridColor(BORDER_LIGHT); // set grid line color

        if (competitorList.getTotalCompetitors() == 0) { // if no competitors exist
            JLabel empty = new JLabel("No competitors yet. Take the quiz first!", SwingConstants.CENTER); // show message
            empty.setFont(new Font("Arial", Font.ITALIC, 15)); // set italic font
            empty.setForeground(TEXT_SECONDARY); // set gray text color
            panel.add(empty, BorderLayout.CENTER); // add message to center
        } else { // if competitors exist
            panel.add(new JScrollPane(table), BorderLayout.CENTER); // add scrollable table to center
        }

        return panel; // return the completed table panel
    }

    private JPanel createTopPerformerPanel() { // creates the top performer tab content
        JPanel panel = new JPanel(); // create a new panel
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // stack components vertically
        panel.setBackground(BG_WHITE); // set white background
        panel.setBorder(new EmptyBorder(25, 25, 25, 25)); // add padding

        SKMCompetitor top = competitorList.getTopPerformer(); // get the competitor with the highest score
        if (top != null) { // if at least one competitor exists
            JLabel lblTitle = new JLabel("Top Performer"); // create title label
            lblTitle.setFont(new Font("Arial", Font.BOLD, 22)); // set bold font
            lblTitle.setForeground(ACCENT_GREEN); // set green color
            lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT); // align left
            panel.add(lblTitle); // add title to panel
            panel.add(Box.createVerticalStrut(15)); // add 15px vertical space

            JTextArea txtDetails = new JTextArea(top.getFullDetails()); // create text area with full details
            txtDetails.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
            txtDetails.setForeground(TEXT_DARK); // set dark text color
            txtDetails.setEditable(false); // make text area read-only
            txtDetails.setLineWrap(true); // enable line wrapping
            txtDetails.setWrapStyleWord(true); // wrap at word boundaries
            txtDetails.setBackground(BG_WHITE); // set white background
            txtDetails.setAlignmentX(Component.LEFT_ALIGNMENT); // align left
            panel.add(txtDetails); // add text area to panel
        } else { // if no competitors exist
            JLabel empty = new JLabel("No competitors yet."); // show empty message
            empty.setFont(new Font("Arial", Font.ITALIC, 15)); // set italic font
            empty.setForeground(TEXT_SECONDARY); // set gray color
            panel.add(empty); // add message to panel
        }

        return panel; // return the completed panel
    }

    private JPanel createStatisticsPanel() { // creates the statistics tab content
        JPanel panel = new JPanel(); // create a new panel
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // stack components vertically
        panel.setBackground(BG_WHITE); // set white background
        panel.setBorder(new EmptyBorder(25, 25, 25, 25)); // add padding

        JLabel lblTitle = new JLabel("Statistical Summary"); // create title label
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22)); // set bold font
        lblTitle.setForeground(TEXT_DARK); // set dark text color
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT); // align left
        panel.add(lblTitle); // add title to panel
        panel.add(Box.createVerticalStrut(15)); // add 15px vertical space

        StringBuilder sb = new StringBuilder(); // create string builder for statistics text
        sb.append("Total number of competitors: ").append(competitorList.getTotalCompetitors()).append("\n\n"); // add total count

        SKMCompetitor top = competitorList.getTopPerformer(); // get the top scorer
        if (top != null) { // if a top performer exists
            sb.append("Competitor with the highest score: ")
              .append(top.getCompetitorName().getFullName()) // append their name
              .append(" with an overall score of ").append(top.getOverallScore()).append("\n\n"); // append their score
        }

        // Frequency of individual scores
        Map<Integer, Integer> freq = competitorList.getScoreFrequency(); // get score frequency map
        TreeMap<Integer, Integer> sorted = new TreeMap<>(freq); // sort frequencies by score value
        sb.append("Frequency of individual scores:\n"); // add section header
        sb.append("Score:     "); // add score row header
        for (int key : sorted.keySet()) { // loop through each score value
            sb.append(String.format("%-6d", key)); // append score value, left-aligned with padding
        }
        sb.append("\nFrequency: "); // add frequency row header
        for (int key : sorted.keySet()) { // loop through each score value
            sb.append(String.format("%-6d", sorted.get(key))); // append frequency count, left-aligned
        }

        JTextArea txtStats = new JTextArea(sb.toString()); // create text area with statistics text
        txtStats.setFont(new Font("Monospaced", Font.PLAIN, 13)); // set monospaced font for alignment
        txtStats.setForeground(TEXT_DARK); // set dark text color
        txtStats.setEditable(false); // make text area read-only
        txtStats.setBackground(BG_WHITE); // set white background
        txtStats.setAlignmentX(Component.LEFT_ALIGNMENT); // align left
        panel.add(txtStats); // add text area to panel

        return panel; // return the completed statistics panel
    }

    // ==================== SEARCH PANEL ====================
    private JPanel createSearchPanel() { // creates the search tab content
        JPanel panel = new JPanel(new BorderLayout()); // create panel with BorderLayout
        panel.setBackground(BG_WHITE); // set white background
        panel.setBorder(new EmptyBorder(20, 20, 20, 20)); // add padding

        // Search bar at top
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER)); // create search bar with centered flow
        searchBar.setBackground(BG_WHITE); // set white background

        JLabel lblSearch = new JLabel("Search by ID or Name:"); // create search label
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 15)); // set font size
        lblSearch.setForeground(TEXT_DARK); // set dark text color

        JTextField txtSearch = createStyledTextField(18); // create styled text field with 18 columns

        JButton btnSearch = createStyledButton("Search", ACCENT_BLUE); // create blue Search button
        btnSearch.setPreferredSize(new Dimension(150, 42)); // set preferred size
        btnSearch.setMaximumSize(new Dimension(150, 42)); // set max size

        searchBar.add(lblSearch); // add search label to bar
        searchBar.add(Box.createHorizontalStrut(8)); // add 8px horizontal space
        searchBar.add(txtSearch); // add text field to bar
        searchBar.add(Box.createHorizontalStrut(8)); // add 8px horizontal space
        searchBar.add(btnSearch); // add search button to bar

        panel.add(searchBar, BorderLayout.NORTH); // place search bar at the top

        // Results area
        JTextArea txtResults = new JTextArea("Enter an ID or name and click Search."); // create results text area
        txtResults.setFont(new Font("Arial", Font.PLAIN, 14)); // set font size
        txtResults.setForeground(TEXT_SECONDARY); // set gray placeholder text color
        txtResults.setEditable(false); // make read-only
        txtResults.setLineWrap(true); // enable line wrapping
        txtResults.setWrapStyleWord(true); // wrap at word boundaries
        txtResults.setBackground(BG_WHITE); // set white background
        txtResults.setBorder(new EmptyBorder(15, 10, 10, 10)); // add padding inside text area

        JScrollPane scrollResults = new JScrollPane(txtResults); // wrap text area in scroll pane
        scrollResults.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT)); // add top border only
        panel.add(scrollResults, BorderLayout.CENTER); // place scroll pane in center

        btnSearch.addActionListener(e -> { // when Search button is clicked
            String query = txtSearch.getText().trim(); // get search query text
            if (query.isEmpty()) { // if query is empty
                txtResults.setText("Please enter an ID or name to search."); // show prompt message
                txtResults.setForeground(TEXT_SECONDARY); // set gray color
                return; // stop here
            }

            StringBuilder result = new StringBuilder(); // build result text
            boolean found = false; // flag to track if any match was found

            // Try search by ID
            try { // try to parse query as a number
                int id = Integer.parseInt(query); // parse the query as integer ID
                SKMCompetitor c = competitorList.getCompetitorById(id); // search for competitor by ID
                if (c != null) { // if competitor was found
                    result.append("=== Search Result for ID ").append(id).append(" ===\n\n"); // add header
                    result.append("Full Details:\n"); // add section label
                    result.append(c.getFullDetails()); // add full details
                    result.append("\n\nShort Details:\n"); // add section label
                    result.append(c.getShortDetails()); // add short details
                    found = true; // set found flag
                }
            } catch (NumberFormatException ex) { // if query is not a number
                // Not a number, search by name
            }

            // Search by name (partial match)
            if (!found) { // if not found by ID, try name search
                String lowerQuery = query.toLowerCase(); // convert query to lowercase for case-insensitive search
                for (SKMCompetitor c : competitorList.getCompetitors()) { // loop through all competitors
                    if (c.getCompetitorName().getFullName().toLowerCase().contains(lowerQuery)) { // check if name contains query
                        if (!found) { // if this is the first match
                            result.append("=== Search Results ===\n\n"); // add header
                        }
                        result.append("Full Details:\n"); // add section label
                        result.append(c.getFullDetails()); // add full details
                        result.append("\n\nShort Details:\n"); // add section label
                        result.append(c.getShortDetails()); // add short details
                        result.append("\n\n---\n\n"); // add separator between results
                        found = true; // set found flag
                    }
                }
            }

            if (!found) { // if no competitor was found
                txtResults.setText("No competitor found matching: " + query); // show not found message
                txtResults.setForeground(ACCENT_RED); // set red color for error
            } else { // if results were found
                txtResults.setText(result.toString()); // display the results
                txtResults.setForeground(TEXT_DARK); // set dark text color
                txtResults.setCaretPosition(0); // scroll to the top of the results
            }
        });

        return panel; // return the completed search panel
    }

    // ==================== FULL REPORT PANEL ====================
    private JPanel createFullReportPanel() { // creates the full report tab content
        JPanel panel = new JPanel(new BorderLayout()); // create panel with BorderLayout
        panel.setBackground(BG_WHITE); // set white background
        panel.setBorder(new EmptyBorder(15, 15, 15, 15)); // add padding

        JTextArea txtReport = new JTextArea(); // create text area for the full report
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 13)); // set monospaced font for aligned output
        txtReport.setForeground(TEXT_DARK); // set dark text color
        txtReport.setEditable(false); // make text area read-only
        txtReport.setBackground(BG_WHITE); // set white background

        if (competitorList.getTotalCompetitors() == 0) { // if no competitors exist
            txtReport.setText("No competitors yet. Take the quiz first to generate a report."); // show empty message
        } else { // if competitors exist
            txtReport.setText(competitorList.generateReport()); // generate and display the full report
        }

        JScrollPane scroll = new JScrollPane(txtReport); // wrap text area in scroll pane
        scroll.setBorder(new LineBorder(BORDER_LIGHT, 1)); // add thin border around scroll pane
        panel.add(scroll, BorderLayout.CENTER); // place scroll pane in center

        return panel; // return the completed full report panel
    }

    // ==================== UTILITY ====================
    private JButton createStyledButton(String text, Color bgColor) { // helper method to create a styled button
        JButton btn = new JButton(text); // create button with given text
        btn.setFont(new Font("Arial", Font.BOLD, 15)); // set bold font
        btn.setBackground(bgColor); // set the given background color
        btn.setForeground(Color.WHITE); // set white text color
        btn.setFocusPainted(false); // remove focus highlight border
        btn.setBorderPainted(false); // remove default border
        btn.setOpaque(true); // make background color visible
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // show hand cursor on hover
        btn.setPreferredSize(new Dimension(250, 42)); // set preferred button size
        btn.setMaximumSize(new Dimension(300, 42)); // set maximum button size
        return btn; // return the styled button
    }

    // ==================== MAIN ====================
    public static void main(String[] args) { // entry point of the application
        SwingUtilities.invokeLater(() -> { // run GUI creation on the Event Dispatch Thread (thread-safe)
            Manager app = new Manager(); // create a new Manager instance (builds the entire GUI)
            // Start with a small default window; user may maximize with window controls
            app.setVisible(true); // make the window visible on screen
        });
    }
}

