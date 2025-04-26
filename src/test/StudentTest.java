package test;

import model.*;
import model.grading.PointsBasedCalculator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive test suite for the Student class.
 * Tests all functionality including enrollment, grading, GPA calculation,
 * and property change notifications.
 */
public class StudentTest {
    private Student student;
    private Course course;
    private PointsBasedCalculator calculator;
    private boolean propertyChangeFired;

    /**
     * Sets up the basic test environment before each test.
     * Initializes a student, course, and calculator with minimal configuration.
     * Specific test cases will add additional setup as needed.
     */
    @Before
    public void setUp() {
        student = new Student("John", "Doe", "john@test.com", "password", "jdoe", "12345");
        course = new Course("Math", "MATH101", "Fall 2023", false);
        calculator = new PointsBasedCalculator();
        propertyChangeFired = false;
    }

    // Helper method to create standardized test assignments
    private Assignment createTestAssignment(String name, double points) {
        Assignment assignment = new Assignment(name, points, "2023-10-01", "Test", null);
        assignment.markGraded();
        return assignment;
    }

    // ==================== BASIC FUNCTIONALITY TESTS ====================

    /**
     * Test 1: Verify student initialization
     * Checks that all basic student properties are correctly set during construction.
     */
    @Test
    public void testStudentInitialization() {
        assertEquals("First name should match", "John", student.getFirstName());
        assertEquals("Last name should match", "Doe", student.getLastName());
        assertEquals("Email should match", "john@test.com", student.getEmail());
        assertEquals("Username should match", "jdoe", student.getUsername());
        assertEquals("Student ID should match", "12345", student.getStudentId());
        assertEquals("Role should be STUDENT", Role.STUDENT, student.getRole());
        assertTrue("Current courses should be empty initially", student.getCurrentCourses().isEmpty());
        assertTrue("Completed courses should be empty initially", student.getCompletedCourses().isEmpty());
        assertTrue("Grades should be empty initially", student.getGrades().isEmpty());
    }

    // ==================== COURSE ENROLLMENT TESTS ====================

    /**
     * Test 2: Normal case - Enroll in a course
     * Verifies successful course enrollment.
     */
    @Test
    public void testEnrollInCourse() {
        student.enrollInCourse(course);
        assertEquals("Should have 1 enrolled course", 1, student.getCurrentCourses().size());
        assertTrue("Course should be in enrolled list", student.getCurrentCourses().contains(course));
    }

    /**
     * Test 3: Edge case - Enroll in null course
     * Verifies handling of null course enrollment.
     */
    @Test
    public void testEnrollInNullCourse() {
        student.enrollInCourse(null);
        assertTrue("Should not enroll null course", student.getCurrentCourses().isEmpty());
    }

    /**
     * Test 4: Edge case - Enroll in same course twice
     * Verifies duplicate enrollment prevention.
     */
    @Test
    public void testDuplicateEnrollment() {
        student.enrollInCourse(course);
        student.enrollInCourse(course);
        assertEquals("Should only have one enrollment", 1, student.getCurrentCourses().size());
    }

    // ==================== COURSE COMPLETION TESTS ====================

    /**
     * Test 5: Normal case - Complete a course
     * Verifies successful course completion workflow.
     */
    @Test
    public void testCompleteCourse() {
        // Setup course with calculator
        course.setGradeCalculator(calculator);
        
        // Add graded assignment
        Assignment assignment = createTestAssignment("Test", 100);
        course.addAssignment(assignment);
        
        // Enroll student
        student.enrollInCourse(course);
        
        // Add grade
        Grade grade = new Grade(85.0, "Good work");
        student.addGrade(assignment, grade);
        
        // Complete course
        student.completeCourse(course);
        
        // Calculate expected average
        double expectedAverage = 85.0; // (85/100)*100 = 85%
        String expectedLetterGrade = student.getLetterGrade(expectedAverage);
        
        // Verify final grade
        assertNotNull("Final grade should be set", student.getFinalGradeForCourse(course));
        assertEquals("Final grade should match calculated average", 
            expectedLetterGrade, student.getFinalGradeForCourse(course));
    }

    /**
     * Test 6: Edge case - Complete a course not enrolled in
     * Verifies handling of completing non-enrolled courses.
     */
    @Test
    public void testCompleteNonEnrolledCourse() {
        Course anotherCourse = new Course("Science", "SCI101", "Fall 2023", false);
        student.completeCourse(anotherCourse);
        assertTrue("Should not complete non-enrolled course", student.getCompletedCourses().isEmpty());
    }

    // ==================== GRADE MANAGEMENT TESTS ====================

    /**
     * Test 7: Normal case - Add and retrieve grade for assignment
     * Verifies grade storage and retrieval.
     */
    @Test
    public void testAddAndGetGrade() {
        Assignment assignment = createTestAssignment("Test", 100);
        Grade grade = new Grade(85.0, "Good work");
        student.addGrade(assignment, grade);
        assertEquals("Should retrieve added grade", grade, student.getGradeForAssignment(assignment));
    }

