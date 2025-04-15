package pi_project.Saif.Service;

import pi_project.Saif.Entity.CommandeProduit;
import pi_project.Saif.Interface.Service;
import pi_project.db.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeProduitService implements Service<CommandeProduit> {
    private final Connection connection;

    public CommandeProduitService() {
        this.connection = DataSource.getInstance().getConn();
    }

    @Override
    public void ajouter(CommandeProduit commandeProduit) {
        String sql = "INSERT INTO commande_produit (commande_id, produit_id, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, commandeProduit.getCommandeId());
            ps.setInt(2, commandeProduit.getProduitId());
            ps.setInt(3, commandeProduit.getQuantite());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(CommandeProduit commandeProduit) {
        String sql = "UPDATE commande_produit SET commande_id = ?, produit_id = ?, quantite = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, commandeProduit.getCommandeId());
            ps.setInt(2, commandeProduit.getProduitId());
            ps.setInt(3, commandeProduit.getQuantite());
            ps.setInt(4, commandeProduit.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM commande_produit WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommandeProduit getById(int id) {
        String sql = "SELECT * FROM commande_produit WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new CommandeProduit(
                        rs.getInt("id"),
                        rs.getInt("commande_id"),
                        rs.getInt("produit_id"),
                        rs.getInt("quantite")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CommandeProduit> getAll() {
        List<CommandeProduit> list = new ArrayList<>();
        String sql = "SELECT * FROM commande_produit";
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new CommandeProduit(
                        rs.getInt("id"),
                        rs.getInt("commande_id"),
                        rs.getInt("produit_id"),
                        rs.getInt("quantite")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
