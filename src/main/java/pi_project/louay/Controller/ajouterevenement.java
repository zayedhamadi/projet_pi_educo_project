package pi_project.louay.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import pi_project.louay.Utils.timer;

import java.io.IOException;
import java.time.LocalDate;

public class ajouterevenement {

    @FXML
    private TextField titreField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private TextField lieuField;
    @FXML
    private CheckBox inscriptionRequiseCheckBox;
    @FXML
    private TextField nombrePlacesField;
    @FXML
    private TextField timerField; // Champ pour la durée du timer
    @FXML
    private HBox timerHBox;

    @FXML
    private ComboBox<EventType> typeComboBox;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button retourButton;

    private final evenementImp evenementService = new evenementImp();
    private EvenementController evenementController;

    @FXML
    public void initialize() {
        // Remplir le ComboBox avec les types d'événements
        typeComboBox.getItems().setAll(EventType.values());

        // Paramétrer les cellules de type
        typeComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(EventType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.getLabel());
            }
        });

        // Paramétrer le bouton de type
        typeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(EventType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.getLabel());
            }
        });

        // Désactiver certains champs en fonction de l'état de l'inscription requise
        nombrePlacesField.setDisable(true);
        timerField.setDisable(true);

        inscriptionRequiseCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            nombrePlacesField.setDisable(!newVal);
            timerField.setDisable(!newVal);
            timerHBox.setVisible(newVal);
            if (!newVal) {
                nombrePlacesField.clear();
                timerField.clear();
            }
        });

        // Lier les actions des boutons
        ajouterButton.setOnAction(this::ajouterEvenement);
        retourButton.setOnAction(this::retour);
    }

    public void setEvenementController(EvenementController controller) {
        this.evenementController = controller;
    }

    private void ajouterEvenement(ActionEvent event) {
        try {
            // Récupérer les valeurs des champs
            String titre = titreField.getText().trim();
            String description = descriptionField.getText().trim();
            LocalDate dateDebut = dateDebutPicker.getValue();
            LocalDate dateFin = dateFinPicker.getValue();
            String lieu = lieuField.getText().trim();
            boolean inscriptionRequise = inscriptionRequiseCheckBox.isSelected();
            Integer nombrePlaces = null;
            EventType type = typeComboBox.getValue();

            // Validation des champs
            if (titre.isEmpty() || titre.length() < 4 || titre.length() > 50) {
                showAlert("Le titre doit contenir entre 4 et 50 caractères.");
                return;
            }
            if (description.isEmpty() || description.length() < 10 || description.length() > 500) {
                showAlert("La description doit contenir entre 10 et 500 caractères.");
                return;
            }
            if (dateDebut == null || dateDebut.isBefore(LocalDate.now())) {
                showAlert("La date de début doit être supérieure ou égale à la date actuelle.");
                return;
            }
            if (dateFin == null || dateFin.isBefore(dateDebut)) {
                showAlert("La date de fin doit être postérieure à la date de début.");
                return;
            }
            if (lieu.isEmpty() || lieu.length() < 4 || lieu.length() > 255) {
                showAlert("Le lieu doit contenir entre 4 et 255 caractères.");
                return;
            }
            if (type == null) {
                showAlert("Le type d'événement est obligatoire.");
                return;
            }

            // Traitement de l'inscription requise
            if (inscriptionRequise) {
                String placesText = nombrePlacesField.getText().trim();
                if (placesText.isEmpty()) {
                    showAlert("Veuillez entrer le nombre de places.");
                    return;
                }
                try {
                    nombrePlaces = Integer.parseInt(placesText);
                    if (nombrePlaces <= 0) {
                        showAlert("Le nombre de places doit être un nombre positif.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Le nombre de places doit être un entier valide.");
                    return;
                }
            }

            // Création de l'événement
            evenement e = new evenement(0, titre, description, dateDebut, dateFin, lieu, inscriptionRequise, nombrePlaces, type);
            int id = evenementService.ajouter(e); // La méthode ajouter doit retourner l'ID de l'événement
            e.setId(id);

            // Traitement du timer si nécessaire
            String timerText = timerField.getText().trim();
            int dureeTimer = 0;
            if (!timerText.isEmpty()) {
                try {
                    dureeTimer = Integer.parseInt(timerText);
                    if (dureeTimer <= 0) {
                        showAlert("La durée du timer doit être un entier positif.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showAlert("La durée du timer doit être un entier valide.");
                    return;
                }
            }

            // Démarrer le timer si nécessaire
            if (dureeTimer > 0) {
                timer.addTimer(id, dureeTimer * 60); // Timer en secondes
            }

            showAlert("Événement ajouté avec succès !");
            if (evenementController != null) {
                evenementController.refreshTable();
            }

            retour(null); // Retour à la page précédente

        } catch (Exception e) {
            showAlert("Erreur lors de l'ajout : " + e.getMessage());
        }
    }


    private void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/evenement.fxml"));
            Parent view = loader.load();
            StackPane contentPane = (StackPane) retourButton.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(view);

            EvenementController controller = loader.getController();
            controller.refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void clearForm() {
        titreField.clear();
        descriptionField.clear();
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        lieuField.clear();
        inscriptionRequiseCheckBox.setSelected(false);
        nombrePlacesField.clear();
        timerField.clear();
        typeComboBox.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
