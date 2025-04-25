package view;

import model.*; // Need Assignment, GradingCategory
import controller.AssignmentController; // Need controller for actions

import javax.swing.*;
import java.awt.*;
import java.awt.event.*; // For listeners
import java.time.LocalDate; // For date handling
import java.time.format.DateTimeParseException; // For date errors
import java.util.List; // For categories
import java.util.Map;

/**
 * This class AssignmentView is for showing or editing the details
 * of one single assignment. Could be opened from TeacherView.
 * Has fields for name, points, due date, category, graded status, description.
 */
public class AssignmentView extends JDialog { // Changed to JDialog for popup behavior

    // --- Controller ---
    private AssignmentController assignmentController;
    private Course currentCourse; // Need course to get categories
    private Assignment currentAssignment; // The assignment being viewed/edited

    // --- GUI Components ---
    private JTextField nameField;
    private JTextField pointsField;
    private JTextField dueDateField; // Using JTextField for now
    private JComboBox<GradingCategory> categoryComboBox; // Store actual Category objects
    private JCheckBox isGradedCheckBox;
    private JTextArea descriptionArea;
    private JButton saveChangesButton;
    private JButton closeButton;

    // Flag to track if data was successfully saved
    private boolean saved = false;

    /**
     * Constructor - sets up the dialog window with fields.
     * Takes the parent frame, controller, the course (for categories),
     * and the Assignment object (can be null if creating a new one).
     */
    public AssignmentView(Frame parent, AssignmentController controller, Course course, Assignment assignment) {
        super(parent, "Assignment Details", true); // Make it a modal dialog

        this.assignmentController = controller;
        this.currentCourse = course;
        this.currentAssignment = assignment; // Might be null for new assignment

        // Basic window setup
        this.setSize(450, 420); // Adjusted size slightly
        this.setLocationRelativeTo(parent); // Center relative to parent
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose only this window

        // Use GridBagLayout for structured form layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int gridY = 0; // Row counter

        // Assignment Name
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);
        gridY++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; // Reset

        // Points Possible
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Points Possible:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        pointsField = new JTextField(5);
        mainPanel.add(pointsField, gbc);
        gridY++; gbc.fill = GridBagConstraints.NONE;

        // Due Date
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dueDateField = new JTextField(10);
        mainPanel.add(dueDateField, gbc);
        gridY++; gbc.fill = GridBagConstraints.NONE;

        // Category
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        categoryComboBox = new JComboBox<>(); // Will hold GradingCategory objects
        populateCategoryComboBox(); // Fill it with course categories
        mainPanel.add(categoryComboBox, gbc);
        gridY++; gbc.fill = GridBagConstraints.NONE;

        // Graded Status
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Graded:"), gbc);
        gbc.gridx = 1;
        isGradedCheckBox = new JCheckBox();
        mainPanel.add(isGradedCheckBox, gbc);
        gridY++;

        // Description
        gbc.gridx = 0; gbc.gridy = gridY; gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        descriptionArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(scrollPane, gbc);
        gridY += 3; gbc.gridwidth = 1; gbc.gridheight = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveChangesButton = new JButton("Save Changes");
        closeButton = new JButton("Cancel");
        buttonPanel.add(saveChangesButton);
        buttonPanel.add(closeButton);

        // Add panels to frame
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // Load data if editing an existing assignment
        if (assignment != null) {
            displayAssignmentDetails(assignment);
        } else {
            // Set title for new assignment
             setTitle("Create New Assignment");
        }

        // Add action listeners
        addActionListeners();

