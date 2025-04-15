package pi_project.Saif.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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
                            Button deleteButton = new Button("Supprimer");

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
    private void modifierCategorie(Categorie categorie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ModifierCategorieView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Modifier une catégorie");
            stage.setScene(new Scene(loader.load()));

            // Passer l'objet catégorie à la fenêtre de modification
            ModifierCategorieView controller = loader.getController();  // Récupérer le contrôleur
            controller.setCategorie(categorie);// Passer la catégorie au contrôleur
            controller.setCategorieView(this);

//            loadCategories();//Lyouminchallah
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification");
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

    // Méthode pour ajouter une catégorie
    @FXML
    private void ajouterCategorie() {
        try {
            // Charger la fenêtre de l'ajout de catégorie
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/CategorieAdd.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Ajouter une catégorie");
            stage.setScene(new Scene(loader.load()));
            // 👇 Passer le contrôleur principal à la vue ajout
            CategorieAdd controller = loader.getController();
            controller.setCategorieView(this);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout");
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
