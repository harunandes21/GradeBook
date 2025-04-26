package test.model.grading;

import model.*;
import model.grading.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This tests the PointsBasedCalculator class.
 * We need to make sure it calculates the average correctly based on
 * total points earned divided by total points possible, but only
 * considers assignments marked as graded.
 */
class PointsBasedCalculatorTest {

    private PointsBasedCalculator calculator;
    private Course testCourse;
    private Student testStudent;
    private Assignment hw1;
    private Assignment hw2;
    private Assignment quiz1; // Ungraded

    /**
     * setUp runs before each test.
     * Creates fresh course, student, assignments, and the calculator.
     * Sets up some basic grades for the student.
     */
    @BeforeEach
    void setUp() {
        // Make the calculator we are testing
        calculator = new PointsBasedCalculator();
        // Make a course needs to be set to not use categories
        testCourse = new Course("Test Course Pts", "PTS101", "Test Sem", false); // false = use points
        // Make a student
        testStudent = new Student("Test", "Student", "ts@test.com", "pw", "test_student", "S999");
        // Make assignments
        hw1 = new Assignment("HW1", 100.0, "somedate", "hw", null);
        hw2 = new Assignment("HW2", 50.0, "somedate", "hw", null);
        quiz1 = new Assignment("Quiz1", 20.0, "somedate", "quiz", null);

        // IMPORTANT: Add assignments to the course so the calculator can find them
        testCourse.addAssignment(hw1);
        testCourse.addAssignment(hw2);
        testCourse.addAssignment(quiz1);

        // Add grades for the student for the assignments
        // Use the assignment's addGrade method username, grade
        hw1.addGrade(testStudent.getUsername(), new Grade(80.0, "Okay")); // 80 / 100
        hw2.addGrade(testStudent.getUsername(), new Grade(40.0, "Good")); // 40 / 50
        // quiz1 is left ungraded intentionally

        // Mark assignments as graded or not
        hw1.setGraded(true); // This one counts
        hw2.setGraded(true); // This one counts
        quiz1.setGraded(false); // This one should be ignored by calculator
    }

    /**
     * testSimpleAverage checks a basic case with two graded assignments.
     * Expected: (80 + 40) / (100 + 50) = 120 / 150 = 0.8 = 80.0%
     */
    @Test
    void testSimpleAverage() {
        System.out.println("Testing points calculator simple average");
        // Calculate average for our test student in the test course
        double average = calculator.calculateFinalAverage(testCourse, testStudent);
        // Check if it matches the expected 80.0
        // assertEquals takes expected, actual, and a delta allowance for floating point math
        assertEquals(80.0, average, 0.01, "Average should be (80+40)/(100+50)*100 = 80.0");
    }

    /**
     * testOnlyOneAssignmentGraded checks case where only one assignment counts.
     * Mark hw2 as not graded. Only hw1 should count.
     * Expected: 80 / 100 = 0.8 = 80.0%
     */
    @Test
    void testOnlyOneAssignmentGraded() {
        System.out.println("Testing points calculator with only one assignment graded");
        // Change hw2 status
        hw2.setGraded(false);
        // Recalculate
        double average = calculator.calculateFinalAverage(testCourse, testStudent);
        // Check average based only on hw1
        assertEquals(80.0, average, 0.01, "Average should be 80/100*100 = 80.0");
    }

    /**
     * testNoAssignmentsGraded checks case where nothing is graded.
     * Mark both hw1 and hw2 as not graded.
     * Expected: 0 / 0 -> should return 0.0%
     */
    @Test
    void testNoAssignmentsGraded() {
        System.out.println("Testing points calculator with no assignments graded");
        // Mark both counting assignments as ungraded
        hw1.setGraded(false);
        hw2.setGraded(false);
        // Recalculate
        double average = calculator.calculateFinalAverage(testCourse, testStudent);
        // Average should be 0 since total possible points is 0
        assertEquals(0.0, average, 0.01, "Average should be 0.0 if no assignments graded");
    }

    /**
     * testStudentMissingGrade checks case where an assignment is graded
     * but the student doesn't have a score for it. Should count as 0 earned points.
     * Setup: hw1 graded (100pts), hw2 graded (50pts). Student only has grade for hw1 (80pts).
     * Expected: (80 + 0) / (100 + 50) = 80 / 150 = 53.33%
     */
    @Test
    void testStudentMissingGrade() {
        System.out.println("Testing points calculator when student misses a graded assignment");
        // Need a different student who only did hw1
        Student studentB = new Student("Student", "B", "sb@test.com", "pw", "student_b", "S002");
        hw1.addGrade(studentB.getUsername(), new Grade(80.0, "Done"));
        // studentB has NO grade for hw2, but hw2 IS graded for the course.

        // Calculate for studentB
        double average = calculator.calculateFinalAverage(testCourse, studentB);
        // Expecting 80 / 150
        assertEquals(80.0 / 150.0 * 100.0, average, 0.01, "Average should be (80+0)/(100+50)*100");
    }

    /**
     * testZeroScoreGrade checks if getting a 0 grade is handled correctly.
     * Setup: hw1 grade 0/100, hw2 grade 40/50. Both graded.
     * Expected: (0 + 40) / (100 + 50) = 40 / 150 = 26.67%
     */
    @Test
    void testZeroScoreGrade() {
        System.out.println("Testing points calculator with a zero score grade");
        // Change student's grade for hw1 to 0
        hw1.addGrade(testStudent.getUsername(), new Grade(0.0, "Missed"));
        // Recalculate
        double average = calculator.calculateFinalAverage(testCourse, testStudent);
        // Expecting 40 / 150
        assertEquals(40.0 / 150.0 * 100.0, average, 0.01, "Average should be (0+40)/(100+50)*100");
    }

    /**
     * testNullCourseInput checks edge case passing null for the course.
     * Expected: Should return 0.0 and not crash.
     */
    @Test
    void testNullCourseInput() {
        System.out.println("Testing points calculator with null course");
        double average = calculator.calculateFinalAverage(null, testStudent);
        assertEquals(0.0, average, 0.01, "Should return 0.0 for null course");
    }

    /**
     * testNullStudentInput checks edge case passing null for the student.
     * Expected: Should return 0.0 and not crash.
     */
    @Test
    void testNullStudentInput() {
        System.out.println("Testing points calculator with null student");
        double average = calculator.calculateFinalAverage(testCourse, null);
        assertEquals(0.0, average, 0.01, "Should return 0.0 for null student");
    }

    /**
     * testCourseWithNoAssignments checks edge case course has no assignments defined.
     * Expected: Total possible is 0, should return 0.0.
     */
    @Test
    void testCourseWithNoAssignments() {
        System.out.println("Testing points calculator with course having no assignments");
        // Make a new empty course
        Course emptyCourse = new Course("Empty", "EMP101", "None", false);
        // Calculate
        double average = calculator.calculateFinalAverage(emptyCourse, testStudent);
        // Expect 0
        assertEquals(0.0, average, 0.01, "Should return 0.0 for course with no assignments");
    }

}
