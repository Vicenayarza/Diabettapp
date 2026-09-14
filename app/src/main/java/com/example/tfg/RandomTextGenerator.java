package com.example.tfg;
import java.util.Random;
public class RandomTextGenerator {
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";

    private static final String LETTERS_AND_DIGITS = UPPER_CASE + LOWER_CASE + DIGITS;

    private Random random = new Random();

    public String generateRandomText(int minLength) {
        if (minLength <= 8) {
            throw new IllegalArgumentException("Length must be greater than 8 characters");
        }

        StringBuilder result = new StringBuilder(minLength);

        // Ensure at least one letter and one digit are included
        result.append(UPPER_CASE.charAt(random.nextInt(UPPER_CASE.length())));
        result.append(LOWER_CASE.charAt(random.nextInt(LOWER_CASE.length())));
        result.append(DIGITS.charAt(random.nextInt(DIGITS.length())));

        for (int i = 3; i < minLength; i++) {
            int index = random.nextInt(LETTERS_AND_DIGITS.length());
            result.append(LETTERS_AND_DIGITS.charAt(index));
        }

        // Shuffle the characters to avoid predictable sequences
        char[] resultArray = result.toString().toCharArray();
        for (int i = 0; i < resultArray.length; i++) {
            int randomIndex = random.nextInt(resultArray.length);
            char temp = resultArray[i];
            resultArray[i] = resultArray[randomIndex];
            resultArray[randomIndex] = temp;
        }

        return new String(resultArray);
    }
}
