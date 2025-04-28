package pi_project.louay.Enum;

public enum Statut {
    EN_ATTENTE("En attente"),
    EN_COURS("En cours"),
    TRAITEE("Traitée");

    private final String label;

    Statut(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


    public static Statut fromLabel(String label) {
        for (Statut statut : values()) {
            if (statut.label.equalsIgnoreCase(label)) {
                return statut;
            }
        }
        throw new IllegalArgumentException("Statut inconnu : " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}
