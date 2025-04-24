package controller;

import model.Assignment;
import model.Course;
import model.Grade;
import model.GradingCategory;
import model.Student;
import model.Teacher;
import model.User;
import model.grading.GradeCalculator;
import model.GradeScale; // Needed for assignFinalGrade maybe

import java.util.List;
import java.util.ArrayList;
import java.util.Map; // Needed for assignment grades maybe
import java.io.BufferedReader; // For file import later
import java.io.FileReader; // For file import later
import java.io.IOException; // For file import later
import java.util.Collections; // For sorting later
import java.util.Comparator; // For sorting later
import java.util.stream.Collectors; // Using this for cleaner filtering

/**
 * This is the TeacherController, handles teacher actions.
 * The View calls methods here, and this controller talks to the Model.
 * Needs the Teacher object for the logged in user.
 */
public class TeacherController {

    // Knows which teacher is currently logged in.
    private Teacher theCurrentTeacherUsingTheSystem;
    // Need UserController to find/create users during import
    private UserController userController; 

    /**
     * Constructor, needs the logged in Teacher object and UserController.
     */
    public TeacherController(Teacher loggedInTeacher, UserController userCtrl) { // Added UserController
        this.theCurrentTeacherUsingTheSystem = loggedInTeacher;
        this.userController = userCtrl; // Store UserController

        if (loggedInTeacher != null) {
             System.out.println("TeacherController ready for teacher: " + loggedInTeacher.getUsername());
        } 
        
        else {
             System.out.println("TeacherController WARNING: initialized with null teacher");
        }
        if (userCtrl == null) {
             System.out.println("TeacherController WARNING: initialized with null userController");
        }
    }

    //teacher actions

    /**
     * Gets the list of courses this teacher teaches. For the dashboard UI.
     * Retrieves courses from the teacher model object using the getter.
     * @return A List of Course objects (or empty list if no teacher).
     */
    public List<Course> viewCourses() {
        System.out.println("TeacherController: viewCourses executing");
        // Make sure we have a teacher logged in
        boolean haveTeacher = (theCurrentTeacherUsingTheSystem != null);
        if (haveTeacher) {
             // Ask the Teacher object for its courses, uses the safe copy getter
             List<Course> courses = theCurrentTeacherUsingTheSystem.getCoursesTaught();
             return courses;
        }
        System.out.println("TeacherController: viewCourses - No current teacher");
        // Return an empty list if no teacher is set
        return new ArrayList<>();
    }

    /**
     * Adds a student to a course roster. Called from UI. Updates Course model.
     * TODO: Implement logic using Course.enrollStudent().
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
     * TODO: Implement logic using Course.removeStudent().
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
      * TODO: Implement logic using Course.addAssignment().
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
      * TODO: Implement logic using Course.removeAssignment().
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
     * Uses Course.getEnrolledStudents().
     * @param theCourse The Course object.
     * @return A List of Student objects (or empty list).
     */
    public List<Student> viewStudentsInCourse(Course theCourse) {
        System.out.println("TeacherController: viewStudentsInCourse executing");
        // Check if the course object exists
        boolean courseExists = (theCourse != null);
        if (courseExists) {
            // Ask the Course object for its list of students. This getter returns a copy.
            List<Student> students = theCourse.getEnrolledStudents();
            return students;
        }
        System.out.println("TeacherController: viewStudentsInCourse - null course provided");
        // If course was null, return an empty list.
        return new ArrayList<>();
    }

    /**
     * Records or updates a grade for a student on an assignment.
     * TODO: Implement. Needs Grade object. Call student.addGrade AND assignment.addGrade. 
     * @param theStudent    The Student object.
     * @param theAssignment The Assignment object.
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
     * TODO: Implement. Get student list copy, use Collections.sort with Comparator checking grades. Handle null grades.
     * @param theAssignment The Assignment object.
     * @param sortAscending       true for lowest first, false for highest first.
     * @return A new sorted List of Student objects.
     */
    public List<Student> sortStudentsByGrade(Assignment theAssignment, Course theCourse, boolean sortAscending) {
        // Added Course parameter back, needed to get student list
        System.out.println("TeacherController: sortStudentsByGrade TODO");
        return new ArrayList<>();
    }

