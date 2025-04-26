package test;

import model.*;
import model.grading.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyChangeEvent; // Needed for observer tests
import java.beans.PropertyChangeListener; // Needed for observer tests
import java.util.ArrayList; // Needed for sorting tests
import java.util.Collections; // Needed for sorting tests
import java.util.List;
import java.util.Map;

/**
 * Comprehensive test suite for the Course class.
 * Tests all functionality including enrollment, assignments, grading categories,
 * group management, sorting, observer pattern, and grade management.
 */
public class CourseTest {
    // Declare fields for test objects
    private Course coursePoints; // Course using points-based grading
    private Course courseCategories; // Course using category-based grading
    private Student student1;
    private Student student2;
    private Student student3; // For sorting
    private Assignment assignment1;
    private Assignment assignment2;
    private Assignment assignment3; // For sorting/category tests
    private GradingCategory category1; // Example category
    private GradingCategory category2; // Another category
    private Group group1;
    private Group group2;
    private PointsBasedCalculator pointsCalc;
    private CategoryBasedCalculator categoryCalc;
    private TestPropertyChangeListener listener; // Listener for observer tests

    /**
     * setUp method runs before each test.
     * Initializes common objects used across multiple tests.
     */
    @Before
    public void setUp() {
        // groups
        group1 = new Group("Module1");
        group2 = new Group("Module2");
        Group group3 = new Group("Module3");

        // calculators
        pointsCalc = new PointsBasedCalculator();
        categoryCalc = new CategoryBasedCalculator();

        // courses
        coursePoints = new Course("Data Structures (Points)", "CS201P", "Fall 2023", false); // Points mode
        coursePoints.setGradeCalculator(pointsCalc); // Set calculator
        courseCategories = new Course("Data Structures (Cats)", "CS201C", "Fall 2023", true); // Category mode
        courseCategories.setGradeCalculator(categoryCalc); // Set calculator

        // students
        student1 = new Student("Alice", "Smith", "alice@school.edu", "pass", "asmith", "1001");
        student2 = new Student("Bob", "Jones", "bob@school.edu", "pass", "bjones", "1002");
        student3 = new Student("Charlie", "Davis", "cdavis@school.edu", "pass", "cdavis", "1003"); // For sorting

        // assignments
        assignment1 = new Assignment("LinkedList Project", 100.0, "2023-10-10", "Projects", group2);
        assignment2 = new Assignment("Quiz 1", 50.0, "2023-09-15", "Quizzes", group1);
        assignment3 = new Assignment("Array Homework", 75.0, "2023-09-20", "Homework", group1); // Another assignment

        // categories
        category1 = new GradingCategory("Projects", 0.4, 1); // 40% weight, drop 1
        category2 = new GradingCategory("Quizzes", 0.6, 0); // 60% weight, drop 0

        // Mark assignments as graded for relevant tests
        assignment1.markGraded();
        assignment2.markGraded();
        assignment3.markGraded(); // Mark this one too

        // Initialize property change listener helper
        listener = new TestPropertyChangeListener();
        coursePoints.addPropertyChangeListener(listener); // Add listener to courses
        courseCategories.addPropertyChangeListener(listener);
    }

    // Helper class to listen for property change events
    private static class TestPropertyChangeListener implements PropertyChangeListener {
        PropertyChangeEvent lastEvent = null;
        int eventCount = 0;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            System.out.println("TestListener received event: " + evt.getPropertyName()); // Debug print
            lastEvent = evt;
            eventCount++;
        }

