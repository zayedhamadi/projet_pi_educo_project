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
                            showEditDialog(cours);
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

    private void showEditDialog(Cours cours) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Course");
        dialog.setHeaderText("Modify course details");

        // Set up the fields with current data
        TextField nameField = new TextField(cours.getName());
        TextField matiereField = new TextField(String.valueOf(cours.getIdMatiere()));
        TextField classeField = new TextField(String.valueOf(cours.getClasse()));

        Label pdfLabel = new Label(cours.getPdfFilename() != null ? cours.getPdfFilename() : "No file selected");
        Button browseButton = new Button("Browse");
        HBox pdfBox = new HBox(10, pdfLabel, browseButton);
        // FileChooser to pick the PDF
        final String[] selectedPdfPath = {cours.getPdfFilename()}; // store as array to allow modification in lambda
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        browseButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                selectedPdfPath[0] = file.getAbsolutePath(); // Save selected path
                pdfLabel.setText(file.getName()); // Show only filename
            }
        });

        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Matière ID:"), matiereField,
                new Label("Classe:"), classeField,
                new Label("PDF File:"), pdfBox
        );
        dialog.getDialogPane().setContent(content);

        // Add OK / Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Update the course object
                    cours.setName(nameField.getText());
                    cours.setIdMatiere(Integer.parseInt(matiereField.getText()));
                    cours.setClasse(Integer.parseInt(classeField.getText()));
                    cours.setPdfFilename(selectedPdfPath[0]);

                    // Save changes to the database
                    boolean updated = coursService.modifier(cours);
                    if (updated) {
                        // Force refresh
                        coursListView.refresh();
                    } else {
                        System.out.println("Failed to update course.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }
//    private void showEditPage(Cours cours) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Farouk/EditCours.fxml"));
//            Parent root = loader.load();
//
//            EditCoursController controller = loader.getController();
//            controller.setCoursData(cours);
//
//            Stage stage = new Stage();
//            stage.setTitle("Edit Course");
//            stage.setScene(new Scene(root));
//            stage.showAndWait();
//
//            // Refresh list after editing
//            refreshCourseList();
//        } catch (IOException e) {
//            showAlert("Error", "Could not load edit view", Alert.AlertType.ERROR);
//        }
//    }

}
