package pi_project.Zayed.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.AuthenticationImpl;
import pi_project.Zayed.Service.UserImpl;
import pi_project.Zayed.Utils.Constant;
import pi_project.Zayed.Utils.session;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ProfilAdminConnecteController {

    private final UserImpl userService = new UserImpl();
    AuthenticationImpl authentication = new AuthenticationImpl();
    @FXML
    private TextField nom, prenom, email, numTel, adresse, genre, roles, etatCompte, description;
    @FXML
    private PasswordField password;
    @FXML
    private Button editProfileButton, saveProfileButton, dashboardButton, usersButton, logoutButton, changeImageButton;
    @FXML
    private DatePicker dateNaissance;
    private User user;
    private boolean editMode = false;
    @FXML
    private ImageView profileImage;
    private String newImagePath;

    @FXML
    public void initialize() {

        loadUserData();
        setupButtonActions();
    }

    private void loadUserData() {
        // Get the logged-in user's ID from session
        Integer userId = session.getUserSession();

        if (userId != null) {
            // Fetch user data from database
            user = userService.getSpeceficUser(userId);

            if (user != null) {
                // Display user data in the form
                displayUserData();
            } else {
                Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les données utilisateur", "");
            }
        } else {
            Constant.showAlert(Alert.AlertType.WARNING, "Session expirée", "Veuillez vous reconnecter", "");
            redirectToLogin();
        }
    }

    private void displayUserData() {
        // Basic information
        nom.setText(user.getNom());
        prenom.setText(user.getPrenom());
        email.setText(user.getEmail());
        numTel.setText(String.valueOf(user.getNum_tel()));
        adresse.setText(user.getAdresse());

        // Date formatting
        if (user.getDate_naissance() != null) {
            dateNaissance.setValue(user.getDate_naissance());
        }

        genre.setText(user.getGenre().toString());
        etatCompte.setText(user.getEtat_compte().toString());
        description.setText(user.getDescription());

        StringBuilder rolesText = new StringBuilder();
        for (Role role : user.getRoles()) {
            if (!rolesText.isEmpty()) rolesText.append(", ");
            rolesText.append(role.toString());
        }
        roles.setText(rolesText.toString());

        password.setText("********");

        // Gestion de l'image - Utilisation de la même méthode que dans listActifUserController
        Constant constant = new Constant();
        Image image = this.loadImageFromPath(user.getImage());
        if (image != null) {
            profileImage.setImage(image);
        } else {
            // Image par défaut si aucune image n'est trouvée
            profileImage.setImage(new Image(getClass().getResourceAsStream("/images/default-profile.png")));
        }
    }

    public Image loadImageFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return new Image(getClass().getResourceAsStream("/images/default-profile.png"));
        }

        try {
            // Extraire seulement le nom du fichier si le chemin contient "uploads/"
            String imageName = path.startsWith("uploads/")
                    ? path.substring("uploads/".length())
                    : path;

            String fullPath = "C:\\Users\\21690\\Desktop\\projet_pi\\symfony_project-\\educo_platform\\public\\uploads\\" + imageName;
            File file = new File(fullPath);

            if (file.exists()) {
                return new Image(file.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("Erreur de chargement de l'image: " + e.getMessage());
        }
        return new Image(getClass().getResourceAsStream("/images/default-profile.png"));
    }


    private void setupButtonActions() {
        // Edit Profile Button
        editProfileButton.setOnAction(event -> toggleEditMode());

        // Save Profile Button
        saveProfileButton.setOnAction(event -> saveProfileChanges());

        // Change Image Button
        changeImageButton.setOnAction(event -> changeProfileImage());

        // Dashboard Button
        dashboardButton.setOnAction(event -> redirectToDashboard());

        // Users Button
        usersButton.setOnAction(event -> redirectToUsers());

        // Logout Button
        logoutButton.setOnAction(event -> logout());
    }

    private void toggleEditMode() {
        editMode = !editMode;

        if (editMode) {
            // Enable editing
            nom.setEditable(true);
            prenom.setEditable(true);
            numTel.setEditable(true);
            adresse.setEditable(true);
            dateNaissance.setEditable(true);
            description.setEditable(true);
            changeImageButton.setVisible(true);

            // Show save button
            saveProfileButton.setDisable(false);

            // Change edit button text
            editProfileButton.setText("Annuler");
        } else {
            // Disable editing
            nom.setEditable(false);
            prenom.setEditable(false);
            numTel.setEditable(false);
            adresse.setEditable(false);
            dateNaissance.setEditable(false);
            description.setEditable(false);
            changeImageButton.setVisible(false);
            newImagePath = null;

            // Hide save button
            saveProfileButton.setDisable(true);

            // Reset edit button text
            editProfileButton.setText("Modifier le Profil");

            // Reload original data
            displayUserData();
        }
    }

    private void changeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(profileImage.getScene().getWindow());
        if (selectedFile != null) {
            try {
                String destDir = "C:\\Users\\21690\\Desktop\\projet_pi\\symfony_project-\\educo_platform\\public\\uploads\\";
                File destFolder = new File(destDir);
                if (!destFolder.exists()) {
                    destFolder.mkdirs();
                }

                String originalFileName = selectedFile.getName();
                String destPath = destDir + originalFileName;

                // Gestion des doublons
                File destinationFile = new File(destPath);
                int counter = 1;
                while (destinationFile.exists()) {
                    String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
                    String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                    originalFileName = baseName + "_" + counter + extension;
                    destPath = destDir + originalFileName;
                    destinationFile = new File(destPath);
                    counter++;
                }

                Files.copy(selectedFile.toPath(),
                        destinationFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                profileImage.setImage(new Image(destinationFile.toURI().toString()));

                // Stocker uniquement le nom du fichier (sans "uploads/")
                newImagePath = originalFileName;

            } catch (Exception e) {
                Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image", e.getMessage());
            }
        }
    }

    private void saveProfileChanges() {
        try {
            // Validation des champs
            if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty()) {
                Constant.showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Veuillez remplir tous les champs obligatoires", "");
                return;
            }

            // Mettre à jour l'objet user
            user.setNom(nom.getText());
            user.setPrenom(prenom.getText());
            user.setEmail(email.getText());

            try {
                user.setNum_tel(Integer.parseInt(numTel.getText()));
            } catch (NumberFormatException e) {
                Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Numéro de téléphone invalide", "Veuillez entrer un nombre valide");
                return;
            }

            user.setAdresse(adresse.getText());
            user.setDate_naissance(dateNaissance.getValue());
            user.setDescription(description.getText());

            // Gestion de l'image
            if (newImagePath != null) {
                // Supprimer l'ancienne image si elle existe et si c'est une image différente
                if (user.getImage() != null && !user.getImage().isEmpty()) {
                    // Extraire seulement le nom du fichier si le chemin contient "uploads/"
                    String oldImageName = user.getImage().startsWith("uploads/")
                            ? user.getImage().substring("uploads/".length())
                            : user.getImage();

                    File oldImage = new File("C:\\Users\\21690\\Desktop\\projet_pi\\symfony_project-\\educo_platform\\public\\uploads\\" + oldImageName);
                    if (oldImage.exists()) {
                        oldImage.delete();
                    }
                }
                user.setImage(newImagePath); // Stocke seulement le nom du fichier
            }

            // Mettre à jour dans la base de données
            User updatedUser = userService.updateUser(user);
            if (updatedUser != null) {
                this.user = updatedUser;
                Constant.showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil mis à jour avec succès", "");
                toggleEditMode();
                displayUserData();
            } else {
                Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour", "La mise à jour n'a pas été effectuée");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour du profil", e.getMessage());
        }
    }



    private void redirectToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) dashboardButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'accéder au dashboard", e.getMessage());
        }
    }

    private void redirectToUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/listActifUser.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usersButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Utilisateurs");
        } catch (Exception e) {
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'accéder à la gestion des utilisateurs", e.getMessage());
        }
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (Exception e) {
            System.out.println("Error redirecting to login: " + e.getMessage());
        }
    }

    private void logout() {
        authentication.logout();
        redirectToLogin();
    }
}