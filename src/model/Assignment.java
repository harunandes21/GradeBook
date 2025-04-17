package model;

import java.util.HashMap;
import java.util.Map;

public class Assignment {
    private final String name;
    private final double pointsWorth;
    private final String dueDate;
    private final String categoryName;
    private final String groupName;
    private boolean isGraded;

    private Map<String, Grade> studentGrades = new HashMap<>();

    public Assignment(String name, double pointsWorth, String dueDate, String categoryName, String groupName) {
		if (name == null || name.isEmpty()) {
		   throw new IllegalArgumentException("Name cannot be null or empty");
		}
		if (pointsWorth <= 0) {
		   throw new IllegalArgumentException("Points must be positive");
		}
		if (categoryName == null) {
		   throw new IllegalArgumentException("Category cannot be null");
		}
		
		this.name = name;
		this.pointsWorth = pointsWorth;
		this.dueDate = dueDate;
		this.categoryName = categoryName;
		this.groupName = groupName;
		this.isGraded = false;
		this.studentGrades = new HashMap<String, Grade>();
		}

    public String getName() {
        return name;
    }

    public double getPointsWorth() {
        return pointsWorth;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getCategoryName() {
        return categoryName;
    }
    
    public String getGroupName() {
    	return groupName;
    }

    public boolean isGraded() {
        return isGraded;
    }

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
        if (studentUsername != null && grade != null) {
            studentGrades.put(studentUsername, grade);
        }
    }

    /*
     * Gets a student's grade for this assignment
     */
    public Grade getGrade(String studentUsername) {
        return studentGrades.get(studentUsername);
    }

    /*
     * Checks if a student has submitted this assignment
     */
    public boolean hasSubmission(String studentUsername) {
        return studentGrades.containsKey(studentUsername);
    }

    /*
     * Returns a copy of all grades for this assignment
     */
    public Map<String, Grade> getAllGrades() {
        return new HashMap<String, Grade>(studentGrades);
    }

    @Override
    public String toString() {
        return "Assignment [name=" + name + ", points=" + pointsWorth + ", due=" + dueDate +
                ", category=" + categoryName + ", graded=" + isGraded + "]";
    }
}
