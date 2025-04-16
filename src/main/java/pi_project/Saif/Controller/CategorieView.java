package pi_project.Saif.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pi_project.Saif.Entity.Categorie;
import pi_project.Saif.Service.CategorieService;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

public class CategorieView {

    @FXML
    private TableView<Categorie> tableView;

    @FXML
    private TableColumn<Categorie, Integer> colId;

    @FXML
    private TableColumn<Categorie, String> colNom;

    @FXML
    private TableColumn<Categorie, String> colDescription;

    @FXML
    private TableColumn<Categorie, String> colActions;  // Ajout de la colonne Actions

    private CategorieService service = new CategorieService();

    @FXML
    public void initialize() {
        // Initialiser les colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // Créer une cellule personnalisée pour la colonne Actions
        colActions.setCellFactory(new Callback<TableColumn<Categorie, String>, TableCell<Categorie, String>>() {
            @Override
            public TableCell<Categorie, String> call(TableColumn<Categorie, String> param) {
                return new TableCell<Categorie, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {

                            // Créer les boutons de modification et suppression
                            Button editButton = new Button("Modifier");
                            editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

                            Button deleteButton = new Button("Supprimer");
                            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");


                            // Ajout de l'action de modification
                            editButton.setOnAction(event -> {
                                Categorie categorie = getTableView().getItems().get(getIndex());
                                modifierCategorie(categorie);
                            });

                            // Ajout de l'action de suppression
                            deleteButton.setOnAction(event -> {
                                Categorie categorie = getTableView().getItems().get(getIndex());
                                supprimerCategorie(categorie);
                            });

                            // Ajouter les boutons à la cellule
                            HBox hbox = new HBox(10, editButton, deleteButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });

        // Charger les catégories dans la table
        loadCategories();
    }
    public void refreshTable() {
        loadCategories(); // This will reload the data into the table
        tableView.refresh(); // This forces a refresh of the table's display
    }

     public void loadCategories() {
        // Récupérer toutes les catégories depuis le service
        ObservableList<Categorie> categories = FXCollections.observableArrayList(service.getAll());
        tableView.setItems(categories);
    }

    // Méthode pour modifier une catégorie
//    private void modifierCategorie(Categorie categorie) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ModifierCategorieView.fxml"));
//            Stage stage = new Stage();
//            stage.setTitle("Modifier une catégorie");
//            stage.setScene(new Scene(loader.load()));
//
//            // Passer l'objet catégorie à la fenêtre de modification
//            ModifierCategorieView controller = loader.getController();  // Récupérer le contrôleur
//            controller.setCategorie(categorie);// Passer la catégorie au contrôleur
//            controller.setCategorieView(this);
//
////            loadCategories();//Lyouminchallah
//            stage.show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification");
//        }
//    }

//    private void modifierCategorie(Categorie categorie) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ModifierCategorieView.fxml"));
//            Parent root = loader.load();
//
//            ModifierCategorieView controller = loader.getController();
//            controller.setCategorie(categorie);
//            controller.setCategorieView(this);
//
//            // Remplacer le contenu de la scène actuelle
//            Stage currentStage = (Stage) tableView.getScene().getWindow();
//            currentStage.setScene(new Scene(root));
//            currentStage.setTitle("Modifier une catégorie");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de modification");
//        }
//    }
private void modifierCategorie(Categorie categorie) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ModifierCategorieView.fxml"));
        Parent view = loader.load();

        ModifierCategorieView controller = loader.getController();
        controller.setCategorie(categorie);
        controller.setCategorieView(this);

        // Rechercher le StackPane avec l'ID contentPane dans la scène actuelle
        StackPane contentPane = (StackPane) tableView.getScene().lookup("#contentPane");

        // Remplacer le contenu central sans toucher à la sidebar
        contentPane.getChildren().setAll(view);

    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de modification");
    }
}

    // Méthode pour supprimer une catégorie
    private void supprimerCategorie(Categorie categorie) {
        try {
            service.supprimer(categorie.getId());  // Suppression via le service
            loadCategories();  // Recharger la liste des catégories après suppression
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie supprimée avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la catégorie");
        }
    }


//    @FXML
//    private void ajouterCategorie() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/CategorieAdd.fxml"));
//            Parent root = loader.load();
//
//            CategorieAdd controller = loader.getController();
//            controller.setCategorieView(this);
//
//            // Remplacer le contenu de la scène actuelle
//            Stage currentStage = (Stage) tableView.getScene().getWindow();
//            currentStage.setScene(new Scene(root));
//            currentStage.setTitle("Ajouter une catégorie");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page d'ajout");
//        }
//    }
@FXML
private void ajouterCategorie() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/CategorieAdd.fxml"));
        Parent view = loader.load();

        CategorieAdd controller = loader.getController();
        controller.setCategorieView(this);

        // Rechercher le StackPane avec l'ID contentPane dans la scène actuelle
        StackPane contentPane = (StackPane) tableView.getScene().lookup("#contentPane");

        // Remplacer le contenu central
        contentPane.getChildren().setAll(view);

    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page d'ajout");
    }
}

    // Affichage d'une alerte
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
