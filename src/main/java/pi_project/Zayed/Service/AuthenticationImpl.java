package pi_project.Zayed.Service;


import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Interface.AuthenticationService;
import pi_project.Zayed.Utils.Constant;
import pi_project.Zayed.Utils.Mail;
import pi_project.Zayed.Utils.PasswordUtils;
import pi_project.Zayed.Utils.session;
import pi_project.db.DataSource;

import javax.mail.MessagingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class AuthenticationImpl implements AuthenticationService {
    private static final String FORGET_PWD_QUERY = "UPDATE user SET password = ? WHERE email = ?";
    private static final String LOGIN_QUERY = "SELECT * FROM user WHERE email = ? AND etat_compte = 'Active'";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM user WHERE email = ?";

    private final Connection connection;
    private final Mail mailService;

    public AuthenticationImpl() {
        this.connection = DataSource.getInstance().getConn();
        this.mailService = new Mail();
    }

    public Role getUserRole(String email) {
        try (PreparedStatement pst = connection.prepareStatement(LOGIN_QUERY)) {
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
        return user == null && user.getEtat_compte() == EtatCompte.inactive;
    }

    private boolean checkEmailValid(String email) {

        return email != null && !email.isEmpty();

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

        try (PreparedStatement pst = connection.prepareStatement(LOGIN_QUERY)) {
            pst.setString(1, user.getEmail());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (!isPasswordValid(user.getPassword(), storedPassword)) {
                    System.out.println("Email ou mot de passe incorrect !");
                    return false;
                }

                Role userRole = getUserRole(user.getEmail());
                if (userRole == null) {
                    System.out.println("Aucun rôle valide trouvé pour cet utilisateur !");
                    return false;
                }

                System.out.println("Role : " + userRole);
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
        if (!this.checkEmailValid(email)) {
            System.out.println("Email est invalide");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(FIND_BY_EMAIL_QUERY)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            Constant.handleException(e, "Erreur lors de la recherche par email");
            return false;
        }
    }

    @Override
    public void forgetPassword(String email) {
        if (!this.checkEmailValid(email)) {
            System.out.println("Email est invalide");
        }

        try {
            if (!findUserByEmail(email)) {
                System.out.println("Aucun utilisateur trouvé avec cet email : " + email);
            }

            String newPassword = Constant.generateRandomPassword();
            String encryptedPassword = PasswordUtils.cryptPw(newPassword);

            try (PreparedStatement pst = connection.prepareStatement(FORGET_PWD_QUERY)) {
                pst.setString(1, encryptedPassword);
                pst.setString(2, email);

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Mot de passe réinitialisé pour : " + email);
                    mailService.sendForgetPasswordMail(email, newPassword);
                }
                System.out.println("Échec de la mise à jour du mot de passe pour : " + email);
            }
        } catch (SQLException | MessagingException e) {
            Constant.handleException(e, "Erreur lors de la réinitialisation du mot de passe");
        }
    }
}