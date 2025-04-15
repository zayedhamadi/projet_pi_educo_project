package pi_project.Saif.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import pi_project.Saif.Entity.Categorie;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Service.CategorieService;
import pi_project.Saif.Service.ProduitService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class BoutiqueController {

    @FXML private FlowPane produitsContainer;
    @FXML private ComboBox<Categorie> categorieCombo;
    @FXML private TextField searchField;
    @FXML private Button filtrerBtn;
    @FXML private Button voirPanierBtn;

    private final ProduitService produitService = new ProduitService();
    private final CategorieService categorieService = new CategorieService();
    private final ObservableList<Produit> allProduits = FXCollections.observableArrayList();
    private final ObservableList<Produit> panier = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadCategories();
        loadProduits();

        filtrerBtn.setOnAction(e -> filtrerProduits());
        voirPanierBtn.setOnAction(e -> ouvrirPanier());

        updatePanierCount();
    }

    private void loadCategories() {
        List<Categorie> categories = categorieService.getAll();
        categorieCombo.getItems().add(null); // Pour "Toutes les catégories"
        categorieCombo.getItems().addAll(categories);
    }

    private void loadProduits() {
        List<Produit> produits = produitService.getAll();
        allProduits.setAll(produits);
        afficherProduits(allProduits);
    }

    private void filtrerProduits() {
        String recherche = searchField.getText().toLowerCase().trim();
        Categorie selectedCategorie = categorieCombo.getValue();

        List<Produit> filtres = allProduits.stream()
                .filter(p -> p.getNom().toLowerCase().contains(recherche))
//                .filter(p -> selectedCategorie == null || p.getCategorie().getId() == selectedCategorie.getId())
                .collect(Collectors.toList());

        afficherProduits(filtres);
    }

    @FXML
    public void afficherProduits(List<Produit> produits) {
        produitsContainer.getChildren().clear(); // Vide le conteneur des produits avant d'ajouter de nouveaux produits

        for (Produit p : produits) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/card_produit.fxml"));
                AnchorPane card = loader.load(); // Charge la carte du produit

                // Récupère le contrôleur de la carte
                CardProduitController controller = loader.getController();

                // Assigne les données du produit à la carte
                controller.setData(p, () -> {
                    panier.add(p); // Ajoute le produit au panier
                    updatePanierCount(); // Met à jour le nombre d'articles dans le panier
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Produit ajouté au panier !");
                    alert.showAndWait(); // Affiche une alerte quand le produit est ajouté au panier
                });

                // Ajoute la carte dans le FlowPane
                produitsContainer.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace(); // Gère les erreurs de chargement de la carte
            }
        }
    }
    // Add this method to BoutiqueController to open the cart view
    private void ouvrirPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/Panier.fxml"));
            AnchorPane panierPane = loader.load();

            PanierController controller = loader.getController();
            double total = panier.stream().mapToDouble(Produit::getPrix).sum();
            controller.setCartData(panier, total);

            Stage stage = new Stage();
            stage.setTitle("Votre Panier");
            stage.setScene(new Scene(panierPane));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR, "Impossible d'ouvrir le panier");
            alert.showAndWait();
        }
    }
    private void updatePanierCount() {
        voirPanierBtn.setText("🛒 Panier (" + panier.size() + ")");
    }


}
