package util; // Putting in a util package maybe? Or controller? Use util for now.

import model.Course;
import model.Student;
import model.User;
import controller.UserController; // Needs UserController to find users
import controller.TeacherController; // Needs TeacherController to add students

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * StudentImporter handles reading student usernames from a file CSV
 * and enrolling EXISTING students into a specific course.
 * It uses the UserController to look up if the username exists and if its a student.
 * It uses the TeacherController to actually add the found student to the course roster.
 * This keeps all the messy file reading and parsing logic separate from the main controller.
 */
public class StudentImporter {

    //need access to these controllers to do the work.
    private UserController userController;
    private TeacherController teacherController;

    /**
     * Constructor requires the controllers needed for its operations.
     * The TeacherController that creates this importer needs to pass them in.
     * @param uController The UserController used to find existing user accounts.
     * @param tController The TeacherController used to enroll students in courses.
     */
    public StudentImporter(UserController uController, TeacherController tController) {
        this.userController = uController;
        this.teacherController = tController; // Store this to call addStudentToCourse later
    }

    /**
     * importFromFile reads a file expects CSV format line: username,firstname,lastname...
     * It goes line by line, skipping empty lines and the first header line.
     * For each line, it gets the username, uses UserController to find that user.
     * If the user exists and is a Student, it uses the TeacherController's
     * addStudentToCourse method to enroll them in the given targetCourse.
     * It does not create new student accounts if a username isn't found.
     *
     * @param filePath The full path String to the CSV file containing student usernames.
     * @param targetCourse The Course object these students should be enrolled into.
     * @return true if the file was read okay and at least one student was found and processed successfully enrolled or already enrolled, false if file reading failed or no students processed.
     */
    public boolean importFromFile(String filePath, Course targetCourse) {
        System.out.println("StudentImporter importing from file: " + filePath + " into course: " + (targetCourse != null ? targetCourse.getName() : "null"));
        // First check if we have everything needed to start.
        boolean haveUserController = (this.userController != null);
        boolean haveTeacherController = (this.teacherController != null);
        boolean courseExists = (targetCourse != null);
        boolean pathExists = (filePath != null && !filePath.isEmpty());

        // If any required part is missing, print message and return false.
        if (!haveUserController || !haveTeacherController || !courseExists || !pathExists) {
             System.out.println("StudentImporter problem: missing controllers, course, or path");
            return false;
        }

        // Keep track if we manage to process at least one student successfully.
        boolean atLeastOneProcessed = false;
        // Use try with resources for the file reader. This makes sure the file
        // gets closed properly even if there are errors during reading.
        t
        ry (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) { // Needs imports
            String currentLine; // Will hold each line read from the file.
            int lineNumber = 0; // Keep track of line number for helpful messages.
            // Read lines until we hit the end of the file readLine returns null then.
            
            while ((currentLine = fileReader.readLine()) != null) {
                lineNumber++;
                // Remove whitespace from beginning and end of the line.
                String trimmedLine = currentLine.trim();
                
                // Skip this line if it's empty or if it's the first line assumed header.
                if (trimmedLine.isEmpty() || lineNumber == 1) {
                    System.out.println("Import skipping line " + lineNumber + " header or empty");
                    continue; // Go to the next iteration of the while loop.
                }

                // Split the line into parts using comma as the separator.
                String[] studentData = trimmedLine.split(",");
                
                // We only really need the first part the username to find the student.
                boolean hasEnoughData = (studentData.length >= 1);

                if (hasEnoughData) {
                    // Get the username from the first column, trim spaces.
                    String usernameFromFile = studentData[0].trim();
                    
                    // Make sure the username isn't empty.
                    if (usernameFromFile.isEmpty()) {
                         System.out.println("Import warning: Skipping line " + lineNumber + ", username is empty");
                         continue; // Skip this line if username missing.
                    }

                    // Ask the UserController to find the User object for this username.
                    User userFound = userController.findUserByUsername(usernameFromFile);
                    Student studentToEnroll = null; // Prepare variable to hold Student if found.

                    // Check if the user controller actually found a user.
                    boolean userExists = (userFound != null);
                    if (userExists) {
                        // Found a user. Now make sure it's a Student and not a Teacher.
                        // instanceof checks the actual object type.
                        if (userFound instanceof Student) {
                            // If it is a Student, cast the User object to Student type.
                            studentToEnroll = (Student) userFound;
                        } 
                        
                        else {
                            //if user exists but has wrong role, print warning, skip this line.
                            System.out.println("Import warning: User '" + usernameFromFile + "' on line " + lineNumber + " exists but is not a Student.");
                        }
                    } 
                    
                    else {
                         //if user controller didn't find the username, print warning, skip line.
                         //we are not creating accounts here.
                         System.out.println("Import warning: User '" + usernameFromFile + "' on line " + lineNumber + " not found in system. Skipping enrollment.");
                    }

                    // Check if we ended up with a valid Student object after the checks.
                    boolean haveStudentToEnroll = (studentToEnroll != null);
                    if (haveStudentToEnroll) {
                        //if yes, use the TeacherController's method to add this student to the course.
                        boolean enrolledOk = teacherController.addStudentToCourse(studentToEnroll, targetCourse);
                        
                        // Check if the add method reported success.
                        if (enrolledOk) {
                             // Log that it worked.
                             System.out.println("Import success: Enrolled student '" + usernameFromFile + "' in course '" + targetCourse.getName() + "'");
                             atLeastOneProcessed = true; // Mark that we did something.
                        } 
                        
                        else {
                             //if it failed maybe already enrolled?, log that too.
                             System.out.println("Import info: Student '" + usernameFromFile + "' failed to add, maybe already enrolled?");
                             atLeastOneProcessed = true; // Still count as processed ok.
                        }
                    }
                    // If we didn't get a studentToEnroll object, just continue to next line.
                } 
                
                else {
                     //if the line didn't have enough data after splitting by comma.
                     System.out.println("Import warning: Skipping line " + lineNumber + ", not enough data needs at least username");
                }
            } //end while loop reading file lines

        } catch (IOException fileError) {
            // This block runs if there's a problem opening or reading the file.
            System.out.println("StudentImporter problem: ERROR reading file '" + filePath + "': " + fileError.getMessage());
            return false; // Return false to signal the import failed.
        } 
        
        catch (Exception generalError) {
            // This block catches any other unexpected error during the process.
            System.out.println("StudentImporter problem: ERROR processing file: " + generalError.getMessage());
            generalError.printStackTrace(); // Print details to help debug.
            return false; // Return false to signal failure.
        }

        //if the whole file was processed, return true only if we managed to process at least one student line.
        return atLeastOneProcessed;
    }
}