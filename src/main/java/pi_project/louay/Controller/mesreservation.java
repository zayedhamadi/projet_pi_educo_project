package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import pi_project.Zayed.Utils.session;
import pi_project.louay.Service.inscevenementImp;
import pi_project.louay.Entity.inscriptionevenement;
import javafx.beans.property.SimpleStringProperty;


public class mesreservation {

    @FXML
    private TableView<inscriptionevenement> reservationTable;

    @FXML
    private TableColumn<inscriptionevenement, String> evenementColumn;

    @FXML
    private TableColumn<inscriptionevenement, String> enfantColumn;

    @FXML
    private TableColumn<inscriptionevenement, String> dateInscriptionColumn;

    @FXML
    private TableColumn<inscriptionevenement, Void> actionsColumn;

    //@FXML
    //private Button backButton;

    private ObservableList<inscriptionevenement> reservationList;

    @FXML
    public void initialize() {
        reservationList = FXCollections.observableArrayList();

        inscevenementImp service = new inscevenementImp();
        int userId = session.getUserSession();
        reservationList.addAll(service.getReservationsForParent(userId));


        evenementColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEvenement().getTitre())
        );


        enfantColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEnfant_id().getNom())
        );


        dateInscriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateInscription().toString())
        );


        actionsColumn.setCellFactory(new Callback<TableColumn<inscriptionevenement, Void>, TableCell<inscriptionevenement, Void>>() {
            @Override
            public TableCell<inscriptionevenement, Void> call(final TableColumn<inscriptionevenement, Void> param) {
                return new TableCell<inscriptionevenement, Void>() {
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        deleteButton.setOnAction(event -> {
                            inscriptionevenement data = getTableView().getItems().get(getIndex());
                            handleDelete(data);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        });

        reservationTable.setItems(reservationList);


      //  backButton.setOnAction(event -> handleBack());
    }

    private void handleDelete(inscriptionevenement inscription) {
        inscevenementImp service = new inscevenementImp();
        service.supprimer(inscription);
        reservationList.remove(inscription);
    }

    //private void handleBack() {
    //    System.out.println("Retour");
    //}
}
