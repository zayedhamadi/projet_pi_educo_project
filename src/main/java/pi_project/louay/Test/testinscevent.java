package pi_project.louay.Test;

import pi_project.louay.Entity.evenement;
import pi_project.louay.Entity.inscriptionevenement;
import pi_project.louay.Service.inscevenementImp;

import java.time.LocalDate;
import java.util.List;

public class testinscevent {
    public static void main(String[] args) {
        inscevenementImp service = new inscevenementImp();

        // 🔸 Créer un objet Evenement (tu dois avoir un événement avec cet ID dans ta base)
        evenement ev = new evenement();
        ev.setId(3); // ⚠️ Remplace par un ID réel existant dans ta base de données

        // 🔹 Créer une inscription
        inscriptionevenement inscription = new inscriptionevenement();
        inscription.setEnfant_id(1); // ⚠️ Remplace par l'ID d’un enfant réel existant
        inscription.setEvenement(ev);
        inscription.setDateInscription(LocalDate.now());

        // ➕ Ajouter une inscription
        service.ajouter(inscription);

        // 🔁 Afficher toutes les inscriptions
        System.out.println("\n📋 Liste des inscriptions :");
        List<inscriptionevenement> inscriptions = service.getAll();
        for (inscriptionevenement i : inscriptions) {
            System.out.println(i);
        }

        // ✏️ Modifier la première inscription trouvée
        if (!inscriptions.isEmpty()) {
            inscriptionevenement first = inscriptions.get(0);
            first.setDateInscription(LocalDate.of(2025, 5, 1)); // exemple nouvelle date
            service.modifier(first);
            System.out.println("\n✏️ Inscription modifiée !");
        }

        // ❌ Supprimer la première inscription trouvée
        if (!inscriptions.isEmpty()) {
            inscriptionevenement first = inscriptions.get(0);
            service.supprimer(first);
            System.out.println("\n🗑️ Inscription supprimée !");
        }
    }
}