    /**
     * Test 8: Edge case - Add null grade
     * Verifies handling of null grades.
     */
    @Test
    public void testAddNullGrade() {
        Assignment assignment = createTestAssignment("Test", 100);
        student.addGrade(assignment, null);
        assertNull("Should not store null grade", student.getGradeForAssignment(assignment));
    }

    /**
     * Test 9: Edge case - Get grade for null assignment
     * Verifies handling of null assignment queries.
     */
    @Test
    public void testGetGradeForNullAssignment() {
        assertNull("Should return null for null assignment", student.getGradeForAssignment(null));
    }

    // ==================== GPA CALCULATION TESTS ====================

    /**
     * Test 10: Normal case - Calculate GPA with completed courses
     * Verifies correct GPA calculation for completed courses.
     */
    @Test
    public void testCalculateGPA() {
        // Setup course with calculator
        course.setGradeCalculator(calculator);
        
        // Add assignments
        Assignment assignment1 = createTestAssignment("Test 1", 100);
        Assignment assignment2 = createTestAssignment("Test 2", 100);
        course.addAssignment(assignment1);
        course.addAssignment(assignment2);
        
        // Enroll and add grades
        student.enrollInCourse(course);
        student.addGrade(assignment1, new Grade(80.0, "Good"));
        student.addGrade(assignment2, new Grade(90.0, "Excellent"));
        
        // Complete course (this should set final grade)
        student.completeCourse(course);
        
        // Verify GPA
        assertEquals("GPA should be 3.0", 3.0, student.calculateGPA(), 0.01);
    }

    /**
     * Test 11: Edge case - Calculate GPA with no completed courses
     * Verifies GPA calculation with no completed courses.
     */
    @Test
    public void testCalculateGPAWithNoCompletedCourses() {
        assertEquals("GPA should be 0 with no completed courses", 0.0, student.calculateGPA(), 0.01);
    }

    /**
     * Test 24: Normal case - Calculate GPA with multiple courses
     * Verifies correct GPA calculation across multiple courses.
     */
    @Test
    public void testCalculateGPAWithMultipleCourses() {
        // Setup first course (85% = B = 3.0)
        Course math = new Course("Math", "MATH101", "Fall 2023", false);
        math.setGradeCalculator(calculator);
        Assignment mathTest = createTestAssignment("Math Test", 100);
        math.addAssignment(mathTest);
        student.enrollInCourse(math);
        student.addGrade(mathTest, new Grade(85.0, "Good"));
        student.completeCourse(math); // Should auto-set final grade
        
        // Setup second course (95% = A = 4.0)
        Course science = new Course("Science", "SCI101", "Fall 2023", false);
        science.setGradeCalculator(calculator);
        Assignment scienceTest = createTestAssignment("Science Test", 100);
        science.addAssignment(scienceTest);
        student.enrollInCourse(science);
        student.addGrade(scienceTest, new Grade(95.0, "Excellent"));
        student.completeCourse(science); // Should auto-set final grade
        
        // Verify average GPA (3.0 + 4.0)/2 = 3.5
        assertEquals("GPA should average to 3.5", 3.5, student.calculateGPA(), 0.01);
    }
    // ==================== CLASS AVERAGE TESTS ====================

    /**
     * Test 12: Normal case - Calculate class average
     * Verifies correct class average calculation.
     */
    @Test
    public void testCalculateClassAverage() {
    	// Setup course with calculator
        course.setGradeCalculator(calculator);
        
        // Add assignments (already marked as graded by helper)
        Assignment assignment1 = createTestAssignment("Test 1", 100);
        Assignment assignment2 = createTestAssignment("Test 2", 100);
        course.addAssignment(assignment1);
        course.addAssignment(assignment2);
        
        // Enroll and add grades (80 + 90 = 85 average)
        student.enrollInCourse(course);
        student.addGrade(assignment1, new Grade(80.0, "Good"));
        student.addGrade(assignment2, new Grade(90.0, "Excellent"));
        
        // Verify assignments are graded
        assertTrue("Assignment 1 should be graded", assignment1.isGraded());
        assertTrue("Assignment 2 should be graded", assignment2.isGraded());
        
        double average = student.calculateClassAverage(course);
        assertEquals("Class average should be 85", 85.0, average, 0.01);
    }

    /**
     * Test 13: Edge case - Calculate average for null course
     * Verifies handling of null course in average calculation.
     */
    @Test
    public void testCalculateClassAverageForNullCourse() {
        assertEquals("Should return 0 for null course", 0.0, student.calculateClassAverage(null), 0.01);
    }

    /**
     * Test 14: Edge case - Calculate average for non-enrolled course
     * Verifies handling of non-enrolled course in average calculation.
     */
    @Test
    public void testCalculateClassAverageForNonEnrolledCourse() {
        Course anotherCourse = new Course("Science", "SCI101", "Fall 2023", false);
        assertEquals("Should return 0 for non-enrolled course", 0.0, 
            student.calculateClassAverage(anotherCourse), 0.01);
    }

