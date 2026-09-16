import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class DigitalSuggestionBox {

    private static ArrayList<Suggestion> suggestions = new ArrayList<>();
    private static final String DATA_FILE = "suggestions.dat";

    // Colors
    private static final Color SIDEBAR = new Color(24, 31, 46);
    private static final Color SIDEBAR_HOVER = new Color(40, 49, 68);
    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(30, 38, 52);
    private static final Color SECONDARY_TEXT = new Color(105, 115, 130);
    private static final Color PRIMARY = new Color(57, 105, 216);
    private static final Color BORDER = new Color(225, 229, 237);

    private static JFrame frame;
    private static JPanel contentPanel;

    public static void main(String[] args) {

        loadSuggestions();

        SwingUtilities.invokeLater(() -> {
            createApplication();
        });
    }

    // =========================
    // MAIN APPLICATION
    // =========================

    private static void createApplication() {

        frame = new JFrame("CampusVoice - Digital Suggestion Box");
        frame.setSize(1100, 700);
        frame.setMinimumSize(new Dimension(1000, 650));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);

        mainPanel.add(createSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);

        showDashboard();

        frame.setVisible(true);
    }

    // =========================
    // SIDEBAR
    // =========================

    private static JPanel createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 700));
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(30, 22, 20, 22));

        JLabel logo = new JLabel("CampusVoice");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Digital Suggestion Box");
        tagline.setFont(new Font("Arial", Font.PLAIN, 11));
        tagline.setForeground(new Color(165, 174, 190));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(logo);
        top.add(Box.createVerticalStrut(5));
        top.add(tagline);
        top.add(Box.createVerticalStrut(35));

        JButton dashboardButton = createSidebarButton("Dashboard");
        JButton mySuggestionsButton = createSidebarButton("My Suggestions");
        JButton submitButton = createSidebarButton("Submit Suggestion");
        JButton adminButton = createSidebarButton("Admin Panel");

        dashboardButton.addActionListener(e -> showDashboard());
        mySuggestionsButton.addActionListener(e -> showMySuggestions());
        submitButton.addActionListener(e -> showSubmitSuggestion());
        adminButton.addActionListener(e -> showAdminLogin());

        top.add(dashboardButton);
        top.add(Box.createVerticalStrut(8));
        top.add(mySuggestionsButton);
        top.add(Box.createVerticalStrut(8));
        top.add(submitButton);
        top.add(Box.createVerticalStrut(8));
        top.add(adminButton);

        sidebar.add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(15, 22, 25, 22));

        JLabel footer = new JLabel("Anonymous & Secure");
        footer.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.setForeground(new Color(130, 140, 158));

        bottom.add(footer, BorderLayout.WEST);

        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private static JButton createSidebarButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(new Color(220, 225, 235));
        button.setBackground(SIDEBAR);
        button.setBorder(new EmptyBorder(12, 14, 12, 14));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SIDEBAR);
            }
        });

        return button;
    }

    // =========================
    // DASHBOARD
    // =========================

    private static void showDashboard() {

        contentPanel.removeAll();

        JPanel page = new JPanel();
        page.setBackground(BACKGROUND);
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Good day!");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Share your ideas, complaints and suggestions anonymously."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY_TEXT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(title);
        page.add(Box.createVerticalStrut(5));
        page.add(subtitle);
        page.add(Box.createVerticalStrut(25));

        JButton submitButton = new JButton("+  Submit Suggestion");
        submitButton.setFont(new Font("Arial", Font.BOLD, 13));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(PRIMARY);
        submitButton.setBorder(new EmptyBorder(12, 18, 12, 18));
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitButton.addActionListener(e -> showSubmitSuggestion());

        page.add(submitButton);
        page.add(Box.createVerticalStrut(25));

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        int total = suggestions.size();
        int review = countStatus("Under Review");
        int resolved = countStatus("Resolved");

        statsPanel.add(createStatCard("Total Suggestions", String.valueOf(total)));
        statsPanel.add(createStatCard("Under Review", String.valueOf(review)));
        statsPanel.add(createStatCard("Resolved", String.valueOf(resolved)));

        page.add(statsPanel);
        page.add(Box.createVerticalStrut(25));

        JPanel recentCard = createCardPanel();

        recentCard.setLayout(new BorderLayout());
        recentCard.setBorder(new CompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel recentTitle = new JLabel("Recent Activity");
        recentTitle.setFont(new Font("Arial", Font.BOLD, 17));
        recentTitle.setForeground(TEXT);

        recentCard.add(recentTitle, BorderLayout.NORTH);

        JPanel activityPanel = new JPanel();
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setLayout(new BoxLayout(activityPanel, BoxLayout.Y_AXIS));

        int start = Math.max(0, suggestions.size() - 4);

        if (suggestions.isEmpty()) {

            JLabel empty = new JLabel("No suggestions submitted yet.");
            empty.setFont(new Font("Arial", Font.PLAIN, 13));
            empty.setForeground(SECONDARY_TEXT);

            activityPanel.add(Box.createVerticalStrut(20));
            activityPanel.add(empty);

        } else {

            for (int i = suggestions.size() - 1; i >= start; i--) {

                Suggestion s = suggestions.get(i);

                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(new EmptyBorder(12, 0, 12, 0));

                JLabel left = new JLabel(
                        s.getCategory() + "   |   " + s.getId()
                );
                left.setFont(new Font("Arial", Font.BOLD, 13));
                left.setForeground(TEXT);

                JLabel right = new JLabel(s.getStatus());
                right.setFont(new Font("Arial", Font.PLAIN, 12));
                right.setForeground(PRIMARY);

                row.add(left, BorderLayout.WEST);
                row.add(right, BorderLayout.EAST);

                activityPanel.add(row);

                if (i > start) {
                    JSeparator separator = new JSeparator();
                    separator.setForeground(BORDER);
                    activityPanel.add(separator);
                }
            }
        }

        recentCard.add(activityPanel, BorderLayout.CENTER);

        page.add(recentCard);

        contentPanel.add(page, BorderLayout.CENTER);

        refreshContent();
    }

    private static JPanel createStatCard(String title, String value) {

        JPanel card = createCardPanel();

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(SECONDARY_TEXT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 27));
        valueLabel.setForeground(TEXT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    private static JPanel createCardPanel() {

        JPanel panel = new JPanel();
        panel.setBackground(CARD);

        return panel;
    }

    // =========================
    // SUBMIT SUGGESTION
    // =========================

    private static void showSubmitSuggestion() {

        contentPanel.removeAll();

        JPanel page = new JPanel();
        page.setBackground(BACKGROUND);
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Submit Suggestion");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Your feedback helps improve the campus."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY_TEXT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(title);
        page.add(Box.createVerticalStrut(5));
        page.add(subtitle);
        page.add(Box.createVerticalStrut(25));

        JPanel formCard = createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new CompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(25, 25, 25, 25)
        ));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 13));
        categoryLabel.setForeground(TEXT);

        JComboBox<String> categoryBox = new JComboBox<>(
                new String[]{
                        "Academics",
                        "Infrastructure",
                        "Canteen",
                        "Transport",
                        "Events",
                        "Other"
                }
        );

        categoryBox.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel descriptionLabel = new JLabel("Suggestion / Complaint");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        descriptionLabel.setForeground(TEXT);

        JTextArea descriptionArea = new JTextArea(8, 40);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(new LineBorder(BORDER));

        JLabel anonymousNotice = new JLabel(
                "This suggestion will be submitted anonymously."
        );
        anonymousNotice.setFont(new Font("Arial", Font.ITALIC, 12));
        anonymousNotice.setForeground(SECONDARY_TEXT);

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.setFocusPainted(false);

        JButton submitButton = new JButton("Submit Suggestion");
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(PRIMARY);
        submitButton.setFocusPainted(false);

        clearButton.addActionListener(e -> {
            descriptionArea.setText("");
            categoryBox.setSelectedIndex(0);
        });

        submitButton.addActionListener(e -> {

            String description = descriptionArea.getText().trim();

            if (description.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter a suggestion or complaint.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            String id = generateSuggestionId();

            Suggestion suggestion = new Suggestion(
                    id,
                    categoryBox.getSelectedItem().toString(),
                    description
            );

            suggestions.add(suggestion);

            saveSuggestions();

            JOptionPane.showMessageDialog(
                    frame,
                    "Suggestion submitted successfully!\n\n"
                            + "Your Tracking ID: " + id
                            + "\n\n"
                            + "Please save this ID. You can use it to track your suggestion.",
                    "Submission Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            showDashboard();
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formCard.add(categoryLabel, gbc);

        gbc.gridy++;
        formCard.add(categoryBox, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 8, 8, 8);
        formCard.add(descriptionLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;
        formCard.add(scrollPane, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formCard.add(anonymousNotice, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        buttonPanel.add(clearButton);
        buttonPanel.add(submitButton);

        gbc.gridy++;
        formCard.add(buttonPanel, gbc);

        page.add(formCard);

        contentPanel.add(page, BorderLayout.CENTER);

        refreshContent();
    }

    // =========================
    // MY SUGGESTIONS
    // =========================

    private static void showMySuggestions() {

        contentPanel.removeAll();

        JPanel page = new JPanel();
        page.setBackground(BACKGROUND);
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("My Suggestions");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Enter your tracking ID to check the status of your suggestion."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY_TEXT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(title);
        page.add(Box.createVerticalStrut(5));
        page.add(subtitle);
        page.add(Box.createVerticalStrut(25));

        JPanel searchCard = createCardPanel();
        searchCard.setLayout(new BorderLayout(10, 10));
        searchCard.setBorder(new CompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));
        searchCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        JTextField idField = new JTextField();
        idField.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton searchButton = new JButton("Track Suggestion");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(PRIMARY);
        searchButton.setFocusPainted(false);

        searchPanel.add(idField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        searchCard.add(searchPanel, BorderLayout.NORTH);

        JPanel resultPanel = new JPanel();
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(new EmptyBorder(20, 5, 5, 5));

        searchCard.add(resultPanel, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {

            String id = idField.getText().trim();

            if (id.isEmpty()) {
                return;
            }

            Suggestion suggestion = findSuggestion(id);

            resultPanel.removeAll();

            if (suggestion == null) {

                JLabel notFound = new JLabel(
                        "No suggestion found with this tracking ID."
                );

                notFound.setFont(new Font("Arial", Font.PLAIN, 13));
                notFound.setForeground(Color.RED);

                resultPanel.add(notFound);

            } else {

                addDetail(resultPanel, "Tracking ID", suggestion.getId());
                addDetail(resultPanel, "Category", suggestion.getCategory());
                addDetail(resultPanel, "Submitted", suggestion.getDate());
                addDetail(resultPanel, "Status", suggestion.getStatus());

                resultPanel.add(Box.createVerticalStrut(15));

                JLabel suggestionLabel = new JLabel("Suggestion / Complaint");
                suggestionLabel.setFont(new Font("Arial", Font.BOLD, 13));
                suggestionLabel.setForeground(TEXT);

                resultPanel.add(suggestionLabel);
                resultPanel.add(Box.createVerticalStrut(5));

                JTextArea suggestionText = new JTextArea(
                        suggestion.getDescription()
                );

                suggestionText.setFont(new Font("Arial", Font.PLAIN, 13));
                suggestionText.setLineWrap(true);
                suggestionText.setWrapStyleWord(true);
                suggestionText.setEditable(false);
                suggestionText.setBackground(Color.WHITE);

                resultPanel.add(suggestionText);

                resultPanel.add(Box.createVerticalStrut(15));

                JLabel responseLabel = new JLabel("Admin Response");
                responseLabel.setFont(new Font("Arial", Font.BOLD, 13));
                responseLabel.setForeground(TEXT);

                resultPanel.add(responseLabel);
                resultPanel.add(Box.createVerticalStrut(5));

                String response = suggestion.getAdminResponse();

                if (response == null || response.trim().isEmpty()) {
                    response = "No response yet.";
                }

                JLabel responseText = new JLabel(
                        "<html>" + response.replace("\n", "<br>") + "</html>"
                );

                responseText.setFont(new Font("Arial", Font.PLAIN, 13));
                responseText.setForeground(SECONDARY_TEXT);

                resultPanel.add(responseText);
            }

            resultPanel.revalidate();
            resultPanel.repaint();
        });

        page.add(searchCard);

        contentPanel.add(page, BorderLayout.CENTER);

        refreshContent();
    }

    private static void addDetail(
            JPanel panel,
            String label,
            String value
    ) {

        JLabel detail = new JLabel(
                "<html><b>" + label + ":</b> " + value + "</html>"
        );

        detail.setFont(new Font("Arial", Font.PLAIN, 13));
        detail.setForeground(TEXT);

        panel.add(detail);
        panel.add(Box.createVerticalStrut(7));
    }

    // =========================
    // ADMIN LOGIN
    // =========================

    private static void showAdminLogin() {

        JDialog dialog = new JDialog(
                frame,
                "Admin Login",
                true
        );

        dialog.setSize(430, 330);
        dialog.setLocationRelativeTo(frame);
        dialog.setResizable(false);

        JPanel main = new JPanel();
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(25, 30, 25, 30));
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Login to manage campus suggestions."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(SECONDARY_TEXT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(title);
        main.add(Box.createVerticalStrut(5));
        main.add(subtitle);
        main.add(Box.createVerticalStrut(25));

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        usernameLabel.setForeground(TEXT);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 13));
        usernameField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 38)
        );

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        passwordLabel.setForeground(TEXT);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 38)
        );

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(PRIMARY);
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40)
        );

        main.add(usernameLabel);
        main.add(Box.createVerticalStrut(5));
        main.add(usernameField);
        main.add(Box.createVerticalStrut(15));
        main.add(passwordLabel);
        main.add(Box.createVerticalStrut(5));
        main.add(passwordField);
        main.add(Box.createVerticalStrut(20));
        main.add(loginButton);

        loginButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.equals("admin") &&
                    password.equals("admin123")) {

                dialog.dispose();
                showAdminDashboard();

            } else {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        passwordField.addActionListener(e -> loginButton.doClick());

        dialog.setContentPane(main);
        dialog.setVisible(true);
    }

    // =========================
    // ADMIN DASHBOARD
    // =========================

    private static void showAdminDashboard() {

        contentPanel.removeAll();

        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(
                titlePanel,
                BoxLayout.Y_AXIS
        ));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Manage and respond to anonymous campus feedback."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY_TEXT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);

        logoutButton.addActionListener(e -> showDashboard());

        header.add(titlePanel, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        page.add(header, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(1, 5, 10, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(0, 85));

        stats.add(createSmallStat(
                "Total",
                String.valueOf(suggestions.size())
        ));

        stats.add(createSmallStat(
                "Submitted",
                String.valueOf(countStatus("Submitted"))
        ));

        stats.add(createSmallStat(
                "Under Review",
                String.valueOf(countStatus("Under Review"))
        ));

        stats.add(createSmallStat(
                "In Progress",
                String.valueOf(countStatus("In Progress"))
        ));

        stats.add(createSmallStat(
                "Resolved",
                String.valueOf(countStatus("Resolved"))
        ));

        page.add(stats, BorderLayout.CENTER);

        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new CompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(15, 15, 15, 15)
        ));

        String[] columns = {
                "ID",
                "Category",
                "Date",
                "Status"
        };

        DefaultTableModel model = new DefaultTableModel(
                columns,
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        for (Suggestion s : suggestions) {

            model.addRow(new Object[]{
                    s.getId(),
                    s.getCategory(),
                    s.getDate(),
                    s.getStatus()
            });
        }

        JTable table = new JTable(model);

        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        table.getTableHeader().setBackground(
                new Color(240, 243, 248)
        );

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new LineBorder(BORDER));

        tableCard.add(tableScroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(
                FlowLayout.RIGHT
        ));
        controls.setOpaque(false);

        JButton manageButton = new JButton("Manage Selected Suggestion");
        manageButton.setFont(new Font("Arial", Font.BOLD, 12));
        manageButton.setForeground(Color.WHITE);
        manageButton.setBackground(PRIMARY);
        manageButton.setFocusPainted(false);

        controls.add(manageButton);

        tableCard.add(controls, BorderLayout.SOUTH);

        page.add(tableCard, BorderLayout.SOUTH);

        manageButton.addActionListener(e -> {

            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select a suggestion first.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            String id = model.getValueAt(
                    selectedRow,
                    0
            ).toString();

            Suggestion selectedSuggestion = findSuggestion(id);

            if (selectedSuggestion != null) {

                showManageSuggestion(
                        selectedSuggestion
                );

                showAdminDashboard();
            }
        });

        contentPanel.add(page, BorderLayout.CENTER);

        refreshContent();
    }

    private static JPanel createSmallStat(
            String title,
            String value
    ) {

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(10, 12, 10, 12)
        ));

        panel.setLayout(new BoxLayout(
                panel,
                BoxLayout.Y_AXIS
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        titleLabel.setForeground(SECONDARY_TEXT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(TEXT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(valueLabel);

        return panel;
    }

    // =========================
    // MANAGE SUGGESTION
    // =========================

    private static void showManageSuggestion(
            Suggestion suggestion
    ) {

        JDialog dialog = new JDialog(
                frame,
                "Manage Suggestion",
                true
        );

        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(frame);

        JPanel main = new JPanel();
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(20, 25, 20, 25));
        main.setLayout(new BoxLayout(
                main,
                BoxLayout.Y_AXIS
        ));

        JLabel title = new JLabel(
                "Manage " + suggestion.getId()
        );

        title.setFont(new Font(
                "Arial",
                Font.BOLD,
                22
        ));

        title.setForeground(TEXT);

        JLabel category = new JLabel(
                "Category: " + suggestion.getCategory()
        );

        category.setFont(new Font(
                "Arial",
                Font.PLAIN,
                13
        ));

        category.setForeground(SECONDARY_TEXT);

        JLabel date = new JLabel(
                "Submitted: " + suggestion.getDate()
        );

        date.setFont(new Font(
                "Arial",
                Font.PLAIN,
                13
        ));

        date.setForeground(SECONDARY_TEXT);

        main.add(title);
        main.add(Box.createVerticalStrut(5));
        main.add(category);
        main.add(date);
        main.add(Box.createVerticalStrut(20));

        JLabel suggestionLabel = new JLabel(
                "Suggestion / Complaint"
        );

        suggestionLabel.setFont(new Font(
                "Arial",
                Font.BOLD,
                13
        ));

        JTextArea suggestionArea = new JTextArea(
                suggestion.getDescription()
        );

        suggestionArea.setFont(new Font(
                "Arial",
                Font.PLAIN,
                13
        ));

        suggestionArea.setLineWrap(true);
        suggestionArea.setWrapStyleWord(true);
        suggestionArea.setEditable(false);

        JScrollPane suggestionScroll =
                new JScrollPane(suggestionArea);

        suggestionScroll.setPreferredSize(
                new Dimension(500, 120)
        );

        main.add(suggestionLabel);
        main.add(Box.createVerticalStrut(5));
        main.add(suggestionScroll);
        main.add(Box.createVerticalStrut(15));

        JLabel statusLabel = new JLabel("Status");

        statusLabel.setFont(new Font(
                "Arial",
                Font.BOLD,
                13
        ));

        JComboBox<String> statusBox =
                new JComboBox<>(
                        new String[]{
                                "Submitted",
                                "Under Review",
                                "In Progress",
                                "Resolved"
                        }
                );

        statusBox.setSelectedItem(
                suggestion.getStatus()
        );

        main.add(statusLabel);
        main.add(Box.createVerticalStrut(5));
        main.add(statusBox);
        main.add(Box.createVerticalStrut(15));

        JLabel responseLabel = new JLabel(
                "Admin Response"
        );

        responseLabel.setFont(new Font(
                "Arial",
                Font.BOLD,
                13
        ));

        JTextArea responseArea = new JTextArea(
                suggestion.getAdminResponse()
        );

        responseArea.setFont(new Font(
                "Arial",
                Font.PLAIN,
                13
        ));

        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);

        JScrollPane responseScroll =
                new JScrollPane(responseArea);

        responseScroll.setPreferredSize(
                new Dimension(500, 90)
        );

        main.add(responseLabel);
        main.add(Box.createVerticalStrut(5));
        main.add(responseScroll);
        main.add(Box.createVerticalStrut(15));

        JButton saveButton = new JButton(
                "Save Changes"
        );

        saveButton.setFont(new Font(
                "Arial",
                Font.BOLD,
                12
        ));

        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(PRIMARY);
        saveButton.setFocusPainted(false);

        saveButton.addActionListener(e -> {

            suggestion.setStatus(
                    statusBox.getSelectedItem().toString()
            );

            suggestion.setAdminResponse(
                    responseArea.getText().trim()
            );

            saveSuggestions();

            JOptionPane.showMessageDialog(
                    dialog,
                    "Suggestion updated successfully.",
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dialog.dispose();
        });

        main.add(saveButton);

        dialog.setContentPane(main);
        dialog.setVisible(true);
    }

    // =========================
    // DATA FUNCTIONS
    // =========================

    private static int countStatus(String status) {

        int count = 0;

        for (Suggestion s : suggestions) {

            if (s.getStatus().equals(status)) {
                count++;
            }
        }

        return count;
    }

    private static String generateSuggestionId() {

        int number = 1001 + suggestions.size();

        String id = "SUG-" + number;

        while (findSuggestion(id) != null) {
            number++;
            id = "SUG-" + number;
        }

        return id;
    }

    private static Suggestion findSuggestion(String id) {

        for (Suggestion s : suggestions) {

            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }

        return null;
    }

    private static void saveSuggestions() {

        try {

            ObjectOutputStream output =
                    new ObjectOutputStream(
                            new FileOutputStream(DATA_FILE)
                    );

            output.writeObject(suggestions);
            output.close();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Could not save suggestions.",
                    "Storage Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadSuggestions() {

        File file = new File(DATA_FILE);

        if (!file.exists()) {
            return;
        }

        try {

            ObjectInputStream input =
                    new ObjectInputStream(
                            new FileInputStream(file)
                    );

            suggestions =
                    (ArrayList<Suggestion>) input.readObject();

            input.close();

        } catch (IOException | ClassNotFoundException e) {

            suggestions = new ArrayList<>();
        }
    }

    // =========================
    // UI REFRESH
    // =========================

    private static void refreshContent() {

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}