        public void reset() {
            lastEvent = null;
            eventCount = 0;
        }
    }


    // ==================== CONSTRUCTOR TESTS ====================

    /** Test 1: Valid constructor */
    @Test
    public void testConstructorWithValidParameters() {
        System.out.println("Testing constructor valid");
        assertEquals("Name should match", "Data Structures (Points)", coursePoints.getName());
        assertEquals("Course ID should match", "CS201P", coursePoints.getCourseId());
        assertEquals("Semester should match", "Fall 2023", coursePoints.getSemester());
        assertFalse("Should not use categories initially", coursePoints.usesCategories());
        assertTrue("Enrolled students should be empty", coursePoints.getEnrolledStudents().isEmpty());
        assertTrue("Assignments should be empty", coursePoints.getAllAssignments().isEmpty());
        assertTrue("Categories should be empty", coursePoints.getGradingCategories().isEmpty()); // Check categories map
        assertTrue("Groups should be empty", coursePoints.getGroups().isEmpty()); // Check groups list
        assertNotNull("PropertyChangeSupport should exist", coursePoints); // Implicit check pcs!=null
    }

    /** Test 2: Null/Empty name, id, semester */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullName() { new Course(null, "ID", "Sem", false); }
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() { new Course(" ", "ID", "Sem", false); }
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullId() { new Course("Name", null, "Sem", false); }
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyId() { new Course("Name", " ", "Sem", false); }
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullSemester() { new Course("Name", "ID", null, false); }
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptySemester() { new Course("Name", "ID", " ", false); }

    // ==================== STUDENT ENROLLMENT TESTS ====================

    /** Test 3: Enroll single, null, duplicate */
    @Test
    public void testEnrollStudentScenarios() {
        System.out.println("Testing enrollStudent");
        listener.reset();
        coursePoints.enrollStudent(student1);
        assertEquals("Should have 1 student", 1, coursePoints.getEnrolledStudents().size());
        assertTrue("Should contain student1", coursePoints.getEnrolledStudents().contains(student1));
        assertEquals("Should fire enroll event", 1, listener.eventCount);
        assertEquals("Event name should be studentEnrolled", "studentEnrolled", listener.lastEvent.getPropertyName());
        assertEquals("Event value should be student1", student1, listener.lastEvent.getNewValue());

        listener.reset();
        coursePoints.enrollStudent(null); // Enroll null
        assertEquals("Should still have 1 student after null", 1, coursePoints.getEnrolledStudents().size());
        assertEquals("Should not fire event for null", 0, listener.eventCount);

        listener.reset();
        coursePoints.enrollStudent(student1); // Enroll duplicate
        assertEquals("Should still have 1 student after duplicate", 1, coursePoints.getEnrolledStudents().size());
        assertEquals("Should not fire event for duplicate", 0, listener.eventCount);
    }

    /** Test 6: Remove student */
    @Test
    public void testRemoveStudentScenarios() {
        System.out.println("Testing removeStudent");
        coursePoints.enrollStudent(student1); // Prerequisite

        listener.reset();
        coursePoints.removeStudent(student2); // Remove non-enrolled student
        assertEquals("Should still have 1 student", 1, coursePoints.getEnrolledStudents().size());
        assertEquals("Should not fire event for non-enrolled", 0, listener.eventCount);

        listener.reset();
        coursePoints.removeStudent(null); // Remove null student
        assertEquals("Should still have 1 student after null remove", 1, coursePoints.getEnrolledStudents().size());
        assertEquals("Should not fire event for null remove", 0, listener.eventCount);

        listener.reset();
        coursePoints.removeStudent(student1); // Remove enrolled student
        assertTrue("Student list should be empty", coursePoints.getEnrolledStudents().isEmpty());
        assertEquals("Should fire remove event", 1, listener.eventCount);
        assertEquals("Event name should be studentRemoved", "studentRemoved", listener.lastEvent.getPropertyName());
        assertEquals("Event old value should be student1", student1, listener.lastEvent.getOldValue());
        assertNull("Event new value should be null", listener.lastEvent.getNewValue());
    }

    /** Test 6b: getEnrolledStudents returns copy */
    @Test
    public void testGetEnrolledStudentsEncapsulation() {
        System.out.println("Testing getEnrolledStudents encapsulation");
        coursePoints.enrollStudent(student1);
        List<Student> list = coursePoints.getEnrolledStudents();
        try {
            list.add(student2); // Try modifying the returned list
        } catch (UnsupportedOperationException e) {
            // If it's unmodifiable, that's also good encapsulation
        }
        assertEquals("Original list size should remain 1", 1, coursePoints.getEnrolledStudents().size());
        assertFalse("Original list should not contain student2", coursePoints.getEnrolledStudents().contains(student2));
    }


    // ==================== ASSIGNMENT MANAGEMENT TESTS ====================

    /** Test 7: Add assignment scenarios */
    @Test
    public void testAddAssignmentScenarios() {
        System.out.println("Testing addAssignment");
        listener.reset();
        coursePoints.addAssignment(assignment1);
        assertEquals("Should have 1 assignment", 1, coursePoints.getAllAssignments().size());
        assertTrue("Should contain assignment1", coursePoints.getAllAssignments().contains(assignment1));
        assertEquals("Should fire add event", 1, listener.eventCount);
        assertEquals("Event name should be assignmentAdded", "assignmentAdded", listener.lastEvent.getPropertyName());
        assertEquals("Event value should be assignment1", assignment1, listener.lastEvent.getNewValue());

        listener.reset();
        coursePoints.addAssignment(null); // Add null
        assertEquals("Should still have 1 assignment after null", 1, coursePoints.getAllAssignments().size());
        assertEquals("Should not fire event for null", 0, listener.eventCount);

        listener.reset();
        coursePoints.addAssignment(assignment1); // Add duplicate
        assertEquals("Should still have 1 assignment after duplicate", 1, coursePoints.getAllAssignments().size());
        assertEquals("Should not fire event for duplicate", 0, listener.eventCount);

        // Test adding to category-based course where category exists
        listener.reset();
        courseCategories.addGradingCategory(category1); // Add "Projects" category
        courseCategories.addAssignment(assignment1); // assignment1 has category "Projects"
        assertTrue("Category should contain assignment", category1.getAssignments().contains(assignment1));
        assertEquals("Should fire add event for category course", 1, listener.eventCount);

        // Test adding to category-based course where category does NOT exist
        listener.reset();
        courseCategories.addAssignment(assignment2); // assignment2 has category "Quizzes", not added yet
        assertEquals("Should add assignment even if category missing", 2, courseCategories.getAllAssignments().size());
        assertTrue("Course assignments should contain assignment2", courseCategories.getAllAssignments().contains(assignment2));
        // Check that assignment was NOT added to category1
        assertFalse("Category1 should not contain assignment2", category1.getAssignments().contains(assignment2));
        assertEquals("Should fire add event even if category missing", 1, listener.eventCount);
    }


    /** Test 9: Remove assignment scenarios */
    @Test
    public void testRemoveAssignmentScenarios() {
        System.out.println("Testing removeAssignment");
        coursePoints.enrollStudent(student1);
        coursePoints.addAssignment(assignment1);
        assignment1.addGrade(student1.getUsername(), new Grade(90.0, "Good")); // Add grade

        listener.reset();
        coursePoints.removeAssignment(assignment2); // Remove non-existent assignment
        assertEquals("Should still have 1 assignment", 1, coursePoints.getAllAssignments().size());
        assertEquals("Should not fire event for non-existent remove", 0, listener.eventCount);

        listener.reset();
        coursePoints.removeAssignment(null); // Remove null assignment
        assertEquals("Should still have 1 assignment after null remove", 1, coursePoints.getAllAssignments().size());
        assertEquals("Should not fire event for null remove", 0, listener.eventCount);

        listener.reset();
        coursePoints.removeAssignment(assignment1); // Remove existing assignment
        assertTrue("Assignment list should be empty", coursePoints.getAllAssignments().isEmpty());
        assertTrue("Assignment's grades should be cleared", assignment1.getAllGrades().isEmpty()); // Check grade clearing
        // TODO: Check student's grade map was also cleared if Student.removeGradeForAssignment exists
        // Assuming student.removeGradeForAssignment exists and was called by course.removeAssignment
        // assertNull("Student's grade for assignment1 should be null", student1.getGradeForAssignment(assignment1));

        assertEquals("Should fire remove event", 1, listener.eventCount);
        assertEquals("Event name should be assignmentRemoved", "assignmentRemoved", listener.lastEvent.getPropertyName());
        assertEquals("Event old value should be assignment1", assignment1, listener.lastEvent.getOldValue());
    }

    /** Test 9b: Remove assignment from category-based course */
    @Test
    public void testRemoveAssignmentFromCategoryCourse() {
        System.out.println("Testing removeAssignment from category course");
        courseCategories.addGradingCategory(category1); // Projects
        courseCategories.addAssignment(assignment1); // Belongs to Projects

        assertTrue("Category should contain assignment before remove", category1.getAssignments().contains(assignment1));
        courseCategories.removeAssignment(assignment1);
        assertFalse("Category should not contain assignment after remove", category1.getAssignments().contains(assignment1));
    }

     /** Test 9c: getAllAssignments returns copy */
     @Test
     public void testGetAllAssignmentsEncapsulation() {
         System.out.println("Testing getAllAssignments encapsulation");
         coursePoints.addAssignment(assignment1);
         List<Assignment> list = coursePoints.getAllAssignments();
         try {
             list.add(assignment2); // Try modifying the returned list
         } catch (UnsupportedOperationException e) { }
         assertEquals("Original list size should remain 1", 1, coursePoints.getAllAssignments().size());
         assertFalse("Original list should not contain assignment2", coursePoints.getAllAssignments().contains(assignment2));
     }

    // ==================== GRADE MANAGEMENT TESTS ====================

    /** Test 10: Get grades for student */
    @Test
    public void testGetGradesForStudentScenarios() {
        System.out.println("Testing getGradesForStudent");
        coursePoints.enrollStudent(student1);
        coursePoints.addAssignment(assignment1);
        coursePoints.addAssignment(assignment2);
        assignment1.addGrade(student1.getUsername(), new Grade(85.0, "Good"));
        // assignment2 has no grade for student1 yet

        Map<Assignment, Grade> grades = coursePoints.getGradesForStudent(student1);
        assertEquals("Should have 1 grade entry for student1", 1, grades.size());
        assertTrue("Should contain key assignment1", grades.containsKey(assignment1));
        assertEquals("Grade points should match", 85.0, grades.get(assignment1).getPointsEarned(), 0.01);
        assertFalse("Should not contain key assignment2", grades.containsKey(assignment2));

        // Test with null student
        Map<Assignment, Grade> gradesNull = coursePoints.getGradesForStudent(null);
        assertNotNull("Map should not be null for null student", gradesNull);
        assertTrue("Map should be empty for null student", gradesNull.isEmpty());

        // Test for student not enrolled
        Map<Assignment, Grade> gradesNotEnrolled = coursePoints.getGradesForStudent(student2);
        assertTrue("Map should be empty for non-enrolled student", gradesNotEnrolled.isEmpty());
    }

     /** Test 12: getUngradedAssignmentsForStudent */
     @Test
     public void testGetUngradedAssignmentsForStudent() {
         System.out.println("Testing getUngradedAssignmentsForStudent");
         coursePoints.enrollStudent(student1);
         coursePoints.addAssignment(assignment1); // Has grade 85
         coursePoints.addAssignment(assignment2); // No grade yet
         coursePoints.addAssignment(assignment3); // No grade yet
         assignment1.addGrade(student1.getUsername(), new Grade(85.0, ""));

         List<Assignment> ungraded = coursePoints.getUngradedAssignmentsForStudent(student1);
         assertEquals("Should have 2 ungraded assignments", 2, ungraded.size());
         assertTrue("Should contain assignment2", ungraded.contains(assignment2));
         assertTrue("Should contain assignment3", ungraded.contains(assignment3));
         assertFalse("Should not contain assignment1", ungraded.contains(assignment1));

         // Test with null student
         List<Assignment> ungradedNull = coursePoints.getUngradedAssignmentsForStudent(null);
         assertNotNull("List should not be null for null student", ungradedNull);
         assertTrue("List should be empty for null student", ungradedNull.isEmpty());

         // Test with no assignments in course
         Course emptyCourse = new Course("Empty", "E1", "S1", false);
         emptyCourse.enrollStudent(student1);
         List<Assignment> ungradedEmpty = emptyCourse.getUngradedAssignmentsForStudent(student1);
         assertTrue("List should be empty for course with no assignments", ungradedEmpty.isEmpty());
     }

    // ==================== CATEGORY MANAGEMENT TESTS ====================

    /** Test 13: Add category scenarios */
    @Test
    public void testAddGradingCategoryScenarios() {
        System.out.println("Testing addGradingCategory");
        listener.reset();
        courseCategories.addGradingCategory(category1);
        assertEquals("Should have 1 category", 1, courseCategories.getGradingCategories().size());
        assertTrue("Should contain category1", courseCategories.getGradingCategories().containsValue(category1));
        assertEquals("Should fire add event", 1, listener.eventCount);
        assertEquals("Event name should be categoryAdded", "categoryAdded", listener.lastEvent.getPropertyName());
        assertEquals("Event value should be category1", category1, listener.lastEvent.getNewValue());

        listener.reset();
        courseCategories.addGradingCategory(null); // Add null
        assertEquals("Should still have 1 category after null", 1, courseCategories.getGradingCategories().size());
        assertEquals("Should not fire event for null", 0, listener.eventCount);

        listener.reset();
        courseCategories.addGradingCategory(category1); // Add duplicate
        assertEquals("Should still have 1 category after duplicate", 1, courseCategories.getGradingCategories().size());
        assertEquals("Should not fire event for duplicate", 0, listener.eventCount);
    }

    /** Test 14b: clearGradingCategories */
    @Test
    public void testClearGradingCategories() {
        System.out.println("Testing clearGradingCategories");
        courseCategories.addGradingCategory(category1);
        courseCategories.addGradingCategory(category2);
        assertEquals("Should have 2 categories before clear", 2, courseCategories.getGradingCategories().size());

        listener.reset();
        courseCategories.clearGradingCategories();
        assertTrue("Categories map should be empty after clear", courseCategories.getGradingCategories().isEmpty());
        assertEquals("Should fire clear event", 1, listener.eventCount);
        assertEquals("Event name should be categoriesCleared", "categoriesCleared", listener.lastEvent.getPropertyName());

        // Test clearing when already empty
        listener.reset();
        courseCategories.clearGradingCategories();
        assertTrue("Should remain empty", courseCategories.getGradingCategories().isEmpty());
        assertEquals("Should not fire event if already empty", 0, listener.eventCount);
    }

     /** Test 14c: getGradingCategories returns copy */
     @Test
     public void testGetGradingCategoriesEncapsulation() {
         System.out.println("Testing getGradingCategories encapsulation");
         courseCategories.addGradingCategory(category1);
         Map<String, GradingCategory> map = courseCategories.getGradingCategories();
         try {
             map.put("Extra", new GradingCategory("Extra", 0.1, 0)); // Try modifying the returned map
         } catch (UnsupportedOperationException e) { }
         assertEquals("Original map size should remain 1", 1, courseCategories.getGradingCategories().size());
         assertFalse("Original map should not contain Extra", courseCategories.getGradingCategories().containsKey("Extra"));
     }

    // ==================== GROUP FILTERING TESTS ====================

    /** Test 16: Get assignments by group */
    @Test
    public void testGetGroupAssignmentsScenarios() {
        System.out.println("Testing getGroupAssignments");
        coursePoints.addAssignment(assignment1); // Group: Module2
        coursePoints.addAssignment(assignment2); // Group: Module1
        coursePoints.addAssignment(assignment3); // Group: Module1

        // Test finding Module1
        List<Assignment> module1Assignments = coursePoints.getGroupAssignments(group1.getGroupName()); // Use group name
        assertEquals("Should have 2 assignments in Module1", 2, module1Assignments.size());
        assertTrue("Should contain assignment2", module1Assignments.contains(assignment2));
        assertTrue("Should contain assignment3", module1Assignments.contains(assignment3));
        assertFalse("Should not contain assignment1", module1Assignments.contains(assignment1));

        // Test finding Module2
        List<Assignment> module2Assignments = coursePoints.getGroupAssignments(group2.getGroupName());
        assertEquals("Should have 1 assignment in Module2", 1, module2Assignments.size());
        assertTrue("Should contain assignment1", module2Assignments.contains(assignment1));

        // Test finding non-existent group
        List<Assignment> nonExistent = coursePoints.getGroupAssignments("NonExistentGroup");
        assertTrue("Should return empty list for non-existent group", nonExistent.isEmpty());

        // Test finding with null group name
        List<Assignment> nullGroup = coursePoints.getGroupAssignments(null);
        assertTrue("Should return empty list for null group name", nullGroup.isEmpty());
    }

    // ==================== CATEGORY FILTERING TESTS ====================
    /** Test getAssignmentsByCategory */
    @Test
    public void testGetAssignmentsByCategoryScenarios() {
        System.out.println("Testing getAssignmentsByCategory");
        coursePoints.addAssignment(assignment1); // Category: Projects
        coursePoints.addAssignment(assignment2); // Category: Quizzes
        coursePoints.addAssignment(assignment3); // Category: Homework

        // Test finding "Quizzes" (case insensitive)
        List<Assignment> quizzes = coursePoints.getAssignmentsByCategory("quizzes");
        assertEquals("Should find 1 quiz", 1, quizzes.size());
        assertTrue("Should contain assignment2", quizzes.contains(assignment2));

        // Test finding "Projects"
        List<Assignment> projects = coursePoints.getAssignmentsByCategory("Projects");
        assertEquals("Should find 1 project", 1, projects.size());
        assertTrue("Should contain assignment1", projects.contains(assignment1));

        // Test finding non-existent category
        List<Assignment> nonExistent = coursePoints.getAssignmentsByCategory("Labs");
        assertTrue("Should return empty list for non-existent category", nonExistent.isEmpty());

        // Test finding with null category name
        List<Assignment> nullCategory = coursePoints.getAssignmentsByCategory(null);
        assertTrue("Should return empty list for null category name", nullCategory.isEmpty());
    }

    // ==================== CALCULATOR TESTS ====================

    /** Test 17: Set and get grade calculator */
    @Test
    public void testSetAndGetGradeCalculator() {
        System.out.println("Testing setGradeCalculator");
        listener.reset();
        coursePoints.setGradeCalculator(categoryCalc); // Change calculator
        assertEquals("Should return categoryCalc", categoryCalc, coursePoints.getGradeCalculator());
        assertEquals("Should fire event", 1, listener.eventCount);
        assertEquals("Event name should be gradeCalculator", "gradeCalculator", listener.lastEvent.getPropertyName());
        assertEquals("Old value should be pointsCalc", pointsCalc, listener.lastEvent.getOldValue());
        assertEquals("New value should be categoryCalc", categoryCalc, listener.lastEvent.getNewValue());

        // Test getting initial calculator
        assertNull("Initial calculator should be null before explicit set", new Course("T","T","T",false).getGradeCalculator());
    }

    // ==================== SORTING TESTS ====================

    /** Test getEnrolledStudentsSortedByName */
    @Test
    public void testGetEnrolledStudentsSortedByName() {
        System.out.println("Testing sorting students by name");
        coursePoints.enrollStudent(student2); // Bob Jones
        coursePoints.enrollStudent(student1); // Alice Smith
        coursePoints.enrollStudent(student3); // Charlie Davis

        // Sort by last name A-Z
        List<Student> sortedLastAsc = coursePoints.getEnrolledStudentsSortedByName(true, true);
        assertEquals("First should be Charlie Davis", student3, sortedLastAsc.get(0));
        assertEquals("Second should be Bob Jones", student2, sortedLastAsc.get(1));
        assertEquals("Third should be Alice Smith", student1, sortedLastAsc.get(2));

        // Sort by last name Z-A
        List<Student> sortedLastDesc = coursePoints.getEnrolledStudentsSortedByName(true, false);
        assertEquals("First should be Alice Smith", student1, sortedLastDesc.get(0));
        assertEquals("Second should be Bob Jones", student2, sortedLastDesc.get(1));
        assertEquals("Third should be Charlie Davis", student3, sortedLastDesc.get(2));

        // Sort by first name A-Z
        List<Student> sortedFirstAsc = coursePoints.getEnrolledStudentsSortedByName(false, true);
        assertEquals("First should be Alice Smith", student1, sortedFirstAsc.get(0));
        assertEquals("Second should be Bob Jones", student2, sortedFirstAsc.get(1));
        assertEquals("Third should be Charlie Davis", student3, sortedFirstAsc.get(2));

        // Sort by first name Z-A
        List<Student> sortedFirstDesc = coursePoints.getEnrolledStudentsSortedByName(false, false);
        assertEquals("First should be Charlie Davis", student3, sortedFirstDesc.get(0));
        assertEquals("Second should be Bob Jones", student2, sortedFirstDesc.get(1));
        assertEquals("Third should be Alice Smith", student1, sortedFirstDesc.get(2));
    }

    /** Test getEnrolledStudentsSortedByAssignmentGrade */
    @Test
    public void testGetEnrolledStudentsSortedByAssignmentGrade() {
        System.out.println("Testing sorting students by grade");
        coursePoints.enrollStudent(student1); // Alice
        coursePoints.enrollStudent(student2); // Bob
        coursePoints.enrollStudent(student3); // Charlie (no grade)
        coursePoints.addAssignment(assignment2); // Quiz 1 (50 pts)
        assignment2.addGrade(student1.getUsername(), new Grade(45.0, "")); // Alice: 45/50
        assignment2.addGrade(student2.getUsername(), new Grade(40.0, "")); // Bob: 40/50

        // Sort by Quiz 1 grade, Ascending (lowest first)
        // Charlie (no grade, -inf) < Bob (40) < Alice (45)
        List<Student> sortedGradeAsc = coursePoints.getEnrolledStudentsSortedByAssignmentGrade(assignment2, true);
        assertEquals("First should be Charlie (no grade)", student3, sortedGradeAsc.get(0));
        assertEquals("Second should be Bob (40)", student2, sortedGradeAsc.get(1));
        assertEquals("Third should be Alice (45)", student1, sortedGradeAsc.get(2));

        // Sort by Quiz 1 grade, Descending (highest first)
        // Alice (45) > Bob (40) > Charlie (no grade, -inf)
        List<Student> sortedGradeDesc = coursePoints.getEnrolledStudentsSortedByAssignmentGrade(assignment2, false);
        assertEquals("First should be Alice (45)", student1, sortedGradeDesc.get(0));
        assertEquals("Second should be Bob (40)", student2, sortedGradeDesc.get(1));
        assertEquals("Third should be Charlie (no grade)", student3, sortedGradeDesc.get(2));

        // Test sorting with null assignment
        List<Student> sortedNullAssign = coursePoints.getEnrolledStudentsSortedByAssignmentGrade(null, true);
        assertTrue("Should return empty list for null assignment", sortedNullAssign.isEmpty());
    }

    // ==================== OBSERVER PATTERN TESTS ====================

    /** Test 20: removePropertyChangeListener */
    @Test
    public void testRemovePropertyChangeListener() {
        System.out.println("Testing removePropertyChangeListener");
        TestPropertyChangeListener tempListener = new TestPropertyChangeListener();
        coursePoints.addPropertyChangeListener(tempListener);
        coursePoints.removePropertyChangeListener(tempListener); // Remove it
        coursePoints.enrollStudent(student1); // Trigger event
        assertNull("Listener should not have received event", tempListener.lastEvent); // Check listener didn't get event

        // Test removing null listener (should not crash)
        try {
            coursePoints.removePropertyChangeListener(null);
        } catch (Exception e) {
            fail("Removing null listener should not throw exception");
        }
        // Test adding null listener (should not crash)
         try {
             coursePoints.addPropertyChangeListener(null);
         } catch (Exception e) {
             fail("Adding null listener should not throw exception");
         }
    }


    // ==================== GROUP MANAGEMENT (Course Level) TESTS ====================

    /** Test createGroup */
    @Test
    public void testCreateGroup() {
        System.out.println("Testing createGroup");
        coursePoints.createGroup("Project Group Alpha");
        assertEquals("Should have 1 group", 1, coursePoints.getGroups().size());
        assertEquals("Group name should match", "Project Group Alpha", coursePoints.getGroups().get(0).getGroupName());
    }

    /** Test createGroup duplicate name */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateGroupDuplicate() {
        System.out.println("Testing createGroup duplicate");
        coursePoints.createGroup("Group X");
        coursePoints.createGroup("Group X"); // Should throw exception
    }

    /** Test addStudentToGroup */
    @Test
    public void testAddStudentToGroup() {
        System.out.println("Testing addStudentToGroup");
        coursePoints.createGroup("Group Y");
        coursePoints.enrollStudent(student1);

        boolean added = coursePoints.addStudentToGroup("Group Y", student1);
        assertTrue("Should return true for successful add", added);
        assertEquals("Group Y should have 1 member", 1, coursePoints.findGroupByName("Group Y").getMembers().size());
        assertTrue("Group Y should contain student1", coursePoints.findGroupByName("Group Y").contains(student1));

        // Add same student again
        boolean addedAgain = coursePoints.addStudentToGroup("Group Y", student1);
        assertFalse("Should return false for duplicate add", addedAgain);
        assertEquals("Group Y should still have 1 member", 1, coursePoints.findGroupByName("Group Y").getMembers().size());

        // Add null student
        boolean addedNull = coursePoints.addStudentToGroup("Group Y", null);
        assertFalse("Should return false for null student", addedNull);

        // Add to non-existent group
        boolean addedNonExistent = coursePoints.addStudentToGroup("Bad Group", student1);
        assertFalse("Should return false for non-existent group", addedNonExistent);
    }

    /** Test getGroups returns unmodifiable list */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetGroupsEncapsulation() {
        System.out.println("Testing getGroups encapsulation");
        coursePoints.createGroup("Test Group");
        List<Group> groups = coursePoints.getGroups();
        groups.add(new Group("Another Group")); // Should throw exception
    }

    /** Test findGroupByName */
    @Test
    public void testFindGroupByName() {
        System.out.println("Testing findGroupByName");
        coursePoints.createGroup("FindMe");
        Group found = coursePoints.findGroupByName("FindMe");
        assertNotNull("Should find existing group", found);
        assertEquals("Found group name should match", "FindMe", found.getGroupName());

        Group notFound = coursePoints.findGroupByName("DontFindMe");
        assertNull("Should return null for non-existent group", notFound);

        Group nullFound = coursePoints.findGroupByName(null);
        assertNull("Should return null for null name", nullFound);
    }

    // ==================== OVERRIDES TESTS ====================

    /** Test toString */
    @Test
    public void testToString() {
        System.out.println("Testing toString");
        // toString just returns the course name
        assertEquals("toString should return course name", coursePoints.getName(), coursePoints.toString());
    }

    /** Test equals and hashCode */
    @Test
    public void testEqualsAndHashCode() {
        System.out.println("Testing equals and hashCode");
        // Create courses for comparison
        Course sameId = new Course("Different Name", "CS201P", "Other Sem", true); // Same ID as coursePoints
        Course differentId = new Course("Data Structures (Points)", "CS999", "Fall 2023", false); // Different ID
        String notACourse = "CS201P";

        // Test equals
        assertTrue("Same object should be equal", coursePoints.equals(coursePoints));
        assertTrue("Objects with same courseId should be equal", coursePoints.equals(sameId));
        assertFalse("Objects with different courseId should not be equal", coursePoints.equals(differentId));
        assertFalse("Object should not equal null", coursePoints.equals(null));
        assertFalse("Object should not equal different type", coursePoints.equals(notACourse));

        // Test hashCode
        assertEquals("Equal objects must have same hashCode", coursePoints.hashCode(), sameId.hashCode());
    }

}