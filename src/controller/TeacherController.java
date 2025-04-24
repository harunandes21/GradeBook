package controller;

import model.Assignment;
import model.Course;
import model.Grade;
import model.GradingCategory;
import model.Student;
import model.Teacher;
import model.User; // Needed for import check
import model.grading.GradeCalculator;
import model.GradeScale; // Needed for assignFinalGrade

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.BufferedReader; // For file import
import java.io.FileReader; // For file import
import java.io.IOException; // For file import
import java.util.Collections; // For sorting
import java.util.Comparator; // For sorting
import java.util.stream.Collectors; // Used in viewUngradedAssignments

/**
 * This is the TeacherController, handles teacher actions.
 * The View calls methods here, and this controller talks to the Model.
 * Needs the Teacher object for the logged in user to know who is doing stuff
 * and the UserController to help find users sometimes like during import.
 */
public class TeacherController {

    // Knows which teacher is currently logged in.
    private Teacher theCurrentTeacherUsingTheSystem;
    // Need UserController to find users during import maybe create later.
    private UserController userController;

    /**
     * Constructor, needs the logged in Teacher object and UserController.
     * It just stores these so the other methods can use them.
     * Also prints some messages to say if things look okay.
     */
    public TeacherController(Teacher loggedInTeacher, UserController userCtrl) {
        this.theCurrentTeacherUsingTheSystem = loggedInTeacher;
        this.userController = userCtrl;
        // Basic check to make sure we got a teacher.
        if (loggedInTeacher != null) {
             System.out.println("TeacherController ready for teacher: " + loggedInTeacher.getUsername());
        } 
        
        else {
             // Warn if no teacher given, things might break later.
             System.out.println("TeacherController problem: started with null teacher");
        }
        // Basic check for the user controller too.
        if (userCtrl == null) {
             System.out.println("TeacherController problem: started with null userController");
        }
    }

    //teacher actions

    /**
     * Gets the list of courses this teacher teaches. Used for the dashboard UI.
     * It asks the Teacher object we stored earlier for its list of courses.
     * It returns a copy of the list so the UI cant mess up the original.
     * @return A List of Course objects, or an empty list if no teacher logged in.
     */
    public List<Course> viewCourses() {
        System.out.println("TeacherController getting courses");
        // Check if we know who the current teacher is.
        boolean haveTeacher = (theCurrentTeacherUsingTheSystem != null);
        if (haveTeacher) {
             // Ask the Teacher object for its courses.
             // The getCoursesTaught method already gives us a safe copy.
             List<Course> courses = theCurrentTeacherUsingTheSystem.getCoursesTaught();
             return courses;
        }
        // If there's no teacher logged in, just return an empty list.
        System.out.println("TeacherController problem: viewCourses called but no teacher");
        return new ArrayList<>();
    }

    /**
     * Adds a student to a course roster. Gets called from the UI.
     * It tells the Course object to enroll the student.
     * Does checks first to make sure we got a student and course.
     * @param studentToAdd The Student object the teacher wants to add.
     * @param courseToAddTo The Course object they should be added to.
     * @return true if it seems to work, false if inputs were null.
     */
    public boolean addStudentToCourse(Student studentToAdd, Course courseToAddTo) {
        System.out.println("TeacherController adding student to course");
        // Check if the student and course actually exist first.
        boolean studentExists = (studentToAdd != null);
        boolean courseExists = (courseToAddTo != null);
        if (!studentExists || !courseExists) {
            System.out.println("TeacherController problem: addStudentToCourse got null student or course");
            return false; // Can't add if something is missing.
        }
        // Tell the Course object to enroll this student.
        // The Course's enrollStudent method handles checking if they're already there.
        courseToAddTo.enrollStudent(studentToAdd);
        // We'll assume it worked if the enrollStudent method didn't crash.
        return true;
    }

