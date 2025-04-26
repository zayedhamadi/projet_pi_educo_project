package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pi_project.Zayed.Service.AuthenticationImpl;
import pi_project.Zayed.Utils.Constant;

import java.io.IOException;
import java.util.Objects;

public class NavbarParent {
    AuthenticationImpl auth = new AuthenticationImpl();
    @FXML
    private StackPane navbarparent;
@FXML
    private BorderPane navparent;
    public void initialize() {
        loadBoutiqueView();
    }

    public void loadBoutiqueView() {
        loadView("/Saif/BoutiqueView.fxml");
    }

    public void loadreclamationView() {
        loadView("/louay/mesreclamation.fxml");
    }

    public void loadevenementView() {
        loadView("/louay/eventliste.fxml");
    }

    public void loadmesreservationView() {
        loadView("/louay/mesreservation.fxml");
    }


    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            navbarparent.getChildren().setAll(node);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public void loadProfileUser() {
        loadView("/Zayed/ProfilParent.fxml");
    }

    public void logout() {
        auth.logout();
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Zayed/Login.fxml")));
            Stage stage = (Stage) this.navparent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("login");
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Erreur lors du chargement de la vue : " + "/Zayed/Login.fxml");
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la vue", e.getMessage());
        }
    }
}
