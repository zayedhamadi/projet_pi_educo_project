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
import lombok.Setter;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.AuthenticationImpl;
import pi_project.Zayed.Service.UserImpl;
import pi_project.Zayed.Utils.Constant;
import pi_project.Zayed.Utils.PasswordUtils;
import pi_project.Zayed.Utils.session;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Properties;

public class ProfilAdminController {
    private final UserImpl userService = new UserImpl();
    AuthenticationImpl authentication = new AuthenticationImpl();
    @Setter
    private listActifUserController listActifUserController;
    @FXML
    private TextField nom, prenom, email, numTel, adresse, genre, roles, etatCompte;
    @FXML
    private TextArea description;
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
        Integer userId = session.getUserSession();

        if (userId != null) {
            user = userService.getSpeceficUser(userId);

            if (user != null) {
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
        nom.setText(user.getNom());
        prenom.setText(user.getPrenom());
        email.setText(user.getEmail());
        numTel.setText(String.valueOf(user.getNum_tel()));
        adresse.setText(user.getAdresse());

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

        Image image = this.loadImageFromPath(user.getImage());
        profileImage.setImage(Objects.requireNonNullElseGet(image, () -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default-profile.png")))));
    }

//    public Image loadImageFromPath(String path) {
//        if (path == null || path.isEmpty()) {
//            return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default-profile.png")));
//        }
//
//        try {
//            String imageName = path.startsWith("uploads/")
//                    ? path.substring("uploads/".length())
//                    : path;
//
//            String fullPath = "C:\\Users\\21690\\Desktop\\projet_pi\\symfony_project-\\educo_platform\\public\\uploads\\" + imageName;
//            File file = new File(fullPath);
//
//            if (file.exists()) {
//                return new Image(file.toURI().toString());
//            }
//        } catch (Exception e) {
//            System.out.println("Erreur de chargement de l'image: " + e.getMessage());
//        }
//        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default-profile.png")));
//    }

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



    private void setupButtonActions() {
        editProfileButton.setOnAction(event -> toggleEditMode());
        saveProfileButton.setOnAction(event -> saveProfileChanges());
        changeImageButton.setOnAction(event -> changeProfileImage());
       // dashboardButton.setOnAction(event -> redirectToDashboard());
        //usersButton.setOnAction(event -> redirectToUsers());
        // logoutButton.setOnAction(event -> logout());
    }

    private void toggleEditMode() {
        editMode = !editMode;

        if (editMode) {
            nom.setEditable(true);
            prenom.setEditable(true);
            numTel.setEditable(true);
            adresse.setEditable(true);
            dateNaissance.setEditable(true);
            description.setEditable(true);
            changeImageButton.setVisible(true);
            password.setEditable(true);
            password.setText("");
            saveProfileButton.setDisable(false);

            editProfileButton.setText("Annuler");
        } else {
            nom.setEditable(false);
            prenom.setEditable(false);
            numTel.setEditable(false);
            adresse.setEditable(false);
            dateNaissance.setEditable(false);
            description.setEditable(false);
            changeImageButton.setVisible(false);
            password.setEditable(false);
            password.setText("********");
            newImagePath = null;

            saveProfileButton.setDisable(true);

            editProfileButton.setText("Modifier le Profil");

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
                // Lire le chemin d'upload depuis config.properties
                Properties props = new Properties();
                props.load(new FileInputStream("config.properties"));
                String uploadPath = props.getProperty("upload.path");

                File destFolder = new File(uploadPath);
                if (!destFolder.exists()) {
                    destFolder.mkdirs();
                }

                // Nettoyage du nom de fichier pour éviter les problèmes
                String originalFileName = selectedFile.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                File destinationFile = new File(uploadPath, originalFileName);

                // Éviter les doublons en ajoutant un compteur
                int counter = 1;
                while (destinationFile.exists()) {
                    String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
                    String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                    originalFileName = baseName + "_" + counter + extension;
                    destinationFile = new File(uploadPath, originalFileName);
                    counter++;
                }

                // Copier l'image
                Files.copy(selectedFile.toPath(),
                        destinationFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                // Charger l'image dans l'interface
                profileImage.setImage(new Image(destinationFile.toURI().toString()));

                // Stocker uniquement le nom du fichier pour l'enregistrer dans la base
                newImagePath = originalFileName;

            } catch (Exception e) {
                Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image", e.getMessage());
            }
        }
    }


    private void saveProfileChanges() {
        try {
            if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty()) {
                Constant.showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Veuillez remplir tous les champs obligatoires", "");
                return;
            }

            if (!password.getText().isEmpty() && !password.getText().equals("********")) {
                String hashedPwd = PasswordUtils.cryptPw(password.getText());
                user.setPassword(hashedPwd);
            }

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

            if (newImagePath != null) {
                // Supprimer l'ancienne image si elle existe
                if (user.getImage() != null && !user.getImage().isEmpty()) {
                    try {
                        Properties props = new Properties();
                        props.load(new FileInputStream("config.properties"));
                        String uploadPath = props.getProperty("upload.path");

                        String oldImageName = user.getImage();
                        File oldImage = new File(uploadPath, oldImageName);
                        if (oldImage.exists()) {
                            oldImage.delete();
                        }
                    } catch (Exception e) {
                        System.out.println("Erreur lors de la suppression de l'ancienne image: " + e.getMessage());
                    }
                }
                user.setImage(newImagePath);
            }

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

    @FXML
    private void redirectToAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/addUser.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (Exception e) {
            System.out.println("Error redirecting to login: " + e.getMessage());
        }
    }

    @FXML
    private void Usersinactives() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/listInactifUser.fxml"));
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

    @FXML
    public void gestionproduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ProduitView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (Exception e) {
            System.out.println("Error redirecting to login: " + e.getMessage());
        }
    }

    @FXML
    public void reclamation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/reclamation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (Exception e) {
            System.out.println("Error redirecting to login: " + e.getMessage());
        }
    }

    @FXML
    public void evenement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/evenement.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (Exception e) {
            System.out.println("Error redirecting to login: " + e.getMessage());
        }
    }
}