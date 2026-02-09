package quiz;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 tests for the Name class.
 *
 * @author Sailesh Kumar Mandal
 */
class NameTest {

    @Test
    void testFullNameTwoParts() {
        Name n = new Name("Alice", "Green");
        assertEquals("Alice Green", n.getFullName(),
                "Full name with first and last should be 'Alice Green'");
    }

    @Test
    void testInitialsTwoParts() {
        Name n = new Name("Alice", "Green");
        assertEquals("AG", n.getInitials(),
                "Initials for 'Alice Green' should be 'AG'");
    }

    @Test
    void testFullNameThreeParts() {
        Name n = new Name("Keith", "John", "Talbot");
        assertEquals("Keith John Talbot", n.getFullName(),
                "Full name with middle name should include all three parts");
    }

    @Test
    void testInitialsThreeParts() {
        Name n = new Name("Keith", "John", "Talbot");
        assertEquals("KJT", n.getInitials(),
                "Initials for 'Keith John Talbot' should be 'KJT'");
    }

    @Test
    void testSetFirstName() {
        Name n = new Name("Alice", "Green");
        n.setFirstName("Bob");
        assertEquals("Bob", n.getFirstName(),
                "First name should update to 'Bob' after setFirstName");
    }

    @Test
    void testSetMiddleName() {
        Name n = new Name("Alice", "Green");
        n.setMiddleName("Jane");
        assertEquals("Jane", n.getMiddleName(),
                "Middle name should update to 'Jane' after setMiddleName");
        assertEquals("Alice Jane Green", n.getFullName(),
                "Full name should include the new middle name");
    }

    @Test
    void testSetLastName() {
        Name n = new Name("Alice", "Green");
        n.setLastName("Brown");
        assertEquals("Brown", n.getLastName(),
                "Last name should update to 'Brown' after setLastName");
    }

    @Test
    void testToString() {
        Name n = new Name("Alice", "Green");
        assertEquals("Alice Green", n.toString(),
                "toString should return the full name");
    }

    @Test
    void testEmptyMiddleName() {
        Name n = new Name("Alice", "Green");
        assertEquals("", n.getMiddleName(),
                "Middle name should be empty when constructed with two-part name");
    }
}
