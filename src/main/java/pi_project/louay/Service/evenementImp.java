package pi_project.louay.Service;

import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Interface.Ievenementservice;
import pi_project.db.DataSource;
import pi_project.louay.Utils.sms;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class evenementImp implements Ievenementservice<evenement> {

    private final Connection cnx;

    public evenementImp() {
        cnx = DataSource.getInstance().getConn();
    }

    @Override
    public int ajouter(evenement e) {
        String sql = "INSERT INTO evenement (titre, description, date_debut, date_fin, lieu, inscription_requise, nombre_places, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, e.getTitre());
            pst.setString(2, e.getDescription());
            pst.setTimestamp(3, Timestamp.valueOf(e.getDateDebut().atStartOfDay()));
            pst.setTimestamp(4, Timestamp.valueOf(e.getDateFin().atStartOfDay()));
            pst.setString(5, e.getLieu());
            pst.setBoolean(6, e.isInscriptionRequise());

            if (e.getNombrePlaces() != null) {
                pst.setInt(7, e.getNombrePlaces());
            } else {
                pst.setNull(7, Types.INTEGER);
            }

            pst.setString(8, e.getType().getLabel());

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    System.out.println("Événement ajouté avec ID : " + generatedId);
                    return generatedId;
                }
            }

        } catch (SQLException ex) {
            System.out.println("Erreur ajout événement : " + ex.getMessage());
        }
        return -1; // Retourne -1 en cas d’erreur
    }


    @Override
    public void modifier(evenement e) {
        String sql = "UPDATE evenement SET titre=?, description=?, date_debut=?, date_fin=?, lieu=?, inscription_requise=?, nombre_places=?, type=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, e.getTitre());
            pst.setString(2, e.getDescription());

            pst.setTimestamp(3, Timestamp.valueOf(e.getDateDebut().atStartOfDay()));
            pst.setTimestamp(4, Timestamp.valueOf(e.getDateFin().atStartOfDay()));
            pst.setString(5, e.getLieu());
            pst.setBoolean(6, e.isInscriptionRequise());
            if (e.getNombrePlaces() != null) {
                pst.setInt(7, e.getNombrePlaces());
            } else {
                pst.setNull(7, Types.INTEGER);
            }

            pst.setString(8, e.getType().getLabel());
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
            String selectParents = """
           SELECT u.num_tel, u.nom, u.prenom\s
           FROM inscription_evenement ie
           JOIN eleve el ON ie.enfant_id = el.id
           JOIN user u ON el.id_parent_id = u.id
           WHERE ie.evenement_id = ?
           
        """;

            Set<String> numerosEnvoyes = new HashSet<>();

            try (PreparedStatement pst = cnx.prepareStatement(selectParents)) {
                pst.setInt(1, e.getId());
                try (ResultSet rs = pst.executeQuery()) {
                    sms smsSender = new sms();

                    while (rs.next()) {
                        String numTel = String.valueOf(rs.getInt("num_tel"));
                        String nomParent = rs.getString("nom");
                        String prenomParent = rs.getString("prenom");


                        if (!numerosEnvoyes.contains(numTel)) {
                            String message = "Bonjour " + prenomParent + " " + nomParent + ", l'événement '" + e.getTitre() + "' prévu le " + e.getDateDebut() + " a été annulé.";
                            smsSender.envoyerSms(numTel, message);
                            numerosEnvoyes.add(numTel);
                            System.out.println("SMS envoyé à " + numTel);
                        }
                    }
                }}


                String deleteInscriptions = "DELETE FROM inscription_evenement WHERE evenement_id = ?";
                try (PreparedStatement pst = cnx.prepareStatement(deleteInscriptions)) {
                    pst.setInt(1, e.getId());
                    pst.executeUpdate();
                }


                String deleteEvent = "DELETE FROM evenement WHERE id = ?";
                try (PreparedStatement pst = cnx.prepareStatement(deleteEvent)) {
                    pst.setInt(1, e.getId());
                    pst.executeUpdate();
                    System.out.println("Événement supprimé avec notifications SMS !");
                }

            } catch (SQLException ex) {
                System.out.println("Erreur suppression événement ou envoi SMS : " + ex.getMessage());
            }
        }


        @Override
        public List<evenement> getAll () {
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
                    String typeLabel = rs.getString("type");
                    e.setType(EventType.fromLabel(typeLabel));
                    list.add(e);
                }
            } catch (SQLException ex) {
                System.out.println("Erreur récupération événements : " + ex.getMessage());
            }
            return list;
        }
        @Override
        public evenement getById ( int id){
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
                        String typeLabel = rs.getString("type");
                        e.setType(EventType.fromLabel(typeLabel));
                        return e;
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Erreur getById événement : " + ex.getMessage());
            }
            return null;
        }
        @Override
    public List<evenement> getEvenementsCetteSemaine(LocalDate startOfWeek, LocalDate endOfWeek) {
        List<evenement> list = new ArrayList<>();
        String sql = "SELECT * FROM evenement WHERE date_debut BETWEEN ? AND ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setDate(1, java.sql.Date.valueOf(startOfWeek));
            pst.setDate(2, java.sql.Date.valueOf(endOfWeek));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    evenement e = new evenement();
                    e.setId(rs.getInt("id"));
                    e.setTitre(rs.getString("titre"));
                    e.setDescription(rs.getString("description"));
                    e.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime().toLocalDate());
                    e.setDateFin(rs.getTimestamp("date_fin").toLocalDateTime().toLocalDate());
                    e.setLieu(rs.getString("lieu"));
                    String typeLabel = rs.getString("type");
                    e.setType(EventType.fromLabel(typeLabel));
                    e.setInscriptionRequise(rs.getBoolean("inscription_requise"));
                    e.setNombrePlaces(rs.getInt("nombre_places"));

                    list.add(e);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Erreur récupération événements de cette semaine : " + ex.getMessage());
        }

        return list;
    }


}


