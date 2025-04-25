package controller;

import model.Assignment;
import model.Course;
import model.Grade;
import model.GradingCategory;
import model.Student;
import model.Teacher;
import model.User;
import model.grading.GradeCalculator;
import model.GradeScale;
import util.StudentImporter; //new importer class

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
// import java.io.BufferedReader; //importer handles these now
// import java.io.FileReader;
// import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * This is the TeacherController, handles teacher actions.
 * The TeacherView calls methods in here when buttons are clicked.
 * This controller talks to the Model classes Course, Assignment, Student etc
 * or uses helper classes like StudentImporter to get the actual work done.
 * It needs the Teacher who's logged in and the UserController.
 */
public class TeacherController {

    //need to keep track of the teacher using the app.
    private Teacher theCurrentTeacherUsingTheSystem;
    
    //need the user controller mainly for the student importer helper.
    private UserController userController;

    /**
     * Constructor for TeacherController.
     * stores the teacher and user controller objects passed in.
     * Prints status messages.
     */
    public TeacherController(Teacher loggedInTeacher, UserController userCtrl) {
        this.theCurrentTeacherUsingTheSystem = loggedInTeacher;
        this.userController = userCtrl;
        // Basic checks
        if (loggedInTeacher != null) {
             System.out.println("TeacherController ready for teacher: " + loggedInTeacher.getUsername());
        } 
        
        else {
             System.out.println("TeacherController problem: started with null teacher");
        }
        
        if (userCtrl == null) {
             System.out.println("TeacherController problem: started with null userController");
        }
    }

    //teacher actions

    /**
     * viewCourses gets the list of courses this teacher teaches.
     * Called by the TeacherView to show the courses in the dropdown.
     * It just asks the Teacher model object for its list.
     * @return A List of Course objects, or empty list if no teacher logged in.
     */
    public List<Course> viewCourses() {
        System.out.println("TeacherController getting courses");
        // Check if we know who the teacher is.
        boolean haveTeacher = (theCurrentTeacherUsingTheSystem != null);
        if (haveTeacher) {
             // Ask the Teacher object for its list. Getter returns a copy.
             List<Course> courses = theCurrentTeacherUsingTheSystem.getCoursesTaught();
             return courses;
        }
        //if no teacher, return empty list.
        System.out.println("TeacherController problem: no teacher for viewCourses");
        return new ArrayList<>();
    }

    /**
     * addStudentToCourse adds a student to the selected course.
     * Called from the TeacherView. Tells the Course model to enroll the student.
     * Checks inputs first.
     * @param studentToAdd The Student object.
     * @param courseToAddTo The Course object.
     * @return true if seems ok, false if inputs null.
     */
    public boolean addStudentToCourse(Student studentToAdd, Course courseToAddTo) {
        System.out.println("TeacherController adding student to course");
        // Check inputs.
        boolean studentExists = (studentToAdd != null);
        boolean courseExists = (courseToAddTo != null);
        if (!studentExists || !courseExists) {
            System.out.println("TeacherController problem: addStudentToCourse got null inputs");
            return false;
        }
        // give to Course model method.
        courseToAddTo.enrollStudent(studentToAdd);
        // Assume ok.
        return true;
    }

    /**
     * removeStudentFromCourse removes a student from the selected course.
     * Called from the TeacherView. Tells the Course model to remove the student.
     * Checks inputs first.
     * @param studentToRemove The Student object.
     * @param courseToRemoveFrom The Course object.
     * @return true if seems ok, false if inputs null.
     */
    public boolean removeStudentFromCourse(Student studentToRemove, Course courseToRemoveFrom) {
        System.out.println("TeacherController removing student from course");
        // Check inputs.
        boolean studentExists = (studentToRemove != null);
        boolean courseExists = (courseToRemoveFrom != null);
        if (!studentExists || !courseExists) {
            System.out.println("TeacherController problem: removeStudentFromCourse got null inputs");
            return false;
        }
        // give to Course model method.
        courseToRemoveFrom.removeStudent(studentToRemove);
        // Assume ok.
        return true;
    }