    /**
     * Removes a student from a course roster. Gets called from the UI.
     * It tells the Course object to remove the student.
     * Does checks first to make sure we got a student and course.
     * @param studentToRemove The Student object the teacher wants to remove.
     * @param courseToRemoveFrom The Course object they should be removed from.
     * @return true if it seems to work, false if inputs were null.
     */
    public boolean removeStudentFromCourse(Student studentToRemove, Course courseToRemoveFrom) {
        System.out.println("TeacherController removing student from course");
        // Check if the student and course objects exist.
        boolean studentExists = (studentToRemove != null);
        boolean courseExists = (courseToRemoveFrom != null);
        if (!studentExists || !courseExists) {
            System.out.println("TeacherController problem: removeStudentFromCourse got null student or course");
            return false; // Can't remove if something is missing.
        }
        // Tell the Course object to remove this student.
        courseToRemoveFrom.removeStudent(studentToRemove);
        
        // Assume it worked if the method didn't crash.
        return true;
    }

     /**
      * Adds a new assignment definition to a course. Gets called from the UI.
      * It tells the Course object to add the new assignment.
      * Checks inputs first.
      * @param assignmentToAdd The new Assignment object the teacher created.
      * @param courseToAddTo   The Course object this assignment belongs to.
      * @return true if it seems to work, false if inputs were null.
      */
     public boolean addAssignmentToCourse(Assignment assignmentToAdd, Course courseToAddTo) {
        System.out.println("TeacherController adding assignment to course");
        // Check if the assignment and course objects exist.
        boolean assignmentExists = (assignmentToAdd != null);
        boolean courseExists = (courseToAddTo != null);
        if (!assignmentExists || !courseExists) {
            System.out.println("TeacherController problem: addAssignmentToCourse got null assignment or course");
            return false; // Can't add if something is missing.
        }
        // Tell the Course object to add this assignment.
        courseToAddTo.addAssignment(assignmentToAdd);
        // Assume it worked if no crash.
        return true;
     }

     /**
      * Removes an assignment definition from a course. Gets called from the UI.
      * It tells the Course object to remove the assignment.
      * The Course object's removeAssignment method should handle cleaning up grades too.
      * Checks inputs first.
      * @param assignmentToRemove The Assignment object the teacher wants to delete.
      * @param courseToRemoveFrom The Course object it belongs to.
      * @return true if it seems to work, false if inputs were null.
      */
     public boolean removeAssignmentFromCourse(Assignment assignmentToRemove, Course courseToRemoveFrom) {
        System.out.println("TeacherController removing assignment from course");
        // Check if the assignment and course exist.
        boolean assignmentExists = (assignmentToRemove != null);
        boolean courseExists = (courseToRemoveFrom != null);
        if (!assignmentExists || !courseExists) {
            System.out.println("TeacherController problem: removeAssignmentFromCourse got null assignment or course");
            return false; // Can't remove if something missing.
        }
        // Tell the Course object to remove the assignment.
        // This uses the method Person A implemented which should handle grades.
        courseToRemoveFrom.removeAssignment(assignmentToRemove);
        // Assume it worked if no crash.
        return true;
     }

    /**
     * Gets the list of students enrolled in a course. Used by the UI to show the roster.
     * Asks the Course object for its student list. Checks if course exists first.
     * @param theCourse The Course object whose roster is needed.
     * @return A List of Student objects, or an empty list if course was null.
     */
    public List<Student> viewStudentsInCourse(Course theCourse) {
        System.out.println("TeacherController getting students for course");
        // Check if the course object exists first.
        boolean courseExists = (theCourse != null);
        if (courseExists) {
            // Ask the Course object for its student list. The getter returns a copy.
            List<Student> students = theCourse.getEnrolledStudents();
            return students;
        }
        // If the course was null, print a message and return an empty list.
        System.out.println("TeacherController problem: viewStudentsInCourse got null course");
        return new ArrayList<>();
    }

