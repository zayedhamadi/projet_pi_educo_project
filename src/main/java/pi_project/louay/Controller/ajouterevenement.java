package pi_project.louay.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;

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
    private ComboBox<EventType> typeComboBox;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button retourButton;

    private final evenementImp evenementService = new evenementImp();

    @FXML
    public void initialize() {
        // Remplir la ComboBox avec les valeurs de l'enum EventType
        typeComboBox.getItems().setAll(EventType.values());

        // Désactiver le champ "Nombre de places" par défaut
        nombrePlacesField.setDisable(true);

        // Activer/désactiver selon la checkbox
        inscriptionRequiseCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            nombrePlacesField.setDisable(!newVal);
            if (!newVal) {
                nombrePlacesField.clear();
            }
        });

        // Action du bouton ajouter
        ajouterButton.setOnAction(this::ajouterEvenement);

        // Action du bouton retour
        retourButton.setOnAction(this::retour);
    }

    private void ajouterEvenement(ActionEvent event) {
        try {
            String titre = titreField.getText().trim();
            String description = descriptionField.getText().trim();
            LocalDate dateDebut = dateDebutPicker.getValue();
            LocalDate dateFin = dateFinPicker.getValue();
            String lieu = lieuField.getText().trim();
            boolean inscriptionRequise = inscriptionRequiseCheckBox.isSelected();
            Integer nombrePlaces = null;
            EventType type = typeComboBox.getValue();

            // === CONTRÔLES DE BASE ===
            if (titre.isEmpty()) {
                showAlert("Le titre est obligatoire.");
                return;
            }
            if (titre.length() < 4 || titre.length() > 50) {
                showAlert("Le titre doit contenir entre 4 et 50 caractères.");
                return;
            }

            if (description.isEmpty()) {
                showAlert("La description est obligatoire.");
                return;
            }
            if (description.length() < 10 || description.length() > 500) {
                showAlert("La description doit contenir entre 10 et 500 caractères.");
                return;
            }

            if (dateDebut == null) {
                showAlert("La date de début est obligatoire.");
                return;
            }
            if (dateDebut.isBefore(LocalDate.now())) {
                showAlert("La date de début doit être supérieure à la date actuelle.");
                return;
            }

            if (dateFin == null) {
                showAlert("La date de fin est obligatoire.");
                return;
            }
            if (dateFin.isBefore(dateDebut)) {
                showAlert("La date de fin doit être postérieure à la date de début.");
                return;
            }

            if (lieu.isEmpty()) {
                showAlert("Le lieu est obligatoire.");
                return;
            }
            if (lieu.length() < 4 || lieu.length() > 255) {
                showAlert("Le lieu doit contenir entre 4 et 255 caractères.");
                return;
            }

            if (type == null) {
                showAlert("Le type d'événement est obligatoire.");
                return;
            }

            // === CONTRÔLE SI INSCRIPTION REQUISE ===
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

            // === Création de l'objet ===
            evenement e = new evenement(0, titre, description, dateDebut, dateFin, lieu, inscriptionRequise, nombrePlaces, type);

            evenementService.ajouter(e);
            showAlert("Événement ajouté avec succès !");
            clearForm();
            ((Button) event.getSource()).getScene().getWindow().hide();

        } catch (Exception e) {
            showAlert("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    private void retour(ActionEvent event) {
        // Ferme la fenêtre actuelle et retourne à la fenêtre précédente
        Stage stage = (Stage) retourButton.getScene().getWindow();
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
        typeComboBox.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
