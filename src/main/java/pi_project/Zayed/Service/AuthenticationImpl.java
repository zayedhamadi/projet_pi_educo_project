package pi_project.Zayed.Service;

import pi_project.Zayed.Entity.LoginHistory;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Interface.AuthenticationService;
import pi_project.Zayed.Utils.*;
import pi_project.db.DataSource;

import javax.mail.MessagingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Set;

public class AuthenticationImpl implements AuthenticationService {

    private static final String updatePwdByNumTel = "UPDATE user SET password = ? WHERE num_tel = ?";
    private static final String updatePwdByEmail = "UPDATE user SET password = ? WHERE email = ?";
    private static final String loginAuthentification = "SELECT * FROM user WHERE email = ? AND etat_compte = 'Active'";
    private static final String findUserByEmail = "SELECT * FROM user WHERE email = ?";
    private static final String findUserByNumTel = "SELECT * FROM user WHERE num_tel = ?";
    private static final String query = "SELECT nom, prenom FROM user WHERE num_tel = ?";
    private final Connection connection;
    private final Mail mailService;
    private final SMS smsService;
    LoginHistoryImp loginHistoryService;

    public AuthenticationImpl() {
        this.connection = DataSource.getInstance().getConn();
        this.mailService = new Mail();
        this.smsService = new SMS();
        loginHistoryService = new LoginHistoryImp();
    }

    public Role getUserRole(String email) {
        try (PreparedStatement pst = connection.prepareStatement(loginAuthentification)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Set<Role> roles = Constant.extractRolesFromJson(rs.getString("roles"));
                return roles.stream()
                        .filter(this::isValidRole)
                        .findFirst()
                        .orElse(null);
            }
        } catch (SQLException e) {
            Constant.handleException(e, "Erreur lors de la récupération du rôle");
        }
        return null;
    }

    private boolean isAccountInactive(User user) {
        return user != null && user.getEtat_compte() == EtatCompte.inactive;
    }

    private boolean checkEmailValid(String email) {
        return email != null && !email.isEmpty();
    }

    private boolean checkNumTelValid(String numTel) {
        return numTel != null && numTel.length() == 8;
    }

    private boolean isPasswordValid(String inputPassword, String storedPassword) {
        return storedPassword != null && PasswordUtils.checkPw(inputPassword, storedPassword);
    }

    private boolean isValidRole(Role role) {
        return role == Role.Admin || role == Role.Enseignant || role == Role.Parent;
    }

    @Override
    public boolean login(User user) {
        if (isAccountInactive(user)) {
            System.out.println("Ce compte est inactif et ne peut pas se connecter");
            return false;
        }

        try (PreparedStatement pst = connection.prepareStatement(loginAuthentification)) {
            pst.setString(1, user.getEmail());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (!isPasswordValid(user.getPassword(), storedPassword)) {
                    System.out.println("Email ou mot de passe incorrect !");
                    return false;
                }
                LoginHistory loginHistory = new LoginHistory(
                        user.getEmail(),
                        LocalDateTime.now(),
                        rs.getInt("id")
                );
                this.loginHistoryService.addLoginHistory(loginHistory);
                Role userRole = getUserRole(user.getEmail());
                if (userRole == null) {
                    System.out.println("Aucun rôle valide trouvé pour cet utilisateur !");
                    return false;
                }

                System.out.println("Rôle : " + userRole);
                session.setUserSession(rs.getInt("id"));

                switch (userRole) {
                    case Admin -> System.out.println("Connexion en tant qu'administrateur");
                    case Enseignant -> System.out.println("Connexion en tant qu'enseignant");
                    case Parent -> System.out.println("Connexion en tant que parent");
                }

                return true;
            }

            System.out.println("Utilisateur non trouvé !");
            return false;

        } catch (SQLException e) {
            Constant.handleException(e, "Erreur lors de la connexion");
            return false;
        }
    }

    @Override
    public void logout() {
        session.clearSession();
        System.out.println("Utilisateur déconnecté.");
    }

    @Override
    public boolean findUserByEmail(String email) {
        if (!checkEmailValid(email)) {
            System.out.println("Email est invalide");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(findUserByEmail)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Constant.handleException(e, "Erreur lors de la recherche par email");
            return false;
        }
    }

    @Override
    public void forgetPassword(String email) {
        if (!checkEmailValid(email)) {
            System.out.println("Email est invalide");
            return;
        }

        try {
            if (!findUserByEmail(email)) {
                System.out.println("Aucun utilisateur trouvé avec cet email : " + email);
                return;
            }

            String newPassword = Constant.generateRandomPassword();
            String encryptedPassword = PasswordUtils.cryptPw(newPassword);

            try (PreparedStatement pst = connection.prepareStatement(updatePwdByEmail)) {
                pst.setString(1, encryptedPassword);
                pst.setString(2, email);

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Mot de passe réinitialisé pour : " + email);
                    mailService.sendForgetPasswordMail(email, newPassword);
                } else {
                    System.out.println("Échec de la mise à jour du mot de passe pour : " + email);
                }
            }
        } catch (SQLException | MessagingException e) {
            Constant.handleException(e, "Erreur lors de la réinitialisation du mot de passe");
        }
    }

    public boolean findUserByNumTel(String numTel) {
        if (!(this.checkNumTelValid(numTel))) {
            System.out.println("Numéro de téléphone invalide");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(findUserByNumTel)) {
            ps.setString(1, numTel);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Constant.handleException(e, "Erreur lors de la recherche par numéro de téléphone");
            return false;
        }
    }


    public void forgetPasswordByNumTel(String numTel) {
        if (!(this.checkNumTelValid(numTel))) {
            System.out.println("Numéro de téléphone invalide");
            return;
        }

        try {
            if (!findUserByNumTel(numTel)) {
                System.out.println("Aucun utilisateur trouvé avec ce numéro : " + numTel);
                return;
            }

            User user = getUserByNumTel(numTel);
            String newPwd = Constant.generateRandomPassword();
            String cryptPw = PasswordUtils.cryptPw(newPwd);

            try (PreparedStatement pst = connection.prepareStatement(updatePwdByNumTel)) {
                pst.setString(1, cryptPw);
                pst.setString(2, numTel);

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Mot de passe réinitialisé pour le numéro : " + numTel);

                    assert user != null;
                    String message = "Cher utilisateur " + user.getNom() + " " + user.getPrenom() + ",\n" +
                            "Suite à votre demande, voici votre nouveau mot de passe : " + newPwd + "\n" +
                            "Pour des raisons de sécurité, nous vous conseillons de :\n" +
                            "1. Vous connecter rapidement\n" +
                            "2. Modifier ce mot de passe\n" +
                            "3. Ne pas le partager\n\n" +
                            "Cordialement,\n" +
                            "L'équipe EducoPlateform";

                    smsService.envoyerSms(numTel, message);
                } else {
                    System.out.println("Échec de la mise à jour du mot de passe pour : " + numTel);
                }
            }
        } catch (SQLException e) {
            Constant.handleException(e, "Erreur lors de la réinitialisation du mot de passe par téléphone");
        }
    }

    private User getUserByNumTel(String numTel) throws SQLException {

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, numTel);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                return user;
            }
        }
        return null;
    }
}
