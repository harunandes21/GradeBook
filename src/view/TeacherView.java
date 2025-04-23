package view;

import javax.swing.*;
// We will need imports later for layouts, components, events, etc.
// import java.awt.*;
// import java.awt.event.*;
// Maybe need imports for model data later
// import model.*;
// import java.util.List;

/**
 * This class TeacherView will be be the main window the teacher sees.
 * It needs to show stuff like the courses they teach, the students in a course,
 * assignments, grades, and have buttons for teacher actions like adding grades.
 * This is just the basic frame setup for now.
 * TODO: Need to add all the actual GUI parts like tables, buttons, lists later
 *       and connect them to the TeacherController.
 */
public class TeacherView extends JFrame {

    //GUI parts
    // We will declare all the buttons, tables, lists etc here later.
    // For now, just a placeholder label.
    private JLabel placeholderLabel;

    /**
     * Constructor, sets up the basic window frame.
     * puts a simple placeholder message inside for now.
     * TODO: Replace placeholder with real layout managers and add actual JTable, JButton, JList components.
     */
    public TeacherView() {
    	
        this.setTitle("Teacher Dashboard"); //title bar text
        this.setSize(800, 600); //set initial size (width, height)
        this.setLocationRelativeTo(null); //center window on screen
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close app when this window closes

        // Just put a simple label in the middle for now so it's not empty.
        placeholderLabel = new JLabel("Teacher View GUI");
        // center label text
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Add the label to the frame's content area.
        this.add(placeholderLabel);

        System.out.println("TeacherView created skeleton");

    }

    //getters for Components
    // TODO: Add public getter methods here later for each important GUI component
    //        so the Controller can access them and add listeners or update data.

    //Methods to update view data
    // TODO: Add public methods here later that the Controller or Observer pattern
    //       can call to update what's shown in the view.

}