    /**
     * Records or updates a grade for one student on one assignment. This is main grading action.
     * It first makes a new Grade object with the score and feedback.
     * Then it tells the Student object to store this grade internally maybe in its map.
     * Then it also tells the Assignment object to store the grade in its map keyed by username.
     * Finally it marks the assignment itself as graded using Assignment.markGraded.
     * Checks inputs first, includes a try catch for score validation in Grade constructor.
     *
     * @param theStudent    The Student object getting the grade.
     * @param theAssignment The Assignment object being graded.
     * @param scoreEarned   The score the student got double.
     * @param feedbackText  The feedback the teacher wrote String.
     * @return true if grade saved ok, false if error like null inputs or bad score.
     */
    public boolean addGrade(Student theStudent, Assignment theAssignment, double scoreEarned, String feedbackText) {
        System.out.println("TeacherController adding grade for " + (theStudent != null ? theStudent.getUsername() : "null student?") + " on " + (theAssignment != null ? theAssignment.getName() : "null assignment?"));
        // Check inputs first. Make sure we have student and assignment.
        boolean studentExists = (theStudent != null);
        boolean assignmentExists = (theAssignment != null);
        if (!studentExists || !assignmentExists) {
            System.out.println("TeacherController problem: addGrade got null student or assignment");
            return false;
        }

        // Use a try block because creating the Grade object might fail if score is invalid.
        try {
            // Next, make the new Grade object. The Grade constructor checks if score is negative.
            Grade gradeObject = new Grade(scoreEarned, feedbackText);

            // Next, tell the Student object to store this Grade, linked to the Assignment.
            // Assumes Student has addGrade(Assignment, Grade) from Person B.
            theStudent.addGrade(theAssignment, gradeObject);

            // Next, tell the Assignment object to store this Grade too, linked to the student username.
            // Assumes Assignment has addGrade(String, Grade) from Person B/A.
            theAssignment.addGrade(theStudent.getUsername(), gradeObject);

            // Next, make sure the Assignment itself is marked as graded.
            // Calling markGraded() multiple times is okay.
            theAssignment.markGraded();

            // If we got to this point without errors, it worked.
            System.out.println("Grade added successfully.");
            return true;
        } 
        
        catch (IllegalArgumentException badScoreError) {
            // Catch the error if the Grade constructor didn't like the score e.g., negative.
            System.out.println("TeacherController problem: addGrade got invalid score " + scoreEarned + " error: " + badScoreError.getMessage());
            return false; // Failed because score was bad.
        } 
        
        catch (Exception anyOtherError) {
            // Catch any other unexpected problem during the process.
             System.out.println("TeacherController problem: addGrade unexpected error: " + anyOtherError.getMessage());
             anyOtherError.printStackTrace(); // Print details to help debug.
            return false; // Failed due to unexpected error.
        }
    }

    /**
     * Calculates the class average score for one assignment.
     * It gets all the grades recorded for that assignment from the Assignment object.
     * Then it loops through them, sums the scores, counts how many grades there were,
     * and calculates the average. Handles the case where there are no grades yet.
     *
     * @param theAssignment The Assignment object to calculate average for.
     * @return The average score as a double, or 0.0 if no grades are found.
     */
    public double calculateClassAverage(Assignment theAssignment) {
        System.out.println("TeacherController calculating class average");
        // Check if the assignment exists first.
        boolean assignmentExists = (theAssignment != null);
        if (!assignmentExists) {
            System.out.println("TeacherController problem: calculateClassAverage got null assignment");
            return 0.0;
        }

        // Get the map of all grades username to Grade object from the Assignment. Returns a copy.
        Map<String, Grade> allGradesForAssignment = theAssignment.getAllGrades();

        // Check if the map is empty or null.
        boolean anyGradesExist = (allGradesForAssignment != null && !allGradesForAssignment.isEmpty());
        if (!anyGradesExist) {
            System.out.println("TeacherController info: calculateClassAverage found no grades for assignment " + theAssignment.getName());
            return 0.0; // No grades means average is 0.
        }

        // Variables to hold the sum and count.
        double sumOfAllScores = 0.0;
        int numberOfGradesFound = 0;

        // Loop through just the Grade objects values in the map.
        for (Grade grade : allGradesForAssignment.values()) {
            // Check if the grade object itself is valid.
            boolean gradeIsValid = (grade != null);
            
            if (gradeIsValid) {
                // Add the score to the sum and increment the count.
                sumOfAllScores = sumOfAllScores + grade.getPointsEarned();
                numberOfGradesFound++;
            }
        }

        // Calculate the average. Make sure we have grades to avoid division by zero.
        double classAverageResult = 0.0;
        boolean canCalculate = (numberOfGradesFound > 0);
        if (canCalculate) {
            classAverageResult = sumOfAllScores / numberOfGradesFound;
        }

        // Return the calculated average.
        return classAverageResult;
    }

