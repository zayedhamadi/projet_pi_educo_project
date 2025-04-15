package pi_project.Saif.Entity;

import java.time.LocalDateTime;

public class Commande {
    private int id;
    private int parentId;
    private LocalDateTime dateCommande;
    private double montantTotal;
    private String statut;
    private String modePaiement;

    // Constructeurs
    public Commande() {}

    public Commande(int id, int parentId, LocalDateTime dateCommande, double montantTotal, String statut, String modePaiement) {
        this.id = id;
        this.parentId = parentId;
        this.dateCommande = dateCommande;
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.modePaiement = modePaiement;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }
}
