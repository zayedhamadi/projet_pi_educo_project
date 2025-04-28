package pi_project.louay.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class eventlisteController implements Initializable {

    @FXML
    private FlowPane eventContainer;
    @FXML
    private Button weekButton;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> typeCombo;

    private final evenementImp evenementService = new evenementImp();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation du ComboBox avec les types d'événements
        typeCombo.getItems().add("Tous types");
        for (EventType type : EventType.values()) {
            typeCombo.getItems().add(type.getLabel());
        }

        afficherEvenements(); // Affiche tous les événements au départ
        setupActions(); // Configuration des actions de filtrage
    }

    private void afficherEvenements() {
        List<evenement> allEvenements = evenementService.getAll();
        eventContainer.getChildren().clear();

        for (evenement evt : allEvenements) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/CardEvenement.fxml"));
                Parent card = loader.load();

                CardEvenementController controller = loader.getController();
                controller.setData(evt);

                eventContainer.getChildren().add(card);

            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la carte événement : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private void afficherEvenementsSemaine() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        List<evenement> eventsThisWeek = evenementService.getEvenementsCetteSemaine(startOfWeek, endOfWeek);

        eventContainer.getChildren().clear();

        for (evenement evt : eventsThisWeek) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/CardEvenement.fxml"));
                Parent card = loader.load();

                CardEvenementController controller = loader.getController();
                controller.setData(evt);

                eventContainer.getChildren().add(card);

            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la carte événement : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void setupActions() {
        // Ajout d'un écouteur pour le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterEvents());

        // Ajout d'un écouteur pour le ComboBox (type d'événement)
        typeCombo.valueProperty().addListener((observable, oldValue, newValue) -> filterEvents());
        weekButton.setOnAction(event -> afficherEvenementsSemaine());

    }

    // Fonction de filtrage
    private void filterEvents() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedType = typeCombo.getValue();

        // Filtrage des événements selon le texte de recherche et le type sélectionné
        List<evenement> filteredEvents = evenementService.getAll().stream()
                .filter(evt -> (searchText.isEmpty() || evt.getTitre().toLowerCase().contains(searchText)))
                .filter(evt -> (selectedType == null || selectedType.equals("Tous types") || evt.getType().getLabel().equalsIgnoreCase(selectedType)))
                .toList();

        eventContainer.getChildren().clear();

        for (evenement evt : filteredEvents) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/CardEvenement.fxml"));
                Parent card = loader.load();

                CardEvenementController controller = loader.getController();
                controller.setData(evt);

                eventContainer.getChildren().add(card);

            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la carte filtrée : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
