package pi_project.Fedi.entites;

import pi_project.Zayed.Entity.User;
import java.util.Date;

public class eleve {
    private int id;
    private classe classe;
    private User parent;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private double moyenne;
    private int nbreAbsence;
    private Date dateInscription;
    private String qrCode;

    // Constructeurs
    public eleve() {
    }

    public eleve(classe classe, User parent, String nom, String prenom,
                 Date dateNaissance, double moyenne, int nbreAbsence,
                 Date dateInscription, String qrCode) {
        this.classe = classe;
        this.parent = parent;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.moyenne = moyenne;
        this.nbreAbsence = nbreAbsence;
        this.dateInscription = dateInscription;
        this.qrCode = qrCode;
    }

    public eleve(int id, classe classe, User parent, String nom, String prenom,
                 Date dateNaissance, double moyenne, int nbreAbsence,
                 Date dateInscription, String qrCode) {
        this(classe, parent, nom, prenom, dateNaissance, moyenne, nbreAbsence, dateInscription, qrCode);
        this.id = id;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public classe getClasse() {
        return classe;
    }

    public void setClasse(classe classe) {
        this.classe = classe;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public double getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(double moyenne) {
        this.moyenne = moyenne;
    }

    public int getNbreAbsence() {
        return nbreAbsence;
    }

    public void setNbreAbsence(int nbreAbsence) {
        this.nbreAbsence = nbreAbsence;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    // Méthodes utilitaires
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public int getAge() {
        if (dateNaissance == null) return 0;
        long ageInMillis = System.currentTimeMillis() - dateNaissance.getTime();
        return (int) (ageInMillis / (1000L * 60 * 60 * 24 * 365));
    }

    // Méthodes pour les relations
    public String getNomClasse() {
        return classe != null ? classe.getNomclasse() : "Non assigné";
    }

    public String getEmailParent() {
        return parent != null ? parent.getEmail() : "Non renseigné";
    }

    @Override
    public String toString() {
        return "Eleve{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", classe=" + getNomClasse() +
                ", parent=" + getEmailParent() +
                ", moyenne=" + moyenne +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        eleve eleve = (eleve) o;
        return id == eleve.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}