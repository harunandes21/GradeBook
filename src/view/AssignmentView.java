package view;

import model.*; // Need Assignment, GradingCategory, Course
import controller.AssignmentController; // Need controller for actions
import controller.TeacherController; // Needed for creating NEW assignments

import javax.swing.*;
import java.awt.*;
import java.awt.event.*; // For listeners
// import java.time.LocalDate; // Using String for date for now based on model
// import java.time.format.DateTimeParseException; // Need for real date parsing
import java.util.List; // For categories
import java.util.Map; // For categories
import java.util.Comparator; // For sorting categories maybe
import java.util.ArrayList; // For sorting categories maybe

/**
 * This class AssignmentView is for showing or editing the details
 * of one single assignment. Used as a popup dialog from TeacherView.
 * Has fields for name, points, due date, category, graded status, description.
 * Connects Save button to AssignmentController or TeacherController.
 */
public class AssignmentView extends JDialog {

    // --- Controllers ---
    private AssignmentController assignmentController; // For editing actions
    private TeacherController teacherController; // Needed if CREATING new assignment
    private Course currentCourse; // Need course to get categories and add new assignments
    private Assignment currentAssignment; // The assignment being viewed/edited can be null if new

    // --- GUI Components ---
    private JTextField nameField;
    private JTextField pointsField;
    private JTextField dueDateField; // Expecting YYYY-MM-DD string maybe
    private JComboBox<GradingCategory> categoryComboBox; // Store actual Category objects
    private JCheckBox isGradedCheckBox;
    private JTextArea descriptionArea;
    private JButton saveButton; // Renamed
    private JButton cancelButton; // Renamed

    // Flag to track if data was successfully saved for the calling window
    private boolean saved = false;

    /**
     * Constructor - sets up the dialog window with fields.
     * Takes the parent frame, controllers, the course,
     * and the Assignment object null means creating a new one.
     */
    public AssignmentView(Frame parent, AssignmentController assignCtrl, TeacherController teachCtrl, Course course, Assignment assignment) {
        // Make it a modal dialog blocks parent until closed
        super(parent, "Assignment Details", true);

        this.assignmentController = assignCtrl;
        this.teacherController = teachCtrl; // Store teacher controller
        this.currentCourse = course;
        this.currentAssignment = assignment; // Might be null

        // Basic window setup
        this.setSize(450, 420);
        this.setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Just close this dialog

        // --- Main Panel using GridBagLayout for form structure ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between components
        gbc.anchor = GridBagConstraints.WEST; // Align labels left

        int gridY = 0; // Keep track of current row

        // --- Assignment Name ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);
        gridY++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; // Reset

        // --- Points Possible ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Points Possible:"), gbc);
        gbc.gridx = 1; // Let it resize maybe? No.
        pointsField = new JTextField(5);
        mainPanel.add(pointsField, gbc);
        gridY++;

        // --- Due Date ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; // Let it resize
        // Just a text field for now. Should validate format on save.
        dueDateField = new JTextField(10);
        mainPanel.add(dueDateField, gbc);
        gridY++;

        // --- Category ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        categoryComboBox = new JComboBox<>(); // Will hold GradingCategory objects
        populateCategoryComboBox(); // Fill dropdown with categories from the course
        mainPanel.add(categoryComboBox, gbc);
        gridY++; gbc.fill = GridBagConstraints.NONE;

        // --- Graded Status ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Graded:"), gbc);
        gbc.gridx = 1;
        isGradedCheckBox = new JCheckBox(); // Simple check box
        mainPanel.add(isGradedCheckBox, gbc);
        gridY++;

        // --- Description ---
        gbc.gridx = 0; gbc.gridy = gridY; gbc.anchor = GridBagConstraints.NORTHWEST; // Align label top-left
        mainPanel.add(new JLabel("Description:"), gbc);
        // Make text area span multiple rows and columns and resize
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0; // Let it grow
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true); // Wrap text
        descriptionArea.setWrapStyleWord(true);
        // Put text area inside scroll pane in case description is long
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(scrollPane, gbc);
        // Reset grid settings after multi cell component
        gridY += 3; gbc.gridwidth = 1; gbc.gridheight = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;

