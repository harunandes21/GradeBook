package controller;

import model.*;
import view.*;

import javax.swing.*;
import java.util.List;
// A controller to handle the operation of switching between views and starting the app
public class MainController {
    private UserController userController;
    private LoginController loginController;
    private User loggedInUser;

    public MainController() {
        userController = new UserController();
        loginController = new LoginController(userController);
    }

    public void startApp() {
        showLoginView();
    }

    private void showLoginView() {
        LoginView loginView = new LoginView();

        loginView.getLoginButton().addActionListener(e -> {
            String username = loginView.getUsername();
            String password = loginView.getPassword();
            User user = loginController.login(username, password);
            if (user != null) {
                loggedInUser = user;
                loginView.dispose();
                if (loggedInUser instanceof Teacher teacher) {
                    setupCoursesForTeacher(teacher); // setup fresh and completed courses
                }
                
                showMainView();
            } else {
                JOptionPane.showMessageDialog(loginView, "Login failed. Please try again.");
            }
        });

        loginView.getCreateAccountButton().addActionListener(e -> {
            loginView.dispose();
            showCreateAccountView();
        });
    }

    private void showMainView() {
        List<String> courseNames;
        if (loggedInUser instanceof Teacher) {
            Teacher teacher = (Teacher) loggedInUser;
            courseNames = teacher.getCoursesTaught().stream().map(Course::getName).toList();
        } else if (loggedInUser instanceof Student) {
            Student student = (Student) loggedInUser;
            courseNames = student.getCurrentCourses().stream().map(Course::getName).toList();
        } else {
            courseNames = List.of();
        }

        MainView mainView = new MainView(loggedInUser.getFirstName() + " " + loggedInUser.getLastName(), courseNames);

        mainView.getLogoutButton().addActionListener(e -> {
            mainView.dispose();
            loggedInUser = null;
            showLoginView();
        });
        
        mainView.getViewCourseButton().addActionListener(e -> {
            String selectedCourseName = mainView.getSelectedCourse();
            if (selectedCourseName == null || selectedCourseName.isEmpty()) {
                JOptionPane.showMessageDialog(mainView, "Please select a course.");
                return;
            }

            Course selectedCourse = null;

            if (loggedInUser instanceof Teacher) {
                Teacher teacher = (Teacher) loggedInUser;
                for (Course c : teacher.getCoursesTaught()) {
                    if (c.getName().equals(selectedCourseName)) {
                        selectedCourse = c;
                        break;
                    }
                }
            } else if (loggedInUser instanceof Student) {
                Student student = (Student) loggedInUser;
                for (Course c : student.getCurrentCourses()) {
                    if (c.getName().equals(selectedCourseName)) {
                        selectedCourse = c;
                        break;
                    }
                }
            }

            if (selectedCourse != null) {
                mainView.dispose();
                Teacher teacher = (Teacher) loggedInUser;
                TeacherController teacherController = new TeacherController(teacher, userController);
                new CourseView(teacher, selectedCourse, teacherController).setVisible(true);

            } else {
                JOptionPane.showMessageDialog(mainView, "Course data not found.");
            }
        });

        
    }

    private void showCreateAccountView() {
        CreateAccountView createView = new CreateAccountView();

        createView.getCreateButton().addActionListener(e -> {
            String fn = createView.getFirstName();
            String ln = createView.getLastName();
            String username = createView.getUsername();
            String password = createView.getPassword();
            Role role = Role.valueOf(createView.getRole().toUpperCase());

            User createdUser = userController.createAccount(fn, ln, username, password, role);
            if (createdUser != null) {
                JOptionPane.showMessageDialog(createView, "Account created! Please log in.");
                createView.dispose();
                showLoginView();
            } else {
                JOptionPane.showMessageDialog(createView, "Failed to create account. Username may already exist.");
            }
        });

        createView.getCancelButton().addActionListener(e -> {
            createView.dispose();
            showLoginView();
        });
    }
    private void setupCoursesForTeacher(Teacher teacher) {
        // 1. Fresh Courses
        Course csc335 = new Course("CSC 335 - Software Engineering", "CSC335", "Spring 2025", false);
        Course math101 = new Course("MATH 101 - College Algebra", "MATH101", "Spring 2025", false);

        // 2. Completed Courses
        Course psy336 = new Course("PSY 336 - Cognitive Psychology", "PSY336", "Fall 2024", true);
        Course eng102 = new Course("ENG 102 - First Year Composition", "ENG102", "Fall 2024", true);

        // Hardcode Assignments for completed courses
        Assignment psyQuiz1 = new Assignment("Quiz 1", 50, "2024-09-10", "Quiz", "Group A");
        Assignment psyHw1 = new Assignment("Homework 1", 100, "2024-09-20", "Homework", "Group A");
        psy336.addAssignment(psyQuiz1);
        psy336.addAssignment(psyHw1);

        Assignment engEssay = new Assignment("Essay 1", 100, "2024-10-01", "Essay", "Group B");
        Assignment engQuiz = new Assignment("Quiz 1", 50, "2024-10-05", "Quiz", "Group B");
        eng102.addAssignment(engEssay);
        eng102.addAssignment(engQuiz);

        // Create students for completed courses
        Student alice = new Student("Alice", "Nguyen", "alice@uofa.edu", "pass", "alice123", "S001");
        Student brian = new Student("Brian", "Lopez", "brian@uofa.edu", "pass", "brian456", "S002");
        Student clara = new Student("Clara", "Zhao", "clara@uofa.edu", "pass", "clara789", "S003");

        psy336.enrollStudent(alice);
        psy336.enrollStudent(brian);
        eng102.enrollStudent(clara);

        // Assign grades
        alice.addGrade(psyQuiz1, new Grade(45, "Good effort"));
        alice.addGrade(psyHw1, new Grade(92, "Excellent"));

        brian.addGrade(psyQuiz1, new Grade(40, "Solid"));
        brian.addGrade(psyHw1, new Grade(85, "Well done"));

        clara.addGrade(engEssay, new Grade(90, "Strong essay"));
        clara.addGrade(engQuiz, new Grade(48, "Almost perfect"));

        // Add all courses to teacher
        teacher.addCourse(csc335);
        teacher.addCourse(math101);
        teacher.addCourse(psy336);
        teacher.addCourse(eng102);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainController().startApp());
    }
}
