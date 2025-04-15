package pi_project.Zayed.Utils;

public class session {
    private static Integer idSession = null;

    public static Integer getUserSession() {
        return idSession;
    }

    public static void setUserSession(int id) {
        idSession = id;
    }

    public static void clearSession() {
        idSession = null;
    }
}