     /**
      * addAssignmentToCourse adds a new assignment definition to a course.
      * Called from TeacherView. Tells the Course model to add it.
      * Checks inputs first.
      * @param assignmentToAdd The new Assignment object.
      * @param courseToAddTo   The Course object.
      * @return true if seems ok, false if inputs null.
      */
     public boolean addAssignmentToCourse(Assignment assignmentToAdd, Course courseToAddTo) {
        System.out.println("TeacherController adding assignment to course");
        // Check inputs.
        boolean assignmentExists = (assignmentToAdd != null);
        boolean courseExists = (courseToAddTo != null);
        if (!assignmentExists || !courseExists) {
            System.out.println("TeacherController problem: addAssignmentToCourse got null inputs");
            return false;
        }
        // give to Course model method.
        courseToAddTo.addAssignment(assignmentToAdd);
        // Assume ok.
        return true;
     }

     /**
      * removeAssignmentFromCourse removes an assignment definition from a course.
      * Called from TeacherView. Tells the Course model to remove it.
      * Course model method should handle removing related grades. Checks inputs first.
      * @param assignmentToRemove The Assignment object.
      * @param courseToRemoveFrom The Course object.
      * @return true if seems ok, false if inputs null.
      */
     public boolean removeAssignmentFromCourse(Assignment assignmentToRemove, Course courseToRemoveFrom) {
        System.out.println("TeacherController removing assignment from course");
        // Check inputs.
        boolean assignmentExists = (assignmentToRemove != null);
        boolean courseExists = (courseToRemoveFrom != null);
        if (!assignmentExists || !courseExists) {
            System.out.println("TeacherController problem: removeAssignmentFromCourse got null inputs");
            return false;
        }
        // give to Course model method.
        courseToRemoveFrom.removeAssignment(assignmentToRemove);
        // Assume ok.
        return true;
     }

    /**
     * viewStudentsInCourse gets the list of students enrolled in a course.
     * Called by TeacherView to show the roster. Asks the Course object for the list.
     * Checks if course exists first.
     * @param theCourse The Course object.
     * @return A List of Student objects or empty list.
     */
    public List<Student> viewStudentsInCourse(Course theCourse) {
        System.out.println("TeacherController getting students for course");
        // Check course exists.
        boolean courseExists = (theCourse != null);
        if (courseExists) {
            // give to Course model getter returns copy.
            return theCourse.getEnrolledStudents();
        }
        
        System.out.println("TeacherController problem: viewStudentsInCourse got null course");
        return new ArrayList<>();
    }

    /**
     * addGrade records or updates a grade for one student on one assignment.
     * Called by TeacherView after teacher enters grade info.
     * It makes the Grade object. Tells Student model to store it. Tells Assignment model
     * to store it too. Tells Assignment model to mark itself as graded.
     * Checks inputs and handles potential errors like bad scores.
     *
     * @param theStudent    The Student object getting the grade.
     * @param theAssignment The Assignment object being graded.
     * @param scoreEarned   The score number the student got.
     * @param feedbackText  The feedback comments teacher wrote.
     * @return true if grade saved ok, false if error happened.
     */
    public boolean addGrade(Student theStudent, Assignment theAssignment, double scoreEarned, String feedbackText) {
        //print message for context.
    	String studentName;
    	if (theStudent != null) {
    	    studentName = theStudent.getUsername();
    	} 
    	
    	else {
    	    studentName = "null student?";
    	}

    	String assignmentName;
    	if (theAssignment != null) {
    	    assignmentName = theAssignment.getName();
    	} 
    	
    	else {
    	    assignmentName = "null assignment?";
    	}

    	System.out.println("TeacherController adding grade for " + studentName + " on " + assignmentName);

        //check inputs first.
        boolean studentExists = (theStudent != null);
        boolean assignmentExists = (theAssignment != null);
        
        if (!studentExists || !assignmentExists) {
            System.out.println("TeacherController problem: addGrade got null student or assignment");
            return false;
        }

        // Use try catch because Grade constructor might throw error for bad score.
        try {
            // Make the Grade object first.
            Grade gradeObject = new Grade(scoreEarned, feedbackText);

            // Tell the Student model to store it.
            theStudent.addGrade(theAssignment, gradeObject);

            // Tell the Assignment model to store it too.
            theAssignment.addGrade(theStudent.getUsername(), gradeObject);

            // Tell the Assignment model to mark itself as graded.
            theAssignment.markGraded();

            System.out.println("Grade added successfully.");
            // Models should fire observer events now.
            return true; // It worked.
        } 
        
        catch (IllegalArgumentException badScoreError) {
            // Handle invalid score from Grade constructor.
            System.out.println("TeacherController problem: addGrade got invalid score " + scoreEarned);
            return false; // Failed.
        } 
        
        catch (Exception anyOtherError) {
            // Handle any other unexpected crash.
             System.out.println("TeacherController problem: addGrade unexpected error");
             anyOtherError.printStackTrace(); // Print details for debugging.
            
             return false; // Failed
        }
    }

