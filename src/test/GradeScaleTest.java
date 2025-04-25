package test;

import model.GradeScale;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Comprehensive test suite for the GradeScale enum.
 * Tests GPA values, letter retrieval, and mapping from percentage and letter.
 */
public class GradeScaleTest {

    // ==================== GPA VALUE TESTS ====================

    /**
     * Test 1: Normal case - GPA values for each grade
     * Verifies correct GPA is returned for each scale.
     */
    @Test
    public void testGpaValues() {
        assertEquals("GPA for A should be 4.0", 4.0, GradeScale.A.getGpaValue(), 0.01);
        assertEquals("GPA for B should be 3.0", 3.0, GradeScale.B.getGpaValue(), 0.01);
        assertEquals("GPA for C should be 2.0", 2.0, GradeScale.C.getGpaValue(), 0.01);
        assertEquals("GPA for D should be 1.0", 1.0, GradeScale.D.getGpaValue(), 0.01);
        assertEquals("GPA for E should be 0.0", 0.0, GradeScale.E.getGpaValue(), 0.01);
    }

    // ==================== LETTER RETRIEVAL TESTS ====================

    /**
     * Test 2: Normal case - Letter retrieval
     * Verifies letter is returned correctly from enum.
     */
    @Test
    public void testGetLetter() {
        assertEquals("Letter for A", "A", GradeScale.A.getLetter());
        assertEquals("Letter for B", "B", GradeScale.B.getLetter());
        assertEquals("Letter for C", "C", GradeScale.C.getLetter());
        assertEquals("Letter for D", "D", GradeScale.D.getLetter());
        assertEquals("Letter for E", "E", GradeScale.E.getLetter());
    }

    // ==================== FROM PERCENTAGE TESTS ====================

    /**
     * Test 3: Normal case - Mapping from percentage
     * Verifies correct grade scale is returned for common percentages.
     */
    @Test
    public void testFromPercentageNormal() {
        assertEquals("95% should map to A", GradeScale.A, GradeScale.fromPercentage(95));
        assertEquals("85% should map to B", GradeScale.B, GradeScale.fromPercentage(85));
        assertEquals("75% should map to C", GradeScale.C, GradeScale.fromPercentage(75));
        assertEquals("65% should map to D", GradeScale.D, GradeScale.fromPercentage(65));
        assertEquals("50% should map to E", GradeScale.E, GradeScale.fromPercentage(50));
    }

    /**
     * Test 4: Edge case - Exact boundary percentages
     * Verifies correct mapping for values on grade boundaries.
     */
    @Test
    public void testFromPercentageBoundaries() {
        assertEquals("90% is boundary for A", GradeScale.A, GradeScale.fromPercentage(90));
        assertEquals("80% is boundary for B", GradeScale.B, GradeScale.fromPercentage(80));
        assertEquals("70% is boundary for C", GradeScale.C, GradeScale.fromPercentage(70));
        assertEquals("60% is boundary for D", GradeScale.D, GradeScale.fromPercentage(60));
        assertEquals("0% is boundary for E", GradeScale.E, GradeScale.fromPercentage(0));
    }

    /**
     * Test 5: Edge case - Percentages below E (e.g., negative)
     * Verifies that values below 0 default to E.
     */
    @Test
    public void testFromPercentageNegative() {
        assertEquals("Negative percentage should map to E", GradeScale.E, GradeScale.fromPercentage(-10));
    }

    // ==================== FROM LETTER TESTS ====================

    /**
     * Test 6: Normal case - Mapping from letter
     * Verifies correct grade scale is returned for valid letters.
     */
    @Test
    public void testFromLetter() {
        assertEquals("Letter A should map to A", GradeScale.A, GradeScale.fromLetter("A"));
        assertEquals("Letter B should map to B", GradeScale.B, GradeScale.fromLetter("B"));
        assertEquals("Letter C should map to C", GradeScale.C, GradeScale.fromLetter("C"));
        assertEquals("Letter D should map to D", GradeScale.D, GradeScale.fromLetter("D"));
        assertEquals("Letter E should map to E", GradeScale.E, GradeScale.fromLetter("E"));
    }

    /**
     * Test 7: Edge case - Lowercase letters
     * Verifies letter matching is case-insensitive.
     */
    @Test
    public void testFromLetterLowercase() {
        assertEquals("Lowercase a should map to A", GradeScale.A, GradeScale.fromLetter("a"));
        assertEquals("Lowercase e should map to E", GradeScale.E, GradeScale.fromLetter("e"));
    }

    /**
     * Test 8: Edge case - Invalid letter
     * Verifies that unrecognized letters default to E.
     */
    @Test
    public void testFromLetterInvalid() {
        assertEquals("Invalid letter X should map to E", GradeScale.E, GradeScale.fromLetter("X"));
        assertEquals("Empty string should map to E", GradeScale.E, GradeScale.fromLetter(""));
        assertEquals("Null should map to E", GradeScale.E, GradeScale.fromLetter(null));
    }
}
