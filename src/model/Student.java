package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import model.grading.GradeCalculator;
import model.grading.PointsBasedCalculator; // Might need default

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * This Student class represents a student user in our gradebook.
 * It extends the main User class to get the basic name, login stuff.
 * Then it adds student specific things like the student ID, lists for
 * current and completed courses, a map to hold all their individual grades
 * for assignments, and another map to store their final letter grade for
 * completed courses. It also handles calculating their GPA based on those final grades.
 */
public class Student extends User {
    private final String studentId;
    // This list holds the Course objects the student is currently taking this semester.
    // Final just means the list object itself cant be replaced, we can still add/remove courses.
    private final List<Course> currentCourses;

    // This list holds Course objects the student has finished in the past.
    private final List<Course> completedCourses;

    // This map stores the actual grades the student got on assignments.
    // The key is the Assignment object itself, and the value is the Grade object points and feedback.
    // This lets us quickly find the grade for any specific assignment.
    private final Map<Assignment, Grade> grades;

    // This map stores the final letter grade A, B, C etc the student received for courses they completed.
    // The key is the Course object, the value is the letter grade String.
    private final Map<Course, String> finalGrades;

    // This is for the Observer pattern using standard Java stuff.
    // Lets the Student object notify Views when something changes like a grade added.
    // transient means gson wont try to save this special object to the JSON file.
    private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    //Constructor
    /**
     * Constructor makes a new Student object.
     * First it calls the User constructor using super to set up the
     * name, username, email, hashed password, and sets the role to STUDENT.
     * Then it sets the student's ID and makes empty lists and maps
     * to hold their courses and grades later.
     */
    public Student(String firstName, String lastName, String email, String password, String username, String studentId) {
        // Call User constructor, pass Role.STUDENT this time.
        super(firstName, lastName, email, password, username, Role.STUDENT);

        // Store the student specific ID.
        this.studentId = studentId;

        //make the empty lists and maps ready to use.
        this.currentCourses = new ArrayList<>();
        this.completedCourses = new ArrayList<>();
        this.grades = new HashMap<>();
        this.finalGrades = new HashMap<>();
    }

    //getters
    /** Gets the student's ID string. */
    public String getStudentId() {
        return studentId;
    }

    /** Gets a copy of the list of courses the student is currently taking.
     *  EXAMPLE OF ENCAPSULATION Returning a copy is important so outside code cant
     *  mess up the student's actual internal list. Avoids escaping references.
     */
    public List<Course> getCurrentCourses() {
        //make a new ArrayList and give it all the courses from the internal list.
        return new ArrayList<Course>(currentCourses);
    }

    /** Gets a copy of the list of courses the student finished.
     *  ENCAPSULATION again, returns a copy.
     */
    public List<Course> getCompletedCourses() {
        // Make a new list copy.
        return new ArrayList<Course>(completedCourses);
    }

    /** Gets a copy of the map holding the student's grades.
     *  Key is Assignment object, value is Grade object.
     *  ENCAPSULATION, returns a copy.
     */
    public Map<Assignment, Grade> getGrades() {
        // Make a new map copy.
        return new HashMap<Assignment, Grade>(grades);
    }


    //Course management
    /**
     * enrollInCourse adds a course to this student's list of current courses.
     * Checks first if the course is valid and if the student isn't already
     * enrolled in it to avoid duplicates in the list.
     * Important part is it fires a property change event using pcs
     * to tell any listeners like the UI that the student's course list changed.
     * @param course The Course object to add.
     */
    public void enrollInCourse(Course course) {
        //check if course is not null and if its not already in the current list.
        if (course != null && !currentCourses.contains(course)) {

        	//add it to the student's list of current courses.
            currentCourses.add(course);

            //send out a notification signal "courseEnrolled" using the pcs helper.
            // Views listening for this property name can then update themselves.
            pcs.firePropertyChange("courseEnrolled", null, course);
        }
    }

