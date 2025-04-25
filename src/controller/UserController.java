package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Role;
import model.Student;
import model.Teacher;
import model.User;

import java.io.*;

public class UserController {
    private final String folderPath = "accounts";
    private final Gson gson;

    public UserController() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public User createAccount(String firstName, String lastName, String username, String password, Role role) {
        
        File file = new File(folderPath + "/" + username+".json");
        String id = generateUserId(username);
        String email=username+"@arizona.edu";

        if (file.exists()) {
            return null; // Username already taken
        }
        
       
        User newUser = null;
        if (role == Role.STUDENT) {
            newUser = new Student(firstName, lastName, email, password, username, id);
        } 
        
        else if (role == Role.TEACHER) {
            newUser = new Teacher(firstName, lastName, email, password, username, id);
        }

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(newUser, writer);
            return newUser;
        } 
        
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User login(String username, String password) {
        String fileName = getFileName(username);
        File file = new File(folderPath + "/" + fileName);
        if (!file.exists()) return null;

        try (Reader reader = new FileReader(file)) {
        	JsonObject raw = JsonParser.parseReader(reader).getAsJsonObject();
            String role = raw.get("role").getAsString();
            Gson gson = new Gson();
            User user = gson.fromJson(reader, User.class);
            
            if (role.equals("STUDENT")) {
                return gson.fromJson(raw, Student.class);
            } 
            
            else if (role.equals("TEACHER")) {
                return gson.fromJson(raw, Teacher.class);
            }
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public void logout(User user) {
        System.out.println(user.getUsername() + " logged out.");
    }

    private String getFileName(String username) {
        return username + ".json";
    }
    
    public User findUserByUsername(String username) {
        String fileName = getFileName(username);
        File file = new File(folderPath + "/" + fileName);
        
        if (!file.exists()) return null;

        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, User.class);
        } 
        
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Generate a unique id for each unique username 
    private String generateUserId(String username) {
        return String.format("1%09d", Math.abs(username.hashCode()));
    }


}
