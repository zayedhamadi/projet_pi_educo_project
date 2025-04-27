package pi_project.Zayed.Service;

import pi_project.Zayed.Entity.Cesser;
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
import java.util.ArrayList;
import java.util.List;

public class CesserImpl implements CesserService {
    private final Connection cnx;
    private final Mail mail = new Mail();

    public CesserImpl() {
        cnx = DataSource.getInstance().getConn();
    }


    public int getCesserCount() {
        String query = "SELECT COUNT(*) FROM cessation";
        try (PreparedStatement pst = cnx.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public void cesserUser(int id, String motif) {
        if (motif == null || motif.trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif de cessation ne peut pas être vide");
        }

        String insertCesser = "INSERT INTO cessation (id_user_id, motif, date_motif) VALUES (?, ?, ?)";
        String updateEtatUser = "UPDATE user SET etat_compte = ? WHERE id = ?";

        try {
            cnx.setAutoCommit(false);

            try (PreparedStatement insertStmt = cnx.prepareStatement(insertCesser);
                 PreparedStatement updateStmt = cnx.prepareStatement(updateEtatUser)) {

                insertStmt.setInt(1, id);
                insertStmt.setString(2, motif);
                insertStmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                insertStmt.executeUpdate();

                updateStmt.setString(1, EtatCompte.inactive.name());
                updateStmt.setInt(2, id);
                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated == 0) {
                    throw new SQLException("Échec de la mise à jour de l'utilisateur, ID non trouvé: " + id);
                }

                cnx.commit();

                UserImpl userService = new UserImpl();
                User user = userService.getSpeceficUser(id);
                if (user != null) {
                    mail.sendMailDeCessation(user.getEmail(), motif);
                }

                System.out.println("L'utilisateur avec ID " + id + " a été cessé avec succès. Motif: " + motif);
            } catch (SQLException | MessagingException e) {
                cnx.rollback();
                throw new RuntimeException("Erreur lors de la cessation de l'utilisateur", e);
            } finally {
                cnx.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de gestion de transaction", e);
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
                    mail.sendMailReactivation(user.getEmail());
                    return user;
                }
            } else {
                System.out.println("Aucune ligne affectée, l'utilisateur pourrait déjà être actif.");
            }
        } catch (SQLException | MessagingException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'activation du compte.");
        }
        return null;
    }


    public List<Cesser> getAllCesser() {
        List<Cesser> cesserList = new ArrayList<>();
        String query = "SELECT * FROM cessation ORDER BY date_motif DESC";

        try (PreparedStatement stmt = cnx.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cesser cesser = new Cesser();
                cesser.setId(rs.getInt("id"));
                cesser.setIdUserId(rs.getInt("id_user_id"));
                cesser.setMotif(rs.getString("motif"));
                cesser.setDateMotif(rs.getDate("date_motif").toLocalDate());

                cesserList.add(cesser);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des cessations", e);
        }

        return cesserList;
    }

    @Override
    public void SupprimerCessation(int id) {
        String delete = "DELETE FROM cessation WHERE id_user_id = ?";
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
