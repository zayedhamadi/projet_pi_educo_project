package pi_project.Saif.Service;

import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Interface.Service;
import pi_project.db.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements Service<Produit> {

    private final Connection connection;

    public ProduitService() {
        this.connection = DataSource.getInstance().getConn();
    }

    @Override
    public void ajouter(Produit produit) {
        String sql = "INSERT INTO produit (nom, description, prix, stock, image, categorie_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getDescription());
            ps.setDouble(3, produit.getPrix());
            ps.setInt(4, produit.getStock());
            ps.setString(5, produit.getImage());
            ps.setInt(6, produit.getCategorieId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Produit produit) {
        String sql = "UPDATE produit SET nom = ?, description = ?, prix = ?, stock = ?, image = ?, categorie_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getDescription());
            ps.setDouble(3, produit.getPrix());
            ps.setInt(4, produit.getStock());
            ps.setString(5, produit.getImage());
            ps.setInt(6, produit.getCategorieId());
            ps.setInt(7, produit.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Produit getById(int id) {
        String sql = "SELECT * FROM produit WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getString("image"),
                        rs.getInt("categorie_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Produit> getAll() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit";
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getString("image"),
                        rs.getInt("categorie_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    public List<Produit> rechercherParMotCle(String motCle) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE nom LIKE ? OR description LIKE ? OR prix LIKE ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String recherche = "%" + motCle + "%";
            ps.setString(1, recherche);
            ps.setString(2, recherche);
            ps.setString(3, recherche);  // Recherche également dans le prix en tant que texte

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getString("image"),
                        rs.getInt("categorie_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
    // Dans ProduitService.java
    public List<Produit> rechercherProduits(String recherche, Integer categorieId) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE (nom LIKE ? OR description LIKE ? OR prix LIKE ?)";

        // Si une catégorie est spécifiée, ajouter la condition de filtrage
        if (categorieId != null) {
            sql += " AND categorie_id = ?";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + recherche + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);  // Recherche également dans le prix

            // Si une catégorie est spécifiée, ajoutez la condition pour l'id de la catégorie
            if (categorieId != null) {
                ps.setInt(4, categorieId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getString("image"),
                        rs.getInt("categorie_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

}
