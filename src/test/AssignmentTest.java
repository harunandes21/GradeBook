package test;

import model.Assignment;
import model.Grade; // Need Grade for testing grades map

import static org.junit.jupiter.api.Assertions.*; // Use JUnit 5 assertions
import org.junit.jupiter.api.BeforeEach; // For setup method run before each test
import org.junit.jupiter.api.Test; // For marking test methods

import java.util.Map;
import java.util.HashMap; // For creating test grade map

/**
 * This tests the Assignment class from the model package.
 * Tests constructor, getters, setters, grade management,
 * calculations like average/median, and other methods.
 */
class AssignmentTest {

    // Declare variables for test objects
    private Assignment assignment;
    private Grade grade1;
    private Grade grade2;
    private Grade grade3;

    /**
     * setUp runs before each test method.
     * Makes a fresh assignment object and some grade objects.
     */
    @BeforeEach
    void setUp() {
        // Default assignment for most tests
        assignment = new Assignment("HW 1", 100.0, "2024-10-01", "Homework", "Group A");
        // Some grades to use
        grade1 = new Grade(85.0, "Good");
        grade2 = new Grade(95.0, "Excellent");
        grade3 = new Grade(75.0, "Okay");
    }

    // ==================== Constructor Tests ====================

    /**
     * testConstructorValid tests normal creation.
     * Makes sure all the fields get set right from the constructor.
     */
    @Test
    void testConstructorValid() {
        System.out.println("Testing Assignment constructor valid case");
        assertEquals("HW 1", assignment.getName(), "Name check");
        assertEquals(100.0, assignment.getPointsWorth(), 0.01, "Points check");
        assertEquals("2024-10-01", assignment.getDueDate(), "Due date check");
        assertEquals("Homework", assignment.getCategoryName(), "Category check");
        assertEquals("Group A", assignment.getGroupName(), "Group check");
        assertFalse(assignment.isGraded(), "Should start ungraded");
        assertNotNull(assignment.getAllGrades(), "Grades map should exist");
        assertTrue(assignment.getAllGrades().isEmpty(), "Grades map should be empty");
    }

    /**
     * testConstructorNullName checks if it stops null name.
     * Expects IllegalArgumentException.
     */
    @Test
    void testConstructorNullName() {
        System.out.println("Testing Assignment constructor null name");
        assertThrows(IllegalArgumentException.class, () -> {
            new Assignment(null, 100.0, "d", "c", "g");
        }, "Constructor should throw error for null name");
    }

    /**
     * testConstructorEmptyName checks if it stops empty name.
     * Expects IllegalArgumentException.
     */
    @Test
    void testConstructorEmptyName() {
        System.out.println("Testing Assignment constructor empty name");
        assertThrows(IllegalArgumentException.class, () -> {
            new Assignment("   ", 100.0, "d", "c", "g");
        }, "Constructor should throw error for empty name");
    }

    /**
     * testConstructorZeroPoints checks if it stops zero points.
     * Expects IllegalArgumentException.
     */
    @Test
    void testConstructorZeroPoints() {
        System.out.println("Testing Assignment constructor zero points");
        assertThrows(IllegalArgumentException.class, () -> {
            new Assignment("Test", 0.0, "d", "c", "g");
        }, "Constructor should throw error for zero points");
    }

    /**
     * testConstructorNegativePoints checks if it stops negative points.
     * Expects IllegalArgumentException.
     */
    @Test
    void testConstructorNegativePoints() {
        System.out.println("Testing Assignment constructor negative points");
        assertThrows(IllegalArgumentException.class, () -> {
            new Assignment("Test", -10.0, "d", "c", "g");
        }, "Constructor should throw error for negative points");
    }

    // Note: Constructor doesn't check categoryName null based on provided code, so no test for that needed unless model changes.

    // ==================== Setter Tests ====================

    /**
     * testSetAssignmentName checks updating the name.
     */
    @Test
    void testSetAssignmentName() {
        System.out.println("Testing Assignment setAssignmentName");
        assignment.setAssignmentName("Homework One");
        assertEquals("Homework One", assignment.getName(), "Name should be updated");
    }

    /**
     * testSetAssignmentNameEmpty checks setting empty name is ignored.
     */
    @Test
    void testSetAssignmentNameEmpty() {
        System.out.println("Testing Assignment setAssignmentName empty");
        assignment.setAssignmentName("   "); // Try setting empty
        assertEquals("HW 1", assignment.getName(), "Name should not change if set to empty");
    }

