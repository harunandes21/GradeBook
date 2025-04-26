package test;

import model.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Comprehensive test suite for the GradingCategory class.
 * Tests functionality including category creation, assignment management,
 * and logic for dropping lowest grades.
 */
public class GradingCategoryTest {
    private GradingCategory category;
    private Assignment a1, a2, a3;
    private Student student;
    private Map<Assignment, Grade> studentGrades;

    @Before
    public void setUp() {
        category = new GradingCategory("Quizzes", 0.3, 1);
        a1 = new Assignment("Quiz 1", 10, "2023-09-01", "Quizzes", null);
        a2 = new Assignment("Quiz 2", 10, "2023-09-08", "Quizzes", null);
        a3 = new Assignment("Quiz 3", 10, "2023-09-15", "Quizzes", null);

        a1.markGraded();
        a2.markGraded();
        a3.markGraded();

        student = new Student("Alice", "Smith", "alice@school.edu", "pass", "asmith", "1001");

        studentGrades = new HashMap<>();
        studentGrades.put(a1, new Grade(7.0, "Okay"));
        studentGrades.put(a2, new Grade(5.0, "Needs work"));
        studentGrades.put(a3, new Grade(9.0, "Well done"));

        category.addAssignment(a1);
        category.addAssignment(a2);
        category.addAssignment(a3);
    }

    // ==================== CONSTRUCTOR TESTS ====================

    /**
     * Test 1: Normal case - Valid constructor
     * Verifies fields are properly initialized.
     */
    @Test
    public void testValidConstructor() {
        GradingCategory cat = new GradingCategory("Projects", 0.4, 2);
        assertEquals("Name should match", "Projects", cat.getName());
        assertEquals("Weight should match", 0.4, cat.getWeight(), 0.01);
        assertEquals("Dropped count should match", 2, cat.getNumDropped());
    }

    /**
     * Test 2: Edge case - Null name
     * Verifies constructor throws IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullName() {
        new GradingCategory(null, 0.5, 0);
    }

    /**
     * Test 3: Edge case - Empty name
     * Verifies constructor throws IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() {
        new GradingCategory("", 0.5, 0);
    }

    /**
     * Test 4: Edge case - Weight < 0
     * Verifies constructor throws IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegativeWeight() {
        new GradingCategory("Homework", -0.1, 0);
    }

    /**
     * Test 5: Edge case - Weight > 1
     * Verifies constructor throws IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithTooHighWeight() {
        new GradingCategory("Homework", 1.1, 0);
    }

    /**
     * Test 6: Edge case - Dropped count < 0
     * Verifies constructor throws IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegativeNumDropped() {
        new GradingCategory("Homework", 0.5, -1);
    }

    // ==================== ASSIGNMENT MANAGEMENT TESTS ====================

    /**
     * Test 7: Normal case - Add assignment
     * Verifies assignment is added to the list.
     */
    @Test
    public void testAddAssignment() {
        Assignment a4 = new Assignment("Quiz 4", 10, "2023-09-22", "Quizzes", null);
        category.addAssignment(a4);
        assertTrue("Assignment should be added", category.getAssignments().contains(a4));
    }

    /**
     * Test 8: Normal case - Remove assignment
     * Verifies assignment is removed from the list.
     */
    @Test
    public void testRemoveAssignment() {
        category.removeAssignment(a2);
        assertFalse("Assignment should be removed", category.getAssignments().contains(a2));
    }

    /**
     * Test 9: Encapsulation - Returned list is a copy
     * Verifies getAssignments returns a defensive copy.
     */
    @Test
    public void testGetAssignmentsReturnsCopy() {
        List<Assignment> copy = category.getAssignments();
        copy.clear(); // modify copy
        assertEquals("Internal list should remain unchanged", 3, category.getAssignments().size());
    }

    // ==================== GRADE DROPPING LOGIC TESTS ====================

    /**
     * Test 10: Normal case - Drop lowest grade
     * Verifies only lowest scored assignment is dropped.
     */
    @Test
    public void testGetDroppedAssignmentsDropsLowest() {
        List<Assignment> dropped = category.getDroppedAssignments(studentGrades);
        assertEquals("Should drop one assignment", 1, dropped.size());
        assertTrue("Should drop Quiz 2", dropped.contains(a2));
    }

    /**
     * Test 11: Edge case - Zero drops allowed
     * Verifies no assignments are dropped.
     */
    @Test
    public void testGetDroppedAssignmentsWithZeroDrop() {
        GradingCategory noDrop = new GradingCategory("Tests", 0.5, 0);
        noDrop.addAssignment(a1);
        noDrop.addAssignment(a2);
        List<Assignment> dropped = noDrop.getDroppedAssignments(studentGrades);
        assertTrue("No assignments should be dropped", dropped.isEmpty());
    }

    /**
     * Test 12: Edge case - Fewer graded assignments than drop count
     * Verifies only available graded assignments are dropped.
     */
    @Test
    public void testGetDroppedAssignmentsWhenNotEnoughGrades() {
        GradingCategory dropTwo = new GradingCategory("Labs", 0.2, 2);
        dropTwo.addAssignment(a1); // only one graded assignment
        List<Assignment> dropped = dropTwo.getDroppedAssignments(studentGrades);
        assertEquals("Should only drop one available assignment", 1, dropped.size());
        assertTrue("Should drop Quiz 1", dropped.contains(a1));
    }

    /**
     * Test 13: Edge case - No grades at all
     * Verifies no assignments are dropped when no grades exist.
     */
    @Test
    public void testGetDroppedAssignmentsNoGrades() {
        GradingCategory empty = new GradingCategory("Empty", 0.3, 1);
        Assignment newA = new Assignment("Unsubmitted", 10, "2023-10-01", "Empty", null);
        empty.addAssignment(newA);
        List<Assignment> dropped = empty.getDroppedAssignments(Collections.emptyMap());
        assertTrue("Should drop nothing when there are no grades", dropped.isEmpty());
    }
}
