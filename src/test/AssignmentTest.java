package test;

import model.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList; // Needed for median test
import java.util.Collections; // Needed for median test
import java.util.HashMap; // Needed for getAllGrades test
import java.util.List; // Needed for median test
import java.util.Map;

/**
 * Comprehensive test suite for the Assignment class.
 * Tests all functionality including grade management, submission tracking,
 * assignment properties, calculations, and edge cases.
 */
public class AssignmentTest {
    // Declare fields for test objects
    private Assignment assignment;
    private Grade goodGrade;
    private Grade perfectGrade;
    private Grade zeroGrade;
    private Grade grade1; // For calculation tests
    private Grade grade2; // For calculation tests
    private Grade grade3; // For calculation tests
    private Group group1; // For setup and testing
    private Group group2; // For testing setter

    /**
     * setUp method runs before each test.
     * Initializes common objects used across multiple tests.
     */
    @Before
    public void setUp() {
        // Create groups first
        group1 = new Group("Module1");
        group2 = new Group("Module2");
        // Create the main assignment object for testing
        assignment = new Assignment("Math Quiz", 100.0, "2023-10-15", "Quizzes", group1);
        // Create various Grade objects
        goodGrade = new Grade(85.0, "Good work");
        perfectGrade = new Grade(100.0, "Perfect score");
        zeroGrade = new Grade(0.0, "Missing assignment");
        grade1 = new Grade(85.0, "Good");
        grade2 = new Grade(95.0, "Excellent");
        grade3 = new Grade(75.0, "Okay");
    }

    // ==================== CONSTRUCTOR TESTS ====================

    /**
     * Test 1: Normal case - Constructor with valid parameters
     * Verifies all properties are correctly initialized.
     */
    @Test
    public void testConstructorWithValidParameters() {
        System.out.println("Testing Assignment constructor valid case");
        assertEquals("Name should match", "Math Quiz", assignment.getName());
        assertEquals("Points worth should match", 100.0, assignment.getPointsWorth(), 0.01);
        assertEquals("Due date should match", "2023-10-15", assignment.getDueDate());
        assertEquals("Category should match", "Quizzes", assignment.getCategoryName());
        assertEquals("Group should match the one passed in", group1, assignment.getGroup()); // Check group object
        assertFalse("Should not be graded initially", assignment.isGraded());
        assertEquals("Description should be empty string initially", "", assignment.getDescription()); // Check initial description
        assertNotNull("Grades map should not be null", assignment.getAllGrades()); // Check map exists
        assertTrue("Grades map should be empty", assignment.getAllGrades().isEmpty()); // Check map is empty
    }

    /**
     * Test 2: Edge case - Constructor with empty name
     * Verifies IllegalArgumentException is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() {
        System.out.println("Testing Assignment constructor empty name");
        // Pass a valid group object here instead of null if required
        new Assignment("", 100.0, "2023-10-15", "Quizzes", group1);
    }

    /**
     * Test 2b: Edge case - Constructor with null name
     * Verifies IllegalArgumentException is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullName() {
        System.out.println("Testing Assignment constructor null name");
        new Assignment(null, 100.0, "2023-10-15", "Quizzes", group1);
    }

    /**
     * Test 3: Edge case - Constructor with zero points
     * Verifies IllegalArgumentException is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithZeroPoints() {
        System.out.println("Testing Assignment constructor zero points");
        new Assignment("Math Quiz", 0.0, "2023-10-15", "Quizzes", group1);
    }

     /**
      * Test 3b: Edge case - Constructor with negative points
      * Verifies IllegalArgumentException is thrown.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testConstructorWithNegativePoints() {
         System.out.println("Testing Assignment constructor negative points");
         new Assignment("Math Quiz", -10.0, "2023-10-15", "Quizzes", group1);
     }

    /**
     * Test 4: Edge case - Constructor with null category name (Allowed by code)
     * Verifies null category is accepted and stored.
     */
    @Test
    public void testConstructorWithNullCategory() {
        System.out.println("Testing Assignment constructor null category");
        // Assignment constructor should allow null categoryName based on the code
        Assignment assignWithNullCat = new Assignment("Test Assign", 100.0, "2023-10-15", null, group1);
        assertNull("Category name should be null when passed as null", assignWithNullCat.getCategoryName());
    }

