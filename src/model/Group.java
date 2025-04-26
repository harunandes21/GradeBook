package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a named group of students for organizing course work.
 * Provides basic functionality for managing group membership.
 */
public class Group {
    private final String name;
    private final List<Student> members;

    /**
     * Creates a new student group with the specified name.
     * @param name The name for the group (must not be null or empty)
     * @throws IllegalArgumentException if name is null or empty
     */
    public Group(String name) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty");
        }
        this.name = name;
        this.members = new ArrayList<>();
    }

    /**
     * Adds a student to this group if they aren't already a member.
     * @param student The student to add (ignored if null)
     * @return true if student was added, false if already present or null
     */
    public boolean addMember(Student student) {
        if (student != null && !members.contains(student)) {
            return members.add(student);
        }
        return false;
    }

    /**
     * Removes a student from this group.
     * @param student The student to remove
     * @return true if student was removed, false if not found or null
     */
    public boolean removeMember(Student student) {
        return members.remove(student);
    }
    
    /**
     * Checks if a student belongs to this group.
     * @param student The student to check
     * @return true if student is a member, false otherwise (including null case)
     */
    public boolean contains(Student student) {
        return members.contains(student);
    }

    // Basic getters
    public String getGroupName() { return name; }
    public List<Student> getMembers() { return new ArrayList<>(members); }

    @Override
    public String toString() {
        return "Group " + name + " [" + members.size() + " members]";
    }
}