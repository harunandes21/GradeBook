package model;

import org.mindrot.jbcrypt.BCrypt;


public class User {
	
	private String firstName;
	private String lastName;
	private String email;
	private String hashedPass;
	private String username;
	private Role role;
	
	public User(String firstName, String lastName, String email, String pass, String username, Role role) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.hashedPass = hashPassword(pass);
		this.username = username;
		this.role =role;
	}
	
	public String getUsername() {
        return username;
    }
	
	public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
    
    public Role getRole() {
        return role;
    }
	
	public boolean checkPassword(String inputPassword) {
        return BCrypt.checkpw(inputPassword, hashedPass);
    }
	
	private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

	
	
	

}
