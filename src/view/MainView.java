package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainView extends JFrame {
    private JLabel welcomeLabel;
    private JList<String> courseList;
    private JButton viewCourseButton;
    private JButton logoutButton;

    public MainView(String username, List<String> courseNames) {
        setTitle("Gradebook - Main Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top: Welcome message
        welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Center: List of courses
        courseList = new JList<>(courseNames.toArray(new String[0]));
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Courses"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom: Buttons
        JPanel buttonPanel = new JPanel();
        viewCourseButton = new JButton("View Selected Course");
        logoutButton = new JButton("Logout");
        buttonPanel.add(viewCourseButton);
        buttonPanel.add(logoutButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    public JButton getViewCourseButton() {
        return viewCourseButton;
    }

    
    public JButton getLogoutButton() {
        return logoutButton;
    }

    public String getSelectedCourse() {
        return courseList.getSelectedValue();
    }
    //Testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainView("Harun Andeshmand", List.of("Csc 335", "Psy336", "Engl106", "Csc 144"));
        });
    }

}
