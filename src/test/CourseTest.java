package test;

import model.*;
import model.grading.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive test suite for the Course class.
 * Tests all functionality including enrollment, assignments, grading categories,
 * and grade management.
 */
public class CourseTest {
    private Course course;
    private Student student1;
    private Student student2;
    private Assignment assignment1;
    private Assignment assignment2;
    private GradingCategory category;

    @Before
    public void setUp() {
    	// groups
        Group group1 = new Group("Module1");
        Group group2 = new Group("Module2"); 
    	
        course = new Course("Data Structures", "CS201", "Fall 2023", false);
        student1 = new Student("Alice", "Smith", "alice@school.edu", "pass", "asmith", "1001");
        student2 = new Student("Bob", "Jones", "bob@school.edu", "pass", "bjones", "1002");
        assignment1 = new Assignment("LinkedList Project", 100.0, "2023-10-10", "Projects", group2);
        assignment2 = new Assignment("Quiz 1", 50.0, "2023-09-15", "Quizzes", group1);
        category = new GradingCategory("Projects", 0.4, 1);
        
        // Mark assignments as graded
        assignment1.markGraded();
        assignment2.markGraded();
    }

    // ==================== CONSTRUCTOR TESTS ====================

    /**
     * Test 1: Normal case - Constructor with valid parameters
     * Verifies all properties are correctly initialized.
     */
    @Test
    public void testConstructorWithValidParameters() {
        assertEquals("Name should match", "Data Structures", course.getName());
        assertEquals("Course ID should match", "CS201", course.getCourseId());
        assertEquals("Semester should match", "Fall 2023", course.getSemester());
        assertFalse("Should not use categories initially", course.usesCategories());
        assertTrue("Enrolled students should be empty", course.getEnrolledStudents().isEmpty());
        assertTrue("Assignments should be empty", course.getAllAssignments().isEmpty());
    }

    /**
     * Test 2: Edge case - Constructor with null name
     * Verifies IllegalArgumentException is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullName() {
        new Course(null, "CS201", "Fall 2023", false);
    }

    // ==================== STUDENT ENROLLMENT TESTS ====================

    /**
     * Test 3: Normal case - Enroll single student
     * Verifies student is properly enrolled.
     */
    @Test
    public void testEnrollSingleStudent() {
        course.enrollStudent(student1);
        List<Student> enrolled = course.getEnrolledStudents();
        assertEquals("Should have 1 enrolled student", 1, enrolled.size());
        assertTrue("Should contain enrolled student", enrolled.contains(student1));
    }

    /**
     * Test 4: Edge case - Enroll null student
     * Verifies null student is not enrolled.
     */
    @Test
    public void testEnrollNullStudent() {
        course.enrollStudent(null);
        assertTrue("Enrolled students should remain empty", course.getEnrolledStudents().isEmpty());
    }

    /**
     * Test 5: Edge case - Enroll duplicate student
     * Verifies student is only enrolled once.
     */
    @Test
    public void testEnrollDuplicateStudent() {
        course.enrollStudent(student1);
        course.enrollStudent(student1);
        assertEquals("Should only have 1 enrollment", 1, course.getEnrolledStudents().size());
    }

    /**
     * Test 6: Normal case - Remove enrolled student
     * Verifies student is properly removed.
     */
    @Test
    public void testRemoveEnrolledStudent() {
        course.enrollStudent(student1);
        course.removeStudent(student1);
        assertTrue("Enrolled students should be empty", course.getEnrolledStudents().isEmpty());
    }

    // ==================== ASSIGNMENT MANAGEMENT TESTS ====================

    /**
     * Test 7: Normal case - Add single assignment
     * Verifies assignment is properly added.
     */
    @Test
    public void testAddSingleAssignment() {
        course.addAssignment(assignment1);
        List<Assignment> assignments = course.getAllAssignments();
        assertEquals("Should have 1 assignment", 1, assignments.size());
        assertTrue("Should contain added assignment", assignments.contains(assignment1));
    }

    /**
     * Test 8: Edge case - Add null assignment
     * Verifies null assignment is not added.
     */
    @Test
    public void testAddNullAssignment() {
        course.addAssignment(null);
        assertTrue("Assignments should remain empty", course.getAllAssignments().isEmpty());
    }

