package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pi_project.Saif.Entity.Commande;
import pi_project.Saif.Service.CommandeService;

import java.util.List;

public class AdminCommandeController {

    @FXML private TableView<Commande> tableCommande;
    @FXML private TableColumn<Commande, Integer> idColumn;
    @FXML private TableColumn<Commande, Integer> parentIdColumn;
    @FXML private TableColumn<Commande, String> dateCommandeColumn;
    @FXML private TableColumn<Commande, Double> montantTotalColumn;
    @FXML private TableColumn<Commande, String> statutColumn;
    @FXML private TableColumn<Commande, Void> actionColumn;
    @FXML private Button marquerPretBtn;

    private CommandeService commandeService;

    public AdminCommandeController() {
        this.commandeService = new CommandeService();
    }

    @FXML
    private void initialize() {
        // Initialize the table columns with data from CommandeService
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        parentIdColumn.setCellValueFactory(new PropertyValueFactory<>("parentId"));
        dateCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        montantTotalColumn.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Setup actions for the "Actions" column (Mark as ready)
        actionColumn.setCellFactory(param -> new TableCell<Commande, Void>() {
            private final Button markReadyButton = new Button("Marquer comme prête");

            {
                markReadyButton.setOnAction(event -> {
                    Commande selectedCommande = getTableView().getItems().get(getIndex());
                    commandeService.updateOrderStatus(selectedCommande.getId(), "Prête");
                    refreshTable();
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : markReadyButton);
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        List<Commande> commandes = commandeService.getAll();
        tableCommande.getItems().clear();
        tableCommande.getItems().addAll(commandes);
    }
}
