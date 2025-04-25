package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.grading.GradeCalculator;
import model.grading.PointsBasedCalculator;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * Represents a student user in the gradebook system.
 * Extends the base User class with student-specific functionality.
 */
public class Student extends User {
    private final String studentId;					
    private final List<Course> currentCourses;
    private final List<Course> completedCourses;
    private final Map<Assignment, Grade> grades;
    private final Map<Course, String> finalGrades;
    
    private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    // Constructor
    public Student(String firstName, String lastName, String email, String password, String username, String studentId) {
        super(firstName, lastName, email, password, username, Role.STUDENT);
        this.studentId = studentId;
        this.currentCourses = new ArrayList<>();
        this.completedCourses = new ArrayList<>();
        this.grades = new HashMap<>();
        this.finalGrades = new HashMap<>();
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
            pcs.firePropertyChange("courseEnrolled", null, course);
        }
    }

    /*
     * Marks a course as completed by moving it from current to completed
     */
    public void completeCourse(Course course) {
        if (currentCourses.contains(course)) {
            // Calculate average and set final grade BEFORE moving course
            if (!finalGrades.containsKey(course)) {
                double average = calculateClassAverage(course);
                if (average > 0) {
                    String letterGrade = getLetterGrade(average);
                    finalGrades.put(course, letterGrade);
                }
            }
            
            // Move course to completed
            currentCourses.remove(course);
            completedCourses.add(course);
            
            pcs.firePropertyChange("courseCompleted", null, course);
        }
    }
    
    /*
     * Adds a grade for an assignment
     */
    public void addGrade(Assignment assignment, Grade grade) {
        if (assignment != null && grade != null) {
            grades.put(assignment, grade);
            assignment.addGrade(this.getUsername(), grade);
            pcs.firePropertyChange("gradeAdded", null, assignment);
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
    public double calculateGPA() {
        if (completedCourses.isEmpty()) {
            return 0.0;
        }
        
        double totalGPA = 0.0;
        int count = 0;
        
        for (Course course : completedCourses) {
            String letterGrade = finalGrades.get(course);
            if (letterGrade != null && !letterGrade.isEmpty()) {
                totalGPA += GradeScale.fromLetter(letterGrade).getGpaValue();
                count++;
            }
        }
        
        return count > 0 ? totalGPA / count : 0.0;
    }
    
    /*
     * Calculates the student's average in a specific course
     */
    public double calculateClassAverage(Course theCourse) {
    	if (theCourse == null || !currentCourses.contains(theCourse)) {
            return 0.0;
        }
        
        // Ensure calculator exists
        GradeCalculator calculator = theCourse.getGradeCalculator();
        if (calculator == null) {
            calculator = new PointsBasedCalculator();
            theCourse.setGradeCalculator(calculator);
        }
        
        // Calculate average
        double average = calculator.calculateFinalAverage(theCourse, this);
        
        // Ensure we don't return NaN or negative values
        return average > 0 ? average : 0.0;
    }
    
    /*
     * Sets the final letter grade for a course
     */
    public void setFinalGradeForCourse(Course theCourse, String letterGrade) {
        if (theCourse != null && letterGrade != null && currentCourses.contains(theCourse)) {
            finalGrades.put(theCourse, letterGrade);
            pcs.firePropertyChange("finalGradeSet", null, new Object[]{theCourse, letterGrade});
        }
    }
    
    /*
     * Gets the final letter grade for a course
     */
    public String getFinalGradeForCourse(Course theCourse) {
        return finalGrades.get(theCourse);
    }
    
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
    
    // Observer pattern support
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
