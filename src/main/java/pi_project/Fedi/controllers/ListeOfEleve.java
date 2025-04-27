package pi_project.Fedi.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.services.eleveservice;
import pi_project.Fedi.services.EmailService;
import pi_project.Zayed.Entity.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeOfEleve implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private GridPane gridPane;
    @FXML private VBox notificationContainer;

    private final eleveservice eleveService = new eleveservice();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Créer le conteneur de notifications s'il n'existe pas
        if (notificationContainer == null) {
            notificationContainer = new VBox(10);
            notificationContainer.setAlignment(Pos.TOP_RIGHT);
            notificationContainer.setPadding(new Insets(20));
            notificationContainer.setMaxWidth(300);
            notificationContainer.setMouseTransparent(false);
            
            // Ajouter le conteneur de notifications au BorderPane (coin supérieur droit)
            BorderPane.setAlignment(notificationContainer, Pos.TOP_RIGHT);
            BorderPane.setMargin(notificationContainer, new Insets(10));
            rootPane.setRight(notificationContainer);
        }
        
        chargerEleves();
    }

    private void chargerEleves() {
        gridPane.getChildren().clear();

        List<eleve> eleves = eleveService.getAll();
        int column = 0;
        int row = 0;

        for (eleve e : eleves) {
            VBox card = creerCarteEleve(e);
            gridPane.add(card, column, row);

            column++;
            if (column == 3) { // 3 cartes par ligne
                column = 0;
                row++;
            }
        }
    }

    private void afficherNotification(eleve e, String message, String niveau) {
        Platform.runLater(() -> {
            // Créer la carte de notification
            VBox notification = new VBox(5);
            notification.getStyleClass().add("notification-card");
            notification.setStyle("-fx-background-color: #FF4444; -fx-background-radius: 5; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
            
            // En-tête de la notification
            Label titreLabel = new Label("Élève en difficulté");
            titreLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
            
            // Contenu de la notification
            Label nomLabel = new Label(e.getNom() + " " + e.getPrenom());
            nomLabel.setStyle("-fx-text-fill: white;");
            
            Label moyenneLabel = new Label(String.format("Moyenne: %.2f", e.getMoyenne()));
            moyenneLabel.setStyle("-fx-text-fill: white;");
            
            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-text-fill: white;");
            messageLabel.setWrapText(true);
            
            // Boutons
            HBox boutons = new HBox(10);
            boutons.setAlignment(Pos.CENTER_RIGHT);
            
            Button btnContacter = new Button("Contacter parent");
            btnContacter.setStyle("-fx-background-color: white; -fx-text-fill: #FF4444;");
            
            Button btnFermer = new Button("Fermer");
            btnFermer.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white;");
            
            boutons.getChildren().addAll(btnContacter, btnFermer);
            
            // Ajouter tous les éléments à la notification
            notification.getChildren().addAll(
                titreLabel,
                nomLabel,
                moyenneLabel,
                messageLabel,
                boutons
            );
            
            // Ajouter la notification au conteneur
            notificationContainer.getChildren().add(0, notification);
            
            // Animation d'entrée
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), notification);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            
            // Action des boutons
            btnContacter.setOnAction(event -> {
                try {
                    EmailService emailService = new EmailService();
                    User parent = e.getParent();
                    
                    // Vérification des informations du parent
                    if (parent == null) {
                        throw new IllegalStateException("Les informations du parent ne sont pas disponibles");
                    }
                    
                    String parentEmail = parent.getEmail();
                    String parentNom = parent.getNom();
                    String parentPrenom = parent.getPrenom();
                    
                    // Vérification de l'email
                    if (parentEmail == null || parentEmail.trim().isEmpty()) {
                        throw new IllegalStateException("L'adresse email du parent n'est pas disponible");
                    }
                    
                    // Vérification du nom et prénom
                    if (parentNom == null || parentNom.trim().isEmpty()) {
                        parentNom = "Parent";
                    }
                    if (parentPrenom == null || parentPrenom.trim().isEmpty()) {
                        parentPrenom = "";
                    }
                    
                    String nomCompletParent = (parentNom + " " + parentPrenom).trim();
                    String classeNom = e.getClasse() != null ? e.getClasse().getNomclasse() : "Non assigné";
                    
                    String messageEmail = String.format(
                        "Nous avons remarqué que votre enfant rencontre quelques difficultés dans ses études. " +
                        "Sa moyenne actuelle est de %.2f/20, ce qui nécessite une attention particulière. " +
                        "%s",
                        e.getMoyenne(),
                        message
                    );
                    
                    emailService.sendStudentPerformanceEmail(
                        parentEmail,
                        nomCompletParent,
                        e.getNom() + " " + e.getPrenom(),
                        e.getMoyenne(),
                        e.getNbreAbsence(),
                        classeNom,
                        messageEmail
                    );
                    
                    // Afficher une confirmation
                    Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
                    confirmation.setTitle("Email envoyé");
                    confirmation.setHeaderText(null);
                    confirmation.setContentText("Un email a été envoyé au parent à l'adresse : " + parentEmail);
                    confirmation.show();
                    
                    // Supprimer automatiquement la notification après l'envoi de l'email
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notification);
                    fadeOut.setFromValue(1);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(e2 -> notificationContainer.getChildren().remove(notification));
                    fadeOut.play();
                    
                } catch (IllegalStateException ex) {
                    Alert erreur = new Alert(Alert.AlertType.ERROR);
                    erreur.setTitle("Erreur");
                    erreur.setHeaderText("Impossible d'envoyer l'email");
                    erreur.setContentText(ex.getMessage());
                    erreur.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert erreur = new Alert(Alert.AlertType.ERROR);
                    erreur.setTitle("Erreur");
                    erreur.setHeaderText("Erreur lors de l'envoi de l'email");
                    erreur.setContentText("Une erreur s'est produite lors de l'envoi de l'email : " + ex.getMessage());
                    erreur.show();
                }
            });
            
            btnFermer.setOnAction(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notification);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e2 -> notificationContainer.getChildren().remove(notification));
                fadeOut.play();
            });
        });
    }

    private void afficherQRCodeEnGrand(eleve e) {
        try {
            // Créer une nouvelle fenêtre
            Stage qrStage = new Stage();
            qrStage.initModality(Modality.APPLICATION_MODAL);
            qrStage.initStyle(StageStyle.UTILITY);
            qrStage.setTitle("QR Code - " + e.getNom() + " " + e.getPrenom());

            // Générer le contenu du QR code
            String qrContent = String.format(
                "Élève: %s %s\nID: %d\nClasse: %s\nMoyenne: %.2f",
                e.getNom(),
                e.getPrenom(),
                e.getId(),
                e.getClasse() != null ? e.getClasse().getNomclasse() : "Non assigné",
                e.getMoyenne()
            );

            // Générer le QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 400, 400);

            // Convertir en image JavaFX
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            Image qrImage = new Image(inputStream);

            // Créer l'ImageView pour le QR code
            ImageView qrImageView = new ImageView(qrImage);
            qrImageView.setFitWidth(300);
            qrImageView.setFitHeight(300);
            qrImageView.setPreserveRatio(true);

            // Créer le conteneur
            VBox container = new VBox(20);
            container.setAlignment(Pos.CENTER);
            container.setPadding(new Insets(20));
            container.setStyle("-fx-background-color: white;");

            // Ajouter le QR code et les informations
            Label infoLabel = new Label("Scanner pour voir les informations de l'élève");
            infoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            container.getChildren().addAll(qrImageView, infoLabel);

            // Configurer la scène
            Scene scene = new Scene(container);
            qrStage.setScene(scene);
            qrStage.show();

        } catch (WriterException | IOException ex) {
            System.err.println("Erreur lors de la génération du QR code: " + ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la génération du QR code");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private VBox creerCarteEleve(eleve e) {
        VBox card = new VBox(10);
        card.getStyleClass().add("eleve-card");
        card.setPadding(new Insets(15));

        // Créer un HBox pour le titre et les indicateurs
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label nomLabel = new Label(e.getNom() + " " + e.getPrenom());
        nomLabel.getStyleClass().add("eleve-card-title");

        // Créer le conteneur pour les indicateurs
        HBox indicateursBox = new HBox(5);
        indicateursBox.setAlignment(Pos.CENTER_LEFT);

        // Indicateur principal (moyenne actuelle)
        Circle indicateurPrincipal = new Circle(8);
        indicateurPrincipal.setStroke(Color.BLACK);
        indicateurPrincipal.setStrokeWidth(1);
        
        // Indicateur de tendance (plus petit)
        Circle indicateurTendance = new Circle(5);
        indicateurTendance.setStroke(Color.BLACK);
        indicateurTendance.setStrokeWidth(1);
        
        // Définir les couleurs en fonction de la moyenne
        double moyenne = e.getMoyenne();
        String message;
        Color couleurPrincipale;
        Color couleurTendance;
        
        // Calcul de la couleur principale avec dégradé
        if (moyenne >= 15) {
            couleurPrincipale = Color.rgb(0, 255, 0); // Vert vif
            message = "Excellent";
        } else if (moyenne >= 12) {
            // Dégradé du vert au jaune
            double ratio = (moyenne - 12) / (15 - 12);
            couleurPrincipale = Color.rgb(
                (int) (255 * (1 - ratio)),  // Rouge
                255,                        // Vert
                0                          // Bleu
            );
            message = "Bien";
        } else if (moyenne >= 10) {
            // Dégradé du jaune à l'orange
            double ratio = (moyenne - 10) / (12 - 10);
            couleurPrincipale = Color.rgb(
                255,                       // Rouge
                (int) (255 * ratio),      // Vert
                0                         // Bleu
            );
            message = "Moyen";
        } else {
            // Dégradé de l'orange au rouge
            double ratio = Math.max(0, moyenne / 10);
            couleurPrincipale = Color.rgb(
                255,                      // Rouge
                (int) (128 * ratio),     // Vert
                0                        // Bleu
            );
            message = "À risque";
        }

        // Prédiction de tendance basée sur la moyenne
        double tendancePredite = calculerTendance(e);
        if (tendancePredite > moyenne + 0.5) {
            couleurTendance = Color.rgb(0, 255, 0, 0.7); // Vert transparent (tendance positive)
            message += "\nTendance : ↗ En amélioration";
        } else if (tendancePredite < moyenne - 0.5) {
            couleurTendance = Color.rgb(255, 0, 0, 0.7); // Rouge transparent (tendance négative)
            message += "\nTendance : ↘ En baisse";
        } else {
            couleurTendance = Color.rgb(255, 255, 0, 0.7); // Jaune transparent (stable)
            message += "\nTendance : → Stable";
        }

        // Appliquer les couleurs
        indicateurPrincipal.setFill(couleurPrincipale);
        indicateurTendance.setFill(couleurTendance);

        // Ajouter les indicateurs au conteneur
        indicateursBox.getChildren().addAll(indicateurPrincipal, indicateurTendance);

        // Tooltip avec informations détaillées
        String tooltipText = String.format("%s\nMoyenne: %.2f/20", message, moyenne);
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(indicateursBox, tooltip);

        headerBox.getChildren().addAll(indicateursBox, nomLabel);

        // Affichage de la classe avec gestion du null
        String classeNom = e.getClasse() != null ? e.getClasse().getNomclasse() : "Non assigné";
        Label classeLabel = new Label("Classe: " + classeNom);
        Label moyenneLabel = new Label(String.format("Moyenne: %.2f", moyenne));

        classeLabel.getStyleClass().add("eleve-card-info");
        moyenneLabel.getStyleClass().add("eleve-card-info");

        // Boutons d'action
        HBox actions = new HBox(10);
        actions.getStyleClass().add("eleve-card-actions");

        Button btnUpdate = new Button("Modifier");
        Button btnDelete = new Button("Supprimer");
        Button btnQRCode = new Button("QR Code");

        // Style du bouton QR Code
        btnQRCode.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnQRCode.setOnAction(event -> afficherQRCodeEnGrand(e));

        btnUpdate.getStyleClass().add("eleve-card-button");
        btnDelete.getStyleClass().add("eleve-card-button");
        btnQRCode.getStyleClass().add("eleve-card-button");

        // Action modifier
        btnUpdate.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/UpdateEleve.fxml"));
                Parent root = loader.load();
                UpdateEleve controller = loader.getController();
                controller.setEleve(e);

                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                ex.printStackTrace();
                showErreur("Erreur lors du chargement de la page de modification.");
            }
        });

        // Action supprimer
        btnDelete.setOnAction(event -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Voulez-vous vraiment supprimer cet élève ?");
            confirmation.setContentText(e.getNom() + " " + e.getPrenom());

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    eleveService.delete(e.getId());
                    chargerEleves();
                }
            });
        });

        actions.getChildren().addAll(btnUpdate, btnDelete, btnQRCode);
        card.getChildren().addAll(headerBox, classeLabel, moyenneLabel, actions);

        // Si l'élève est en difficulté, afficher une notification
        if (moyenne < 10) {
            String messageNotif = String.format(
                "Moyenne actuelle: %.2f/20\n%s",
                moyenne,
                "Une intervention pédagogique pourrait être nécessaire."
            );
            afficherNotification(e, messageNotif, "danger");
        }

        return card;
    }

    // Méthode pour calculer la tendance de l'élève
    private double calculerTendance(eleve e) {
        // TODO: Implémenter la logique de calcul de tendance basée sur l'historique
        // Pour l'instant, on utilise une simulation simple
        double moyenne = e.getMoyenne();
        double variation = Math.sin(moyenne * 0.5) * 1.5; // Simulation d'une variation
        return moyenne + variation;
    }

    private void showErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Erreur");
        alert.setContentText(message);

        alert.showAndWait();
    }

    @FXML
    private void handleAjouterEleve() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/AjouterEleve.fxml"));
            Parent listeView = loader.load();

            Scene currentScene = rootPane.getScene();
            currentScene.setRoot(listeView);
        } catch (Exception e) {
            System.out.println(("Erreur lors du retour à la liste"));
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Fedi/AjouterEleve.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de retourner au menu");
            alert.setContentText("Une erreur s'est produite lors du chargement de la page.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleExportExcel() {
        try {
            // Créer un nouveau classeur Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Liste des Élèves");

            // Créer le style pour l'en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Créer les en-têtes
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nom", "Prénom", "Classe", "Moyenne", "Nombre d'absences"};
            
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Remplir les données
            List<eleve> eleves = eleveService.getAll();
            int rowNum = 1;
            
            for (eleve e : eleves) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(e.getId());
                row.createCell(1).setCellValue(e.getNom());
                row.createCell(2).setCellValue(e.getPrenom());
                row.createCell(3).setCellValue(e.getClasse() != null ? e.getClasse().getNomclasse() : "Non assigné");
                row.createCell(4).setCellValue(e.getMoyenne());
                row.createCell(5).setCellValue(e.getNbreAbsence());
            }

            // Ajuster la largeur des colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Demander à l'utilisateur où sauvegarder le fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le fichier Excel");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx")
            );
            fileChooser.setInitialFileName("liste_eleves.xlsx");
            
            File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
            
            if (file != null) {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                    
                    // Afficher une confirmation
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Export réussi");
                    alert.setHeaderText(null);
                    alert.setContentText("Le fichier Excel a été exporté avec succès !");
                    alert.showAndWait();
                }
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'export Excel");
            alert.setContentText("Une erreur s'est produite lors de l'export du fichier Excel : " + e.getMessage());
            alert.showAndWait();
        }
    }
}