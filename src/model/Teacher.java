package model;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import model.Course; 

/**
 * This Teacher class is for teachers in the gradebook.
 * We add teacher things like teacherId and their course list here.
 * It's the model class for teachers in the gradebook
 * It extends the User class, so it already has the name and login stuff handled
 * I added the teacherId field specific for teachers and a list called coursesTaught 
 * which will hold the actual Course objects later on. 
 * The constructor calls the User one using super and sets up the teacher stuff. 
 * There are also getters for the ID and the course list, and a basic addCourse method. 
 * Right now it's mostly just the structure defining what a Teacher is in the system.
 */
public class Teacher extends User {

    // Holds the teacher's ID number or code.
    // private makes it ENCAPSULATED.
    private String teacherId;

    // List to hold the Course objects the teacher teaches.
    // private avoids ESCAPING REFERENCES issues.
    private List<Course> coursesTaught;
    private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);


    //Consructor
    /**
     * Makes a new Teacher.
     * Needs user info plus the teacher ID.
     * Calls the User constructor first with super for the common stuff.
     * Then sets teacherId and makes an empty list for courses.
     */
    public Teacher(
        String teacherFirstName,
        String teacherLastName,
        String teacherEmail,
        String teacherPassword,
        String teacherUsername,
        String teacherSpecificId
    ) {
        // User constructor handles name, email, username, password hashing.
        super(teacherFirstName, teacherLastName, teacherEmail, teacherPassword, teacherUsername,Role.TEACHER);

        //set the teacher specific id.
        this.teacherId = teacherSpecificId;

        // Make an empty list for courses.
        this.coursesTaught = new ArrayList<>();
    }

    /////////GETTERS

    /**
     * Returns the teacher's ID.
     */
    public String getTeacherId() {
        return this.teacherId;
    }

    /**
     * gives back the list of courses the teacher teaches.
     * Returns a copy of the list for encapsulation, safer so the original isn't accessed.
     * Uses the actual Course type now.
     */
    public List<Course> getCoursesTaught() {
        //return a new ArrayList containing the elements from the internal list.
        //this prevents code outside Teacher from modifying the original coursesTaught list.
        return new ArrayList<>(this.coursesTaught);
    }

    /////////METHODS

    /**
     * Adds a course to this teacher's list.
     * Used when assigning courses, checks if the course is null first.
     * Uses the real Course type.
     *
     * @param courseToAdd The Course object to add.
     */
    public void addCourse(Course courseToAdd) {
        boolean isCourseOk = (courseToAdd != null);
        if (isCourseOk) {
            // check if this specific course is already in our list or not
            boolean courseIsNotAlreadyAdded = !this.coursesTaught.contains(courseToAdd);

            //only add it if it's not already there
            if (courseIsNotAlreadyAdded) {
                 this.coursesTaught.add(courseToAdd);
            }

        } 
        
        else {
            // print if someone tries adding null
            System.out.println("tried to add null course to teacher " + getUsername());
        }
    }
    
    
    

    //OVERRIDES

    /**
     * Makes a text version of the Teacher.
     * for printing and debugging, shows ID and basic user info.
     * Avoids antipatterns by just focusing on teacher data string.
     */
    @Override
    public String toString() {
        String basicUserInfo = "User(Name: " + getFirstName() + " " + getLastName() + ", Username: " + getUsername() + ")";
        int courseCount = 0;
        
        if (this.coursesTaught != null) {
            courseCount = this.coursesTaught.size();
        }
        
        String teacherInfo = "Teacher [ID=" + this.teacherId
                           + ", " + basicUserInfo
                           + ", Course Count=" + courseCount
                           + "]";
        return teacherInfo;
    }
}