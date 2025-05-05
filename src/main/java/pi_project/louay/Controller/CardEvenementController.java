package pi_project.louay.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.services.eleveservice;
import pi_project.Zayed.Utils.session;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Service.inscevenementImp;
import pi_project.louay.Utils.timer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CardEvenementController {

    private final inscevenementImp inscriptionService = new inscevenementImp();
    private final eleveservice eleveService = new eleveservice();
    private final int userId = session.getUserSession();
    @FXML
    private Label titreLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label dateDebutLabel;
    @FXML
    private Label dateFinLabel;
    @FXML
    private Label lieuLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label inscriptionRequiseLabel;
    @FXML
    private Label placesRestantesLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private Button reserverBtn;
    private evenement evenementActuel;

    public void setData(evenement evt) {
        this.evenementActuel = evt;

        titreLabel.setText(evt.getTitre());
        //descriptionLabel.setText(evt.getDescription());
        dateDebutLabel.setText("Début: " + evt.getDateDebut().toString());
        dateFinLabel.setText("Fin: " + evt.getDateFin().toString());
        lieuLabel.setText("Lieu: " + evt.getLieu());
        typeLabel.setText("Type: " + evt.getType().toString());

        // Mettre à jour les labels pour l'inscription et le nombre de places restantes
        if (evenementActuel.isInscriptionRequise()) {
            inscriptionRequiseLabel.setText("Inscription requise : avec reservation");
            int nombreInscriptions = inscriptionService.getNombreInscriptions(evenementActuel.getId());
            int placesRestantes = evenementActuel.getNombrePlaces() - nombreInscriptions;
            placesRestantesLabel.setText("Places restantes: " + placesRestantes);
            placesRestantesLabel.setVisible(true); // Rendre visible si inscription requise
        } else {
            inscriptionRequiseLabel.setText("Inscription requise : publique");
            placesRestantesLabel.setVisible(false); // Cacher si inscription non requise
        }

        setupReservationButton();
    }


    private void setupReservationButton() {
        if (evenementActuel.isInscriptionRequise()) {
            int nombreInscriptions = inscriptionService.getNombreInscriptions(evenementActuel.getId());
            int placesRestantes = evenementActuel.getNombrePlaces() - nombreInscriptions;

            // Si les places sont complètes, désactivez le bouton et affichez "Complet"
            if (placesRestantes <= 0) {
                reserverBtn.setText("Complet");
                reserverBtn.setDisable(true);
            } else {
                // Si un timer est présent pour l'expiration de la réservation
                LocalDateTime expiration = timer.getExpiration(evenementActuel.getId());

                if (expiration != null) {
                    long secondsRemaining = LocalDateTime.now().until(expiration, ChronoUnit.SECONDS);

                    // Afficher le timer dans le label
                    updateTimerLabel(secondsRemaining);

                    // Si le temps est déjà expiré, affichez directement "Expiré"
                    if (secondsRemaining <= 0) {
                        reserverBtn.setText("Expiré");
                        reserverBtn.setDisable(true);
                    } else {
                        // Démarrer le timer pour mettre à jour chaque seconde
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                            long updatedSecondsRemaining = LocalDateTime.now().until(expiration, ChronoUnit.SECONDS);
                            updateTimerLabel(updatedSecondsRemaining);

                            if (updatedSecondsRemaining <= 0) {
                                reserverBtn.setText("Expiré");
                                reserverBtn.setDisable(true);
                            } else {
                                reserverBtn.setText("Réserver");
                                reserverBtn.setDisable(false);
                            }
                        }));
                        timeline.setCycleCount(Timeline.INDEFINITE);
                        timeline.play();
                    }
                } else {
                    // Si pas de timer, affichez "Réserver" et autorisez la réservation
                    reserverBtn.setText("Réserver");
                    reserverBtn.setDisable(false);
                }

                // Action de réservation
                reserverBtn.setOnAction(e -> reserverEvenement());
            }
        } else {
            reserverBtn.setVisible(false); // Si inscription non requise, cacher le bouton
        }
    }

    private void updateTimerLabel(long secondsRemaining) {
        if (secondsRemaining <= 0) {
            // timerLabel.setText("Expiré");
        } else {
            long minutesRemaining = secondsRemaining / 60;
            long hoursRemaining = minutesRemaining / 60;
            minutesRemaining = minutesRemaining % 60;
            long secondsDisplay = secondsRemaining % 60;

            timerLabel.setText(String.format("Temps restant: %02d:%02d:%02d", hoursRemaining, minutesRemaining, secondsDisplay));
        }
    }

    private void reserverEvenement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/reserverEvenement.fxml"));
            Parent root = loader.load();

            ReservationController controller = loader.getController();
            List<eleve> enfants = eleveService.getEnfantsParParent(userId);
            controller.setEvenement(evenementActuel, enfants);

            Stage stage = new Stage();
            stage.setTitle("Réserver : " + evenementActuel.getTitre());
            stage.setScene(new Scene(root));
            stage.showAndWait();
            int newNombreInscriptions = inscriptionService.getNombreInscriptions(evenementActuel.getId());
            int newPlacesRestantes = evenementActuel.getNombrePlaces() - newNombreInscriptions;
            placesRestantesLabel.setText("Places restantes: " + newPlacesRestantes);
            setupReservationButton();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
