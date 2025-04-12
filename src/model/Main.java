package model;

import  controller.UserController;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UserController controller = new UserController();
		
		User newUser= controller.createAccount("Harun", "Andeshmand", "andeshmand", "1234");
			System.out.println(newUser);
		
		
		
		
		
		
		
		
	}

}