     /**
      * calculates the class median score on one assignment.
      * Median is the middle score when all scores are sorted.
      * Gets all grades from the assignment, extracts the scores into a list,
      * sorts the list, then finds the middle value or averages the two middle values.
      *
      * @param theAssignment The Assignment object.
      * @return The median score as a double, or 0.0 if no grades found.
      */
     public double calculateClassMedian(Assignment theAssignment) {
        System.out.println("TeacherController calculating class median");
        // Check if assignment exists.
        boolean assignmentExists = (theAssignment != null);
        if (!assignmentExists) {
            System.out.println("TeacherController problem: calculateClassMedian got null assignment");
            return 0.0;
        }

        // Get all the grades for this assignment.
        Map<String, Grade> allGradesForAssignment = theAssignment.getAllGrades(); // Gets copy

        // Check if we got any grades back.
        boolean anyGradesExist = (allGradesForAssignment != null && !allGradesForAssignment.isEmpty());
        if (!anyGradesExist) {
            System.out.println("TeacherController info: calculateClassMedian found no grades for assignment " + theAssignment.getName());
            return 0.0; // No grades, median is 0? Or undefined? Return 0 for now.
        }

        // Create a list to hold just the scores numbers.
        List<Double> scoresList = new ArrayList<>();
        // Loop through the grades and add each score to the list.
        for (Grade grade : allGradesForAssignment.values()) {
             boolean gradeIsValid = (grade != null);
             if (gradeIsValid) {
                 scoresList.add(grade.getPointsEarned());
             }
        }

        // Check if the scores list ended up empty maybe grades were null?
        boolean haveScoresToList = !scoresList.isEmpty();
        if (!haveScoresToList) {
             System.out.println("TeacherController problem: calculateClassMedian ended up with no scores in list");
             return 0.0; // Return 0 if list is empty after loop.
        }

        // Sort the list of scores numerically from lowest to highest.
        Collections.sort(scoresList);

        // Find the median score.
        double medianScoreResult = 0.0;
        int numberOfScores = scoresList.size();
        // Check if the number of scores is even or odd.
        boolean isEvenNumberOfScores = (numberOfScores % 2 == 0);

        if (isEvenNumberOfScores) {
            // If even, find the two middle indices.lists are 0 indexed.
            int middleIndex1 = numberOfScores / 2 - 1;
            int middleIndex2 = numberOfScores / 2;
            
            // Get the two middle scores.
            double middleScore1 = scoresList.get(middleIndex1);
            double middleScore2 = scoresList.get(middleIndex2);
            
            // The median is the average of those two middle scores.
            medianScoreResult = (middleScore1 + middleScore2) / 2.0;
        } 
        
        else {
            // If odd, there's only one middle index.
            int middleIndex = numberOfScores / 2;
            // The median is just the score at that middle index.
            medianScoreResult = scoresList.get(middleIndex);
        }

        // Return the calculated median score.
        return medianScoreResult;
     }

