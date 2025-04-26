package view;

import model.*;

import javax.swing.*;

import controller.MainController;
import controller.TeacherController;
import controller.UserController;

import java.awt.*;
import java.util.List;
import java.util.Optional;

/* We could also think about having 2 different course views as StudentCourseView and TeacherCourseView 
 * To be discussed later 
 * */
public class CourseView extends JFrame {
    private JLabel courseTitle;
    private JButton backButton;
    private JButton addStudentButton;
    private JButton addAssignmentButton;
    private JButton calcAverageButton;
    private TeacherController teacherController;
    private User user;



    private JTable rosterTable;
    private JTable assignmentTable;
    private JTable gradeTable;
    private JTable myGradeTable;
	private Course course;

    public CourseView(User user, Course course, TeacherController teacherController) {
        this.teacherController = teacherController;
        this.course = course;
        this.user = user;

        setTitle("Course: " + course.getName());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top bar
        JPanel topPanel = new JPanel(new BorderLayout());
        courseTitle = new JLabel("Course: " + course.getName());
        courseTitle.setFont(new Font("Arial", Font.BOLD, 20));
        backButton = new JButton("Back to Main");
        topPanel.add(courseTitle, BorderLayout.WEST);
        topPanel.add(backButton, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        if (user instanceof Teacher) {
            rosterTable = new JTable();
            assignmentTable = new JTable();
            gradeTable = new JTable();

            tabbedPane.addTab("Roster", new JScrollPane(rosterTable));
            tabbedPane.addTab("Assignments", new JScrollPane(assignmentTable));
            tabbedPane.addTab("Grades", new JScrollPane(gradeTable));
        } else if (user instanceof Student) {
            assignmentTable = new JTable();
            myGradeTable = new JTable();

            tabbedPane.addTab("Assignments", new JScrollPane(assignmentTable));
            tabbedPane.addTab("My Grades", new JScrollPane(myGradeTable));
        }

        // Bottom buttons for teacher
        if (user instanceof Teacher) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            addStudentButton = new JButton("Add Student");
            addAssignmentButton = new JButton("Add Assignment");
            calcAverageButton = new JButton("Calculate Averages");
            
            JButton manageGroupsBtn = new JButton("Manage Groups");
            buttonPanel.add(manageGroupsBtn);
            
            manageGroupsBtn.addActionListener(e -> showGroupManagementDialog());
            
            buttonPanel.add(addStudentButton);
            buttonPanel.add(addAssignmentButton);
            buttonPanel.add(calcAverageButton);
            add(buttonPanel, BorderLayout.SOUTH);

            // Add Student Logic
            addStudentButton.addActionListener(e -> {
                String studentUsername = JOptionPane.showInputDialog(this, "Enter student username:");
                if (studentUsername != null && !studentUsername.trim().isEmpty()) {
                    Student foundStudent = teacherController.getStudentByUsername(studentUsername.trim());
                    if (foundStudent != null) {
                        boolean success = teacherController.addStudentToCourse(foundStudent, course);
                        if (!success) {
                            JOptionPane.showMessageDialog(this, "Failed to add student.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Student not found.");
                    }
                }
            });

            // Add Assignment Logic
            addAssignmentButton.addActionListener(e -> {
                JTextField nameField = new JTextField(10);
                JTextField pointsField = new JTextField(5);
                JTextField dueDateField = new JTextField(10);
                JTextField categoryField = new JTextField(10);
                
                // Group selection components
                JComboBox<String> groupCombo = new JComboBox<>();
                groupCombo.addItem("None"); // Default no group
                course.getGroups().forEach(g -> groupCombo.addItem(g.getGroupName()));
                
                JButton createGroupBtn = new JButton("New Group");
                JPanel groupPanel = new JPanel(new BorderLayout());
                groupPanel.add(groupCombo, BorderLayout.CENTER);
                groupPanel.add(createGroupBtn, BorderLayout.EAST);

                JPanel panel = new JPanel(new GridLayout(6, 2));
                panel.add(new JLabel("Assignment Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Points Worth:"));
                panel.add(pointsField);
                panel.add(new JLabel("Due Date:"));
                panel.add(dueDateField);
                panel.add(new JLabel("Category:"));
                panel.add(categoryField);
                panel.add(new JLabel("Group:"));
                panel.add(groupPanel);
                panel.add(new JLabel("")); // Empty label for spacing
                panel.add(new JLabel("")); // Empty label for spacing
                
                // Group button action
                createGroupBtn.addActionListener(ce -> {
                    String newGroupName = JOptionPane.showInputDialog(this, "Enter new group name:");
                    if (newGroupName != null && !newGroupName.trim().isEmpty()) {
                        try {
                            Group newGroup = new Group(newGroupName.trim());
                            course.getGroups().add(newGroup);
                            groupCombo.addItem(newGroup.getGroupName());
                            groupCombo.setSelectedItem(newGroup.getGroupName());
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                int result = JOptionPane.showConfirmDialog(this, panel, "Add Assignment", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String name = nameField.getText().trim();
                        double points = Double.parseDouble(pointsField.getText().trim());
                        String dueDate = dueDateField.getText().trim();
                        String category = categoryField.getText().trim();
                        
                        // Handle group selection
                        Group selectedGroup = null;
                        String selectedGroupName = (String)groupCombo.getSelectedItem();
                        if (!"None".equals(selectedGroupName)) {
                            selectedGroup = course.getGroups().stream()
                                .filter(g -> g.getGroupName().equals(selectedGroupName))
                                .findFirst()
                                .orElse(null);
                        }

                        Assignment newAssign = new Assignment(name, points, dueDate, category, selectedGroup);
                        boolean added = teacherController.addAssignmentToCourse(newAssign, course);

                        if (!added) {
                            JOptionPane.showMessageDialog(this, "Failed to add assignment.");
                        
	                    } else {
	                        refreshAssignmentTable(); // Update the table view
	                    }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                    }
                }
            });

            // Calculate Averages Logic
            calcAverageButton.addActionListener(e -> {
                List<Assignment> assignments = course.getAllAssignments();
                if (assignments.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No assignments in course.");
                    return;
                }

                StringBuilder message = new StringBuilder("Class Averages:\n");
                for (Assignment a : assignments) {
                    double avg = teacherController.calculateClassAverage(a);
                    message.append(a.getName()).append(": ").append(String.format("%.2f", avg)).append("\n");
                }
                JOptionPane.showMessageDialog(this, message.toString());
            });
        }

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // React to model events
        course.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "studentEnrolled", "studentRemoved" -> {
                    if (user instanceof Teacher) refreshRoster(course);
                }
                case "assignmentAdded" -> refreshAssignments(course);
                case "gradeAdded" -> {
                    if (user instanceof Student) refreshMyGrades(course);
                }
            }
        });

        
        backButton.addActionListener(e -> {
            dispose(); // Close this CourseView window

            SwingUtilities.invokeLater(() -> {
                List<String> courseNames;
                if (user instanceof Teacher teacher) {
                    courseNames = teacher.getCoursesTaught().stream().map(Course::getName).toList();
                } else if (user instanceof Student student) {
                    courseNames = student.getCurrentCourses().stream().map(Course::getName).toList();
                } else {
                    courseNames = List.of();
                }

                MainView mainView = new MainView(user.getFirstName() + " " + user.getLastName(), courseNames);

                mainView.getLogoutButton().addActionListener(ev -> {
                    mainView.dispose();
                    new MainController().startApp(); // OPTIONAL: restart login flow
                });

                mainView.getViewCourseButton().addActionListener(ev -> {
                    String selectedCourseName = mainView.getSelectedCourse();
                    if (selectedCourseName == null || selectedCourseName.isEmpty()) {
                        JOptionPane.showMessageDialog(mainView, "Please select a course.");
                        return;
                    }

                    Course selectedCourse = null;
                    if (user instanceof Teacher teacher) {
                        for (Course c : teacher.getCoursesTaught()) {
                            if (c.getName().equals(selectedCourseName)) {
                                selectedCourse = c;
                                break;
                            }
                        }
                    } else if (user instanceof Student student) {
                        for (Course c : student.getCurrentCourses()) {
                            if (c.getName().equals(selectedCourseName)) {
                                selectedCourse = c;
                                break;
                            }
                        }
                    }

                    if (selectedCourse != null) {
                        mainView.dispose();
                        if (user instanceof Teacher teacher) {
                        	new CourseView(teacher, selectedCourse, teacherController).setVisible(true);

                        }
                    } else {
                        JOptionPane.showMessageDialog(mainView, "Course data not found.");
                    }
                });

                mainView.setVisible(true);
            });
        });


        
        refreshAssignmentTable(); // Fill assignments
        refreshStudentTable();    // Fill students
        refreshGradesTable();
    }
    private void refreshRoster(Course course) {
        if (rosterTable == null) return;
        List<Student> students = course.getEnrolledStudents();
        String[][] data = new String[students.size()][2];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i][0] = s.getFirstName() + " " + s.getLastName();
            data[i][1] = s.getStudentId();
        }
        String[] columnNames = {"Name", "Student ID"};
        rosterTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private void refreshAssignments(Course course) {
        if (assignmentTable == null) return;
        List<Assignment> assignments = course.getAllAssignments();
        String[][] data = new String[assignments.size()][2];
        for (int i = 0; i < assignments.size(); i++) {
            Assignment a = assignments.get(i);
            data[i][0] = a.getName();
            data[i][1] = String.valueOf(a.getPointsWorth());
        }
        String[] columnNames = {"Assignment", "Points"};
        assignmentTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }
    private void refreshMyGrades(Course course) {
        if (myGradeTable == null) return;

        List<Assignment> assignments = course.getAllAssignments();
        String[] columnNames = new String[assignments.size() + 1];
        columnNames[0] = "Assignment";
        for (int i = 0; i < assignments.size(); i++) {
            columnNames[i + 1] = assignments.get(i).getName();
        }

        // Only one row: the logged-in student
        String[][] data = new String[1][assignments.size() + 1];
        data[0][0] = user.getUsername(); // First column is student username

        if (user instanceof Student student) {
            for (int j = 0; j < assignments.size(); j++) {
                Assignment assignment = assignments.get(j);
                Grade grade = student.getGradeForAssignment(assignment);
                if (grade != null) {
                    data[0][j + 1] = String.valueOf(grade.getPointsEarned());
                } else {
                    data[0][j + 1] = "-"; // No grade yet
                }
            }
        }

        myGradeTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
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
    
    public JButton getAddStudentButton() {
        return addStudentButton;
    }

    public JButton getAddAssignmentButton() {
        return addAssignmentButton;
    }

    public JButton getCalcAverageButton() {
        return calcAverageButton;
    }
    
    private void refreshAssignmentTable() {
        if (assignmentTable == null) return;
        List<Assignment> assignments = course.getAllAssignments();
        String[][] data = new String[assignments.size()][4];
        for (int i = 0; i < assignments.size(); i++) {
            Assignment a = assignments.get(i);
            data[i][0] = a.getName();
            data[i][1] = a.getDueDate();
            data[i][2] = String.valueOf(a.getPointsWorth());
            data[i][3] = a.getGroup() != null ? a.getGroup().getGroupName() : "None";
        }
        String[] columnNames = {"Assignment", "Due Date", "Points", "Group"};
        assignmentTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private void refreshStudentTable() {
        if (rosterTable == null) return;
        List<Student> students = course.getEnrolledStudents();
        String[][] data = new String[students.size()][3];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i][0] = s.getFirstName() + " " + s.getLastName();
            data[i][1] = s.getStudentId();
            data[i][2] = s.getEmail();
        }
        String[] columnNames = {"Name", "Student ID", "Email"};
        rosterTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private void refreshGradesTable() {
        if (gradeTable == null) return;
        List<Student> students = course.getEnrolledStudents();
        List<Assignment> assignments = course.getAllAssignments();
        String[] columnNames = new String[assignments.size() + 1];
        columnNames[0] = "Student";
        for (int i = 0; i < assignments.size(); i++) {
            columnNames[i + 1] = assignments.get(i).getName();
        }

        String[][] data = new String[students.size()][assignments.size() + 1];
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            data[i][0] = student.getUsername();
            for (int j = 0; j < assignments.size(); j++) {
                Assignment assignment = assignments.get(j);
                Grade grade = student.getGradeForAssignment(assignment);
                if (grade != null) {
                    data[i][j + 1] = String.valueOf(grade.getPointsEarned());
                } else {
                    data[i][j + 1] = "-"; // No grade yet
                }
            }
        }

        gradeTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }


