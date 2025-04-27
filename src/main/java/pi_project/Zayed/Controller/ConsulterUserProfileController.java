package pi_project.Zayed.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.CesserImpl;
import pi_project.Zayed.Service.UserImpl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class ConsulterUserProfileController {

    private final CesserImpl cesserService = new CesserImpl();
    private final UserImpl userService = new UserImpl();
    UserImpl userImpl = new UserImpl();
    @Setter
    @Getter
    private listActifUserController parentController;

    @FXML
    private ImageView profileImage;
    @FXML
    private TextField nom, prenom, email, numTel, adresse, etatCompte;
    @FXML
    private ComboBox<Genre> genreCombo;
    @FXML
    private ComboBox<Role> rolesCombo;
    @FXML
    private TextArea description;
    @FXML
    private DatePicker dateNaissance;
    @FXML
    private Button UpdateProfileButton, CesserProfileButton;

    private boolean editMode = false;
    private User currentUser;

    @FXML
    public void initialize() {
        // Initialise les ComboBox avec les valeurs des énumérations
        genreCombo.setItems(FXCollections.observableArrayList(Genre.values()));
        rolesCombo.setItems(FXCollections.observableArrayList(Role.values()));
    }

    public void initData(User user) {
        this.currentUser = user;
        nom.setText(user.getNom());
        prenom.setText(user.getPrenom());
        email.setText(user.getEmail());
        numTel.setText(String.valueOf(user.getNum_tel()));
        adresse.setText(user.getAdresse());
        dateNaissance.setValue(user.getDate_naissance());
        genreCombo.setValue(user.getGenre());

        // Pour gérer plusieurs rôles, on prend le premier rôle comme exemple
        if (!user.getRoles().isEmpty()) {
            rolesCombo.setValue(user.getRoles().iterator().next());
        }

        etatCompte.setText(user.getEtat_compte().toString());
        description.setText(user.getDescription());

        Image image = loadImageFromPath(user.getImage());
        profileImage.setImage(image != null ? image : new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default-profile.png"))));

        if (user.getEtat_compte().toString().equals("inactive")) {
            CesserProfileButton.setDisable(true);
            CesserProfileButton.setText("Utilisateur déjà cessé");
        }
    }

    @FXML
    private void handleUpdateUser() {
        if (!editMode) {
            toggleEditMode(true);
            UpdateProfileButton.setText("Enregistrer les modifications");
        } else {
            if (validateInput()) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation");
                confirmation.setHeaderText("Confirmer la modification");
                confirmation.setContentText("Êtes-vous sûr de vouloir modifier cet utilisateur?");

                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        currentUser.setNom(nom.getText());
                        currentUser.setPrenom(prenom.getText());
                        currentUser.setEmail(email.getText());
                        currentUser.setNum_tel(Integer.parseInt(numTel.getText()));
                        currentUser.setAdresse(adresse.getText());
                        currentUser.setDate_naissance(dateNaissance.getValue());
                        currentUser.setDescription(description.getText());
                        currentUser.setGenre(genreCombo.getValue());

                        // Mise à jour des rôles
                        currentUser.getRoles().clear();
                        currentUser.getRoles().add(rolesCombo.getValue());

                        userService.updateUser(currentUser);

                        toggleEditMode(false);
                        UpdateProfileButton.setText("Modifier les données");

                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Succès");
                        success.setHeaderText(null);
                        success.setContentText("L'utilisateur a été modifié avec succès!");
                        success.showAndWait();

                    } catch (Exception e) {
                        showErrorAlert("Erreur lors de la modification", e.getMessage());
                    }
                }
            }
        }
    }

    private boolean validateInput() {
        try {
            Integer.parseInt(numTel.getText());
        } catch (NumberFormatException e) {
            showErrorAlert("Erreur de validation", "Le numéro de téléphone doit être un nombre valide");
            return false;
        }

        if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty()) {
            showErrorAlert("Erreur de validation", "Les champs Nom, Prénom et Email sont obligatoires");
            return false;
        }

        if (!email.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showErrorAlert("Erreur de validation", "L'adresse email n'est pas valide");
            return false;
        }

        if (genreCombo.getValue() == null) {
            showErrorAlert("Erreur de validation", "Le genre doit être sélectionné");
            return false;
        }

        if (rolesCombo.getValue() == null) {
            showErrorAlert("Erreur de validation", "Au moins un rôle doit être sélectionné");
            return false;
        }

        return true;
    }

    private void toggleEditMode(boolean edit) {
        this.editMode = edit;
        nom.setEditable(edit);
        prenom.setEditable(edit);
        email.setEditable(edit);
        numTel.setEditable(edit);
        adresse.setEditable(edit);
        dateNaissance.setDisable(!edit);
        description.setEditable(edit);
        genreCombo.setDisable(!edit);
        rolesCombo.setDisable(!edit);
        etatCompte.setEditable(false);
    }

    @FXML
    private void handleCesserUser() {
        // Création de la boîte de dialogue pour saisir le motif
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cessation d'utilisateur");
        dialog.setHeaderText("Motif de la cessation");
        dialog.setContentText("Veuillez entrer le motif:");

        // Configuration des boutons de la boîte de dialogue
        ButtonType confirmButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(confirmButtonType, ButtonType.CANCEL);

        // Affichage de la boîte de dialogue et attente de la réponse
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(motif -> {
            // Validation du motif
            if (motif == null || motif.trim().isEmpty()) {
                showErrorAlert("Erreur de saisie", "Le motif de cessation ne peut pas être vide");
                return;
            }

            // Boîte de dialogue de confirmation
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de cessation");
            confirmation.setHeaderText("Vous êtes sur le point de cesser cet utilisateur");
            confirmation.setContentText("Motif: " + motif + "\n\nCette action est irréversible. Confirmer?");

            Optional<ButtonType> confirmResult = confirmation.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                try {
                    // Appel du service de cessation
                    cesserService.cesserUser(currentUser.getId(), motif.trim());

                    // Mise à jour de l'interface utilisateur
                    updateUIAfterCessation();

                    // Affichage du message de succès
                    showSuccessAlert("Succès", "L'utilisateur a été cessé avec succès!");

                    // Rafraîchissement de la liste parente si disponible
                    refreshParentList();

                } catch (IllegalArgumentException e) {
                    showErrorAlert("Erreur de validation", e.getMessage());
                } catch (RuntimeException e) {
                    showErrorAlert("Erreur système", "Une erreur est survenue lors de la cessation: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUIAfterCessation() {
        etatCompte.setText("inactive");
        CesserProfileButton.setDisable(true);
        CesserProfileButton.setText("Utilisateur cessé");
        CesserProfileButton.setStyle("-fx-background-color: #ffcccc;");
    }

    private void showSuccessAlert(String title, String message) {
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle(title);
        success.setHeaderText(null);
        success.setContentText(message);
        success.showAndWait();
    }

    private void refreshParentList() {
        if (parentController != null) {
            parentController.refreshList();
        }
    }

    @FXML
    private void handleRetour() {
        parentController.returnToList();
    }
    private Image loadImageFromPath(String path) {
        try {
            if (path != null && !path.isEmpty()) {
                Properties props = new Properties();
                props.load(new FileInputStream("config.properties"));
                String uploadPath = props.getProperty("upload.path");

                String fullPath = uploadPath + "/" + path;
                File file = new File(fullPath);
                if (file.exists()) {
                    return new Image(file.toURI().toString());
                } else {
                    System.out.println("Image non trouvée à : " + fullPath);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur de chargement de l'image: " + e.getMessage());
        }
        return null;
    }


//    private Image loadImageFromPath(String path) {
//        try {
//            if (path != null && !path.isEmpty()) {
//                String fullPath = "C:/Users/21690/Desktop/projet_pi/symfony_project-/educo_platform/public/uploads/" + path;
//                return new Image("file:" + fullPath);
//            }
//        } catch (Exception e) {
//            System.out.println("Erreur de chargement de l'image: " + e.getMessage());
//        }
//        return null;
//    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}