    /**
     * calculates one student's overall course average using the course's set strategy.
     * It gets the GradeCalculator object stored in the Course, and tells that
     * calculator object to do the calculation using its specific strategy points or category.
     * Checks for null inputs and if a calculator is set for the course first.
     *
     * @param theStudent The Student object whose average is needed.
     * @param theCourse  The Course object that defines the grading strategy.
     * @return The student's average double percentage, or 0.0 if inputs null or no calculator set.
     */
    public double calculateStudentAverage(Student theStudent, Course theCourse) {
        System.out.println("TeacherController calculating student average");
        // Check inputs first.
        boolean studentExists = (theStudent != null);
        boolean courseExists = (theCourse != null);
        if (!studentExists || !courseExists) {
            System.out.println("TeacherController problem: calculateStudentAverage got null student or course");
            return 0.0;
        }

        // Ask the Course object which GradeCalculator strategy it's using.
        // Assumes Course has getGradeCalculator() method from Person A.
        GradeCalculator calculator = theCourse.getGradeCalculator();

        // Check if the course actually has a calculator assigned.
        boolean calculatorExists = (calculator != null);
        if (calculatorExists) {
            // If it does, just delegate the work to that calculator object.
            // Pass it the course and student, it knows what to do based on its type.
            double average = calculator.calculateFinalAverage(theCourse, theStudent);
            return average;
        } 
        
        else {
            // If the course doesn't have a calculator set, we can't calculate the average.
            System.out.println("TeacherController problem: calculateStudentAverage - No GradeCalculator set for course " + theCourse.getName());
            return 0.0; // Return 0 as a default.
        }
    }

    /**
     * Gets the student list for a course, sorted by name either first or last.
     * Used by UI to show sorted roster. Creates a copy of the student list,
     * creates a Comparator based on the sort choices, sorts the copy, returns sorted copy.
     *
     * @param theCourse The Course object whose students to sort.
     * @param sortByLastName  true to sort by last name, false to sort by first name.
     * @param sortAscending   true for A-Z order, false for Z-A order.
     * @return A new List of Student objects, sorted as requested. Empty if course null.
     */
    public List<Student> sortStudentsByName(Course theCourse, boolean sortByLastName, boolean sortAscending) {
        System.out.println("TeacherController sorting students by name");
        // Check if course exists.
        boolean courseExists = (theCourse != null);
        if (!courseExists) {
            System.out.println("TeacherController problem: sortStudentsByName got null course");
            return new ArrayList<>(); // Return empty list if no course.
        }

        // Get the list of students from the course. The getter returns a copy.
        List<Student> studentsToSort = theCourse.getEnrolledStudents();

        // Make the Comparator object. This object knows how to compare two students.
        Comparator<Student> nameComparator;
        // Decide which field to compare based on the sortByLastName flag.
        if (sortByLastName) {
            // Create a comparator that uses the getLastName method of Student.
            // String.CASE_INSENSITIVE_ORDER makes it ignore upper/lower case.
            nameComparator = Comparator.comparing(Student::getLastName, String.CASE_INSENSITIVE_ORDER);
        } 
        
        else {
            // Create a comparator that uses the getFirstName method instead.
            nameComparator = Comparator.comparing(Student::getFirstName, String.CASE_INSENSITIVE_ORDER);
        }

        // Check if the sort should be descending Z to A instead of ascending A to Z.
        if (!sortAscending) {
            // If descending, reverse the order of the comparator we just made.
            nameComparator = nameComparator.reversed();
        }

        // Now use the Collections helper to sort the list using our comparator.
        Collections.sort(studentsToSort, nameComparator);

        // Return the sorted list copy.
        return studentsToSort;
    }

    /**
     * gets the student list, sorted by grade on one specific assignment. For UI ranking.
     * Gets student list copy, creates a Comparator that looks up each student's grade
     * for the given assignment, sorts based on score handles students with no grade.
     *
     * @param theAssignment The Assignment object to sort grades by.
     * @param theCourse Needs the course to get the student list.
     * @param sortAscending true for lowest score first, false for highest score first.
     * @return A new sorted List of Student objects. Empty list if inputs null.
     */
    public List<Student> sortStudentsByGrade(Assignment theAssignment, Course theCourse, boolean sortAscending) {
        System.out.println("TeacherController sorting students by grade");
        // Check inputs exist first.
        boolean assignmentExists = (theAssignment != null);
        boolean courseExists = (theCourse != null);
        if (!assignmentExists || !courseExists) {
             System.out.println("TeacherController problem: sortStudentsByGrade got null assignment or course");
             return new ArrayList<>(); // Return empty list if inputs bad.
        }

        // Get the list of students from the course getter gives copy.
        List<Student> studentsToSort = theCourse.getEnrolledStudents();

        // Create the Comparator. more complex.
        // It compares two students s1 and s2 based on their grade for theAssignment.
        Comparator<Student> gradeComparator = Comparator.comparingDouble(student -> {
            
        	//this inner part runs for each student being compared.
            // It gets the student's grade object for the specific assignment we care about.
            Grade grade = theAssignment.getGrade(student.getUsername());
            
            // Check if a grade object was actually found.
            if (grade != null) {
                //if yes, return the points earned as the value to compare.
                return grade.getPointsEarned();
            } 
            
            else {
                // If the student has no grade for this assignment, we need to return something.
                // Returning negative infinity makes them sort to the very beginning if ascending,
                // or the very end if descending. This handles missing grades 
                return Double.NEGATIVE_INFINITY;
            }
        });

        //check if we need to sort highest score first instead of lowest first.
        if (!sortAscending) {
            //reverse the comparison order if needed.
            gradeComparator = gradeComparator.reversed();
        }

        // Sort the student list using this grade comparator.
        Collections.sort(studentsToSort, gradeComparator);

        // Return the sorted list.
        return studentsToSort;
    }