    /**
     * completeCourse moves a course from the student's current list to their completed list.
     * This happens when the semester ends or teacher finalizes grades.
     * It calculates the student's average in the course using calculateClassAverage,
     * converts that percentage to a letter grade using getLetterGrade,
     * and stores that final letter grade in the finalGrades map for GPA calculation later.
     * Fires an event to notify listeners.
     * @param course The Course object to mark as completed.
     */
    public void completeCourse(Course course) {
        //check if valid course and if it's actually in the current courses list.
        if (course != null && currentCourses.contains(course)) {
            //remove it from the current list.
            currentCourses.remove(course);

            //add it to the completed list.
            completedCourses.add(course);

            // now figure out the final grade for this course.
            // First calculate the percentage average the student had in this course.
            double averagePercentage = calculateClassAverage(course); // Uses the course's calculator

            // Check if the average is a valid number could be -1 or NaN depending on calculator maybe? >=0 is safe check.
            if (averagePercentage >= 0) {
                // Convert the percentage like 85.0 to a letter grade like B.
                String letterGrade = getLetterGrade(averagePercentage); //uses GradeScale enum

                // Store this letter grade in the map, associated with this course.
                finalGrades.put(course, letterGrade);
            } else {
                //if the average couldn't be calculated maybe no grades yet,
                // store an empty string or maybe "N/A"? Empty for now.
                finalGrades.put(course, "");
            }

            //send notification that a course was completed. Event name "courseCompleted".
            pcs.firePropertyChange("courseCompleted", null, course);
        }
    }
    /////////
    //grade management

    /**
     * addGrade stores a grade the student got for an assignment.
     * Puts the Grade object into the student's internal grades map,
     * using the Assignment object as the key. Overwrites old grade if present.
     * Fires event to notify view grade was added/updated.
     * @param assignment The Assignment object the grade is for key.
     * @param grade The Grade object score/feedback value.
     */
    public void addGrade(Assignment assignment, Grade grade) {
        // Check inputs arent null.
        if (assignment != null && grade != null) {

        	//put the assignment grade pair into the map. Overwrites if key already exists.
            grades.put(assignment, grade);

            //send notification that grades changed. Event name "gradeAdded".
            // Send the assignment as context maybe?
            pcs.firePropertyChange("gradeAdded", null, assignment);
        }
    }

    /**
     * removeGradeForAssignment removes the grade entry for a specific assignment
     * from the student's internal grades map.
     * This is needed when an assignment gets deleted from the course entirely.
     * @param assignment The Assignment object whose grade should be removed key.
     */
    public void removeGradeForAssignment(Assignment assignment) {
        // Check input is not null.
        if (assignment != null) {

        	// Use the map's remove method. It returns the Grade object that was removed,
            // or null if the assignment wasn't found as a key.
            Grade removedGrade = grades.remove(assignment);

            //check if something was actually removed.
            if (removedGrade != null) {

            	//if yes, notify listeners that a grade was removed. Event name "gradeRemoved".
                // Send assignment as context.
                 pcs.firePropertyChange("gradeRemoved", assignment, null);
            }
        }
    }


    /**
     * getGradeForAssignment gets the Grade object for one specific assignment.
     * Looks it up in the student's internal grades map using the Assignment as the key.
     * @param assignment The Assignment object key we want the grade for.
     * @return The Grade object score/feedback, or null if no grade found for that assignment.
     */
    public Grade getGradeForAssignment(Assignment assignment) {
        //get from the map using the Assignment object itself as the key.
        // Returns null if key not found.
        return grades.get(assignment);
    }

