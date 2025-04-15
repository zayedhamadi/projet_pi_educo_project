package pi_project.Saif.Controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Service.CommandeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaiementStripeController {

    private ObservableList<Produit> panier;
    private double total;
    @FXML private Label totalLabel;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvcField;
    @FXML private Label statusLabel;


    public void setData(ObservableList<Produit> panier, double total) {
        this.panier = panier;
        this.total = total;
    }

    @FXML
    private void payerAvecStripe() {
        Stripe.apiKey = ""; // ta vraie clé ici

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", (int) (total * 100)); // Stripe attend les montants en centimes
            params.put("currency", "eur");
            params.put("payment_method_types", List.of("card"));

            PaymentIntent intent = PaymentIntent.create(params);

            // simulate un paiement réussi automatiquement (car on n'a pas le front Stripe)
            System.out.println("Paiement réussi : " + intent.getId());

            // Enregistrer la commande maintenant
            CommandeService commandeService = new CommandeService();
            commandeService.passerCommande(1, panier, "Stripe");

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Commande payée et enregistrée !");
            alert.showAndWait();

            ((Stage) ((Button) payerButton).getScene().getWindow()).close();

        } catch (StripeException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Paiement échoué").showAndWait();
        }
    }
    @FXML
    private void handlePaiement() {
        // Pour le test, on simule le paiement avec la carte 4242 4242 4242 4242
        String cardNumber = cardNumberField.getText();
        String expiry = expiryField.getText();
        String cvc = cvcField.getText();

        if (cardNumber.equals("4242 4242 4242 4242") && !expiry.isEmpty() && !cvc.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("✅ Paiement effectué avec succès !");
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("❌ Paiement échoué. Vérifiez les informations.");
        }
    }

    @FXML
    private Button payerButton;
}
