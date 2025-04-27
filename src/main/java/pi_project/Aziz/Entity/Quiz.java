package pi_project.Aziz.Entity;

import java.util.Date;

public class Quiz {
    private int id;
    private String nom;
    private Date dateAjout;
    private String description;
    private int timeLimit;


    // Foreign keys for Classe, Matiere, and Cours
    private int classeId;
    private int matiereId;
    private int coursId;

    private String classeName;
    private String matiereName;
    private String coursName;


    // Constructeur vide
    public Quiz() {}

    // Constructeur complet
    public Quiz(int id, String nom, Date dateAjout, int classeId, int matiereId, int coursId, int timeLimit) {
        this.id = id;
        this.nom = nom;
        this.dateAjout = dateAjout;
        this.classeId = classeId;
        this.matiereId = matiereId;
        this.coursId = coursId;
        this.timeLimit = timeLimit;

    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }

    public int getClasseId() {
        return classeId;
    }

    public void setClasseId(int classeId) {
        this.classeId = classeId;
    }

    public int getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(int matiereId) {
        this.matiereId = matiereId;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCoursId() {
        return coursId;
    }

    public void setCoursId(int coursId) {
        this.coursId = coursId;
    }
    public String getClasseName() { return classeName; }

    public void setClasseNom(String classeNom) { this.classeName = classeNom; }

    public String getMatiereName() { return matiereName; }

    public void setMatiereNom(String matiereNom) { this.matiereName= matiereNom; }

    public String getCoursName() { return coursName; }

    public void setCoursNom(String coursNom) { this.coursName = coursNom; }
    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", dateAjout=" + dateAjout +
                ", classeId=" + classeId +
                ", matiereId=" + matiereId +
                ", coursId=" + coursId +
                ", classeNom='" + classeName + '\'' +
                ", matiereNom='" + matiereName + '\'' +
                ", coursNom='" + coursName + '\'' +
                '}';
    }

    // toString method for displaying the quiz info

}