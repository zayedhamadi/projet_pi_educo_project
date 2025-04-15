package pi_project.Aziz.Entity;

import java.util.List;

public class Question {
    private int id;
    private String texte;
    private List<String> options;
    private String reponse;
    private Quiz quiz;
    private String quizName;
    private int quizId;
// Store just the name
// Reference to the associated Quiz

    // Default constructor
    public Question() {}

    // Full constructor
    public Question(int id, String texte, List<String> options, String reponse, Quiz quiz) {
        this.id = id;
        this.texte = texte;
        this.options = options;
        this.reponse = reponse;
        this.quiz = quiz;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }
    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    // toString
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", texte='" + texte + '\'' +
                ", options=" + options +
                ", reponse='" + reponse + '\'' +
                ", quiz=" + quiz +
                '}';
    }
}

