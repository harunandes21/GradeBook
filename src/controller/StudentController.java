package controller;

import model.*;
import model.grading.GradeCalculator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling student-related operations in the gradebook system.
 * Manages student login, course enrollment, grade viewing, and GPA calculation.
 */
public class StudentController {
    private Student currentStudent;
    private Course currentCourse;
    private final UserController userController;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Constructs a StudentController with the specified UserController.
     */
    public StudentController(UserController userController) {
        if (userController == null) {
            throw new IllegalArgumentException("UserController cannot be null");
        }
        this.userController = userController;
    }

    /**
     * Attempts to log in a student.
     * @return true if login succeeds, false otherwise
     */
    public boolean login(String username, String password) {
        User user = userController.login(username, password);
        if (user instanceof Student) {
            this.currentStudent = (Student) user;
            pcs.firePropertyChange("login", null, currentStudent);
            return true;
        }
        return false;
    }

    /**
     * Logs out the current student.
     */
    public void logout() {
        if (currentStudent != null) {
            userController.logout(currentStudent);
        }
        currentStudent = null;
        currentCourse = null;
    }

    /**
     * Enrolls the current student in a course.
     */
    public void enrollInCourse(Course course) {
        if (currentStudent == null || course == null) return;
        
        currentStudent.enrollInCourse(course);
        course.enrollStudent(currentStudent);
        pcs.firePropertyChange("enrollment", null, course);
    }

    /**
     * Gets the list of courses the student is currently enrolled in.
     */
    public List<Course> viewCourses() {
        if (currentStudent == null) {
            throw new IllegalStateException("Not logged in");
        }
        return currentStudent.getCurrentCourses();
    }

    /**
     * Gets all grades for the current student in a specific course.
     */
    public Map<Assignment, Grade> getGradesForCourse(Course c) {
        if (c == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (currentStudent == null) {
            throw new IllegalStateException("Not logged in");
        }
        return c.getGradesForStudent(currentStudent);
    }
    

    /**
     * Gets all assignments in a specific course.
     */
    public List<Assignment> viewAssignmentsInCourse(Course c) {
        if (c == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (currentStudent == null) {
            throw new IllegalStateException("Not logged in");
        }
        return c.getAllAssignments();
    }
    
    /**
     * Gets feedback for all assignments in current course
     */
    public Map<String, String> getAssignmentFeedback() {
        if (currentCourse == null || currentStudent == null) {
            throw new IllegalStateException("No course selected or not logged in");
        }
        return currentStudent.getFeedbackByCourse(currentCourse);
    }

    /**
     * Calculates the current student's grade in the current course.
     */
    public double calculateCourseGrade() {
        if (currentCourse == null || currentStudent == null) return 0.0;
        
        GradeCalculator calculator = currentCourse.getGradeCalculator();
        return calculator != null ? 
            calculator.calculateFinalAverage(currentCourse, currentStudent) : 0.0;
    }

    /**
     * Calculates the current student's overall GPA.
     */
    public double calculateGPA() {
        if (currentStudent == null) {
            throw new IllegalStateException("Not logged in");
        }
        return currentStudent.calculateGPA();
    }

    /**
     * Gets the currently logged in student.
     */
    public Student getCurrentStudent() {
        return currentStudent;
    }

    /**
     * Sets the current course for grade calculations.
     */
    public void setCurrentCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        this.currentCourse = course;
    }
    
    // --- Observer Pattern Support ---

    /**
     * Adds a property change listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /**
     * Removes a property change listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
}