    /**
     * calculateClassAverage calculates the average score for one assignment.
     * It just asks the Assignment object to calculate its own average using its method.
     * Checks input first.
     * @param theAssignment The Assignment object.
     * @return The average score double, or 0.0 if assignment null.
     */
    public double calculateClassAverage(Assignment theAssignment) {
        System.out.println("TeacherController calculating class average");
        // Check assignment exists.
        boolean assignmentExists = (theAssignment != null);
        
        if (!assignmentExists) {
            System.out.println("TeacherController problem: calculateClassAverage got null assignment");
            return 0.0;
        }
        // give calculation to the Assignment model's method.
        return theAssignment.calculateAverageScore();
    }

     /**
      * calculateClassMedian calculates the median score on one assignment.
      * It just asks the Assignment object to calculate its own median using its method.
      * Checks input first.
      * @param theAssignment The Assignment object.
      * @return The median score double, or 0.0 if assignment null.
      */
     public double calculateClassMedian(Assignment theAssignment) {
        System.out.println("TeacherController calculating class median");
        // Check assignment exists.
        boolean assignmentExists = (theAssignment != null);
        
        if (!assignmentExists) {
            System.out.println("TeacherController problem: calculateClassMedian got null assignment");
            return 0.0;
        }
        // give calculation to the Assignment model's method.
        return theAssignment.calculateMedianScore();
     }

    /**
     * calculateStudentAverage calculates one student's overall course average.
     * It asks the Course for its GradeCalculator strategy object.
     * Then it tells that calculator strategy to calculate the average.
     * Checks inputs and if the course actually has a calculator set.
     * @param theStudent The Student object.
     * @param theCourse  The Course object.
     * @return The student's average double percentage, or 0.0 if error.
     */
    public double calculateStudentAverage(Student theStudent, Course theCourse) {
        System.out.println("TeacherController calculating student average");
        //check inputs
        boolean studentExists = (theStudent != null);
        boolean courseExists = (theCourse != null);
        if (!studentExists || !courseExists) {
            System.out.println("TeacherController problem: calculateStudentAverage got null student or course");
            return 0.0;
        }
        
        // Get calculator strategy from Course.
        GradeCalculator calculator = theCourse.getGradeCalculator();
        // Check if course had one.
        boolean calculatorExists = (calculator != null);
        
        if (calculatorExists) {
            // Tell the calculator object to do the work.
            return calculator.calculateFinalAverage(theCourse, theStudent);
        } 
        
        else {
            // If no calculator set, print message and return 0.
            System.out.println("TeacherController problem: calculateStudentAverage - No GradeCalculator set for course " + theCourse.getName());
            return 0.0;
        }
    }

    /**
     * sortStudentsByName gets the student list for a course, sorted by name.
     * It asks the Course object to do the sorting using its method.
     * Checks input first.
     * @param theCourse The Course object.
     * @param sortByLastName  true for last name, false for first.
     * @param sortAscending   true for A-Z, false for Z-A.
     * @return A new sorted List of Student objects. Empty if course null.
     */
    public List<Student> sortStudentsByName(Course theCourse, boolean sortByLastName, boolean sortAscending) {
        System.out.println("TeacherController sorting students by name");
        // Check course exists.
        boolean courseExists = (theCourse != null);
        
        if (!courseExists) {
            System.out.println("TeacherController problem: sortStudentsByName got null course");
            return new ArrayList<>();
        }
        
        // give sorting to the Course model's method.
        return theCourse.getEnrolledStudentsSortedByName(sortByLastName, sortAscending);
    }