    /**
     * testSetAssignmentNameNull checks setting null name is ignored.
     */
    @Test
    void testSetAssignmentNameNull() {
        System.out.println("Testing Assignment setAssignmentName null");
        assignment.setAssignmentName(null); // Try setting null
        assertEquals("HW 1", assignment.getName(), "Name should not change if set to null");
    }

    /**
     * testSetPointsPossible checks updating points.
     */
    @Test
    void testSetPointsPossible() {
        System.out.println("Testing Assignment setPointsPossible");
        assignment.setPointsPossible(90.0);
        assertEquals(90.0, assignment.getPointsWorth(), 0.01, "Points should be updated");
    }

    /**
     * testSetPointsPossibleZero checks setting points to zero is allowed.
     */
    @Test
    void testSetPointsPossibleZero() {
        System.out.println("Testing Assignment setPointsPossible zero");
        assignment.setPointsPossible(0.0); // Zero points is allowed maybe for extra credit?
        assertEquals(0.0, assignment.getPointsWorth(), 0.01, "Points should be allowed to be zero");
    }

    /**
     * testSetPointsPossibleNegative checks setting negative points fails.
     * Expects IllegalArgumentException.
     */
    @Test
    void testSetPointsPossibleNegative() {
        System.out.println("Testing Assignment setPointsPossible negative");
        assertThrows(IllegalArgumentException.class, () -> {
            assignment.setPointsPossible(-5.0);
        }, "Setter should throw error for negative points");
    }

    /**
     * testSetDueDate checks updating due date string.
     */
    @Test
    void testSetDueDate() {
        System.out.println("Testing Assignment setDueDate");
        assignment.setDueDate("2024-11-15");
        assertEquals("2024-11-15", assignment.getDueDate(), "Due date should be updated");
    }

    /**
     * testSetDueDateNull checks setting due date to null.
     */
    @Test
    void testSetDueDateNull() {
        System.out.println("Testing Assignment setDueDate null");
        assignment.setDueDate(null);
        assertNull(assignment.getDueDate(), "Due date should be allowed to be null");
    }

    /**
     * testSetCategoryName checks updating category name string.
     */
    @Test
    void testSetCategoryName() {
        System.out.println("Testing Assignment setCategoryName");
        assignment.setCategoryName("Labs");
        assertEquals("Labs", assignment.getCategoryName(), "Category name should be updated");
    }

    /**
     * testSetCategoryNameNull checks setting category name to null.
     */
    @Test
    void testSetCategoryNameNull() {
        System.out.println("Testing Assignment setCategoryName null");
        assignment.setCategoryName(null);
        assertNull(assignment.getCategoryName(), "Category name should be allowed to be null");
    }

    /**
     * testSetGroupName checks updating group name string.
     */
    @Test
    void testSetGroupName() {
        System.out.println("Testing Assignment setGroupName");
        assignment.setGroupName("Group B");
        assertEquals("Group B", assignment.getGroupName(), "Group name should be updated");
    }

    /**
     * testSetGroupNameNull checks setting group name to null.
     */
    @Test
    void testSetGroupNameNull() {
        System.out.println("Testing Assignment setGroupName null");
        assignment.setGroupName(null);
        assertNull(assignment.getGroupName(), "Group name should be allowed to be null");
    }

    /**
     * testSetGraded checks setting graded status true and false.
     */
    @Test
    void testSetGraded() {
        System.out.println("Testing Assignment setGraded");
        assignment.setGraded(true);
        assertTrue(assignment.isGraded(), "Should be graded after setGraded(true)");
        assignment.setGraded(false);
        assertFalse(assignment.isGraded(), "Should be ungraded after setGraded(false)");
    }

    /**
     * testMarkGraded checks the helper method markGraded.
     */
    @Test
    void testMarkGraded() {
        System.out.println("Testing Assignment markGraded");
        assignment.markGraded();
        assertTrue(assignment.isGraded(), "Should be graded after markGraded()");
    }

    /**
     * testSetDescription checks updating the description.
     */
    @Test
    void testSetDescription() {
        System.out.println("Testing Assignment setDescription");
        assignment.setDescription("New description text.");
        assertEquals("New description text.", assignment.getDescription(), "Description should be updated");
    }

    // ==================== Grade Management Tests ====================

    /**
     * testAddGetGrade checks adding and getting a single grade.
     */
    @Test
    void testAddGetGrade() {
        System.out.println("Testing Assignment addGrade and getGrade");
        assignment.addGrade("stud1", grade1);
        assertEquals(grade1, assignment.getGrade("stud1"), "Should get back the grade added");
    }

