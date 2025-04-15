package pi_project.Fedi.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.services.classeservice;
import pi_project.Main;

import java.io.IOException;

public class AjouterClasse {

    @FXML
    private TextField capacite;

    @FXML
    private SplitMenuButton enseignent;

    @FXML
    private TextField nomclasse;

    @FXML
    private TextField numsalle;

    @FXML
    private Label nomErreur;

    @FXML
    private Label salleErreur;

    @FXML
    private Label capaciteErreur;

    private final classeservice ps = new classeservice();

    @FXML
    void back(ActionEvent event) throws Exception {
        try {
            Main.setRoot("ListeOfClasse.fxml"); // Retour à la liste
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void save(ActionEvent event) {
        // Nettoyer les erreurs précédentes
        nomErreur.setText("");
        salleErreur.setText("");
        capaciteErreur.setText("");

        boolean isValid = true;

        String nom = nomclasse.getText().trim();
        String salleStr = numsalle.getText().trim();
        String capaciteStr = capacite.getText().trim();

        // Validation du nom
        if (nom.isEmpty()) {
            nomErreur.setText("Le nom de la classe est obligatoire.");
            isValid = false;
        }

        // Validation du numéro de salle
        int salle = 0;
        try {
            salle = Integer.parseInt(salleStr);
            if (salle <= 0) {
                salleErreur.setText("Le numéro de salle doit être un entier > 0.");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            salleErreur.setText("Le numéro de salle doit être un entier.");
            isValid = false;
        }

        // Validation de la capacité
        int cap = 0;
        try {
            cap = Integer.parseInt(capaciteStr);
            if (cap <= 0) {
                capaciteErreur.setText("La capacité doit être > 0.");
                isValid = false;
            } else if (cap > 30) {
                capaciteErreur.setText("La capacité ne peut pas dépasser 30.");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            capaciteErreur.setText("La capacité doit être un entier.");
            isValid = false;
        }

        // Si tout est valide, on ajoute la classe
        if (isValid) {
            classe c = new classe(nom, salle, cap);
            ps.add(c);
            System.out.println("Classe ajoutée avec succès !");
            nomclasse.clear();
            numsalle.clear();
            capacite.clear();

            // Retour automatique à la liste (optionnel)
            try {
                Main.setRoot("ListeOfClasse.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
