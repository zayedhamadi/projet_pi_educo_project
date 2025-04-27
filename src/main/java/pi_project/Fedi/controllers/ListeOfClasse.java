package pi_project.Fedi.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.services.classeservice;
import pi_project.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import javafx.scene.chart.PieChart;
import java.util.List;
import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.services.eleveservice;

public class ListeOfClasse implements Initializable {

    private final classeservice service = new classeservice();
    private final eleveservice eleveService = new eleveservice();
    private final ObservableList<classe> data = FXCollections.observableArrayList();
    @FXML
    private BorderPane rootPane;
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField searchField;
    @FXML
    private VBox chartContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger les données
        data.addAll(service.getAll());
        afficherCartes();
        
        // Ajouter le listener pour la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            rechercherClasses(newValue);
        });
    }

    private void rechercherClasses(String recherche) {
        if (recherche == null || recherche.trim().isEmpty()) {
            afficherCartes();
            return;
        }

        recherche = recherche.toLowerCase().trim();
        ObservableList<classe> resultats = FXCollections.observableArrayList();

        for (classe c : data) {
            if (c.getNomclasse().toLowerCase().contains(recherche) ||
                String.valueOf(c.getNumsalle()).contains(recherche) ||
                String.valueOf(c.getCapacite()).contains(recherche)) {
                resultats.add(c);
            }
        }

        afficherCartes(resultats);
    }

    private void afficherCartes() {
        afficherCartes(data);
    }

    private void afficherCartes(ObservableList<classe> classes) {
        gridPane.getChildren().clear();
        int column = 0;
        int row = 0;
        int maxColumns = 3;

        for (classe c : classes) {
            VBox card = createClassCard(c);
            gridPane.add(card, column, row);

            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createClassCard(classe c) {
        VBox card = new VBox(10);
        card.getStyleClass().add("class-card");

        // Titre de la classe
        Text title = new Text(c.getNomclasse());
        title.getStyleClass().add("class-card-title");

        // Informations de la classe
        Text salleInfo = new Text("Salle: " + c.getNumsalle());
        Text capaciteInfo = new Text("Capacité: " + c.getCapacite());
        salleInfo.getStyleClass().add("class-card-info");
        capaciteInfo.getStyleClass().add("class-card-info");

        // Boutons d'action
        HBox actions = new HBox(10);
        actions.getStyleClass().add("class-card-actions");

        Button btnUpdate = new Button("Modifier");
        Button btnDelete = new Button("Supprimer");
        btnUpdate.getStyleClass().add("class-card-button");
        btnDelete.getStyleClass().add("class-card-button");

        btnUpdate.setOnAction(event -> {
            try {
                Stage currentStage = (Stage) btnUpdate.getScene().getWindow();
                ouvrirSceneUpdate(c,currentStage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        btnDelete.setOnAction(event -> {
            // Vérifier s'il y a des élèves dans la classe
            List<eleve> elevesClasse = eleveService.getByClasse(c.getId());
            
            if (!elevesClasse.isEmpty()) {
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Attention");
                warning.setHeaderText("Cette classe contient " + elevesClasse.size() + " élève(s)");
                warning.setContentText("Vous devez d'abord supprimer ou réaffecter tous les élèves avant de pouvoir supprimer la classe.");
                
                ButtonType btnSupprimer = new ButtonType("Supprimer les élèves");
                ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
                
                warning.getButtonTypes().setAll(btnSupprimer, btnAnnuler);
                
                warning.showAndWait().ifPresent(response -> {
                    if (response == btnSupprimer) {
                        try {
                            // Supprimer tous les élèves de la classe
                            for (eleve e : elevesClasse) {
                                eleveService.delete(e.getId());
                            }
                            
                            // Puis supprimer la classe
                            service.delete(c.getId());
                            data.remove(c);
                            afficherCartes();
                            handleShowStats();
                            
                            showAlert("Succès", "La classe et ses élèves ont été supprimés avec succès.");
                        } catch (Exception e) {
                            showErreur("Erreur lors de la suppression: " + e.getMessage());
                        }
                    }
                });
            } else {
                // Si la classe n'a pas d'élèves, procéder à la suppression normale
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation");
                confirmation.setHeaderText("Voulez-vous vraiment supprimer cette classe ?");
                confirmation.setContentText(c.getNomclasse());

                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            service.delete(c.getId());
                            data.remove(c);
                            afficherCartes();
                            handleShowStats();
                            showAlert("Succès", "La classe a été supprimée avec succès.");
                        } catch (Exception e) {
                            showErreur("Erreur lors de la suppression: " + e.getMessage());
                        }
                    }
                });
            }
        });

        actions.getChildren().addAll(btnUpdate, btnDelete);
        card.getChildren().addAll(title, salleInfo, capaciteInfo, actions);

        return card;
    }

    @FXML
    private void handleAjouterClasse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/AjouterClasse.fxml"));
            Parent listeView = loader.load();

            Scene currentScene = rootPane.getScene();
            currentScene.setRoot(listeView);
        } catch (Exception e) {
            System.out.println(("Erreur lors du retour à la liste"));
            e.printStackTrace();
        }
    }
