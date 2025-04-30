package pi_project.Aziz.Controller;

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

public class EnseignantlyoutController {
    AuthenticationImpl auth = new AuthenticationImpl();
    @FXML
    private StackPane contentPane;

    @FXML
    private BorderPane mainLayout;


    @FXML
    public void showquiz() {
        loadView("/Aziz/afficherquiz.fxml");
    }

    @FXML
    public void showCoursView() {
        loadView("/Farouk/cours_list.fxml") ;
    }

    @FXML
    public void showClendadrViewEnsg() {
        loadView("/Farouk/view_calendar.fxml") ;
    }

    @FXML

    public void showquestion() {
        loadView("/Aziz/afficherquestion.fxml");
    }
    public void showquizstats() {
        loadView("/Aziz/EnsegnatQuizStats.fxml");
    }
    public void loadreclamationView() {
        loadView("/louay/mesreclamation.fxml");
    }

    public void initialize() {
        showquiz();
    }

    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProfileUser() {
        loadView("/Zayed/ProfilEnseignant.fxml");
    }

    public void logout() {
        auth.logout();
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Zayed/Login.fxml")));
            Stage stage = (Stage) this.mainLayout.getScene().getWindow();
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
