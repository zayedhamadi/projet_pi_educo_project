package pi_project.Saif.Service;

import pi_project.Saif.Entity.Categorie;
import pi_project.Saif.Interface.Service;
import pi_project.db.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements Service<Categorie> {
    private final Connection connection;

    public CategorieService() {
        this.connection = DataSource.getInstance().getConn();
    }

    @Override
    public void ajouter(Categorie categorie) {
        String sql = "INSERT INTO categorie (nom, description) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categorie.getNom());
            ps.setString(2, categorie.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Categorie categorie) {
        String sql = "UPDATE categorie SET nom = ?, description = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categorie.getNom());
            ps.setString(2, categorie.getDescription());
            ps.setInt(3, categorie.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM categorie WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Categorie getById(int id) {
        String sql = "SELECT * FROM categorie WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Categorie(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Categorie> getAll() {
        List<Categorie> list = new ArrayList<>();
        String sql = "SELECT * FROM categorie";
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Categorie(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Ajoute cette méthode pour récupérer une catégorie par ID
    public String getNomCategorieParId(int id) {
        // Recherche de la catégorie par son ID
        Categorie categorie = getCategorieById(id); // Appelle la méthode pour récupérer la catégorie par ID

        if (categorie != null) {
            return categorie.getNom(); // Retourne le nom de la catégorie
        }

        return null; // Retourne null si la catégorie n'est pas trouvée
    }

    // Implémentation de la méthode pour obtenir une catégorie par ID
    public Categorie getCategorieById(int id) {
        String sql = "SELECT * FROM categorie WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Categorie(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retourne null si la catégorie n'est pas trouvée
    }

}
