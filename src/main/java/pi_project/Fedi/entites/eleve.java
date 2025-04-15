package pi_project.Fedi.entites;

import java.util.Date;

public class eleve {
    private int id;
    private classe id_classe;
    private int id_parent;
    private String nom;
    private String prenom;
    private Date date_naissance;
    private double moyenne;
    private  int nbre_absence;
    private Date date_inscription;
    private String qr_code;

    public eleve(classe id_classe, int id_parent, String nom, String prenom, Date date_naissance, double moyenne, int nbre_absence, Date date_inscription, String qr_code) {
        this.id_classe = id_classe;
        this.id_parent = id_parent;
        this.nom = nom;
        this.prenom = prenom;
        this.date_naissance = date_naissance;
        this.moyenne = moyenne;
        this.nbre_absence = nbre_absence;
        this.date_inscription = date_inscription;
        this.qr_code = qr_code;
    }

    public eleve(int id, classe id_cl, int id_parent, String nom, String prenom, Date date_naissance, double moyenne, int nbre_absence, Date date_inscription, String qr_code) {
        this.id = id;
        this.id_classe = id_cl;
        this.id_parent = id_parent;
        this.nom = nom;
        this.prenom = prenom;
        this.date_naissance = date_naissance;
        this.moyenne = moyenne;
        this.nbre_absence = nbre_absence;
        this.date_inscription = date_inscription;
        this.qr_code = qr_code;

    }
     public eleve(int id, int idClasseId, int idParentId, String nom, String prenom, java.sql.Date dateDeNaissance, float moyenne, int nbreAbscence, java.sql.Date dateInscription, String qrCodeDataUri){

     }
    @Override
    public String toString() {
        return "eleve{" +
                "id=" + id +
                ", id_classe=" + id_classe +
                ", id_parent=" + id_parent +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", date_naissance=" + date_naissance +
                ", moyenne=" + moyenne +
                ", nbre_absence=" + nbre_absence +
                ", date_inscription=" + date_inscription +
                ", qr_code='" + qr_code + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public classe getId_classe() {
        return id_classe;
    }

    public int getId_parent() {
        return id_parent;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Date getDate_naissance() {
        return date_naissance;
    }

    public float getMoyenne() {
        return (float) moyenne;
    }

    public int getNbre_absence() {
        return nbre_absence;
    }

    public Date getDate_inscription() {
        return date_inscription;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId_classe(classe id_classe) {
        this.id_classe = id_classe;
    }

    public void setId_parent(int id_parent) {
        this.id_parent = id_parent;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setDate_naissance(Date date_naissance) {
        this.date_naissance = date_naissance;
    }

    public void setMoyenne(float moyenne) {
        this.moyenne = moyenne;
    }

    public void setNbre_absence(int nbre_absence) {
        this.nbre_absence = nbre_absence;
    }

    public void setDate_inscription(Date date_inscription) {
        this.date_inscription = date_inscription;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }
}
