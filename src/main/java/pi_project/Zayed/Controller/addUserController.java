package pi_project.Zayed.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.UserImpl;
import pi_project.Zayed.Utils.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

public class addUserController {

    private final UserImpl userImpl = new UserImpl();

    @FXML
    private BorderPane mainForm;
    @FXML
    private Button backToActiveUserr, AnnulerAjout;
    @FXML
    private TextField nom, prenom, email, description, numTel, adresse;
    @FXML
    private PasswordField password;
    @FXML
    private DatePicker dateNaissance;
    @FXML
    private ComboBox<String> role, genre;
    @FXML
    private ImageView imageP;
    @FXML
    private Label all;

    private File selectedFile;

    @FXML
    public void initialize() {
        role.setItems(FXCollections.observableArrayList("Admin", "Enseignant", "Parent"));
        genre.setItems(FXCollections.observableArrayList("homme", "femme"));
    }

    public void AnnulerAjout(ActionEvent actionEvent) {
        Stage stage = (Stage) AnnulerAjout.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void ajouterUneImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageP.setImage(image);
        }
    }

    private boolean doingsomecheckValidator() {
        if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty()
                || password.getText().isEmpty() || description.getText().isEmpty()
                || numTel.getText().isEmpty() || adresse.getText().isEmpty()
                || dateNaissance.getValue() == null || role.getValue() == null || genre.getValue() == null
                || selectedFile == null) {
            Constant.showAlert(Alert.AlertType.INFORMATION, "Veuillez remplir tous les champs et sélectionner une image", "Erreur d'ajout", "Erreur d'ajout");
            all.setText("Veuillez remplir tous les champs et sélectionner une image.");
            return false;
        }
        return true;
    }

    @FXML
    void AjouterUser() {
        try {
            if (!doingsomecheckValidator()) return;

            String imageName = saveImage();

            int num = Integer.parseInt(numTel.getText());
            Set<Role> roles = new HashSet<>();
            roles.add(Role.valueOf(role.getValue()));

            User user = new User();
            user.setNom(nom.getText());
            user.setPrenom(prenom.getText());
            user.setEmail(email.getText());
            user.setPassword(password.getText());
            user.setDescription(description.getText());
            user.setAdresse(adresse.getText());
            user.setNum_tel(num);
            user.setDate_naissance(dateNaissance.getValue());
            user.setRoles(roles);
            user.setGenre(Genre.valueOf(genre.getValue()));
            user.setImage(imageName);

            userImpl.addUser(user);
            Constant.showAlert(Alert.AlertType.INFORMATION, "Utilisateur ajouté avec succès !", "Succès", "Succès");
            resetForm();

        } catch (Exception e) {
            e.printStackTrace();
            Constant.handleException(e, e.getMessage());
        }
    }

    private String saveImage() throws IOException {
        Properties props = new Properties();
        FileInputStream in = new FileInputStream("config.properties");
        props.load(in);
        in.close();

        String uploadPath = props.getProperty("upload.path");
        String originalFileName = selectedFile.getName();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;

        Path destination = Paths.get(uploadPath, uniqueFileName);

        if (Files.exists(destination)) {
            Files.delete(destination);
        }

        Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    private void resetForm() {
        nom.clear();
        prenom.clear();
        email.clear();
        password.clear();
        description.clear();
        numTel.clear();
        adresse.clear();
        dateNaissance.setValue(null);
        role.getSelectionModel().clearSelection();
        genre.getSelectionModel().clearSelection();
        imageP.setImage(null);
        selectedFile = null;
        all.setText("");
    }
}
