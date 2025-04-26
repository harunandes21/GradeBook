package test.controller;

import model.*; 
import model.grading.*;
import controller.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

/**
 * This tests the TeacherController class.
 * Because the controller mostly calls model methods, unit testing it fully
 * without a running UI or mocking frameworks is hard.
 * We focus here on testing methods that have some logic inside them
 * or methods that are easy to set up and verify the results of, like
 * getting lists of things or setting simple properties.
 */
class TeacherControllerTest {

    // Declare variables needed
    private UserController userController;
    private TeacherController teacherController;
    private Teacher testTeacher;
    private Course course1;
    private Course course2;
    private Student student1;
    private Assignment assign1Graded;
    private Assignment assign2Ungraded;
    private Assignment assign3Graded;

    /**
     * setUp runs before each test.
     * Creates controllers, teacher, courses, assignments etc needed.
     */
    @BeforeEach
    void setUp() {
        // Make user controller needs no args
        userController = new UserController();
        // Make teacher create user first for consistency
        User teacherUser = userController.createAccount("Teacher", "User", "teach1", "pw", Role.TEACHER);
        // Make sure it created ok and cast it
        assertNotNull(teacherUser, "Teacher user creation failed for setup");
        assertTrue(teacherUser instanceof Teacher, "Created user should be a Teacher");
        testTeacher = (Teacher) teacherUser;

        // Make the teacher controller, pass it the teacher and user controller
        teacherController = new TeacherController(testTeacher, userController);

        // Make courses and add them to teacher
        course1 = new Course("Course 101", "C101", "S24", false); // Points based
        course2 = new Course("Course 202", "C202", "S24", true); // Category based
        testTeacher.addCourse(course1);
        testTeacher.addCourse(course2);

        // Make a student and enroll in course1
        User studentUser = userController.createAccount("Student", "One", "stud1", "pw", Role.STUDENT);
        assertNotNull(studentUser, "Student user creation failed for setup");
        assertTrue(studentUser instanceof Student, "Created user should be a Student");
        student1 = (Student) studentUser;
        course1.enrollStudent(student1); // Enroll student in course 1

        // Make assignments for course1
        assign1Graded = new Assignment("HW1", 10, "d1", "cat", "g");
        assign2Ungraded = new Assignment("HW2", 20, "d2", "cat", "g");
        assign3Graded = new Assignment("Quiz1", 5, "d3", "cat", "g");
        assign1Graded.setGraded(true); // Mark this one graded
        assign2Ungraded.setGraded(false); // This one is not graded
        assign3Graded.setGraded(true); // This one is graded
        course1.addAssignment(assign1Graded);
        course1.addAssignment(assign2Ungraded);
        course1.addAssignment(assign3Graded);

        // Add a grade for the student on a graded assignment
        student1.addGrade(assign1Graded, new Grade(8.0, ""));
    }

    /**
     * testViewCourses checks if the controller returns the correct courses
     * assigned to the teacher.
     */
    @Test
    void testViewCourses() {
        System.out.println("Testing TeacherController viewCourses");
        // Ask controller for the teacher's courses
        List<Course> courses = teacherController.viewCourses();
        // Check we got the right number back
        assertEquals(2, courses.size(), "Should return 2 courses for the teacher");
        // Check if both courses we added are in the list
        assertTrue(courses.contains(course1), "List should contain course1");
        assertTrue(courses.contains(course2), "List should contain course2");
    }

    /**
     * testViewStudentsInCourse checks getting the student list for a course.
     */
    @Test
    void testViewStudentsInCourse() {
        System.out.println("Testing TeacherController viewStudentsInCourse");
        // Ask controller for students in course1
        List<Student> students = teacherController.viewStudentsInCourse(course1);
        // Check we got 1 student back
        assertEquals(1, students.size(), "Should be 1 student in course1");
        assertTrue(students.contains(student1), "List should contain student1");

        // Check for course2 which should be empty
        students = teacherController.viewStudentsInCourse(course2);
        assertTrue(students.isEmpty(), "Should be 0 students in course2");
    }

