package pi_project.Aziz.Entity;
import pi_project.Fedi.entites.eleve;
public class Note {
    private int id;
    private float score;
    private eleve eleve;
    private Quiz quiz;

    public Note() {}

    public Note(int id, float score, eleve eleve, Quiz quiz) {
        this.id = id;
        this.score = score;
        this.eleve = eleve;
        this.quiz = quiz;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public eleve getEleve() {
        return eleve;
    }

    public void setEleve(eleve eleve) {
        this.eleve = eleve;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}

