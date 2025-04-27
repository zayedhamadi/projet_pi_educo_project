package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pi_project.Fedi.entites.eleve;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Entity.inscriptionevenement;
import pi_project.louay.Service.inscevenementImp;
import pi_project.louay.Service.evenementImp;

import java.time.LocalDate;
import java.util.List;

public class ReservationController {

    @FXML
    private Label titreLabel, lieuLabel, dateLabel, placesLabel, errorLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<eleve> enfantComboBox;
    @FXML
    private Button inscrireButton;

    private final inscevenementImp serviceInscription = new inscevenementImp();

    private evenement evenementSelectionne;
    private List<eleve> enfantsDuParent;

    public void setEvenement(evenement ev, List<eleve> enfants) {
        this.evenementSelectionne = ev;
        this.enfantsDuParent = enfants;

        titreLabel.setText(ev.getTitre());
        descriptionArea.setText(ev.getDescription());
        lieuLabel.setText("Lieu : " + ev.getLieu());
        dateLabel.setText("Du " + ev.getDateDebut() + " au " + ev.getDateFin());
        placesLabel.setText("Places disponibles : " + ev.getNombrePlaces());


        enfantComboBox.setItems(FXCollections.observableArrayList(enfants));
    }

    @FXML
    private void initialize() {
        errorLabel.setText("");


        enfantComboBox.setCellFactory(listView -> new ListCell<eleve>() {
            @Override
            protected void updateItem(eleve item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });


        enfantComboBox.setButtonCell(new ListCell<eleve>() {
            @Override
            protected void updateItem(eleve item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });
    }

    @FXML
    private void handleInscription() {
        eleve enfant = enfantComboBox.getValue();
        if (enfant == null) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Veuillez sélectionner un enfant.");
            return;
        }

        if (evenementSelectionne.getNombrePlaces() <= 0) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Plus de places disponibles.");
            return;
        }

        // Vérification doublon
        if (serviceInscription.estDejaInscrit(enfant.getId(), evenementSelectionne.getId())) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Cet enfant est déjà inscrit à cet événement.");
            return;
        }

        // Inscription
        inscriptionevenement inscription = new inscriptionevenement();
        inscription.setEnfant_id(enfant);
        inscription.setEvenement(evenementSelectionne);
        inscription.setDateInscription(LocalDate.now());

        serviceInscription.ajouter(inscription);

        // Mise à jour de l'interface sans modifier le nombre de places
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setText("Inscription réussie !");
        placesLabel.setText("Places disponibles : " + evenementSelectionne.getNombrePlaces());
        if (errorLabel.getScene() != null && errorLabel.getScene().getWindow() != null) {
            errorLabel.getScene().getWindow().hide();
        }
    }


    @FXML
    private void handleRetour() {
        // Logique pour revenir à la page précédente (à adapter selon ton projet)
    }
}