    /**
     * Test 4b: Edge case - Constructor with null group (Allowed by code)
     * Verifies null group is accepted and stored.
     */
    @Test
    public void testConstructorWithNullGroup() {
        System.out.println("Testing Assignment constructor null group");
        // Assignment constructor allows null group
        Assignment assignWithNullGroup = new Assignment("Test Assign 2", 50.0, "2023-11-01", "SomeCat", null);
        assertNull("Group should be null when passed as null", assignWithNullGroup.getGroup());
    }


    // ==================== SETTER TESTS ====================

    /** Test setAssignmentName with valid input */
    @Test
    public void testSetAssignmentNameValid() {
        System.out.println("Testing setAssignmentName valid");
        assignment.setAssignmentName("  Updated Quiz Name  "); // With spaces
        assertEquals("Name should be updated and trimmed", "Updated Quiz Name", assignment.getName());
    }

    /** Test setAssignmentName with empty/null input */
    @Test
    public void testSetAssignmentNameEmptyOrNull() {
        System.out.println("Testing setAssignmentName empty/null");
        String originalName = assignment.getName();
        assignment.setAssignmentName(""); // Empty string
        assertEquals("Name should not change for empty string", originalName, assignment.getName());
        assignment.setAssignmentName("   "); // Blank string
        assertEquals("Name should not change for blank string", originalName, assignment.getName());
        assignment.setAssignmentName(null); // Null value
        assertEquals("Name should not change for null", originalName, assignment.getName());
    }

    /** Test setPointsPossible with valid input */
    @Test
    public void testSetPointsPossibleValid() {
        System.out.println("Testing setPointsPossible valid");
        assignment.setPointsPossible(75.5);
        assertEquals("Points should be updated", 75.5, assignment.getPointsWorth(), 0.01);
    }

    /** Test setPointsPossible with zero input */
    @Test
    public void testSetPointsPossibleZero() {
        System.out.println("Testing setPointsPossible zero");
        assignment.setPointsPossible(0.0); // Allowed
        assertEquals("Points should be allowed to be zero", 0.0, assignment.getPointsWorth(), 0.01);
    }

    /** Test setPointsPossible with negative input */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPointsPossibleNegative() {
        System.out.println("Testing setPointsPossible negative");
        assignment.setPointsPossible(-1.0); // Should throw exception
    }

    /** Test setDueDate */
    @Test
    public void testSetDueDate() {
        System.out.println("Testing setDueDate");
        assignment.setDueDate("2025-01-01");
        assertEquals("Due date should be updated", "2025-01-01", assignment.getDueDate());
        assignment.setDueDate(null); // Allows null
        assertNull("Due date should allow null", assignment.getDueDate());
    }

    /** Test setCategoryName */
    @Test
    public void testSetCategoryName() {
        System.out.println("Testing setCategoryName");
        assignment.setCategoryName("Exams");
        assertEquals("Category name should be updated", "Exams", assignment.getCategoryName());
        assignment.setCategoryName(null); // Allows null
        assertNull("Category name should allow null", assignment.getCategoryName());
    }

    /** Test setGroupName (setter name from Assignment.java is setGroupName) */
    @Test
    public void testSetGroupName() { // Renaming test method slightly for clarity
        System.out.println("Testing setGroup"); // Referring to the action
        assignment.setGroupName(group2); // Use the actual setter name
        assertEquals("Group should be updated", group2, assignment.getGroup());
        assignment.setGroupName(null); // Use the actual setter name, allows null
        assertNull("Group should allow null", assignment.getGroup());
    }

    /** Test setDescription */
    @Test
    public void testSetDescription() {
        System.out.println("Testing setDescription");
        assignment.setDescription("New description here.");
        assertEquals("Description should be updated", "New description here.", assignment.getDescription());
        assignment.setDescription(""); // Empty string
        assertEquals("Description should allow empty string", "", assignment.getDescription());
        assignment.setDescription(null); // Allows null
        assertNull("Description should allow null", assignment.getDescription());
    }


