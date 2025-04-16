package pi_project.louay.Test;

import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;

import java.time.LocalDate;

public class testevenement {
    public static void main(String[] args) {
        evenementImp service = new evenementImp();

        // 1. Ajout
        evenement e1 = new evenement();
        e1.setTitre("Atelier Java");
        e1.setDescription("Introduction à JavaFX");
        e1.setDateDebut(LocalDate.of(2025, 4, 15));
        e1.setDateFin(LocalDate.of(2025, 4, 16));
        e1.setNombrePlaces(30);
        e1.setLieu("Centre de formation");
        e1.setType(EventType.ATELIER);
        service.ajouter(e1);

        System.out.println("ID généré: " + e1.getId()); // Vérifiez l'ID

        // 2. Récupération - Vérifiez que l'objet existe
       // evenement fetched = service.getById(e1.getId());
        //if(fetched == null) {
          //  System.err.println("ERREUR: Événement non trouvé avec ID " + e1.getId());
           // return; // Arrête le test si échec
        //}

        // 3. Modification
       // fetched.setTitre("Nouveau titre");
        //service.modifier(fetched);

        // 4. Vérification
        //evenement updated = service.getById(e1.getId());
        //System.out.println("Après modification: " + updated.getTitre());
        evenement e = new evenement();
        e.setId(2);

        service.supprimer(e);
    }
}