//
//    @FXML
//    private void handleAjouterEleve() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/AjouterEleve.fxml"));
//            Parent listeView = loader.load();
//
//            Scene currentScene = rootPane.getScene();
//            currentScene.setRoot(listeView);
//        } catch (Exception e) {
//            System.out.println(("Erreur lors du retour à la liste"));
//            e.printStackTrace();
//        }
//    }

    private void ouvrirSceneUpdate(classe selected, Stage stage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/UpdateClasse.fxml"));
            Parent root = loader.load();

            UpdateClasse controller = loader.getController();
            controller.setClasse(selected);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErreur("Erreur lors du chargement de la scène UpdateClasse.");
        }
    }

    private void showErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleExportPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter en PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());

            if (file != null) {
                PdfWriter writer = new PdfWriter(new FileOutputStream(file));
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Style personnalisé
                Color headerColor = new DeviceRgb(41, 128, 185);
                Color textColor = new DeviceRgb(44, 62, 80);
                Color tableHeaderColor = new DeviceRgb(52, 152, 219);

                // En-tête
                Paragraph header = new Paragraph("Rapport des Classes")
                        .setFontSize(24)
                        .setBold()
                        .setFontColor(headerColor)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20);
                document.add(header);

                // Date de génération
                Paragraph date = new Paragraph("Généré le: " + java.time.LocalDate.now())
                        .setFontSize(12)
                        .setFontColor(textColor)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMarginBottom(20);
                document.add(date);

                // Statistiques
                Paragraph stats = new Paragraph("Statistiques")
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(headerColor)
                        .setMarginBottom(10);
                document.add(stats);

                // Tableau des statistiques
                Table statsTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
                statsTable.addCell(createCell("Nombre total de classes", true));
                statsTable.addCell(createCell(String.valueOf(data.size()), false));
                statsTable.addCell(createCell("Capacité totale", true));
                statsTable.addCell(createCell(String.valueOf(data.stream().mapToInt(classe::getCapacite).sum()), false));
                document.add(statsTable);
                document.add(new Paragraph("\n"));

                // Liste des classes
                Paragraph classesTitle = new Paragraph("Liste détaillée des classes")
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(headerColor)
                        .setMarginBottom(10);
                document.add(classesTitle);

                // Tableau principal
                Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
                
                // En-tête du tableau
                table.addHeaderCell(createHeaderCell("Nom de la classe"));
                table.addHeaderCell(createHeaderCell("Numéro de salle"));
                table.addHeaderCell(createHeaderCell("Capacité"));
                table.addHeaderCell(createHeaderCell("ID"));

                // Données
                for (classe c : data) {
                    table.addCell(createCell(c.getNomclasse(), false));
                    table.addCell(createCell(String.valueOf(c.getNumsalle()), false));
                    table.addCell(createCell(String.valueOf(c.getCapacite()), false));
                    table.addCell(createCell(String.valueOf(c.getId()), false));
                }

                document.add(table);

                // Pied de page
                Paragraph footer = new Paragraph("© 2024 Educo - Tous droits réservés")
                        .setFontSize(10)
                        .setFontColor(textColor)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(20);
                document.add(footer);

                document.close();

                showAlert("Succès", "Le fichier PDF a été exporté avec succès !");
            }
        } catch (IOException e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'exportation du PDF.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowStats() {
        // Créer le PieChart
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Répartition des Élèves par Niveau");

        // Récupérer toutes les classes
        List<classe> classes = service.getAll();
        
        // Map pour stocker le nombre d'élèves par niveau
        java.util.Map<String, Integer> elevesParNiveau = new java.util.HashMap<>();
        
        // Pour chaque classe, extraire le niveau et compter les élèves
        for (classe c : classes) {
            // Extraire le niveau du nom de la classe (supposons que le format est "6ème A", "5ème B", etc.)
            String niveau = extraireNiveau(c.getNomclasse());
            
            // Obtenir le nombre d'élèves dans cette classe
            List<eleve> elevesClasse = eleveService.getByClasse(c.getId());
            
            // Ajouter au compteur du niveau
            elevesParNiveau.merge(niveau, elevesClasse.size(), Integer::sum);
        }
        
        // Ajouter les données au PieChart
        for (java.util.Map.Entry<String, Integer> entry : elevesParNiveau.entrySet()) {
            String niveau = entry.getKey();
            int nombreEleves = entry.getValue();
            
            PieChart.Data slice = new PieChart.Data(
                niveau + " (" + nombreEleves + " élèves)", 
                nombreEleves
            );
            pieChart.getData().add(slice);
        }

        // Calculer les pourcentages
        int totalEleves = pieChart.getData().stream()
                .mapToInt(data -> (int) data.getPieValue())
                .sum();

        for (PieChart.Data data : pieChart.getData()) {
            double percentage = (data.getPieValue() / totalEleves) * 100;
            
            // Ajouter un événement de clic pour afficher le pourcentage
            data.getNode().setOnMouseClicked(event -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Statistiques");
                alert.setHeaderText(data.getName());
                alert.setContentText(String.format("Pourcentage: %.1f%%", percentage));
                alert.showAndWait();
            });
        }

        // Vider le conteneur et ajouter le nouveau graphique
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(pieChart);
        
        // Ajuster la taille du graphique
        pieChart.setPrefSize(400, 400);
    }

    private String extraireNiveau(String nomClasse) {
        // Supposons que le format est "6ème A", "5ème B", etc.
        // On extrait juste la partie numérique avec "ème"
        if (nomClasse == null || nomClasse.isEmpty()) {
            return "Non défini";
        }
        
        // Chercher le motif "Xème" où X est un chiffre
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)[èe]me");
        java.util.regex.Matcher matcher = pattern.matcher(nomClasse.toLowerCase());
        
        if (matcher.find()) {
            return matcher.group(1) + "ème";
        }
        
        // Si le format ne correspond pas, retourner le nom complet
        return nomClasse;
    }

    private com.itextpdf.layout.element.Cell createHeaderCell(String text) {
        return new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(text))
                .setBackgroundColor(new DeviceRgb(52, 152, 219))
                .setFontColor(new DeviceRgb(255, 255, 255))
                .setBold()
                .setPadding(8);
    }

    private com.itextpdf.layout.element.Cell createCell(String text, boolean isHeader) {
        com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(text))
                .setPadding(8);
        
        if (isHeader) {
            cell.setBackgroundColor(new DeviceRgb(236, 240, 241))
                .setBold();
        }
        
        return cell;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}