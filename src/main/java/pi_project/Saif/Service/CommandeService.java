package pi_project.Saif.Service;

import pi_project.Saif.Entity.Commande;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Interface.Service;
import pi_project.db.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeService implements Service<Commande> {
    private final Connection connection;

    public CommandeService() {
        this.connection = DataSource.getInstance().getConn();
    }

    @Override
    public void ajouter(Commande commande) {
        String sql = "INSERT INTO commande (parent_id, date_commande, montant_total, statut, mode_paiement) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, commande.getParentId());
            ps.setTimestamp(2, Timestamp.valueOf(commande.getDateCommande()));
            ps.setDouble(3, commande.getMontantTotal());
            ps.setString(4, commande.getStatut());
            ps.setString(5, commande.getModePaiement());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Commande commande) {
        String sql = "UPDATE commande SET parent_id = ?, date_commande = ?, montant_total = ?, statut = ?, mode_paiement = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, commande.getParentId());
            ps.setTimestamp(2, Timestamp.valueOf(commande.getDateCommande()));
            ps.setDouble(3, commande.getMontantTotal());
            ps.setString(4, commande.getStatut());
            ps.setString(5, commande.getModePaiement());
            ps.setInt(6, commande.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM commande WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Commande getById(int id) {
        String sql = "SELECT * FROM commande WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Commande(
                        rs.getInt("id"),
                        rs.getInt("parent_id"),
                        rs.getTimestamp("date_commande").toLocalDateTime(),
                        rs.getDouble("montant_total"),
                        rs.getString("statut"),
                        rs.getString("mode_paiement")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Commande> getAll() {
        List<Commande> list = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Commande(
                        rs.getInt("id"),
                        rs.getInt("parent_id"),
                        rs.getTimestamp("date_commande").toLocalDateTime(),
                        rs.getDouble("montant_total"),
                        rs.getString("statut"),
                        rs.getString("mode_paiement")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void passerCommande(int parentId, List<Produit> produits, String modePaiement) {
        String insertCommandeSQL = "INSERT INTO commande (parent_id, date_commande, montant_total, statut, mode_paiement) VALUES (?, ?, ?, ?, ?)";
        String insertCommandeProduitSQL = "INSERT INTO commande_produit (commande_id, produit_id, quantite) VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false); // ⚠️ Start transaction

            // 1. Insertion de la commande
            double total = produits.stream().mapToDouble(Produit::getPrix).sum();
            PreparedStatement commandeStmt = connection.prepareStatement(insertCommandeSQL, Statement.RETURN_GENERATED_KEYS);
            commandeStmt.setInt(1, parentId);
            commandeStmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            commandeStmt.setDouble(3, total);
            commandeStmt.setString(4, "en cours");
            commandeStmt.setString(5, modePaiement);
            commandeStmt.executeUpdate();

            // 2. Récupérer l'ID généré
            ResultSet rs = commandeStmt.getGeneratedKeys();
            int commandeId = -1;
            if (rs.next()) {
                commandeId = rs.getInt(1);
            }

            // 3. Insertion des produits liés
            PreparedStatement cpStmt = connection.prepareStatement(insertCommandeProduitSQL);
            for (Produit produit : produits) {
                cpStmt.setInt(1, commandeId);
                cpStmt.setInt(2, produit.getId());
                cpStmt.setInt(3, 1); // quantité fixe pour le moment
                cpStmt.addBatch();
            }
            cpStmt.executeBatch();

            connection.commit(); // ✅ Commit la transaction
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                connection.rollback(); // ❌ En cas d'erreur, rollback
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

}
