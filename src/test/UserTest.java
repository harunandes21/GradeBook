package test;

import model.Role;
import model.User;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for User model.
 * Covers initialization and password handling, getters.
 */
public class UserTest {
    private User user;
    private final String firstName = "Jane";
    private final String lastName = "Doe";
    private final String email = "jane.doe@example.com";
    private final String password = "securepassword";
    private final String username = "janedoe";
    private final Role role = Role.STUDENT;

    @Before
    public void setUp() {
        user = new User(firstName, lastName, email, password, username, role);
    }

    

    @Test
    public void testUserInitialization() {
        assertEquals("First name should match", firstName, user.getFirstName());
        assertEquals("Last name should match", lastName, user.getLastName());
        assertEquals("Email should match", email, user.getEmail());
        assertEquals("Username should match", username, user.getUsername());
        assertEquals("Role should match", role, user.getRole());
    }

    // password tests

    @Test
    public void testCheckCorrectPassword() {
        assertTrue("Correct password should return true", user.checkPassword(password));
    }

    @Test
    public void testCheckIncorrectPassword() {
        assertFalse("Incorrect password should return false", user.checkPassword("wrongpassword"));
    }

    @Test
    public void testCheckEmptyPassword() {
        assertFalse("Empty password should return false", user.checkPassword(""));
    }

    @Test
    public void testCheckNullPassword() {
        assertFalse("Null password should return false", user.checkPassword(null));
    }

    
}
