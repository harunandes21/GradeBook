package test.controller;

import model.*; // Need models
import controller.*; // Need controller

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This tests the AssignmentController class.
 * It focuses on the methods for editing assignment details and marking graded status,
 * as these have some basic logic or call setters we can verify.
 * Testing setAssignmentCategory depends on model changes.
 */
class AssignmentControllerTest {

    // Declare variables
    private AssignmentController assignmentController;
    private Assignment testAssignment;

    /**
     * setUp runs before each test.
     * Creates a fresh controller and a test assignment object.
     */
    @BeforeEach
    void setUp() {
        // Make the controller we are testing
        assignmentController = new AssignmentController(); // Constructor takes no args now
        // Make a basic assignment to test editing on
        testAssignment = new Assignment("Initial Name", 50.0, "date", "Initial Cat", "g");
    }

    /**
     * testEditAssignmentDetails_NameAndPoints tests changing name and points.
     * Checks if controller returns true and if assignment object actually updated.
     */
    @Test
    void testEditAssignmentDetails_NameAndPoints() {
        System.out.println("Testing AssignmentController editAssignmentDetails name and points");
        // Call controller to change name and points
        boolean success = assignmentController.editAssignmentDetails(testAssignment, "New Name", 75.0);
        // Check controller reported success
        assertTrue(success, "editAssignmentDetails should return true for valid changes");
        // Check the assignment object itself was updated
        assertEquals("New Name", testAssignment.getName(), "Assignment name should be updated");
        assertEquals(75.0, testAssignment.getPointsWorth(), 0.01, "Assignment points should be updated");
    }

    /**
     * testEditAssignmentDetails_OnlyName tests changing only the name.
     * Pass -1 for points to signal no change.
     * Checks name updated, points unchanged.
     */
    @Test
    void testEditAssignmentDetails_OnlyName() {
        System.out.println("Testing AssignmentController editAssignmentDetails only name");
        // Call controller to change only name use -1 for points
        boolean success = assignmentController.editAssignmentDetails(testAssignment, "Name Only", -1.0);
        assertTrue(success, "editAssignmentDetails should return true for name change");
        // Check name updated
        assertEquals("Name Only", testAssignment.getName(), "Assignment name should be updated");
        // Check points not changed from initial 50.0
        assertEquals(50.0, testAssignment.getPointsWorth(), 0.01, "Assignment points should not change");
    }

    /**
     * testEditAssignmentDetails_OnlyPoints tests changing only points.
     * Pass null or empty string for name to signal no change.
     * Checks points updated, name unchanged.
     */
    @Test
    void testEditAssignmentDetails_OnlyPoints() {
        System.out.println("Testing AssignmentController editAssignmentDetails only points");
        // Call controller to change only points pass null for name
        boolean success = assignmentController.editAssignmentDetails(testAssignment, null, 99.0);
        assertTrue(success, "editAssignmentDetails should return true for points change");
        // Check name not changed
        assertEquals("Initial Name", testAssignment.getName(), "Assignment name should not change");
        // Check points updated
        assertEquals(99.0, testAssignment.getPointsWorth(), 0.01, "Assignment points should be updated");
    }

    /**
     * testEditAssignmentDetails_InvalidPoints checks trying to set negative points.
     * Controller should catch error from Assignment setter and return false.
     */
    @Test
    void testEditAssignmentDetails_InvalidPoints() {
        System.out.println("Testing AssignmentController editAssignmentDetails invalid points");
        // Call controller with negative points
        boolean success = assignmentController.editAssignmentDetails(testAssignment, null, -10.0);
        // Check controller reported failure
        assertFalse(success, "editAssignmentDetails should return false for invalid negative points");
        // Check assignment points didn't change from original 50.0
        assertEquals(50.0, testAssignment.getPointsWorth(), 0.01, "Assignment points should not change on error");
    }

    /**
     * testEditAssignmentDetails_NullAssignment checks edge case of null input.
     * Controller should handle null assignment gracefully and return false.
     */
    @Test
    void testEditAssignmentDetails_NullAssignment() {
        System.out.println("Testing AssignmentController editAssignmentDetails with null assignment");
        // Call controller with null assignment
        boolean success = assignmentController.editAssignmentDetails(null, "Doesn't Matter", 100.0);
        // Check controller reported failure
        assertFalse(success, "editAssignmentDetails should return false for null assignment input");
    }

    /**
     * testMarkAssignmentGraded_True tests setting graded status to true.
     * Assumes Assignment model now has setGraded(boolean).
     */
    @Test
    void testMarkAssignmentGraded_True() {
        System.out.println("Testing AssignmentController markAssignmentGraded true");
        // Initial state should be false
        assertFalse(testAssignment.isGraded(), "Assignment should start ungraded");
        // Call controller to mark graded
        boolean success = assignmentController.markAssignmentGraded(testAssignment, true);
        // Check controller reported success
        assertTrue(success, "markAssignmentGraded(true) should return true");
        // Check the assignment object's status
        assertTrue(testAssignment.isGraded(), "Assignment should now be graded");
    }

    /**
     * testMarkAssignmentGraded_False tests setting graded status back to false.
     * Also assumes Assignment model has setGraded(boolean).
     */
    @Test
    void testMarkAssignmentGraded_False() {
        System.out.println("Testing AssignmentController markAssignmentGraded false");
        // Start by making sure it's true
        testAssignment.setGraded(true); // Use setter directly for setup
        assertTrue(testAssignment.isGraded(), "Assignment should be graded initially for this test");
        // Call controller to mark ungraded
        boolean success = assignmentController.markAssignmentGraded(testAssignment, false);
        // Check controller reported success
        assertTrue(success, "markAssignmentGraded(false) should return true");
        // Check the assignment object's status
        assertFalse(testAssignment.isGraded(), "Assignment should now be ungraded");
    }

    /**
     * testMarkAssignmentGraded_NullAssignment checks edge case null input.
     * Controller should handle null assignment gracefully and return false.
     */
    @Test
    void testMarkAssignmentGraded_NullAssignment() {
        System.out.println("Testing AssignmentController markAssignmentGraded with null assignment");
        // Call controller with null
        boolean success = assignmentController.markAssignmentGraded(null, true);
        // Check controller reported failure
        assertFalse(success, "markAssignmentGraded should return false for null assignment input");
    }

    /**
     * testSetAssignmentCategory_Blocked checks the category setting method.
     * Since the Assignment model likely doesn't support setting category object yet,
     * this test verifies the controller currently returns false as expected.
     */
    @Test
    void testSetAssignmentCategory_Blocked() {
         System.out.println("Testing AssignmentController setAssignmentCategory (currently blocked)");
         // Make a dummy category
         GradingCategory dummyCategory = new GradingCategory("Test Cat", 0.1, 0);
         // Call the controller method
         boolean success = assignmentController.setAssignmentCategory(testAssignment, dummyCategory);
         // Expect it to return false because Assignment model needs update first
         assertFalse(success, "setAssignmentCategory should return false until Assignment model supports it");
    }

}