package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a student user in the gradebook system.
 * Extends the base User class with student-specific functionality.
 */
public class Student extends User {
    private final String studentId;					
    private final List<Course> currentCourses;
    private final List<Course> completedCourses;
    private final Map<Assignment, Grade> grades;

    // Constructor
    public Student(String firstName, String lastName, String email, String password, String username, String studentId) {
        super(firstName, lastName, email, password, username);
        this.studentId = studentId;
        this.currentCourses = new ArrayList<>();
        this.completedCourses = new ArrayList<>();
        this.grades = new HashMap<>();
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public List<Course> getCurrentCourses() {
        return new ArrayList<Course>(currentCourses);
    }

    public List<Course> getCompletedCourses() {
        return new ArrayList<Course>(completedCourses);
    }

    public Map<Assignment, Grade> getGrades() {
        return new HashMap<Assignment, Grade>(grades);
    }

    /*
     * Enrolls the student in a course if not already enrolled
     */
    public void enrollInCourse(Course course) {
        if (course != null && !currentCourses.contains(course)) {
            currentCourses.add(course);
            course.enrollStudent(this);
        }
    }

    /*
     * Marks a course as completed by moving it from current to completed
     */
    public void completeCourse(Course course) {
    	if (currentCourses.contains(course)) {
    		currentCourses.remove(course);
            completedCourses.add(course);
        }
    }
    
    /*
     * Adds a grade for an assignment
     */
    public void addGrade(Assignment assignment, Grade grade) {
        if (assignment != null && grade != null) {
            grades.put(assignment, grade);
            assignment.addGrade(this.getUsername(), grade);
        }
    }

    /*
     * Gets the grade for a specific assignment
     */
    public Grade getGradeForAssignment(Assignment assignment) {
        return grades.get(assignment);
    }

    /*
     * Calculates the student's overall GPA based on completed courses
     */
//    public double calculateGPA(GradingStrategy strategy) {
//    	// use GradingStrategy (which also will handle class averages)
//    }
    
    /*
     * Converts percentage grade to GPA
     */
    public double convertToGPA(double percentage) {
        return GradeScale.fromPercentage(percentage).getGpaValue();
    }
    
    /*
     * Converts percentage grade to a letter grade
     */
    public String getLetterGrade(double percentage) {
        return GradeScale.fromPercentage(percentage).getLetter();
    }
    
    /*
     * Gets all feedback comments for assignments in a course
     * Returns a map of assignment names to feedback comments
     */
    public Map<String, String> getFeedbackByCourse(Course course) {
        Map<String, String> feedbackMap = new HashMap<String, String>();
        for (Assignment a : course.getAllAssignments()) {
            Grade g = grades.get(a);
            if (g != null && g.getFeedback() != null) {
                feedbackMap.put(a.getName(), g.getFeedback());
            }
        }
        return feedbackMap;
    }
}
