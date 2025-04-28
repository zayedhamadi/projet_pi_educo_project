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
//    public List<CommandeProduit> getProduitsByCommandeId(int idCommande) {
//        List<CommandeProduit> produitsDetails = new ArrayList<>();
//
//        String sql = "SELECT p.nom, p.prix, cp.quantite, cp.commande_id, cp.produit_id " +
//                "FROM commande_produit cp " +
//                "JOIN produit p ON cp.produit_id = p.id " +
//                "WHERE cp.commande_id = ?";
//
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setInt(1, idCommande);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                // Extract product details from the query result
//                String nomProduit = rs.getString("nom");
//                double prixProduit = rs.getDouble("prix");
//                int quantite = rs.getInt("quantite");
//                int commandeId = rs.getInt("commande_id");
//                int produitId = rs.getInt("produit_id");
//
//                // You can modify the CommandeProduit object to hold some of these values
//                // You can store only the essential values into CommandeProduit
//                CommandeProduit cp = new CommandeProduit();
//                cp.setCommandeId(commandeId);
//                cp.setProduitId(produitId);
//                cp.setQuantite(quantite);
//
//                // You can add logic here to store `nomProduit`, `prixProduit`, and `total` somewhere
//                // Maybe print them or use them in the UI to display to the user
//                double total = prixProduit * quantite;
//
//                // For example, printing them out
//                System.out.println("Produit: " + nomProduit + ", Quantité: " + quantite + ", Prix: " + prixProduit + ", Total: " + total);
//
//                // You can choose how to utilize these additional details without modifying the `CommandeProduit` class
//                produitsDetails.add(cp);  // Add the object to your list
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return produitsDetails;
//    }
public List<CommandeProduit> getProduitsByCommandeId(int idCommande) {
    List<CommandeProduit> produitsDetails = new ArrayList<>();

    String sql = "SELECT p.nom, p.prix, cp.quantite, cp.commande_id, cp.produit_id " +
            "FROM commande_produit cp " +
            "JOIN produit p ON cp.produit_id = p.id " +
            "WHERE cp.commande_id = ?";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, idCommande);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            // Extract product details from the query result
            String nomProduit = rs.getString("nom");
            double prixProduit = rs.getDouble("prix");
            int quantite = rs.getInt("quantite");
            int commandeId = rs.getInt("commande_id");
            int produitId = rs.getInt("produit_id");

            // Create CommandeProduit object and set basic details
            CommandeProduit cp = new CommandeProduit();
            cp.setCommandeId(commandeId);
            cp.setProduitId(produitId);
            cp.setQuantite(quantite);

            // Print the product details
            double total = prixProduit * quantite;
            System.out.println("Produit: " + nomProduit + ", Quantité: " + quantite +
                    ", Prix: " + prixProduit + ", Total: " + total);

            // You can store these details in a Map, List, or any other structure if needed
            // For now, we are just adding the CommandeProduit to the list
            produitsDetails.add(cp);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return produitsDetails;
}

}
