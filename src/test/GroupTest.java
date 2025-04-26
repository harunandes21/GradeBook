package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import model.*;

import java.util.Collections;
import java.util.List;

/**
 * Comprehensive test suite for the Group class.
 * Tests all functionality including member management,
 * group properties, and edge cases.
 */
public class GroupTest {
	private Group group;
    private Student student1;
    private Student student2;

    @Before
    public void setUp() {
        group = new Group("Group A");
        student1 = new Student("John", "Doe", "john@uni.edu", "pass123", "johndoe", "S001");
        student2 = new Student("Jane", "Smith", "jane@uni.edu", "pass456", "janesmith", "S002");
    }
    
    // ==================== CONSTRUCTOR TESTS ====================

    /**
     * Test 1: Normal case - Constructor with valid parameters
     * Verifies all properties are correctly initialized.
     */
    @Test
    public void testConstructorWithValidParameters() {
        assertEquals("Name should match", "Group A", group.getGroupName());
        assertTrue("Members list should be empty initially", group.getMembers().isEmpty());
    }

    /**
     * Test 2: Edge case - Constructor with empty name
     * Verifies IllegalArgumentException is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() {
        new Group("");
    }

    // ==================== MEMBER MANAGEMENT TESTS ====================

    /**
     * Test 4: Normal case - Add member successfully
     * Verifies member is added and can be retrieved.
     */
    @Test
    public void testAddMemberSuccess() {
        assertTrue("Should add student successfully", group.addMember(student1));
        assertEquals("Should have 1 member", 1, group.getMembers().size());
        assertTrue("Should contain added student", group.getMembers().contains(student1));
    }

    /**
     * Test 5: Edge case - Add null student
     * Verifies method returns false.
     */
    @Test
    public void testAddNullMember() {
        assertFalse("Should not add null student", group.addMember(null));
    }

    /**
     * Test 6: Edge case - Add duplicate member
     * Verifies method returns false for duplicate adds.
     */
    @Test
    public void testAddDuplicateMember() {
        group.addMember(student1);
        assertFalse("Should not add duplicate student", group.addMember(student1));
    }

    /**
     * Test 7: Normal case - Remove member successfully
     * Verifies member is removed and no longer in group.
     */
    @Test
    public void testRemoveMemberSuccess() {
        group.addMember(student1);
        assertTrue("Should remove successfully", group.removeMember(student1));
        assertFalse("Should no longer contain student", group.getMembers().contains(student1));
    }

    /**
     * Test 8: Edge case - Remove non-existent member
     * Verifies method returns false.
     */
    @Test
    public void testRemoveNonExistentMember() {
        assertFalse("Should return false for non-member", group.removeMember(student1));
    }

    /**
     * Test 9: Edge case - Remove null member
     * Verifies method returns false.
     */
    @Test
    public void testRemoveNullMember() {
        assertFalse("Should return false for null", group.removeMember(null));
    }

    // ==================== UTILITY METHOD TESTS ====================

    /**
     * Test 10: Normal case - Contains member check
     * Verifies contains() works correctly.
     */
    @Test
    public void testContainsMember() {
        group.addMember(student1);
        assertTrue("Should contain added member", 
            group.contains(student1));
        assertFalse("Should not contain non-member", 
            group.contains(student2));
    }

    /**
     * Test 11: Normal case - Get members returns copy
     * Verifies encapsulation by checking modifications to returned list don't affect group.
     */
    @Test
    public void testGetMembersReturnsCopy() {
        group.addMember(student1);
        List<Student> members = group.getMembers();
        members.clear();
        assertFalse("Original members should remain", group.getMembers().isEmpty());
    }

    /**
     * Test 12: Normal case - toString format
     * Verifies toString() produces expected format.
     */
    @Test
    public void testToString() {
        group.addMember(student1);
        group.addMember(student2);
        String expected = "Group Group A [2 members]";
        assertEquals("toString should match expected format", expected, group.toString());
    }
}