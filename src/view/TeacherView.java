package view;

import model.*; // Import models for data display
import controller.TeacherController; // Need controller for actions
import controller.UserController; // Maybe needed for import context

import javax.swing.*;
import javax.swing.table.DefaultTableModel; // For table data
import java.awt.*;
import java.awt.event.*; // For button clicks
import java.util.List;
import java.util.ArrayList;
import java.beans.PropertyChangeListener; // For Observer
import java.beans.PropertyChangeEvent; // For Observer

/**
 * This class TeacherView is the main GUI window for the Teacher user.
 * It shows courses, students, assignments, grades and has buttons
 * to trigger actions like adding grades or sorting.
 * Implements PropertyChangeListener to update when models change (Observer pattern).
 */
public class TeacherView extends JFrame implements PropertyChangeListener {

    // --- Controller ---
    private TeacherController teacherController;
    private UserController userController; // Might be needed for actions like import
    private Teacher currentTeacher;

    // --- GUI Components ---
    private JComboBox<Course> courseComboBox;
    private JTable studentTable;
    private JTable assignmentTable;
    // private JTable gradeEntryTable; // Decided against complex grade table for now
    private JButton addAssignmentButton;
    private JButton removeAssignmentButton;
    private JButton editAssignmentButton; // Added Edit button
    private JButton addStudentButton;
    private JButton removeStudentButton;
    private JButton addGradeButton;
    private JButton importStudentsButton;
    private JButton sortStudentsByNameButton;
    private JButton sortStudentsByGradeButton;
    private JButton setGradingModeButton;
    private JButton setupCategoriesButton;
    private JButton viewUngradedButton; // Renamed for clarity
    private JButton calculateAssignmentAverageButton; // Renamed
    private JButton assignFinalGradeButton;
    private JButton refreshDataButton; // Added refresh

    // Table models to hold the data
    private DefaultTableModel studentTableModel;
    private DefaultTableModel assignmentTableModel;

    /**
     * Constructor - sets up the frame, layout, components, and action listeners.
     * Takes the controller and logged-in teacher.
     */
    public TeacherView(TeacherController controller, Teacher teacher, UserController uCtrl) {
        this.teacherController = controller;
        this.currentTeacher = teacher;
        this.userController = uCtrl; // Store user controller

        // Basic frame setup
        this.setTitle("Teacher Dashboard - " + teacher.getUsername());
        this.setSize(950, 750); // Made slightly wider/taller
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(10, 10)); // Main layout

