package pi_project.Zayed.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Service.CesserImpl;

import java.util.Objects;
import java.util.Optional;

public class ConsulterUserProfileController {

    @Setter @Getter
    private listActifUserController parentController;

    @FXML private ImageView profileImage;
    @FXML private TextField nom, prenom, email, numTel, adresse, genre, roles, etatCompte;
    @FXML private TextArea description;
    @FXML private DatePicker dateNaissance;
    @FXML private Button CesserProfileButton;

    private User currentUser;
    private CesserImpl cesserService = new CesserImpl();

    public void initData(User user) {
        this.currentUser = user;
        nom.setText(user.getNom());
        prenom.setText(user.getPrenom());
        email.setText(user.getEmail());
        numTel.setText(String.valueOf(user.getNum_tel()));
        adresse.setText(user.getAdresse());
        dateNaissance.setValue(user.getDate_naissance());
        genre.setText(user.getGenre().toString());
        etatCompte.setText(user.getEtat_compte().toString());
        description.setText(user.getDescription());

        String rolesText = String.join(", ",
                user.getRoles().stream()
                        .map(Enum::toString)
                        .toArray(String[]::new));
        roles.setText(rolesText);

        Image image = loadImageFromPath(user.getImage());
        profileImage.setImage(image != null ? image : new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default-profile.png"))) );

        if (user.getEtat_compte().toString().equals("inactive")) {
            CesserProfileButton.setDisable(true);
            CesserProfileButton.setText("Utilisateur déjà cessé");
        }
    }

    @FXML
    private void handleCesserUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cessation d'utilisateur");
        dialog.setHeaderText("Motif de la cessation");
        dialog.setContentText("Veuillez entrer le motif:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(motif -> {
            if (!motif.isEmpty()) {
                // Confirmation avant de procéder
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation");
                confirmation.setHeaderText("Confirmer la cessation");
                confirmation.setContentText("Êtes-vous sûr de vouloir cesser cet utilisateur?\nMotif: " + motif);

                Optional<ButtonType> confirmResult = confirmation.showAndWait();
                if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                    try {
                        cesserService.cesserUser(currentUser.getId(), motif);
                        // Mettre à jour l'interface
                        etatCompte.setText("inactive");
                        CesserProfileButton.setDisable(true);
                        CesserProfileButton.setText("Utilisateur cessé");

                        // Afficher un message de succès
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Succès");
                        success.setHeaderText(null);
                        success.setContentText("L'utilisateur a été cessé avec succès!");
                        success.showAndWait();

                        if (parentController != null) {
                            parentController.refreshList();
                        }
                    } catch (Exception e) {
                        showErrorAlert("Erreur lors de la cessation", e.getMessage());
                    }
                }
            } else {
                showErrorAlert("Erreur", "Le motif ne peut pas être vide");
            }
        });
    }

    @FXML
    private void handleRetour() {
        parentController.returnToList();
    }

    private Image loadImageFromPath(String path) {
        try {
            if (path != null && !path.isEmpty()) {
                String fullPath = "C:/Users/21690/Desktop/projet_pi/symfony_project-/educo_platform/public/uploads/" + path;
                return new Image("file:" + fullPath);
            }
        } catch (Exception e) {
            System.out.println("Erreur de chargement de l'image: " + e.getMessage());
        }
        return null;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}