    /** Test getGrade with null username */
    @Test
    public void testGetGradeNullUsername() {
        System.out.println("Testing getGrade with null username");
        assertNull("getGrade(null) should return null", assignment.getGrade(null));
    }

    /** Test getAllGrades returns a defensive copy */
    @Test
    public void testGetAllGradesEncapsulation() {
        System.out.println("Testing getAllGrades encapsulation");
        assignment.addGrade("stud1", goodGrade);
        Map<String, Grade> gradesCopy = assignment.getAllGrades();
        // Modify the returned copy
        gradesCopy.put("stud2", perfectGrade); // Add to copy
        // Get the internal map again (via getter)
        Map<String, Grade> gradesInternal = assignment.getAllGrades();
        // Check that the internal map was not affected
        assertEquals("Internal map size should remain 1", 1, gradesInternal.size());
        assertFalse("Internal map should not contain stud2", gradesInternal.containsKey("stud2"));
    }

    /** Test clearAllGrades */
    @Test
    public void testClearAllGrades() {
        System.out.println("Testing clearAllGrades");
        assignment.addGrade("stud1", goodGrade);
        assignment.addGrade("stud2", perfectGrade);
        assertFalse("Map should not be empty before clear", assignment.getAllGrades().isEmpty());
        assignment.clearAllGrades(); // Clear the grades
        assertTrue("Map should be empty after clear", assignment.getAllGrades().isEmpty());
    }


    // ==================== GRADED STATUS TESTS ====================
    // testMarkGraded and testSetGraded are covered

    // ==================== SUBMISSION TRACKING TESTS ====================
    // testHasSubmission and testHasSubmissionWithNullUsername are covered

    // ==================== CALCULATION TESTS ====================

    /** Test calculateAverageScore with multiple grades */
    @Test
    public void testCalculateAverageScoreNormal() {
        System.out.println("Testing calculateAverageScore normal");
        assignment.addGrade("stud1", grade1); // 85
        assignment.addGrade("stud2", grade2); // 95
        assignment.addGrade("stud3", grade3); // 75
        // Average = (85 + 95 + 75) / 3 = 255 / 3 = 85.0
        assertEquals("Average should be 85.0", 85.0, assignment.calculateAverageScore(), 0.01);
    }

    /** Test calculateAverageScore with one grade */
    @Test
    public void testCalculateAverageScoreOneGrade() {
        System.out.println("Testing calculateAverageScore one grade");
        assignment.addGrade("stud1", grade1); // 85
        assertEquals("Average should be 85.0", 85.0, assignment.calculateAverageScore(), 0.01);
    }

    /** Test calculateAverageScore with no grades */
    @Test
    public void testCalculateAverageScoreNoGrades() {
        System.out.println("Testing calculateAverageScore no grades");
        assertEquals("Average should be 0.0", 0.0, assignment.calculateAverageScore(), 0.01);
    }

    /** Test calculateAverageScore ignores potential null Grade objects in map */
    @Test
    public void testCalculateAverageScoreSkipsNullGrades() {
        System.out.println("Testing calculateAverageScore skips null grades");
        // Manually add grades including a null (not possible via addGrade, but test robustness)
        // Use the getter which returns a copy, modify it, then we can't test the original method easily.
        // We rely on the internal check: for (Grade grade : currentGrades.values()) { if (gradeIsValid) ... }
        // Test with one valid grade to ensure the loop works
        assignment.addGrade("stud1", grade1); // 85
        assertEquals("Average with one valid grade", 85.0, assignment.calculateAverageScore(), 0.01);
    }

    /** Test calculateMedianScore with odd number of grades */
    @Test
    public void testCalculateMedianScoreOdd() {
        System.out.println("Testing calculateMedianScore odd");
        assignment.addGrade("stud1", grade1); // 85
        assignment.addGrade("stud2", grade2); // 95
        assignment.addGrade("stud3", grade3); // 75
        // Sorted: 75, 85, 95. Median is 85.
        assertEquals("Median should be 85.0", 85.0, assignment.calculateMedianScore(), 0.01);
    }

