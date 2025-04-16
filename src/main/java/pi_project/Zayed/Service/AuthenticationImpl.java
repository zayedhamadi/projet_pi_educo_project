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
    private static final String forgetPwd = "UPDATE user SET password = ? WHERE email = ? ";
    private static final String loginUser = "SELECT * FROM user WHERE email = ? AND etat_compte = 'Active'";
    private final Connection cnx;
    Mail mail = new Mail();

    public AuthenticationImpl() {
        this.cnx = DataSource.getInstance().getConn();
    }


    public Role getUserRole(String email) {
        try (PreparedStatement pst = cnx.prepareStatement(loginUser)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Set<Role> roles = Constant.extractRolesFromJson(rs.getString("roles"));

                roles.retainAll(Set.of(Role.Admin, Role.Enseignant, Role.Parent));

                if (!roles.isEmpty()) {
                    return roles.iterator().next();
                }
            }
        } catch (SQLException e) {
            Constant.handleException(e, "Erreur lors de la récupération du rôle");
        }
        return null;
    }

    @Override
    public boolean login(User user) {
        if (user.getEtat_compte() == EtatCompte.inactive) {
            System.out.println("Ce compte est inactif et ne peut pas se connecter");
            return false;
        }

        try (PreparedStatement pst = cnx.prepareStatement(loginUser)) {
            pst.setString(1, user.getEmail());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (storedPassword == null || !PasswordUtils.checkPw(user.getPassword(), storedPassword)) {
                    System.out.println("Email ou mot de passe incorrect !");
                    return false;
                }

                Set<Role> roles = Constant.extractRolesFromJson(rs.getString("roles"));
                roles.retainAll(Set.of(Role.Admin, Role.Enseignant, Role.Parent));

                if (roles.isEmpty()) {
                    System.out.println("Aucun rôle valide trouvé pour cet utilisateur !");
                    return false;
                }

                Role userRole = roles.iterator().next();
                System.out.println("Role : " + userRole);

                int idSession = rs.getInt("id");
                session.setUserSession(idSession);
                System.out.println("idSession " + idSession);

                switch (userRole) {
                    case Admin -> System.out.println("Je suis Admin");
                    case Enseignant -> System.out.println("Je suis Enseignant");
                    case Parent -> System.out.println("Je suis Parent");
                }

                return true;
            } else {
                System.out.println("Utilisateur non trouvé !");
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
    public void forgetPassword(String email) {
        String newPassword = Constant.generateRandomPassword();
        String encryptedPassword = PasswordUtils.cryptPw(newPassword);

        try (PreparedStatement pst = cnx.prepareStatement(forgetPwd)) {
            pst.setString(1, encryptedPassword);
            pst.setString(2, email);

            if (pst.executeUpdate() > 0) {
                System.out.println("Mot de passe réinitialisé pour : " + email);
                mail.sendForgetPasswordMail(email, newPassword);
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet email : " + email);
            }
        } catch (SQLException | MessagingException e) {
            Constant.handleException(e, "Erreur lors de la réinitialisation du mot de passe");
        }
    }
}