        // --- Top Panel: Course Selection ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(new JLabel("Select Course:"));
        courseComboBox = new JComboBox<>();
        updateCourseList(); // Fill the dropdown
        topPanel.add(courseComboBox);
        refreshDataButton = new JButton("Refresh View"); // Button to manually refresh data
        topPanel.add(refreshDataButton);
        this.add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Tabbed View ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Student Tab ---
        JPanel studentPanel = new JPanel(new BorderLayout(5, 5));
        studentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Setup table model and table
        studentTableModel = new DefaultTableModel(new Object[]{"Username", "First Name", "Last Name", "ID"}, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; } // Make table read-only
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Select one student at a time
        studentPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        // Buttons for student actions
        JPanel studentButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addStudentButton = new JButton("Add Student..."); // Needs dialog
        removeStudentButton = new JButton("Remove Selected Student");
        sortStudentsByNameButton = new JButton("Sort by Name (Last)"); // Specify sort type
        studentButtonPanel.add(addStudentButton);
        studentButtonPanel.add(removeStudentButton);
        studentButtonPanel.add(sortStudentsByNameButton);
        studentPanel.add(studentButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Students", studentPanel);

        // --- Assignment Tab ---
        JPanel assignmentPanel = new JPanel(new BorderLayout(5, 5));
        assignmentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Setup table model and table
        assignmentTableModel = new DefaultTableModel(new Object[]{"Name", "Due Date", "Points", "Category", "Graded?"}, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        assignmentTable = new JTable(assignmentTableModel);
        assignmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignmentPanel.add(new JScrollPane(assignmentTable), BorderLayout.CENTER);
        // Buttons for assignment actions
        JPanel assignmentButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addAssignmentButton = new JButton("Add Assignment..."); // Needs dialog
        editAssignmentButton = new JButton("Edit Selected Assignment..."); // Needs dialog
        removeAssignmentButton = new JButton("Remove Selected Assignment");
        viewUngradedButton = new JButton("Show Only Ungraded"); // Change text for clarity
        sortStudentsByGradeButton = new JButton("Sort Students by Grade (Selected Assign.)");
        calculateAssignmentAverageButton = new JButton("Calc Avg (Selected Assign)");
        assignmentButtonPanel.add(addAssignmentButton);
        assignmentButtonPanel.add(editAssignmentButton);
        assignmentButtonPanel.add(removeAssignmentButton);
        assignmentButtonPanel.add(viewUngradedButton); // Re-using button, maybe toggle text?
        assignmentButtonPanel.add(sortStudentsByGradeButton);
        assignmentButtonPanel.add(calculateAssignmentAverageButton);
        assignmentPanel.add(assignmentButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Assignments", assignmentPanel);

        // --- Grading Tab ---
        // Simplified this tab - focus actions on selected student/assignment
        JPanel gradesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gradesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        addGradeButton = new JButton("Add/Edit Grade for Selected..."); // Needs student+assignment selected
        assignFinalGradeButton = new JButton("Assign Final Course Grade..."); // Needs student selected
        gradesPanel.add(new JLabel("Select Student & Assignment first -> "));
        gradesPanel.add(addGradeButton);
        gradesPanel.add(new JLabel("Select Student first -> "));
        gradesPanel.add(assignFinalGradeButton);
        tabbedPane.addTab("Grading Actions", gradesPanel);

        this.add(tabbedPane, BorderLayout.CENTER);

        // --- Bottom Panel: Course Config Actions ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        importStudentsButton = new JButton("Import Students from File...");
        setGradingModeButton = new JButton("Set Course Grading Mode..."); // Needs popup
        setupCategoriesButton = new JButton("Setup Grading Categories..."); // Needs popup
        bottomPanel.add(importStudentsButton);
        bottomPanel.add(setGradingModeButton);
        bottomPanel.add(setupCategoriesButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // Connect all the button clicks etc to methods
        addActionListeners();

        System.out.println("TeacherView created with components");
        // Load data for the first course initially shown in the combo box
        displaySelectedCourseData();
        // Controller should make it visible after setup usually
        // this.setVisible(true);
    }

    /**
     * updateCourseList fills the course dropdown JComboBox.
     * It asks the teacher controller for the list of courses the teacher teaches.
     * Clears the box first then adds each Course object. toString() shows name.
     */
    private void updateCourseList() {
        // Check if controller exists
        boolean haveController = (teacherController != null);
        if (!haveController) return;

        // Remember which course was selected, if any
        Object previouslySelected = courseComboBox.getSelectedItem();

        // Clear out old items
        courseComboBox.removeAllItems();

        // Get the fresh list of courses for this teacher
        List<Course> courses = teacherController.viewCourses();

        // Check if the list isn't null
        boolean coursesExist = (courses != null);
        if (coursesExist) {
            // Add each Course object directly to the combo box
            for (Course c : courses) {
                courseComboBox.addItem(c);
            }
        }

        // Try to re-select the previously selected course if it's still in the list
        if (previouslySelected instanceof Course && courses.contains(previouslySelected)) {
             courseComboBox.setSelectedItem(previouslySelected);
        } else if (courseComboBox.getItemCount() > 0) {
             // Otherwise select the first one if list isn't empty
             courseComboBox.setSelectedIndex(0);
        }
    }

    /**
     * displaySelectedCourseData gets called when the course dropdown changes.
     * It gets the selected Course object, then asks the controller for the
     * students and assignments for that course and updates the JTables.
     */
    private void displaySelectedCourseData() {
        // Find out which Course object is currently selected in the dropdown
        Object selectedItem = courseComboBox.getSelectedItem();
        Course selectedCourse = null;
        if (selectedItem instanceof Course) {
            selectedCourse = (Course) selectedItem;
        }

        // If nothing is selected, or we lost the controller, clear the tables.
        boolean haveValidSelection = (selectedCourse != null && teacherController != null);
        if (!haveValidSelection) {
            studentTableModel.setRowCount(0); // Clear student table
            assignmentTableModel.setRowCount(0); // Clear assignment table
            return;
        }

        // --- Update Student Table ---
        studentTableModel.setRowCount(0); // Clear old student rows
        // Ask controller for students in the selected course
        List<Student> students = teacherController.viewStudentsInCourse(selectedCourse);
        boolean studentsExist = (students != null);
        if (studentsExist) {
            // Loop through the student list
            for (Student s : students) {
                // Add a row to the student table model with this student's info
                studentTableModel.addRow(new Object[]{
                    s.getUsername(),
                    s.getFirstName(),
                    s.getLastName(),
                    s.getStudentId()
                });
            }
        }

        // --- Update Assignment Table ---
        assignmentTableModel.setRowCount(0); // Clear old assignment rows
        // Ask the course model directly for its assignments (controller method isn't strictly needed here)
        List<Assignment> assignments = selectedCourse.getAllAssignments();
        boolean assignmentsExist = (assignments != null);
        if (assignmentsExist) {
            // Loop through the assignment list
            for (Assignment a : assignments) {
                // Add a row to the assignment table model
                String dueDateStr = (a.getDueDate() != null) ? a.getDueDate() : "Not Set"; // Use actual date later maybe
                String categoryNameStr = (a.getCategoryName() != null) ? a.getCategoryName() : "None";
                String isGradedStr = a.isGraded() ? "Yes" : "No";
                assignmentTableModel.addRow(new Object[]{
                    a.getName(),
                    dueDateStr,
                    a.getPointsWorth(),
                    categoryNameStr,
                    isGradedStr
                });
            }
        }

        // TODO: Update grade entry area later if needed
    }

    /**
     * addActionListeners connects all the buttons to do stuff.
     * Right now, most just print a TODO message. We need to implement
     * the actual logic, like showing dialogs and calling controller methods.
     * Using lambda expressions -> for simple listeners.
     */
    private void addActionListeners() {
        // When the course dropdown selection changes, update the tables
        courseComboBox.addActionListener(e -> displaySelectedCourseData());

        // Refresh button just reloads data for the currently selected course
        refreshDataButton.addActionListener(e -> displaySelectedCourseData());

        // Sort students by last name (example, make more flexible later)
        sortStudentsByNameButton.addActionListener(e -> {
            System.out.println("Sort Students by Name button pressed TODO");
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            if (selectedCourse != null && teacherController != null) {
                List<Student> sortedStudents = teacherController.sortStudentsByName(selectedCourse, true, true); // Sort last name A-Z
                // Update table display
                studentTableModel.setRowCount(0);
                if (sortedStudents != null) {
                    for (Student s : sortedStudents) {
                         studentTableModel.addRow(new Object[]{s.getUsername(), s.getFirstName(), s.getLastName(), s.getStudentId()});
                    }
                }
            }
        });

        // Sort students by grade on selected assignment
        sortStudentsByGradeButton.addActionListener(e -> {
             System.out.println("Sort Students by Grade button pressed TODO");
             Course selectedCourse = (Course) courseComboBox.getSelectedItem();
             int selectedRow = assignmentTable.getSelectedRow();
             if (selectedCourse != null && selectedRow >= 0 && teacherController != null) {
                 // Need to get the actual Assignment object from the selected table row
                 // This is tricky with DefaultTableModel, better if table stores Assignment objects directly
                 // For now, assume we can get it somehow (maybe by name lookup - less robust)
                 String assignmentName = (String) assignmentTableModel.getValueAt(selectedRow, 0);
                 Assignment selectedAssignment = findAssignmentInCourse(selectedCourse, assignmentName);

                 if(selectedAssignment != null) {
                     List<Student> sortedStudents = teacherController.sortStudentsByGrade(selectedAssignment, selectedCourse, false); // Sort highest first
                     // Update student table display
                     studentTableModel.setRowCount(0);
                     if (sortedStudents != null) {
                         for (Student s : sortedStudents) {
                              studentTableModel.addRow(new Object[]{s.getUsername(), s.getFirstName(), s.getLastName(), s.getStudentId()});
                         }
                     }
                 } else {
                      JOptionPane.showMessageDialog(this, "Could not find selected assignment.", "Error", JOptionPane.ERROR_MESSAGE);
                 }
             } else {
                 JOptionPane.showMessageDialog(this, "Please select a course and an assignment first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
             }
        });

        // Calculate average for selected assignment
        calculateAssignmentAverageButton.addActionListener(e -> {
            System.out.println("Calculate Assignment Average button pressed TODO");
            Course selectedCourse = (Course) courseComboBox.getSelectedItem(); // Need course context? Maybe not.
            int selectedRow = assignmentTable.getSelectedRow();
            if (selectedRow >= 0 && teacherController != null && selectedCourse != null) {
                 String assignmentName = (String) assignmentTableModel.getValueAt(selectedRow, 0);
                 Assignment selectedAssignment = findAssignmentInCourse(selectedCourse, assignmentName);
                 if (selectedAssignment != null) {
                     double avg = teacherController.calculateClassAverage(selectedAssignment);
                     JOptionPane.showMessageDialog(this, "Class average for " + selectedAssignment.getName() + ": " + String.format("%.2f", avg), "Class Average", JOptionPane.INFORMATION_MESSAGE);
                 } else {
                      JOptionPane.showMessageDialog(this, "Could not find selected assignment.", "Error", JOptionPane.ERROR_MESSAGE);
                 }
            } else {
                 JOptionPane.showMessageDialog(this, "Please select an assignment first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        // --- Buttons needing Dialogs or more complex interaction ---
        addStudentButton.addActionListener(e -> {
            System.out.println("Add Student button pressed TODO - Needs Dialog/Popup");
            // Show dialog -> get student username -> find student -> call controller.addStudentToCourse -> refreshData
        });

        removeStudentButton.addActionListener(e -> {
            System.out.println("Remove Student button pressed TODO - Needs selection handling");
            // Get selected student from table -> confirm -> call controller.removeStudentFromCourse -> refreshData
        });

        addAssignmentButton.addActionListener(e -> {
            System.out.println("Add Assignment button pressed TODO - Needs Dialog/Popup");
            // Show dialog -> get assignment details -> create Assignment -> call controller.addAssignmentToCourse -> refreshData
        });

        editAssignmentButton.addActionListener(e -> {
             System.out.println("Edit Assignment button pressed TODO - Needs Dialog/Popup");
             // Get selected assignment -> Show AssignmentView dialog -> pass assignment data -> connect save button
             // Maybe open AssignmentView directly:
             // Assignment selectedAssignment = getSelectedAssignmentFromTable();
             // if (selectedAssignment != null) {
             //    AssignmentView editView = new AssignmentView(selectedAssignment); // Needs AssignmentController passed too?
             //    editView.setVisible(true);
             // }
        });

        removeAssignmentButton.addActionListener(e -> {
            System.out.println("Remove Assignment button pressed TODO - Needs selection handling");
            // Get selected assignment -> confirm -> call controller.removeAssignmentFromCourse -> refreshData
        });

        viewUngradedButton.addActionListener(e -> {
            System.out.println("View Ungraded button pressed TODO - Needs table filtering logic");
            // Maybe toggle button text "Show All"?
            // Get selected course -> call controller.viewUngradedAssignments -> update assignment table model
        });

        addGradeButton.addActionListener(e -> {
            System.out.println("Add Grade button pressed TODO - Needs Dialog/Popup and student/assignment selection");
            // Get selected student -> Get selected assignment -> Show dialog for score/feedback -> call controller.addGrade
        });

        importStudentsButton.addActionListener(e -> {
            System.out.println("Import Students button pressed TODO - Needs JFileChooser");
            // Show JFileChooser -> get file path -> call controller.importStudentsFromFile -> refreshData
        });

        setGradingModeButton.addActionListener(e -> {
             System.out.println("Set Grading Mode button pressed TODO - Needs Dialog");
             // Get selected course -> show dialog (Points/Category) -> call controller.setCourseGradingMode
        });

        setupCategoriesButton.addActionListener(e -> {
             System.out.println("Setup Categories button pressed TODO - Needs Dialog");
             // Get selected course -> show dialog to define categories/weights/drops -> create List<GradingCategory> -> call controller.setupAssignmentCategories
        });

        assignFinalGradeButton.addActionListener(e -> {
            System.out.println("Assign Final Grade button pressed TODO - Needs Dialog");
            // Get selected student -> get selected course -> show dialog for letter grade -> call controller.assignFinalGrade
        });
    }

    /**
     * Helper method to find an Assignment object in the current course by name.
     * Needed because JTable stores Strings, not the objects directly easily.
     * This isn't super efficient if there are many assignments.
     */
     private Assignment findAssignmentInCourse(Course course, String assignmentName) {
         if (course == null || assignmentName == null) return null;
         for (Assignment a : course.getAllAssignments()) {
             if (a.getName().equals(assignmentName)) {
                 return a;
             }
         }
         return null; // Not found
     }

     /**
     * findStudentInCourse is a helper to get the actual Student object
     * based on the username selected in the student table.
     * @param course The currently selected course.
     * @param username The username from the table row.
     * @return The Student object or null if not found.
     */
    private Student findStudentInCourse(Course course, String username) {
        if (course == null || username == null) return null;
        for (Student s : course.getEnrolledStudents()) { // getEnrolledStudents gives a copy
            if (s.getUsername().equals(username)) {
                return s;
            }
        }
        return null; // Not found
    }


    // --- Observer Method ---

    /**
     * This method gets called by the Models (like Course, Student)
     * when their data changes, because this View is registered as a listener.
     * TODO: Implement the logic to update specific parts of the GUI
     *       based on the property name that changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("TeacherView detected property change: " + evt.getPropertyName());
        // Get the name of the property that changed
        String propertyName = evt.getPropertyName();

        // Decide what to refresh based on the property name
        // This needs to match the names used in model's firePropertyChange calls
        if ("studentEnrolled".equals(propertyName) || "studentRemoved".equals(propertyName)) {
            // If students changed in the current course, refresh student table
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            Object source = evt.getSource(); // Check if the source is the currently selected course?
            if (source instanceof Course && source.equals(selectedCourse)) {
                 System.out.println("Refreshing student table due to enrollment change...");
                 displaySelectedCourseData(); // Reload both tables for simplicity now
            }
        } else if ("assignmentAdded".equals(propertyName) /* || "assignmentRemoved".equals(propertyName) */) {
             // If assignments changed in the current course, refresh assignment table
             Course selectedCourse = (Course) courseComboBox.getSelectedItem();
             Object source = evt.getSource();
             if (source instanceof Course && source.equals(selectedCourse)) {
                  System.out.println("Refreshing assignment table due to assignment change...");
                  displaySelectedCourseData(); // Reload both tables for simplicity now
             }
        } else if ("gradeAdded".equals(propertyName) /* || maybe other grade changes */) {
            // If a grade changed, maybe refresh grade display areas or averages?
            // This is more complex - might need more specific info from event
             System.out.println("Grade changed event detected - TODO: update relevant views");
             // Maybe just refresh everything for now?
             displaySelectedCourseData();
        }
        // Add more else if blocks for other properties as needed
    }

    // --- Getters for Components (Needed for Controller interaction) ---
    public JComboBox<Course> getCourseComboBox() { return courseComboBox; }
    public JTable getStudentTable() { return studentTable; }
    public JTable getAssignmentTable() { return assignmentTable; }
    public JButton getAddAssignmentButton() { return addAssignmentButton; }
    public JButton getRemoveAssignmentButton() { return removeAssignmentButton; }
    public JButton getEditAssignmentButton() { return editAssignmentButton; }
    public JButton getAddStudentButton() { return addStudentButton; }
    public JButton getRemoveStudentButton() { return removeStudentButton; }
    public JButton getAddGradeButton() { return addGradeButton; }
    public JButton getImportStudentsButton() { return importStudentsButton; }
    public JButton getSortStudentsByNameButton() { return sortStudentsByNameButton; }
    public JButton getSortStudentsByGradeButton() { return sortStudentsByGradeButton; }
    public JButton getSetGradingModeButton() { return setGradingModeButton; }
    public JButton getSetupCategoriesButton() { return setupCategoriesButton; }
    public JButton getViewUngradedButton() { return viewUngradedButton; }
    public JButton getCalculateAssignmentAverageButton() { return calculateAssignmentAverageButton; }
    public JButton getAssignFinalGradeButton() { return assignFinalGradeButton; }
    public JButton getRefreshDataButton() { return refreshDataButton; }

}