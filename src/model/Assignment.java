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
    private String groupName;
    private boolean isGraded;

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
    public Assignment(String name, double pointsWorth, String dueDate, String categoryName, String groupName) {
		if (name == null || name.isEmpty()) {
		   throw new IllegalArgumentException("Assignment name cannot be null or empty");
		}
		
        //check so we dont have non positive points
		if (pointsWorth <= 0) {
		   throw new IllegalArgumentException("Assignment points must be positive");
		}
		
		if (categoryName == null) {
		   throw new IllegalArgumentException("Assignment category cannot be null");
		}

		this.name = name;
		this.pointsWorth = pointsWorth;
		this.dueDate = dueDate; 
		this.categoryName = categoryName;
		this.groupName = groupName;
		this.isGraded = false; //start it as not graded
		this.studentGrades = new HashMap<String, Grade>(); //,map initialized
	}

    //Getters

    /** Gets the assignment's name. */
    public String getName() {
        return name;
    }

    /** Gets the max points possible. */
    public double getPointsWorth() {
        return pointsWorth;
    }

    /** Gets the due date string. */
    public String getDueDate() {
        return dueDate;
    }

    /** Gets the category name string */
    public String getCategoryName() {
        return categoryName;
    }

    /** Gets the group name string */
    public String getGroupName() {
    	return groupName;
    }

    /** checks if the assignment is marked as graded*/
    public boolean isGraded() {
        return isGraded;
    }

    //Setters, needed by controllers

    /**
     * Updates the assignment's name with a new value
     * checks to make sure the new name isnt null or empty.
     * 
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
                return;
            }
        }
        
        System.out.println("Assignment warning: attempted to set an empty or null assignment name.");
    }
    
    /**
     * Sets the graded status of this assignment
     */
    public void setGraded(boolean graded) {
        this.isGraded = graded;
    }


    /**
	 * Updates the num of points this assignment is worth.
	 * error if provided value is negative,
	 * since assignments cant be worth negative points
     * @param pointsWorth The new points value double.
     */
    public void setPointsPossible(double pointsWorth) {
        if (pointsWorth < 0) {
            throw new IllegalArgumentException("Assignment points must be not negative");
        }
        
        this.pointsWorth = pointsWorth;
    }

    /**
     * setGraded allows setting the graded status true or false.
     * Needed so AssignmentController can mark graded or ungraded.
     * @param graded The new boolean status.
     */
     public void setGraded(boolean graded) {
         this.isGraded = graded;
     }

    //grade management

     /*
      * Marks this assignment as graded
      */
     public void markGraded() {
         this.isGraded = true;
     }

    /*
     * Adds a grade for a student
     */
    public void addGrade(String studentUsername, Grade grade) {
        if (studentUsername != null && !studentUsername.isEmpty()) {
            studentGrades.put(studentUsername, grade);
        }
    }

    /*
     * Gets a student's grade for this assignment
     */
    public Grade getGrade(String studentUsername) {
        // Check input username
        boolean usernameOk = (studentUsername != null && !studentUsername.isEmpty());
        if (!usernameOk) {
            return null;
        }
        //get the grade from the map using the username.
        return studentGrades.get(studentUsername);
    }

    /**
     * hasSubmission checks if a student has any grade for this assignment.
     * checks if the username key exists in the grades map.
     */
    public boolean hasSubmission(String studentUsername) {

        boolean usernameOk = (studentUsername != null && !studentUsername.isEmpty());
        if (!usernameOk) {
            return false;
        }
        //check if the map has an entry for this username
        // returns true if a grade exists for the student, false otherwise.
        return studentGrades.containsKey(studentUsername);
    }

    /*
     * Returns a copy of all grades for this assignment
     * EXAMPLE OF ENCAPSULATION since returning a copy prevents outside code from changing
     * the assignment's internal map.
     */
    public Map<String, Grade> getAllGrades() {
        //create a new HashMap, copy all entries from the internal map into it.
        return new HashMap<String, Grade>(this.studentGrades);
    }
    
    /*
     * Clears all grades
     */
    public void clearAllGrades() {
    	studentGrades.clear();
    }

    //Calculations we moved from controller

    /**
     * calculateAverageScore calculates the average score for this assignment
     * based on all the grades currently stored inside.
     * It gets all the grades, loops, sums, counts, and divides.
     * @return The average score as a double, 0.0 if no grades.
     */
    public double calculateAverageScore() {
        //get the internal map of grades. Not copying it since we're just reading it
        Map<String, Grade> currentGrades = this.studentGrades;
        
        boolean anyGradesExist = (currentGrades != null && !currentGrades.isEmpty());
        if (!anyGradesExist) {
            //with no grades, average would bes 0.
            return 0.0;
        }

        //variables for calculation
        double sumOfScores = 0.0;
        int numberOfGrades = 0;
        
        //loop through the Grade objects in the map's values
        for (Grade grade : currentGrades.values()) {
            
        	//check grade object valid
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
     * It gets all the grades, extracts the scores, sorts them, finds the middle value.
     * @return The median score as a double, or 0.0 if no grades.
     */
     public double calculateMedianScore() {
        //get the internal map of grades
        Map<String, Grade> currentGrades = this.studentGrades;
        
        //check if any grades exist.
        boolean anyGradesExist = (currentGrades != null && !currentGrades.isEmpty());
        
        if (!anyGradesExist) {
            return 0.0;
        }

        //make a list to hold just the score numbers
        List<Double> scoresList = new ArrayList<>();
        
        //loop through grades, add valid scores to the list
        for (Grade grade : currentGrades.values()) {
             boolean gradeIsValid = (grade != null);
             
             if (gradeIsValid) {
                 scoresList.add(grade.getPointsEarned());
             }
        }

        //check if list ended up empty
        boolean haveScoresToList = !scoresList.isEmpty();
        if (!haveScoresToList) {
             return 0.0; // Return 0 if no valid scores found
        }

        //sort the scores low to high
        Collections.sort(scoresList);

        //find the median value.
        double medianScoreResult = 0.0;
        int numberOfScores = scoresList.size();
        boolean isEvenNumberOfScores = (numberOfScores % 2 == 0);

        if (isEvenNumberOfScores) {
            //if even number, average the 2 middle scores.
            int middleIndex1 = numberOfScores / 2 - 1;
            int middleIndex2 = numberOfScores / 2;
            
            double middleScore1 = scoresList.get(middleIndex1);
            double middleScore2 = scoresList.get(middleIndex2);
            
            medianScoreResult = (middleScore1 + middleScore2) / 2.0;
        } 
        
        else {
            // If odd number, median is the middle score.
            int middleIndex = numberOfScores / 2;
            medianScoreResult = scoresList.get(middleIndex);
        }
        return medianScoreResult;
     }

    /**
     * toString gives a text summary of the assignment
     */
    @Override
    public String toString() {
        // string showing key details.
        return "Assignment [name=" + name + ", points=" + pointsWorth + ", due=" + dueDate +
                ", category=" + categoryName + ", graded=" + isGraded + "]";
    }
}