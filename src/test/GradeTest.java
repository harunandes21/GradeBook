package test;

import model.Grade;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive test suite for the Grade class.
 * Tests all functionality including point validation, feedback,
 * and edge cases.
 */
public class GradeTest {
    private Grade passingGrade;
    private Grade perfectGrade;
    private Grade failingGrade;

    @Before
    public void setUp() {
        passingGrade = new Grade(75.0, "Good effort");
        perfectGrade = new Grade(100.0, "Excellent work!");
        failingGrade = new Grade(45.0, "Needs improvement");
    }

    // ==================== CONSTRUCTOR TESTS ====================

    /**
     * Test 1: Normal case - Constructor with valid parameters
     * Verifies grade is properly initialized with points and feedback.
     */
    @Test
    public void testConstructorWithValidParameters() {
        assertEquals("Points should match", 75.0, passingGrade.getPointsEarned(), 0.01);
        assertEquals("Feedback should match", "Good effort", passingGrade.getFeedback());
    }

    /**
     * Test 2: Edge case - Constructor with minimum points (0)
     * Verifies grade can be created with zero points.
     */
    @Test
    public void testConstructorWithZeroPoints() {
        Grade zeroGrade = new Grade(0.0, "Missing assignment");
        assertEquals("Points should be 0", 0.0, zeroGrade.getPointsEarned(), 0.01);
    }

    /**
     * Test 3: Edge case - Constructor with null feedback
     * Verifies grade can be created with null feedback.
     */
    @Test
    public void testConstructorWithNullFeedback() {
        Grade nullFeedbackGrade = new Grade(80.0, null);
        assertNull("Feedback should be null", nullFeedbackGrade.getFeedback());
    }

    /**
     * Test 4: Edge case - Constructor with negative points
     * Verifies IllegalArgumentException is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegativePoints() {
        new Grade(-5.0, "Invalid points");
    }

    // ==================== POINTS EARNED TESTS ====================

    /**
     * Test 5: Normal case - Set valid points
     * Verifies points can be updated to valid values.
     */
    @Test
    public void testSetPointsEarned() {
        passingGrade.setPointsEarned(85.0);
        assertEquals("Points should be updated", 85.0, passingGrade.getPointsEarned(), 0.01);
    }

    /**
     * Test 6: Edge case - Set points to zero
     * Verifies zero points are accepted.
     */
    @Test
    public void testSetPointsToZero() {
        passingGrade.setPointsEarned(0.0);
        assertEquals("Points should be 0", 0.0, passingGrade.getPointsEarned(), 0.01);
    }

    /**
     * Test 7: Edge case - Set negative points
     * Verifies IllegalArgumentException is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativePoints() {
        passingGrade.setPointsEarned(-10.0);
    }

    // ==================== FEEDBACK TESTS ====================

    /**
     * Test 8: Normal case - Set feedback
     * Verifies feedback can be updated.
     */
    @Test
    public void testSetFeedback() {
        passingGrade.setFeedback("Improved work");
        assertEquals("Feedback should be updated", "Improved work", passingGrade.getFeedback());
    }

    /**
     * Test 9: Edge case - Set null feedback
     * Verifies feedback can be set to null.
     */
    @Test
    public void testSetNullFeedback() {
        passingGrade.setFeedback(null);
        assertNull("Feedback should be null", passingGrade.getFeedback());
    }

    /**
     * Test 10: Edge case - Set empty feedback
     * Verifies empty string feedback is accepted.
     */
    @Test
    public void testSetEmptyFeedback() {
        passingGrade.setFeedback("");
        assertEquals("Feedback should be empty", "", passingGrade.getFeedback());
    }

    // ==================== TO STRING TESTS ====================

    /**
     * Test 11: Normal case - toString representation
     * Verifies string contains both points and feedback.
     */
    @Test
    public void testToString() {
        String str = passingGrade.toString();
        assertTrue("Should contain points", str.contains("75.0"));
        assertTrue("Should contain feedback", str.contains("Good effort"));
    }

    /**
     * Test 12: Edge case - toString with null feedback
     * Verifies proper handling of null feedback in string representation.
     */
    @Test
    public void testToStringWithNullFeedback() {
        Grade grade = new Grade(90.0, null);
        String str = grade.toString();
        assertTrue("Should contain points", str.contains("90.0"));
        assertTrue("Should indicate null feedback", str.contains("null"));
    }

    // ==================== COMPLEX SCENARIO TESTS ====================

    /**
     * Test 13: Complex case - Multiple operations on grade
     * Verifies grade remains consistent after multiple updates.
     */
    @Test
    public void testMultipleGradeOperations() {
        // Initial state
        assertEquals("Initial points should match", 75.0, passingGrade.getPointsEarned(), 0.01);
        
        // Update points
        passingGrade.setPointsEarned(80.0);
        assertEquals("Points should update", 80.0, passingGrade.getPointsEarned(), 0.01);
        
        // Update feedback
        passingGrade.setFeedback("Revised feedback");
        assertEquals("Feedback should update", "Revised feedback", passingGrade.getFeedback());
        
        // Verify toString
        String str = passingGrade.toString();
        assertTrue("Should contain updated points", str.contains("80.0"));
        assertTrue("Should contain updated feedback", str.contains("Revised feedback"));
    }

    /**
     * Test 14: Complex case - Grade comparison
     * Verifies grades can be compared by points earned.
     */
    @Test
    public void testGradeComparison() {
        assertTrue("Passing grade should be greater than failing", 
                  passingGrade.getPointsEarned() > failingGrade.getPointsEarned());
        assertTrue("Perfect grade should be greatest", 
                  perfectGrade.getPointsEarned() > passingGrade.getPointsEarned());
    }
}