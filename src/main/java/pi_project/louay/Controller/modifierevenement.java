package pi_project.louay.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.time.LocalDate;

import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;
import pi_project.louay.Utils.timer;

public class modifierevenement {

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
    private TextField timerField;

    @FXML
    private Button retourButton;

    @FXML
    private Button modifierButton;

    private evenement evenementAModifier;

    private final evenementImp evenementService = new evenementImp();

    private final timer timerManager = new timer();

    public void setEvenement(evenement e) {
        this.evenementAModifier = e;
        remplirChamps(e);
    }

    @FXML
    private void initialize() {
        typeComboBox.getItems().setAll(EventType.values());

        modifierButton.setOnAction(e -> modifierEvenement());
        retourButton.setOnAction(e -> retournerVersEvenements());

        nombrePlacesField.setDisable(true);
        timerField.setDisable(true);

        inscriptionRequiseCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            nombrePlacesField.setDisable(!newVal);
            timerField.setDisable(!newVal);
            if (!newVal) {
                nombrePlacesField.clear();
                timerField.clear();
            }
        });
    }

    private void remplirChamps(evenement e) {
        titreField.setText(e.getTitre());
        descriptionField.setText(e.getDescription());
        dateDebutPicker.setValue(e.getDateDebut());
        dateFinPicker.setValue(e.getDateFin());
        lieuField.setText(e.getLieu());
        inscriptionRequiseCheckBox.setSelected(e.isInscriptionRequise());
        nombrePlacesField.setText(String.valueOf(e.getNombrePlaces()));
        nombrePlacesField.setDisable(!e.isInscriptionRequise());
        typeComboBox.setValue(e.getType());
        timerField.setText(""); // Remplace si besoin par la vraie valeur du timer
        timerField.setDisable(!e.isInscriptionRequise());
    }

    private void modifierEvenement() {
        String titre = titreField.getText().trim();
        String description = descriptionField.getText().trim();
        String lieu = lieuField.getText().trim();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        boolean inscriptionRequise = inscriptionRequiseCheckBox.isSelected();
        String placesText = nombrePlacesField.getText().trim();
        EventType type = typeComboBox.getValue();
        String timerText = timerField.getText().trim();

        if (titre.isEmpty() || description.isEmpty() || lieu.isEmpty() || dateDebut == null || dateFin == null || type == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (titre.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Le titre doit contenir au moins 4 caractères.");
            return;
        }

        if (lieu.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Le lieu doit contenir au moins 4 caractères.");
            return;
        }

        if (description.length() < 10) {
            showAlert(Alert.AlertType.ERROR, "La description doit contenir au moins 10 caractères.");
            return;
        }

        if (dateFin.isBefore(dateDebut)) {
            showAlert(Alert.AlertType.ERROR, "La date de fin doit être postérieure à la date de début.");
            return;
        }

        int nombrePlaces = 0;
        if (inscriptionRequise) {
            try {
                nombrePlaces = Integer.parseInt(placesText);
                if (nombrePlaces <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Le nombre de places doit être un entier positif.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Le nombre de places doit être un entier valide.");
                return;
            }
        }

        int dureeTimer = 0;
        if (inscriptionRequise && !timerText.isEmpty()) {
            try {
                dureeTimer = Integer.parseInt(timerText);
                if (dureeTimer <= 0) {
                    showAlert(Alert.AlertType.ERROR, "La durée du timer doit être un entier positif.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "La durée du timer doit être un entier valide.");
                return;
            }
        }

        try {
            evenementAModifier.setTitre(titre);
            evenementAModifier.setDescription(description);
            evenementAModifier.setDateDebut(dateDebut);
            evenementAModifier.setDateFin(dateFin);
            evenementAModifier.setLieu(lieu);
            evenementAModifier.setInscriptionRequise(inscriptionRequise);
            evenementAModifier.setNombrePlaces(nombrePlaces);
            evenementAModifier.setType(type);

            evenementService.modifier(evenementAModifier);

            if (inscriptionRequise && dureeTimer > 0) {
                timerManager.addTimer(evenementAModifier.getId(), dureeTimer * 60); // en secondes
            }

            showAlert(Alert.AlertType.INFORMATION, "Événement modifié avec succès !");
            retournerVersEvenements();

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification de l'événement.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void retournerVersEvenements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/evenement.fxml"));
            Parent view = loader.load();
            StackPane contentPane = (StackPane) retourButton.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(view);
            EvenementController controller = loader.getController();
            controller.refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la navigation vers la page des événements.");
        }
    }
}
