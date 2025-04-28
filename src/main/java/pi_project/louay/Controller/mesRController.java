package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pi_project.Zayed.Utils.session;
import pi_project.louay.Entity.reclamation;
import pi_project.louay.Enum.Statut;
import pi_project.louay.Service.reclamationImp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class mesRController implements Initializable {

    @FXML private TableView<reclamation> tableView;
    @FXML private TableColumn<reclamation, String> colTitre;
    @FXML private TableColumn<reclamation, String> colDescription;
    @FXML private TableColumn<reclamation, Statut> colStatut;
    @FXML private Button ajouterBtn;
    @FXML private Pagination pagination;
    @FXML private ComboBox<Statut> typeCombo; // Statut directement au lieu de String

    private final reclamationImp reclamationService = new reclamationImp();
    private ObservableList<reclamation> allReclamations = FXCollections.observableArrayList();
    private ObservableList<reclamation> filteredReclamations = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 10;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Remplir le ComboBox avec les statuts
        typeCombo.setItems(FXCollections.observableArrayList(Statut.values()));
        typeCombo.getItems().add(0, null); // Option "Tous statuts"
        typeCombo.setPromptText("Tous les statuts");

        // Chargement initial des réclamations
        loadReclamations();

        // Listener sur ComboBox pour appliquer le filtre
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));

        // Gestion pagination
        pagination.setPageFactory(this::createPage);

        // Gestion du bouton ajouter
        ajouterBtn.setOnAction(event -> handleAjouterReclamation());
    }

    private void loadReclamations() {
        Integer userId = session.getUserSession();
        if (userId != null) {
            allReclamations.setAll(reclamationService.getByUserId(userId));
            filteredReclamations.setAll(allReclamations);
            refreshPagination();
        } else {
            System.out.println("Aucun utilisateur connecté");
            allReclamations.clear();
            filteredReclamations.clear();
            refreshPagination();
        }
    }

    private void applyFilter(Statut selectedStatut) {
        if (selectedStatut == null) {
            filteredReclamations.setAll(allReclamations);
        } else {
            filteredReclamations.setAll();
            for (reclamation r : allReclamations) {
                if (r.getStatut() == selectedStatut) {
                    filteredReclamations.add(r);
                }
            }
        }
        refreshPagination();
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredReclamations.size());
        ObservableList<reclamation> pageData = FXCollections.observableArrayList(
                filteredReclamations.subList(fromIndex, toIndex)
        );
        tableView.setItems(pageData);
        return new VBox(tableView);
    }

    private void refreshPagination() {
        int pageCount = (int) Math.ceil((double) filteredReclamations.size() / ROWS_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private void handleAjouterReclamation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/ajouterreclamation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Réclamation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recharger les données après ajout
            loadReclamations();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
