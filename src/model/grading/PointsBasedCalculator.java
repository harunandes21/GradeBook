package model.grading;

import model.Course;
import model.Student;
import model.Assignment;
import model.Grade;
import java.util.List;
import java.util.Map;

/**
 * This class is one strategy for calculating grades.
 * Implements GradeCalculator for the simple points method which is
 * Final Grade = Total Earned / Total Possible.
 * 
 */
public class PointsBasedCalculator implements GradeCalculator {

    /**
     * Calculates the average using the total points method.
     * It gets all the assignments that belong to the course.
     * Then it loops through each one. For the specific student we care about,
     * it checks if the assignment was graded. If it was, it adds the assignment's
     * total possible points to a running total. Then it checks if the student
     * actually got a grade for that assignment using the assignment's own grade map.
     * If the student did get a grade, it adds the points they earned to another running total.
     * Then, it divides the total points the student earned by the total possible points
     * for all the graded assignments in the course to get the percentage average.
     * Uses the real Course and Student types now.
     *
     * @param theCourse  The Course object, it has the assignments list.
     * @param theStudent The Student object, needed to look up their specific grades.
     * @return The calculated average double percentage, or 0.0 if no points possible.
     */
    @Override
    public double calculateFinalAverage(Course theCourse, Student theStudent) {
        //first, just make sure we actually got a course and student.
        boolean courseIsMissing = (theCourse == null);
        boolean studentIsMissing = (theStudent == null);
        
        if (courseIsMissing || studentIsMissing) {
            // Print a message if something's wrong and return 0.
            System.out.println("in PointsBasedCalculator, got null course or student, can't calculate");
            return 0.0;
        }

        //initialize variables to keep track of points. Start them at zero.
        double totalPointsEarnedByThisStudent = 0.0;
        double totalPointsPossibleOverall = 0.0;

        // First, get the list of all assignments for this specific course.
        // The Course object should give us this list. Using its getter.
        List<Assignment> assignmentsInThisCourse = theCourse.getAllAssignments();

        // Next, go through every assignment in that list.
        boolean assignmentsListExists = (assignmentsInThisCourse != null);
        if (assignmentsListExists) {
            for (Assignment currentAssignment : assignmentsInThisCourse) {

                //Next, check if this assignment is actually marked as graded.
                // We only count graded assignments in the average.
                boolean isThisAssignmentGraded = currentAssignment.isGraded();

                if (isThisAssignmentGraded) {
                    // Next, if it's graded, get how many points it was worth.
                    double pointsPossibleForThisAssignment = currentAssignment.getPointsWorth();
                    // add this amount to the total possible points for the course average.
                    totalPointsPossibleOverall = totalPointsPossibleOverall + pointsPossibleForThisAssignment;

                    // Next, find the specific Grade object for this student on this assignment.
                    // The Assignment object itself holds a map of grades keyed by username.
                    String studentUsername = theStudent.getUsername();
                    Grade studentGradeForThisAssignment = currentAssignment.getGrade(studentUsername);

                    // Next, check if the student actually received a grade.
                    boolean studentHasGrade = (studentGradeForThisAssignment != null);
                    if (studentHasGrade) {
                        // if they did, get the points they earned from the Grade object.
                        double pointsEarned = studentGradeForThisAssignment.getPointsEarned();
                        
                        // add these earned points to the student's running total.
                        totalPointsEarnedByThisStudent = totalPointsEarnedByThisStudent + pointsEarned;
                    }
                    // if studentHasGrade is false, they get 0 points implicitly for this assignment.
                }
                //if isThisAssignmentGraded is false, skip it entirely.
                
            } //end loop through assignments
            
        } // End if assignments list exists

        // Next, calculate the final percentage average.
        double finalAverageResult = 0.0;
        //make sure we don't divide by zero if no graded assignments had points.
        boolean canCalculateAverage = (totalPointsPossibleOverall > 0.0);
        
        if (canCalculateAverage) {
            //calculate the percentage.
            finalAverageResult = (totalPointsEarnedByThisStudent / totalPointsPossibleOverall) * 100.0;
        } 
        
        else {
            //if no points were possible for example no graded assignments, the average is just 0.
            System.out.println("In PointsBasedCalculator, total possible points is zero, average is 0 for student " + theStudent.getUsername());
        }

        // return the final calculated average
        return finalAverageResult;
    }
}