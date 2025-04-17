package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Represents a category of assignments with specific grading rules
 */
public class GradingCategory {
    private final String name;
    private final double weight;
    private final int numDropped;
    private final List<Assignment> assignments;

    public GradingCategory(String name, double weight, int numDropped) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (weight < 0 || weight > 1) {
            throw new IllegalArgumentException("Weight must be between 0 and 1");
        }
        if (numDropped < 0) {
            throw new IllegalArgumentException("Dropped number must be non-negative");
        }

        this.name = name;
        this.weight = weight;
        this.numDropped = numDropped;
        this.assignments = new ArrayList<Assignment>();
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public int getNumDropped() {
        return numDropped;
    }

    public List<Assignment> getAssignments() {
        return new ArrayList<Assignment>(assignments);
    }

    /*
     * Adds an assignment to this category
     */
    public void addAssignment(Assignment a) {
        if (a != null) {
            assignments.add(a);
        }
    }

    /*
     * Removes an assignment from this category
     */
    public void removeAssignment(Assignment a) {
        assignments.remove(a);
    }

    /**
     * Determines which assignments should be dropped for a student
     * @param studentGrades: map of the student's grades
     * @return list of assignments to drop
     */
    public List<Assignment> getDroppedAssignments(Map<Assignment, Grade> studentGrades) {
        List<Assignment> eligible = new ArrayList<Assignment>();
        // getting all graded assignments
        for (Assignment a : assignments) {
            if (studentGrades.containsKey(a)) {
                eligible.add(a);
            }
        }
        // sorting assignments by score, lowest to highest
        Collections.sort(eligible, new GradeComparator(studentGrades));

        // returning only the number to drop
        int dropCount = Math.min(numDropped, eligible.size());
        return eligible.subList(0, dropCount);
    }
    
    /*
     * Helper class for comparing assignments by grade
     */
    private class GradeComparator implements Comparator<Assignment> {
        private Map<Assignment, Grade> grades;
        
        public GradeComparator(Map<Assignment, Grade> grades) {
            this.grades = grades;
        }
        
        public int compare(Assignment a1, Assignment a2) {
            double g1 = grades.get(a1).getPointsEarned();
            double g2 = grades.get(a2).getPointsEarned();
            return Double.compare(g1, g2);
        }
    }
}