    /**
     * testGetGradeNotFound checks getting grade for student who doesnt have one.
     */
    @Test
    void testGetGradeNotFound() {
        System.out.println("Testing Assignment getGrade for non-existent grade");
        assertNull(assignment.getGrade("stud1"), "Should return null if no grade for student");
    }

    /**
     * testGetGradeNullUsername checks getting grade with null username.
     */
    @Test
    void testGetGradeNullUsername() {
        System.out.println("Testing Assignment getGrade with null username");
        assertNull(assignment.getGrade(null), "Should return null if username is null");
    }

     /**
      * testAddGradeNullUsername checks adding grade with null username is ignored.
      */
     @Test
     void testAddGradeNullUsername() {
         System.out.println("Testing Assignment addGrade with null username");
         assignment.addGrade(null, grade1);
         assertTrue(assignment.getAllGrades().isEmpty(), "Grade should not be added with null username");
     }

     /**
      * testAddGradeNullGrade checks adding null grade is ignored.
      */
     @Test
     void testAddGradeNullGrade() {
         System.out.println("Testing Assignment addGrade with null grade");
         assignment.addGrade("stud1", null);
         assertFalse(assignment.hasSubmission("stud1"), "Null grade should not count as submission");
         assertTrue(assignment.getAllGrades().isEmpty(), "Grades map should still be empty");
     }

    /**
     * testHasSubmission checks if student has submitted based on grade entry.
     */
    @Test
    void testHasSubmission() {
        System.out.println("Testing Assignment hasSubmission");
        assertFalse(assignment.hasSubmission("stud1"), "Should be false initially");
        assignment.addGrade("stud1", grade1);
        assertTrue(assignment.hasSubmission("stud1"), "Should be true after adding grade");
    }

    /**
     * testHasSubmissionNullUsername checks hasSubmission with null username.
     */
    @Test
    void testHasSubmissionNullUsername() {
        System.out.println("Testing Assignment hasSubmission with null username");
        assertFalse(assignment.hasSubmission(null), "hasSubmission should return false for null username");
    }

    /**
     * testGetAllGrades checks getting the map of grades.
     * Also tests encapsulation by trying to modify the returned map.
     */
    @Test
    void testGetAllGrades() {
        System.out.println("Testing Assignment getAllGrades and encapsulation");
        // Add some grades
        assignment.addGrade("stud1", grade1);
        assignment.addGrade("stud2", grade2);
        // Get the map
        Map<String, Grade> gradesCopy = assignment.getAllGrades();
        // Check size
        assertEquals(2, gradesCopy.size(), "Returned map should have 2 grades");
        // Try modifying the copy
        gradesCopy.put("stud3", grade3);
        // Get the map again from the original assignment
        Map<String, Grade> gradesOriginal = assignment.getAllGrades();
        // Check original size is still 2, proving copy was returned
        assertEquals(2, gradesOriginal.size(), "Original map size should remain 2 after modifying copy");
        assertFalse(gradesOriginal.containsKey("stud3"), "Original map should not contain stud3");
    }

    /**
     * testClearAllGrades checks if it removes all grades.
     */
    @Test
    void testClearAllGrades() {
        System.out.println("Testing Assignment clearAllGrades");
        // Add grades
        assignment.addGrade("stud1", grade1);
        assignment.addGrade("stud2", grade2);
        assertEquals(2, assignment.getAllGrades().size(), "Should have 2 grades initially");
        // Clear them
        assignment.clearAllGrades();
        // Check map is now empty
        assertTrue(assignment.getAllGrades().isEmpty(), "Grades map should be empty after clearAllGrades");
    }

    // ==================== Calculation Tests ====================

    /**
     * testCalculateAverageScore checks average calculation.
     * Grades: 85, 95, 75. Average = (85+95+75)/3 = 255/3 = 85.0
     */
    @Test
    void testCalculateAverageScore() {
        System.out.println("Testing Assignment calculateAverageScore");
        assignment.addGrade("stud1", grade1); // 85
        assignment.addGrade("stud2", grade2); // 95
        assignment.addGrade("stud3", grade3); // 75
        double average = assignment.calculateAverageScore();
        assertEquals(85.0, average, 0.01, "Average should be 85.0");
    }

    /**
     * testCalculateAverageScore_OneGrade checks average with only one grade.
     */
    @Test
    void testCalculateAverageScore_OneGrade() {
        System.out.println("Testing Assignment calculateAverageScore with one grade");
        assignment.addGrade("stud1", grade1); // 85
        double average = assignment.calculateAverageScore();
        assertEquals(85.0, average, 0.01, "Average should be 85.0 with one grade");
    }