        // --- Buttons Panel ---
        // Use FlowLayout aligned right for buttons at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save"); // Renamed
        cancelButton = new JButton("Cancel"); // Renamed
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // --- Add Panels to Dialog Window ---
        // Use BorderLayout for the dialog itself
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER); // Form in the middle
        this.add(buttonPanel, BorderLayout.SOUTH); // Buttons at the bottom

        // If editing existing assignment null check, fill fields with its data
        if (assignment != null) {
            displayAssignmentDetails(assignment);
        } else {
            // Otherwise set title for creating a new one
             setTitle("Create New Assignment");
        }

        // Connect buttons to their actions
        addActionListeners();

        System.out.println("AssignmentView created with components");
    }

    /**
     * populateCategoryComboBox fills the dropdown with GradingCategory objects
     * from the current course. Uses category name for display.
     * Adds a "(None)" option represented by null.
     */
    private void populateCategoryComboBox() {
        categoryComboBox.removeAllItems(); // Clear old items
        // Add null option first to represent no category selected
        categoryComboBox.addItem(null); // Need custom renderer later maybe

        boolean courseExists = (currentCourse != null);
        if (courseExists) {
            // Get the categories map from the course
            Map<String, GradingCategory> categories = currentCourse.getGradingCategories(); // Gets copy
            boolean categoriesExist = (categories != null);
            if (categoriesExist) {
                // Sort categories alphabetically by name for the dropdown maybe?
                 List<GradingCategory> sortedCategories = new ArrayList<>(categories.values());
                 sortedCategories.sort(Comparator.comparing(GradingCategory::getName, String.CASE_INSENSITIVE_ORDER));
                // Add each actual GradingCategory object to the dropdown
                for (GradingCategory cat : sortedCategories) {
                    categoryComboBox.addItem(cat); // JComboBox uses toString()
                }
            }
        }
        // Set default selection maybe?
        categoryComboBox.setSelectedIndex(0); // Select the null/"None" option
    }

    /**
     * displayAssignmentDetails fills the GUI fields with data from the assignment object.
     * This gets called when the dialog opens for editing an existing assignment.
     * @param assignment The assignment object whose details we need to show.
     */
    public void displayAssignmentDetails(Assignment assignment) {
        // Check assignment exists
        if (assignment == null) return;
        this.currentAssignment = assignment; // Store for save logic
        setTitle("Edit Assignment: " + assignment.getName()); // Update window title

        // Set text/values in the fields based on assignment's getter methods
        nameField.setText(assignment.getName());
        pointsField.setText(String.valueOf(assignment.getPointsWorth()));
        dueDateField.setText(assignment.getDueDate() != null ? assignment.getDueDate() : ""); // Use string date
        descriptionArea.setText(assignment.getDescription() != null ? assignment.getDescription() : ""); // Use getter

        // Select the correct category in the combo box
        // Loop through items find the GradingCategory object matching assignment's categoryName string.
        categoryComboBox.setSelectedItem(null); // Default to none
        String assignmentCategoryName = assignment.getCategoryName();
        boolean categoryNameExists = (assignmentCategoryName != null);
        if (categoryNameExists && !assignmentCategoryName.equalsIgnoreCase("None") && !assignmentCategoryName.equalsIgnoreCase("Uncategorized")) {
             for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                 GradingCategory catInBox = categoryComboBox.getItemAt(i);
                 // Check object not null and names match
                 if (catInBox != null && catInBox.getName().equals(assignmentCategoryName)) {
                      categoryComboBox.setSelectedIndex(i); // Select the matching object
                      break; // Stop looking
                 }
             }
        } else {
             categoryComboBox.setSelectedIndex(0); // Select the null/"None" item if no match
        }

        // Set the checkbox based on assignment's graded status
        isGradedCheckBox.setSelected(assignment.isGraded());
    }

    /**
     * addActionListeners connects the Save and Cancel buttons to code.
     * Cancel just closes the window. Save calls the saveAssignment helper method.
     */
    private void addActionListeners() {
        // Cancel button action: just close this dialog using dispose()
        cancelButton.addActionListener(e -> this.dispose());

        // Save button action: call the saveAssignment method
        saveButton.addActionListener(e -> saveAssignment());
    }

    /**
     * saveAssignment is called when the Save button is clicked.
     * It reads the values from all the GUI fields name, points, date, category etc.
     * It validates the input like making sure points is a number.
     * If creating a new assignment currentAssignment is null, it makes a new Assignment object
     * and uses TeacherController to add it to the course.
     * If editing existing currentAssignment isn't null, it uses AssignmentController
     * to update the existing assignment's details.
     * Closes the dialog window only if the save operation seems successful.
     */
    private void saveAssignment() {
        System.out.println("AssignmentView save button trying to save...");

        // --- 1. Get data from all the GUI input fields ---
        String name = nameField.getText().trim();
        String pointsStr = pointsField.getText().trim();
        String dueDateStr = dueDateField.getText().trim(); // Assume format YYYY-MM-DD maybe?
        Object selectedCategoryObj = categoryComboBox.getSelectedItem(); // This is GradingCategory or null
        boolean isGraded = isGradedCheckBox.isSelected();
        String description = descriptionArea.getText();

        // --- 2. Validate the input data ---
        if (name.isEmpty()) {
            showError("Assignment name cannot be empty.");
            return; // Stop saving
        }
        double points = -1.0; // Default to invalid
        try {
            points = Double.parseDouble(pointsStr);
            if (points < 0) throw new NumberFormatException("Points cannot be negative");
        } catch (NumberFormatException ex) {
            showError("Points Possible must be a valid non negative number.");
            return; // Stop saving
        }
        // TODO Add date string validation if needed

        // Get the category name string to store in Assignment
        // Assignment model currently expects a String for category name
        GradingCategory categoryObject = null;
        if (selectedCategoryObj instanceof GradingCategory) {
            categoryObject = (GradingCategory) selectedCategoryObj;
        }
        String categoryName = (categoryObject != null) ? categoryObject.getName() : null; // Use null if no category selected

        // --- 3. Call Controller to save ---
        boolean success = false;
        boolean isEditing = (this.currentAssignment != null);

        if (isEditing) {
            // Editing existing assignment
            System.out.println("AssignmentView saving changes to: " + currentAssignment.getName());
            // Need AssignmentController for edits
            boolean haveAssignCtrl = (assignmentController != null);
            if (haveAssignCtrl) {
                 // Call controller to update basic fields name, points
                 success = assignmentController.editAssignmentDetails(currentAssignment, name, points /*, pass other fields like date */);
                 // Also update category name and description using assumed setters in Assignment model
                 // TODO Assignment model needs setCategoryName(String) and setDescription(String)
                 // For now, assume they exist and call them directly or via controller later
                 try {
                     currentAssignment.setCategoryName(categoryName); // Needs setter
                     currentAssignment.setDescription(description); // Needs setter
                     // Update graded status using controller
                     boolean gradedSuccess = assignmentController.markAssignmentGraded(currentAssignment, isGraded);
                     success = success && gradedSuccess; // Combine results
                 } catch (Exception e) {
                      System.out.println("AssignmentView problem: Error setting category/desc/graded status - check Assignment setters");
                      success = false;
                 }
            } else {
                 System.out.println("AssignmentView error: AssignmentController is missing, cannot save edits.");
                 success = false;
            }

        } else { // Creating a new assignment
            System.out.println("AssignmentView creating new assignment: " + name);
            // Need TeacherController and Course to add new assignment
            boolean canCreate = (teacherController != null && currentCourse != null);
            if (canCreate) {
                // Make a new Assignment object
                // ID? Use placeholder for now. Group name? Use null for now.
                String newId = "Assign_" + System.currentTimeMillis(); // Simple temporary ID
                String groupName = null; // No group field in UI currently
                try {
                    // Use the Assignment constructor needs categoryName String
                    Assignment newAssignment = new Assignment(newId, name, points, dueDateStr, categoryName, groupName);
                    // Set description assume setter exists
                    newAssignment.setDescription(description); // TODO Verify Assignment.setDescription
                    // Set graded status assume setter exists
                    newAssignment.setGraded(isGraded); // TODO Verify Assignment.setGraded

                    // Use TeacherController to add this new assignment to the course
                    success = teacherController.addAssignmentToCourse(newAssignment, currentCourse);

                } catch (IllegalArgumentException ex) {
                     showError("Error creating assignment: " + ex.getMessage());
                     success = false;
                } catch (Exception ex) {
                     showError("Unexpected error creating assignment: " + ex.getMessage());
                     success = false;
                }
            } else {
                 System.out.println("AssignmentView error: Missing TeacherController or Course reference, cannot create.");
                 success = false;
            }
        }


        // --- 4. Close dialog ONLY if save was successful ---
        if (success) {
            this.saved = true; // Set flag so TeacherView knows it worked
            System.out.println("Assignment save successful, closing dialog.");
            this.dispose(); // Close this dialog window
        } else {
            // Show generic error if controller method returned false or other issue
            showError("Failed to save assignment changes. Check inputs or console.");
        }
    }

    // --- Helper to show simple error popup ---
    private void showError(String message) {
         JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
    // --- Helper to show simple info popup ---
    private void showInfo(String message) {
         JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * wasSaveSuccessful is called by the window that opened this dialog
     * after it closes to check if the user actually saved changes or just cancelled.
     * @return true if save button was clicked and controller reported success.
     */
    public boolean wasSaveSuccessful() {
        return saved;
    }
}