package quiz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Main Quiz Application with Java Swing GUI.
 * Acts as both the quiz runner and competition manager.
 * 25 questions across 5 levels, each question worth 4%.
 * Scores are recorded per level (Score1-Score5), and results are
 * displayed in a competitor report with statistics.
 */
public class Manager extends JFrame {

    private CompetitorList competitorList;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Registration panel components
    private JTextField txtFirstName, txtMiddleName, txtLastName, txtAge, txtCountry;
    private JComboBox<String> cmbLevel;

    // Quiz panel components
    private JLabel lblLevel, lblQuestion, lblTimer, lblProgress, lblScore;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionGroup;
    private JButton btnNext;

    // Quiz state
    private HACompetitor currentCompetitor;
    private int currentLevel;
    private int currentQuestionInLevel;
    private int levelCorrect;
    private int totalCorrect;
    private int[] levelScores;
    private Timer questionTimer;
    private int timeRemaining;
    private static final int TIME_PER_QUESTION = 15; // seconds per question
    private static final int ADVANCED_THRESHOLD = 80;
    private static final int INTERMEDIATE_THRESHOLD = 50;

    // Color scheme - clean white theme
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(100, 100, 100);
    private static final Color ACCENT_BLUE = new Color(41, 128, 185);
    private static final Color ACCENT_GREEN = new Color(39, 174, 96);
    private static final Color ACCENT_RED = new Color(192, 57, 43);
    private static final Color BORDER_LIGHT = new Color(220, 220, 220);
    private static final Color BG_LIGHT_GRAY = new Color(245, 245, 245);

    public Manager() {
        competitorList = new CompetitorList();
        levelScores = new int[5];

        setTitle("Quiz Competition Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createWelcomePanel(), "WELCOME");
        mainPanel.add(createRegistrationPanel(), "REGISTER");
        mainPanel.add(createQuizPanel(), "QUIZ");
        // Level complete and report panels are created dynamically

        add(mainPanel);
        cardLayout.show(mainPanel, "WELCOME");
    }

    // ==================== WELCOME PANEL ====================
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG_WHITE);
        center.setBorder(new EmptyBorder(60, 50, 50, 50));