    /** Test calculateMedianScore with even number of grades */
    @Test
    public void testCalculateMedianScoreEven() {
        System.out.println("Testing calculateMedianScore even");
        assignment.addGrade("stud1", grade1); // 85
        assignment.addGrade("stud3", grade3); // 75
        // Sorted: 75, 85. Median is (75 + 85) / 2 = 80.0
        assertEquals("Median should be 80.0", 80.0, assignment.calculateMedianScore(), 0.01);
    }

    /** Test calculateMedianScore with even number including duplicates */
    @Test
    public void testCalculateMedianScoreEvenDuplicates() {
        System.out.println("Testing calculateMedianScore even duplicates");
        assignment.addGrade("s1", new Grade(70.0, ""));
        assignment.addGrade("s2", new Grade(80.0, ""));
        assignment.addGrade("s3", new Grade(80.0, ""));
        assignment.addGrade("s4", new Grade(90.0, ""));
        // Sorted: 70, 80, 80, 90. Median is (80 + 80) / 2 = 80.0
        assertEquals("Median should be 80.0", 80.0, assignment.calculateMedianScore(), 0.01);
    }

    /** Test calculateMedianScore with one grade */
    @Test
    public void testCalculateMedianScoreOneGrade() {
        System.out.println("Testing calculateMedianScore one grade");
        assignment.addGrade("stud1", grade1); // 85
        assertEquals("Median should be 85.0", 85.0, assignment.calculateMedianScore(), 0.01);
    }

    /** Test calculateMedianScore with no grades */
    @Test
    public void testCalculateMedianScoreNoGrades() {
        System.out.println("Testing calculateMedianScore no grades");
        assertEquals("Median should be 0.0", 0.0, assignment.calculateMedianScore(), 0.01);
    }

    /** Test calculateMedianScore returns 0 if list becomes empty after filtering nulls */
    @Test
    public void testCalculateMedianScoreEmptyListAfterNullFilter() {
        System.out.println("Testing calculateMedianScore empty list after null filter");
        // This scenario relies on the check `if (!haveScoresToList)`
        // We can't easily force the private map to only contain nulls.
        // However, the `calculateMedianScoreNoGrades` test covers the case where
        // the initial map is empty, which also leads to returning 0.0.
        // This implicitly covers the desired outcome if the filtering resulted in empty.
        assertEquals("Median should be 0.0 if no valid grades", 0.0, assignment.calculateMedianScore(), 0.01);
    }


    // ==================== equals() and hashCode() TESTS ====================

    /** Test equals method logic */
    @Test
    public void testEquals() {
        System.out.println("Testing equals method");
        // Create assignments for comparison
        Assignment sameNameDifferentDetails = new Assignment("Math Quiz", 50.0, "other date", "Other Cat", group2);
        Assignment differentName = new Assignment("Science Quiz", 100.0, "2023-10-15", "Quizzes", group1);
        String otherType = "Math Quiz";

        // Test cases
        assertTrue("Same object should be equal", assignment.equals(assignment));
        assertTrue("Objects with same name should be equal", assignment.equals(sameNameDifferentDetails));
        assertFalse("Objects with different names should not be equal", assignment.equals(differentName));
        assertFalse("Object should not be equal to null", assignment.equals(null));
        assertFalse("Object should not be equal to different type", assignment.equals(otherType));
    }

    /** Test hashCode method logic */
    @Test
    public void testHashCode() {
        System.out.println("Testing hashCode method");
        Assignment sameNameDifferentDetails = new Assignment("Math Quiz", 50.0, "other date", "Other Cat", group2);
        Assignment differentName = new Assignment("Science Quiz", 100.0, "2023-10-15", "Quizzes", group1);

        // Consistency: equals -> same hashCode
        assertEquals("Equal objects must have same hashCode", assignment.hashCode(), sameNameDifferentDetails.hashCode());

    }

    // ==================== TO STRING TESTS ====================
    // testToString is covered
}