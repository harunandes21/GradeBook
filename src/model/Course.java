package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.grading.GradeCalculator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * Represents a course in the gradebook system
 */
public class Course {
    private final String name;
    private final String courseId;
    private final String semester;
    private final Map<String, Student> enrolledStudents; // key: username
    private final List<Assignment> assignments;
    private final Map<String, GradingCategory> categories; // key: category name
    private boolean useCategories; // true = weighted mode, false = points mode
    private GradeCalculator gradeCalculator;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);


    

    // Constructor
    public Course(String name, String courseId, String semester, boolean useCategories) {
        if (name == null || courseId == null || semester == null) {
            throw new IllegalArgumentException("Course info must not be null.");
        }

        this.name = name;
        this.courseId = courseId;
        this.semester = semester;
        this.useCategories = useCategories;

        this.enrolledStudents = new HashMap<>();
        this.assignments = new ArrayList<>();
        this.categories = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getSemester() {
        return semester;
    }

    public boolean usesCategories() {
        return useCategories;
    }
    
    // ----------Enrollment----------

    /*
     * Enrolls a student in this course
     */
    public void enrollStudent(Student s) {
        if (s != null && !enrolledStudents.containsKey(s.getUsername())) {
            enrolledStudents.put(s.getUsername(), s);
            s.enrollInCourse(this);
            pcs.firePropertyChange("studentEnrolled", null, s);
        }
    }

    /*
     * Removes a student from this course
     */
    public void removeStudent(Student s) {
        if (s != null) {
            enrolledStudents.remove(s.getUsername());
            pcs.firePropertyChange("studentRemoved", s, null);
        }
    }

    /*
     * Returns a copy of enrolled students list
     */
    public List<Student> getEnrolledStudents() {
        return new ArrayList<Student>(enrolledStudents.values());
    }

    // ----------Assignments and Categories----------
    
    /*
     * Adds assignment to this course
     */
    public void addAssignment(Assignment a) {
        if (a != null) {
            assignments.add(a);
            if (useCategories && categories.containsKey(a.getCategoryName())) {
                categories.get(a.getCategoryName()).addAssignment(a);
            }
            pcs.firePropertyChange("assignmentAdded", null, a);
        }
    }

    /*
     * Adds a grading category to this course
     */
    public void addGradingCategory(GradingCategory category) {
        if (category != null && !categories.containsKey(category.getName())) {
            categories.put(category.getName(), category);
        }
    }
    
    /*
     * Returns a copy of all grading categories
     */
    public Map<String, GradingCategory> getGradingCategories() {
        return new HashMap<>(categories);
    }


    /*
     * Returns a copy of all assignments
     */
    public List<Assignment> getAllAssignments() {
        return new ArrayList<Assignment>(assignments);
    }
  
    /*
     * Returns a map of assignments and their grades for a student
     */
    public Map<Assignment, Grade> getGradesForStudent(Student student) {
        Map<Assignment, Grade> result = new HashMap<>();
        for (Assignment a : assignments) {
            Grade g = a.getGrade(student.getUsername());
            if (g != null) {
                result.put(a, g);
            }
        }
        return result;
    }

    /*
     * Gets ungraded assignments for a student 
     */
    public List<Assignment> getUngradedAssignmentsForStudent(Student student) {
        List<Assignment> result = new ArrayList<Assignment>();
        for (Assignment a : assignments) {
            if (a.getGrade(student.getUsername()) == null) {
                result.add(a);
            }
        }
        return result;
    }
    
    /*
     * Gets assignments in a specific group
     */
    public List<Assignment> getGroupAssignments(String groupName) {
        List<Assignment> groupAssignments = new ArrayList<Assignment>();
        for (Assignment a : assignments) {
            if (groupName.equals(a.getGroupName())) {
                groupAssignments.add(a);
            }
        }
        return groupAssignments;
    }
    
    /*
     * Gets assignments in a specific grading category
     */
    public List<Assignment> getAssignmentsByCategory(String categoryName) {
        List<Assignment> categoryAssignments = new ArrayList<Assignment>();
        for (Assignment a : assignments) {
            if (categoryName.equals(a.getCategoryName())) {
                categoryAssignments.add(a);
            }
        }
        return categoryAssignments;
    }
    
    
    /*
     * removes it from the list of course assignments.
     * removes it from its category (if categories are used).
     * removes it from every student’s grade so it doesn't mess up GPA or course averages later.
     * 
     */
    public void removeAssignment(Assignment a) {
        if (a != null && assignments.contains(a)) {
            assignments.remove(a);

            
            if (useCategories && categories.containsKey(a.getCategoryName())) {
                categories.get(a.getCategoryName()).removeAssignment(a);
            }

            
            Map<String, Grade> grades = a.getAllGrades();
            for (String studentUsername : grades.keySet()) {
                Student student = enrolledStudents.get(studentUsername);
                if (student != null) {
                    student.getGrades().remove(a); 
                }
            }
        }
    }


    @Override
    public String toString() {
        return name;
    }
    public void clearGradingCategories() {
        categories.clear();
    }

    public void setGradeCalculator(GradeCalculator gc) {
        this.gradeCalculator = gc;
    }

    public GradeCalculator getGradeCalculator() {
        return this.gradeCalculator;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }


}
