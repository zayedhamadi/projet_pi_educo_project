package pi_project.Fedi.services;

import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.interfaces.idsevice;
import pi_project.Zayed.Entity.User;
import pi_project.Fedi.entites.classe;
import pi_project.db.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class eleveservice implements idsevice<eleve> {
    private final Connection connection;

    public eleveservice() {
        this.connection = DataSource.getInstance().getConn();
    }

    @Override
    public void add(eleve e) {
        String query = "INSERT INTO `eleve`(`id_classe_id`, `id_parent_id`, `nom`, `prenom`, `date_de_naissance`, `moyenne`, `nbre_abscence`, `date_inscription`, `qr_code_data_uri`) VALUES (?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, e.getClasse().getId());
            statement.setInt(2, e.getParent().getId());
            statement.setString(3, e.getNom());
            statement.setString(4, e.getPrenom());
            statement.setDate(5, new java.sql.Date(e.getDateNaissance().getTime()));
            statement.setDouble(6, e.getMoyenne());
            statement.setInt(7, e.getNbreAbsence());
            statement.setDate(8, new java.sql.Date(e.getDateInscription().getTime()));
            statement.setString(9, e.getQrCode());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    e.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de l'ajout de l'élève", ex);
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM `eleve` WHERE `id`=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) {
                throw new RuntimeException("Aucun élève trouvé avec l'ID: " + id);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la suppression de l'élève", ex);
        }
    }

    @Override
    public void update(eleve e) {
        String query = "UPDATE `eleve` SET `id_classe_id`=?, `id_parent_id`=?, `nom`=?, `prenom`=?, `date_de_naissance`=?, `moyenne`=?, `nbre_abscence`=?, `date_inscription`=?, `qr_code_data_uri`=? WHERE `id`=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, e.getClasse().getId());
            statement.setInt(2, e.getParent().getId());
            statement.setString(3, e.getNom());
            statement.setString(4, e.getPrenom());
            statement.setDate(5, new java.sql.Date(e.getDateNaissance().getTime()));
            statement.setDouble(6, e.getMoyenne());
            statement.setInt(7, e.getNbreAbsence());
            statement.setDate(8, new java.sql.Date(e.getDateInscription().getTime()));
            statement.setString(9, e.getQrCode());
            statement.setInt(10, e.getId());

            if (statement.executeUpdate() == 0) {
                throw new RuntimeException("Aucun élève trouvé avec l'ID: " + e.getId());
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'élève", ex);
        }
    }

    @Override
    public List<eleve> getAll() {
        List<eleve> eleves = new ArrayList<>();
        String query = "SELECT * FROM `eleve`";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                eleves.add(mapToEleve(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la récupération des élèves", ex);
        }
        return eleves;
    }

    @Override
    public eleve getOne(int id) {
        String query = "SELECT * FROM `eleve` WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapToEleve(rs);
            } else {
                throw new RuntimeException("Aucun élève trouvé avec l'ID: " + id);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la récupération de l'élève", ex);
        }
    }

    private eleve mapToEleve(ResultSet rs) throws SQLException {
        eleve e = new eleve();
        e.setId(rs.getInt("id"));

        // Vous devrez implémenter classeService et parentService pour récupérer les objets complets
        classe cl = new classe();
        cl.setId(rs.getInt("id_classe_id"));
        e.setClasse(cl);

        User parent = new User();
        parent.setId(rs.getInt("id_parent_id"));
        e.setParent(parent);

        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setDateNaissance(rs.getDate("date_de_naissance"));
        e.setMoyenne(rs.getDouble("moyenne"));
        e.setNbreAbsence(rs.getInt("nbre_abscence"));
        e.setDateInscription(rs.getDate("date_inscription"));
        e.setQrCode(rs.getString("qr_code_data_uri"));

        return e;
    }

    // Méthodes supplémentaires
    public List<eleve> getByClasse(int classeId) {
        List<eleve> eleves = new ArrayList<>();
        String query = "SELECT * FROM `eleve` WHERE id_classe_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, classeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                eleves.add(mapToEleve(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la récupération des élèves par classe", ex);
        }
        return eleves;
    }

    public List<classe> getAllClasses() {
        List<classe> classes = new ArrayList<>();
        String query = "SELECT id, nom_classe FROM classe"; // Utilisez nom_classe ici

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                classe c = new classe();
                c.setId(rs.getInt("id"));
                c.setNomclasse(rs.getString("nom_classe")); // Et ici aussi
                classes.add(c);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la récupération des classes", ex);
        }
        return classes;
    }

    public List<User> getAllParents() {
        List<User> parents = new ArrayList<>();
        String query = "SELECT id, email FROM user WHERE roles LIKE '%Parent%'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                parents.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des parents: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des parents", e);
        }
        return parents;
    }
}