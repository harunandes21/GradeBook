package controller;

import model.Assignment;
import model.GradingCategory;
import model.Course;

/**
 * This AssignmentController is supposed to handle specific stuff
 * just for changing Assignment details like editing the points or name.
 * It keeps that separate from the main TeacherController.
 */
public class AssignmentController {


    /**
     * constructor
     * just prints message when it's created.
     */
    public AssignmentController() {
        System.out.println("AssignmentController created");
    }

    /**
     * editAssignmentDetails changes the name or points of an assignment
     * that already exists. It gets the assignment object and the new values.
     * It checks if a new name was given and calls the setter in Assignment.
     * It checks if new points were given non negative and calls the setter in Assignment.
     * Need to add more later if we want to edit due date or description here too.
     *
     * @param assignmentToEdit The Assignment object we want to change.
     * @param newName          The new name String, or null/empty means don't change it.
     * @param newPoints        The new points double, negative means don't change it.
     * @return true if things seemed to update ok, false if the assignment was null.
     */
    public boolean editAssignmentDetails(Assignment assignmentToEdit, String newName, double newPoints /*, add more params later */) {
        System.out.println("AssignmentController trying to edit details");
        // First check if we even have an assignment to edit.
        boolean haveAssignment = (assignmentToEdit != null);
        if (!haveAssignment) {
            //print message if assignment was null.
            System.out.println("editAssignmentDetails error: got null assignment");
            return false; //can't edit nothing.
        }

        // Check if a new name was actually provided. Ignore empty strings.
        boolean shouldChangeName = (newName != null && !newName.trim().isEmpty());
        if (shouldChangeName) {
            // If yes, tell the Assignment object to update its name.
            // Assignment class needs a setAssignmentName method.
            assignmentToEdit.setAssignmentName(newName.trim());
            System.out.println("assignment name updated");
        }

        // Check if a valid non negative new point value was given.
        boolean shouldChangePoints = (newPoints >= 0);
        if (shouldChangePoints) {
            // If yes, tell the Assignment object to update its points.
            // Assumes Assignment class has setPointsPossible method.
            
            try {
                 assignmentToEdit.setPointsPossible(newPoints); 
                 System.out.println("assignment points updated");
            } 
            
            catch (IllegalArgumentException error) {
                 // if the setter didn't like the points value.
                 System.out.println("editAssignmentDetails error: bad points value " + newPoints);
                 return false; 
            } 
            
            catch (Exception otherError) {
                 //catch anything else
                 System.out.println("editAssignmentDetails error updating points " + otherError.getMessage());
                 return false;
            }
        }

        return true;
    }

    /**
     * setAssignmentCategory links an assignment to one of the grading categories.
     * Like putting Homework 1 into the Homework category.
     * TODO: This whole thing depends on the Assignment class having a way
     *       to store a GradingCategory object, not just the category name string
     *       it seems to have now. Needs Assignment model to be updated first.
     *
     * @param theAssignment The Assignment object to update.
     * @param theCategory   The GradingCategory object to link it to.
     * @return true if it worked, false if inputs null or the needed setter isn't there.
     */
    public boolean setAssignmentCategory(Assignment theAssignment, GradingCategory theCategory) {
         System.out.println("AssignmentController trying to set category");

         boolean haveAssignment = (theAssignment != null);
         boolean haveCategory = (theCategory != null);
         
         if (!haveAssignment || !haveCategory) {
             System.out.println("setAssignmentCategory error: got null assignment or category");
             return false;
         }
         //needs a setCategory(GradingCategory c) method 
.
         return false;
    }

    /**
     * markAssignmentGraded changes the graded status of an assignment.
     * Lets the teacher mark it as done grading or maybe undo that.
     * TODO: The Assignment class only has markGraded(). It needs a way
     *       to set the status both true AND false, like setGraded(boolean).
     *
     * @param theAssignment The Assignment object to change.
     * @param isNowGraded   The new status, true means graded, false means not graded.
     * @return true if status updated ok, false if error or null input or missing method.
     */
    public boolean markAssignmentGraded(Assignment theAssignment, boolean isNowGraded) {
         System.out.println("AssignmentController trying to mark graded status");
         // Check input first.
         boolean haveAssignment = (theAssignment != null);
         if (!haveAssignment) {
              System.out.println("markAssignmentGraded error: got null assignment");
             return false;
         }


         //Assignment needs setGraded(boolean) method
         System.out.println("markAssignmentGraded TODO:  to work fully.");
         
         try {
              // Call the needed setter method.
              theAssignment.setGraded(isNowGraded);
              System.out.println("assignment graded status set to: " + isNowGraded);
              return true; 
         } 
         
         catch (NoSuchMethodError | Exception e) { // Catch if method missing or other error
             System.out.println("markAssignmentGraded error: failed to set status. Maybe Assignment needs setGraded(boolean)?");
             return false;
         }

    }
}