    /**
     * sortStudentsByGrade gets the student list, sorted by grade on one assignment.
     * It asks the Course object to do the sorting using its method.
     * Checks inputs first.
     * @param theAssignment The Assignment object.
     * @param theCourse Needs the course object.
     * @param sortAscending true for lowest first, false for highest first.
     * @return A new sorted List of Student objects. Empty list if inputs null.
     */
    public List<Student> sortStudentsByGrade(Assignment theAssignment, Course theCourse, boolean sortAscending) {
        System.out.println("TeacherController sorting students by grade");
        // Check inputs.
        boolean assignmentExists = (theAssignment != null);
        boolean courseExists = (theCourse != null);
        
        if (!assignmentExists || !courseExists) {
             System.out.println("TeacherController problem: sortStudentsByGrade got null assignment or course");
             return new ArrayList<>();
        }
        
        // give sorting to the Course model's method.
        return theCourse.getEnrolledStudentsSortedByAssignmentGrade(theAssignment, sortAscending);
    }

     /**
      * assignFinalGrade assigns the final letter grade for a student in a course.
      * It tells the Student object to store the grade using its setter method.
      * The Student setter method should handle validation of the letter grade string.
      * Checks inputs first.
      * @param theStudent The Student object.
      * @param theCourse  The Course object.
      * @param finalLetterGrade The letter grade String like "A", "B".
      * @return true if stored ok, false if error or null inputs.
      */
     public boolean assignFinalGrade(Student theStudent, Course theCourse, String finalLetterGrade) {
        System.out.println("TeacherController assigning final grade");
        // Check inputs first.
        boolean studentExists = (theStudent != null);
        boolean courseExists = (theCourse != null);
        boolean gradeStringExists = (finalLetterGrade != null && !finalLetterGrade.trim().isEmpty());
        
        if (!studentExists || !courseExists || !gradeStringExists) {
            System.out.println("TeacherController problem: assignFinalGrade got null inputs");
            return false;
        }
        
        try {
            // give setting the grade and validating the letter to the Student model.
            theStudent.setFinalGradeForCourse(theCourse, finalLetterGrade);
            // Student model should fire observer event now.
            return true; // Assume it worked if no error.
        } 
        
        catch (IllegalArgumentException e) {
             // Catch if Student setter throws error for bad letter grade.
             System.out.println("TeacherController problem: assignFinalGrade invalid letter grade " + finalLetterGrade);
             return false;
        } 
        
        catch (Exception e) {
             // Catch other unexpected error
             System.out.println("TeacherController problem: assignFinalGrade unexpected error");
             return false;
        }
     }

    /**
     * viewUngradedAssignments finds assignments in a course not yet marked as graded.
     * Asks the Course for all assignments, then filters the list using a stream.
     * @param theCourse The Course object.
     * @return A List of Assignment objects that are not graded. Empty list if none or error.
     */
    public List<Assignment> viewUngradedAssignments(Course theCourse) {
        System.out.println("TeacherController getting ungraded assignments");
        // Check course exists.
        boolean courseExists = (theCourse != null);
        if (!courseExists) {
            System.out.println("TeacherController problem: viewUngradedAssignments got null course");
            return new ArrayList<>();
        }
        // Get all assignments copy from Course.
        List<Assignment> allAssignments = theCourse.getAllAssignments();
        // Make sure list isn't null.
        boolean assignmentListExists = (allAssignments != null);
        if (!assignmentListExists) {
             return new ArrayList<>();
        }
        // Use stream to filter the list easily.
        List<Assignment> ungradedList = allAssignments.stream()
            .filter(assignment -> assignment != null && !assignment.isGraded()) // Keep only non-null, ungraded ones
            .collect(Collectors.toList()); // Put results into a new list
        // Return the filtered list.
        return ungradedList;
    }

