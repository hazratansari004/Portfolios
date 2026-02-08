package quiz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private JTextField txtFirstName, txtLastName, txtAge;

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

    public Manager() {
        competitorList = new CompetitorList();
        levelScores = new int[5];

        setTitle("Quiz Competition Application");
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
        panel.setBackground(new Color(44, 62, 80));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(44, 62, 80));
        center.setBorder(new EmptyBorder(80, 50, 50, 50));

        JLabel title = new JLabel("Quiz Competition");
        title.setFont(new Font("Arial", Font.BOLD, 42));
        title.setForeground(new Color(236, 240, 241));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Test your knowledge across 5 levels!");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitle.setForeground(new Color(189, 195, 199));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = new JLabel("<html><center>25 Questions | 5 Levels | 15 seconds per question<br>"
                + "Each question is worth 4% of your total score</center></html>");
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        info.setForeground(new Color(149, 165, 166));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnStart = createStyledButton("Start Quiz", new Color(46, 204, 113));
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStart.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));

        JButton btnViewReport = createStyledButton("View Report", new Color(52, 152, 219));
        btnViewReport.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnViewReport.addActionListener(e -> showReportPanel());

        JButton btnLookup = createStyledButton("Look Up Competitor", new Color(155, 89, 182));
        btnLookup.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLookup.addActionListener(e -> lookupCompetitor());

        center.add(title);
        center.add(Box.createVerticalStrut(15));
        center.add(subtitle);
        center.add(Box.createVerticalStrut(10));
        center.add(info);
        center.add(Box.createVerticalStrut(40));
        center.add(btnStart);
        center.add(Box.createVerticalStrut(10));
        center.add(btnViewReport);
        center.add(Box.createVerticalStrut(10));
        center.add(btnLookup);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    // ==================== REGISTRATION PANEL ====================
    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        JLabel header = new JLabel("Competitor Registration", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 28));
        header.setForeground(new Color(44, 62, 80));
        header.setBorder(new EmptyBorder(30, 0, 20, 0));
        panel.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(236, 240, 241));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblFirst = new JLabel("First Name:");
        lblFirst.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(lblFirst, gbc);

        txtFirstName = new JTextField(20);
        txtFirstName.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1; gbc.gridy = 0;
        form.add(txtFirstName, gbc);

        JLabel lblLast = new JLabel("Last Name:");
        lblLast.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(lblLast, gbc);

        txtLastName = new JTextField(20);
        txtLastName.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1; gbc.gridy = 1;
        form.add(txtLastName, gbc);

        JLabel lblAge = new JLabel("Age:");
        lblAge.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(lblAge, gbc);

        txtAge = new JTextField(20);
        txtAge.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1; gbc.gridy = 2;
        form.add(txtAge, gbc);

        JButton btnRegister = createStyledButton("Begin Quiz", new Color(46, 204, 113));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(btnRegister, gbc);

        btnRegister.addActionListener(e -> startQuiz());

        JButton btnBack = createStyledButton("Back", new Color(149, 165, 166));
        gbc.gridy = 4;
        form.add(btnBack, gbc);
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    // ==================== QUIZ PANEL ====================
    private JPanel createQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(44, 62, 80));
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        lblLevel = new JLabel("Level 1: Beginner");
        lblLevel.setFont(new Font("Arial", Font.BOLD, 18));
        lblLevel.setForeground(Color.WHITE);

        lblTimer = new JLabel("Time: 15s");
        lblTimer.setFont(new Font("Arial", Font.BOLD, 18));
        lblTimer.setForeground(new Color(231, 76, 60));

        lblProgress = new JLabel("Q1/25 (0%)");
        lblProgress.setFont(new Font("Arial", Font.BOLD, 14));
        lblProgress.setForeground(new Color(189, 195, 199));

        topBar.add(lblLevel, BorderLayout.WEST);
        topBar.add(lblProgress, BorderLayout.CENTER);
        topBar.add(lblTimer, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Question area
        JPanel questionArea = new JPanel();
        questionArea.setLayout(new BoxLayout(questionArea, BoxLayout.Y_AXIS));
        questionArea.setBackground(Color.WHITE);
        questionArea.setBorder(new EmptyBorder(30, 50, 20, 50));

        lblQuestion = new JLabel("Question text here");
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 20));
        lblQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionArea.add(lblQuestion);
        questionArea.add(Box.createVerticalStrut(25));

        optionButtons = new JRadioButton[4];
        optionGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton("Option " + (i + 1));
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            optionButtons[i].setBackground(Color.WHITE);
            optionButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            optionGroup.add(optionButtons[i]);
            questionArea.add(optionButtons[i]);
            questionArea.add(Box.createVerticalStrut(10));
        }

        panel.add(questionArea, BorderLayout.CENTER);

        // Bottom bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomBar.setBackground(new Color(236, 240, 241));
        bottomBar.setBorder(new EmptyBorder(10, 0, 10, 0));

        lblScore = new JLabel("Score: 0%");
        lblScore.setFont(new Font("Arial", Font.BOLD, 16));

        btnNext = createStyledButton("Next", new Color(52, 152, 219));
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
        String lastName = txtLastName.getText().trim();
        String ageStr = txtAge.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.",
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

        Name name = new Name(firstName, lastName);
        int id = competitorList.getNextId();
        currentCompetitor = new HACompetitor(id, name, "Beginner", age);
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
        if (questionTimer != null) {
            questionTimer.stop();
        }
        questionTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                lblTimer.setText("Time: " + timeRemaining + "s");
                if (timeRemaining <= 5) {
                    lblTimer.setForeground(new Color(231, 76, 60));
                } else {
                    lblTimer.setForeground(Color.WHITE);
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
        panel.setBackground(new Color(39, 174, 96));
        panel.setBorder(new EmptyBorder(80, 50, 50, 50));

        JLabel lblDone = new JLabel("Level " + currentLevel + " Completed!");
        lblDone.setFont(new Font("Arial", Font.BOLD, 36));
        lblDone.setForeground(Color.WHITE);
        lblDone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLevelScore = new JLabel("You got " + levelCorrect + " out of 5 correct");
        lblLevelScore.setFont(new Font("Arial", Font.PLAIN, 22));
        lblLevelScore.setForeground(new Color(236, 240, 241));
        lblLevelScore.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTotal = new JLabel("Total progress: " + (totalCorrect * 4) + "%");
        lblTotal.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTotal.setForeground(new Color(236, 240, 241));
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
            lblNext.setFont(new Font("Arial", Font.ITALIC, 16));
            lblNext.setForeground(Color.WHITE);
            lblNext.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblNext);
            panel.add(Box.createVerticalStrut(20));

            JButton btnContinue = createStyledButton("Continue to Level " + (currentLevel + 1), new Color(41, 128, 185));
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
            lblFinished.setForeground(Color.WHITE);
            lblFinished.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblFinished);
            panel.add(Box.createVerticalStrut(20));

            JButton btnFinish = createStyledButton("View Results", new Color(41, 128, 185));
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
        if (totalPercent >= 80) level = "Advanced";
        else if (totalPercent >= 50) level = "Intermediate";
        else level = "Beginner";

        currentCompetitor.setLevel(level);
        currentCompetitor.setScores(levelScores);
        competitorList.addCompetitor(currentCompetitor);

        showReportPanel();
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
        panel.setBackground(Color.WHITE);

        // Header
        JLabel header = new JLabel("Competition Report", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 28));
        header.setForeground(new Color(44, 62, 80));
        header.setBorder(new EmptyBorder(15, 0, 10, 0));
        panel.add(header, BorderLayout.NORTH);

        // Tabbed pane for sections
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 14));

        // Tab 1: Competitor Table
        tabs.addTab("Competitors", createCompetitorTablePanel());

        // Tab 2: Top Performer
        tabs.addTab("Top Performer", createTopPerformerPanel());

        // Tab 3: Statistics
        tabs.addTab("Statistics", createStatisticsPanel());

        panel.add(tabs, BorderLayout.CENTER);

        // Bottom bar
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(new Color(236, 240, 241));

        JButton btnHome = createStyledButton("Home", new Color(44, 62, 80));
        btnHome.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));

        JButton btnLookup = createStyledButton("Look Up Competitor", new Color(155, 89, 182));
        btnLookup.addActionListener(e -> lookupCompetitor());

        bottom.add(btnHome);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(btnLookup);

        panel.add(bottom, BorderLayout.SOUTH);

        mainPanel.add(panel, "REPORT");
        cardLayout.show(mainPanel, "REPORT");
    }

    private JPanel createCompetitorTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Name", "Level", "Score1", "Score2", "Score3", "Score4", "Score5", "Overall"};
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
                scores[0], scores[1], scores[2], scores[3], scores[4],
                c.getOverallScore()
            });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        if (competitorList.getTotalCompetitors() == 0) {
            JLabel empty = new JLabel("No competitors yet. Take the quiz first!", SwingConstants.CENTER);
            empty.setFont(new Font("Arial", Font.ITALIC, 16));
            panel.add(empty, BorderLayout.CENTER);
        } else {
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createTopPerformerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        HACompetitor top = competitorList.getTopPerformer();
        if (top != null) {
            JLabel lblTitle = new JLabel("Top Performer");
            lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
            lblTitle.setForeground(new Color(39, 174, 96));
            lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lblTitle);
            panel.add(Box.createVerticalStrut(15));

            JTextArea txtDetails = new JTextArea(top.getFullDetails());
            txtDetails.setFont(new Font("Arial", Font.PLAIN, 16));
            txtDetails.setEditable(false);
            txtDetails.setLineWrap(true);
            txtDetails.setWrapStyleWord(true);
            txtDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(txtDetails);
        } else {
            JLabel empty = new JLabel("No competitors yet.");
            empty.setFont(new Font("Arial", Font.ITALIC, 16));
            panel.add(empty);
        }

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("Statistical Summary");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));
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
        txtStats.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtStats.setEditable(false);
        txtStats.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtStats);

        return panel;
    }

    // ==================== LOOKUP ====================
    private void lookupCompetitor() {
        if (competitorList.getTotalCompetitors() == 0) {
            JOptionPane.showMessageDialog(this, "No competitors registered yet. Take the quiz first!",
                    "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "Enter Competitor ID:", "Look Up Competitor", JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(input.trim());
            HACompetitor c = competitorList.getCompetitorById(id);
            if (c != null) {
                JOptionPane.showMessageDialog(this,
                        c.getShortDetails(),
                        "Competitor Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No competitor found with ID " + id + ".",
                        "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid ID. Please enter a numeric value.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== UTILITY ====================
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(250, 45));
        btn.setMaximumSize(new Dimension(300, 45));
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
