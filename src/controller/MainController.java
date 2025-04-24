package controller;

import model.*;
import view.*;

import javax.swing.*;
import java.util.List;
// A controlelr to handle the operation of switching between views and starting the app
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainController().startApp());
    }
}
