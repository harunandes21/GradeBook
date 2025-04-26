package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a single assignment within a course like Hommework 1 or Midterm.
 * Holds info like name, points, due date, category, graded status.
 * Also holds the grades students received for it and calculates its own average/median.
 */
public class Assignment {
    //made fields not final anymore so they can be edited by setters
    private String name;
    private double pointsWorth;
    private String dueDate;
    private String categoryName;
    private Group group;
    private boolean isGraded;
    private String description;

    //this Map called studentGrades is where the Assignment object keeps track
    // of all the grades students have received for this specific assignment.
    // It uses the student's username, which is a String, as the key
    // to look up the corresponding Grade object, which holds the points and feedback.
    // So if we want student john123's grade for Homework 1, we look inside
    // Homework 1's studentGrades map using the key 'john123'.
    // Using a Map makes it fast to find a specific student's grade later.
    //ENCAPSULATION EXAMPLE
    // it's private so only methods inside Assignment can change it directly
    private Map<String, Grade> studentGrades; // Key=Student Username, Value=Grade Object

    /**
     * constructor for making a new Assignment.
     */
    public Assignment(String name, double pointsWorth, String dueDate, String categoryName, Group group) {
		if (name == null || name.trim().isEmpty()) {
		   throw new IllegalArgumentException("Assignment name cannot be null or empty");
		}
		
        //check so we dont have non positive points
		if (pointsWorth <= 0) {
		   throw new IllegalArgumentException("Assignment points must be positive");
		}
		

		this.name = name.trim();
		this.pointsWorth = pointsWorth;
		this.dueDate = dueDate; 
		this.categoryName = categoryName;
		this.group = group;
		this.isGraded = false; //start it as not graded
		this.description = "";
		this.studentGrades = new HashMap<String, Grade>(); //,map initialized
		
	}
    

    // --- Getters ---

    /** Gets the assignment's name. */
    public String getName() {
        return name;
    }

    /** Gets the max points possible. */
    public double getPointsWorth() {
        return pointsWorth;
    }

    /** Gets the due date string. Returns null if not set. */
    public String getDueDate() {
        // Maybe parse this to LocalDate if needed elsewhere?
        // For now just return the stored string.
        return dueDate;
    }

    /** Gets the category name string */
    public String getCategoryName() {
        return categoryName;
    }

    /** Gets the group */
    public Group getGroup() {
        return group;
    }

    /** checks if the assignment is marked as graded*/
    public boolean isGraded() {
        return isGraded;
    }

    /** Gets the assignment description */
    public String getDescription() {
        return description;
    }

    // --- Setters, needed by controllers ---

    /**
     * Updates the assignment's name with a new value
     * checks to make sure the new name isnt null or empty.
     * @param newAssignmentName The new name to assign.
     */
    public void setAssignmentName(String newAssignmentName) {
        boolean isNameProvided = newAssignmentName != null;
        boolean isNameNotEmpty = false;
        if (isNameProvided) {
            String trimmedName = newAssignmentName.trim();
            isNameNotEmpty = !trimmedName.isEmpty();
            if (isNameNotEmpty) {
                this.name = trimmedName;
                // TODO: Maybe fire observer event if Assignment becomes observable?
                return;
            }
        }
        // If name was null or empty, don't change it, print warning.
        System.out.println("Assignment warning: setAssignmentName ignored empty or null name.");
    }

    /**
	 * Updates the num of points this assignment is worth.
	 * throws error if provided value is negative,
	 * since assignments cant be worth negative points.
     * @param points The new points value double.
     */
    public void setPointsPossible(double points) {
        // Check if points are valid non negative.
        if (points < 0) {
            // Throw error to signal bad input from controller/view.
            throw new IllegalArgumentException("Assignment points must be non negative");
        }
        // If okay, update the value.
        this.pointsWorth = points;
        // TODO: Maybe fire observer event?
    }

    /**
     * Updates the assignment's due date string.
     * TODO: Add validation later to ensure YYYY-MM-DD format maybe?
     * @param newDueDate The new due date string.
     */
     public void setDueDate(String newDueDate) {
         // Store the string directly for now.
         this.dueDate = newDueDate;
         // TODO: Maybe fire observer event?
     }