    /**
     * testCalculateAverageScore_NoGrades checks average with no grades.
     */
    @Test
    void testCalculateAverageScore_NoGrades() {
        System.out.println("Testing Assignment calculateAverageScore with no grades");
        double average = assignment.calculateAverageScore();
        assertEquals(0.0, average, 0.01, "Average should be 0.0 with no grades");
    }

    /**
     * testCalculateMedianScore_Odd checks median with odd number of grades.
     * Grades: 75, 85, 95. Sorted: 75, 85, 95. Median = 85.
     */
    @Test
    void testCalculateMedianScore_Odd() {
        System.out.println("Testing Assignment calculateMedianScore odd number");
        assignment.addGrade("stud1", grade1); // 85
        assignment.addGrade("stud2", grade2); // 95
        assignment.addGrade("stud3", grade3); // 75
        double median = assignment.calculateMedianScore();
        assertEquals(85.0, median, 0.01, "Median should be 85.0");
    }

    /**
     * testCalculateMedianScore_Even checks median with even number of grades.
     * Grades: 75, 85. Sorted: 75, 85. Median = (75+85)/2 = 80.
     */
    @Test
    void testCalculateMedianScore_Even() {
        System.out.println("Testing Assignment calculateMedianScore even number");
        assignment.addGrade("stud1", grade1); // 85
        assignment.addGrade("stud3", grade3); // 75
        double median = assignment.calculateMedianScore();
        assertEquals(80.0, median, 0.01, "Median should be 80.0");
    }

    /**
     * testCalculateMedianScore_OneGrade checks median with only one grade.
     */
    @Test
    void testCalculateMedianScore_OneGrade() {
        System.out.println("Testing Assignment calculateMedianScore with one grade");
        assignment.addGrade("stud1", grade1); // 85
        double median = assignment.calculateMedianScore();
        assertEquals(85.0, median, 0.01, "Median should be 85.0 with one grade");
    }

    /**
     * testCalculateMedianScore_NoGrades checks median with no grades.
     */
    @Test
    void testCalculateMedianScore_NoGrades() {
        System.out.println("Testing Assignment calculateMedianScore with no grades");
        double median = assignment.calculateMedianScore();
        assertEquals(0.0, median, 0.01, "Median should be 0.0 with no grades");
    }

    // ==================== equals and hashCode Tests ====================

    /**
     * testEquals checks equals method based on name.
     */
    @Test
    void testEquals() {
        System.out.println("Testing Assignment equals method");
        Assignment assignSameName = new Assignment("HW 1", 50.0, "d", "c", "g");
        Assignment assignDiffName = new Assignment("HW 2", 100.0, "d", "c", "g");
        Assignment assignNull = null;
        String notAnAssignment = "HW 1";

        // Same object
        assertTrue(assignment.equals(assignment), "Same object should be equal");
        // Same name, different object
        assertTrue(assignment.equals(assignSameName), "Objects with same name should be equal");
        // Different name
        assertFalse(assignment.equals(assignDiffName), "Objects with different name should not be equal");
        // Null object
        assertFalse(assignment.equals(assignNull), "Object should not be equal to null");
        // Different type
        assertFalse(assignment.equals(notAnAssignment), "Object should not be equal to different type");
    }

    /**
     * testHashCode checks hashcode consistency with equals.
     * Objects equal according to equals() must have same hashcode.
     */
    @Test
    void testHashCode() {
        System.out.println("Testing Assignment hashCode method");
        Assignment assignSameName = new Assignment("HW 1", 50.0, "d", "c", "g");
        Assignment assignDiffName = new Assignment("HW 2", 100.0, "d", "c", "g");

        // Equal objects must have same hashcode
        assertEquals(assignment.hashCode(), assignSameName.hashCode(), "Equal objects must have same hashcode");
        // Unequal objects should ideally have different hashcodes not strictly required but good practice
        assertNotEquals(assignment.hashCode(), assignDiffName.hashCode(), "Unequal objects ideally have different hashcodes");
    }

    // ==================== toString Test ====================

    /**
     * testToString checks the string representation.
     */
    @Test
    void testToString() {
        System.out.println("Testing Assignment toString method");
        String output = assignment.toString();
        // Check contains key info
        assertTrue(output.contains(assignment.getName()), "toString should contain name");
        assertTrue(output.contains(String.valueOf(assignment.getPointsWorth())), "toString should contain points");
        assertTrue(output.contains(assignment.getCategoryName()), "toString should contain category");
        assertTrue(output.contains("graded=" + assignment.isGraded()), "toString should contain graded status");
    }

}