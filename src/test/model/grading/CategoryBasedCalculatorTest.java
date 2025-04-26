package test.model.grading;

import model.*; 
import model.grading.*; 

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * This tests the CategoryBasedCalculator class.
 * This one handles the weighted categories and dropping lowest scores logic.
 * Needs more setup to test correctly. We need categories with weights,
 * assignments linked to those categories, and student grades.
 */
class CategoryBasedCalculatorTest {

    // Declare variables
    private CategoryBasedCalculator calculator;
    private Course categoryCourse;
    private Student testStudent;
    private GradingCategory hwCategory; // Homework, 50% weight, drop 1 lowest
    private GradingCategory quizCategory; // Quizzes, 50% weight, drop 0 lowest
    private Assignment hw1, hw2, hw3; // Homework assignments
    private Assignment q1, q2; // Quiz assignments

    /**
     * setUp runs before each test. Creates a course set up for categories,
     * defines the categories, adds assignments to them, and gives student grades.
     */
    @BeforeEach
    void setUp() {
        // Make the calculator we are testing
        calculator = new CategoryBasedCalculator();
        // Make a course CONFIGURED TO USE categories true = use categories
        categoryCourse = new Course("Test Course Cat", "CAT101", "Test Sem", true);
        // Make a student
        testStudent = new Student("Test", "Student", "ts@test.com", "pw", "test_student", "S999");

        // Define categories
        // Homework is 50% of grade, drop 1 lowest score
        hwCategory = new GradingCategory("Homework", 0.5, 1);
        // Quizzes are 50% of grade, drop 0 lowest scores
        quizCategory = new GradingCategory("Quizzes", 0.5, 0);

        // Add categories to the course
        categoryCourse.addGradingCategory(hwCategory);
        categoryCourse.addGradingCategory(quizCategory);
        
        Group group1 = new Group("Module1");

        // Make assignments, make sure categoryName matches category object name exactly
        hw1 = new Assignment("HW1", 10.0, "d1", "Homework", group1); // Score 8/10 = 80%
        hw2 = new Assignment("HW2", 10.0, "d2", "Homework", group1); // Score 6/10 = 60% (lowest)
        hw3 = new Assignment("HW3", 10.0, "d3", "Homework", group1); // Score 9/10 = 90%
        q1 = new Assignment("Q1", 20.0, "d4", "Quizzes", group1);  // Score 18/20 = 90%
        q2 = new Assignment("Q2", 20.0, "d5", "Quizzes", group1);  // Score 16/20 = 80%

        // Add assignments to the course (which also adds them to category if name matches)
        categoryCourse.addAssignment(hw1);
        categoryCourse.addAssignment(hw2);
        categoryCourse.addAssignment(hw3);
        categoryCourse.addAssignment(q1);
        categoryCourse.addAssignment(q2);

        // Add grades for the student FOR these assignments
        // Use student's addGrade method
        testStudent.addGrade(hw1, new Grade(8.0, ""));
        testStudent.addGrade(hw2, new Grade(6.0, "")); // This is the lowest HW score
        testStudent.addGrade(hw3, new Grade(9.0, ""));
        testStudent.addGrade(q1, new Grade(18.0, ""));
        testStudent.addGrade(q2, new Grade(16.0, ""));

        // Mark assignments as graded IN THE ASSIGNMENT OBJECT ITSELF
        // The calculator helper uses Assignment.isGraded()
        hw1.setGraded(true);
        hw2.setGraded(true);
        hw3.setGraded(true);
        q1.setGraded(true);
        q2.setGraded(true);

        // Assign the calculator strategy to the course
        categoryCourse.setGradeCalculator(calculator);
    }

    /**
     * testNormalCalculation checks the main scenario.
     * HW: 8/10, 6/10, 9/10 -> drop 6/10. Average = (8+9) / (10+10) = 17/20 = 85.0%
     * Quiz: 18/20, 16/20 -> no drops. Average = (18+16) / (20+20) = 34/40 = 85.0%
     * Final: (85.0 * 0.5) + (85.0 * 0.5) = 42.5 + 42.5 = 85.0
     */
    @Test
    void testNormalCalculation() {
        System.out.println("Testing category calculator normal case...");
        // Calculate average for the student in the course
        double average = calculator.calculateFinalAverage(categoryCourse, testStudent);
        // Check against expected value
        assertEquals(85.0, average, 0.01, "Average should be 85.0 with HW drop");
    }

    /**
     * testCalculationWithNoDrops checks if setting drops to 0 works.
     * Create new category with drop 0, add assignments, recalculate.
     * HW: 8/10, 6/10, 9/10 -> no drops. Average = (8+6+9)/(10+10+10) = 23/30 = 76.67%
     * Quiz: 85.0% still.
     * Final: (76.67 * 0.5) + (85.0 * 0.5) = 38.335 + 42.5 = 80.835
     */
    @Test
    void testCalculationWithNoDrops() {
        System.out.println("Testing category calculator with no drops...");
        // Remake the HW category with 0 drops but same weight
        GradingCategory hwNoDropCategory = new GradingCategory("Homework", 0.5, 0);
        // We need to re add the assignments to this new category object
        hwNoDropCategory.addAssignment(hw1);
        hwNoDropCategory.addAssignment(hw2);
        hwNoDropCategory.addAssignment(hw3);
        // Clear old categories and add new ones to course
        categoryCourse.clearGradingCategories();
        categoryCourse.addGradingCategory(hwNoDropCategory);
        categoryCourse.addGradingCategory(quizCategory); // Add quiz category back

        // Recalculate
        double average = calculator.calculateFinalAverage(categoryCourse, testStudent);
        // Calculate expected
        double expectedHwAvg = (8.0 + 6.0 + 9.0) / (10.0 + 10.0 + 10.0) * 100.0; // 23/30 = 76.67%
        double expectedQuizAvg = (18.0 + 16.0) / (20.0 + 20.0) * 100.0; // 34/40 = 85.0%
        double expectedFinal = (expectedHwAvg * 0.5) + (expectedQuizAvg * 0.5);
        // Check result
        assertEquals(expectedFinal, average, 0.01, "Average should be 80.83 with no HW drop");
    }

