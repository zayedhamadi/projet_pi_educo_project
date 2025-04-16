package pi_project.louay.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;

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
    private ComboBox<String> typeComboBox;

    @FXML
    private Button retourButton;

    @FXML
    private Button modifierButton;

    private evenement evenementAModifier;

    private final evenementImp evenementService = new evenementImp();

    public void setEvenement(evenement e) {
        this.evenementAModifier = e;
        remplirChamps(e);
    }

    @FXML
    private void initialize() {
        // Charger les types d'événements dans le ComboBox
        for (EventType type : EventType.values()) {
            typeComboBox.getItems().add(type.name());
        }

        // Gérer les actions des boutons
        modifierButton.setOnAction(e -> modifierEvenement());
        retourButton.setOnAction(e -> fermerFenetre());

        // Désactiver le champ de nombre de places par défaut
        nombrePlacesField.setDisable(true);

        // Activer/désactiver selon la checkbox
        inscriptionRequiseCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            nombrePlacesField.setDisable(!newVal);
            if (!newVal) {
                nombrePlacesField.clear();
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
        nombrePlacesField.setDisable(!e.isInscriptionRequise()); // 🔥 Ajout important pour activer/désactiver le champ
        typeComboBox.setValue(e.getType().name());
    }

    private void modifierEvenement() {
        String titre = titreField.getText().trim();
        String description = descriptionField.getText().trim();
        String lieu = lieuField.getText().trim();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        boolean inscriptionRequise = inscriptionRequiseCheckBox.isSelected();
        String placesText = nombrePlacesField.getText().trim();
        String type = typeComboBox.getValue();

        // Vérification des champs obligatoires
        if (titre.isEmpty() || description.isEmpty() || lieu.isEmpty() || dateDebut == null || dateFin == null || type == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Longueur minimale du titre et lieu (4 caractères)
        if (titre.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Le titre doit contenir au moins 4 caractères.");
            return;
        }

        if (lieu.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Le lieu doit contenir au moins 4 caractères.");
            return;
        }

        // Longueur minimale de la description (10 caractères)
        if (description.length() < 10) {
            showAlert(Alert.AlertType.ERROR, "La description doit contenir au moins 10 caractères.");
            return;
        }

        // Vérification que la date de fin est après la date de début
        if (dateFin.isBefore(dateDebut)) {
            showAlert(Alert.AlertType.ERROR, "La date de fin doit être postérieure à la date de début.");
            return;
        }

        // Vérification du nombre de places (positif si inscription requise)
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

        try {
            // Mettre à jour l'événement
            evenementAModifier.setTitre(titre);
            evenementAModifier.setDescription(description);
            evenementAModifier.setDateDebut(dateDebut);
            evenementAModifier.setDateFin(dateFin);
            evenementAModifier.setLieu(lieu);
            evenementAModifier.setInscriptionRequise(inscriptionRequise);
            evenementAModifier.setNombrePlaces(nombrePlaces);
            evenementAModifier.setType(EventType.valueOf(type));

            evenementService.modifier(evenementAModifier);

            showAlert(Alert.AlertType.INFORMATION, "Événement modifié avec succès !");
            fermerFenetre();

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

    private void fermerFenetre() {
        Stage stage = (Stage) retourButton.getScene().getWindow();
        stage.close();
    }
}
