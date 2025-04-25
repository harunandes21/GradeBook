package test;

import model.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

/**
 * Comprehensive test suite for the Assignment class.
 * Tests all functionality including grade management, submission tracking,
 * and assignment properties.
 */
public class AssignmentTest {
    private Assignment assignment;
    private Grade goodGrade;
    private Grade perfectGrade;
    private Grade zeroGrade;

    @Before
    public void setUp() {
        assignment = new Assignment("Math Quiz", 100.0, "2023-10-15", "Quizzes", "Week5");
        goodGrade = new Grade(85.0, "Good work");
        perfectGrade = new Grade(100.0, "Perfect score");
        zeroGrade = new Grade(0.0, "Missing assignment");
    }

    // ==================== CONSTRUCTOR TESTS ====================

    /**
     * Test 1: Normal case - Constructor with valid parameters
     * Verifies all properties are correctly initialized.
     */
    @Test
    public void testConstructorWithValidParameters() {
        assertEquals("Name should match", "Math Quiz", assignment.getName());
        assertEquals("Points worth should match", 100.0, assignment.getPointsWorth(), 0.01);
        assertEquals("Due date should match", "2023-10-15", assignment.getDueDate());
        assertEquals("Category should match", "Quizzes", assignment.getCategoryName());
        assertEquals("Group should match", "Week5", assignment.getGroupName());
        assertFalse("Should not be graded initially", assignment.isGraded());
        assertTrue("Grades map should be empty", assignment.getAllGrades().isEmpty());
    }

    /**
     * Test 2: Edge case - Constructor with empty name
     * Verifies IllegalArgumentException is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() {
        new Assignment("", 100.0, "2023-10-15", "Quizzes", "Week5");
    }

    /**
     * Test 3: Edge case - Constructor with zero points
     * Verifies IllegalArgumentException is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithZeroPoints() {
        new Assignment("Math Quiz", 0.0, "2023-10-15", "Quizzes", "Week5");
    }

    /**
     * Test 4: Edge case - Constructor with null category
     * Verifies IllegalArgumentException is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullCategory() {
        new Assignment("Math Quiz", 100.0, "2023-10-15", null, "Week5");
    }

    // ==================== GRADE MANAGEMENT TESTS ====================

    /**
     * Test 5: Normal case - Add and retrieve grade
     * Verifies grade is properly stored and retrieved.
     */
    @Test
    public void testAddAndGetGrade() {
        assignment.addGrade("student1", goodGrade);
        Grade retrieved = assignment.getGrade("student1");
        assertEquals("Retrieved grade should match", goodGrade, retrieved);
        assertEquals("Points earned should match", 85.0, retrieved.getPointsEarned(), 0.01);
    }

    /**
     * Test 6: Edge case - Add null grade
     * Verifies null grades are not stored.
     */
    @Test
    public void testAddNullGrade() {
        assignment.addGrade("student1", null);
        assertNull("Should not store null grade", assignment.getGrade("student1"));
    }

    /**
     * Test 7: Edge case - Add grade with null username
     * Verifies null usernames are not stored.
     */
    @Test
    public void testAddGradeWithNullUsername() {
        assignment.addGrade(null, goodGrade);
        assertTrue("Grades map should remain empty", assignment.getAllGrades().isEmpty());
    }

    /**
     * Test 8: Complex case - Multiple grades for different students
     * Verifies correct handling of multiple student grades.
     */
    @Test
    public void testMultipleStudentGrades() {
        assignment.addGrade("student1", goodGrade);
        assignment.addGrade("student2", perfectGrade);
        assignment.addGrade("student3", zeroGrade);
        
        Map<String, Grade> allGrades = assignment.getAllGrades();
        assertEquals("Should have 3 grades", 3, allGrades.size());
        assertEquals("Student1 grade should match", goodGrade, allGrades.get("student1"));
        assertEquals("Student2 grade should match", perfectGrade, allGrades.get("student2"));
        assertEquals("Student3 grade should match", zeroGrade, allGrades.get("student3"));
    }

    /**
     * Test 9: Edge case - Get grade for non-existent student
     * Verifies null is returned for unknown students.
     */
    @Test
    public void testGetGradeForNonExistentStudent() {
        assertNull("Should return null for unknown student", assignment.getGrade("unknown"));
    }

    // ==================== GRADED STATUS TESTS ====================

    /**
     * Test 10: Normal case - Mark assignment as graded
     * Verifies graded status can be set.
     */
    @Test
    public void testMarkGraded() {
        assignment.markGraded();
        assertTrue("Should be marked as graded", assignment.isGraded());
    }

    /**
     * Test 11: Normal case - Set graded status
     * Verifies graded status can be toggled.
     */
    @Test
    public void testSetGraded() {
        assignment.setGraded(true);
        assertTrue("Should be set to graded", assignment.isGraded());
        
        assignment.setGraded(false);
        assertFalse("Should be set to not graded", assignment.isGraded());
    }

    // ==================== SUBMISSION TRACKING TESTS ====================

    /**
     * Test 12: Normal case - Check submission exists
     * Verifies correct detection of student submissions.
     */
    @Test
    public void testHasSubmission() {
        assignment.addGrade("student1", goodGrade);
        assertTrue("Should have submission for student1", assignment.hasSubmission("student1"));
        assertFalse("Should not have submission for student2", assignment.hasSubmission("student2"));
    }

    /**
     * Test 13: Edge case - Check submission with null username
     * Verifies handling of null username.
     */
    @Test
    public void testHasSubmissionWithNullUsername() {
        assertFalse("Should return false for null username", assignment.hasSubmission(null));
    }

    // ==================== COMPLEX SCENARIO TESTS ====================

    /**
     * Test 14: Complex case - Grade modification after submission
     * Verifies grade updates don't affect submission tracking.
     */
    @Test
    public void testGradeModificationAfterSubmission() {
        // Initial submission
        assignment.addGrade("student1", goodGrade);
        assertTrue("Should have initial submission", assignment.hasSubmission("student1"));
        
        // Update grade
        Grade updatedGrade = new Grade(90.0, "Improved work");
        assignment.addGrade("student1", updatedGrade);
        
        // Verify updates
        assertEquals("Grade should be updated", updatedGrade, assignment.getGrade("student1"));
        assertTrue("Should still have submission", assignment.hasSubmission("student1"));
    }

    /**
     * Test 15: Complex case - Mixed graded and ungraded assignments
     * Verifies proper handling of graded status with multiple operations.
     */
    @Test
    public void testMixedGradedUngradedOperations() {
        // Start ungraded
        assertFalse("Should start ungraded", assignment.isGraded());
        
        // Add grades but don't mark graded
        assignment.addGrade("student1", goodGrade);
        assertFalse("Should still be ungraded", assignment.isGraded());
        
        // Mark graded
        assignment.markGraded();
        assertTrue("Should now be graded", assignment.isGraded());
        
        // Add another grade
        assignment.addGrade("student2", perfectGrade);
        assertTrue("Should remain graded", assignment.isGraded());
    }

    // ==================== TO STRING TESTS ====================

    /**
     * Test 16: Normal case - toString representation
     * Verifies string representation contains key information.
     */
    @Test
    public void testToString() {
        String str = assignment.toString();
        assertTrue("Should contain name", str.contains("Math Quiz"));
        assertTrue("Should contain points", str.contains("100.0"));
        assertTrue("Should contain category", str.contains("Quizzes"));
        assertTrue("Should contain graded status", str.contains("graded=false"));
    }
}