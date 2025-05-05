package pi_project.Zayed.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Service.AuthenticationImpl;
import pi_project.Zayed.Service.securiteLogin;
import pi_project.Zayed.Utils.Constant;

import java.util.Objects;

public class LoginController {


    @FXML
    private StackPane s;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;


    @FXML
    private ImageView eyeImage;
    private boolean isPasswordVisible = false;
    private TextField visiblePasswordField;


    @FXML
    private void initialize() {
        if (s != null) {
            eyeImage.setOnMouseClicked(event -> togglePasswordVisibility());

            visiblePasswordField = new TextField();
            visiblePasswordField.setManaged(false);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.textProperty().bindBidirectional(password.textProperty());
            visiblePasswordField.promptTextProperty().bind(password.promptTextProperty());
            visiblePasswordField.setStyle("-fx-background-color: transparent; -fx-border-color: #0596ff; -fx-border-width: 0px 0px 2px 0px;");
            visiblePasswordField.setLayoutX(password.getLayoutX());
            visiblePasswordField.setLayoutY(password.getLayoutY());
            visiblePasswordField.setPrefSize(password.getPrefWidth(), password.getPrefHeight());
            s.getChildren().add(visiblePasswordField);
        } else {
            System.out.println("StackPane 's' is null.");
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            password.setVisible(true);
            password.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            isPasswordVisible = false;
            StackPane.setAlignment(eyeImage, Pos.CENTER_RIGHT);
        } else {
            password.setVisible(false);
            password.setManaged(false);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            isPasswordVisible = true;
            StackPane.setAlignment(eyeImage, Pos.CENTER_LEFT);
        }
    }

//    @FXML
//    private void login() {
//        String userEmail = email.getText().trim();
//        String userPassword = password.getText().trim();
//
//        if (userEmail.isEmpty() || userPassword.isEmpty()) {
//            Constant.showAlert(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs", "Échec");
//            return;
//        }
//
//        if (!userEmail.matches(".+@.+\\..+")) {
//            Constant.showAlert(Alert.AlertType.WARNING, "Email invalide", "Format d'email incorrect", "Échec");
//            return;
//        }
//
//        try {
//            User user = new User(userEmail, userPassword);
//            AuthenticationImpl authService = new AuthenticationImpl();
//            boolean isAuthenticated = authService.login(user);
//
//            if (isAuthenticated) {
//                Role userRole = authService.getUserRole(userEmail);
//                if (userRole != null) {
//                    switch (userRole) {
//                        case Admin -> {
//                            Constant.showAlert(Alert.AlertType.INFORMATION, "Connexion réussie", "Bienvenue Admin", "Vous avez accès en tant qu'Admin");
//                            loadScene("/Saif/MainLayout.fxml", "Profil Admin");
//                        }
//                        case Enseignant -> {
//                            Constant.showAlert(Alert.AlertType.INFORMATION, "Connexion réussie", "Bienvenue Enseignant", "Vous avez accès en tant qu'Enseignant");

    /// /                            loadScene("/Zayed/ProfilEnseignant.fxml", "Profil Enseignant");
//                            loadScene("/Aziz/enseignanlayout.fxml", "Profil Enseignant");
//
//                        }
//                        case Parent -> {
//                            Constant.showAlert(Alert.AlertType.INFORMATION, "Connexion réussie", "Bienvenue Parent", "Vous avez accès en tant que Parent");
//                            //loadScene("/Zayed/ProfilParent.fxml", "Profil Parent");
//                            loadScene("/Saif/NavbarParent.fxml", "Profil Parent");
//
//                        }
//                    }
//                } else {
//                    Constant.showAlert(Alert.AlertType.WARNING, "Connexion réussie", "Aucun rôle valide trouvé", "Accès restreint");
//                }
//            } else {
//                Constant.showAlert(Alert.AlertType.ERROR, "Échec", "Identifiants incorrects", "Échec");
//            }
//        } catch (Exception e) {
//            Constant.handleException(e, "Une erreur est survenue pendant la connexion");
//        }
//    }
    private void loadScene(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Stage stage = (Stage) email.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Erreur lors du chargement de la vue : " + fxmlPath);
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la vue", e.getMessage());
        }
    }

    @FXML
    public void goingToForgetPwPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/ChoixForgetPw.fxml"));
            Parent root = loader.load();


            Stage currentStage = (Stage) s.getScene().getWindow();


            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle(" Mot de passe oublié");
            currentStage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "problem on going to forget password", "Erreur d aller a la page forget password");
        }
    }


    @FXML
    public void GoingToVerificationCode() {
        String userEmail = email.getText().trim();
        String userPassword = password.getText().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Constant.showAlert(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs", "Échec");
            return;
        }

        if (!userEmail.matches(".+@.+\\..+")) {
            Constant.showAlert(Alert.AlertType.WARNING, "Email invalide", "Format d'email incorrect", "Échec");
            return;
        }

        try {
            User user = new User(userEmail, userPassword);
            AuthenticationImpl authService = new AuthenticationImpl();
            boolean isAuthenticated = authService.login(user);

            if (isAuthenticated) {
                securiteLogin securiteLogin = new securiteLogin();
                securiteLogin.generateAndSendVerificationCode(userEmail);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/VerificationCode.fxml"));
                Parent root = loader.load();
                VerificationController verificationController = loader.getController();
                verificationController.setUserEmail(userEmail);
                Stage currentStage = (Stage) s.getScene().getWindow();
                Scene scene = new Scene(root);
                currentStage.setScene(scene);
                currentStage.setTitle("Vérification du code");
                currentStage.show();
            } else {
                Constant.showAlert(Alert.AlertType.ERROR, "Échec", "Identifiants incorrects", "Échec");
            }
        } catch (Exception e) {
            Constant.handleException(e, "Une erreur est survenue pendant la connexion");
        }

    }
}