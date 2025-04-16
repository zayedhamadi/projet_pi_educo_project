package pi_project.Zayed.Utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
    private static final BCryptPasswordEncoder encode = new BCryptPasswordEncoder();

    public static String cryptPw(String pw) {
        return encode.encode(pw);
    }

    public static boolean checkPw(String pw, String hashedpw) {
        return encode.matches(pw, hashedpw);
    }
}
