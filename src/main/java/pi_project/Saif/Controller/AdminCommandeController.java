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
import java.util.ArrayList;
import java.util.List;

public class AdminCommandeController {
    @FXML private ComboBox<String> statusFilter; // Nouveau ComboBox

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
    private List<Commande> filteredCommandes = new ArrayList<>(); // Variable pour stocker les résultats filtrés

    public AdminCommandeController() {
        this.commandeService = new CommandeService();
    }

    @FXML
    private void initialize() {
        tableCommande.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dateCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        montantTotalColumn.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        dateCommandeColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        montantTotalColumn.setMaxWidth(1f * Integer.MAX_VALUE * 32);
        statutColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        actionColumn.setMaxWidth(1f * Integer.MAX_VALUE * 33);

        actionColumn.setCellFactory(param -> new TableCell<Commande, Void>() {
            private final Button consulterButton = new Button("Consulter");

            {
                consulterButton.setOnAction(event -> {
                    Commande selectedCommande = getTableView().getItems().get(getIndex());
                    openCommandeDetailsWindow(selectedCommande);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : consulterButton);
            }
        });

        // Remplir le ComboBox avec les statuts
        statusFilter.getItems().addAll("Tous", "Payée", "Prête");
        statusFilter.setValue("Tous");
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


//    @FXML
//    private void handleSearch() {
//        String keyword = searchField.getText().trim();
//        if (keyword.isEmpty()) {
//            refreshTable();  // Si aucun mot-clé n'est fourni, recharger toutes les commandes
//        } else {
//            List<Commande> commandes = commandeService.searchCommandes(keyword);
//            tableCommande.getItems().clear();
//            tableCommande.getItems().addAll(commandes);
//        }
//    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        String selectedStatus = statusFilter.getValue();

        List<Commande> commandes = commandeService.getAll(); // Charger toutes les commandes

        // Filtrage par mot-clé
        if (!keyword.isEmpty()) {
            commandes.removeIf(c ->
                    !(c.getDateCommande().toString().toLowerCase().contains(keyword)
                            || String.valueOf(c.getMontantTotal()).contains(keyword)
                            || c.getStatut().toLowerCase().contains(keyword))
            );
        }

        // Filtrage par statut
        if (selectedStatus != null && !selectedStatus.equals("Tous")) {
            commandes.removeIf(c -> !c.getStatut().equalsIgnoreCase(selectedStatus));
        }

        // Stocker les résultats filtrés ou toutes les commandes dans filteredCommandes
        filteredCommandes = new ArrayList<>(commandes);

        // Mettre à jour la table avec les résultats filtrés
        tableCommande.getItems().clear();
        tableCommande.getItems().addAll(filteredCommandes);
    }

    @FXML
    private void handleSort() {
        // Si la recherche a été effectuée, on trie les résultats filtrés
        if (filteredCommandes != null && !filteredCommandes.isEmpty()) {
            filteredCommandes.sort((c1, c2) -> c1.getDateCommande().compareTo(c2.getDateCommande()));
        } else {
            // Sinon, on trie toutes les commandes
            List<Commande> commandes = commandeService.getAll();
            commandes.sort((c1, c2) -> c1.getDateCommande().compareTo(c2.getDateCommande()));
            filteredCommandes = commandes;
        }

        // Mettre à jour la table avec les commandes triées
        tableCommande.getItems().clear();
        tableCommande.getItems().addAll(filteredCommandes);
    }

    private void refreshTable() {
        List<Commande> commandes = commandeService.getAll();
        tableCommande.getItems().clear();
        tableCommande.getItems().addAll(commandes);
    }
}