     /**
     * Updates the assignment's category name string.
     * @param newCategoryName The new category name string. Can be null or empty maybe?
     */
     public void setCategoryName(String newCategoryName) {
         // Store the name string. Allows unsetting category maybe?
         this.categoryName = newCategoryName;
          // TODO: Maybe fire observer event?
          // TODO: If model changes to store GradingCategory object, need setCategory(GradingCategory c) instead.
     }

     /**
     * Updates the assignment's group name string.
     * @param newGroupName The new group name string. Can be null or empty.
     */
     public void setGroupName(Group newGroupName) {   // group isn't a string anymore
         this.group = newGroupName;
          // TODO: Maybe fire observer event?
     }

    /**
     * setGraded allows setting the graded status true or false.
     * Needed so AssignmentController can mark graded or ungraded.
     * @param graded The new boolean status.
     */
     public void setGraded(boolean graded) {
         // Check if status is actually changing before firing event maybe?
         // boolean changed = (this.isGraded != graded);
         this.isGraded = graded;
         // TODO: Fire observer event if changed?
         // if (changed) pcs.firePropertyChange("isGraded", !graded, graded);
     }

    /**
     * Updates the assignment's description text.
     * @param newDescription The new description string.
     */
    public void setDescription(String newDescription) {
        this.description = newDescription;
        // TODO: Maybe fire observer event?
    }

    // --- Grade Management ---

     /**
      * Marks this assignment as graded true. Simple helper.
      */
     public void markGraded() {
         this.setGraded(true); // Use the setter maybe for consistency/events
     }

    /**
     * Adds or updates a grade for a specific student for this assignment.
     * Stores the Grade object in the internal map using the username as key.
     * @param studentUsername The username String key.
     * @param grade The Grade object value points/feedback.
     */
    public void addGrade(String studentUsername, Grade grade) {
        // Check username is valid before using as key.
        boolean usernameOk = (studentUsername != null && !studentUsername.isEmpty());
        // Grade can technically be null maybe if teacher wants to remove grade?
        // Let's assume grade is not null for adding/updating here.
        boolean gradeOk = (grade != null);
        if (usernameOk && gradeOk) {
            // Put replaces existing value if key already there.
            studentGrades.put(studentUsername, grade);
            // TODO: Maybe fire observer event specific to this assignment?
        } else {
             System.out.println("Assignment problem: addGrade got null username or grade for assignment " + this.name);
        }
    }

    /**
     * Gets a specific student's grade object for this assignment.
     * Looks it up in the internal map using the username.
     * @param studentUsername The username String key.
     * @return The Grade object, or null if no grade stored for that student.
     */
    public Grade getGrade(String studentUsername) {
        // Check input username is valid.
        boolean usernameOk = (studentUsername != null && !studentUsername.isEmpty());
        if (!usernameOk) {
            return null; // Can't look up grade for invalid username.
        }
        // Get the grade from the map using the username key. Returns null if not found.
        return studentGrades.get(studentUsername);
    }

    /**
     * hasSubmission checks if a student has any grade recorded for this assignment.
     * Just checks if the username key exists in the internal grades map.
     * @param studentUsername The username String key.
     * @return true if a grade entry exists, false otherwise.
     */
    public boolean hasSubmission(String studentUsername) {
        // Check username is valid.
        boolean usernameOk = (studentUsername != null && !studentUsername.isEmpty());
        if (!usernameOk) {
            return false;
        }
        // Check if the map contains an entry for this username key.
        return studentGrades.containsKey(studentUsername);
    }

    /**
     * Returns a copy of all grades map for this assignment.
     * EXAMPLE OF ENCAPSULATION Returning a copy prevents changing internal map.
     * @return A new Map<String, Grade> copy.
     */
    public Map<String, Grade> getAllGrades() {
        // Create a new HashMap and copy all entries from the internal map into it.
        return new HashMap<String, Grade>(this.studentGrades);
    }

    /**
     * Clears all grades stored within this assignment.
     * Used by Course.removeAssignment to clean up.
     */
    public void clearAllGrades() {
    	studentGrades.clear();
        System.out.println("Assignment info: Cleared all grades for assignment " + this.name);
        // TODO: Maybe fire event?
    }

    // --- Calculations moved from controller ---

