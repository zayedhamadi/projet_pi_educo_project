package pi_project.louay.Service;

import pi_project.db.DataSource;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Entity.inscriptionevenement;
import pi_project.louay.Interface.Ieventservice;

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
    public void ajouter(inscriptionevenement inscription) {
        String sql = "INSERT INTO inscription_evenement (enfant, evenement_id, date_inscription) VALUES (?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, inscription.getEnfant_id());
            pst.setInt(2, inscription.getEvenement().getId());
            pst.setTimestamp(3, Timestamp.valueOf(inscription.getDateInscription().atStartOfDay())); // conversion ici

            pst.executeUpdate();
            System.out.println("Inscription ajoutée !");
        } catch (SQLException e) {
            System.out.println("Erreur ajout inscription : " + e.getMessage());
        }
    }

    @Override
    public void modifier(inscriptionevenement inscription) {
        String sql = "UPDATE inscription_evenement SET enfant=?, evenement_id=?, date_inscription=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, inscription.getEnfant_id());
            pst.setInt(2, inscription.getEvenement().getId());
            pst.setTimestamp(3, Timestamp.valueOf(inscription.getDateInscription().atStartOfDay())); // conversion ici
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
        String sql = "SELECT * FROM inscription_evenement";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                inscriptionevenement insc = new inscriptionevenement();
                insc.setId(rs.getInt("id"));
                insc.setEnfant_id(rs.getInt("enfant"));

                evenement ev = new evenement();
                ev.setId(rs.getInt("evenement_id"));
                insc.setEvenement(ev);

                insc.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime().toLocalDate());

                list.add(insc);
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération inscriptions : " + e.getMessage());
        }

        return list;
    }

    public inscriptionevenement getById(int id) {
        String sql = "SELECT * FROM inscription_evenement WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                inscriptionevenement insc = new inscriptionevenement();
                insc.setId(rs.getInt("id"));
                insc.setEnfant_id(rs.getInt("enfant"));

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
}
