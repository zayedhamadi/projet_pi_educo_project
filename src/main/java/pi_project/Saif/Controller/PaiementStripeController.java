package pi_project.Saif.Controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Service.CommandeService;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Utils.session;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class PaiementStripeController {

    @FXML private Label totalLabel;
    private List<Produit> produits;
    private double total;
    private ObservableList<Produit> panier;
    private PanierController panierController;
    private final CommandeService commandeService = new CommandeService();
    private static final String STRIPE_SECRET_KEY = "sk_test_51QsjAZQwwmpQjSTvmGQ9IvKFaSDjuZHqqytTxvT5dpHm8LYmIbD7p5xFmYd8F5WLBelSlWkv0nrNo0QfssotP2b100AZCTEd0B";
    private String sessionId;
    private ScheduledExecutorService verificationScheduler;
    @FXML private Button voirPanierBtn;
    private double montantAPayer;
    private double remisePourcent ;
User user=new User();

    public void setData(List<Produit> produits, double total, ObservableList<Produit> panier, PanierController panierController) {
        this.produits = produits;
        this.total = total;
        this.panier = panier;
        this.panierController = panierController;
        totalLabel.setText(String.format("Total: %.2f DT", total));

    }
//    public void setRemisePourcent(double remise) {
//        this.remisePourcent = remise;
//        System.out.println("Remise reçue : " + remise);  // Vérifier si la remise est reçue correctement
//    }

//    public void setRemisePourcent(double remise) {
//        this.remisePourcent = remise ;
//        System.out.println("Remise reçue : " + this.remisePourcent);
//    }
public void setRemisePourcent(double remise) {
    this.remisePourcent = remise;
    System.out.println("Remise reçue : " + this.remisePourcent);
    initStripePayment(); // Lancer Stripe seulement après avoir défini la remise
}

    private void initStripePayment() {
        try {
            // 1. Initialize Stripe
            Stripe.apiKey = STRIPE_SECRET_KEY;

            // 2. Create Stripe session
            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://example.com/success")
                    .setCancelUrl("https://example.com/cancel");

            // Add line items
//            for (Produit produit : produits) {
//                paramsBuilder.addLineItem(
//                        SessionCreateParams.LineItem.builder()
//                                .setQuantity((long) produit.getQuantite())
//                                .setPriceData(
//                                        SessionCreateParams.LineItem.PriceData.builder()
//                                                .setCurrency("eur")
//                                                .setUnitAmount((long) (produit.getPrix() * 100))
//                                                .setProductData(
//                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                                                .setName(produit.getNom())
//                                                                .build())
//                                                .build())
//                                .build());
//            }
// Appliquer la remise à chaque produit dans le panier
            for (Produit produit : produits) {
                double prixAvecRemise = produit.getPrix() * (1 - remisePourcent);

//                double prixAvecRemise = produit.getPrix() * (1 - remisePourcent); // Appliquer la remise
                System.out.println("Prix avant remise : " + produit.getPrix());
                System.out.println("Prix après remise : " + prixAvecRemise);

                paramsBuilder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) produit.getQuantite())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount((long) (prixAvecRemise * 100))  // Utiliser le prix après remise
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(produit.getNom())
                                                                .build())
                                                .build())
                                .build());
            }


            Session session = Session.create(paramsBuilder.build());
            this.sessionId = session.getId();

            // 3. Open browser for payment
            openBrowser(session.getUrl());

            // 4. Start payment verification
            startPaymentVerification();

        } catch (StripeException e) {
            showAlert("Erreur Stripe", "Impossible de créer la session de paiement");
        }
    }

    private void startPaymentVerification() {
        verificationScheduler = Executors.newSingleThreadScheduledExecutor();

        verificationScheduler.scheduleAtFixedRate(() -> {
            try {
                Session session = Session.retrieve(sessionId);

                if ("paid".equals(session.getPaymentStatus())) {
                    // Payment succeeded - create the order
                    verificationScheduler.shutdown();
                    Platform.runLater(() -> {
                        try {
                            // 1. Create order in database
                            int commandeId = createOrderInDatabase();

                            // 2. Clear cart
                            panier.clear();



                            // 3. Update UI
                            if (panierController != null) {
                                panierController.updateTotal();
                            }

                            // 4. Show success message
                            showSuccessAlert("Paiement réussi", "Votre commande a été enregistrée");

                            // 5. Close payment window
                            closePaymentWindow();
                            generateReceipt(commandeId, total);

                        } catch (Exception e) {
                            showAlert("Erreur", "Erreur lors de l'enregistrement de la commande");
                        }
                    });
                }
                else if ("expired".equals(session.getPaymentStatus()) ||
                        "canceled".equals(session.getPaymentStatus())) {
                    verificationScheduler.shutdown();
                    Platform.runLater(() -> {
                        showAlert("Paiement échoué", "Le paiement n'a pas abouti");
                    });
                }

            } catch (StripeException e) {
                verificationScheduler.shutdown();
                Platform.runLater(() -> {
                    showAlert("Erreur", "Impossible de vérifier le statut du paiement");
                });
            }
        }, 5, 5, TimeUnit.SECONDS); // Check every 5 seconds

        // Timeout after 30 minutes
        verificationScheduler.schedule(() -> {
            if (!verificationScheduler.isShutdown()) {
                verificationScheduler.shutdown();
                Platform.runLater(() -> {
                    showAlert("Timeout", "La session de paiement a expiré");
                });
            }
        }, 30, TimeUnit.MINUTES);
    }

    private int createOrderInDatabase() throws Exception {
//        int userId = 1; // Replace with actual user ID
        Integer userId = session.getUserSession();

        return commandeService.passerCommande(
                userId,
                produits,
                "Payée", // Directly set as paid
                LocalDateTime.now(),
                total,
                "Carte"
        );
    }

    private void openBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            showAlert("Erreur", "Impossible d'ouvrir le navigateur. Veuillez visiter: " + url);
        }
    }

    private void closePaymentWindow() {
        Stage stage = (Stage) totalLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }



