package pi_project.Saif.Entity;

public class Produit {

        private int id;
        private String nom;
        private String description;
        private double prix;
        private int stock;
        private String image;
        private int categorieId;
    private int quantite = 1;


    // Constructeur
        public Produit(int id, String nom, String description, double prix, int stock, String image, int categorieId) {
            this.id = id;
            this.nom = nom;
            this.description = description;
            this.prix = prix;
            this.stock = stock;
            this.image = image;
            this.categorieId = categorieId;
        }
public Produit(){};

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getPrix() {
            return prix;
        }

        public void setPrix(double prix) {
            this.prix = prix;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getCategorieId() {
            return categorieId;
        }

        public void setCategorieId(int categorieId) {
            this.categorieId = categorieId;
        }
    // ✅ Getter/Setter pour la quantité (non persisté en BD)
    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

}
