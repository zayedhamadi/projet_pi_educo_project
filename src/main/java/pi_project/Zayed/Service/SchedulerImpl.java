package pi_project.Zayed.Service;

import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Interface.CesserService;
import pi_project.Zayed.Utils.Mail;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerImpl {
    //(5*24 * 60) + 90;
    private static final long MAX_INACTIVITY_MINUTES = (5*24 * 60) + 90; // 1 jour + 1h30 = 1530 min
    //(5*24) * 60;
    private static final long WARNING_INACTIVITY_MINUTES = (5*24) * 60;// 1 jour = 1440 min

    private final CesserService cesserService;
    private final Mail mailService;
    private final UserImpl userService;
    private final LoginHistoryImp loginHistoryService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SchedulerImpl() {
        this.mailService = new Mail();
        this.userService = new UserImpl();
        this.cesserService = new CesserImpl();
        this.loginHistoryService = new LoginHistoryImp();
    }

    public void startScheduler() {
        scheduler.scheduleAtFixedRate(this::checkInactivityUsers, 0, 5, TimeUnit.HOURS);
    }

    public void checkInactivityUsers() {
        try {
            System.out.println("[Scheduler] Vérification de l'inactivité des comptes...");

            for (User user : userService.getActifUser()) {
                Optional<LocalDateTime> lastLoginOpt = loginHistoryService.getLastLoginTimeByUserId(user.getId());

                if (lastLoginOpt.isPresent()) {
                    LocalDateTime lastLogin = lastLoginOpt.get();
                    LocalDateTime now = LocalDateTime.now();
                    long minutesSinceLastLogin = java.time.Duration.between(lastLogin, now).toMinutes();

                    if (minutesSinceLastLogin >= MAX_INACTIVITY_MINUTES) {
                        cesserService.cesserUser(user.getId(), "Inactivité prolongée de plus de 1 jour et 1h30.");
                        System.out.println("[Scheduler] Compte désactivé pour l'utilisateur : " + user.getEmail());
                    } else if (minutesSinceLastLogin == WARNING_INACTIVITY_MINUTES) {
                        mailService.sendInactivityWarningMail(user.getEmail());
                        System.out.println("[Scheduler] Avertissement envoyé à l'utilisateur : " + user.getEmail());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Scheduler] Erreur lors de la vérification d'inactivité : " + e.getMessage());
        }
    }
}
