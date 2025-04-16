package pi_project.louay.Service;

import pi_project.louay.Entity.evenement;
import pi_project.louay.Interface.Ievenementservice;
import pi_project.db.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class evenementImp implements Ievenementservice<evenement> {

    private final Connection cnx;

    public evenementImp() {
        cnx = DataSource.getInstance().getConn();
    }

    @Override
    public void ajouter(evenement e) {
        String sql = "INSERT INTO evenement (titre, description, date_debut, date_fin, lieu, inscription_requise, nombre_places, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, e.getTitre());
            pst.setString(2, e.getDescription());
            // In your ajouter and modifier methods
            pst.setTimestamp(3, Timestamp.valueOf(e.getDateDebut().atStartOfDay()));
            pst.setTimestamp(4, Timestamp.valueOf(e.getDateFin().atStartOfDay()));
            pst.setString(5, e.getLieu());
            pst.setBoolean(6, e.isInscriptionRequise());
            if (e.getNombrePlaces() != null) {
                pst.setInt(7, e.getNombrePlaces());
            } else {
                pst.setNull(7, Types.INTEGER);
            }

            pst.setString(8, e.getType().name());

            pst.executeUpdate();
            System.out.println("Événement ajouté !");
        } catch (SQLException ex) {
            System.out.println("Erreur ajout événement : " + ex.getMessage());
        }
    }

    @Override
    public void modifier(evenement e) {
        String sql = "UPDATE evenement SET titre=?, description=?, date_debut=?, date_fin=?, lieu=?, inscription_requise=?, nombre_places=?, type=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, e.getTitre());
            pst.setString(2, e.getDescription());
            // In your ajouter and modifier methods
            pst.setTimestamp(3, Timestamp.valueOf(e.getDateDebut().atStartOfDay()));
            pst.setTimestamp(4, Timestamp.valueOf(e.getDateFin().atStartOfDay()));
            pst.setString(5, e.getLieu());
            pst.setBoolean(6, e.isInscriptionRequise());
            if (e.getNombrePlaces() != null) {
                pst.setInt(7, e.getNombrePlaces());
            } else {
                pst.setNull(7, Types.INTEGER);
            }

            pst.setString(8, e.getType().name());
            pst.setInt(9, e.getId());

            pst.executeUpdate();
            System.out.println(" Événement modifié !");
        } catch (SQLException ex) {
            System.out.println("Erreur modification événement : " + ex.getMessage());
        }
    }

    @Override
    public void supprimer(evenement e) {
        try {
            // Supprimer les inscriptions liées à cet événement
            String deleteInscriptions = "DELETE FROM inscription_evenement WHERE evenement_id = ?";
            try (PreparedStatement pst = cnx.prepareStatement(deleteInscriptions)) {
                pst.setInt(1, e.getId());
                pst.executeUpdate();
            }

            // Puis supprimer l'événement
            String deleteEvent = "DELETE FROM evenement WHERE id = ?";
            try (PreparedStatement pst = cnx.prepareStatement(deleteEvent)) {
                pst.setInt(1, e.getId());
                pst.executeUpdate();
                System.out.println("Événement et inscriptions supprimés !");
            }

        } catch (SQLException ex) {
            System.out.println("Erreur suppression événement : " + ex.getMessage());
        }
    }


    @Override
    public List<evenement> getAll() {
        List<evenement> list = new ArrayList<>();
        String sql = "SELECT * FROM evenement";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                evenement e = new evenement();
                e.setId(rs.getInt("id"));
                e.setTitre(rs.getString("titre"));
                e.setDescription(rs.getString("description"));
                // In getById() method:
                e.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime().toLocalDate());
                e.setDateFin(rs.getTimestamp("date_fin").toLocalDateTime().toLocalDate());
                e.setLieu(rs.getString("lieu"));
                e.setInscriptionRequise(rs.getBoolean("inscription_requise"));
                e.setNombrePlaces(rs.getInt("nombre_places"));
                e.setType(Enum.valueOf(pi_project.louay.Enum.EventType.class, rs.getString("type")));
                list.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur récupération événements : " + ex.getMessage());
        }
        return list;
    }
    @Override
    public evenement getById(int id) {
        String sql = "SELECT * FROM evenement WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    evenement e = new evenement();
                    e.setId(rs.getInt("id"));
                    e.setTitre(rs.getString("titre"));
                    e.setDescription(rs.getString("description"));
                    // In getById() method:
                    e.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime().toLocalDate());
                    e.setDateFin(rs.getTimestamp("date_fin").toLocalDateTime().toLocalDate());
                    e.setLieu(rs.getString("lieu"));
                    e.setInscriptionRequise(rs.getBoolean("inscription_requise"));
                    e.setNombrePlaces(rs.getInt("nombre_places"));
                    e.setType(Enum.valueOf(pi_project.louay.Enum.EventType.class, rs.getString("type")));
                    return e;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Erreur getById événement : " + ex.getMessage());
        }
        return null; // Si aucun événement trouvé
    }

}
