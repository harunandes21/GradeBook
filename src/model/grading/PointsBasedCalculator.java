package model.grading;

import model.Course;
import model.Student;
import model.Assignment;
import model.Grade;
import java.util.List;
import java.util.Map;

/**
 * PointsBasedCalculator is one strategy for calculating grades.
 * Implements GradeCalculator for the simple points method which is 
 * Final Grade = Total Earned / Total Possible. 
 * PointsBasedCalculator.java, is one of the actual strategies that implements 
 * that GradeCalculator interface. This is the simple one for calculating grades
 *  based just on total points earned divided by total points possible.
 *   Right now, the calculateFinalAverage method is just empty inside, prints a TODO message,
 *    and returns zero, because the real logic needs the Course and Student details first.
 */

public class PointsBasedCalculator implements GradeCalculator {

    /**
     * Calculates the average using the total points method.
     * Needs to get assignments, get student grades, sum earned for graded items.
     * TODO: still need calculation logic
     * @param theCourse  The Course object.
     * @param theStudent The Student object.
     * @return The calculated average double
     */
    @Override
    public double calculateFinalAverage(Course theCourse, Student theStudent) {
        System.out.println("PointsBasedCalculator: calculateFinalAverage TODO");
        // Logic goes here later.
        return 0.0;
    }
}