        System.out.println("AssignmentView created with components");
    }

    /**
     * populateCategoryComboBox fills the dropdown with categories from the current course.
     * It adds the actual GradingCategory objects, but the combo box uses their toString() for display.
     */
    private void populateCategoryComboBox() {
        categoryComboBox.removeAllItems(); // Clear first
        // Add a "None" option maybe? Or require selection? For now, just course categories.
        // categoryComboBox.addItem(null); // Represent "No Category"

        if (currentCourse != null) {
            Map<String, GradingCategory> categories = currentCourse.getGradingCategories();
            if (categories != null) {
                for (GradingCategory cat : categories.values()) {
                    categoryComboBox.addItem(cat); // Add the object itself
                }
            }
        }
    }

    /**
     * displayAssignmentDetails fills the GUI fields with data from the passed Assignment object.
     * Called when the view is opened for editing an existing assignment.
     * @param assignment The assignment whose details should be shown.
     */
    public void displayAssignmentDetails(Assignment assignment) {
        if (assignment == null) return;
        this.currentAssignment = assignment; // Store for saving later
        setTitle("Edit Assignment: " + assignment.getName()); // Update title

        nameField.setText(assignment.getName());
        pointsField.setText(String.valueOf(assignment.getPointsWorth()));

        // Handle date - convert LocalDate to String for text field
        String dueDateText = "";
        // TODO: Need Assignment.getDueDate() method returning LocalDate
        // if (assignment.getDueDate() != null) {
        //     dueDateText = assignment.getDueDate().toString(); // Format YYYY-MM-DD
        // }
        dueDateField.setText(dueDateText); // Use placeholder for now

        // Select the correct category in the combo box
        // TODO: Need Assignment.getCategory() method returning GradingCategory
        // GradingCategory currentCat = assignment.getCategory();
        // categoryComboBox.setSelectedItem(currentCat); // Selects based on object equality or toString() match

        isGradedCheckBox.setSelected(assignment.isGraded());

        // TODO: Need Assignment.getDescription() method
        // descriptionArea.setText(assignment.getDescription() != null ? assignment.getDescription() : "");
        descriptionArea.setText(""); // Placeholder
    }

    /**
     * addActionListeners connects the Save and Cancel buttons.
     */
    private void addActionListeners() {
        // Cancel button just closes this dialog window
        closeButton.addActionListener(e -> this.dispose());

        // Save button tries to update or create the assignment
        saveChangesButton.addActionListener(e -> saveAssignment());
    }

    /**
     * saveAssignment is called when the Save button is clicked.
     * It gets the data from the GUI fields, validates it,
     * calls the controller to save the changes (either update existing or create new),
     * and closes the dialog if successful.
     */
    private void saveAssignment() {
        System.out.println("Save Changes button clicked");

        // 1. Get data from fields
        String name = nameField.getText().trim();
        String pointsStr = pointsField.getText().trim();
        String dueDateStr = dueDateField.getText().trim();
        Object selectedCategoryObj = categoryComboBox.getSelectedItem(); // This is GradingCategory or null
        boolean isGraded = isGradedCheckBox.isSelected();
        String description = descriptionArea.getText(); // trim() maybe later?

        // 2. Validate data
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Assignment name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double points = -1; // Use invalid value to check if parsing works
        try {
            points = Double.parseDouble(pointsStr);
            if (points < 0) throw new NumberFormatException(); // Ensure non-negative
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Points Possible must be a non-negative number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // TODO: Validate Date format YYYY-MM-DD and parse to LocalDate
        LocalDate dueDate = null; // Placeholder
        // try { if (!dueDateStr.isEmpty()) dueDate = LocalDate.parse(dueDateStr); } catch ...

        // Get the actual GradingCategory object (can be null if none selected/available)
        GradingCategory category = null;
        if (selectedCategoryObj instanceof GradingCategory) {
            category = (GradingCategory) selectedCategoryObj;
        }
        String categoryName = (category != null) ? category.getName() : "None"; // Use name for Assignment constructor maybe? Check Assignment constructor/setters

        // 3. Call Controller (or update model directly if simple?)
        // This logic depends on whether we are creating NEW or editing EXISTING
        boolean success = false;
        if (currentAssignment == null) { // Creating NEW
             System.out.println("TODO: Need logic to CREATE new Assignment object and add it via Controller");
             // Example:
             // Assignment newAssignment = new Assignment(generateId(), name, points, dueDateStr, categoryName, null); // Need ID generation, groupName?
             // success = teacherController.addAssignmentToCourse(newAssignment, currentCourse); // Need TeacherController ref?
        } else { // Editing EXISTING
             System.out.println("TODO: Need logic to UPDATE existing Assignment object via Controller");
             // Example using AssignmentController:
             // success = assignmentController.editAssignmentDetails(currentAssignment, name, points /*, dueDate, description */);
             // Need to also handle category change and graded status change maybe?
             // assignmentController.setAssignmentCategory(currentAssignment, category); // If category changed
             // assignmentController.markAssignmentGraded(currentAssignment, isGraded); // Update graded status
        }


        // 4. Close dialog ONLY if save was successful
        if (success) {
            this.saved = true; // Set flag maybe?
            this.dispose(); // Close the dialog window
        } else {
            // Show error message if controller method returned false
            JOptionPane.showMessageDialog(this, "Failed to save assignment changes.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Getters for UI values ---
    // Could be used by controller if needed, or validation
    public String getNameFieldValue() { return nameField.getText(); }
    public String getPointsFieldValue() { return pointsField.getText(); }
    // Add more getters if needed

    // Method to check if save was successful before closing
    public boolean wasSaveSuccessful() {
        return saved;
    }
}