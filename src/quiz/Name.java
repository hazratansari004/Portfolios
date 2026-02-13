
package quiz; // Declares this class belongs to the 'quiz' package

/**
 * Represents a competitor's name with first, middle (optional), and last name.
 */
public class Name { // Defines the Name class to represent a competitor's name
    private String firstName; // Stores the first name of the competitor
    private String middleName; // Stores the middle name of the competitor (can be empty)
    private String lastName; // Stores the last name of the competitor

    public Name(String firstName, String lastName) { // Constructor that takes first and last name only
        this.firstName = firstName; // Sets the first name to the value passed in
        this.middleName = ""; // Sets middle name to empty string since none was provided
        this.lastName = lastName; // Sets the last name to the value passed in
    } // End of two-parameter constructor

    public Name(String firstName, String middleName, String lastName) { // Constructor that takes first, middle, and last name
        this.firstName = firstName; // Sets the first name to the value passed in
        this.middleName = middleName; // Sets the middle name to the value passed in
        this.lastName = lastName; // Sets the last name to the value passed in
    } // End of three-parameter constructor

    public String getFirstName() { // Getter method that returns the first name
        return firstName; // Returns the value of firstName
    } // End of getFirstName method

    public void setFirstName(String firstName) { // Setter method to update the first name
        this.firstName = firstName; // Updates firstName with the new value passed in
    } // End of setFirstName method

    public String getMiddleName() { // Getter method that returns the middle name
        return middleName; // Returns the value of middleName
    } // End of getMiddleName method

    public void setMiddleName(String middleName) { // Setter method to update the middle name
        this.middleName = middleName; // Updates middleName with the new value passed in
    } // End of setMiddleName method

    public String getLastName() { // Getter method that returns the last name
        return lastName; // Returns the value of lastName
    } // End of getLastName method

    public void setLastName(String lastName) { // Setter method to update the last name
        this.lastName = lastName; // Updates lastName with the new value passed in
    } // End of setLastName method

    /** Returns the full name including middle name if present. */
    public String getFullName() { // Method that builds and returns the full name as a single string
        if (middleName != null && !middleName.isEmpty()) { // Checks if a middle name exists and is not empty
            return firstName + " " + middleName + " " + lastName; // Returns full name with middle name included
        } // End of if block
        return firstName + " " + lastName; // Returns full name without middle name
    } // End of getFullName method

    /** Returns the initials, e.g. "AJG" for "Alice Jane Green". */
    public String getInitials() { // Method that extracts and returns the initials from the name
        StringBuilder sb = new StringBuilder(); // Creates a StringBuilder to efficiently build the initials string
        if (firstName != null && !firstName.isEmpty()) { // Checks if first name exists and is not empty
            sb.append(Character.toUpperCase(firstName.charAt(0))); // Appends the uppercase first letter of the first name
        } // End of first name check
        if (middleName != null && !middleName.isEmpty()) { // Checks if middle name exists and is not empty
            sb.append(Character.toUpperCase(middleName.charAt(0))); // Appends the uppercase first letter of the middle name
        } // End of middle name check
        if (lastName != null && !lastName.isEmpty()) { // Checks if last name exists and is not empty
            sb.append(Character.toUpperCase(lastName.charAt(0))); // Appends the uppercase first letter of the last name
        } // End of last name check
        return sb.toString(); // Converts the StringBuilder to a String and returns the initials
    } // End of getInitials method

    @Override // Annotation indicating this method overrides a method from the Object superclass
    public String toString() { // Converts the Name object to a String representation
        return getFullName(); // Returns the full name by calling the getFullName method
    } // End of toString method
} // End of Name class