     /**
      * Assigns the final letter grade like "A", "B" etc for a student in a course.
      * It first checks if the letter grade provided is valid using the GradeScale enum.
      * Then it tells the Student object to store this final grade for the course.
      * Uses Student.setFinalGradeForCourse method from Person B.
      *
      * @param theStudent The Student object getting the final grade.
      * @param theCourse  The Course object the grade is for.
      * @param finalLetterGrade The letter grade String like "A", "B-", "C".
      * @return true if stored ok, false if error, null inputs, or invalid letter grade.
      */
     public boolean assignFinalGrade(Student theStudent, Course theCourse, String finalLetterGrade) {
        System.out.println("TeacherController assigning final grade");
        // Check all inputs are provided.
        boolean studentExists = (theStudent != null);
        boolean courseExists = (theCourse != null);
        // Make sure letter grade string is not null or just empty spaces.
        boolean gradeStringExists = (finalLetterGrade != null && !finalLetterGrade.trim().isEmpty());
        
        if (!studentExists || !courseExists || !gradeStringExists) {
            System.out.println("TeacherController problem: assignFinalGrade got null inputs");
            return false;
        }

        // Check if the input string like "B+" or "c" is a valid letter grade we defined in GradeScale.
        String trimmedGrade = finalLetterGrade.trim();
        boolean isValidLetter = false;
        // Loop through all values in the GradeScale enum A, B, C, D, E.
        for (GradeScale gs : GradeScale.values()) {
            // Compare ignoring case.
            if (gs.getLetter().equalsIgnoreCase(trimmedGrade)) {
                isValidLetter = true; // Found a match.
                break; // Stop looping
            }
        }

        //if the input string wasn't A, B, C, D, or E ignore case then it's invalid.
        if (!isValidLetter) {
            System.out.println("TeacherController problem: assignFinalGrade got invalid letter grade " + finalLetterGrade);
            return false; // Indicate failure due to bad format.
        }

        // Tell the Student object to store this final grade, associated with this Course.
        // Assumes Person B implemented setFinalGradeForCourse(Course, String) in Student.
        theStudent.setFinalGradeForCourse(theCourse, trimmedGrade.toUpperCase()); 

        // Assuming the setter worked.
        return true;
     }

    /**
     * Finds assignments in a course not yet marked as graded. Helps teacher track work.
     * Gets all assignments, then filters the list keeping only the ones where isGraded is false.
     * Uses a Java Stream for filtering which is a bit more modern way.
     * @param theCourse The Course object.
     * @return A List of Assignment objects that are not graded. Empty list if none or error.
     */
    public List<Assignment> viewUngradedAssignments(Course theCourse) {
        System.out.println("TeacherController getting ungraded assignments");
        // Check course exists.
        boolean courseExists = (theCourse != null);
        
        if (!courseExists) {
            System.out.println("TeacherController problem: viewUngradedAssignments got null course");
            return new ArrayList<>(); //return empty list.
        }

        // Get all assignments for the course returns a copy.
        List<Assignment> allAssignments = theCourse.getAllAssignments();
        // Check if list is null just in case.
        boolean assignmentListExists = (allAssignments != null);
        
        if (!assignmentListExists) {
             return new ArrayList<>(); // Return empty list if no assignments.
        }

        // Use a stream to process the list.
        List<Assignment> ungradedList = allAssignments.stream()
            // Keep only assignments that are not null AND return false for isGraded().
            .filter(assignment -> assignment != null && !assignment.isGraded())
            // Collect the filtered assignments back into a new list.
            .collect(Collectors.toList()); // Need import java.util.stream.Collectors

        // Return the resulting list containing only ungraded assignments.
        return ungradedList;
    }

