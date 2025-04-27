package pi_project.louay.Service;

import pi_project.db.DataSource;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Entity.inscriptionevenement;
import pi_project.louay.Interface.Ieventservice;
import pi_project.Fedi.entites.eleve;
import pi_project.Zayed.Entity.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class inscevenementImp implements Ieventservice<inscriptionevenement> {

    private final Connection cnx;

    public inscevenementImp() {
        this.cnx = DataSource.getInstance().getConn();
    }

    @Override
    public void ajouter(inscriptionevenement insc) {
        String sql = "INSERT INTO inscription_evenement (enfant_id, evenement_id, date_inscription) VALUES (?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, insc.getEnfant_id().getId());
            pst.setInt(2, insc.getEvenement().getId());
            pst.setDate(3, java.sql.Date.valueOf(insc.getDateInscription()));
            pst.executeUpdate();
            System.out.println("Inscription réussie !");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'inscription : " + ex.getMessage());
        }
    }

    @Override
    public void modifier(inscriptionevenement inscription) {
        String sql = "UPDATE inscription_evenement SET enfant=?, evenement_id=?, date_inscription=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, inscription.getEnfant_id().getId());
            pst.setInt(2, inscription.getEvenement().getId());
            pst.setTimestamp(3, Timestamp.valueOf(inscription.getDateInscription().atStartOfDay()));
            pst.setInt(4, inscription.getId());

            pst.executeUpdate();
            System.out.println("Inscription modifiée !");
        } catch (SQLException e) {
            System.out.println("Erreur modification inscription : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(inscriptionevenement inscription) {
        String sql = "DELETE FROM inscription_evenement WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, inscription.getId());
            pst.executeUpdate();
            System.out.println("Inscription supprimée !");
        } catch (SQLException e) {
            System.out.println("Erreur suppression inscription : " + e.getMessage());
        }
    }

    @Override
    public List<inscriptionevenement> getAll() {
        List<inscriptionevenement> list = new ArrayList<>();

        String sql = "SELECT i.id, i.date_inscription, i.evenement_id, i.enfant_id, e.nom " +
                "FROM inscription_evenement i " +
                "JOIN eleve e ON i.enfant_id = e.id";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                inscriptionevenement insc = new inscriptionevenement();
                insc.setId(rs.getInt("id"));

                // Création de l'enfant
                eleve enfant = new eleve();
                enfant.setId(rs.getInt("enfant_id"));
                enfant.setNom(rs.getString("nom")); // correspond à e.nom
                insc.setEnfant_id(enfant);

                // Création de l'événement
                evenement ev = new evenement();
                ev.setId(rs.getInt("evenement_id"));
                insc.setEvenement(ev);

                // Date d'inscription
                insc.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime().toLocalDate());

                list.add(insc);
            }

        } catch (SQLException e) {
            System.out.println("Erreur récupération inscriptions : " + e.getMessage());
        }

        return list;
    }
    @Override
    public inscriptionevenement getById(int id) {
        String sql = "SELECT i.id, i.date_inscription, i.evenement_id, i.enfant_id, e.nom " +
                "FROM inscription_evenement i " +
                "JOIN eleve e ON i.enfant_id = e.id " +
                "WHERE i.id=?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                inscriptionevenement insc = new inscriptionevenement();
                insc.setId(rs.getInt("id"));

                eleve enfant = new eleve();
                enfant.setId(rs.getInt("enfant_id"));
                enfant.setNom(rs.getString("nom"));
                insc.setEnfant_id(enfant);

                evenement ev = new evenement();
                ev.setId(rs.getInt("evenement_id"));
                insc.setEvenement(ev);

                insc.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime().toLocalDate());

                return insc;
            }

        } catch (SQLException e) {
            System.out.println("Erreur récupération par ID : " + e.getMessage());
        }

        return null;
    }
    @Override
    public List<inscriptionevenement> getReservationsForParent(int parentId) {
        List<inscriptionevenement> list = new ArrayList<>();
        String sql = "SELECT i.id, i.date_inscription, i.evenement_id, i.enfant_id, e.nom, ev.titre " +
                "FROM inscription_evenement i " +
                "JOIN eleve e ON i.enfant_id = e.id " +
                "JOIN evenement ev ON i.evenement_id = ev.id " +
                "WHERE e.id_parent_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, parentId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                inscriptionevenement insc = new inscriptionevenement();
                insc.setId(rs.getInt("id"));


                eleve enfant = new eleve();
                enfant.setId(rs.getInt("enfant_id"));
                enfant.setNom(rs.getString("nom"));
                insc.setEnfant_id(enfant);


                evenement ev = new evenement();
                ev.setId(rs.getInt("evenement_id"));
                ev.setTitre(rs.getString("titre"));
                insc.setEvenement(ev);


                insc.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime().toLocalDate());


                list.add(insc);
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération des réservations du parent : " + e.getMessage());
        }

        return list;
    }
    public boolean estDejaInscrit(int enfantId, int evenementId) {
        String sql = "SELECT COUNT(*) FROM inscription_evenement WHERE enfant_id = ? AND evenement_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, enfantId);
            pst.setInt(2, evenementId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur vérification doublon : " + ex.getMessage());
        }
        return false;
    }
    public int getNombreInscriptions(int evenementId) {
        int count = 0;
        String req = "SELECT COUNT(*) FROM inscription_evenement WHERE evenement_id = ?";
        try (
             PreparedStatement pst = cnx.prepareStatement(req)) {

            pst.setInt(1, evenementId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du comptage des inscriptions : " + e.getMessage());
        }
        return count;
    }



}
