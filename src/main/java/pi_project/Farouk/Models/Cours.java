package pi_project.Farouk.Models;

public class Cours {

    private int id;
    private String name;
    private int idMatiere;
    private int classe;
    private int chapterNumber;
    private String pdfFilename;

    // Constructor without id (for insert operations)
    public Cours(String name, int idMatiere, int classe, int chapterNumber, String pdfFilename) {
        this.name = name;
        this.idMatiere = idMatiere;
        this.classe = classe;
        this.chapterNumber = chapterNumber;
        this.pdfFilename = pdfFilename;
    }

    // Constructor with id (for update/retrieve operations)
    public Cours(int id, String name, int idMatiere, int classe, int chapterNumber, String pdfFilename) {
        this.id = id;
        this.name = name;
        this.idMatiere = idMatiere;
        this.classe = classe;
        this.chapterNumber = chapterNumber;
        this.pdfFilename = pdfFilename;
    }

    public Cours(){

    }
    // Getters and Setters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIdMatiere() {
        return idMatiere;
    }

    public int getClasse() {
        return classe;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public String getPdfFilename() {
        return pdfFilename;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdMatiere(int idMatiere) {
        this.idMatiere = idMatiere;
    }

    public void setClasse(int classe) {
        this.classe = classe;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public void setPdfFilename(String pdfFilename) {
        this.pdfFilename = pdfFilename;
    }
}

