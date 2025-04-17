package controller;

import model.Assignment;
import model.Course;
import model.Grade;
import model.GradingCategory;
import model.Student;
import model.Teacher;
import model.grading.GradeCalculator;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * This is the TeacherController, handles teacher actions.
 * The View calls methods here, and this controller talks to the Model.
 *  It's the main controller for the stuff teachers do. 
 * It's supposed to connect the teacher's view, like the buttons they click, 
 * to the actual model data. 
 * I  made empty method stubs for basically all of them like addGrade, viewCourses, 
 * calculateClassAverage, sortStudentsByName, importStudentsFromFile, etc
 *  Each method just prints a TODO right now, but it lays out all the actions this 
 *  controller needs to handle eventually. 
 *  It needs the Teacher object for the logged in user to work with.
 */
public class TeacherController {

    //Knows which teacher is currently logged in.
    private Teacher theCurrentTeacherUsingTheSystem;

    /**
     * Constructor, needs the logged in Teacher object.
     */
    public TeacherController(Teacher loggedInTeacher) {
        this.theCurrentTeacherUsingTheSystem = loggedInTeacher;
        System.out.println("TeacherController ready for teacher: " + loggedInTeacher.getUsername());
    }

    //teacher actions

    /**
     * Gets the list of courses this teacher teaches. For the dashboard UI.
     * TODO: Implement fetching courses from the teacher model object.
     * @return A List of Course objects.
     */
    public List<Course> viewCourses() {
        System.out.println("TeacherController: viewCourses TODO");
        return new ArrayList<>();
    }

    /**
     * Adds a student to a course roster. Called from UI. Updates Course model.
     * TODO: Implement using Course.enrollStudent()
     * @param studentToAdd The Student object.
     * @param courseToAddTo The Course object.
     * @return true if ok, false if error.
     */
    public boolean addStudentToCourse(Student studentToAdd, Course courseToAddTo) {
        System.out.println("TeacherController: addStudentToCourse TODO");
        return false;
    }

    /**
     * Removes a student from a course roster. Called from UI. Updates Course model.
     * TODO: Implement using Course.removeStudent()
     * @param studentToRemove The Student object.
     * @param courseToRemoveFrom The Course object.
     * @return true if ok, false if error.
     */
    public boolean removeStudentFromCourse(Student studentToRemove, Course courseToRemoveFrom) {
        System.out.println("TeacherController: removeStudentFromCourse TODO");
        return false;
    }

     /**
      * Adds a new assignment definition to a course. Called from UI. Updates Course model.
      * TODO: Implement using Course.addAssignment()
      * @param assignmentToAdd The new Assignment object.
      * @param courseToAddTo   The Course object.
      * @return true if ok, false if error.
      */
     public boolean addAssignmentToCourse(Assignment assignmentToAdd, Course courseToAddTo) {
        System.out.println("TeacherController: addAssignmentToCourse TODO");
        return false;
     }

     /**
      * Removes an assignment definition from a course. Called from UI.
      * TODO: Implement. Needs Course method. Need to decide about deleting existing grades for it too.
      * @param assignmentToRemove The Assignment object.
      * @param courseToRemoveFrom The Course object.
      * @return true if ok, false if error.
      */
     public boolean removeAssignmentFromCourse(Assignment assignmentToRemove, Course courseToRemoveFrom) {
        System.out.println("TeacherController: removeAssignmentFromCourse TODO");
        return false;
     }

    /**
     * Gets the list of students enrolled in a course. For UI roster display.
     * TODO: Implement using Course.getEnrolledStudents()
     * @param theCourse The Course object.
     * @return A List of Student objects.
     */
    public List<Student> viewStudentsInCourse(Course theCourse) {
        System.out.println("TeacherController: viewStudentsInCourse TODO");
        return new ArrayList<>();
    }

    /**
     * Records or updates a grade for a student on an assignment. 
     * TODO: Implement. Needs to create Grade object, call student.addGrade and assignment.addGrade.
     * @param theStudent    The Student object.
     * @param theAssignment The Assignment object
     * @param scoreEarned   The score double.
     * @param feedbackText  The feedback String.
     * @return true if saved ok, false if error.
     */
    public boolean addGrade(Student theStudent, Assignment theAssignment, double scoreEarned, String feedbackText) {
        System.out.println("TeacherController: addGrade TODO");
        return false;
    }

    /**
     * Calculates the class average score on one assignment.
     * TODO: Implement. Use Assignment.getAllGrades(), loop, calculate average. Handle empty grades.
     * @param theAssignment The Assignment object.
     * @return The average score double.
     */
    public double calculateClassAverage(Assignment theAssignment) {
        System.out.println("TeacherController: calculateClassAverage TODO");
        return 0.0;
    }

