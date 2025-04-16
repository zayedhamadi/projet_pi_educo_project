package pi_project.Farouk.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pi_project.Farouk.Models.Cours;
import pi_project.Farouk.Models.Matiere;
import pi_project.Farouk.Services.CoursService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CoursListController {

    @FXML
    private ListView<Cours> coursListView;

    CoursService coursService = new CoursService();
    @FXML
    private Label courseCountLabel;

    @FXML
    public void initialize() {
        try {
            List<Cours> coursList = coursService.recupererTous();
            ObservableList<Cours> observableCours = FXCollections.observableArrayList(coursList);
            coursListView.setItems(observableCours);

            // Update the course count label
            courseCountLabel.setText("Total: " + coursList.size() + " courses");

            // Set up the cell factory
            coursListView.setCellFactory(listView -> new ListCell<Cours>() {
                @Override
                protected void updateItem(Cours cours, boolean empty) {
                    super.updateItem(cours, empty);
                    if (empty || cours == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        VBox container = new VBox(8);
                        container.getStyleClass().add("cours-cell-container");

                        Label title = new Label(cours.getName());
                        title.getStyleClass().add("cours-title");

                        Label matiere = new Label("Matière: " + cours.getIdMatiere());
                        matiere.getStyleClass().add("cours-info");

                        Label classe = new Label("Classe: " + cours.getClasse());
                        classe.getStyleClass().add("cours-info");

                        Label status = new Label("📄 PDF: " + cours.getPdfFilename());
                        status.getStyleClass().add("cours-status");

                        // Delete button
                        Button deleteButton = new Button("Delete");
                        deleteButton.getStyleClass().add("delete-button");
                        deleteButton.setOnAction(event -> {
                            // Delete the Cours from the database
                            deleteCours(cours);
                        });
// Edit button
                        Button editButton = new Button("Edit");
                        editButton.getStyleClass().add("edit-button");
                        editButton.setOnAction(event -> {
                            goToEditCours(cours);
                        });

                        HBox buttonContainer = new HBox(10);
                        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
                        buttonContainer.getChildren().addAll(deleteButton ,editButton);

                        container.getChildren().addAll(title, matiere, classe, status, buttonContainer );
                        setGraphic(container);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAddCours() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Farouk/AjouterCours.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) coursListView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Could not load add course view");
        }
    }

    // Helper method to show alerts (similar to what you likely have for Matiere)
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Method to delete a course
    private void deleteCours(Cours cours) {
        try {
            // Call the service method to delete the course from the database
            boolean deleted = coursService.supprimer(cours);

            if (deleted) {
                // Remove the Cours from the list and refresh the UI
                coursListView.getItems().remove(cours);
                // Update the course count
                courseCountLabel.setText("Total: " + coursListView.getItems().size() + " courses");
            } else {
                // Show an error message if something went wrong
                System.out.println("Failed to delete the course.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void goToEditCours(Cours cours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Farouk/EditCours.fxml"));
            Parent root = loader.load();

            // Pass the cours to edit to the controller
            EditCoursController controller = loader.getController();
            controller.setCours(cours);

            // Get the current stage and replace the scene
            Stage stage = (Stage) coursListView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Could not load edit course view");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
