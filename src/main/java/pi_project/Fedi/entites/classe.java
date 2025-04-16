package pi_project.Fedi.entites;

public class classe {
    private int id;
    private String nomclasse;
    private int numsalle;
    private int capacite;


    public classe(int id, String nom_classe, int num_salle, int capacite_max) {
        this.id = id;
        this.nomclasse = nom_classe;
        this.numsalle = num_salle;
        this.capacite = capacite_max;
    }
    public classe(String nomclasse , int numsalle, int capacite) {
        this.nomclasse = nomclasse;
        this.numsalle = numsalle;
        this.capacite = capacite;


    }
    public classe() {

    }
    @Override
    public String toString() {
        return "classe{" +
                "id=" + id +
                ", nomclasse='" + nomclasse + '\'' +
                ", numsalle=" + numsalle +
                ", capacite=" + capacite +
                '}';
    }






    public int getId() {
        return id;
    }

    public String getNomclasse() {
        return nomclasse;
    }

    public int getNumsalle() {
        return numsalle;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNomclasse(String nomclasse) {
        this.nomclasse = nomclasse;
    }

    public void setNumsalle(int numsalle) {
        this.numsalle = numsalle;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }
}
