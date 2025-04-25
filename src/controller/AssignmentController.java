package controller;

import model.Assignment;
import model.GradingCategory;
import model.Course; // Keep import just in case needed later

/**
 * This AssignmentController handles specific actions just for changing
 * Assignment details, like its name or points.
 * Keeps this separate from the main TeacherController.
 */
public class AssignmentController {

    /**
     *  constructor, prints a message when created
     */
    public AssignmentController() {
        System.out.println("AssignmentController created");
    }

    /**
     * editAssignmentDetails changes the name or points of an assignment.
     * It gets the assignment object and the new values.
     * It calls the setter methods inside the Assignment class to make the changes.
     * Checks inputs first.
     * TODO Add params later if we need to edit due date, description etc here too.
     *
     * @param assignmentToEdit The Assignment object we want to change.
     * @param newName          The new name String, or null/empty means don't change.
     * @param newPoints        The new points double, negative means don't change.
     * @return true if updates worked, false if assignment null or setters failed.
     */
    public boolean editAssignmentDetails(Assignment assignmentToEdit, String newName, double newPoints /*, add more params later */) {
        System.out.println("AssignmentController trying to edit details");
        
        // Check if we got an assignment first.
        boolean haveAssignment = (assignmentToEdit != null);
        if (!haveAssignment) {
            System.out.println("editAssignmentDetails error: got null assignment");
            return false;
        }

        //keep track if any part fails.
        boolean success = true;

        //check if a new name was given and isn't empty.
        boolean shouldChangeName = (newName != null && !newName.trim().isEmpty());
        if (shouldChangeName) {
            //tell the Assignment object to change its name using its setter.
            try {
                 assignmentToEdit.setAssignmentName(newName.trim()); // Use setter in Assignment
                 System.out.println("assignment name updated");
            } 
            
            catch (Exception e) { //catch any error from setter maybe?
                 System.out.println("editAssignmentDetails problem setting name: " + e.getMessage());
                 success = false; // Mark as failed
            }
        }

        //check if valid non negative points were given.
        boolean shouldChangePoints = (newPoints >= 0);
        if (shouldChangePoints) {
            // Tell the Assignment object to change its points using its setter.
            try {
                 assignmentToEdit.setPointsPossible(newPoints); // Use setter in Assignment
                 System.out.println("assignment points updated");
            } 
            
            catch (IllegalArgumentException error) { //catch specific error from setter
                 System.out.println("editAssignmentDetails problem: bad points value " + newPoints);
                 success = false; // Mark as failed
            } 
            
            catch (Exception otherError) { //catch anything else
                 System.out.println("editAssignmentDetails problem updating points");
                 success = false;
            }
        }

        // TODO need blocks here later to update other fields like due date, description, etc

        // Return true only if all updates we tried worked.
        return success;
    }

    /**
     * setAssignmentCategory links an assignment to a grading category.
     * TODO Assignment model needs a setCategory(GradingCategory c) method first.
     *      Currently Assignment only stores category name as a string. This method can't work yet.
     *
     * @param theAssignment The Assignment object.
     * @param theCategory   The GradingCategory object.
     * @return false because the needed model method is missing.
     */
    public boolean setAssignmentCategory(Assignment theAssignment, GradingCategory theCategory) {
         System.out.println("AssignmentController trying to set category");
         // Check inputs.
         boolean haveAssignment = (theAssignment != null);
         boolean haveCategory = (theCategory != null);
         if (!haveAssignment || !haveCategory) {
             System.out.println("setAssignmentCategory error: got null assignment or category");
             return false;
         }

         // This whole function depends on Assignment model getting updated.
         System.out.println("setAssignmentCategory TODO: Assignment model needs update first.");
         // If Assignment had setCategory(theCategory), we would call it here in a try-catch block.

         // Return false because we can't do it yet.
         return false;
    }

    /**
     * markAssignmentGraded changes the graded status of an assignment true or false.
     * Calls the setGraded(boolean) method we added to Assignment.
     *
     * @param theAssignment The Assignment object.
     * @param isNowGraded   The new boolean status.
     * @return true if status updated ok, false if error or null input.
     */
    public boolean markAssignmentGraded(Assignment theAssignment, boolean isNowGraded) {
         System.out.println("AssignmentController trying to mark graded status to: " + isNowGraded);
         //check input.
         boolean haveAssignment = (theAssignment != null);
         
         if (!haveAssignment) {
              System.out.println("markAssignmentGraded error: got null assignment");
             return false;
         }

         // Use the setter method added to Assignment.
         try {
              // Call the setter.
              theAssignment.setGraded(isNowGraded); // Use the setter
              System.out.println("assignment graded status set ok");
              return true; // Assume worked.
         } 
         
         catch (Exception e) { // catch any unexpected error
             System.out.println("markAssignmentGraded problem: failed to set status " + e.getMessage());
             return false; // Failed.
         }
    }
}