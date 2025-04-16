package pi_project.Farouk.Models;

public class Matiere {
    private int id;
    private int id_ensg; // ID of the teacher
    private String nom; // Subject name
    private double coefficient;

    // Constructors
    public Matiere() {
    }

    public Matiere(int id_ensg, String nom, double coefficient) {
        this.id_ensg = id_ensg;
        this.nom = nom;
        this.coefficient = coefficient;
    }

    public Matiere(int id, int id_ensg, String nom, double coefficient) {
        this.id = id;
        this.id_ensg = id_ensg;
        this.nom = nom;
        this.coefficient = coefficient;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_ensg() {
        return id_ensg;
    }

    public void setId_ensg(int id_ensg) {
        this.id_ensg = id_ensg;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public String toString() {
        return nom + " (Coeff: " + coefficient + ")";
    }
}


