package pi_project.Zayed.Service;

import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Interface.CesserService;
import pi_project.Zayed.Utils.Mail;
import pi_project.db.DataSource;

import javax.mail.MessagingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CesserImpl implements CesserService {
    private final Connection cnx;
    private final String CesserUser = "INSERT INTO cessation (id_user_id, motif) VALUES (?, ?)";
    private final String InactiverUser = "UPDATE user SET etat_compte = ? WHERE id = ?";
    private final String ActiverUser = "UPDATE user SET etat_compte = ? WHERE id = ?";
    private final String GetCesserInfo = "SELECT * FROM cessation WHERE id_user_id = ?";

    private final Mail mail = new Mail();

    public CesserImpl() {
        cnx = DataSource.getInstance().getConn();
    }

    @Override
    public void cesserUser(int id, String motif) {
        try {
            UserImpl userService = new UserImpl();
            User user = userService.getSpeceficUser(id);

            if (user == null) {
                throw new SQLException("Utilisateur non trouvé");
            }

            if (user.getEtat_compte() == EtatCompte.inactive) {
                throw new SQLException("L'utilisateur est déjà inactif");
            }

            // Insertion dans la table cessation
            try (PreparedStatement pst = cnx.prepareStatement(this.CesserUser)) {
                pst.setInt(1, id);
                pst.setString(2, motif);
                int done = pst.executeUpdate();

                if (done > 0) {
                    try (PreparedStatement updateStmt = cnx.prepareStatement(this.InactiverUser)) {
                        updateStmt.setString(1, EtatCompte.inactive.toString());
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                    }

                    mail.sendMailDeCessation(user.getEmail(), motif);
                    System.out.println("Utilisateur est cesse!");
                }
            }

        } catch (SQLException | MessagingException e) {
            System.err.println("Erreur lors de la cessation de l'utilisateur: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public User ActiverUserCesser(int id) {
        try {
            UserImpl userService = new UserImpl();
            User user = userService.getSpeceficUser(id);

            if (user == null) {
                throw new SQLException("Utilisateur non trouvé");
            }

            if (user.getEtat_compte() == EtatCompte.active) {
                throw new SQLException("L'utilisateur est déjà actif");
            }

            try (PreparedStatement pst = cnx.prepareStatement(this.ActiverUser)) {
                pst.setString(1, EtatCompte.active.toString());
                pst.setInt(2, id);
                int done = pst.executeUpdate();

                if (done > 0) {
                    // Get cessation info for logging/notification
                    String motif = "";
                    try (PreparedStatement getCesserStmt = cnx.prepareStatement(this.GetCesserInfo)) {
                        getCesserStmt.setInt(1, id);
                        ResultSet rs = getCesserStmt.executeQuery();
                        if (rs.next()) {
                            motif = rs.getString("motif");
                        }
                    }

                    mail.sendMailReactivation(user.getEmail(), motif);
                    System.out.println("Utilisateur réactivé avec succès!");
                    return userService.getSpeceficUser(id);
                }
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la réactivation de l'utilisateur: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }
}

