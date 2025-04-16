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
}