//    private void generateReceipt(int commandeId, double total) {
//        PDDocument document = new PDDocument();
//        PDPage page = new PDPage(PDRectangle.A4);
//        document.addPage(page);
//
//        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
//            // Configuration initiale
//            float margin = 50;
//            float yPosition = page.getMediaBox().getHeight() - margin;
//            float leading = 20;
//            float smallLeading = 15;
//
////             Logo (optionnel - à remplacer par votre propre logo)
////            String logoPath = "src/main/resources/Zayed/images/educo.jpg";
////            PDImageXObject pdImage = PDImageXObject.createFromFile(logoPath, document);
//
//            // Dessiner l'image (x, y, largeur, hauteur)
////            contentStream.drawImage(pdImage, margin, yPosition - 70, 150, 60);
//
//            // En-tête de la facture
//            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
//            contentStream.beginText();
//            contentStream.newLineAtOffset(margin, yPosition);
//            contentStream.showText("FACTURE");
//            contentStream.endText();
//            yPosition -= leading * 2;
//
//            // Informations de la société
//            contentStream.setFont(PDType1Font.HELVETICA, 10);
//            contentStream.beginText();
//            contentStream.newLineAtOffset(margin, yPosition);
//            contentStream.showText("Educo School");
//            contentStream.newLineAtOffset(0, -smallLeading);
//            contentStream.showText("Adresse: 123 Rue des Entrepreneurs");
//            contentStream.newLineAtOffset(0, -smallLeading);
//            contentStream.showText("Tél: +216 12 345 678");
//            contentStream.newLineAtOffset(0, -smallLeading);
//            contentStream.showText("Email: contact@educo.com");
//            contentStream.endText();
//
//            // Informations du client
//            float clientX = page.getMediaBox().getWidth() / 2;
//            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//            contentStream.beginText();
//            contentStream.newLineAtOffset(clientX, yPosition);
//            contentStream.showText("Client:");
//            contentStream.endText();
//
//            contentStream.setFont(PDType1Font.HELVETICA, 10);
//            contentStream.beginText();
//            contentStream.newLineAtOffset(clientX, yPosition - smallLeading);
//
//            contentStream.showText("Nom: " +user.getNom() ); // Méthode à implémenter
//            contentStream.newLineAtOffset(0, -smallLeading);
//            contentStream.showText("Adresse: " + user.getAdresse()); // Méthode à implémenter
//            contentStream.endText();
//            yPosition -= leading * 3;
//
//            // Détails de la facture
//            contentStream.setFont(PDType1Font.HELVETICA, 10);
//            contentStream.beginText();
//            contentStream.newLineAtOffset(margin, yPosition);
//            contentStream.showText("Facture N°: " + commandeId);
//            contentStream.newLineAtOffset(0, -smallLeading);
//            contentStream.showText("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
//            contentStream.endText();
//            yPosition -= leading * 2;
//
//            // Ligne de séparation
//            contentStream.moveTo(margin, yPosition);
//            contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
//            contentStream.stroke();
//            yPosition -= leading;
//
//            // En-tête du tableau des produits
//            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
//            contentStream.beginText();
//            contentStream.newLineAtOffset(margin, yPosition);
//            contentStream.showText("Désignation");
//            contentStream.newLineAtOffset(200, 0);
//            contentStream.showText("Qté");
//            contentStream.newLineAtOffset(50, 0);
//            contentStream.showText("Prix U.");
//            contentStream.newLineAtOffset(80, 0);
//            contentStream.showText("Total");
//            contentStream.endText();
//            yPosition -= smallLeading;
//
//            // Ligne de séparation
//            contentStream.moveTo(margin, yPosition);
//            contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
//            contentStream.stroke();
//            yPosition -= smallLeading;
//
//            // Détails des produits
//            contentStream.setFont(PDType1Font.HELVETICA, 10);
//            for (Produit produit : produits) {
//                contentStream.beginText();
//                contentStream.newLineAtOffset(margin, yPosition);
//                contentStream.showText(produit.getNom());
//                contentStream.newLineAtOffset(200, 0);
//                contentStream.showText(String.valueOf(produit.getQuantite()));
//                contentStream.newLineAtOffset(50, 0);
//                contentStream.showText(String.format("%.2f DT", produit.getPrix()));
//                contentStream.newLineAtOffset(80, 0);
//                contentStream.showText(String.format("%.2f DT", produit.getPrix() * produit.getQuantite()));
//                contentStream.endText();
//                yPosition -= leading;
//            }
//
//            // Ligne de séparation
//            contentStream.moveTo(margin, yPosition);
//            contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
//            contentStream.stroke();
//            yPosition -= leading;
//
//            // Total
//            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//            contentStream.beginText();
//            contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, yPosition);
//            contentStream.showText("TOTAL:");
//            contentStream.newLineAtOffset(50, 0);
//            contentStream.showText(String.format("%.2f DT", total));
//            contentStream.endText();
//            yPosition -= leading * 2;
//
//            // Mentions légales
//            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
//            contentStream.beginText();
//            contentStream.newLineAtOffset(margin, yPosition);
//            contentStream.showText("TVA non applicable, article 293 B du CGI");
//            contentStream.newLineAtOffset(0, -smallLeading);
//            contentStream.showText("Paiement à réception de la facture");
//            contentStream.endText();
//
//        } catch (IOException e) {
//            showAlert("Erreur", "Erreur lors de la génération du PDF: " + e.getMessage());
//            return;
//        }
//
//        // Sauvegarde du fichier
//        try {
//            // Créer un sélecteur de fichier
//            JFileChooser fileChooser = new JFileChooser();
//            fileChooser.setDialogTitle("Enregistrer la facture");
//
//            // Définir le nom de fichier par défaut
//            String defaultFileName = "Facture_" + commandeId + "_" +
//                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
//            fileChooser.setSelectedFile(new File(defaultFileName));
//
//            // Filtrer pour n'afficher que les PDF
//            fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers PDF (*.pdf)", "pdf"));
//
//            // Afficher la boîte de dialogue d'enregistrement
//            int userChoice = fileChooser.showSaveDialog(null);
//
//            if (userChoice == JFileChooser.APPROVE_OPTION) {
//                File fileToSave = fileChooser.getSelectedFile();
//
//                // S'assurer que l'extension est .pdf
//                if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
//                    fileToSave = new File(fileToSave.getPath() + ".pdf");
//                }
//
//                document.save(fileToSave);
//                document.close();
//
//                openReceipt(fileToSave.getAbsolutePath());
//                showSuccessAlert("Facture générée",
//                        "La facture a été sauvegardée sous:\n" + fileToSave.getAbsolutePath());
//            } else {
//                showAlert("Annulé", "L'enregistrement de la facture a été annulé.");
//            }
//        } catch (IOException e) {
//            showAlert("Erreur", "Erreur lors de la sauvegarde du PDF:\n" + e.getMessage());
//        }
//    }

    private void generateReceipt(int commandeId, double total) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Initial setup
            float margin = 50;
            float yPosition = page.getMediaBox().getHeight() - margin;
            float leading = 20;
            float smallLeading = 15;

            // Title
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("FACTURE");
            contentStream.endText();
            yPosition -= leading * 2;

            // Company Information
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Educo School");
            contentStream.newLineAtOffset(0, -smallLeading);
            contentStream.showText("Adresse: 123 Rue des Entrepreneurs");
            contentStream.newLineAtOffset(0, -smallLeading);
            contentStream.showText("Tél: +216 12 345 678");
            contentStream.newLineAtOffset(0, -smallLeading);
            contentStream.showText("Email: contact@educo.com");
            contentStream.endText();

            // Customer Information
            float clientX = page.getMediaBox().getWidth() / 2;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(clientX, yPosition);
            contentStream.showText("Client:");
            contentStream.endText();

            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(clientX, yPosition - smallLeading);
            contentStream.showText("Nom: " + user.getNom());
            contentStream.newLineAtOffset(0, -smallLeading);
            contentStream.showText("Adresse: " + user.getAdresse());
            contentStream.endText();
            yPosition -= leading * 3;

            // Invoice Details
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Facture N°: " + commandeId);
            contentStream.newLineAtOffset(0, -smallLeading);
            contentStream.showText("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            contentStream.endText();
            yPosition -= leading * 2;

            // Separation Line
            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
            contentStream.stroke();
            yPosition -= leading;

            // Table Header
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Désignation");
            contentStream.newLineAtOffset(200, 0);
            contentStream.showText("Qté");
            contentStream.newLineAtOffset(50, 0);
            contentStream.showText("Prix U.");
            contentStream.newLineAtOffset(80, 0);
            contentStream.showText("Total");
            contentStream.endText();
            yPosition -= smallLeading;

            // Separation Line
            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
            contentStream.stroke();
            yPosition -= smallLeading;

            // Product Details
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            for (Produit produit : produits) {
                double prixAvecRemise = produit.getPrix() * (1 - remisePourcent);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(produit.getNom());
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText(String.valueOf(produit.getQuantite()));
                contentStream.newLineAtOffset(50, 0);
                contentStream.showText(String.format("%.2f DT", produit.getPrix()));
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText(String.format("%.2f DT", produit.getPrix() * produit.getQuantite()));
                contentStream.endText();
                yPosition -= leading;

                // Discount Line
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 200, yPosition);
                contentStream.showText("Remise: " + String.format("%.2f DT", produit.getPrix() * produit.getQuantite() - prixAvecRemise * produit.getQuantite()));
                contentStream.endText();
                yPosition -= smallLeading;
            }

            // Separation Line
            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
            contentStream.stroke();
            yPosition -= leading;

            // Total Calculation
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, yPosition);
            contentStream.showText("TOTAL:");
            contentStream.newLineAtOffset(50, 0);
            contentStream.showText(String.format("%.2f DT", total));
            contentStream.endText();
            yPosition -= leading * 2;

            // Legal Notes
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("TVA non applicable, article 293 B du CGI");
            contentStream.newLineAtOffset(0, -smallLeading);
            contentStream.showText("Paiement à réception de la facture");
            contentStream.endText();

        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la génération du PDF: " + e.getMessage());
            return;
        }

        // Save the file
        try {
            // Create a file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enregistrer la facture");

            // Default file name
            String defaultFileName = "Facture_" + commandeId + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            fileChooser.setSelectedFile(new File(defaultFileName));

            // Filter to show only PDFs
            fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers PDF (*.pdf)", "pdf"));

            // Show save dialog
            int userChoice = fileChooser.showSaveDialog(null);

            if (userChoice == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // Ensure the file extension is .pdf
                if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                    fileToSave = new File(fileToSave.getPath() + ".pdf");
                }

                document.save(fileToSave);
                document.close();

                openReceipt(fileToSave.getAbsolutePath());
                showSuccessAlert("Facture générée", "La facture a été sauvegardée sous:\n" + fileToSave.getAbsolutePath());
            } else {
                showAlert("Annulé", "L'enregistrement de la facture a été annulé.");
            }
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la sauvegarde du PDF:\n" + e.getMessage());
        }
    }

    private void openReceipt(String receiptPath) {
        try {
            Desktop.getDesktop().open(new java.io.File(receiptPath));  // Automatically open the generated PDF
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la facture.");
        }
    }



}