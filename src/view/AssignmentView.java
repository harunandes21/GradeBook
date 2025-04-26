package view;

import model.*; // Need Assignment, GradingCategory, Course, Group
import controller.AssignmentController; // Need controller for actions
import controller.TeacherController; // Needed for creating NEW assignments

import javax.swing.*;
import java.awt.*;
import java.awt.event.*; // For listeners
import java.util.List; // For categories
import java.util.Map; // For categories
import java.util.Comparator; // For sorting categories maybe
import java.util.ArrayList; // For sorting categories maybe

/**
 * This class AssignmentView is for showing or editing the details
 * of one single assignment. Used as a popup dialog from TeacherView.
 * Has fields for name, points, due date, category, graded status, description, group assignment.
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
    private JTextField dueDateField;
    private JComboBox<GradingCategory> categoryComboBox;
    private JCheckBox isGradedCheckBox;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;
    
    // Group assignment components
    private JCheckBox groupCheckbox;
    private JComboBox<String> groupSelectionCombo;

    // Flag to track if data was successfully saved for the calling window
    private boolean saved = false;

    /**
     * Constructor - sets up the dialog window with fields.
     * Takes the parent frame, controllers, the course,
     * the Assignment object (null means creating a new one),
     * and the groups combo box from TeacherView.
     */
    public AssignmentView(Frame parent, AssignmentController assignCtrl, TeacherController teachCtrl, 
                        Course course, Assignment assignment, JComboBox<String> groupsCombo) {
        // Make it a modal dialog blocks parent until closed
        super(parent, "Assignment Details", true);

        this.assignmentController = assignCtrl;
        this.teacherController = teachCtrl;
        this.currentCourse = course;
        this.currentAssignment = assignment;

        // Basic window setup
        this.setSize(450, 480); // Increased height for group components
        this.setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- Main Panel using GridBagLayout for form structure ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int gridY = 0;

        // --- Assignment Name ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);
        gridY++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

        // --- Points Possible ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Points Possible:"), gbc);
        gbc.gridx = 1;
        pointsField = new JTextField(5);
        mainPanel.add(pointsField, gbc);
        gridY++;

        // --- Due Date ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dueDateField = new JTextField(10);
        mainPanel.add(dueDateField, gbc);
        gridY++;

        // --- Category ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        categoryComboBox = new JComboBox<>();
        populateCategoryComboBox();
        mainPanel.add(categoryComboBox, gbc);
        gridY++; gbc.fill = GridBagConstraints.NONE;

        // --- Graded Status ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Graded:"), gbc);
        gbc.gridx = 1;
        isGradedCheckBox = new JCheckBox();
        mainPanel.add(isGradedCheckBox, gbc);
        gridY++;

        // --- Group Assignment ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Group Assignment:"), gbc);
        gbc.gridx = 1;
        groupCheckbox = new JCheckBox();
        groupCheckbox.addActionListener(e -> groupSelectionCombo.setEnabled(groupCheckbox.isSelected()));
        mainPanel.add(groupCheckbox, gbc);
        gridY++;

        // --- Group Selection ---
        gbc.gridx = 0; gbc.gridy = gridY;
        mainPanel.add(new JLabel("Select Group:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        groupSelectionCombo = new JComboBox<>();
        // Populate with groups from the groupsCombo passed from TeacherView
        for (int i = 0; i < groupsCombo.getItemCount(); i++) {
            groupSelectionCombo.addItem(groupsCombo.getItemAt(i));
        }
        groupSelectionCombo.setEnabled(false); // Disabled until group checkbox is checked
        mainPanel.add(groupSelectionCombo, gbc);
        gridY++; gbc.fill = GridBagConstraints.NONE;

        // --- Description ---
        gbc.gridx = 0; gbc.gridy = gridY; gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(scrollPane, gbc);
        gridY += 3; gbc.gridwidth = 1; gbc.gridheight = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;

        // --- Buttons Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // --- Add Panels to Dialog Window ---
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // Initialize fields based on assignment (if editing)
        if (assignment != null) {
            displayAssignmentDetails(assignment);
        } else {
            setTitle("Create New Assignment");
        }

        // Connect buttons to their actions
        addActionListeners();
    }

    /**
     * Populates the category dropdown with GradingCategory objects
     * from the current course.
     */
    private void populateCategoryComboBox() {
        categoryComboBox.removeAllItems();
        categoryComboBox.addItem(null);

        if (currentCourse != null) {
            Map<String, GradingCategory> categories = currentCourse.getGradingCategories();
            if (categories != null) {
                List<GradingCategory> sortedCategories = new ArrayList<>(categories.values());
                sortedCategories.sort(Comparator.comparing(GradingCategory::getName, String.CASE_INSENSITIVE_ORDER));
                for (GradingCategory cat : sortedCategories) {
                    categoryComboBox.addItem(cat);
                }
            }
        }
        categoryComboBox.setSelectedIndex(0);
    }

    /**
     * Fills the GUI fields with data from the assignment object.
     */
    public void displayAssignmentDetails(Assignment assignment) {
        if (assignment == null) return;
        this.currentAssignment = assignment;
        setTitle("Edit Assignment: " + assignment.getName());

        nameField.setText(assignment.getName());
        pointsField.setText(String.valueOf(assignment.getPointsWorth()));
        dueDateField.setText(assignment.getDueDate() != null ? assignment.getDueDate() : "");
        descriptionArea.setText(assignment.getDescription() != null ? assignment.getDescription() : "");

        // Set category
        categoryComboBox.setSelectedItem(null);
        String assignmentCategoryName = assignment.getCategoryName();
        if (assignmentCategoryName != null && !assignmentCategoryName.equalsIgnoreCase("None") 
            && !assignmentCategoryName.equalsIgnoreCase("Uncategorized")) {
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                GradingCategory catInBox = categoryComboBox.getItemAt(i);
                if (catInBox != null && catInBox.getName().equals(assignmentCategoryName)) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            categoryComboBox.setSelectedIndex(0);
        }

        // Set graded status
        isGradedCheckBox.setSelected(assignment.isGraded());
        
        // Set group assignment status
        Group group = assignment.getGroup();
        if (group != null) {
            groupCheckbox.setSelected(true);
            groupSelectionCombo.setSelectedItem(group.getGroupName());
            groupSelectionCombo.setEnabled(true);
        } else {
            groupCheckbox.setSelected(false);
            groupSelectionCombo.setEnabled(false);
        }
    }

    private void addActionListeners() {
        cancelButton.addActionListener(e -> this.dispose());
        saveButton.addActionListener(e -> saveAssignment());
    }

    private void saveAssignment() {
        System.out.println("AssignmentView save button trying to save...");

        // Get data from fields
        String name = nameField.getText().trim();
        String pointsStr = pointsField.getText().trim();
        String dueDateStr = dueDateField.getText().trim();
        Object selectedCategoryObj = categoryComboBox.getSelectedItem();
        boolean isGraded = isGradedCheckBox.isSelected();
        String description = descriptionArea.getText();
        
        // Get group info
        Group group = null;
        if (groupCheckbox.isSelected() && groupSelectionCombo.getSelectedItem() != null) {
            String groupName = (String) groupSelectionCombo.getSelectedItem();
            if (currentCourse != null) {
                group = currentCourse.getGroups().stream()
                    .filter(g -> g.getGroupName().equals(groupName))
                    .findFirst()
                    .orElse(null);
            }
        }

        // Validate input
        if (name.isEmpty()) {
            showError("Assignment name cannot be empty.");
            return;
        }
        double points = -1.0;
        try {
            points = Double.parseDouble(pointsStr);
            if (points < 0) throw new NumberFormatException("Points cannot be negative");
        } catch (NumberFormatException ex) {
            showError("Points Possible must be a valid non negative number.");
            return;
        }

        // Get category name
        GradingCategory categoryObject = null;
        if (selectedCategoryObj instanceof GradingCategory) {
            categoryObject = (GradingCategory) selectedCategoryObj;
        }
        String categoryName = (categoryObject != null) ? categoryObject.getName() : null;

        // Call Controller to save
        boolean success = false;
        boolean isEditing = (this.currentAssignment != null);

        if (isEditing) {
            // Editing existing assignment
            System.out.println("AssignmentView saving changes to: " + currentAssignment.getName());
            if (assignmentController != null) {
                success = assignmentController.editAssignmentDetails(currentAssignment, name, points);
                try {
                    currentAssignment.setCategoryName(categoryName);
                    currentAssignment.setDescription(description);
                    currentAssignment.setGroupName(group); // Set the group
                    boolean gradedSuccess = assignmentController.markAssignmentGraded(currentAssignment, isGraded);
                    success = success && gradedSuccess;
                } catch (Exception e) {
                    System.out.println("Error setting assignment properties");
                    success = false;
                }
            }
        } else {
            // Creating new assignment
            System.out.println("AssignmentView creating new assignment: " + name);
            if (teacherController != null && currentCourse != null) {
                try {
                    Assignment newAssignment = new Assignment(name, points, dueDateStr, categoryName, group);
                    newAssignment.setDescription(description);
                    newAssignment.setGraded(isGraded);
                    success = teacherController.addAssignmentToCourse(newAssignment, currentCourse);
                } catch (Exception ex) {
                    showError("Error creating assignment: " + ex.getMessage());
                    success = false;
                }
            }
        }

        if (success) {
            this.saved = true;
            System.out.println("Assignment save successful, closing dialog.");
            this.dispose();
        } else {
            showError("Failed to save assignment changes. Check inputs or console.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean wasSaveSuccessful() {
        return saved;
    }
}