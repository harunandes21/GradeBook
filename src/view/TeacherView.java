package view;

import model.*; // Import models for data display and types
import model.grading.GradeCalculator; // For setting mode
import model.grading.PointsBasedCalculator;
import model.grading.CategoryBasedCalculator;
import controller.TeacherController; // Need controller for actions
import controller.AssignmentController; // Need controller for editing assignments
import controller.UserController; // Need controller for user lookup maybe

import javax.swing.*;
import javax.swing.table.DefaultTableModel; // For table data
import javax.swing.table.TableRowSorter; // For potential table sorting later maybe
import java.awt.*;
import java.awt.event.*; // For button clicks
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator; // For category sorting in dialog
import java.beans.PropertyChangeListener; // For Observer
import java.beans.PropertyChangeEvent; // For Observer
import java.io.File; // For file chooser

/**
 * This class TeacherView is the main GUI window for the Teacher user.
 * It shows courses dropdown, tabs for students and assignments in tables,
 * buttons for teacher actions like adding grades, managing students/assignments,
 * setting up the course, and sorting views.
 * It listens PropertyChangeListener for model changes Observer pattern to update itself.
 */
public class TeacherView extends JFrame implements PropertyChangeListener {

    // --- Controllers ---
    // need references to controllers to handle actions
    private TeacherController teacherController;
    private AssignmentController assignmentController; // Needed for Edit Assignment action
    private UserController userController; // Needed for Import action
    private Teacher currentTeacher;

    // --- GUI Components ---
    private JComboBox<Course> courseComboBox;
    private JTable studentTable;
    private JTable assignmentTable;
    private JButton addAssignmentButton;
    private JButton removeAssignmentButton;
    private JButton editAssignmentButton;
    private JButton addStudentButton;
    private JButton removeStudentButton;
    private JButton addGradeButton;
    private JButton importStudentsButton;
    private JButton sortStudentsByNameButton; // Will toggle sort direction maybe
    private JButton sortStudentsByGradeButton;
    private JButton setGradingModeButton;
    private JButton setupCategoriesButton;
    private JButton viewUngradedButton; // Will toggle text/functionality
    private JButton calculateAssignmentStatsButton;
    private JButton assignFinalGradeButton;
    private JButton refreshDataButton;

    // Table models hold the data for JTables, allows easy updates
    private DefaultTableModel studentTableModel;
    private DefaultTableModel assignmentTableModel;

    // state for the view ungraded button
    private boolean showingOnlyUngraded = false;

