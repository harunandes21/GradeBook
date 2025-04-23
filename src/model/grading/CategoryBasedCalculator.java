package model.grading;

import model.Course;
import model.Student;
import model.Assignment;
import model.Grade;
import model.GradingCategory;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * CategoryBasedCalculator.java is the 2nd grade calculation strategy
 * handles weighted categories and dropping lowest scores.
 * implements the GradeCalculator interface.
 */
public class CategoryBasedCalculator implements GradeCalculator {

    /**
     * calculates the final average based on weighted categories.
     * This method coordinates the process. It gets all the categories for the course.
     * For each category, it calls a helper method calculateCategorySubAverageAfterDrops
     * to figure out the student's percentage score within that single category,
     * taking into account any grades that should be dropped based on the category's rules.
     * Then, it takes that category percentage, multiplies it by the category's weight,
     * and adds it to a running total. The final result is the sum of all these
     * weighted category scores.
     * Uses the real Course and Student types now.
     *
     * @param theCourse  The Course object, which knows its categories and assignments.
     * @param theStudent The Student object, whose grades we need to look up.
     * @return the calculated weighted average double percentage.
     */
    @Override
    public double calculateFinalAverage(Course theCourse, Student theStudent) {
        // checks for valid input. Also check if the course actually uses categories.
        boolean courseIsMissing = (theCourse == null);
        boolean studentIsMissing = (theStudent == null);
        boolean courseUsesPoints = (theCourse != null && !theCourse.usesCategories());
        if (courseIsMissing || studentIsMissing || courseUsesPoints) {
             System.out.println("invalid input or course not using categories");
            return 0.0;
        }

        //variables to accumulate the final score.
        double finalWeightedAverageScore = 0.0;

        // first, get all the GradingCategory objects defined for this course.
        // Course object gives us a map where key is category name, value is the object.
        Map<String, GradingCategory> categoriesForTheCourse = theCourse.getGradingCategories();

        // Next, loop through each GradingCategory object in the map.
        boolean categoriesExist = (categoriesForTheCourse != null);
        
        if (categoriesExist) {
           for (GradingCategory currentCategory : categoriesForTheCourse.values()) {

                // Next, get the weight for this specific category (like 0.4 for 40%).
                double categoryWeight = currentCategory.getWeight();

                // Next, call the helper method to get the student's percentage score
                // just for this category, after applying any drop rules.
                double averagePercentForThisCategory = calculateCategorySubAverageAfterDrops(currentCategory, theStudent, theCourse);

                // Next, check if the helper returned a valid number, so not NaN.
                // NaN means the category was empty or had no graded assignments after drops.
                boolean isValidCategoryAverage = !Double.isNaN(averagePercentForThisCategory);
                
                if (isValidCategoryAverage) {
                    // if the average is valid, calculate this category's contribution
                    // to the final grade by multiplying the average by the weight.
                    double weightedScoreForCategory = averagePercentForThisCategory * categoryWeight;
                    
                    // add it to the overall final grade total
                    finalWeightedAverageScore = finalWeightedAverageScore + weightedScoreForCategory;

                } 
                
                else {
                    //if category average was NaN, just print a note, it contributes nothing.
                    System.out.println("Category '" + currentCategory.getName() + "' average is NaN for student " + theStudent.getUsername());
                }
           } //end loop through categories
           
        }

        return finalWeightedAverageScore;
    }

    // --- Helper Method ---

    /**
     * Helper method to calculate the average percentage for a list of grades
     * within one category, after applying drop rules defined by the category.
     * It gets all assignments in the category, finds which ones to drop for this student,
     * then calculates the earned or possible sum for the remaining graded assignments.
     *
     * @param category The GradingCategory object we're calculating for.
     * @param student The Student whose grades we need.
     * @param course The Course object (needed to get all student grades maybe).
     * @return The average percentage (double), or Double.NaN if there's no countable grades.
     */
    private double calculateCategorySubAverageAfterDrops(GradingCategory category, Student student, Course course) {
        // get all assignments that belong to this category.
        List<Assignment> assignmentsInCategory = category.getAssignments(); //gets a copy

        // Get all of the student's grades for the entire course first.
        // This is needed by the getDroppedAssignments helper in GradingCategory.
        // Using the Course method to get grades for this student.
        Map<Assignment, Grade> studentGradesMap = course.getGradesForStudent(student); // Gets a copy

        // Ask the GradingCategory object itself to figure out which assignments to drop for this student.
        // It needs the student's grades map to compare scores.
        List<Assignment> droppedAssignments = category.getDroppedAssignments(studentGradesMap);

        // Variables to sum points for this category only, after drops.
        double categoryPointsEarned = 0.0;
        double categoryPointsPossible = 0.0;

        //now loop through the assignments actually in this category.
        for (Assignment assignment : assignmentsInCategory) {
            //check if this assignment is marked as graded for the course overall.
            boolean assignmentIsGraded = assignment.isGraded();

            if (assignmentIsGraded) {
                //check if this specific assignment is in the list of ones we should drop
                boolean shouldDropThisOne = droppedAssignments.contains(assignment);

                //if this assignment is not dropped, include it in the category average.
                if (!shouldDropThisOne) {
                    //get the points possible for this assignment
                    double pointsPossible = assignment.getPointsWorth();
                    // Add it to the total possible points for this category.
                    categoryPointsPossible = categoryPointsPossible + pointsPossible;

                    //get the student's grade for this assignment.
                    //we can use the student's getGradeForAssignment method directly here.
                    Grade grade = student.getGradeForAssignment(assignment);
                    if (grade != null) {
                        //if a grade exists, add the earned points.
                        categoryPointsEarned = categoryPointsEarned + grade.getPointsEarned();
                    }
                    //if grade is null for a not dropped, graded assignment, adds 0 earned points.
                }
                // if shouldDropThisOne is true, we just ignore this assignment.
            }
            // if assignmentIsGraded is false, we ignore it.
            
        } //end loop through assignments in category

        //calculate the final percentage average for this category.
        boolean canCalculateCategoryAverage = (categoryPointsPossible > 0.0);
        
        if (canCalculateCategoryAverage) {
            //return the percentage.
            return (categoryPointsEarned / categoryPointsPossible) * 100.0;
        } 
        
        else {
            //if no points were possible in this category after drops, return NaN.
            //This signals to the main method that this category contributes nothing.
            return Double.NaN;
        }
    }
}