package view;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/* We could also think about having 2 different course views as StudentCourseView and TeacherCourseView 
 * To be discussed later 
 * */
public class CourseView extends JFrame {
    private JLabel courseTitle;
    private JButton backButton;

    private JTable rosterTable;
    private JTable assignmentTable;
    private JTable gradeTable;
    private JTable myGradeTable;

    public CourseView(User user, Course course) {
        setTitle("Course: " + course.getName());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top panel with course title and back button
        JPanel topPanel = new JPanel(new BorderLayout());
        courseTitle = new JLabel("Course: " + course.getName());
        courseTitle.setFont(new Font("Arial", Font.BOLD, 20));
        backButton = new JButton("Back to Main");
        topPanel.add(courseTitle, BorderLayout.WEST);
        topPanel.add(backButton, BorderLayout.EAST);

        // Tabbed content area
        JTabbedPane tabbedPane = new JTabbedPane();
        
/* The logic to show different views for teacher and students
 * if the instance is teacher, show specific elements for teacher
 * if the instance is student, show specific elements for student
 * 
 * */
       
        if (user instanceof Teacher) {
            // Roster tab (teacher only)
            rosterTable = new JTable(); // To be filled later
            tabbedPane.addTab("Roster", new JScrollPane(rosterTable));

            // Assignments tab
            assignmentTable = new JTable();
            tabbedPane.addTab("Assignments", new JScrollPane(assignmentTable));

            // Grades tab (all students)
            gradeTable = new JTable();
            tabbedPane.addTab("Grades", new JScrollPane(gradeTable));
        } else if (user instanceof Student) {
            // Assignments tab (student view)
            assignmentTable = new JTable();
            tabbedPane.addTab("Assignments", new JScrollPane(assignmentTable));

            // My Grades tab (just their grades)
            myGradeTable = new JTable();
            tabbedPane.addTab("My Grades", new JScrollPane(myGradeTable));
        }

        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    // To be implemented to controllers later 
    public JButton getBackButton() {
        return backButton;
    }

    public JTable getRosterTable() {
        return rosterTable;
    }

    public JTable getAssignmentTable() {
        return assignmentTable;
    }

    public JTable getGradeTable() {
        return gradeTable;
    }

    public JTable getMyGradeTable() {
        return myGradeTable;
    }
    public static void main(String[] args) {
        // Step 1: Create course and teacher
        Course course = new Course("CSC 335", "335", "Spring 2025", false);
        Teacher melanie = new Teacher("Melanie", "Lotz", "mlotz@cs.arizona.edu", "pass123", "mlotz", "T999");
        melanie.addCourse(course);

        // Step 2: Create assignments
        Assignment hw1 = new Assignment("HW 1", 100, "2025-04-30", "Homework", "Group A");
        Assignment hw2 = new Assignment("HW 2", 100, "2025-05-07", "Homework", "Group A");
        Assignment quiz1 = new Assignment("Quiz 1", 50, "2025-05-01", "Quiz", "Group A");

        course.addAssignment(hw1);
        course.addAssignment(hw2);
        course.addAssignment(quiz1);

        // Step 3: Create students
        Student s1 = new Student("Alice", "Nguyen", "alice@uofa.edu", "pass", "alice123", "S001");
        Student s2 = new Student("Brian", "Lopez", "brian@uofa.edu", "pass", "brian456", "S002");
        Student s3 = new Student("Clara", "Zhao", "clara@uofa.edu", "pass", "clara789", "S003");

        course.enrollStudent(s1);
        course.enrollStudent(s2);
        course.enrollStudent(s3);

        // Step 4: Assign grades
        s1.addGrade(hw1, new Grade(95, "Great job"));
        s1.addGrade(hw2, new Grade(88, "Well done"));
        s1.addGrade(quiz1, new Grade(45, "Almost perfect"));

        s2.addGrade(hw1, new Grade(78, "Try harder"));
        s2.addGrade(hw2, new Grade(82, "Good progress"));
        s2.addGrade(quiz1, new Grade(40, "Good effort"));

        s3.addGrade(hw1, new Grade(100, "Excellent"));
        s3.addGrade(hw2, new Grade(95, "Very good"));
        s3.addGrade(quiz1, new Grade(50, "Perfect"));

        // Step 5: Show CourseView with filled data
        SwingUtilities.invokeLater(() -> {
            CourseView view = new CourseView(melanie, course);

            // === Fill Roster Table ===
            String[] rosterColumns = { "Name", "ID", "Email" };
            Object[][] rosterData = {
                { s1.getFirstName() + " " + s1.getLastName(), s1.getStudentId(), s1.getEmail() },
                { s2.getFirstName() + " " + s2.getLastName(), s2.getStudentId(), s2.getEmail() },
                { s3.getFirstName() + " " + s3.getLastName(), s3.getStudentId(), s3.getEmail() },
            };
            view.getRosterTable().setModel(new javax.swing.table.DefaultTableModel(rosterData, rosterColumns));

            // === Fill Assignment Table ===
            String[] assignmentCols = { "Name", "Due Date", "Points" };
            Object[][] assignmentData = {
                { hw1.getName(), hw1.getDueDate(), hw1.getPointsWorth() },
                { hw2.getName(), hw2.getDueDate(), hw2.getPointsWorth() },
                { quiz1.getName(), quiz1.getDueDate(), quiz1.getPointsWorth() },
            };
            view.getAssignmentTable().setModel(new javax.swing.table.DefaultTableModel(assignmentData, assignmentCols));

            // === Fill Grades Table ===
            String[] gradeCols = { "Student", "HW 1", "HW 2", "Quiz 1" };
            Object[][] gradeData = {
                { s1.getUsername(), s1.getGradeForAssignment(hw1).getPointsEarned(), s1.getGradeForAssignment(hw2).getPointsEarned(), s1.getGradeForAssignment(quiz1).getPointsEarned() },
                { s2.getUsername(), s2.getGradeForAssignment(hw1).getPointsEarned(), s2.getGradeForAssignment(hw2).getPointsEarned(), s2.getGradeForAssignment(quiz1).getPointsEarned() },
                { s3.getUsername(), s3.getGradeForAssignment(hw1).getPointsEarned(), s3.getGradeForAssignment(hw2).getPointsEarned(), s3.getGradeForAssignment(quiz1).getPointsEarned() },
            };
            view.getGradeTable().setModel(new javax.swing.table.DefaultTableModel(gradeData, gradeCols));

            view.setVisible(true);
        });
    }

    
    
}
