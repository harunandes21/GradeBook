package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import model.grading.GradeCalculator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * Represents a course in the gradebook system.
 *holds students, assignments, categories, grading settings
 */
public class Course {
    private final String name;
    private final String courseId;
    private final String semester;
    private final List<Group> groups = new ArrayList<>();
    
    // map holds students enrolled, key is username string, value is Student object.
    private final Map<String, Student> enrolledStudents;
    // list holds all assignments defined for this course.
    private final List<Assignment> assignments;
    // map holds grading categories if used, key is category name string, value is GradingCategory object.
    private final Map<String, GradingCategory> categories;
    // flag true means use weighted category mode, false means use simple points mode.
    private boolean useCategories;
    // holds the specific GradeCalculator strategy object PointsBased or CategoryBased
    private GradeCalculator gradeCalculator;
    // This helper object is for the Observer pattern PropertyChangeSupport
    // It manages listeners and firing events when data changes. Marked transient for JSON.
    private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);


    //Constructor
    // Makes a new Course object. Needs name, id, semester, and grading mode boolean.
    // Initializes lists/maps empty. Throws error if required info null.
    public Course(String name, String courseId, String semester, boolean useCategories) {
        if (name == null || name.trim().isEmpty() || courseId == null || courseId.trim().isEmpty() || semester == null || semester.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name, ID, and semester must not be null or empty.");
        }
        this.name = name.trim();
        this.courseId = courseId.trim();
        this.semester = semester.trim();
        this.useCategories = useCategories; // Store if using categories or points
        // Create the empty collections.
        this.enrolledStudents = new HashMap<>();
        this.assignments = new ArrayList<>();
        this.categories = new HashMap<>();
    }

  //getters
    // Gets course name string.
    public String getName() {
    	return name;
    }
    // Gets course ID string.
    public String getCourseId() {
    	return courseId;
    }
    // Gets course semester string.
    public String getSemester() {
    	return semester;
    }
    // Checks if course uses category grading true means yes.
    public boolean usesCategories() {
    	return useCategories;
    }

    //grade calculator strategy

    /**
     * setGradeCalculator stores which calculation strategy this course uses.
     * gets called by the controller when teacher chooses Points or Category mode.
     * Fires observer event if calculator changes.
     * @param gc The GradeCalculator object like PointsBased or CategoryBased
     */
    public void setGradeCalculator(GradeCalculator gc) {
        // Store old value for observer event
        GradeCalculator oldCalculator = this.gradeCalculator;
        // Just assign the calculator strategy object passed in.
        this.gradeCalculator = gc;
        // Notify listeners that the calculator strategy changed.
        pcs.firePropertyChange("gradeCalculator", oldCalculator, gc);
    }

    /**
     * getGradeCalculator returns the currently set calculation strategy object.
     * The controller uses this to calculate student averages.
     * @return The GradeCalculator object, or null if none set.
     */
    public GradeCalculator getGradeCalculator() {
        // Return the stored calculator object.
        return this.gradeCalculator;
    }

    ////////
    //ENROLLMENT

    /**
     * Adds a student to this course's list of enrolled students.
     * Also tells the student object that they are now enrolled in this course,
     * so the relationship is tracked on both sides.
     * Notifies any listeners like GUI comps that a student was enrolled,
     * using the observer pattern through PropertyChangeSupport.
     * this allows views or other parts of the system to automatically react to the change.
     * @param s The Student object to enroll.
     */
    public void enrollStudent(Student s) {
        // check that the student object is valid and not already enrolled
        if (s != null && !enrolledStudents.containsKey(s.getUsername())) {
            // add the student to the internal map using their username as the key
            Student previouslyAdded = enrolledStudents.put(s.getUsername(), s);
            // Check if put returned null which means student was not already there
            if (previouslyAdded == null) {
                 // Also inform the student that they are now part of this course
                 // Assumes Student class has this method.
                 s.enrollInCourse(this);

                 // Notify any observers like views that a new student has been enrolled
                 // The event name is "studentEnrolled". Old value null, new value is the student.
                 pcs.firePropertyChange("studentEnrolled", null, s);
            }
        }
    }

    /**
     * removeStudent removes a student from the course map using their username.
     * Checks if the student exists first.
     * fires observer event if student was actually removed.
     * @param s The Student object to remove.
     */
    public void removeStudent(Student s) {
        //check input and if student actually enrolled using username key.
        if (s != null && enrolledStudents.containsKey(s.getUsername())) {
            //remove student from the map using username key. Returns removed student or null.
            Student removedStudent = enrolledStudents.remove(s.getUsername());
            // Check if remove actually returned the student object meaning it was there.
            if (removedStudent != null) {
                 //notify listeners. Event name "studentRemoved". Old value is student, new is null.
                 pcs.firePropertyChange("studentRemoved", s, null);
            }
        }
    }

    /**
     * getEnrolledStudents returns a copy of the list of students in the course.
     * EXAMPLE OF ENCAPSULATION since this
     * prevents outside code from changing the internal enrolledStudents map.
     * It makes a new list and copies the student objects into it.
     * @return A new List<Student> containing enrolled students.
     */
    public List<Student> getEnrolledStudents() {
        //get the values Student objects from the map and put them in a new ArrayList copy.
        return new ArrayList<Student>(enrolledStudents.values());
    }

    //////////////////////
    //ASSIGNMENTS AND CATEGORIES

    /**
     * addAssignment adds an assignment definition to the course's list.
     * Checks if the assignment is valid and not already added based on equals method.
     * If the course uses categories, it also adds the assignment to the correct category object map.
     * Fires observer event if assignment was actually added.
     * @param a The Assignment object to add.
     */
    public void addAssignment(Assignment a) {
        // check input is not null and assignment isn't already in the list using equals.
        if (a != null && !assignments.contains(a)) {
            // Add to the main assignment list for the course.
            assignments.add(a);

            //if this course is using category weights...
            if (useCategories) {
                // ...then find the category object using the assignment's category name string.
                GradingCategory category = categories.get(a.getCategoryName());

                //if the category object exists...
                if (category != null) {
                    // ...tell it to add this assignment to its internal list too.
                    category.addAssignment(a);
                } else {
                    // Print warning if category wasn't found. Maybe teacher needs to add category first.
                    System.out.println("Course warning: added assignment '" + a.getName() + "' but category '" + a.getCategoryName() + "' not found.");
                }
            }

            //notify listeners that assignment list changed. Event name "assignmentAdded".
            pcs.firePropertyChange("assignmentAdded", null, a);
        }
    }

    /**
     * removeAssignment removes an assignment definition from the course.
     * It removes it from the main course assignment list.
     * If using categories, it removes it from the category's list too.
     * It loops through all enrolled students and tells each student object
     * to remove any grade they had stored for this specific assignment using student.removeGradeForAssignment.
     * It also tells the Assignment object itself to clear its internal map of grades using a.clearAllGrades().
     * Fires observer event if assignment was removed.
     * @param a The Assignment object to remove.
     */
    public void removeAssignment(Assignment a) {
        //check if assignment is valid and actually exists in the course list.
        if (a != null && assignments.contains(a)) {
            //remove from the main list first. remove returns true if successful.
            boolean removed = assignments.remove(a);

            // Only do cleanup if it was successfully removed from main list.
            if (removed) {
                //if using categories, find the category...
                if (useCategories && categories.containsKey(a.getCategoryName())) {
                    GradingCategory category = categories.get(a.getCategoryName());
                    //...and if category exists, tell it to remove the assignment too.
                    if (category != null) {
                        category.removeAssignment(a);
                    }
                }

                // Clear grades stored inside the Assignment object itself.
                // Assumes Assignment has this method.
                a.clearAllGrades(); // TODO Verify Assignment.clearAllGrades exists

                // Also remove grades stored inside each Student object for this assignment.
                // loop through all student objects currently enrolled.
                for (Student student : enrolledStudents.values()) {
                    //tell the student object to remove any grade associated with this assignment object.
                    // Assumes Student has this method.
                    student.removeGradeForAssignment(a);
                }

                //notify observers that the assignment was removed.
                //the removed assignment is sent as the old value in the event name "assignmentRemoved".
                pcs.firePropertyChange("assignmentRemoved", a, null);
            }
        }
    }


    /**
     * addGradingCategory adds a category definition like homework or exams to the course.
     * only adds if a category with the same name isn't already there.
     * Fires event if category added.
     * @param category The GradingCategory object to add.
     */
    public void addGradingCategory(GradingCategory category) {
        //check input object exists, make sure category name isn't already used as a key in the map.
        if (category != null && !categories.containsKey(category.getName())) {
            //add to the map using category name as key, category object as value.
            categories.put(category.getName(), category);
            // Notify listeners category list changed
             pcs.firePropertyChange("categoryAdded", null, category);
        }
    }

    /**
     * clearGradingCategories removes all defined categories from the course.
     * useful if teacher wants to reset or change grading scheme.
     * Fires event after clearing.
     */
    public void clearGradingCategories() {
        // Check if there's anything to clear first
        if (!categories.isEmpty()) {
            //clear the map holding the category objects.
            categories.clear();
            // Notify listeners that categories were cleared
            pcs.firePropertyChange("categoriesCleared", null, null); // Send null for old/new maybe
        }
    }


    /**
     * returns a copy of the map of grading categories.
     * EXAMPLE OF ENCAPSULATION, prevents changing internal map by making a new one.
     * @return A new Map<String, GradingCategory> copy.
     */
    public Map<String, GradingCategory> getGradingCategories() {
        // Make new HashMap, pass old map to constructor to copy entries.
        return new HashMap<>(categories);
    }


    /**
     * returns a copy of the list of all assignments in the course.
     * ENCAPSULATION, returns a new list copy.
     * @return A new List<Assignment> copy.
     */
    public List<Assignment> getAllAssignments() {
        // Make new ArrayList, pass old list to constructor to copy entries.
        return new ArrayList<Assignment>(assignments);
    }

    /**
     * getGradesForStudent gets all the grades a specific student has received
     * for assignments that belong to this course.
     * It loops through only this course's assignments list
     * and asks the student object for their grade on each one.
     * @param student The Student object whose grades we want. Check if null.
     * @return A new Map where key is Assignment object, value is Grade object. Empty if student null.
     */
    public Map<Assignment, Grade> getGradesForStudent(Student student) {
        // Check student input first.
        if (student == null) {
            System.out.println("Course problem: getGradesForStudent got null student");
        	return new HashMap<>(); // Return empty map.
        }
        // Create a new map to store the results.
        Map<Assignment, Grade> result = new HashMap<>();

        // Loop through the assignments list that belongs to *this course*.
        // Using this.assignments is safe because we are inside the class.
        for (Assignment a : this.assignments) {
            // Ask the student object passed in for their grade on this assignment.
            // Assumes Student has getGradeForAssignment from Person B.
        	Grade g = a.getGrade(student.getUsername());

        	// If the student had a grade Grade object returned not null...
            if (g != null) {
                // ...add the Assignment and Grade pair to our result map.
                result.put(a, g);
            }
        }
        // Return the map containing just the grades for this student on this course's assignments.
        return result;
    }

    /**
     * Gets ungraded assignments for a specific student in this course.
     * Loops through this course's assignments, asks student for grade on each.
     * If student returns null grade, adds assignment to result list.
     * @param student The Student object. Check if null.
     * @return list of assignments the student has no grade for in this course. Empty if student null.
     */
    public List<Assignment> getUngradedAssignmentsForStudent(Student student) {
        // Check student input.
        if (student == null) {
            System.out.println("Course problem: getUngradedAssignmentsForStudent got null student");
        	return new ArrayList<>();
        }

        // list to store results.
        List<Assignment> result = new ArrayList<Assignment>();

        // loop through assignments in this course. Use internal list.
        for (Assignment a : this.assignments) {
            // ask the student if they have a grade for this assignment.
            Grade g = a.getGrade(student.getUsername()); // Assumes Student method exists

            // if the grade returned is null, it means it's ungraded for them.
            if (g == null) {
                result.add(a); // Add assignment to our results list.
            }
        }
        // return the list of assignments the student didnt have grades for.
        return result;
    }

    /**
     * Gets assignments that belong to a specific group name within this course.
     * Loops through course assignments, compares group name using equals exact match.
     * @param groupName The group name string to look for. Check if null.
     * @return list of assignments matching the group name. Empty if groupName null.
     */
    public List<Assignment> getGroupAssignments(String groupName) {
        // Check input.
        if (groupName == null) {
            System.out.println("Course problem: getGroupAssignments got null groupName");
        	return new ArrayList<>();
        }

        // list to store results.
        List<Assignment> groupAssignments = new ArrayList<Assignment>();

        // loop through assignments in this course. Use internal list.
        for (Assignment a : this.assignments) {
            // Check if assignment group name matches the one requested. Use equals for exact match.
            // Assumes Assignment.getGroupName exists.
            if (groupName.equals(a.getName())) {
                groupAssignments.add(a);
            }
        }
        // return found list.
        return groupAssignments;
    }

    /**
     * Gets assignments that belong to a specific grading category name like Homework.
     * Loops through course assignments, compares category name ignoring case.
     * @param categoryName The category name string to look for. Check if null.
     * @return list of assignments matching the category name. Empty if categoryName null.
     */
    public List<Assignment> getAssignmentsByCategory(String categoryName) {
        // Check input.
        if (categoryName == null) {
            System.out.println("Course problem: getAssignmentsByCategory got null categoryName");
        	return new ArrayList<>();
        }

        List<Assignment> categoryAssignments = new ArrayList<Assignment>();

        // loop through assignments in this course. Use internal list.
        for (Assignment a : this.assignments) {
             // Compare assignment's category name with requested one, ignore case difference.
             // Assumes Assignment.getCategoryName exists.
            if (categoryName.equalsIgnoreCase(a.getCategoryName())) {
                categoryAssignments.add(a);
            }
        }
        // Return the list found.
        return categoryAssignments;
    }


    /////////////////
    /////////////////
    //Sorting methods previously in controller, moved here

    /**
     * makes a new list of students sorted by their names.
     * It gets the current student list, makes a copy,
     * decides whether to sort by first or last name based on the boolean flag,
     * decides whether to sort A-Z or Z-A based on the other boolean flag,
     * then uses Java's Collections.sort with a Comparator to do the actual sorting.
     * Returns the new sorted list, doesn't change the original internal map order.
     *
     * @param sortByLastName If true, sorts by last name. If false, sorts by first name.
     * @param ascending If true, sorts from A to Z. If false, sorts from Z to A.
     * @return A new list of students sorted by the chosen name and order.
     */
    public List<Student> getEnrolledStudentsSortedByName(boolean sortByLastName, boolean ascending) {
        System.out.println("Course sorting students by name");

        // get a new list copy of students to sort using the getter we already have.
        List<Student> studentsToSort = this.getEnrolledStudents();

        // make a Comparator variable to hold the sorting rule.
        Comparator<Student> nameComparator;
        // Check the flag to decide which name field to use.
        if (sortByLastName) {
            // Use a method reference Student::getLastName with Comparator.comparing.
            // String.CASE_INSENSITIVE_ORDER makes sorting ignore if names are upper or lower case.
            nameComparator = Comparator.comparing(Student::getLastName, String.CASE_INSENSITIVE_ORDER);
        } else {
            // Use Student::getFirstName instead if flag is false.
            nameComparator = Comparator.comparing(Student::getFirstName, String.CASE_INSENSITIVE_ORDER);
        }

        // Check the ascending flag.
        if (!ascending) {
            // If we want Z to A, reverse the comparator's natural order.
            nameComparator = nameComparator.reversed();
        }

        // Now use the standard Collections.sort method to sort our list copy
        // using the name comparison rule we set up.
        Collections.sort(studentsToSort, nameComparator);

        // Return the sorted list copy.
        return studentsToSort;
    }


    /**
     * getEnrolledStudentsSortedByAssignmentGrade returns a new sorted list of students
     * based on their score on one specific assignment.
     * It gets the current student list copy, then makes a Comparator that knows
     * how to compare two students by looking up their grade for the specific assignment.
     * Handles students who don't have a grade yet by sorting them lowest highest.
     * @param assignment The Assignment whose grades to use for sorting. Check if null.
     * @param ascending true puts lowest score first, false puts highest score first.
     * @return New sorted List<Student>. Empty list if assignment null.
     */
    public List<Student> getEnrolledStudentsSortedByAssignmentGrade(Assignment assignment, boolean ascending) {
        System.out.println("Course sorting students by grade for assignment: " + (assignment != null ? assignment.getName() : "null"));
        // Check the assignment input first.
        boolean assignmentExists = (assignment != null);
        if (!assignmentExists) {
            System.out.println("Course problem: sortStudentsByGrade got null assignment");
            return new ArrayList<>(); // Return empty list if assignment missing.
        }

        // Get the current list of students using the getter returns a copy.
        List<Student> studentsToSort = this.getEnrolledStudents();

        // Create the Comparator rule for comparing two students student1, student2 based on grade.
        // Comparator.comparingDouble takes a function that extracts the double score to compare.
        Comparator<Student> gradeComparator = Comparator.comparingDouble(student -> {
            // This lambda function runs for each student being compared.
            // Get this student's grade object for the specific assignment we're sorting by.
            Grade grade = assignment.getGrade(student.getUsername()); // Assumes Assignment.getGrade exists
            // Check if the student actually had a grade recorded.
            if (grade != null) {
                // If yes, return the points they earned as the value to sort by.
                return grade.getPointsEarned();
            } else {
                // If no grade found, return a very small number negative infinity.
                // This makes students without grades sort to the beginning when ascending A-Z low to high,
                // and to the end when descending Z-A high to low.
                return Double.NEGATIVE_INFINITY;
            }
        });

        // Check if we need descending order highest score first.
        if (!ascending) {
            // If yes, reverse the comparator.
            gradeComparator = gradeComparator.reversed();
        }

        // Use Collections.sort to sort the student list copy using our grade rule.
        Collections.sort(studentsToSort, gradeComparator);

        // Return the newly sorted list.
        return studentsToSort;
    }

    /////////////////////
    // observer pattern stuff

    /**
     * lets outside things like views listen for changes in this course.
     * uses the built in PropertyChangeSupport helper object from Java beans package.
     * The view calls this to register itself.
     * @param listener whatever wants to be notified when something changes maybe a View.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // Check listener isn't null before adding.
        if (listener != null) {
             pcs.addPropertyChangeListener(listener);
        }
    }

    /**
     * stops a listener from getting updates from this course.
     * Used when view is closed maybe.
     * @param listener the listener object to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // Tell the helper object pcs to remove the listener.
        if (listener != null) {
            pcs.removePropertyChangeListener(listener);
        }
    }
    
  //group stuff
    /**
     * Creates a new student group in this course
     * @param groupName Unique name for the group
     * @throws IllegalArgumentException if name exists or is invalid
     */
    public void createGroup(String groupName) {
        if (groups.stream().anyMatch(g -> g.getGroupName().equalsIgnoreCase(groupName))) {
            throw new IllegalArgumentException("Group name already exists");
        }
        groups.add(new Group(groupName));
    }

    /**
     * Adds a student to a course group
     * @param groupName Target group name
     * @param student Student to add
     * @return true if added successfully
     */
    public boolean addStudentToGroup(String groupName, Student student) {
        Group group = groups.stream()
            .filter(g -> g.getGroupName().equals(groupName))
            .findFirst()
            .orElse(null);
        return group != null && group.addMember(student);
    }

    /**
     * Gets all groups in this course
     * @return Unmodifiable list of groups
     */
    public List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    /**
     * Finds group by name
     */
    public Group findGroupByName(String groupName) {
        if (groupName == null) return null;
        return groups.stream()
            .filter(g -> g.getGroupName().equals(groupName))
            .findFirst()
            .orElse(null);
    }

    //overrides

    /**
     * toString returns just the course name.
     * This is useful because JComboBox shows this string in the dropdown automatically.
     */
    @Override
    public String toString() {
        // Return the name field directly.
        return name;
    }

    /**
     * equals method checks if two Course objects are the same.
     * Based ONLY on the courseId for now, assumes course IDs are unique system wide.
     */
     @Override
     public boolean equals(Object o) {
         // Standard checks: same object, null, different class.
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         // Cast to Course.
         Course course = (Course) o;
         // Compare using the courseId field. Use Objects.equals for null safety just in case.
         return Objects.equals(courseId, course.courseId);
     }

     /**
      * hashCode method goes with equals. Based on same field courseId.
      */
      @Override
      public int hashCode() {
          // Use Objects.hash helper to generate hash code from courseId.
          return Objects.hash(courseId);
      }


}