    /**
     * Test 25: Normal case - Calculate class average with some ungraded assignments
     * Verifies that only graded assignments are included in average calculation.
     */
    @Test
    public void testCalculateClassAverageWithSomeUngradedAssignments() {
        course.setGradeCalculator(calculator);
        
        Assignment graded = createTestAssignment("Graded", 100);
        Assignment ungraded = new Assignment("Ungraded", 100, "2023-10-01", "Test", null);
        ungraded.setGraded(false);
        
        course.addAssignment(graded);
        course.addAssignment(ungraded);
        
        student.enrollInCourse(course);
        student.addGrade(graded, new Grade(90.0, "Good"));
        
        // Verify graded status
        assertTrue("Graded assignment should be marked", graded.isGraded());
        assertFalse("Ungraded assignment should not be marked", ungraded.isGraded());
        
        assertEquals("Should only average graded assignments", 
            90.0, student.calculateClassAverage(course), 0.01);
    }

    // ==================== FINAL GRADE TESTS ====================

    /**
     * Test 15: Normal case - Set and get final grade
     * Verifies final grade storage and retrieval.
     */
    @Test
    public void testSetAndGetFinalGrade() {
        student.enrollInCourse(course);
        student.setFinalGradeForCourse(course, "A");
        assertEquals("Should retrieve set final grade", "A", student.getFinalGradeForCourse(course));
    }

    /**
     * Test 16: Edge case - Get final grade for non-enrolled course
     * Verifies handling of final grade requests for non-enrolled courses.
     */
    @Test
    public void testGetFinalGradeForNonEnrolledCourse() {
        assertNull("Should return null for non-enrolled course", 
            student.getFinalGradeForCourse(course));
    }

    // ==================== GRADE CONVERSION TESTS ====================

    /**
     * Test 17: Normal case - Convert percentage to GPA
     * Verifies correct percentage-to-GPA conversion.
     */
    @Test
    public void testConvertToGPA() {
        assertEquals("95% should convert to 4.0", 4.0, student.convertToGPA(95.0), 0.01);
        assertEquals("85% should convert to 3.0", 3.0, student.convertToGPA(85.0), 0.01);
        assertEquals("75% should convert to 2.0", 2.0, student.convertToGPA(75.0), 0.01);
    }

    /**
     * Test 18: Normal case - Get letter grade from percentage
     * Verifies correct percentage-to-letter-grade conversion.
     */
    @Test
    public void testGetLetterGrade() {
        assertEquals("95% should be A", "A", student.getLetterGrade(95.0));
        assertEquals("85% should be B", "B", student.getLetterGrade(85.0));
        assertEquals("75% should be C", "C", student.getLetterGrade(75.0));
    }

    // ==================== FEEDBACK TESTS ====================

    /**
     * Test 19: Normal case - Get feedback by course
     * Verifies feedback retrieval for a course.
     */
    @Test
    public void testGetFeedbackByCourse() {
        course.setGradeCalculator(calculator);
        Assignment assignment = createTestAssignment("Test", 100);
        course.addAssignment(assignment);
        
        student.enrollInCourse(course);
        Grade grade = new Grade(85.0, "Good work");
        student.addGrade(assignment, grade);
        
        Map<String, String> feedback = student.getFeedbackByCourse(course);
        assertEquals("Should have one feedback entry", 1, feedback.size());
        assertEquals("Feedback should match", "Good work", feedback.get("Test"));
    }

    /**
     * Test 20: Edge case - Get feedback for course with no assignments
     * Verifies feedback retrieval for empty courses.
     */
    @Test
    public void testGetFeedbackForEmptyCourse() {
        student.enrollInCourse(course);
        Map<String, String> feedback = student.getFeedbackByCourse(course);
        assertTrue("Feedback should be empty for course with no assignments", feedback.isEmpty());
    }

    // ==================== PROPERTY CHANGE TESTS ====================

    /**
     * Test 21: Normal case - Property change notification for course enrollment
     * Verifies property change notifications on enrollment.
     */
    @Test
    public void testPropertyChangeNotificationForEnrollment() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                propertyChangeFired = true;
                assertEquals("Event property should match", "courseEnrolled", evt.getPropertyName());
                assertEquals("Event value should be course", course, evt.getNewValue());
            }
        };
        
        student.addPropertyChangeListener(listener);
        student.enrollInCourse(course);
        assertTrue("Property change should be fired", propertyChangeFired);
    }

    /**
     * Test 22: Edge case - Remove property change listener
     * Verifies listener removal functionality.
     */
    @Test
    public void testRemovePropertyChangeListener() {
        PropertyChangeListener listener = evt -> propertyChangeFired = true;
        student.addPropertyChangeListener(listener);
        student.removePropertyChangeListener(listener);
        student.enrollInCourse(course);
        assertFalse("Property change should not be fired after removal", propertyChangeFired);
    }

    // ==================== ROLE TEST ====================

    /**
     * Test 23: Normal case - Verify role inheritance from User
     * Verifies correct role assignment.
     */
    @Test
    public void testGetRole() {
        assertEquals("Student role should be STUDENT", Role.STUDENT, student.getRole());
    }
}