    //calculations
    /**
     * calculateGPA calculates the student's overall Grade Point Average.
     * It only looks at courses in the completedCourses list.
     * For each completed course, it gets the final letter grade stored in the finalGrades map.
     * It converts that letter grade A, B etc into a GPA point value 4.0, 3.0 etc using GradeScale.
     * It sums up these GPA points and divides by the number of completed courses that had a grade.
     * @return The calculated GPA as a double like 3.5, or 0.0 if no completed courses have grades.
     */
    public double calculateGPA() {
        //check if the list of completed courses is empty.
        if (completedCourses == null || completedCourses.isEmpty()) {
            //no completed courses means GPA is 0.
            return 0.0;
        }

        double totalGpaPoints = 0.0;
        int numberOfCoursesCounted = 0; //how many courses had a final grade to count

        //loop through each course in the completed courses list.
        for (Course course : completedCourses) {

        	//get the final letter grade stored for this course from the map.
            String letterGrade = finalGrades.get(course);

            //check that a grade was actually stored and isn't empty.
            if (letterGrade != null && !letterGrade.isEmpty()) {

            	//if yes, use the GradeScale enum to convert letter for example "B" to GPA value 3.0.
                double gpaValue = GradeScale.fromLetter(letterGrade).getGpaValue();

                //add this value to the total sum of GPA points.
                totalGpaPoints = totalGpaPoints + gpaValue;

                //increment the counter for courses included in the GPA.
                numberOfCoursesCounted++;
            }
        }

        //calculate the final GPA average. avoid division by zero.
        boolean canCalculate = (numberOfCoursesCounted > 0);

        if (canCalculate) {
            // Divide total points by number of courses counted.
            return totalGpaPoints / numberOfCoursesCounted;
        } else {
            //if no completed courses had final grades, overall GPA is 0.
            return 0.0;
        }
    }

    /**
     * calculateClassAverage calculates the student's current average in a specific course.
     * IMPORTANT This method doesn't do the calculation itself.
     * It asks the Course object for its currently set GradeCalculator strategy points or category.
     * Then it tells that calculator object to calculate the average for this student in that course.
     * This follows the Strategy pattern.
     * @param theCourse The Course object to calculate average for.
     * @return The average percentage double, or 0.0 if course null, not enrolled, or no calculator set.
     */
    public double calculateClassAverage(Course theCourse) {
        // Check course is valid and student is currently enrolled.
        boolean courseExists = (theCourse != null);
        // Check BOTH current and completed maybe? No, requirement says current average. Just check current.
        boolean isCurrent = currentCourses.contains(theCourse);

        if (!courseExists || !isCurrent) {
             System.out.println("Student problem: calculateClassAverage got null or not current course");
            return 0.0;
        }

        //ask the Course object for its calculator strategy.
        GradeCalculator calculator = theCourse.getGradeCalculator();

        //check if the course has one set.
        boolean calculatorExists = (calculator != null);
        if (calculatorExists) {
            //if yes, give the work to the calculator object.
            return calculator.calculateFinalAverage(theCourse, this);
        } else {
            //if no calculator, can't calculate.
             System.out.println("Student problem: calculateClassAverage course " + theCourse.getName() + " has no calculator");
            return 0.0;
        }
    }

    //final grade management
    /**
     * setFinalGradeForCourse stores the final letter grade the teacher assigned for a course.
     * Checks if the grade letter is valid using GradeScale first.
     * Then puts the valid grade letter into the finalGrades map for this student.
     * Fires observer event.
     * @param theCourse The course the grade is for.
     * @param letterGrade The grade string like "A", "B-", "C" etc.
     */
    public void setFinalGradeForCourse(Course theCourse, String letterGrade) {
        // Check inputs are valid.
        boolean courseExists = (theCourse != null);
        // Check student is actually in the course maybe completed it? Or still current?
        // Let's assume teacher assigns it while current, before completeCourse is called maybe.
        // Or maybe it can be assigned after completion too. Check if student associated with course.
        boolean isInCourse = currentCourses.contains(theCourse) || completedCourses.contains(theCourse);
        boolean gradeStringExists = (letterGrade != null && !letterGrade.trim().isEmpty());

        if (courseExists && isInCourse && gradeStringExists) {
            // Validate the letter grade format using GradeScale helper.
            String trimmedGrade = letterGrade.trim();
            boolean isValidLetter = false;
            for (GradeScale gs : GradeScale.values()) {
                if (gs.getLetter().equalsIgnoreCase(trimmedGrade)) {
                     isValidLetter = true;
                     trimmedGrade = gs.getLetter(); // Use the official case like "A" not "a"
                     break;
                }
            }

            if (isValidLetter) {
                 //store the validated grade letter in the map.
                 finalGrades.put(theCourse, trimmedGrade);
                 //notify listeners. Pass course and grade info.
                 pcs.firePropertyChange("finalGradeSet", null, new Object[]{theCourse, trimmedGrade});
            } else {
                 // Throw error if letter grade itself isn't valid like "X".
                 throw new IllegalArgumentException("Invalid final letter grade provided: " + letterGrade);
            }
        } else {
             // Print message if inputs bad.
             System.out.println("Student problem: setFinalGradeForCourse got null course/grade or student not associated with course");
             // Maybe throw exception here too? For now just print.
        }
    }

