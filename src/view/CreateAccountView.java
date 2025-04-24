package view;

import javax.swing.*;
import java.awt.*;

/*View for creating an account. Will show up when users click on crete an account. 
 * Need to figure out how to put the roles and Id while creating the account. 
 * */
public class CreateAccountView extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    private JComboBox<String> roleBox;
   
    private JButton createButton;
    private JButton cancelButton;

    
    public CreateAccountView() {
        setTitle("Create Account");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        // First Name
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        panel.add(firstNameField, gbc); y++;

        // Last Name
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(15);
        panel.add(lastNameField, gbc); y++;

        // Username
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc); y++;

        // Password
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc); y++;

        

        // Role
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"Student", "Teacher"});
        panel.add(roleBox, gbc); y++;

        

        // Buttons
        gbc.gridx = 0; gbc.gridy = y;
        createButton = new JButton("Create Account");
        panel.add(createButton, gbc);
        gbc.gridx = 1;
        cancelButton = new JButton("Cancel");
        panel.add(cancelButton, gbc);

        add(panel);
        setVisible(true);
    }

    //to be implement on controllers later 
    public String getFirstName() { return firstNameField.getText(); }
    public String getLastName() { return lastNameField.getText(); }
    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    
    public String getRole() { return (String) roleBox.getSelectedItem(); }
  
    public JButton getCreateButton() { return createButton; }
    public JButton getCancelButton() { return cancelButton; }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CreateAccountView::new);
    }

}