    /**
     * Constructor - sets up the frame, layout, components, and action listeners.
     * Takes the controllers and logged-in teacher. Builds the whole UI.
     * Registers itself as a listener to the courses.
     */
    public TeacherView(TeacherController controller, Teacher teacher, UserController uCtrl, AssignmentController assignCtrl) {
        this.teacherController = controller;
        this.currentTeacher = teacher;
        this.userController = uCtrl;
        this.assignmentController = assignCtrl; // Store assignment controller

        // Register listener for courses this teacher teaches
        registerCourseListeners();

        // Basic frame setup
        this.setTitle("Teacher Dashboard - " + teacher.getUsername());
        this.setSize(950, 750);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Course Selection & Refresh ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(new JLabel("Select Course:"));
        courseComboBox = new JComboBox<>();
        updateCourseList(); // Fill the dropdown initially
        topPanel.add(courseComboBox);
        refreshDataButton = new JButton("Refresh Current View");
        topPanel.add(refreshDataButton);
        this.add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Tabbed View ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Student Tab ---
        JPanel studentPanel = new JPanel(new BorderLayout(5, 5));
        studentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Setup table model - make cells not directly editable in the table
        studentTableModel = new DefaultTableModel(new Object[]{"Username", "First Name", "Last Name", "ID"}, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only select one student
        studentPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER); // Put table in scrollpane
        // Panel for student related buttons
        JPanel studentButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addStudentButton = new JButton("Add Existing Student...");
        removeStudentButton = new JButton("Remove Selected Student");
        sortStudentsByNameButton = new JButton("Sort Name (Last A-Z)"); // Default sort shown
        studentButtonPanel.add(addStudentButton);
        studentButtonPanel.add(removeStudentButton);
        studentButtonPanel.add(sortStudentsByNameButton);
        studentPanel.add(studentButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Students", studentPanel);

        // --- Assignment Tab ---
        JPanel assignmentPanel = new JPanel(new BorderLayout(5, 5));
        assignmentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Setup assignment table model - not editable
        assignmentTableModel = new DefaultTableModel(new Object[]{"Name", "Due Date", "Points", "Category", "Graded?"}, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        assignmentTable = new JTable(assignmentTableModel);
        assignmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only select one assignment
        assignmentPanel.add(new JScrollPane(assignmentTable), BorderLayout.CENTER); // Table in scrollpane
        // Panel for assignment related buttons
        JPanel assignmentButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addAssignmentButton = new JButton("Add Assignment...");
        editAssignmentButton = new JButton("Edit Selected Assignment...");
        removeAssignmentButton = new JButton("Remove Selected Assignment");
        viewUngradedButton = new JButton("Show Only Ungraded"); // Button text will change
        sortStudentsByGradeButton = new JButton("Sort Students by Grade (Selected Assign.)");
        calculateAssignmentStatsButton = new JButton("Calc Assign Stats"); // Avg/Median
        assignmentButtonPanel.add(addAssignmentButton);
        assignmentButtonPanel.add(editAssignmentButton);
        assignmentButtonPanel.add(removeAssignmentButton);
        assignmentButtonPanel.add(viewUngradedButton);
        assignmentButtonPanel.add(sortStudentsByGradeButton);
        assignmentButtonPanel.add(calculateAssignmentStatsButton);
        assignmentPanel.add(assignmentButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Assignments", assignmentPanel);

        // --- Grading Tab ---
        // Simple tab with buttons that require selections in other tabs first
        JPanel gradesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gradesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        addGradeButton = new JButton("Add/Edit Grade for Selected...");
        assignFinalGradeButton = new JButton("Assign Final Course Grade...");
        gradesPanel.add(new JLabel("Select Student & Assignment -> "));
        gradesPanel.add(addGradeButton);
        gradesPanel.add(new JLabel("Select Student -> "));
        gradesPanel.add(assignFinalGradeButton);
        tabbedPane.addTab("Grading Actions", gradesPanel);

        // Add tabbed pane to the center of the window
        this.add(tabbedPane, BorderLayout.CENTER);

        // --- Bottom Panel: Course Config Actions ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        importStudentsButton = new JButton("Import Students from File...");
        setGradingModeButton = new JButton("Set Course Grading Mode...");
        setupCategoriesButton = new JButton("Setup Grading Categories...");
        bottomPanel.add(importStudentsButton);
        bottomPanel.add(setGradingModeButton);
        bottomPanel.add(setupCategoriesButton);
        // Add bottom panel to the bottom of the window
        this.add(bottomPanel, BorderLayout.SOUTH);

        // Connect all the buttons to their action listener code
        addActionListeners();

        System.out.println("TeacherView created with components");
        // Load initial data for the first course selected in the dropdown
        displaySelectedCourseData();
    }

    /**
     * updateCourseList is a helper method to fill the course JComboBox.
     * It asks the teacher controller for the teacher's course list.
     * Clears the box first then adds each Course object. Also registers listener.
     */
    private void updateCourseList() {
        boolean haveController = (teacherController != null);
        if (!haveController) return; // Need controller
        Object previouslySelected = courseComboBox.getSelectedItem(); // Remember selection

        // Remove listener from old courses? Maybe not needed if objects are reused.
        // For now, just re-add listener below.

        courseComboBox.removeAllItems(); // Clear dropdown
        List<Course> courses = teacherController.viewCourses(); // Get current list

        boolean coursesExist = (courses != null && !courses.isEmpty());
        if (coursesExist) {
            for (Course c : courses) {
                courseComboBox.addItem(c); // Add Course object
                // Make sure we listen to this course object for changes
                c.addPropertyChangeListener(this);
            }
        }

        // Try to re-select the course that was selected before, if it still exists
        boolean previousWasCourse = (previouslySelected instanceof Course);
        boolean previousStillExists = (courses != null && courses.contains(previouslySelected));
        if (previousWasCourse && previousStillExists) {
             courseComboBox.setSelectedItem(previouslySelected);
        } else if (courseComboBox.getItemCount() > 0) {
             // If previous selection gone or none existed, select the first one
             courseComboBox.setSelectedIndex(0);
        }
    }

    /**
     * displaySelectedCourseData is helper called when course dropdown changes or refresh.
     * Gets selected Course. Asks controller for students. Asks course for assignments.
     * Updates the student and assignment JTables based on filter state showingOnlyUngraded.
     */
    private void displaySelectedCourseData() {
        System.out.println("TeacherView refreshing displayed data");
        // Get the Course object from the dropdown
        Object selectedItem = courseComboBox.getSelectedItem();
        Course selectedCourse = null;
        if (selectedItem instanceof Course) {
            selectedCourse = (Course) selectedItem;
        }

        // If no course selected or controller missing, clear tables and stop
        boolean haveValidSelection = (selectedCourse != null && teacherController != null);
        if (!haveValidSelection) {
            studentTableModel.setRowCount(0);
            assignmentTableModel.setRowCount(0);
            return;
        }

        // Update Student Table
        updateStudentTable(teacherController.viewStudentsInCourse(selectedCourse));

        // Update Assignment Table based on filter state
        updateAssignmentTable(selectedCourse);
    }

    /**
     * addActionListeners connects all the buttons to their corresponding actions.
     * Uses lambda expressions for the ActionListener code.
     * Calls appropriate teacherController methods. Shows popups for info/errors/input.
     */
    private void addActionListeners() {
        // When course dropdown changes, reload all data for that course
        courseComboBox.addActionListener(e -> displaySelectedCourseData());

        // Refresh button just reloads data for current course
        refreshDataButton.addActionListener(e -> displaySelectedCourseData());

        // Sort students button: sort by last name A-Z, update table
        sortStudentsByNameButton.addActionListener(e -> {
            System.out.println("Sort Students by Name button clicked");
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            if (selectedCourse != null && teacherController != null) {
                // TODO: Add option to toggle sort direction maybe later
                List<Student> sortedStudents = teacherController.sortStudentsByName(selectedCourse, true, true); // Last name, A-Z
                updateStudentTable(sortedStudents); // Refresh table with sorted list
            } else {
                 showError("Select a course first.");
            }
        });

        // Sort students by grade button: sort by grade on selected assignment, highest first
        sortStudentsByGradeButton.addActionListener(e -> {
             System.out.println("Sort Students by Grade button clicked");
             Course selectedCourse = (Course) courseComboBox.getSelectedItem();
             Assignment selectedAssignment = getSelectedAssignmentFromTable(); // Use helper
             if (selectedCourse != null && selectedAssignment != null && teacherController != null) {
                 List<Student> sortedStudents = teacherController.sortStudentsByGrade(selectedAssignment, selectedCourse, false); // Highest first
                 updateStudentTable(sortedStudents); // Refresh student table with sorted list
             } else {
                 showError("Select a course and an assignment first.");
             }
        });

        // Calculate assignment stats button: calc avg/median for selected assignment
        calculateAssignmentStatsButton.addActionListener(e -> {
            System.out.println("Calculate Assignment Stats button clicked");
            Assignment selectedAssignment = getSelectedAssignmentFromTable(); // Use helper
            if (selectedAssignment != null && teacherController != null) {
                 // Call controller methods for both stats
                 double avg = teacherController.calculateClassAverage(selectedAssignment);
                 double median = teacherController.calculateClassMedian(selectedAssignment);
                 // Show results in a popup message
                 String message = "Stats for '" + selectedAssignment.getName() + "':\n"
                                + "Average Score: " + String.format("%.2f", avg) + "\n"
                                + "Median Score: " + String.format("%.2f", median);
                 showInfo(message);
            } else {
                 showError("Select an assignment first.");
            }
        });

        // Add Student button: popup asks for username, finds student, adds to course
        addStudentButton.addActionListener(e -> {
            System.out.println("Add Student button clicked");
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            if (selectedCourse == null) { showError("Select a course first."); return; }
            // Simple popup for username
            String username = JOptionPane.showInputDialog(this, "Enter username of EXISTING student to add:");
            boolean usernameEntered = (username != null && !username.trim().isEmpty());
            if (usernameEntered) {
                // Use controller helper to find Student object
                Student student = teacherController.getStudentByUsername(username.trim());
                boolean studentFound = (student != null);
                if (studentFound) {
                    // If found, try adding to course
                    teacherController.addStudentToCourse(student, selectedCourse);
                    // Observer should update view, but show message anyway
                    showInfo("Add student attempt finished check list.");
                } else {
                    showError("Student username '" + username + "' not found.");
                }
            }
        });

        // Remove selected student button: confirm then remove
        removeStudentButton.addActionListener(e -> {
            System.out.println("Remove Student button clicked");
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            Student selectedStudent = getSelectedStudentFromTable(); // Use helper
            if (selectedCourse != null && selectedStudent != null && teacherController != null) {
                // Ask user to confirm
                int confirm = JOptionPane.showConfirmDialog(this, "Remove student '" + selectedStudent.getUsername() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Tell controller to remove
                    teacherController.removeStudentFromCourse(selectedStudent, selectedCourse);
                    // Observer should update view, show message
                    showInfo("Remove student attempt finished check list.");
                }
            } else { showError("Select a course and a student first."); }
        });

        // Add Assignment button: opens AssignmentView dialog for creation
        addAssignmentButton.addActionListener(e -> {
            System.out.println("Add Assignment button pressed");
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            if (selectedCourse == null) { showError("Select a course first."); return; }
            // Need the AssignmentController
            if (assignmentController == null) { showError("AssignmentController missing."); return; }
            // Create AssignmentView dialog, pass null for assignment to indicate new
            AssignmentView assignmentDialog = new AssignmentView(this, assignmentController, teacherController, selectedCourse, null);
            assignmentDialog.setVisible(true); // Show the popup dialog
            // After dialog closes, the Observer pattern should update the assignment table if save worked
        });

        // Edit Assignment button: opens AssignmentView dialog for editing
        editAssignmentButton.addActionListener(e -> {
             System.out.println("Edit Assignment button pressed");
             Course selectedCourse = (Course) courseComboBox.getSelectedItem();
             Assignment selectedAssignment = getSelectedAssignmentFromTable(); // Use helper
             // Check we have everything needed
             boolean canEdit = (selectedCourse != null && selectedAssignment != null && assignmentController != null);
             if (canEdit) {
                 // Create and show the AssignmentView dialog, passing the assignment to edit
                 AssignmentView editDialog = new AssignmentView(this, assignmentController, teacherController, selectedCourse, selectedAssignment);
                 editDialog.setVisible(true);
                 // Observer pattern should update this view if save was successful inside dialog
             } else {
                 showError("Select a course and an assignment to edit, or controller missing.");
             }
        });

        // Remove selected assignment button: confirm then remove
        removeAssignmentButton.addActionListener(e -> {
            System.out.println("Remove Assignment button pressed");
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            Assignment selectedAssignment = getSelectedAssignmentFromTable(); // Use helper
            if (selectedCourse != null && selectedAssignment != null && teacherController != null) {
                 // Ask user to confirm first
                 int confirm = JOptionPane.showConfirmDialog(this, "Remove assignment '" + selectedAssignment.getName() + "' and all its grades?", "Confirm", JOptionPane.YES_NO_OPTION);
                 if (confirm == JOptionPane.YES_OPTION) {
                      // Tell controller to remove
                      teacherController.removeAssignmentFromCourse(selectedAssignment, selectedCourse);
                      // Observer should update table, show message
                      showInfo("Remove assignment attempt finished check list.");
                 }
            } else { showError("Select a course and assignment first."); }
        });

        // View Ungraded / All button: toggles state and refreshes assignment table
        viewUngradedButton.addActionListener(e -> {
            System.out.println("View Ungraded/All button pressed");
            showingOnlyUngraded = !showingOnlyUngraded; // Flip the state flag
            displaySelectedCourseData(); // Reload assignment table based on new state
        });

        // Add Grade button needs popup
        addGradeButton.addActionListener(e -> {
            System.out.println("Add Grade button pressed");
            // Get selected student and assignment from tables
            Student selectedStudent = getSelectedStudentFromTable();
            Assignment selectedAssignment = getSelectedAssignmentFromTable();
            // Check if both are selected
            boolean canGrade = (selectedStudent != null && selectedAssignment != null && teacherController != null);
            if (canGrade) {
                 // TODO: Make a better JDialog for this instead of multiple popups
                 // Simple input for now
                 String currentScoreStr = ""; // Show current score maybe?
                 Grade currentGrade = selectedStudent.getGradeForAssignment(selectedAssignment);
                 if (currentGrade != null) {
                      currentScoreStr = String.valueOf(currentGrade.getPointsEarned());
                 }
                 String scoreStr = JOptionPane.showInputDialog(this, "Enter score for " + selectedStudent.getFirstName() + " on " + selectedAssignment.getName() + ":", currentScoreStr);
                 if (scoreStr == null) return; // User cancelled score input

                 String currentFeedback = (currentGrade != null ? currentGrade.getFeedback() : "");
                 String feedback = JOptionPane.showInputDialog(this, "Enter feedback (optional):", currentFeedback);
                 if (feedback == null) feedback = ""; // Handle cancel on feedback

                 // Try to parse score and call controller
                 try {
                      double score = Double.parseDouble(scoreStr.trim());
                      boolean saved = teacherController.addGrade(selectedStudent, selectedAssignment, score, feedback);
                      if (saved) {
                           showInfo("Grade saved.");
                           // Observer should update views eventually
                      } else { showError("Failed to save grade check console."); }
                 } catch (NumberFormatException ex) { showError("Invalid score number entered."); }
            } else { showError("Select a student AND an assignment first."); }
        });

        // Import Students button uses JFileChooser
        importStudentsButton.addActionListener(e -> {
            System.out.println("Import Students button pressed");
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            if (selectedCourse == null) { showError("Select a course first."); return; }
            // Make file chooser dialog
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Student CSV File (username,first,last)");
            // Show the dialog to choose file
            int result = fileChooser.showOpenDialog(this);
            // Check if user actually chose a file
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                // Call controller method to handle the import process
                boolean success = teacherController.importStudentsFromFile(selectedFile.getAbsolutePath(), selectedCourse);
                if (success) {
                    showInfo("Student import finished.\nCheck console output for details/warnings.");
                    // Observer should update student table via Course event
                } else { showError("Student import failed. Check file or console."); }
            }
        });

        // Set Grading Mode needs popup
        setGradingModeButton.addActionListener(e -> {
             System.out.println("Set Grading Mode button pressed");
             Course selectedCourse = (Course) courseComboBox.getSelectedItem();
             if (selectedCourse == null) { showError("Select a course first."); return; }
             // Simple popup to choose mode
             Object[] options = {"Points Based", "Category Based"};
             int choice = JOptionPane.showOptionDialog(this,
                    "Select grading mode for " + selectedCourse.getName() + ":",
                    "Set Grading Mode",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

             GradeCalculator selectedCalc = null;
             if (choice == 0) { // Points
                 selectedCalc = new PointsBasedCalculator();
             } else if (choice == 1) { // Category
                 selectedCalc = new CategoryBasedCalculator();
             } // else cancelled

             if (selectedCalc != null) {
                  // Tell controller to set the chosen calculator on the course model
                  boolean success = teacherController.setCourseGradingMode(selectedCourse, selectedCalc);
                  if (success) { showInfo("Grading mode set for course."); }
                  else { showError("Failed to set grading mode."); }
             }
        });

        // Setup Categories needs popup
        setupCategoriesButton.addActionListener(e -> {
             System.out.println("Setup Categories button pressed");
             Course selectedCourse = (Course) courseComboBox.getSelectedItem();
             if (selectedCourse == null) { showError("Select a course first."); return; }
             // Check if course is actually using categories mode first
             if (!selectedCourse.usesCategories()) {
                  showError("Course must be set to 'Category Based' mode first to setup categories.");
                  return;
             }
             // TODO: This needs a custom JDialog to manage categories properly.
             // That dialog would get the current categories Map<String, GradingCategory> from course,
             // allow adding/editing/removing categories (name, weight 0-1, drops >= 0),
             // validate weights sum to 1.0 maybe,
             // then create a final List<GradingCategory> and pass it back.
             // Example call after dialog:
             // List<GradingCategory> finalCategories = categorySetupDialog.getFinalCategories();
             // if (finalCategories != null) { // Check if user didn't cancel
             //    teacherController.setupAssignmentCategories(selectedCourse, finalCategories);
             // }
             showInfo("Setup Categories dialog needs to be built!");
        });

        // Assign Final Grade needs popup
        assignFinalGradeButton.addActionListener(e -> {
            System.out.println("Assign Final Grade button pressed");
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            Student selectedStudent = getSelectedStudentFromTable(); // Use helper
             if (selectedCourse != null && selectedStudent != null && teacherController != null) {
                 // Simple popup for letter grade
                 String letterGrade = JOptionPane.showInputDialog(this, "Enter final letter grade (A, B, C, D, E) for " + selectedStudent.getUsername() + ":");
                 // Check if user entered something
                 if (letterGrade != null && !letterGrade.trim().isEmpty()) {
                      // Call controller to assign the grade Student model validates letter
                      boolean success = teacherController.assignFinalGrade(selectedStudent, selectedCourse, letterGrade.trim());
                      if (success) { showInfo("Final grade assigned."); }
                      else { showError("Failed to assign final grade. Check letter is A-E."); }
                 }
             } else { showError("Select a course and student first."); }
        });
    }

    // --- Helper methods for GUI ---

    /**
     * findAssignmentInCourse is a helper to get the actual Assignment object
     * based on the name selected in the assignment table.
     * Needed because JTable stores Strings, not the objects directly easily.
     */
     private Assignment findAssignmentInCourse(Course course, String assignmentName) {
         if (course == null || assignmentName == null) return null;
         for (Assignment a : course.getAllAssignments()) { // Use getter maybe?
             if (a.getName().equals(assignmentName)) {
                 return a; // Found it
             }
         }
         System.out.println("Helper problem: findAssignmentInCourse couldn't find " + assignmentName);
         return null; // Didnt find it
     }

     /**
     * getSelectedAssignmentFromTable is a helper to find the Assignment object
     * corresponding to the currently selected row in the assignment JTable.
     * Returns null if no row selected or assignment not found.
     */
     private Assignment getSelectedAssignmentFromTable() {
          int selectedRow = assignmentTable.getSelectedRow();
          Course selectedCourse = (Course) courseComboBox.getSelectedItem();
          // Check valid row and course selected
          boolean haveSelection = (selectedRow >= 0 && selectedCourse != null);
          if (haveSelection) {
               try {
                    // Get name from first column index 0
                    String assignmentName = (String) assignmentTableModel.getValueAt(selectedRow, 0);
                    // Use other helper to find the actual object
                    return findAssignmentInCourse(selectedCourse, assignmentName);
               } catch (ArrayIndexOutOfBoundsException e) {
                    // Catch error if row index somehow invalid
                    System.out.println("Helper problem: Error getting value from assignment table row " + selectedRow);
                    return null;
               }
          }
          return null; // Nothing selected or no course
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
        // Loop through students in the course using getter returns copy
        for (Student s : course.getEnrolledStudents()) {
            if (s.getUsername().equals(username)) {
                return s; // Found it
            }
        }
        System.out.println("Helper problem: findStudentInCourse couldn't find " + username);
        return null; // Didnt find it
    }

     /**
     * getSelectedStudentFromTable is a helper to find the Student object
     * corresponding to the currently selected row in the student JTable.
     * Returns null if no row selected or student not found.
     */
     private Student getSelectedStudentFromTable() {
          int selectedRow = studentTable.getSelectedRow();
          Course selectedCourse = (Course) courseComboBox.getSelectedItem();
          // Check valid row and course selected
          boolean haveSelection = (selectedRow >= 0 && selectedCourse != null);
          if (haveSelection) {
               try {
                    // Get username from first column index 0
                    String username = (String) studentTableModel.getValueAt(selectedRow, 0);
                    // Use other helper to find the actual object
                    return findStudentInCourse(selectedCourse, username);
               } catch (ArrayIndexOutOfBoundsException e) {
                    // Catch error if row index somehow invalid
                    System.out.println("Helper problem: Error getting value from student table row " + selectedRow);
                    return null;
               }
          }
          return null; // Nothing selected or no course
     }

     /**
      * updateStudentTable is a helper used by sorting methods or refresh
      * to clear and refill the student table with a new list.
      * @param students The list of students to display.
      */
     private void updateStudentTable(List<Student> students) {
         studentTableModel.setRowCount(0); // Clear table first
         // Check if list is valid
         boolean haveStudents = (students != null);
         if (haveStudents) {
             for (Student s : students) { // Refill table
                 studentTableModel.addRow(new Object[]{s.getUsername(), s.getFirstName(), s.getLastName(), s.getStudentId()});
             }
         }
     }

     /**
      * updateAssignmentTable is a helper used by refresh or filters
      * to clear and refill the assignment table with a new list.
      * @param course The course whose assignments to display (needed for filtering maybe)
      */
      private void updateAssignmentTable(Course course) {
         assignmentTableModel.setRowCount(0); // Clear table
         if (course == null) return; // Need course

         List<Assignment> assignmentsToShow;
         // Decide whether to show all or only ungraded based on flag
         if (showingOnlyUngraded) {
              assignmentsToShow = teacherController.viewUngradedAssignments(course); // Get filtered list
              viewUngradedButton.setText("Show All Assignments"); // Update button text
         } else {
              assignmentsToShow = course.getAllAssignments(); // Get all assignments
              viewUngradedButton.setText("Show Only Ungraded"); // Reset button text
         }

         // Check if list has assignments
         boolean haveAssignments = (assignmentsToShow != null);
         if (haveAssignments) {
             for (Assignment a : assignmentsToShow) { // Refill table
                 String dueDateStr = a.getDueDate(); // String date
                 String categoryNameStr = a.getCategoryName() != null ? a.getCategoryName() : "None";
                 String isGradedStr = a.isGraded() ? "Yes" : "No";
                 assignmentTableModel.addRow(new Object[]{a.getName(), dueDateStr, a.getPointsWorth(), categoryNameStr, isGradedStr});
             }
         }
      }

    // --- Simple Dialog Helpers ---
    /** Shows a basic information message popup. */
    private void showInfo(String message) {
         JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    /** Shows a basic error message popup. */
    private void showError(String message) {
         JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }


    // --- Observer Method ---

    /**
     * This method propertyChange gets called by the Models like Course or Student
     * when their data changes because this View registered as a listener.
     * It checks what property changed using evt.getPropertyName() and
     * reloads the relevant part of the GUI usually by calling displaySelectedCourseData.
     * Needs to run GUI updates on the Swing thread using invokeLater.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Print message to console to show event was received, helpful for debugging
        System.out.println("TeacherView detected property change: " + evt.getPropertyName() + " from " + evt.getSource().getClass().getSimpleName());
        // Get the name of the property that changed e.g., "studentEnrolled"
        String propertyThatChanged = evt.getPropertyName();
        // Get the object that fired the event e.g., a Course object or Student object
        Object sourceObject = evt.getSource();
        // Get the course currently selected in the dropdown
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();

        // Decide if we need to refresh the view based on the event
        boolean needsRefresh = false;

        // Check if the event came from the Course object we are currently looking at
        boolean changeIsInSelectedCourse = (sourceObject instanceof Course && sourceObject.equals(selectedCourse));

        // List of property names from Course model that mean we should refresh tables
        if (changeIsInSelectedCourse) {
            if ("studentEnrolled".equals(propertyThatChanged) ||
                "studentRemoved".equals(propertyThatChanged) ||
                "assignmentAdded".equals(propertyThatChanged) ||
                "assignmentRemoved".equals(propertyThatChanged) ) {
                // If students or assignments changed in this course, refresh needed
                needsRefresh = true;
                System.out.println("Course structure changed, flagging refresh");
            }
        }
        // Also refresh if any grade was added/removed anywhere, might affect averages or tables
        // This is broad, maybe could be more specific later?
        if ("gradeAdded".equals(propertyThatChanged) || "gradeRemoved".equals(propertyThatChanged)) {
             needsRefresh = true;
             System.out.println("Grade changed, flagging refresh");
        }
        // Could also listen for "finalGradeSet" from Student but TeacherView doesn't show that directly


        // If we decided a refresh is needed...
        if (needsRefresh) {
            // ... use invokeLater to make sure the GUI update happens safely on the Swing event thread.
            // Otherwise GUI can crash or act weird if updated from wrong thread.
            SwingUtilities.invokeLater(() -> {
                 System.out.println("TeacherView refreshing data due to event: " + propertyThatChanged);
                 displaySelectedCourseData(); // Call helper to reload table data
            });
        }
    }

     /**
      * registerCourseListeners adds this view as a listener to all courses
      * currently taught by the teacher. Called initially and maybe on refresh?
      * Need to be careful about removing listeners if course list changes drastically.
      */
     private void registerCourseListeners() {
         if (teacherController != null) {
              List<Course> courses = teacherController.viewCourses();
              if (courses != null) {
                   for (Course c : courses) {
                        if (c != null) {
                             //System.out.println("Registering listener for course: " + c.getName());
                             c.addPropertyChangeListener(this);
                        }
                   }
              }
         }
     }

     // TODO: Need method to remove listeners when view closes maybe?
     // Or when course list is updated. Otherwise might have memory leaks.
     // public void removeCourseListeners() { ... }


    // --- Getters for Components Needed by maybe Controller or Tests ---
    // Views usually don't need getters like this if logic is handled by listeners
    // but keeping them just in case.
    public JComboBox<Course> getCourseComboBox() { return courseComboBox; }
    public JTable getStudentTable() { return studentTable; }
    public JTable getAssignmentTable() { return assignmentTable; }
    // ... Getters for all buttons ...
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
    public JButton getCalculateAssignmentStatsButton() { return calculateAssignmentStatsButton; }
    public JButton getAssignFinalGradeButton() { return assignFinalGradeButton; }
    public JButton getRefreshDataButton() { return refreshDataButton; }

}