    /**
     * importStudentsFromFile imports EXISTING students from a file into a course.
     * It creates a StudentImporter object and tells that object to do the actual work
     * of reading the file and finding/enrolling students.
     * This keeps the file handling details out of this main controller.
     * @param filePathOnComputer the path to the student file String.
     * @param theCourse    The Course object to add students to.
     * @return true if importer reported success at least one student processed, false otherwise.
     */
    public boolean importStudentsFromFile(String filePathOnComputer, Course theCourse) {
        System.out.println("TeacherController importing students from file: " + filePathOnComputer);
        // Check that we have the things needed to create the importer.
        boolean haveUserController = (this.userController != null);
        boolean courseExists = (theCourse != null);
        boolean pathExists = (filePathOnComputer != null && !filePathOnComputer.isEmpty());
        if (!haveUserController || !courseExists || !pathExists) {
             System.out.println("TeacherController problem: importStudentsFromFile missing stuff needed for importer");
            return false;
        }

        // Make a new StudentImporter object, giving it the controllers it needs.
        StudentImporter importer = new StudentImporter(this.userController, this);
        
        // Tell the importer object to process the file and enroll students in the course.
        boolean importResult = importer.importFromFile(filePathOnComputer, theCourse);
        
        //return whatever boolean result the importer returned true if ok, false if error.
        return importResult;
    }


    //COURSE SETTING UP METHODS.

    /**
     * setCourseGradingMode sets the GradeCalculator strategy points or category for a course.
     * It tells the Course object which calculator instance to use using its setter.
     * Checks inputs first.
     * @param theCourse   The Course object.
     * @param calculatorToUse The GradeCalculator object e.g., new PointsBasedCalculator().
     * @return true if set ok, false if inputs null.
     */
    public boolean setCourseGradingMode(Course theCourse, GradeCalculator calculatorToUse) {
         System.out.println("TeacherController setting course grading mode");
         // Check inputs.
         boolean courseExists = (theCourse != null);
         boolean calculatorExists = (calculatorToUse != null);
         
         if (!courseExists || !calculatorExists) {
             System.out.println("TeacherController problem: setCourseGradingMode got null inputs");
             return false;
         }
         
         // give to Course model's setter method.
         theCourse.setGradeCalculator(calculatorToUse);
         
         //assume ok.
         return true;
     }

     /**
      * setupAssignmentCategories configures the grading categories like name, weight, drops
      * for a course that uses category based grading.
      * It tells the Course model to clear any old categories first, then adds
      * all the new category definitions from the list provided.
      * Checks inputs first.
      * @param theCourse The Course object to configure.
      * @param categoryDetails List of new GradingCategory objects to add.
      * @return true if setup ok, false if inputs null.
      */
     public boolean setupAssignmentCategories(Course theCourse, List<GradingCategory> categoryDetails ) {
         System.out.println("TeacherController setting up assignment categories");
         // Check inputs.
         boolean courseExists = (theCourse != null);
         boolean detailsExist = (categoryDetails != null); // Check list itself exists
         
         if (!courseExists || !detailsExist) {
             System.out.println("TeacherController problem: setupAssignmentCategories got null inputs");
             return false;
         }
         
         // Tell Course model to remove old categories.
         theCourse.clearGradingCategories();
         
         // Loop through the provided list of new categories.
         for (GradingCategory category : categoryDetails) {
             //check if category object is valid.
             boolean categoryIsValid = (category != null);
             if (categoryIsValid) {
                 // Tell Course model to add this one. 
                 theCourse.addGradingCategory(category);
             } 
             
             else {
                 System.out.println("TeacherController warning: setupAssignmentCategories found null category in list");
             }
         }
         // Assume worked ok if loop finished.
         return true;
     }

     /**
      * getStudentByUsername is a helper maybe used by other methods or UI.
      * Asks UserController to find user, checks if it's a Student.
      * @param username The username string to look for.
      * @return The Student object if found and is student, otherwise null.
      */
     public Student getStudentByUsername(String username) {
         boolean canSearch = (userController != null && username != null && !username.isEmpty());
         if (!canSearch) {
             return null;
         }
         
         User user = userController.findUserByUsername(username);
         if (user instanceof Student) {
             return (Student) user;
         }
         
         return null;
    	}

}