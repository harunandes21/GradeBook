package controller;

import model.Assignment;
import model.GradingCategory;
import model.Course; 

/**
 * This controller is planned to handle the more specific actions 
 * related just to managing assignment details themselves, like editing assignment points, 
 * due dates, or linking them to specific grading categories. 
 * The TeacherController handles more broad actions like adding grades or adding assignments 
 * to a course, but this one focuses just on the assignment's own stuff. Right now, it just 
 * has the basic empty method stubs like editAssignmentDetails and setAssignmentCategory,
 *  ready for the logic later.
 */

public class AssignmentController {

    private Object currentContext;

	/**
     * Example constructor.
     * @param contextObject Context if needed maybe Course object.
     */
    public AssignmentController(Object contextObject) { 
        this.currentContext = contextObject;
        System.out.println("AssignmentController created with context: " + contextObject);
    }

    /**
     * Modifies details of an existing assignment like name, points, due date.
     * Called from UI edit screen.
     * TODO: Implement using Assignment setters. Define how newDetails are passed.
     * @param assignmentToEdit The Assignment object.
     * @param newDetails       How the new details are passed maybe Map?.
     * @return true if edit ok, false if error.
     */
    public boolean editAssignmentDetails(Assignment assignmentToEdit /*, Object newDetails */) {
        System.out.println("AssignmentController: editAssignmentDetails TODO");
        return false;
    }

    /**
     * Assigns an assignment to a grading category.
     * TODO: Implement using Assignment.setCategory()
     * @param theAssignment The Assignment object.
     * @param theCategory   The GradingCategory object.
     * @return true if set ok, false if error.
     */
    public boolean setAssignmentCategory(Assignment theAssignment, GradingCategory theCategory) {
         System.out.println("AssignmentController: setAssignmentCategory TODO");
         return false;
    }

    /**
     * manually marks an assignment as graded or not graded.
     * TODO: Implement using Assignment.setGraded()
     * @param theAssignment The Assignment object.
     * @param isNowGraded   The new boolean status
     * @return true if updated ok, false if error.
     */
    public boolean markAssignmentGraded(Assignment theAssignment, boolean isNowGraded) {
         System.out.println("AssignmentController: markAssignmentGraded TODO");
         return false;
    }
}