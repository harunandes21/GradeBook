package view;

import javax.swing.*;
import javax.swing.table.*;
import model.*;
import controller.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Displays detailed grade information for a specific course, including
 * assignment scores and instructor feedback.
 */
@SuppressWarnings("serial")
public class GradeView extends JFrame implements PropertyChangeListener {
    private final StudentController controller;
    private final Course course;
    private JLabel statusBar;
    private JTextArea feedbackArea;

    /**
     * Constructs GradeView for specific course.
     * @param controller The StudentController for data access
     * @param course The Course to display grades for
     */
    public GradeView(StudentController controller, Course course) {
        this.controller = controller;
        this.course = course;
        controller.addPropertyChangeListener(this);
        setupUI();
    }

    /**
     * Initializes all UI components and layouts.
     */
    private void setupUI() {
        setTitle("Grades for " + course.getName());
        setSize(750, 550); // Adjusted dimensions
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Course header
        JLabel courseLabel = new JLabel(
            course.getName() + " - " + course.getSemester(),
            SwingConstants.CENTER
        );
        courseLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(courseLabel, BorderLayout.NORTH);

        // Grades table
        JTable gradesTable = createGradesTable();
        JScrollPane tableScrollPane = new JScrollPane(gradesTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Assignment Grades"));
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Feedback panel
        feedbackArea = createFeedbackArea();
        JScrollPane feedbackScrollPane = new JScrollPane(feedbackArea);
        feedbackScrollPane.setBorder(BorderFactory.createTitledBorder("Feedback"));

        statusBar = new JLabel(" ", SwingConstants.CENTER);
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        // Feedback and Status
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(feedbackScrollPane, BorderLayout.CENTER);
        southPanel.add(statusBar, BorderLayout.SOUTH);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        updateStatus();
        add(panel);
    }

    /**
     * Creates and populates grades table.
     * @return Configured JTable with grade data
     */
    private JTable createGradesTable() {
        Map<Assignment, Grade> grades = controller.getGradesForCourse(course);
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Assignment", "Score", "Total Points", "Percentage"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        grades.forEach((assignment, grade) -> {
            double percentage = (grade.getPointsEarned() / assignment.getPointsWorth()) * 100;
            model.addRow(new Object[]{
                assignment.getName(),
                grade.getPointsEarned(),
                assignment.getPointsWorth(),
                String.format("%.1f%%", percentage)
            });
        });

        JTable table = new JTable(model);
        table.getColumnModel().getColumn(3).setCellRenderer(new PercentageRenderer());
        return table;
    }

    /**
     * Creates feedback text area.
     * @return Configured JTextArea with feedback
     */
    private JTextArea createFeedbackArea() {
        JTextArea feedbackArea = new JTextArea(5, 40);
        feedbackArea.setEditable(false);
        feedbackArea.setFont(new Font("Arial", Font.PLAIN, 12));
        feedbackArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Map<String, String> feedback = controller.getAssignmentFeedback();
        if (feedback.isEmpty()) {
            feedbackArea.setText("No feedback available.");
        } else {
            feedback.forEach((assignment, comment) -> 
                feedbackArea.append("• " + assignment + ":\n" + comment + "\n\n")
            );
        }
        return feedbackArea;
    }

    /**
     * Updates status bar with assignment count.
     */
    private void updateStatus() {
    	int gradedCount = controller.getGradesForCourse(course).size();
        double average = controller.calculateClassAverage(course);
        statusBar.setText(String.format(
            "Showing %d graded assignments | Course Average: %.1f%%", 
            gradedCount, 
            average
        ));
    }

    /**
     * Calculates course average.
     * @return Formatted average percentage
     */
    private String getCourseAverage() {
        double avg = controller.calculateClassAverage(course);
        return String.format("%.1f", avg);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("gradeAdded")) {
            SwingUtilities.invokeLater(() -> {
                setupUI();
            });
        }
    }

    /**
     * Custom renderer for percentage values.
     */
    private static class PercentageRenderer extends DefaultTableCellRenderer {
        public PercentageRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
    }
}