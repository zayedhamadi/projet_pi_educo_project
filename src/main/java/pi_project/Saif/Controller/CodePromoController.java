package pi_project.Saif.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pi_project.Saif.Entity.CodePromo;
import pi_project.Saif.Service.CodePromoService;

import java.time.LocalDate;

public class CodePromoController {

    @FXML private TableView<CodePromo> table;
    @FXML private TableColumn<CodePromo, String> codeCol;
    @FXML private TableColumn<CodePromo, Double> remiseCol;
    @FXML private TableColumn<CodePromo, LocalDate> dateDebutCol;
    @FXML private TableColumn<CodePromo, LocalDate> dateFinCol;

    @FXML private TextField codeField;
    @FXML private TextField remiseField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;

    @FXML private Button ajouterBtn;
    @FXML private Button supprimerBtn;

    private final CodePromoService service = new CodePromoService();

    @FXML
    public void initialize() {
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        remiseCol.setCellValueFactory(new PropertyValueFactory<>("remisePourcent"));
        dateDebutCol.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinCol.setCellValueFactory(new PropertyValueFactory<>("dateFin"));

        ObservableList<CodePromo> codes = FXCollections.observableArrayList(service.getAllCodes());
        table.setItems(codes);

        ajouterBtn.setOnAction(e -> {
            String code = codeField.getText().trim();
            double remise;

            try {
                remise = Double.parseDouble(remiseField.getText().trim());
                if (remise < 0 || remise > 1) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert("Veuillez entrer une remise entre 0 et 1 (ex : 0.1 pour 10%).");
                return;
            }

            LocalDate debut = dateDebutPicker.getValue();
            LocalDate fin = dateFinPicker.getValue();

            if (debut == null || fin == null || fin.isBefore(debut)) {
                showAlert("Veuillez choisir des dates valides (date de fin après la date de début).");
                return;
            }

            if (service.getCodePromo(code) != null) {
                showAlert("Ce code promo existe déjà !");
                return;
            }

            CodePromo newCode = new CodePromo(code, remise, debut, fin);
            service.ajouterCode(newCode);
            codes.add(newCode);

            codeField.clear();
            remiseField.clear();
            dateDebutPicker.setValue(null);
            dateFinPicker.setValue(null);
        });

        supprimerBtn.setOnAction(e -> {
            CodePromo selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.supprimerCode(selected.getCode());
                codes.remove(selected);
            } else {
                showAlert("Veuillez sélectionner un code promo à supprimer.");
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
