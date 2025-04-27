package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pi_project.Saif.Entity.Commande;
import pi_project.Saif.Service.CommandeService;

import java.io.IOException;
import java.util.List;

public class AdminCommandeController {

    @FXML private TableView<Commande> tableCommande;
//    @FXML private TableColumn<Commande, Integer> idColumn;
//    @FXML private TableColumn<Commande, Integer> parentIdColumn;
    @FXML private TableColumn<Commande, String> dateCommandeColumn;
    @FXML private TableColumn<Commande, Double> montantTotalColumn;
    @FXML private TableColumn<Commande, String> statutColumn;
    @FXML
    private TableColumn<Commande, Void> actionColumn;
    @FXML private Button marquerPretBtn;

    private CommandeService commandeService;

    @FXML private TextField searchField;  // Champ de recherche

    public AdminCommandeController() {
        this.commandeService = new CommandeService();
    }

    @FXML
    private void initialize() {
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        parentIdColumn.setCellValueFactory(new PropertyValueFactory<>("parentId"));
        dateCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        montantTotalColumn.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Setup actions for the "Consulter" button in the table
        actionColumn.setCellFactory(param -> new TableCell<Commande, Void>() {
            private final Button consulterButton = new Button("Consulter");

            {
                consulterButton.setOnAction(event -> {
                    Commande selectedCommande = getTableView().getItems().get(getIndex());
                    openCommandeDetailsWindow(selectedCommande);  // Open the new window to show details
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : consulterButton);
            }
        });

        refreshTable();
    }
    private void openCommandeDetailsWindow(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/CommandeDetails.fxml"));  // Ajuste bien le chemin
            AnchorPane detailsPane = loader.load();

            CommandeDetailsController detailsController = loader.getController();
            detailsController.setCommande(commande);

            Stage detailsStage = new Stage();
            detailsStage.setScene(new Scene(detailsPane));
            detailsStage.setTitle("Détails de la Commande");

            // ✅ Quand la fenêtre se ferme, rafraîchir le tableau principal
            detailsStage.setOnHiding(event -> refreshTable());

            detailsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();  // Si aucun mot-clé n'est fourni, recharger toutes les commandes
        } else {
            List<Commande> commandes = commandeService.searchCommandes(keyword);
            tableCommande.getItems().clear();
            tableCommande.getItems().addAll(commandes);
        }
    }
    private void refreshTable() {
        List<Commande> commandes = commandeService.getAll();
        tableCommande.getItems().clear();
        tableCommande.getItems().addAll(commandes);
    }
}