     /**
      * Assigns the final letter grade for a student in a course.
      * TODO: Implement. Need place in model to store this. Use GradeScale enum?
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
     * Uses Course.getAllAssignments() and Assignment.isGraded(). Filters the list.
     * @param theCourse The Course object.
     * @return A List of Assignment objects that are not graded.
     */
    public List<Assignment> viewUngradedAssignments(Course theCourse) {
        System.out.println("TeacherController: viewUngradedAssignments executing");
        
        //check if course exists
        boolean courseExists = (theCourse != null);
        if (!courseExists) {
            System.out.println("null course");
            return new ArrayList<>(); // Return empty if no course
        }

        //get all assignments from the course ,this is a copy .
        List<Assignment> allAssignments = theCourse.getAllAssignments();
        List<Assignment> ungradedList = new ArrayList<>();

        //check if the list is not null before looping
        boolean assignmentListExists = (allAssignments != null);
        
        if (assignmentListExists) {
            //loop through each assignment
            for (Assignment assignment : allAssignments) {
                // Check if assignment object itself is ok and if it's not graded
                boolean assignmentIsValid = (assignment != null);
                
                if (assignmentIsValid) {
                     boolean isItGraded = assignment.isGraded();
                     
                     if (!isItGraded) {
                         //if not graded, add it to our results list.
                         ungradedList.add(assignment);
                     }
                }
            }
        }
        //return the list of assignments that passed the check.
        return ungradedList;
    }

    /**
     * Imports students from a file like CSV and adds them to a course.
     * TODO: need to implement file reading, parsing, needs Student lookup/creation logic using UserController.
     * @param filePathOnComputer the path to the student file String.
     * @param theCourse    The Course object.
     * @return true if finished ok, false if major error.
     */
    public boolean importStudentsFromFile(String filePathOnComputer, Course theCourse) {
        // Removed UserController param, assuming it's available via field 'this.userController'
        System.out.println("TeacherController: importStudentsFromFile TODO");
        return false;
    }

    //COURSE SETTING UP METHODS.

    /**
     * Sets the GradeCalculator strategy for a course like Points or Category.
     * Calls the setter method on the Course object. Null checks.
     * @param theCourse   The Course object.
     * @param calculatorToUse The GradeCalculator strategy instance.
     * @return true if set ok (assuming setter exists and works), false if inputs null.
     */
    public boolean setCourseGradingMode(Course theCourse, GradeCalculator calculatorToUse) {
         System.out.println("TeacherController: setCourseGradingMode executing");
         // Check if inputs are valid first
         boolean courseExists = (theCourse != null);
         boolean calculatorExists = (calculatorToUse != null);
         if (!courseExists || !calculatorExists) {
             System.out.println("TeacherController: setCourseGradingMode - null inputs");
             return false;
         }

         // Tell the Course object to use this calculator strategy.
         // Assumes Course has this setter method from Person A's work.
         theCourse.setGradeCalculator(calculatorToUse);

         // For now, assume it worked if the setter didn't crash.
         // Could make the setter return boolean later if needed.
         return true;
     }

     /**
      * Sets up the grading categories names, weights, drops for a course using category grading.
      * Uses Course.addGradingCategory(). Assumes categoryDetails list is provided.
      * @param theCourse The Course object.
      * @param categoryDetails List of GradingCategory objects to add.
      * @return true if setup ok, assuming adds work, false if inputs null.
      */
     public boolean setupAssignmentCategories(Course theCourse, List<GradingCategory> categoryDetails ) {
         System.out.println("TeacherController, setupAssignmentCategories executing");
         //check inputs first
         boolean courseExists = (theCourse != null);
         boolean detailsExist = (categoryDetails != null);
         if (!courseExists || !detailsExist) {
             System.out.println(" null inputs");
             return false;
         }

         // Might want to clear old categories first? Depends on.
         // Assumes Course has clearGradingCategories() if needed.
         // theCourse.clearGradingCategories();

         //loop through the list of category objects provided
         for (GradingCategory category : categoryDetails) {
             // make sure the category object itself isn't null
             boolean categoryIsValid = (category != null);
             if (categoryIsValid) {
                 //tell the Course object to add this category.
                 //assumes Course has this add method from Person A's work.
                 theCourse.addGradingCategory(category);
             }
         }
         //assume it worked if no errors happened during the loop.
         return true;
     }
     public Student getStudentByUsername(String username) {
    	    if (userController == null || username == null || username.isEmpty()) {
    	        return null;
    	    }
    	    User user = userController.findUserByUsername(username);
    	    if (user instanceof Student) {
    	        return (Student) user;
    	    }
    	    return null;
    	}

}