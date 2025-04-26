package test.model;

import model.Teacher;
import model.Course;
import model.Role; // Need Role enum

import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.BeforeEach; // For setup method run before each test
import org.junit.jupiter.api.Test; // For marking test methods

import java.util.List;

/**
 * This tests the Teacher class from the model package.
 * We need to make sure teachers get created right,
 * that we can add courses to them, and that getting their
 * info works okay, especially getting the course list safely.
 */
class TeacherTest {

    // Declare variables needed for tests up here
    private Teacher testTeacher;
    private Course course1;
    private Course course2;

    /**
     * setUp method runs before every single @Test method below.
     * It just creates a fresh Teacher and some Course objects so each
     * test starts with the same clean setup. Avoids repeating code.
     */
    @BeforeEach
    void setUp() {
        // Make a new teacher object for testing
        testTeacher = new Teacher(
            "Professor", "Doe", "Doe@school.edu",
            "password123", "Doe", "T001"
        );
        // Make some course objects to use in tests
        course1 = new Course("Intro to Stuff", "CSC101", "Fall 2024", false);
        course2 = new Course("Advanced Stuff", "CSC400", "Fall 2024", true);
    }

    /**
     * testConstructorAndGetters checks if the teacher object is created
     * with the right details we passed into the constructor.
     * It uses assertEquals to compare the expected value with what the
     * getter methods actually return. Also checks the role.
     */
    @Test
    void testConstructorAndGetters() {
        System.out.println("Testing Teacher constructor and basic getters");
        // Check first name
        assertEquals("Professor", testTeacher.getFirstName(), "First name should be set correctly");
        // Check last name
        assertEquals("Doe", testTeacher.getLastName(), "Last name should be set correctly");
        // Check email
        assertEquals("Doe@school.edu", testTeacher.getEmail(), "Email should be set correctly");
        // Check username
        assertEquals("Doe", testTeacher.getUsername(), "Username should be set correctly");
        // Check teacher specific ID
        assertEquals("T001", testTeacher.getTeacherId(), "Teacher ID should be set correctly");
        // Check role inherited from User should be TEACHER
        assertEquals(Role.TEACHER, testTeacher.getRole(), "Role should be TEACHER");
        // Check the initial course list is empty but not null
        assertNotNull(testTeacher.getCoursesTaught(), "Course list should exist");
        assertTrue(testTeacher.getCoursesTaught().isEmpty(), "Course list should be empty initially");
    }

    /**
     * testAddCourse checks if adding a valid course works.
     * It adds one course, checks if the list size is 1.
     * Adds another course, checks if size is 2.
     */
    @Test
    void testAddCourse() {
        System.out.println("Testing adding valid courses");
        // Add the first course
        testTeacher.addCourse(course1);
        // Get the list and check size
        List<Course> courses = testTeacher.getCoursesTaught();
        assertEquals(1, courses.size(), "Should have 1 course after adding one");
        assertTrue(courses.contains(course1), "List should contain course1");

        // Add the second course
        testTeacher.addCourse(course2);
        // Get the list again check size
        courses = testTeacher.getCoursesTaught();
        assertEquals(2, courses.size(), "Should have 2 courses after adding second");
        assertTrue(courses.contains(course2), "List should contain course2");
    }

    /**
     * testAddNullCourse checks the edge case of trying to add null.
     * The addCourse method should ignore null input and not add it.
     * List size should remain 0.
     */
    @Test
    void testAddNullCourse() {
        System.out.println("Testing adding a null course");
        // Try adding null
        testTeacher.addCourse(null);
        // Get the list and check size is still 0
        List<Course> courses = testTeacher.getCoursesTaught();
        assertTrue(courses.isEmpty(), "Should not add null course, list still empty");
    }

    /**
     * testAddDuplicateCourse checks adding the same course twice.
     * The addCourse method has logic to check if it contains the course already.
     * It should only add the course the first time. Size should be 1.
     */
    @Test
    void testAddDuplicateCourse() {
        System.out.println("Testing adding the same course twice");
        // Add course1
        testTeacher.addCourse(course1);
        // Try adding course1 again
        testTeacher.addCourse(course1);
        // Get list and check size is still 1
        List<Course> courses = testTeacher.getCoursesTaught();
        assertEquals(1, courses.size(), "Should only have 1 course after adding duplicate");
    }

    /**
     * testGetCoursesTaughtEncapsulation checks if the getter method returns a copy.
     * It gets the list, tries to add a course to the *returned* list.
     * Then it gets the list *again* from the teacher object. The second list
     * should not contain the course added to the first copy, proving the
     * internal list wasn't modified encapsulation works.
     */
    @Test
    void testGetCoursesTaughtEncapsulation() {
        System.out.println("Testing getCoursesTaught returns a safe copy");
        // Add initial course
        testTeacher.addCourse(course1);

        // Get the list the first time
        List<Course> listCopy1 = testTeacher.getCoursesTaught();
        // Try to add another course directly to this returned list
        Course course3 = new Course("Extra", "EXT100", "Nowhere", false);
        try {
             listCopy1.add(course3); // This should modify the copy, not the original
             System.out.println("Added course to the returned list copy.");
        } catch (UnsupportedOperationException e) {
             // If getCoursesTaught returns an *unmodifiable* list, adding fails, which is also good encapsulation.
             System.out.println("Returned list is unmodifiable, good.");
        }


        // Now, get the list from the teacher *again*.
        List<Course> listCopy2 = testTeacher.getCoursesTaught();

        // Check the size of the second list. It should not have changed.
        // If Teacher.getCoursesTaught returns 'new ArrayList<>()', size will be 1.
        // If Teacher.getCoursesTaught returns 'Collections.unmodifiableList()', size will be 1.
        // If Teacher.getCoursesTaught returned the original list (bad), size would be 2 here.
        assertEquals(1, listCopy2.size(), "Modifying returned list should not affect original list size");
        assertFalse(listCopy2.contains(course3), "Original list should not contain course3 added to copy");
    }

    /**
     * testToString checks the toString method.
     * It just makes sure the output string contains some expected parts
     * like the username and teacher ID. Not super strict check.
     */
    @Test
    void testToString() {
        System.out.println("Testing toString method");
        String output = testTeacher.toString();
        // Check if the output contains important fields
        assertTrue(output.contains(testTeacher.getUsername()), "toString should include username");
        assertTrue(output.contains(testTeacher.getTeacherId()), "toString should include teacherId");
        assertTrue(output.contains("Teacher"), "toString should indicate it's a Teacher");
        // Check course count initially 0
        assertTrue(output.contains("Course Count=0"), "toString should show 0 courses initially");
        // Add a course and check again
        testTeacher.addCourse(course1);
        output = testTeacher.toString();
        assertTrue(output.contains("Course Count=1"), "toString should show 1 course after adding");
    }

}