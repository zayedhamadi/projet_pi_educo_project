package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pi_project.Saif.Entity.Commande;
import pi_project.Saif.Service.CommandeService;
import pi_project.Zayed.Utils.session;

import java.util.List;

public class SuiviCommandeController {

    @FXML
    private TableView<Commande> table;
//    @FXML
//    private TableColumn<Commande, Integer> idCol;
    @FXML
    private TableColumn<Commande, String> dateCol;
    @FXML
    private TableColumn<Commande, Double> montantCol;
    @FXML
    private TableColumn<Commande, String> statutCol;
    @FXML
    private TableColumn<Commande, String> paiementCol;

    private CommandeService commandeService;

    public SuiviCommandeController() {
        commandeService = new CommandeService();
    }

    @FXML
    public void initialize() {
        // Initialiser les colonnes de la table
//        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        paiementCol.setCellValueFactory(new PropertyValueFactory<>("modePaiement"));
        Integer userId = session.getUserSession();
        // Charger les commandes pour l'utilisateur (parent_id)
        loadCommandes(userId);  // Exemple : 1 est l'id de l'utilisateur
    }

    // Charger les commandes de l'utilisateur (par exemple, parent_id = 1)
    private void loadCommandes(int parentId) {

        List<Commande> commandes = commandeService.getCommandesByParentId(parentId);
        table.getItems().clear();
        table.getItems().addAll(commandes);
    }
}
