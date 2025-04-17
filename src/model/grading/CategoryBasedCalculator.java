package model.grading;

import model.Course;
import model.Student;
import model.Assignment;
import model.Grade;
import model.GradingCategory;
import java.util.List;
import java.util.Map;


/**
 * CategoryBasedCalculator.java is the 2nd grade calculation strategy
 * handles weighted categories and dropping lowest scores.
 * implements the GradeCalculator interface.
 * also implementing GradeCalculator.
 *  This is for the more complicated second option, where grades are based on weighted categories like
 *   homework and exams, and maybe dropping the lowest scores. 
 *   Like the points based one, the calculateFinalAverage method here is just an empty skeleton right now
 *    printing TODO, because the actual logic for weights and drops will be complex and needs the other model
 *     classes and probably some helper methods later.
 */
public class CategoryBasedCalculator implements GradeCalculator {

    /**
     * calculates the final average based on weighted categories.
     * Needs to get categories, weights, drops, grades, apply rules, combine averages.
     * TODO: still need the calculation logic here.
     * @param theCourse  The Course object with category info.
     * @param theStudent The Student object with grades.
     * @return the calculated weighted average double
     */
    @Override
    public double calculateFinalAverage(Course theCourse, Student theStudent) {
        System.out.println("CategoryBasedCalculator: calculateFinalAverage TODO");
        // logic goes here later.
        return 0.0;
    }


}