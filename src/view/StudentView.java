package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.*;
import controller.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The main dashboard view for students, displaying enrolled courses, GPA,
 * and navigation to grade details. Implements PropertyChangeListener for
 * automatic refresh when model data changes.
 */
@SuppressWarnings("serial")
public class StudentView extends JFrame implements PropertyChangeListener {
    private final StudentController controller;
    private JLabel welcomeLabel;
    private JLabel gpaLabel;
    private JList<String> courseList;
    private JButton viewGradesButton;
    private JButton refreshButton;
    private JButton logoutButton;
    private JTabbedPane tabbedPane; // For current/completed courses
    private JPanel completedCoursesPanel;
    private DefaultTableModel completedCoursesModel;

    /**
     * Constructs the StudentView with controller reference.
     * @param controller The StudentController for business logic
     */
    public StudentView(StudentController controller) {
        this.controller = controller;
        controller.addPropertyChangeListener(this);
        setupUI();
        refreshData();
    }

    /**
     * Initializes all UI components and layouts.
     */
    private void setupUI() {
        setTitle("Student Dashboard");
        setSize(700, 500); // Increased height for tabs
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel - Welcome message and GPA
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        welcomeLabel = new JLabel("", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gpaLabel = new JLabel("", SwingConstants.CENTER);
        gpaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(welcomeLabel);
        topPanel.add(gpaLabel);
        panel.add(topPanel, BorderLayout.NORTH);

        // Tabbed pane for current/completed courses
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Current Courses", createCurrentCoursesPanel());
        tabbedPane.addTab("Completed Courses", createCompletedCoursesPanel());
        panel.add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel - Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        viewGradesButton = new JButton("View Grades");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");
        
        viewGradesButton.addActionListener(this::handleViewGrades);
        refreshButton.addActionListener(e -> refreshData());
        
        buttonPanel.add(viewGradesButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    /**
     * Creates the panel for current courses.
     * @return JPanel containing current courses list
     */
    private JPanel createCurrentCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        courseList = new JList<>();
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Enrolled Courses"));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the panel for completed courses.
     * @return JPanel containing completed courses table
     */
    private JPanel createCompletedCoursesPanel() {
        completedCoursesPanel = new JPanel(new BorderLayout());
        completedCoursesModel = new DefaultTableModel(new Object[]{"Course", "Final Grade"}, 0);
        JTable table = new JTable(completedCoursesModel);
        completedCoursesPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        return completedCoursesPanel;
    }
    
    /**
     * Refreshes the completed courses
     */
    private void refreshCompletedCourses() {
        completedCoursesModel.setRowCount(0);
        controller.getCurrentStudent().getCompletedCourses().forEach(c -> {
            String grade = controller.getFinalGradeForCourse(c);
            completedCoursesModel.addRow(new Object[]{c.getName(), grade});
        });
    }

    /**
     * Refreshes all displayed data from controller.
     */
    private void refreshData() {
        Student student = controller.getCurrentStudent();
        if (student != null) {
            // Update welcome message
            welcomeLabel.setText("Welcome, " + student.getFirstName() + " " + student.getLastName());
            
            // Update GPA with letter grade
            double gpa = controller.calculateGPA();
            gpaLabel.setText(String.format("Overall GPA: %.2f (%s)", 
                gpa, 
                GradeScale.fromPercentage(gpa * 25).getLetter()));
            
            // Refresh current courses
            List<Course> courses = controller.viewCourses();
            DefaultListModel<String> model = new DefaultListModel<>();
            courses.forEach(course -> model.addElement(course.getName()));
            courseList.setModel(model);

            refreshCompletedCourses();
        }
    }

    /**
     * Handles "View Grades" button click.
     * @param e The action event
     */
    private void handleViewGrades(ActionEvent e) {
        String selectedCourse = courseList.getSelectedValue();
        if (selectedCourse != null) {
            Course selected = controller.viewCourses().stream()
                .filter(c -> c.getName().equals(selectedCourse))
                .findFirst()
                .orElse(null);
                
            if (selected != null) {
                new GradeView(controller, selected).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a course first", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Returns logout button for external listeners.
     * @return The logout button
     */
    public JButton getLogoutButton() {
        return logoutButton;
    }

    /**
     * Listens for model changes and refreshes view.
     * @param evt The property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            if (evt.getPropertyName().equals("gradeAdded") || 
                evt.getPropertyName().equals("courseEnrolled")) {
                refreshData();
            }
        });
    }
}