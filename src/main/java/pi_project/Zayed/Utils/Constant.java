package pi_project.Zayed.Utils;


import com.google.gson.Gson;

import java.util.Random;

public class Constant {
    private static final Gson gson = new Gson();
    private static final String passwordChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*";
    private static final int passwordLength = 10;


    public static String generateRandomPassword() {
        Random rand = new Random();
        StringBuilder password = new StringBuilder(passwordLength);

        for (int i = 0; i < passwordLength; i++) {
            password.append(passwordChars.charAt(rand.nextInt(passwordChars.length())));
        }
        return password.toString();
    }
}