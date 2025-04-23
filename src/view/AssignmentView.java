package view;

import javax.swing.*;
// We will need imports later for layouts, components, events, etc.
// import java.awt.*;
// import java.awt.event.*;
// Maybe need imports for model data later
// import model.Assignment;

/**
 * This class AssignmentView is planned for showing the details
 * of one single assignment. Maybe the teacher clicks an assignment
 * in their TeacherView and this window pops up. Could also be used
 * by students maybe.
 * This is just the basic frame setup.
 * TODO: Need to add the actual GUI parts like text fields, labels later
 *       and connect them to a controller (maybe AssignmentController?).
 */
public class AssignmentView extends JFrame { // Or maybe it should be a JPanel inside another window?

    // --- GUI Components ---
    // We will declare labels, text fields, etc. here later
    // to show/edit assignment name, points, due date, description.
    private JLabel placeholderLabel;

    /**
     * Constructor - sets up the basic window frame.
     * Puts a simple placeholder message inside for now.
     * TODO: Replace placeholder with real layout and components.
     *       Might need the Assignment object passed into the constructor
     *       so it knows which assignment's details to show.
     */
    public AssignmentView(/* Maybe needs Assignment object? Controller? */) {
        // Basic window setup
        this.setTitle("Assignment Details"); // Title bar text
        this.setSize(400, 300); // Set initial size
        this.setLocationRelativeTo(null); // Center window

        // When this window closes, just get rid of it, don't exit the whole app.
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Just put a simple label inside for now.
        placeholderLabel = new JLabel("Assignment View GUI - Needs Content");
        // Center the text
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Add the label to the frame.
        this.add(placeholderLabel);

        System.out.println("AssignmentView created (basic skeleton)");
        // Don't make visible automatically here.
        // this.setVisible(true);
    }

    // --- Getters/Setters for Components ---
    // TODO: Add public methods here later to get data from text fields (if editable)
    //       or to get buttons so the Controller can add listeners. Example:
    // public String getPointsFieldValue() { /* return pointsField.getText(); */ return ""; }
    // public JButton getSaveChangesButton() { /* return saveButton; */ return null; }

    // --- Methods to Update View Data ---
    // TODO: Add public methods here later that the Controller can call
    //       to fill in the labels/fields with the specific assignment's data. Example:
    // public void displayAssignmentDetails(Assignment assignment) { /* set text in labels/fields */ }

    /*
    // Example main method just for testing if this basic window shows up.
    // Delete this later.
    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
             System.out.println("Running AssignmentView test main...");
             AssignmentView basicView = new AssignmentView();
             basicView.setVisible(true); // Make visible for testing
         });
    }
    */
}