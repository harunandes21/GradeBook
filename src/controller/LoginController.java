package controller;

import model.User;

/**
 * This controller handles the login process.
 * Its going to connect to  the login screen (LoginView) to the user system (UserController).
 *
 */
public class LoginController {

    
    private UserController userController;

    /**
     * Creates a new LoginController and links it to the UserController.
     *
     * @param userController the helper class that handles login checking and user files
     */
    public LoginController(UserController userController) {
        this.userController = userController;
    }

    /**
     * Tries to log in using a username and password.
     * If the info is correct, it returns the User object. If not, it returns null.
     *
     * @param username the login username typed by the user
     * @param password the login password typed by the user
     * @return the User object if login works, or null if it fails
     */
    public User login(String username, String password) {
        return userController.login(username, password);
    }
}