    /**
     * Test 9: Complex case - Add and remove assignment
     * Verifies assignment is properly removed and student grades are cleared.
     */
    @Test
    public void testAddAndRemoveAssignment() {
        // Setup
        course.enrollStudent(student1);
        course.addAssignment(assignment1);
        assignment1.addGrade(student1.getUsername(), new Grade(90.0, "Good"));
        
        // Verify preconditions
        assertFalse("Assignment should have grades before removal", 
                   assignment1.getAllGrades().isEmpty());
        
        // Test removal
        course.removeAssignment(assignment1);
        
        // Verify post-conditions
        assertFalse("Assignment should be removed from course", 
                   course.getAllAssignments().contains(assignment1));
        assertTrue("Assignment grades should be cleared", 
                  assignment1.getAllGrades().isEmpty());
    }

    // ==================== GRADE MANAGEMENT TESTS ====================

    /**
     * Test 10: Normal case - Get grades for enrolled student
     * Verifies correct grades are returned.
     */
    @Test
    public void testGetGradesForEnrolledStudent() {
        course.enrollStudent(student1);
        course.addAssignment(assignment1);
        assignment1.addGrade(student1.getUsername(), new Grade(85.0, "Good"));
        
        Map<Assignment, Grade> grades = course.getGradesForStudent(student1);
        assertEquals("Should have 1 grade entry", 1, grades.size());
        assertEquals("Grade should match", 85.0, grades.get(assignment1).getPointsEarned(), 0.01);
    }

    /**
     * Test 11: Edge case - Get grades for non-enrolled student
     * Verifies empty map is returned.
     */
    @Test
    public void testGetGradesForNonEnrolledStudent() {
        course.addAssignment(assignment1);
        Map<Assignment, Grade> grades = course.getGradesForStudent(student1);
        assertTrue("Should return empty map", grades.isEmpty());
    }

    /**
     * Test 12: Complex case - Mixed graded and ungraded assignments
     * Verifies only graded assignments are returned.
     */
    @Test
    public void testGetGradesWithUngradedAssignments() {
        // Setup
        course.enrollStudent(student1);
        course.addAssignment(assignment1); // Graded
        Group group3 = new Group("Module3");
        Assignment ungraded = new Assignment("Essay", 100.0, "2023-11-01", "Writing", group3);
        course.addAssignment(ungraded); // Not graded
        
        // Add grade only to assignment1
        assignment1.addGrade(student1.getUsername(), new Grade(90.0, "Excellent"));
        
        // Test
        Map<Assignment, Grade> grades = course.getGradesForStudent(student1);
        assertEquals("Should only have grade for graded assignment", 1, grades.size());
        assertTrue("Should contain graded assignment", grades.containsKey(assignment1));
        assertFalse("Should not contain ungraded assignment", grades.containsKey(ungraded));
    }

    // ==================== CATEGORY MANAGEMENT TESTS ====================

    /**
     * Test 13: Normal case - Add grading category
     * Verifies category is properly added.
     */
    @Test
    public void testAddGradingCategory() {
        course.addGradingCategory(category);
        assertTrue("Should contain added category", course.getGradingCategories().containsKey("Projects"));
    }

    /**
     * Test 14: Edge case - Add duplicate category
     * Verifies category is not duplicated.
     */
    @Test
    public void testAddDuplicateCategory() {
        course.addGradingCategory(category);
        course.addGradingCategory(category);
        assertEquals("Should only have 1 category", 1, course.getGradingCategories().size());
    }

    /**
     * Test 15: Complex case - Assignment added to category
     * Verifies assignment is properly categorized.
     */
    @Test
    public void testAssignmentAddedToCategory() {
        // Enable categories and add one
        course = new Course("Data Structures", "CS201", "Fall 2023", true);
        course.addGradingCategory(category);
        
        // Add assignment that matches category
        course.addAssignment(assignment1);
        
        // Verify
        List<Assignment> categoryAssignments = course.getAssignmentsByCategory("Projects");
        assertEquals("Should have 1 assignment in category", 1, categoryAssignments.size());
        assertTrue("Should contain the assignment", categoryAssignments.contains(assignment1));
    }

