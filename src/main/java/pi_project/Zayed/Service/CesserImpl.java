package pi_project.Zayed.Service;

import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Interface.CesserService;
import pi_project.Zayed.Utils.Constant;
import pi_project.Zayed.Utils.Mail;
import pi_project.db.DataSource;

import javax.mail.MessagingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CesserImpl implements CesserService {
    private final Connection cnx;
    private final Mail mail = new Mail();

    public CesserImpl() {
        cnx = DataSource.getInstance().getConn();
    }

    @Override
    public void cesserUser(int id, String motif) {
        String insertCesser = "INSERT INTO cesser (idUserId, motif, dateMotif) VALUES (?, ?, ?)";
        String updateEtatUser = "UPDATE user SET etat_compte = ? WHERE id = ?";

        try (PreparedStatement insertStmt = cnx.prepareStatement(insertCesser);
             PreparedStatement updateStmt = cnx.prepareStatement(updateEtatUser)) {

            insertStmt.setInt(1, id);
            insertStmt.setString(2, motif);
            insertStmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            insertStmt.executeUpdate();

            updateStmt.setString(1, EtatCompte.inactive.name());
            updateStmt.setInt(2, id);
            updateStmt.executeUpdate();


            UserImpl user1 = new UserImpl();
            User user = user1.getSpeceficUser(id);
            if (user != null) {
                mail.sendMailDeCessation(user.getEmail(), motif);
            }

            System.out.println("L'utilisateur avec ID " + id + " a été cessé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la cessation de l'utilisateur.");
        }
    }


    @Override
    public User ActiverUserCesser(int id) {
        String update = "UPDATE user SET etat_compte = ? WHERE id = ?";
        String select = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement updateStmt = cnx.prepareStatement(update);
             PreparedStatement selectStmt = cnx.prepareStatement(select)) {

            updateStmt.setString(1, EtatCompte.active.name());
            updateStmt.setInt(2, id);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                selectStmt.setInt(1, id);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    User user = new User();
                    mail.sendMailReactivation(user.getEmail() );

                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setEtat_compte(EtatCompte.active);
                    user.setRoles(Constant.extractRolesFromJson(rs.getString("roles")));
                    user.setAdresse(rs.getString("adresse"));
                    user.setDescription(rs.getString("description"));
                    user.setNum_tel(rs.getInt("num_tel"));
                    user.setDate_naissance(rs.getDate("date_naissance").toLocalDate());
                    user.setImage(rs.getString("image"));
                    user.setGenre(Genre.valueOf(rs.getString("genre")));

                    return user;
                }

            } else {
                System.out.println("Aucune ligne affectée, l'utilisateur pourrait déjà être actif.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'activation du compte.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


    @Override
    public void SupprimerCessation(int id) {
        String delete = "DELETE FROM cesser WHERE idUserId = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(delete)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cessation supprimée avec succès pour l'utilisateur ID " + id);
            } else {
                System.out.println("Aucune cessation trouvée pour cet utilisateur.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la suppression de la cessation.");
        }
    }

}