    /**
     * testViewStudentsInNullCourse checks edge case getting students for null course.
     * Controller should handle this and return empty list.
     */
    @Test
    void testViewStudentsInNullCourse() {
        System.out.println("Testing TeacherController viewStudentsInCourse with null");
        List<Student> students = teacherController.viewStudentsInCourse(null);
        // Check list is not null and is empty
        assertNotNull(students, "Returned list should not be null");
        assertTrue(students.isEmpty(), "Should return empty list for null course");
    }

    /**
     * testViewUngradedAssignments checks getting only assignments not marked graded.
     */
    @Test
    void testViewUngradedAssignments() {
        System.out.println("Testing TeacherController viewUngradedAssignments");
        // Ask controller for ungraded assignments in course1
        List<Assignment> ungraded = teacherController.viewUngradedAssignments(course1);
        // Check we only got assign2Ungraded back
        assertEquals(1, ungraded.size(), "Should find 1 ungraded assignment");
        assertTrue(ungraded.contains(assign2Ungraded), "List should contain assign2Ungraded");
        assertFalse(ungraded.contains(assign1Graded), "List should not contain assign1Graded");
        assertFalse(ungraded.contains(assign3Graded), "List should not contain assign3Graded");
    }

    /**
     * testViewUngradedAssignmentsNoUngraded checks case where all are graded.
     */
    @Test
    void testViewUngradedAssignmentsNoUngraded() {
        System.out.println("Testing TeacherController viewUngradedAssignments when all are graded");
        // Mark the second assignment as graded too
        assign2Ungraded.setGraded(true);
        // Ask controller for ungraded list
        List<Assignment> ungraded = teacherController.viewUngradedAssignments(course1);
        // List should be empty now
        assertTrue(ungraded.isEmpty(), "Should return empty list when all assignments graded");
    }

    /**
     * testSetCourseGradingMode checks if setting the calculator strategy works.
     */
    @Test
    void testSetCourseGradingMode() {
        System.out.println("Testing TeacherController setCourseGradingMode");
        // Make calculator instances
        PointsBasedCalculator pointsCalc = new PointsBasedCalculator();
        CategoryBasedCalculator categoryCalc = new CategoryBasedCalculator();

        // Set course1 to points based initially checked in setup implicitly?
        // Let's explicitly set it
        boolean success1 = teacherController.setCourseGradingMode(course1, pointsCalc);
        assertTrue(success1, "Setting points mode should succeed");
        // Check if the course actually stored it
        assertEquals(pointsCalc, course1.getGradeCalculator(), "Course should have points calculator");

        // Set course2 to category based
        boolean success2 = teacherController.setCourseGradingMode(course2, categoryCalc);
        assertTrue(success2, "Setting category mode should succeed");
        // Check if course2 stored it
        assertEquals(categoryCalc, course2.getGradeCalculator(), "Course should have category calculator");
    }

    /**
     * testSetupAssignmentCategories checks adding categories to a course.
     * This just checks if the controller calls the course method. Assumes course method works.
     */
    @Test
    void testSetupAssignmentCategories() {
        System.out.println("Testing TeacherController setupAssignmentCategories");
        // Make some category objects
        GradingCategory cat1 = new GradingCategory("Homework", 0.4, 1);
        GradingCategory cat2 = new GradingCategory("Exams", 0.6, 0);
        List<GradingCategory> catsToAdd = new ArrayList<>();
        catsToAdd.add(cat1);
        catsToAdd.add(cat2);

        // Tell controller to set these categories for course2 uses category based
        boolean success = teacherController.setupAssignmentCategories(course2, catsToAdd);
        assertTrue(success, "Setup categories should return true assuming Course method works");

        // Check if the course actually has the categories now
        Map<String, GradingCategory> courseCats = course2.getGradingCategories();
        assertEquals(2, courseCats.size(), "Course should now have 2 categories");
        assertTrue(courseCats.containsKey("Homework"), "Course should contain Homework category");
        assertTrue(courseCats.containsKey("Exams"), "Course should contain Exams category");
    }

}