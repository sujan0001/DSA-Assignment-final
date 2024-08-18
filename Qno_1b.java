//Question 1 b solutions

// package org.example;

import java.util.Scanner;

public class Qno_1b {

    // Method to rotate a character in the alphabet
    private static char rotateChar(char c, int direction) {
        if (Character.isLowerCase(c)) {
            if (direction == 1) { // Clockwise
                return c == 'z' ? 'a' : (char)(c + 1);
            } else { // Counter-clockwise
                return c == 'a' ? 'z' : (char)(c - 1);
            }
        } else if (Character.isUpperCase(c)) {
            if (direction == 1) { // Clockwise
                return c == 'Z' ? 'A' : (char)(c + 1);
            } else { // Counter-clockwise
                return c == 'A' ? 'Z' : (char)(c - 1);
            }
        } else {
            return c; // Non-alphabetic characters are not changed
        }
    }

    // Method to apply the shifts to the message
    private static String applyShifts(String s, int[][] shifts) {
        char[] message = s.toCharArray();

        for (int[] shift : shifts) {
            int start = shift[0];
            int end = shift[1];
            int direction = shift[2];

            for (int i = start; i <= end; i++) {
                message[i] = rotateChar(message[i], direction);
            }
        }

        return new String(message);
    }

    // Main method to read input and apply shifts
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read input message
        System.out.print("Enter the message: ");
        String s = scanner.nextLine();

        // Read shifts
        System.out.print("Enter the shifts in the format [[start, end, direction], ...] ie-[[0, 1, 1], [2, 3, 0], [0, 2, 1]] form: ");
        String shiftsInput = scanner.nextLine();

        // Parse the shifts input
        int[][] shifts = parseShifts(shiftsInput);

        // If parsing failed, shifts will be null
        if (shifts == null) {
            System.out.println("Error parsing shifts input. Please enter shifts in the correct format.");
        } else {
            // Apply shifts and print the result
            String result = applyShifts(s, shifts);
            System.out.println("the final deciphered message is: " + result);
        }

        scanner.close();
    }

    // Method to parse shifts input
    private static int[][] parseShifts(String shiftsInput) {
        try {
            // Remove spaces and leading/trailing brackets if present
            shiftsInput = shiftsInput.trim().replaceAll("\\s+", "");
            if (shiftsInput.startsWith("[[") && shiftsInput.endsWith("]]")) {
                shiftsInput = shiftsInput.substring(2, shiftsInput.length() - 2);
            }

            // Split by "],["
            String[] shiftStrings = shiftsInput.split("\\],\\[");

            // Initialize shifts array
            int[][] shifts = new int[shiftStrings.length][3];

            // Parse each shift
            for (int i = 0; i < shiftStrings.length; i++) {
                String[] parts = shiftStrings[i].split(",");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid shift format.");
                }
                shifts[i][0] = Integer.parseInt(parts[0]); // start
                shifts[i][1] = Integer.parseInt(parts[1]); // end
                shifts[i][2] = Integer.parseInt(parts[2]); // direction
            }

            return shifts;
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            // Print the exception for debugging purpose
            e.printStackTrace();
            return null;
        }
    }
}


