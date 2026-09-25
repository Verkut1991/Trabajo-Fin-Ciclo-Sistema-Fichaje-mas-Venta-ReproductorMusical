package com.example.fichajefx.utils;

public class NifValidator {
    public static boolean isValidSpanishNIF(String nif) {
        if (nif == null)
            return false;
        nif = nif.toUpperCase().trim();

        // Validación básica de formato (8 números + 1 letra)
        if (!nif.matches("^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$")) {
            return false;
        }

        String numbers = nif.substring(0, 8);
        char letter = nif.charAt(8);
        String chars = "TRWAGMYFPDXBNJZSQVHLCKE";

        int n = Integer.parseInt(numbers);
        return chars.charAt(n % 23) == letter;
    }
}