    /**
     * calculateAverageScore calculates the average score for this assignment
     * based on all the grades currently stored inside it.
     * Gets all the grades, loops, sums, counts, and divides.
     * @return The average score as a double, 0.0 if no grades.
     */
    public double calculateAverageScore() {
        // Get the internal map of grades directly.
        Map<String, Grade> currentGrades = this.studentGrades;

        // Check if map is null or empty.
        boolean anyGradesExist = (currentGrades != null && !currentGrades.isEmpty());
        if (!anyGradesExist) {
            return 0.0; // No grades, average is 0.
        }

        //variables for calculation.
        double sumOfScores = 0.0;
        int numberOfGrades = 0;

        //loop through the Grade objects values in the map.
        for (Grade grade : currentGrades.values()) {
        	//check grade object valid.
            boolean gradeIsValid = (grade != null);
            if (gradeIsValid) {
                sumOfScores = sumOfScores + grade.getPointsEarned();
                numberOfGrades++;
            }
        }

        //calculate average, make sure dont divide by zero.
        double averageResult = 0.0;
        boolean canCalculate = (numberOfGrades > 0);
        if (canCalculate) {
            averageResult = sumOfScores / numberOfGrades;
        }

        return averageResult;
    }

    /**
     * calculateMedianScore calculates the median score for this assignment.
     * It gets all the grades, extracts the scores into a list,
     * sorts the list, then finds the middle value or averages the two middle values.
     * @return The median score as a double, or 0.0 if no grades.
     */
     public double calculateMedianScore() {
        //get the internal map of grades.
        Map<String, Grade> currentGrades = this.studentGrades;

        //check if any grades exist.
        boolean anyGradesExist = (currentGrades != null && !currentGrades.isEmpty());
        if (!anyGradesExist) {
            return 0.0;
        }

        //make a list to hold just the score numbers.
        List<Double> scoresList = new ArrayList<>();

        //loop through grades, add valid scores to the list.
        for (Grade grade : currentGrades.values()) {
             boolean gradeIsValid = (grade != null);
             if (gradeIsValid) {
                 scoresList.add(grade.getPointsEarned());
             }
        }

        //check if list ended up empty after loop maybe grades were null.
        boolean haveScoresToList = !scoresList.isEmpty();
        if (!haveScoresToList) {
             return 0.0; // Return 0 if no valid scores found.
        }

        //sort the scores low to high.
        Collections.sort(scoresList);

        //find the median value.
        double medianScoreResult = 0.0;
        int numberOfScores = scoresList.size();
        boolean isEvenNumberOfScores = (numberOfScores % 2 == 0);

        if (isEvenNumberOfScores) {
            //if even number, average the 2 middle scores.
            // Need to handle case of only 0 or 1 score? sort handles 1. If 0, list empty check above catches it.
            int middleIndex1 = numberOfScores / 2 - 1;
            int middleIndex2 = numberOfScores / 2;
            double middleScore1 = scoresList.get(middleIndex1);
            double middleScore2 = scoresList.get(middleIndex2);
            medianScoreResult = (middleScore1 + middleScore2) / 2.0;
        } else {
            // If odd number, median is the middle score. Index is size / 2.
            int middleIndex = numberOfScores / 2;
            medianScoreResult = scoresList.get(middleIndex);
        }
        return medianScoreResult;
     }

    /**
     * equals method to compare assignments. Based only on name for simplicity now maybe?
     * Or maybe name + course? Just name for now.
     * Important for checking if assignment already exists in lists/maps.
     */
    @Override
    public boolean equals(Object obj) {
        // Standard equals checks: same object, null, different class.
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        // Cast object to Assignment.
        Assignment otherAssignment = (Assignment) obj;
        // Compare based on the name field using String's equals.
        // Make sure name field isn't null maybe? Constructor checks it.
        return this.name.equals(otherAssignment.name);
    }

    /**
     * hashCode method. Must override if equals is overridden.
     * Base it on the same fields used in equals, which is just name right now.
     */
    @Override
    public int hashCode() {
        // Use the String's hashCode method on the name field.
        // Check for null first maybe? Constructor checks name not null.
        return name.hashCode();
    }

    /**
     * toString gives a text summary of the assignment.
     * Includes name, points, due date, category, graded status.
     */
    @Override
    public String toString() {
        // build string showing key details.
        return "Assignment [name=" + name + ", points=" + pointsWorth + ", due=" + dueDate +
                ", category=" + categoryName + ", graded=" + isGraded + "]";
    }
}