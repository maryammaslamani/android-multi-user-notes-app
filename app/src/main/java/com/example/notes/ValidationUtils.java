package com.example.notes;

import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        if (email == null)
            return false;
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isValidName(String name) {
        if (name == null)
            return false;
        int nameLength = name.trim().length();
        return nameLength >= 3 && nameLength <= 10;
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 12)
            return false;

        boolean hasNumber = false;
        boolean hasLowercase = false;
        boolean hasUppercase = false;

        for (int i = 0; i < password.length(); i++) {
            char character = password.charAt(i);
            if (Character.isDigit(character))
                hasNumber = true;
            if (Character.isLowerCase(character))
                hasLowercase = true;
            if (Character.isUpperCase(character))
                hasUppercase = true;
        }

        return hasNumber && hasLowercase && hasUppercase;
    }

    public static boolean passwordsMatch(String password, String confirmPassword) {
        if (password == null)
            return false;

        return password.equals(confirmPassword);
    }
}