
package pi_project.Saif.Controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import pi_project.Saif.Entity.CodePromo;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Service.CodePromoService;
import pi_project.Saif.Service.CommandeService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class PanierController {

    @FXML private TableView<Produit> cartTable;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, Double> colPrix;
    @FXML private TableColumn<Produit, Integer> colQuantite;
    @FXML private TableColumn<Produit, Double> colTotal;
    @FXML private TableColumn<Produit, Void> colAction;
    @FXML private Label totalLabel;
    @FXML private TextField codePromoField;
    @FXML private Button appliquerCodePromoBtn;

    private double remisePourcent = 0.0;  // Par exemple 10% = 0.10
    private final CodePromoService codePromoService = new CodePromoService();

    private ObservableList<Produit> panier;

    @FXML
    public void initialize() {
//        appliquerCodePromoBtn.setOnAction(event -> {
//            String code = codePromoField.getText().trim();
//
//            // Exemple simple de codes promos en dur
//            if (code.equalsIgnoreCase("WELCOME10")) {
//                remisePourcent = 0.10;
//                showAlert("Code promo appliqué : -10%");
//            } else if (code.equalsIgnoreCase("FREESHIP")) {
//                remisePourcent = 0.05;
//                showAlert("Code promo appliqué : -5%");
//            } else {
//                remisePourcent = 0.0;
//                showAlert( "Ce code promo n'est pas reconnu.");
//            }
//
//            updateTotal();  // Mettre à jour le total avec la remise
//        });
        appliquerCodePromoBtn.setOnAction(event -> {
            String code = codePromoField.getText().trim();

            // Vérification du code promo via la méthode 'verifierCodePromo'
            CodePromo promo = codePromoService.getCodePromo(code);  // Récupère l'objet CodePromo

            if (promo != null) {
                LocalDate today = LocalDate.now();
                if (!today.isBefore(promo.getDateDebut()) && !today.isAfter(promo.getDateFin())) {
                    remisePourcent = promo.getRemisePourcent();
                    showAlert("Code promo appliqué : -" + (int)(remisePourcent * 100) + "%");
                } else {
                    remisePourcent = 0.0;
                    showAlert("Code promo expiré ou non encore actif.");
                }
            } else {
                remisePourcent = 0.0;
                showAlert("Code promo invalide.");
            }


            updateTotal();
        });

        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        colPrix.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrix()).asObject());

        // Mise en place de la cellule éditable pour la quantité
        colQuantite.setCellFactory(param -> new TableCell<Produit, Integer>() {
            private final TextField quantiteTextField = new TextField();
            private final Button updateBtn = new Button("Mettre à jour");

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Produit produit = getTableView().getItems().get(getIndex());
                    quantiteTextField.setText(String.valueOf(produit.getQuantite()));  // Afficher la quantité actuelle dans le champ de texte

                    // Ajout du bouton de mise à jour avec gestion de l'événement
                    updateBtn.setOnAction(event -> {
                        try {
                            // Vérification de l'entrée de l'utilisateur
                            int nouvelleQuantite = Integer.parseInt(quantiteTextField.getText());

                            // Vérification si la quantité dépasse le stock
                            if (nouvelleQuantite > produit.getStock()) {
                                showAlert("Stock insuffisant", "La quantité demandée dépasse le stock disponible.");
                            } else if (nouvelleQuantite > 0) {
                                produit.setQuantite(nouvelleQuantite);  // Mise à jour de la quantité
                                cartTable.refresh();  // Rafraîchir la table pour voir les modifications
                                updateTotal();  // Mise à jour immédiate du total
                            } else {
                                showAlert("Quantité invalide", "La quantité doit être supérieure à zéro.");
                            }
                        } catch (NumberFormatException e) {
                            showAlert("Erreur de saisie", "Veuillez entrer un nombre valide.");
                        }
                    });

                    // Disposer le champ de texte et le bouton dans une HBox
                    HBox hBox = new HBox(quantiteTextField, updateBtn);
                    setGraphic(hBox);  // Affiche le HBox dans la cellule
                }
            }

            private void showAlert(String title, String message) {
                Alert alert = new Alert(Alert.AlertType.WARNING, message);
                alert.setTitle(title);
                alert.showAndWait();
            }
        });

        // Affectation du 'getQuantite' pour l'affichage de la quantité dans la table
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantite()).asObject());

        // Calcul du total pour chaque ligne
        colTotal.setCellValueFactory(data -> {
            double prix = data.getValue().getPrix();
            int quantite = data.getValue().getQuantite();
            return new SimpleDoubleProperty(prix * quantite).asObject();
        });

        // Mise en place de la suppression d'un produit
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");

            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
                deleteBtn.setOnAction(event -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(produit);  // Retirer du tableau
                    panier.remove(produit);  // Retirer du panier (si vous gardez cette liste ailleurs)
                    updateTotal();  // Mise à jour du total
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });
    }

    // Méthode pour définir les données du panier
    public void setCartData(ObservableList<Produit> panier, double total) {
        this.panier = panier;
        cartTable.setItems(panier);
        updateTotal();  // Mise à jour initiale du total
    }

    // Mise à jour du total
    public void updateTotal() {
        double total = cartTable.getItems().stream()
                .mapToDouble(produit -> produit.getPrix() * produit.getQuantite())
                .sum();  // Calcul du total des produits
        // Appliquer la remise si elle existe
        double montantRemise = total * remisePourcent;
        total = total - montantRemise; // Soustraire la remise du total
        totalLabel.setText(String.format("%.2f DT", total));  // Mise à jour de l'affichage du total
    }


    @FXML
    public void retourBoutique() {
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }

    // Méthode pour passer la commande
    @FXML
    public void passerCommande() {
        double total = cartTable.getItems().stream()
                .mapToDouble(produit -> produit.getPrix() * produit.getQuantite())
                .sum();
        double montantRemise = total * remisePourcent;
        total -= montantRemise;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/PaiementStripe.fxml"));
            Parent root = loader.load();

            PaiementStripeController controller = loader.getController();
            controller.setData(
                    new ArrayList<>(panier),
                    total,
                    panier,  // Passer la référence du panier
                    this    // Passer la référence du contrôleur panier
            );
            controller.setRemisePourcent(remisePourcent);  // Ajouter cette ligne pour transmettre la remise

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Paiement avec Stripe");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur Impossible d'ouvrir l'interface de paiement");
        }
    }

    private void showAlert( String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearCart() {
        panier.clear();
        cartTable.getItems().clear();
        updateTotal();
    }}