    /**
     * testOnlyOneCategoryGraded checks case where one category has no graded items.
     * Mark all quizzes as not graded. Quiz category average should be NaN/-1, contribution 0.
     * HW average is 85.0% after drop 1.
     * Final: (85.0 * 0.5) + (0 * 0.5) = 42.5
     */
    @Test
    void testOnlyOneCategoryGraded() {
        System.out.println("Testing category calculator with one category having no graded assignments...");
        // Mark quizzes as not graded
        q1.setGraded(false);
        q2.setGraded(false);
        // Recalculate
        double average = calculator.calculateFinalAverage(categoryCourse, testStudent);
        // HW avg is still 85.0. Quiz avg is NaN/-1. Final should only use HW weight.
        double expectedHwAvg = 85.0;
        double expectedFinal = expectedHwAvg * hwCategory.getWeight(); // 85.0 * 0.5
        // Check result
        assertEquals(expectedFinal, average, 0.01, "Average should be 42.5 based only on HW category");
    }

    /**
     * testStudentMissingOneGrade checks when student misses one assignment in a category.
     * Remove student's grade for hw1. HW is 6/10, 9/10. Drop 1 lowest -> drop hw2(6/10).
     * HW Average = 9/10 = 90.0%. Quiz average still 85.0%.
     * Final: (90.0 * 0.5) + (85.0 * 0.5) = 45.0 + 42.5 = 87.5
     */
    @Test
    void testStudentMissingOneGrade() {
        System.out.println("Testing category calculator when student missing one grade in drop category...");
        // Remove student's grade for hw1
        testStudent.removeGradeForAssignment(hw1); // Assumes student has this method
        // Recalculate
        double average = calculator.calculateFinalAverage(categoryCourse, testStudent);
        // Calculate expected
        // HW grades are now just 6, 9. Lowest is 6. Drop it. Keep 9. HW Avg = 9/10*100 = 90.0%
        double expectedHwAvg = 90.0;
        double expectedQuizAvg = 85.0;
        double expectedFinal = (expectedHwAvg * 0.5) + (expectedQuizAvg * 0.5);
        // Check result
        assertEquals(expectedFinal, average, 0.01, "Average should be 87.5");
    }

    /**
     * testCourseSetToPointsMode checks that calculator returns 0 if course isn't set to use categories.
     */
    @Test
    void testCourseSetToPointsMode() {
        System.out.println("Testing category calculator when course uses points mode...");
        // Create a new course that uses points mode false = points
        Course pointsCourse = new Course("Points Course", "PTS202", "Test", false);
        // Add same categories/assignments/grades just for data
        pointsCourse.addGradingCategory(hwCategory);
        pointsCourse.addGradingCategory(quizCategory);
        pointsCourse.addAssignment(hw1); pointsCourse.addAssignment(hw2); pointsCourse.addAssignment(hw3);
        pointsCourse.addAssignment(q1); pointsCourse.addAssignment(q2);
        testStudent.addGrade(hw1, new Grade(8.0, "")); testStudent.addGrade(hw2, new Grade(6.0, "")); // ... add others ...
        // Set the calculator for the course
        pointsCourse.setGradeCalculator(calculator);
        // Calculate average
        double average = calculator.calculateFinalAverage(pointsCourse, testStudent);
        // Expect 0 because course isn't using categories
        assertEquals(0.0, average, 0.01, "Should return 0.0 if course not set to use categories");
    }

    /**
     * testNullCourseInput checks passing null course. Expected 0.0.
     */
    @Test
    void testNullCourseInput() {
        System.out.println("Testing category calculator with null course...");
        double average = calculator.calculateFinalAverage(null, testStudent);
        assertEquals(0.0, average, 0.01, "Should return 0.0 for null course");
    }

    /**
     * testNullStudentInput checks passing null student. Expected 0.0.
     */
    @Test
    void testNullStudentInput() {
        System.out.println("Testing category calculator with null student...");
        double average = calculator.calculateFinalAverage(categoryCourse, null);
        assertEquals(0.0, average, 0.01, "Should return 0.0 for null student");
    }

    /**
     * testCourseWithNoCategories checks course using categories but none defined. Expected 0.0.
     */
    @Test
    void testCourseWithNoCategories() {
        System.out.println("Testing category calculator with course having no categories defined...");
        
        // Make course using categories, but don't add any
        Course noCatCourse = new Course("No Cats", "NC101", "Test", true);
        noCatCourse.addAssignment(hw1); // Add assignment just so it's not empty
        testStudent.addGrade(hw1, new Grade(8.0, ""));
        hw1.setGraded(true);
        noCatCourse.setGradeCalculator(calculator);
        
        // Calculate average
        double average = calculator.calculateFinalAverage(noCatCourse, testStudent);
        
        // Expect 0 because no categories to contribute weight
        assertEquals(0.0, average, 0.01, "Should return 0.0 for category course with no categories");
    }

}