    // ==================== GROUP MANAGEMENT TESTS ====================

    /**
     * Test 16: Normal case - Get assignments by group
     * Verifies correct group filtering.
     */
    @Test
    public void testGetAssignmentsByGroup() {
        course.addAssignment(assignment1); // Group: Module2
        course.addAssignment(assignment2); // Group: Module1
        
        List<Assignment> module2Assignments = course.getGroupAssignments("Module2");
        assertEquals("Should have 1 assignment in group", 1, module2Assignments.size());
        assertTrue("Should contain the assignment", module2Assignments.contains(assignment1));
    }

    // ==================== CALCULATOR TESTS ====================

    /**
     * Test 17: Normal case - Set and get grade calculator
     * Verifies calculator is properly set.
     */
    @Test
    public void testSetAndGetGradeCalculator() {
        GradeCalculator calculator = new PointsBasedCalculator();
        course.setGradeCalculator(calculator);
        assertEquals("Should return set calculator", calculator, course.getGradeCalculator());
    }

    // ==================== COMPLEX SCENARIO TESTS ====================

    /**
     * Test 18: Complex case - Full course workflow
     * Verifies complete course lifecycle with multiple operations.
     */
    @Test
    public void testFullCourseWorkflow() {
        // 1. Enroll students
        course.enrollStudent(student1);
        course.enrollStudent(student2);
        assertEquals("Should have 2 enrolled students", 2, course.getEnrolledStudents().size());

        // 2. Add assignments
        course.addAssignment(assignment1);
        course.addAssignment(assignment2);
        assertEquals("Should have 2 assignments", 2, course.getAllAssignments().size());

        // 3. Add grades
        assignment1.addGrade(student1.getUsername(), new Grade(95.0, "Excellent"));
        assignment2.addGrade(student1.getUsername(), new Grade(80.0, "Good"));
        
        // 4. Verify grade retrieval
        Map<Assignment, Grade> grades = course.getGradesForStudent(student1);
        assertEquals("Should have 2 grades", 2, grades.size());
        assertEquals("First grade should match", 95.0, grades.get(assignment1).getPointsEarned(), 0.01);
        
        // 5. Test ungraded assignments
        List<Assignment> ungraded = course.getUngradedAssignmentsForStudent(student2);
        assertEquals("Should have 2 ungraded for student2", 2, ungraded.size());
    }

    /**
     * Test 19: Complex case - Category-based assignment management
     * Verifies proper handling when categories are enabled.
     */
    @Test
    public void testCategoryBasedAssignmentManagement() {
        // Setup course with categories
        course = new Course("Algorithms", "CS301", "Spring 2024", true);
        course.addGradingCategory(new GradingCategory("Homework", 0.3, 1));
        course.addGradingCategory(new GradingCategory("Exams", 0.7, 0));
        Group group4 = new Group("Group 4");
        Group group5 = new Group("Group 5");

        
        // Add assignments to different categories
        Assignment hw1 = new Assignment("HW1", 50.0, "2024-02-01", "Homework", group4);
        Assignment exam1 = new Assignment("Midterm", 100.0, "2024-03-15", "Exams", group5);
        course.addAssignment(hw1);
        course.addAssignment(exam1);
        
        // Verify category assignments
        assertEquals("Should have 1 homework", 1, course.getAssignmentsByCategory("Homework").size());
        assertEquals("Should have 1 exam", 1, course.getAssignmentsByCategory("Exams").size());
    }

    // ==================== PROPERTY CHANGE TESTS ====================

    /**
     * Test 20: Normal case - Property change notification
     * Verifies listeners are notified of changes.
     */
    @Test
    public void testPropertyChangeNotification() {
        final boolean[] notified = {false};
        
        course.addPropertyChangeListener(evt -> {
            notified[0] = true;
            assertEquals("Event should be for student enrollment", "studentEnrolled", evt.getPropertyName());
            assertEquals("Event value should be student", student1, evt.getNewValue());
        });
        
        course.enrollStudent(student1);
        assertTrue("Listener should be notified", notified[0]);
    }
}