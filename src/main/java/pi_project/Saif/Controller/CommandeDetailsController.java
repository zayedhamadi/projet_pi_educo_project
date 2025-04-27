package pi_project.Saif.Controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pi_project.Saif.Entity.Commande;
import pi_project.Saif.Entity.CommandeProduit;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Service.CommandeService;
import pi_project.Saif.Service.CommandeProduitService;
import pi_project.Saif.Service.ProduitService;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Service.UserImpl;

import java.util.List;

public class CommandeDetailsController {

    @FXML private Label commandeIdLabel;
    @FXML private TableView<CommandeProduit> produitsTable;
    @FXML private TableColumn<CommandeProduit, String> nomProduitColumn;
    @FXML private TableColumn<CommandeProduit, Integer> quantiteColumn;
    @FXML private TableColumn<CommandeProduit, Double> prixColumn;
    @FXML private TableColumn<CommandeProduit, Double> totalColumn;
    @FXML private Button marquerPretButton;
    @FXML private Label statutLabel;
    @FXML private Label parentNameLabel;

    private CommandeService commandeService;
    private CommandeProduitService commandeProduitService;

    private Commande commande;

    public CommandeDetailsController() {
        this.commandeService = new CommandeService();
        this.commandeProduitService = new CommandeProduitService();
    }

    @FXML
    public void initialize() {
        // Set up the table to display the product details
        ProduitService produitService = new ProduitService();

        nomProduitColumn.setCellValueFactory(cellData -> {
            Produit produit = produitService.getById(cellData.getValue().getProduitId());
            return new SimpleStringProperty(produit != null ? produit.getNom() : "");
        });

        quantiteColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());

        prixColumn.setCellValueFactory(cellData -> {
            Produit produit = produitService.getById(cellData.getValue().getProduitId());
            return new SimpleDoubleProperty(produit != null ? produit.getPrix() : 0.0).asObject();
        });

        totalColumn.setCellValueFactory(cellData -> {
            Produit produit = produitService.getById(cellData.getValue().getProduitId());
            double prix = (produit != null ? produit.getPrix() : 0.0);
            return new SimpleDoubleProperty(prix * cellData.getValue().getQuantite()).asObject();
        });

        // Set the "Mark as Ready" button action
        marquerPretButton.setOnAction(event -> {
            // Update the order status to "Prête"
            commandeService.updateOrderStatus(commande.getId(), "Prête");

            // Update the label to show the current status
            statutLabel.setText("Statut: Prête");
            statutLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

            // Reload the table data (update the product list)
            reloadProduitsTable();
            marquerPretButton.getScene().getWindow().hide();

            // Optionally, you can disable the button once it's clicked to prevent re-clicking
            marquerPretButton.setDisable(true);
        });
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
        // Display the order details in the UI
        commandeIdLabel.setText("Commande ID: " + commande.getId());
        UserImpl userService = new UserImpl();
        User parent = userService.getSpeceficUser(commande.getParentId());

        if (parent != null) {
            parentNameLabel.setText("Parent : " + parent.getNom() + " " + parent.getPrenom());
        } else {
            parentNameLabel.setText("Parent : Inconnu");
        }

        // Get the products for the given order and display them in the table
        reloadProduitsTable();

        // Display current status (if available)
        statutLabel.setText("Statut: " + commande.getStatut());
        if ("Prête".equalsIgnoreCase(commande.getStatut())) {
            marquerPretButton.setDisable(true);
        }
    }

    // Method to reload the products in the table
    private void reloadProduitsTable() {
        List<CommandeProduit> produits = commandeProduitService.getProduitsByCommandeId(commande.getId());
        produitsTable.getItems().clear();
        produitsTable.getItems().addAll(produits);
    }
}
