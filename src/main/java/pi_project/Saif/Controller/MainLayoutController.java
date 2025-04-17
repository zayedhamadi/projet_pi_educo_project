package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pi_project.Zayed.Service.AuthenticationImpl;

import java.io.IOException;

public class MainLayoutController {
    AuthenticationImpl auth = new AuthenticationImpl();
    @FXML
    private Button logoutt;
    @FXML
    private StackPane contentPane;

    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showProduitView() {
        loadView("/Saif/ProduitView.fxml");
    }

    @FXML
    public void showCategorieView() {
        loadView("/Saif/CategorieView.fxml");
    }

    public void initialize() {
        showCategorieView(); //
    }

    public void ListeUsersActives() {
        this.loadView("/Zayed/listActifUser.fxml");
    }

    public void ListeUsersInactives() {
        this.loadView("/Zayed/listInactifUser.fxml");
    }

    public void ProfilAdmin() {
        this.loadView("/Zayed/ProfilAdmin.fxml");
    }

@FXML
    private void logout() {
        try {
            this.auth.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) this.logoutt.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (Exception e) {
            System.out.println("Error redirecting to login: " + e.getMessage());
        }
    }


}
