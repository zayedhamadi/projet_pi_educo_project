package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pi_project.Zayed.Utils.session;
import pi_project.louay.Entity.reclamation;
import pi_project.louay.Enum.Statut;
import pi_project.louay.Service.reclamationImp;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class mesRController implements Initializable {

    @FXML
    private TableView<reclamation> tableView;

    //@FXML
    //private TableColumn<reclamation, Integer> colId;

    @FXML
    private TableColumn<reclamation, String> colTitre;

    @FXML
    private TableColumn<reclamation, String> colDescription;

    @FXML
    private TableColumn<reclamation, Statut> colStatut;

    @FXML
    private Button ajouterBtn;

    private final reclamationImp reclamationService = new reclamationImp();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));


        Integer userId = session.getUserSession();


        List<reclamation> userReclamations = reclamationService.getByUserId(userId);
        ObservableList<reclamation> data = FXCollections.observableArrayList(userReclamations);
        tableView.setItems(data);


        ajouterBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/ajouterreclamation.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Ajouter une Réclamation");
                stage.setScene(new Scene(root));
                stage.showAndWait();



                List<reclamation> nouvellesDonnees = reclamationService.getByUserId(userId);
                tableView.getItems().setAll(nouvellesDonnees);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
