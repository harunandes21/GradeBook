package model.grading;

import model.Course;
import model.Student;

/**
 * 
 * GradeCalculator is an interface. It's part of the Strategy pattern we need for the two different grading modes. 
 * It defines the calculateFinalAverage method signature. 
 * So, any class that actually calculates grades, like the points one or the category one, 
 * has to have this exact method, which makes sure they all work the same way from the outside.
 */
public interface GradeCalculator {

    /**
     * Main method for calculating one student's final average in a course.
     * Each strategy class like PointsBased or CategoryBased must provide this.
     *
     * @param theCourse The course context
     * @param theStudent The student we're calculating for.
     * @return The final average grade as a double.
     */
    double calculateFinalAverage(Course theCourse, Student theStudent);

}