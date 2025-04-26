package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects; // Needed for equals/hashCode maybe
//import java.util.Iterator; // Not needed currently

import model.grading.GradeCalculator;
// import model.grading.PointsBasedCalculator; // Don't need default here

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
    // This list holds the Course objects the student is currently taking.
    private final List<Course> currentCourses;

    // This list holds Course objects the student has finished.
    private final List<Course> completedCourses;

    // This map stores the actual grades the student got on assignments.
    // Key is Assignment object, Value is Grade object.
    private final Map<Assignment, Grade> grades;

    // This map stores the final letter grade A, B, C etc for completed courses.
    // Key is Course object, Value is letter grade String.
    private final Map<Course, String> finalGrades;

    // This is for the Observer pattern using standard Java stuff PropertyChangeSupport.
    // transient means gson wont try to save this special object to the JSON file.
    private transient PropertyChangeSupport pcs; // Initialize in init method

    //Constructor
    /**
     * Constructor makes a new Student object.
     * Calls User constructor with Role.STUDENT.
     * Sets student ID, makes empty lists and maps for courses/grades.
     * Needs initTransientFields called after loading from JSON.
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
        // Initialize observer helper here too maybe? No, do it in init method.
        // this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * initTransientFields needs to be called after a Student object is loaded from JSON.
     * Because the PropertyChangeSupport helper 'pcs' is marked transient,
     * it doesn't get saved/loaded by Gson. So we need to create it manually after loading.
     */
     public void initTransientFields() {
         // Check if pcs is null meaning it wasn't created or loaded
         if (this.pcs == null) {
             // Create a new helper object linked to this Student instance.
             this.pcs = new PropertyChangeSupport(this);
             System.out.println("Initialized transient PropertyChangeSupport for student: " + getUsername());
         }
     }


    //getters
    /** Gets the student's ID string. */
    public String getStudentId() {
        return studentId;
    }

    /** Gets a copy of the list of courses the student is currently taking.
     *  ENCAPSULATION Returning a copy prevents messing up internal list.
     */
    public List<Course> getCurrentCourses() {
        //make a new ArrayList copy.
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
     * Checks first if the course is valid and if student isn't already enrolled.
     * Fires property change event "courseEnrolled" using pcs if successful.
     * @param course The Course object to add.
     */
    public void enrollInCourse(Course course) {
        //check if course not null and not already in the current list.
        if (course != null && !currentCourses.contains(course)) {
        	//add it to the student's list of current courses.
            currentCourses.add(course);
            //send out notification signal "courseEnrolled".
            // Make sure pcs is initialized first! Call initTransientFields after loading.
            if (pcs != null) {
                 pcs.firePropertyChange("courseEnrolled", null, course);
            } else {
                 System.out.println("Student problem: enrollInCourse - pcs is null for " + getUsername());
            }
        }
    }

    /**
     * completeCourse moves a course from current list to completed list.
     * Calculates the final percentage average for the course using its calculator.
     * Converts percentage to letter grade using GradeScale. Stores letter grade.
     * Fires "courseCompleted" event.
     * @param course The Course object to mark as completed.
     */
    public void completeCourse(Course course) {
        //check if valid course and if it's actually in the current courses list.
        if (course != null && currentCourses.contains(course)) {
            //remove it from the current list.
            currentCourses.remove(course);
            //add it to the completed list.
            completedCourses.add(course);

            // calculate final grade average percentage for this course.
            double averagePercentage = calculateClassAverage(course);

            // Convert percentage to letter grade. Use GradeScale. Handles negative average by returning E maybe.
            String letterGrade = getLetterGrade(averagePercentage);

            // Store this letter grade in the finalGrades map.
            finalGrades.put(course, letterGrade);

            //send notification that a course was completed. Event name "courseCompleted".
            if (pcs != null) {
                pcs.firePropertyChange("courseCompleted", null, course);
            } else {
                 System.out.println("Student problem: completeCourse - pcs is null for " + getUsername());
            }
        }
    }
    /////////
    //grade management

    /**
     * addGrade stores a grade the student got for an assignment.
     * Puts the Grade object into the student's internal grades map,
     * using the Assignment object as the key. Overwrites old grade if present.
     * Fires event "gradeAdded" to notify view grade was added/updated.
     * @param assignment The Assignment object the grade is for key.
     * @param grade The Grade object score/feedback value.
     */
    public void addGrade(Assignment assignment, Grade grade) {
        // Check inputs arent null.
        if (assignment != null && grade != null) {
        	//put the assignment grade pair into the map. Overwrites if key already exists.
            Grade oldGrade = grades.put(assignment, grade); // put returns previous value or null

            //send notification that grades changed. Event name "gradeAdded".
            // Send assignment as context maybe? Old grade could be useful too.
            if (pcs != null) {
                pcs.firePropertyChange("gradeAdded", oldGrade, grade); // Send old/new grade maybe? Or just assignment context? Send assignment for now.
                // pcs.firePropertyChange("gradeAdded", null, assignment); // Alternative
            } else {
                 System.out.println("Student problem: addGrade - pcs is null for " + getUsername());
            }
        } else {
            System.out.println("Student problem: addGrade got null assignment or grade");
        }
    }

    /**
     * removeGradeForAssignment removes the grade entry for a specific assignment
     * from the student's internal grades map.
     * Needed when an assignment gets deleted from the course entirely by the Course.removeAssignment method.
     * Fires event "gradeRemoved".
     * @param assignment The Assignment object whose grade should be removed key.
     */
    public void removeGradeForAssignment(Assignment assignment) {
        // Check input is not null.
        if (assignment != null) {
        	// Use the map's remove method. Returns removed Grade object or null.
            Grade removedGrade = grades.remove(assignment);
            //check if something was actually removed.
            if (removedGrade != null) {
            	//if yes, notify listeners that a grade was removed. Event name "gradeRemoved".
                // Send assignment as context.
                if (pcs != null) {
                    pcs.firePropertyChange("gradeRemoved", assignment, null); // Send assignment that was removed
                } else {
                    System.out.println("Student problem: removeGradeForAssignment - pcs is null for " + getUsername());
                }
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
        // Check input key
        if (assignment == null) {
            return null;
        }
        //get from the map using the Assignment object itself as the key.
        // Returns null if key not found.
        return grades.get(assignment);
    }

    //calculations
    /**
     * calculateGPA calculates the student's overall Grade Point Average.
     * It only looks at courses in the completedCourses list.
     * For each completed course, gets final letter grade from the finalGrades map.
     * Converts letter grade like "B" into GPA value like 3.0 using GradeScale.
     * Sums GPA points and divides by number of completed courses that had a grade.
     * @return The calculated GPA as a double like 3.5, or 0.0 if no completed courses have grades.
     */
    public double calculateGPA() {
        //check if the list of completed courses is empty or null.
        if (completedCourses == null || completedCourses.isEmpty()) {
            return 0.0; //no completed courses means GPA is 0.
        }

        double totalGpaPoints = 0.0;
        int numberOfCoursesCounted = 0; //how many courses had a final grade to count

        //loop through each course in the completed courses list.
        for (Course course : completedCourses) {
        	//get the final letter grade stored for this course from the map.
            String letterGrade = finalGrades.get(course);
            //check that a grade was actually stored and isn't empty/null.
            if (letterGrade != null && !letterGrade.isEmpty()) {
            	//if yes, use the GradeScale enum to convert letter to GPA value.
                double gpaValue = GradeScale.fromLetter(letterGrade).getGpaValue();
                //add this value to the total sum of GPA points.
                totalGpaPoints = totalGpaPoints + gpaValue;
                //increment the counter for courses included in the GPA.
                numberOfCoursesCounted++;
            }
            // If no final letter grade stored, skip this course for GPA calc.
        }

        //calculate the final GPA average. check numberOfCoursesCounted > 0.
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
     * IMPORTANT This method doesn't do the calculation itself. It delegates.
     * It asks the Course object for its currently set GradeCalculator strategy.
     * Then it tells that calculator object to calculate the average for this student in that course.
     * This follows the Strategy pattern.
     * @param theCourse The Course object to calculate average for.
     * @return The average percentage double, or 0.0 if course null, student not current, or no calculator set.
     */
    public double calculateClassAverage(Course theCourse) {
        // Check course is valid and student is currently enrolled in it.
        boolean courseExists = (theCourse != null);
        boolean isCurrent = (courseExists && currentCourses.contains(theCourse)); // Check contains *after* null check

        if (!courseExists || !isCurrent) {
             System.out.println("Student problem: calculateClassAverage got null or student not current in course " + (theCourse != null ? theCourse.getName(): "null"));
            return 0.0; // Return 0 if invalid input or not enrolled
        }

        //ask the Course object for its calculator strategy.
        GradeCalculator calculator = theCourse.getGradeCalculator();

        //check if the course has one set.
        boolean calculatorExists = (calculator != null);
        if (calculatorExists) {
            //if yes, give the work to the calculator object.
            return calculator.calculateFinalAverage(theCourse, this);
        } else {
            //if no calculator set, print error and return 0.
             System.out.println("Student problem: calculateClassAverage course " + theCourse.getName() + " has no calculator strategy set");
            return 0.0;
        }
    }

    //final grade management
    /**
     * setFinalGradeForCourse stores the final letter grade the teacher assigned for a course.
     * Checks if the grade letter is valid using GradeScale first.
     * Then puts the valid grade letter into the finalGrades map for this student.
     * Fires observer event "finalGradeSet".
     * @param theCourse The course the grade is for.
     * @param letterGrade The grade string like "A", "B".
     */
    public void setFinalGradeForCourse(Course theCourse, String letterGrade) {
        // Check inputs are valid.
        boolean courseExists = (theCourse != null);
        // Check student is actually in the course current or completed.
        boolean isInCourse = currentCourses.contains(theCourse) || completedCourses.contains(theCourse);
        boolean gradeStringExists = (letterGrade != null && !letterGrade.trim().isEmpty());

        if (courseExists && isInCourse && gradeStringExists) {
            // Validate the letter grade format using GradeScale helper.
            String trimmedGrade = letterGrade.trim();
            boolean isValidLetter = false;
            String officialLetterGrade = ""; // Store the official letter from enum
            // Loop through enum values A, B, C, D, E.
            for (GradeScale gs : GradeScale.values()) {
                // Compare ignoring case "a" matches "A".
                if (gs.getLetter().equalsIgnoreCase(trimmedGrade)) {
                     isValidLetter = true;
                     officialLetterGrade = gs.getLetter(); // Get the canonical letter like "A"
                     break; // Found match, stop loop.
                }
            }

            if (isValidLetter) {
                 //store the validated, official letter grade in the map.
                 finalGrades.put(theCourse, officialLetterGrade);
                 //notify listeners. Pass course and grade maybe in array.
                 if (pcs != null) {
                     pcs.firePropertyChange("finalGradeSet", null, new Object[]{theCourse, officialLetterGrade});
                 } else {
                      System.out.println("Student problem: setFinalGrade - pcs is null for " + getUsername());
                 }
            } else {
                 // Throw error if letter grade itself isn't valid like "X".
                 // This gives feedback to controller/UI that input was bad.
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
        // Check course key isn't null.
        if (theCourse == null) {
            return null;
        }
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
        // Check if pcs helper object exists needs initTransientFields called first
        if (this.pcs == null) {
             this.initTransientFields(); // Try to initialize it if null
        }
        // Make sure listener is not null before adding
        if (listener != null) {
             pcs.addPropertyChangeListener(listener);
        }
    }

    /**
     * removePropertyChangeListener allows listeners to stop listening.
     * @param listener The listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // Check if pcs helper exists and listener is not null
        if (this.pcs != null && listener != null) {
             pcs.removePropertyChangeListener(listener);
        }
    }

    // --- equals/hashCode based on username/ID ---
    // Needed for checking contains in lists/maps maybe

    /** equals checks if two Student objects represent the same student
     *  Based on the unique studentId field.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same object instance
        // Check null and if it's actually a Student object
        if (o == null || !(o instanceof Student)) return false;
        // Check if the parent User part is equal first maybe? Or just ID? Just ID for now.
        // if (!super.equals(o)) return false; // Optional User check
        Student student = (Student) o;
        // Compare using the studentId field. Objects.equals handles nulls safely.
        return Objects.equals(studentId, student.studentId);
    }

    /** hashCode goes with equals, based on studentId */
    @Override
    public int hashCode() {
        // Use Objects.hash to generate hash from studentId.
        return Objects.hash(studentId);
        // If also using User equals: return Objects.hash(super.hashCode(), studentId);
    }

}