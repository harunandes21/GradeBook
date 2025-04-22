package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    public User createAccount(String firstName, String lastName, String username, String password) {
        
        File file = new File(folderPath + "/" + username+".json");

        if (file.exists()) {
            return null; // Username already taken
        }
        
        String email=username+"@arizona.edu";
        User newUser = new User(firstName, lastName,email,password, username);

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(newUser, writer);
            return newUser;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User login(String username, String password) {
        String fileName = getFileName(username);
        File file = new File(folderPath + "/" + fileName);
        if (!file.exists()) return null;

        try (Reader reader = new FileReader(file)) {
            User user = gson.fromJson(reader, User.class);
            if (user != null && user.checkPassword(password)) {
                return user;
            }
        } catch (IOException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