    public static void main(String[] args) {
        // Step 1: Create course and teacher
        Course course = new Course("CSC 335", "335", "Spring 2025", false);
        Teacher melanie = new Teacher("Melanie", "Lotz", "mlotz@cs.arizona.edu", "pass123", "mlotz", "T999");
        melanie.addCourse(course);
        UserController uc = new UserController();
        TeacherController tc = new TeacherController(melanie, uc);
        Group groupA = new Group("Group A");
        course.getGroups().add(groupA);

        // Step 2: Create assignments
        Assignment hw1 = new Assignment("HW 1", 100, "2025-04-30", "Homework", groupA);
        Assignment hw2 = new Assignment("HW 2", 100, "2025-05-07", "Homework", groupA);
        Assignment quiz1 = new Assignment("Quiz 1", 50, "2025-05-01", "Quiz", null);

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
            CourseView view = new CourseView(melanie, course,tc);

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

    private void showGroupManagementDialog() {
        JDialog dialog = new JDialog(this, "Manage Groups", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        
        // List of groups
        DefaultListModel<String> groupListModel = new DefaultListModel<>();
        course.getGroups().forEach(g -> groupListModel.addElement(g.getGroupName()));
        JList<String> groupList = new JList<>(groupListModel);
        
        // Buttons
        JButton addGroupBtn = new JButton("Add Group");
        JButton removeGroupBtn = new JButton("Remove Group");
        JButton manageMembersBtn = new JButton("Manage Members");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addGroupBtn);
        buttonPanel.add(removeGroupBtn);
        buttonPanel.add(manageMembersBtn);
        
        // Add components to dialog
        dialog.add(new JScrollPane(groupList), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Button actions
        addGroupBtn.addActionListener(e -> {
            String groupName = JOptionPane.showInputDialog(dialog, "Enter group name:");
            if (groupName != null && !groupName.trim().isEmpty()) {
                try {
                    Group newGroup = new Group(groupName.trim());
                    course.getGroups().add(newGroup);
                    groupListModel.addElement(newGroup.getGroupName());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        removeGroupBtn.addActionListener(e -> {
            String selected = groupList.getSelectedValue();
            if (selected != null) {
                course.getGroups().removeIf(g -> g.getGroupName().equals(selected));
                groupListModel.removeElement(selected);
            }
        });
        
        manageMembersBtn.addActionListener(e -> {
            String selectedGroupName = groupList.getSelectedValue();
            if (selectedGroupName != null) {
                showGroupMembersDialog(selectedGroupName);
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void showGroupMembersDialog(String groupName) {
        Optional<Group> groupOpt = course.getGroups().stream()
            .filter(g -> g.getGroupName().equals(groupName))
            .findFirst();
        
        if (!groupOpt.isPresent()) return;
        
        Group group = groupOpt.get();
        JDialog dialog = new JDialog(this, "Manage Members: " + groupName, true);
        dialog.setSize(400, 400);
        dialog.setLayout(new BorderLayout());
        
        // List of all students
        DefaultListModel<String> allStudentsModel = new DefaultListModel<>();
        course.getEnrolledStudents().forEach(s -> allStudentsModel.addElement(s.getUsername()));
        
        // List of group members
        DefaultListModel<String> membersModel = new DefaultListModel<>();
        group.getMembers().forEach(s -> membersModel.addElement(s.getUsername()));
        
        JList<String> allStudentsList = new JList<>(allStudentsModel);
        JList<String> membersList = new JList<>(membersModel);
        
        // Buttons
        JButton addBtn = new JButton("Add →");
        JButton removeBtn = new JButton("← Remove");
        
        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        centerPanel.add(new JScrollPane(allStudentsList));
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        centerPanel.add(buttonPanel);
        centerPanel.add(new JScrollPane(membersList));
        
        // Button actions
        addBtn.addActionListener(e -> {
            String selected = allStudentsList.getSelectedValue();
            if (selected != null) {
                Student student = course.getEnrolledStudents().stream()
                    .filter(s -> s.getUsername().equals(selected))
                    .findFirst()
                    .orElse(null);
                if (student != null && group.addMember(student)) {
                    membersModel.addElement(selected);
                }
            }
        });
        
        removeBtn.addActionListener(e -> {
            String selected = membersList.getSelectedValue();
            if (selected != null) {
                Student student = group.getMembers().stream()
                    .filter(s -> s.getUsername().equals(selected))
                    .findFirst()
                    .orElse(null);
                if (student != null && group.removeMember(student)) {
                    membersModel.removeElement(selected);
                }
            }
        });
        
        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(new JLabel("Group Members: " + groupName), BorderLayout.NORTH);
        dialog.setVisible(true);
    }
    
}