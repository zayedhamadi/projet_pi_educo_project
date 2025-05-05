package pi_project.Zayed.Service;

import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Utils.Constant;
import pi_project.Zayed.Utils.Mail;
import pi_project.db.DataSource;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class securiteLogin {
    private static final Map<String, CodeVerification> verificationCodes = new HashMap<>();
    private static final Map<String, Integer> failedAttempts = new HashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int CODE_EXPIRATION_MINUTES = 5;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static {
        scheduler.scheduleAtFixedRate(securiteLogin::cleanExpiredCodes, 5, 5, TimeUnit.MINUTES);
    }

    private final User user;
    private final Connection connection;
    private final Mail mail;
    private final Constant constant;
    private final AuthenticationImpl authService;
    private final CesserImpl cesserUser;

    public securiteLogin() {
        cesserUser = new CesserImpl();
        user = new User();
        constant = new Constant();
        mail = new Mail();
        connection = DataSource.getInstance().getConn();
        authService = new AuthenticationImpl();
    }

    public static void cleanExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        verificationCodes.entrySet().removeIf(entry ->
                entry.getValue() == null || now.isAfter(entry.getValue().expirationTime)
        );
    }

    public String generateAndSendVerificationCode(String email) throws Exception {
        String code = Constant.generateRandomPassword();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
        verificationCodes.put(email, new CodeVerification(code, expirationTime));
        mail.sendCodeVerificationMail(email, code, CODE_EXPIRATION_MINUTES);
        return code;
    }

    public boolean verifyCode(String email, String inputCode) {
        cleanExpiredCodes();

        CodeVerification codeVerification = verificationCodes.get(email);

        if (codeVerification == null) {
            handleFailedAttempt(email);
            return false;
        }

        if (LocalDateTime.now().isAfter(codeVerification.expirationTime)) {
            verificationCodes.remove(email);
            handleFailedAttempt(email);
            return false;
        }

        boolean isValid = codeVerification.code.equals(inputCode);
        if (isValid) {
            verificationCodes.remove(email);
            failedAttempts.remove(email);
        } else {
            handleFailedAttempt(email);
        }
        return isValid;
    }

    public int getFailedAttemptsCount(String email) {
        return failedAttempts.getOrDefault(email, 0);
    }

    private void handleFailedAttempt(String email) {
        int attempts = failedAttempts.getOrDefault(email, 0) + 1;
        failedAttempts.put(email, attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            try {
                User user = authService.getUserByEmail(email);
                if (user != null) {
                    this.cesserUser.cesserUser(
                            user.getId(),
                            "Désactivation automatique: 3 tentatives de vérification de code échouées"
                    );
                    failedAttempts.remove(email);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la désactivation du compte: " + e.getMessage());
            }
        }
    }

    private static class CodeVerification {
        String code;
        LocalDateTime expirationTime;

        CodeVerification(String code, LocalDateTime expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
        }
    }
}