    /**
     * getFinalGradeForCourse gets the stored final letter grade for a completed or current course.
     * Looks it up in the student's finalGrades map.
     * @param theCourse The Course object key.
     * @return The final letter grade String "A", "B" etc, or null if none stored.
     */
    public String getFinalGradeForCourse(Course theCourse) {
        //just get the value from the map using the Course object as the key.
        return finalGrades.get(theCourse);
    }

    //utility methods
    /**
     * convertToGPA converts a percentage score like 85.0 into a GPA value like 3.0.
     * It just asks the GradeScale enum helper class to do the conversion using fromPercentage.
     * @param percentage The percentage score double.
     * @return The corresponding GPA value double.
     */
    public double convertToGPA(double percentage) {
        //use the static method from GradeScale.
        return GradeScale.fromPercentage(percentage).getGpaValue();
    }

    /**
     * getLetterGrade converts a percentage score like 72.0 into a letter grade like "C".
     * It just asks the GradeScale enum helper class to do the conversion using fromPercentage.
     * @param percentage The percentage score double.
     * @return The corresponding letter grade String.
     */
    public String getLetterGrade(double percentage) {
        //use the static method from GradeScale.
        return GradeScale.fromPercentage(percentage).getLetter();
    }

    /**
     * getFeedbackByCourse gets all the feedback comments the student received
     * for assignments within a specific course.
     * It loops through all assignments belonging to the given course,
     * gets the student's grade object for that assignment using getGradeForAssignment,
     * and if feedback exists puts it in a map result where key is assignment name, value is feedback string.
     * @param course The Course object to get feedback for.
     * @return A new Map<String, String> assignment name to feedback comment. Empty if course null.
     */
    public Map<String, String> getFeedbackByCourse(Course course) {
        //check input.
        if (course == null) {
            System.out.println("Student problem: getFeedbackByCourse got null course");
        	return new HashMap<>(); // Return empty map.
        }

        //make a map to store the results.
        Map<String, String> feedbackMap = new HashMap<String, String>();
        // Get all assignments that are part of this course using course getter.
        List<Assignment> assignments = course.getAllAssignments(); // Gets copy

        //make sure assignment list exists.
        if (assignments != null) {

        	//loop through each assignment in the course.
            for (Assignment a : assignments) {

            	//get this student's grade object for this assignment using student's own method.
                Grade g = this.getGradeForAssignment(a);

                //check if grade exists and if feedback string exists and isn't empty.
                if (g != null && g.getFeedback() != null && !g.getFeedback().trim().isEmpty()) {
                    //if yes, add entry to map assignment name -> feedback text.
                    feedbackMap.put(a.getName(), g.getFeedback());
                }
            }
        }

        //return the map containing all feedback found.
        return feedbackMap;
    }

    //observer pattern support
    /**
     * addPropertyChangeListener allows views or other objects to listen for changes
     * in this Student object like when a grade is added or course completed.
     * @param listener The object that wants to listen could be a View.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    	//pass the listener to the helper object pcs.
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * removePropertyChangeListener allows listeners to stop listening.
     * @param listener The listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    	// tell the helper object pcs to remove the listener.
        pcs.removePropertyChangeListener(listener);
    }
}