        JLabel title = new JLabel("Welcome to Quiz Competition Management");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Test your knowledge across 5 levels!");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = new JLabel("<html><center>25 Questions | 5 Levels | 15 seconds per question<br>"
                + "Each question is worth 4% of your total score</center></html>");
        info.setFont(new Font("Arial", Font.PLAIN, 13));
        info.setForeground(TEXT_SECONDARY);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(400, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnStart = createStyledButton("Start Quiz", ACCENT_GREEN);
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStart.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));

        JButton btnViewAllCompetitors = createStyledButton("View All Competitors", ACCENT_BLUE);
        btnViewAllCompetitors.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnViewAllCompetitors.addActionListener(e -> showViewAllCompetitors());

        JButton btnReports = createStyledButton("Reports", TEXT_DARK);
        btnReports.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReports.addActionListener(e -> showReportPanel());

        center.add(title);
        center.add(Box.createVerticalStrut(10));
        center.add(subtitle);
        center.add(Box.createVerticalStrut(8));
        center.add(info);
        center.add(Box.createVerticalStrut(20));
        center.add(sep);
        center.add(Box.createVerticalStrut(30));
        center.add(btnStart);
        center.add(Box.createVerticalStrut(10));
        center.add(btnViewAllCompetitors);
        center.add(Box.createVerticalStrut(10));
        center.add(btnReports);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    // ==================== REGISTRATION PANEL ====================
    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);

        JLabel header = new JLabel("Competitor Registration", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 26));
        header.setForeground(TEXT_DARK);
        header.setBorder(new EmptyBorder(30, 0, 20, 0));
        panel.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_WHITE);

        int row = 0;

        // First Name
        addFormLabel(form, row, "First Name:");
        txtFirstName = createStyledTextField(20);
        form.add(txtFirstName, createGbc(1, row, 1));
        row++;

        // Middle Name
        addFormLabel(form, row, "Middle Name:");
        txtMiddleName = createStyledTextField(20);
        form.add(txtMiddleName, createGbc(1, row, 1));
        row++;

        // Last Name
        addFormLabel(form, row, "Last Name:");
        txtLastName = createStyledTextField(20);
        form.add(txtLastName, createGbc(1, row, 1));
        row++;

        // Age
        addFormLabel(form, row, "Age:");
        txtAge = createStyledTextField(20);
        form.add(txtAge, createGbc(1, row, 1));
        row++;

        // Country
        addFormLabel(form, row, "Country:");
        txtCountry = createStyledTextField(20);
        form.add(txtCountry, createGbc(1, row, 1));
        row++;

        // Level
        addFormLabel(form, row, "Level:");
        cmbLevel = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Advanced"});
        cmbLevel.setFont(new Font("Arial", Font.PLAIN, 15));
        cmbLevel.setBackground(BG_WHITE);
        form.add(cmbLevel, createGbc(1, row, 1));
        row++;

        // Begin Quiz button
        JButton btnRegister = createStyledButton("Begin Quiz", ACCENT_GREEN);
        form.add(btnRegister, createGbc(0, row, 2));
        btnRegister.addActionListener(e -> startQuiz());
        row++;

        // Back button
        JButton btnBack = createStyledButton("Back", TEXT_SECONDARY);
        form.add(btnBack, createGbc(0, row, 2));
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    /** Creates a new GridBagConstraints instance for each component. */
    private GridBagConstraints createGbc(int gridx, int gridy, int gridwidth) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        return gbc;
    }

    private void addFormLabel(JPanel form, int row, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 15));
        lbl.setForeground(TEXT_DARK);
        form.add(lbl, createGbc(0, row, 1));
    }

    private JTextField createStyledTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(new Font("Arial", Font.PLAIN, 15));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(5, 8, 5, 8)));
        return tf;
    }

    // ==================== QUIZ PANEL ====================
    private JPanel createQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                new EmptyBorder(12, 20, 12, 20)));

        lblLevel = new JLabel("Level 1: Beginner");
        lblLevel.setFont(new Font("Arial", Font.BOLD, 16));
        lblLevel.setForeground(TEXT_DARK);

        lblTimer = new JLabel("Time: 15s");
        lblTimer.setFont(new Font("Arial", Font.BOLD, 16));
        lblTimer.setForeground(TEXT_DARK);

        lblProgress = new JLabel("Q1/25 (0%)");
        lblProgress.setFont(new Font("Arial", Font.BOLD, 13));
        lblProgress.setForeground(TEXT_SECONDARY);
        lblProgress.setHorizontalAlignment(SwingConstants.CENTER);

        topBar.add(lblLevel, BorderLayout.WEST);
        topBar.add(lblProgress, BorderLayout.CENTER);
        topBar.add(lblTimer, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Question area
        JPanel questionArea = new JPanel();
        questionArea.setLayout(new BoxLayout(questionArea, BoxLayout.Y_AXIS));
        questionArea.setBackground(BG_WHITE);
        questionArea.setBorder(new EmptyBorder(30, 50, 20, 50));

        lblQuestion = new JLabel("Question text here");
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 18));
        lblQuestion.setForeground(TEXT_DARK);
        lblQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionArea.add(lblQuestion);
        questionArea.add(Box.createVerticalStrut(25));

        optionButtons = new JRadioButton[4];
        optionGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton("Option " + (i + 1));
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 15));
            optionButtons[i].setBackground(BG_WHITE);
            optionButtons[i].setForeground(TEXT_DARK);
            optionButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            optionGroup.add(optionButtons[i]);
            questionArea.add(optionButtons[i]);
            questionArea.add(Box.createVerticalStrut(10));
        }

        panel.add(questionArea, BorderLayout.CENTER);

        // Bottom bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomBar.setBackground(BG_LIGHT_GRAY);
        bottomBar.setBorder(new EmptyBorder(10, 0, 10, 0));

        lblScore = new JLabel("Score: 0%");
        lblScore.setFont(new Font("Arial", Font.BOLD, 15));
        lblScore.setForeground(TEXT_DARK);

        btnNext = createStyledButton("Next", ACCENT_BLUE);
        btnNext.addActionListener(e -> handleAnswer());

        bottomBar.add(lblScore);
        bottomBar.add(Box.createHorizontalStrut(30));
        bottomBar.add(btnNext);

        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== QUIZ LOGIC ====================
    private void startQuiz() {
        String firstName = txtFirstName.getText().trim();
        String middleName = txtMiddleName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String ageStr = txtAge.getText().trim();
        String country = txtCountry.getText().trim();
        String selectedLevel = (String) cmbLevel.getSelectedItem();

        if (firstName.isEmpty() || lastName.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (First Name, Last Name, Age).",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 1 || age > 120) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age (1-120).",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Name name;
        if (middleName.isEmpty()) {
            name = new Name(firstName, lastName);
        } else {
            name = new Name(firstName, middleName, lastName);
        }
        int id = competitorList.getNextId();
        currentCompetitor = new HACompetitor(id, name, selectedLevel, age, country);
        currentLevel = 1;
        currentQuestionInLevel = 0;
        levelCorrect = 0;
        totalCorrect = 0;
        levelScores = new int[5];

        cardLayout.show(mainPanel, "QUIZ");
        loadQuestion();
    }

    private void loadQuestion() {
        int globalIndex = (currentLevel - 1) * 5 + currentQuestionInLevel;
        List<QuizQuestion> levelQuestions = QuizData.getQuestionsForLevel(currentLevel);
        QuizQuestion q = levelQuestions.get(currentQuestionInLevel);

        lblLevel.setText("Level " + currentLevel + ": " + QuizData.getLevelName(currentLevel));
        lblQuestion.setText("<html><body style='width:600px'>" + (globalIndex + 1) + ". " + q.getQuestion() + "</body></html>");

        String[] opts = q.getOptions();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(opts[i]);
            optionButtons[i].setEnabled(true);
        }
        optionGroup.clearSelection();
        btnNext.setEnabled(true);

        int percent = globalIndex * 4;
        lblProgress.setText("Q" + (globalIndex + 1) + "/25 (" + percent + "%)");
        lblScore.setText("Score: " + (totalCorrect * 4) + "%");

        // Start timer
        timeRemaining = TIME_PER_QUESTION;
        lblTimer.setText("Time: " + timeRemaining + "s");
        lblTimer.setForeground(TEXT_DARK);
        if (questionTimer != null) {
            questionTimer.stop();
        }
        questionTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                lblTimer.setText("Time: " + timeRemaining + "s");
                if (timeRemaining <= 5) {
                    lblTimer.setForeground(ACCENT_RED);
                } else {
                    lblTimer.setForeground(TEXT_DARK);
                }
                if (timeRemaining <= 0) {
                    questionTimer.stop();
                    handleAnswer(); // Auto-submit when time is up
                }
            }
        });
        questionTimer.start();
    }

    private void handleAnswer() {
        if (questionTimer != null) {
            questionTimer.stop();
        }

        // Check if an answer was selected
        int selected = -1;
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                selected = i;
                break;
            }
        }

        // User must select an answer before proceeding (unless time ran out)
        if (selected == -1 && timeRemaining > 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an answer before proceeding.",
                    "No Answer Selected", JOptionPane.WARNING_MESSAGE);
            // Restart the timer since we stopped it
            if (questionTimer != null) {
                questionTimer.start();
            }
            return;
        }

        List<QuizQuestion> levelQuestions = QuizData.getQuestionsForLevel(currentLevel);
        QuizQuestion q = levelQuestions.get(currentQuestionInLevel);

        if (selected >= 0 && q.isCorrect(selected)) {
            levelCorrect++;
            totalCorrect++;
        }

        // Disable options after answering
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setEnabled(false);
        }

        currentQuestionInLevel++;

        if (currentQuestionInLevel >= 5) {
            // Level complete
            levelScores[currentLevel - 1] = levelCorrect;
            showLevelComplete();
        } else {
            loadQuestion();
        }
    }

    private void showLevelComplete() {
        // Remove old level-complete panel if it exists
        for (Component c : mainPanel.getComponents()) {
            if ("LEVEL_COMPLETE".equals(c.getName())) {
                mainPanel.remove(c);
                break;
            }
        }

        JPanel panel = new JPanel();
        panel.setName("LEVEL_COMPLETE");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(80, 50, 50, 50));

        JLabel lblDone = new JLabel("Level " + currentLevel + " Completed!");
        lblDone.setFont(new Font("Arial", Font.BOLD, 32));
        lblDone.setForeground(ACCENT_GREEN);
        lblDone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLevelScore = new JLabel("You got " + levelCorrect + " out of 5 correct");
        lblLevelScore.setFont(new Font("Arial", Font.PLAIN, 20));
        lblLevelScore.setForeground(TEXT_DARK);
        lblLevelScore.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTotal = new JLabel("Total progress: " + (totalCorrect * 4) + "%");
        lblTotal.setFont(new Font("Arial", Font.PLAIN, 16));
        lblTotal.setForeground(TEXT_SECONDARY);
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(30));
        panel.add(lblDone);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblLevelScore);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblTotal);
        panel.add(Box.createVerticalStrut(40));

        if (currentLevel < 5) {
            JLabel lblNext = new JLabel("Next: Level " + (currentLevel + 1) + " - " + QuizData.getLevelName(currentLevel + 1));
            lblNext.setFont(new Font("Arial", Font.ITALIC, 15));
            lblNext.setForeground(TEXT_SECONDARY);
            lblNext.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblNext);
            panel.add(Box.createVerticalStrut(20));

            JButton btnContinue = createStyledButton("Continue to Level " + (currentLevel + 1), ACCENT_BLUE);
            btnContinue.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnContinue.addActionListener(e -> {
                currentLevel++;
                currentQuestionInLevel = 0;
                levelCorrect = 0;
                cardLayout.show(mainPanel, "QUIZ");
                loadQuestion();
            });
            panel.add(btnContinue);
        } else {
            // Quiz finished
            JLabel lblFinished = new JLabel("Quiz Complete! Final Score: " + (totalCorrect * 4) + "%");
            lblFinished.setFont(new Font("Arial", Font.BOLD, 20));
            lblFinished.setForeground(ACCENT_GREEN);
            lblFinished.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblFinished);
            panel.add(Box.createVerticalStrut(20));

            JButton btnFinish = createStyledButton("View Results", ACCENT_BLUE);
            btnFinish.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnFinish.addActionListener(e -> finishQuiz());
            panel.add(btnFinish);
        }

        mainPanel.add(panel, "LEVEL_COMPLETE");
        cardLayout.show(mainPanel, "LEVEL_COMPLETE");
    }

    private void finishQuiz() {
        // Determine level based on total score
        String level;
        int totalPercent = totalCorrect * 4;
        if (totalPercent >= ADVANCED_THRESHOLD) level = "Advanced";
        else if (totalPercent >= INTERMEDIATE_THRESHOLD) level = "Intermediate";
        else level = "Beginner";

        currentCompetitor.setLevel(level);
        currentCompetitor.setScores(levelScores);
        competitorList.addCompetitor(currentCompetitor);

        showReportPanel();
    }

    // ==================== VIEW ALL COMPETITORS ====================
    private void showViewAllCompetitors() {
        // Remove old panel
        for (Component c : mainPanel.getComponents()) {
            if ("VIEW_ALL".equals(c.getName())) {
                mainPanel.remove(c);
                break;
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("VIEW_ALL");
        panel.setBackground(BG_WHITE);

        JLabel header = new JLabel("All Competitors", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 26));
        header.setForeground(TEXT_DARK);
        header.setBorder(new EmptyBorder(15, 0, 10, 0));
        panel.add(header, BorderLayout.NORTH);

        // Table
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(BG_WHITE);
        tableWrapper.setBorder(new EmptyBorder(10, 15, 10, 15));

        String[] columns = {"ID", "Name", "Level", "Country", "Age", "Score1", "Score2", "Score3", "Score4", "Score5", "Overall"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        for (HACompetitor c : competitorList.getCompetitors()) {
            int[] scores = c.getScoreArray();
            model.addRow(new Object[]{
                c.getCompetitorId(),
                c.getCompetitorName().getFullName(),
                c.getLevel(),
                c.getCountry(),
                c.getAge(),
                scores[0], scores[1], scores[2], scores[3], scores[4],
                c.getOverallScore()
            });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setGridColor(BORDER_LIGHT);

        if (competitorList.getTotalCompetitors() == 0) {
            JLabel empty = new JLabel("No competitors yet. Take the quiz first!", SwingConstants.CENTER);
            empty.setFont(new Font("Arial", Font.ITALIC, 15));
            empty.setForeground(TEXT_SECONDARY);
            tableWrapper.add(empty, BorderLayout.CENTER);
        } else {
            tableWrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        panel.add(tableWrapper, BorderLayout.CENTER);

        // Bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(BG_LIGHT_GRAY);
        bottom.setBorder(new EmptyBorder(8, 0, 8, 0));
        JButton btnHome = createStyledButton("Home", TEXT_DARK);
        btnHome.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));
        bottom.add(btnHome);

        panel.add(bottom, BorderLayout.SOUTH);

        mainPanel.add(panel, "VIEW_ALL");
        cardLayout.show(mainPanel, "VIEW_ALL");
    }

    // ==================== REPORT PANEL ====================
    private void showReportPanel() {
        // Remove old report panel
        for (Component c : mainPanel.getComponents()) {
            if ("REPORT".equals(c.getName())) {
                mainPanel.remove(c);
                break;
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("REPORT");
        panel.setBackground(BG_WHITE);

        // Header
        JLabel header = new JLabel("Reports", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 26));
        header.setForeground(TEXT_DARK);
        header.setBorder(new EmptyBorder(15, 0, 10, 0));
        panel.add(header, BorderLayout.NORTH);

        // Tabbed pane for sections
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 13));
        tabs.setBackground(BG_WHITE);

        // Tab 1: Competitor Table
        tabs.addTab("Competitors", createCompetitorTablePanel());

        // Tab 2: Top Performer
        tabs.addTab("Top Performer", createTopPerformerPanel());

        // Tab 3: Statistics
        tabs.addTab("Statistics", createStatisticsPanel());

        // Tab 4: Search Competitor
        tabs.addTab("Search", createSearchPanel());

        // Tab 5: Generate Full Report
        tabs.addTab("Full Report", createFullReportPanel());

        panel.add(tabs, BorderLayout.CENTER);

        // Bottom bar
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(BG_LIGHT_GRAY);
        bottom.setBorder(new EmptyBorder(8, 0, 8, 0));

        JButton btnHome = createStyledButton("Home", TEXT_DARK);
        btnHome.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));
        bottom.add(btnHome);

        panel.add(bottom, BorderLayout.SOUTH);

        mainPanel.add(panel, "REPORT");
        cardLayout.show(mainPanel, "REPORT");
    }

    private JPanel createCompetitorTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Name", "Level", "Country", "Score1", "Score2", "Score3", "Score4", "Score5", "Overall"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        for (HACompetitor c : competitorList.getCompetitors()) {
            int[] scores = c.getScoreArray();
            model.addRow(new Object[]{
                c.getCompetitorId(),
                c.getCompetitorName().getFullName(),
                c.getLevel(),
                c.getCountry(),
                scores[0], scores[1], scores[2], scores[3], scores[4],
                c.getOverallScore()
            });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setGridColor(BORDER_LIGHT);

        if (competitorList.getTotalCompetitors() == 0) {
            JLabel empty = new JLabel("No competitors yet. Take the quiz first!", SwingConstants.CENTER);
            empty.setFont(new Font("Arial", Font.ITALIC, 15));
            empty.setForeground(TEXT_SECONDARY);
            panel.add(empty, BorderLayout.CENTER);
        } else {
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createTopPerformerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        HACompetitor top = competitorList.getTopPerformer();
        if (top != null) {
            JLabel lblTitle = new JLabel("Top Performer");
            lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
            lblTitle.setForeground(ACCENT_GREEN);
            lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lblTitle);
            panel.add(Box.createVerticalStrut(15));

            JTextArea txtDetails = new JTextArea(top.getFullDetails());
            txtDetails.setFont(new Font("Arial", Font.PLAIN, 15));
            txtDetails.setForeground(TEXT_DARK);
            txtDetails.setEditable(false);
            txtDetails.setLineWrap(true);
            txtDetails.setWrapStyleWord(true);
            txtDetails.setBackground(BG_WHITE);
            txtDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(txtDetails);
        } else {
            JLabel empty = new JLabel("No competitors yet.");
            empty.setFont(new Font("Arial", Font.ITALIC, 15));
            empty.setForeground(TEXT_SECONDARY);
            panel.add(empty);
        }

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("Statistical Summary");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(15));

        StringBuilder sb = new StringBuilder();
        sb.append("Total number of competitors: ").append(competitorList.getTotalCompetitors()).append("\n\n");

        HACompetitor top = competitorList.getTopPerformer();
        if (top != null) {
            sb.append("Competitor with the highest score: ")
              .append(top.getCompetitorName().getFullName())
              .append(" with an overall score of ").append(top.getOverallScore()).append("\n\n");
        }

        // Frequency of individual scores
        Map<Integer, Integer> freq = competitorList.getScoreFrequency();
        TreeMap<Integer, Integer> sorted = new TreeMap<>(freq);
        sb.append("Frequency of individual scores:\n");
        sb.append("Score:     ");
        for (int key : sorted.keySet()) {
            sb.append(String.format("%-6d", key));
        }
        sb.append("\nFrequency: ");
        for (int key : sorted.keySet()) {
            sb.append(String.format("%-6d", sorted.get(key)));
        }

        JTextArea txtStats = new JTextArea(sb.toString());
        txtStats.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtStats.setForeground(TEXT_DARK);
        txtStats.setEditable(false);
        txtStats.setBackground(BG_WHITE);
        txtStats.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtStats);

        return panel;
    }

    // ==================== SEARCH PANEL ====================
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search bar at top
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchBar.setBackground(BG_WHITE);

        JLabel lblSearch = new JLabel("Search by ID or Name:");
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSearch.setForeground(TEXT_DARK);

        JTextField txtSearch = createStyledTextField(18);

        JButton btnSearch = createStyledButton("Search", ACCENT_BLUE);
        btnSearch.setPreferredSize(new Dimension(150, 42));
        btnSearch.setMaximumSize(new Dimension(150, 42));

        searchBar.add(lblSearch);
        searchBar.add(Box.createHorizontalStrut(8));
        searchBar.add(txtSearch);
        searchBar.add(Box.createHorizontalStrut(8));
        searchBar.add(btnSearch);

        panel.add(searchBar, BorderLayout.NORTH);

        // Results area
        JTextArea txtResults = new JTextArea("Enter an ID or name and click Search.");
        txtResults.setFont(new Font("Arial", Font.PLAIN, 14));
        txtResults.setForeground(TEXT_SECONDARY);
        txtResults.setEditable(false);
        txtResults.setLineWrap(true);
        txtResults.setWrapStyleWord(true);
        txtResults.setBackground(BG_WHITE);
        txtResults.setBorder(new EmptyBorder(15, 10, 10, 10));

        JScrollPane scrollResults = new JScrollPane(txtResults);
        scrollResults.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT));
        panel.add(scrollResults, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> {
            String query = txtSearch.getText().trim();
            if (query.isEmpty()) {
                txtResults.setText("Please enter an ID or name to search.");
                txtResults.setForeground(TEXT_SECONDARY);
                return;
            }

            StringBuilder result = new StringBuilder();
            boolean found = false;

            // Try search by ID
            try {
                int id = Integer.parseInt(query);
                HACompetitor c = competitorList.getCompetitorById(id);
                if (c != null) {
                    result.append("=== Search Result for ID ").append(id).append(" ===\n\n");
                    result.append("Full Details:\n");
                    result.append(c.getFullDetails());
                    result.append("\n\nShort Details:\n");
                    result.append(c.getShortDetails());
                    found = true;
                }
            } catch (NumberFormatException ex) {
                // Not a number, search by name
            }

            // Search by name (partial match)
            if (!found) {
                String lowerQuery = query.toLowerCase();
                for (HACompetitor c : competitorList.getCompetitors()) {
                    if (c.getCompetitorName().getFullName().toLowerCase().contains(lowerQuery)) {
                        if (!found) {
                            result.append("=== Search Results ===\n\n");
                        }
                        result.append("Full Details:\n");
                        result.append(c.getFullDetails());
                        result.append("\n\nShort Details:\n");
                        result.append(c.getShortDetails());
                        result.append("\n\n---\n\n");
                        found = true;
                    }
                }
            }

            if (!found) {
                txtResults.setText("No competitor found matching: " + query);
                txtResults.setForeground(ACCENT_RED);
            } else {
                txtResults.setText(result.toString());
                txtResults.setForeground(TEXT_DARK);
                txtResults.setCaretPosition(0);
            }
        });

        return panel;
    }

    // ==================== FULL REPORT PANEL ====================
    private JPanel createFullReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextArea txtReport = new JTextArea();
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtReport.setForeground(TEXT_DARK);
        txtReport.setEditable(false);
        txtReport.setBackground(BG_WHITE);

        if (competitorList.getTotalCompetitors() == 0) {
            txtReport.setText("No competitors yet. Take the quiz first to generate a report.");
        } else {
            txtReport.setText(competitorList.generateReport());
        }

        JScrollPane scroll = new JScrollPane(txtReport);
        scroll.setBorder(new LineBorder(BORDER_LIGHT, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ==================== UTILITY ====================
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(250, 42));
        btn.setMaximumSize(new Dimension(300, 42));
        return btn;
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Manager app = new Manager();
            app.setVisible(true);
        });
    }
}