    /**
     * Imports students from a file like CSV and adds them to a course.
     * Reads file line by line, parses data assumes CSV: username,firstName,lastName.
     * Finds user via UserController. If user exists and is a Student, enrolls them in the course.
     * It does NOT create new users if they are not found.
     * @param filePathOnComputer the path to the student file String.
     * @param theCourse    The Course object.
     * @return true if import finished ok and at least one existing student processed/enrolled, false otherwise.
     */
    public boolean importStudentsFromFile(String filePathOnComputer, Course theCourse) {
        System.out.println("TeacherController importing students from file: " + filePathOnComputer);
        // Check we have the things needed to work.
        boolean haveUserController = (this.userController != null);
        boolean courseExists = (theCourse != null);
        boolean pathExists = (filePathOnComputer != null && !filePathOnComputer.isEmpty());

        if (!haveUserController || !courseExists || !pathExists) {
             System.out.println("TeacherController problem: importStudentsFromFile missing controller, course, or path");
            return false;
        }

        // Flag to track if we successfully processed at least one student.
        boolean atLeastOneProcessed = false;
        // Use try-with-resources so the file reader closes automatically.
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePathOnComputer))) {
            String currentLine;
            int lineNumber = 0;
            // Read the file one line at a time until the end.
            while ((currentLine = fileReader.readLine()) != null) {
                lineNumber++;
                // Clean up the line, remove leading/trailing spaces.
                String trimmedLine = currentLine.trim();
                // Skip lines that are empty or maybe the first line header.
                if (trimmedLine.isEmpty() || lineNumber == 1) {
                    System.out.println("Import skipping line " + lineNumber + " (header or empty)");
                    continue; // Go to the next line.
                }

                // Assume CSV format: username,firstName,lastName ... (ignore extra columns for now)
                String[] studentData = trimmedLine.split(",");
                // Need at least the username maybe? Let's assume just username needed to find existing.
                boolean hasEnoughData = (studentData.length >= 1);

                if (hasEnoughData) {
                    // Get the username from the first column.
                    String usernameFromFile = studentData[0].trim();
                    if (usernameFromFile.isEmpty()) {
                         System.out.println("Import warning: Skipping line " + lineNumber + ", username is empty");
                         continue;
                    }

                    // Try to find if a user with this username already exists using UserController.
                    User userFound = userController.findUserByUsername(usernameFromFile);
                    Student studentToEnroll = null;

                    // Check if user was found.
                    boolean userExists = (userFound != null);
                    if (userExists) {
                        // User exists, now check if they are actually a Student object.
                        if (userFound instanceof Student) {
                            // If yes, cast it to Student so we can enroll them.
                            studentToEnroll = (Student) userFound;
                        } 
                        
                        else {
                            // If user exists but isn't a student teacher?, print warning.
                            System.out.println("Import warning: User '" + usernameFromFile + "' on line " + lineNumber + " exists but is not a Student.");
                        }
                    } 
                    
                    else {
                         // If user not found, print warning. We are not creating new users here.
                         System.out.println("Import warning: User '" + usernameFromFile + "' on line " + lineNumber + " not found in system. Skipping enrollment.");
                    }

                    // If we successfully found an existing Student object
                    boolean haveStudentToEnroll = (studentToEnroll != null);
                    if (haveStudentToEnroll) {
                        // try to enroll them in the course using the method we already have.
                        boolean enrolledOk = addStudentToCourse(studentToEnroll, theCourse);
                        
                        if (enrolledOk) {
                             // It worked maybe new enrollment.
                             System.out.println("Import success: Enrolled student '" + usernameFromFile + "' in course '" + theCourse.getName() + "'");
                             atLeastOneProcessed = true; // Mark that we did something useful.
                        } 
                        
                        else {
                             // addStudentToCourse returning false likely means already enrolled or other issue.
                             System.out.println("Import info: Student '" + usernameFromFile + "' probably already enrolled or failed to add.");
                             atLeastOneProcessed = true; //still count as processed.
                        }
                    }
                } 
                
                else {
                     //if the line didn't even have one comma separated value for username.
                     System.out.println("Import warning: Skipping line " + lineNumber + ", not enough data (need at least username)");
                }
            } // end while loop reading lines

        } 
        
        catch (IOException fileError) {
            //catch errors related to opening or reading the file itself.
            System.out.println("TeacherController problem: importStudentsFromFile - ERROR reading file '" + filePathOnComputer + "': " + fileError.getMessage());
            return false; // Failed due to file issue.
        } 
        
        catch (Exception generalError) {
            //catch any other unexpected problems during the import process.
            System.out.println("TeacherController problem: importStudentsFromFile - ERROR processing file: " + generalError.getMessage());
            generalError.printStackTrace(); // Print full error details for debugging.
            return false; // Failed due to unexpected error.
        }

        // Return true only if we successfully processed at least one student line.
        return atLeastOneProcessed;
    }

    //COURSE SETTING UP METHODS.

    /**
     * Sets the GradeCalculator strategy for a course like Points or Category.
     * Calls the setter method on the Course object. 
     * @param theCourse   The Course object to set the mode for.
     * @param calculatorToUse The GradeCalculator object like new PointsBasedCalculator() to use.
     * @return true if set ok, false if inputs were null.
     */
    public boolean setCourseGradingMode(Course theCourse, GradeCalculator calculatorToUse) {
         System.out.println("TeacherController setting course grading mode");
         // Check inputs first.
         boolean courseExists = (theCourse != null);
         boolean calculatorExists = (calculatorToUse != null);
         
         if (!courseExists || !calculatorExists) {
             System.out.println("TeacherController problem: setCourseGradingMode got null inputs");
             return false;
         }
         // Tell the Course object which calculator strategy to use.
         // Assumes Course has the setGradeCalculator method from Person A.
         theCourse.setGradeCalculator(calculatorToUse);
         // Assume it worked.
         return true;
     }

     /**
      * Sets up the grading categories names, weights, drops for a course using category grading.
      * Clears any old categories first, then adds all categories from the provided list.
      * Uses Course.addGradingCategory() and assumes Course.clearGradingCategories() exists.
      * @param theCourse The Course object to configure.
      * @param categoryDetails List of new GradingCategory objects to add.
      * @return true if setup ok, false if inputs were null.
      */
     public boolean setupAssignmentCategories(Course theCourse, List<GradingCategory> categoryDetails ) {
         System.out.println("TeacherController setting up assignment categories");
         // Check inputs first.
         boolean courseExists = (theCourse != null);
         boolean detailsExist = (categoryDetails != null); // Checks if the list itself is null
         if (!courseExists || !detailsExist) {
             System.out.println("TeacherController problem: setupAssignmentCategories got null inputs");
             return false;
         }

         // First, remove any categories that might have been set up before.
         theCourse.clearGradingCategories(); // Assumes Person A added this method to Course.

         // Now loop through the new list of categories provided.
         for (GradingCategory category : categoryDetails) {
             // Make sure the category object in the list isn't null.
             boolean categoryIsValid = (category != null);
             if (categoryIsValid) {
                 // Tell the Course object to add this category.
                 // Assumes Person A added addGradingCategory method to Course.
                 theCourse.addGradingCategory(category);
             } 
             
             else {
                 System.out.println("TeacherController warning: setupAssignmentCategories found null category in list");
             }
         }
         //assume it worked if loop finished.
         return true;
     }
}