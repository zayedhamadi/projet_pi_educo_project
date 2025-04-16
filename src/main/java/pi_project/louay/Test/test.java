package pi_project.louay.Test;

import pi_project.louay.Entity.reclamation;
import pi_project.Zayed.Entity.User;
import pi_project.louay.Enum.Statut;
import pi_project.louay.Service.reclamationImp;

import java.time.LocalDate;
import java.util.List;

public class test {
    public static void main(String[] args) {
        reclamationImp service = new reclamationImp();

        // 🔹 1. Test Ajouter
        reclamation r = new reclamation();
        r.setTitre("Retard de transport");
        r.setDescription("Le bus est toujours en retard");
        r.setDateDeCreation(LocalDate.now());
        r.setStatut(Statut.EN_ATTENTE);

        User u = new User();
        u.setId(1); // remplace par un id existant dans ta base
        r.setUser(u);

        service.ajouter(r);

        // 🔹 2. Test Affichage
        List<reclamation> list = service.getAll();
        System.out.println("📋 Toutes les réclamations :");
        for (reclamation rec : list) {
            System.out.println(rec.getId() + " | " + rec.getTitre() + " | " + rec.getStatut());
        }

        // 🔹 3. Test Update
        if (!list.isEmpty()) {
            reclamation r1 = list.get(0);
            r1.setStatut(Statut.TRAITEE);
            service.modifier(r1);
        }

        // 🔹 4. Test Get by ID
        reclamation testGet = service.getById(2); // mets un ID valide
        if (testGet != null) {
            System.out.println("🔍 Reclamation récupérée : " + testGet.getTitre());
        }

        // 🔹 5. Test Delete
        reclamation r1 = new reclamation();
        r1.setId(3);
       service.supprimer(r1); // ⚠️ mets un id existant à supprimer
    }
}