     /**
      * calculates the class median score on one assignment.
      * TODO: Implement. Get scores from Assignment.getAllGrades(), put in list, sort, find middle.
      * @param theAssignment The Assignment object.
      * @return The median score double.
      */
     public double calculateClassMedian(Assignment theAssignment) {
        System.out.println("TeacherController: calculateClassMedian TODO");
        return 0.0;
     }

    /**
     * calculates one student's overall course average using the course's set strategy.
     * TODO: Implement. Get calculator from Course, call calculator.calculateFinalAverage()
     * @param theStudent The Student object.
     * @param theCourse  The Course object.
     * @return The student's average double percentage.
     */
    public double calculateStudentAverage(Student theStudent, Course theCourse) {
        System.out.println("TeacherController: calculateStudentAverage TODO");
        return 0.0;
    }

    /**
     * Gets the student list for a course, sorted by name. For UI roster.
     * TODO: Implement. Get student list copy, use Collections.sort with Comparator for names.
     * @param theCourse The Course object.
     * @param sortByLastName  true for last name, false for first.
     * @param sortAscending   true for A-Z, false for Z-A.
     * @return A new sorted List of Student objects.
     */
    public List<Student> sortStudentsByName(Course theCourse, boolean sortByLastName, boolean sortAscending) {
        System.out.println("TeacherController: sortStudentsByName TODO");
        return new ArrayList<>();
    }

    /**
     * gets the student list, sorted by grade on one assignment. For UI ranking.
     * TODO: Implement. Get student list copy, use Collections.sort with Comparator checking grades.
     * @param theAssignment The Assignment object.
     * @param sortAscending       true for lowest first, false for highest first.
     * @return A new sorted List of Student objects.
     */
    public List<Student> sortStudentsByGrade(Assignment theAssignment, boolean sortAscending) {
        System.out.println("TeacherController: sortStudentsByGrade TODO");
        return new ArrayList<>();
    }

     /**
      * Assigns the final letter grade for a student in a course.
      * TODO: Implement. Need place in model to store this. Validate grade string? Use GradeScale enum?
      * @param theStudent The Student object.
      * @param theCourse  The Course object.
      * @param finalLetterGrade The letter grade String.
      * @return true if stored ok, false if error.
      */
     public boolean assignFinalGrade(Student theStudent, Course theCourse, String finalLetterGrade) {
        System.out.println("TeacherController: assignFinalGrade TODO");
        return false;
     }

    /**
     * Finds assignments in a course not yet marked as graded. Helps teacher track work.
     * TODO: Implement. Use Course.getAllAssignments() and Assignment.isGraded(). Filter list.
     * @param theCourse The Course object.
     * @return A List of Assignment objects that are not graded.
     */
    public List<Assignment> viewUngradedAssignments(Course theCourse) {
        System.out.println("TeacherController: viewUngradedAssignments TODO");
        return new ArrayList<>();
    }

    /**
     * Imports students from a file like CSV and adds them to a course.
     * TODO: need to implement file reading, parsing, 
     *  needs Student lookup or creation logic maybe using UserController.
     * @param filePathOnComputer the path to the student file String.
     * @param theCourse    The Course object.
     * @return true if finished ok, false if major error.
     */
    public boolean importStudentsFromFile(String filePathOnComputer, Course theCourse) {
        System.out.println("TeacherController: importStudentsFromFile TODO");
        return false;
    }

    //COURSE SETTING UP METHODS. 

    /**
     * Sets the GradeCalculator strategy for a course like Points or Category.
     * TODO: Implement. Needs Course method like setGradeCalculator()
     * @param theCourse   The Course object.
     * @param calculatorToUse The GradeCalculator strategy instance.
     * @return true if set ok, false if error.
     */
    public boolean setCourseGradingMode(Course theCourse, GradeCalculator calculatorToUse) {
         System.out.println("TeacherController: setCourseGradingMode TODO");
         return false;
     }

     /**
      * Sets up the grading categories names, weights, drops for a course using category grading.
      * TODO: Implement. Needs Course method like addGradingCategory(). How are details passed?
      * @param theCourse The Course object.
      * @param categoryDetails Info for categories maybe List<GradingCategory>?.
      * @return true if setup ok, false if error.
      */
     public boolean setupAssignmentCategories(Course theCourse /*, List<GradingCategory> categoryDetails */) {
         System.out.println("TeacherController: setupAssignmentCategories TODO");
         return false;
     }
}