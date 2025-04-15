package pi_project.Saif.Controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Service.CommandeService;

import java.io.IOException;

public class PanierController {

    @FXML private TableView<Produit> cartTable;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, Double> colPrix;
    @FXML private TableColumn<Produit, Integer> colQuantite;
    @FXML private TableColumn<Produit, Double> colTotal;
    @FXML private TableColumn<Produit, Void> colAction;
    @FXML private Label totalLabel;

    private ObservableList<Produit> panier;

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        colPrix.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrix()).asObject());
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(1).asObject());
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrix()).asObject());

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");

            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
                deleteBtn.setOnAction(event -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(produit);
                    updateTotal();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });
    }

    public void setCartData(ObservableList<Produit> panier, double total) {
        this.panier = panier;
        cartTable.setItems(panier);
        updateTotal();
    }

    private void updateTotal() {
        double total = cartTable.getItems().stream()
                .mapToDouble(Produit::getPrix)
                .sum();
        totalLabel.setText(String.format("%.2f DT", total));
    }

    @FXML
    public void retourBoutique() {
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }


//    @FXML
//    public void passerCommande() {
//        if (panier == null || panier.isEmpty()) {
//            Alert alert = new Alert(Alert.AlertType.WARNING, "Votre panier est vide.");
//            alert.showAndWait();
//            return;
//        }
//
//        // ID temporaire du parent - à adapter selon ton système d'authentification
//        int parentId = 1;
//
//        // Appel au service pour passer la commande
//        CommandeService commandeService = new CommandeService();
//        commandeService.passerCommande(parentId, panier, "Carte bancaire");
//
//        // Confirmation
//        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Commande passée avec succès !");
//        alert.showAndWait();
//
//        // Vider le panier
//        panier.clear();
//        cartTable.getItems().clear();
//        updateTotal();
//    }
@FXML
public void passerCommande() {
    double total = cartTable.getItems().stream()
            .mapToDouble(Produit::getPrix)
            .sum();

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/PaiementStripe.fxml"));
        Parent root = loader.load();

        PaiementStripeController controller = loader.getController();
        controller.setData(panier, total); // panier + total envoyés

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Paiement");
        stage.show();

        ((Stage) cartTable.getScene().getWindow()).close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}


}
