package pi_project.louay.Service;

import pi_project.db.DataSource;
import pi_project.louay.Entity.reclamation;
import pi_project.louay.Enum.Statut;
import pi_project.louay.Interface.Iservice;
import pi_project.Zayed.Entity.User;
import pi_project.louay.Utils.mail;
import pi_project.Zayed.Service.UserImpl;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class reclamationImp implements Iservice<reclamation> {
    private final Connection cnx;
    private final UserImpl userService;

    public reclamationImp() {
        cnx = DataSource.getInstance().getConn();
        this.userService = new UserImpl();
    }
    public UserImpl getUserService() {
        return this.userService;
    }

    @Override
    public void ajouter(reclamation r) {
        String query = "INSERT INTO reclamation (titre, description, date_de_creation, statut, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, r.getTitre());
            pst.setString(2, r.getDescription());
            pst.setDate(3, Date.valueOf(r.getDateDeCreation()));
            pst.setString(4, r.getStatut().getLabel());
            pst.setInt(5, r.getUser().getId());
            pst.executeUpdate();
            System.out.println("Reclamation ajoutée !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void modifier(reclamation r) {
        String query = "UPDATE reclamation SET titre=?, description=?, date_de_creation=?, statut=?, user_id=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, r.getTitre());
            pst.setString(2, r.getDescription());
            pst.setDate(3, Date.valueOf(r.getDateDeCreation()));
            pst.setString(4, r.getStatut().getLabel());
            pst.setInt(5, r.getUser().getId());
            pst.setInt(6, r.getId());
            pst.executeUpdate();
            System.out.println("Reclamation modifiée !");

            // Recharger l'utilisateur COMPLET avant l'envoi d'email
            User userComplet = this.userService.getSpeceficUser(r.getUser().getId());
            if (userComplet == null) {
                System.out.println("Erreur : Utilisateur non trouvé en base");
                return;
            }

            if ("Traitée".equalsIgnoreCase(r.getStatut().getLabel())) {
                String emailUser = userComplet.getEmail();
                String titreReclamation = r.getTitre();

                if (emailUser != null && !emailUser.isEmpty()) {
                    try {
                        new mail().sendReclamationTraiteeMail(emailUser, titreReclamation);
                    } catch (Exception e) {
                        System.out.println("Erreur envoi email : " + e.getMessage());
                    }
                } else {
                    System.out.println("Email manquant pour l'utilisateur ID=" + userComplet.getId());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur modification réclamation : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(reclamation r) {
        String query = "DELETE FROM reclamation WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, r.getId());
            pst.executeUpdate();
            System.out.println("Reclamation supprimée !");
        } catch (SQLException e) {
            System.out.println("Erreur suppression : " + e.getMessage());
        }
    }

    @Override
    public List<reclamation> getAll() {
        List<reclamation> list = new ArrayList<>();
        String query = "SELECT * FROM reclamation";
        try (Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                reclamation r = new reclamation();
                r.setId(rs.getInt("id"));
                r.setTitre(rs.getString("titre"));
                r.setDescription(rs.getString("description"));
                r.setDateDeCreation(rs.getDate("date_de_creation").toLocalDate());
                r.setStatut(Statut.fromLabel(rs.getString("statut")));

                User user = new User();
                user.setId(rs.getInt("user_id"));
                r.setUser(user);

                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération : " + e.getMessage());
        }
        return list;
    }

    @Override
    public reclamation getById(int id) {
        String query = "SELECT * FROM reclamation WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                reclamation r = new reclamation();
                r.setId(rs.getInt("id"));
                r.setTitre(rs.getString("titre"));
                r.setDescription(rs.getString("description"));
                r.setDateDeCreation(rs.getDate("date_de_creation").toLocalDate());
                r.setStatut(Statut.fromLabel(rs.getString("statut")));

                User user = new User();
                user.setId(rs.getInt("user_id"));
                r.setUser(user);

                return r;
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération : " + e.getMessage());
        }
        return null;
    }

    public List<reclamation> getByUserId(int userId) {
        List<reclamation> list = new ArrayList<>();
        String query = "SELECT * FROM reclamation WHERE user_id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                reclamation r = new reclamation();
                r.setId(rs.getInt("id"));
                r.setTitre(rs.getString("titre"));
                r.setDescription(rs.getString("description"));
                r.setDateDeCreation(rs.getDate("date_de_creation").toLocalDate());
                r.setStatut(Statut.fromLabel(rs.getString("statut")));

                User user = new User();
                user.setId(rs.getInt("user_id"));
                r.setUser(user);

                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération par user : " + e.getMessage());
        }
        return list;
    }
}
