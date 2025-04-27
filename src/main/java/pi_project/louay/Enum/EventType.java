package pi_project.louay.Enum;

public enum EventType {
    SORTIE_SCOLAIRE("Sortie scolaire"),
    REUNION("Réunion"),
    COMPETITION("Compétition"),
    ATELIER("Atelier"),
    AUTRE("Autre");

    private final String label;

    EventType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static EventType fromLabel(String label) {
        for (EventType type : values()) {
            if (type.label.equalsIgnoreCase(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type d'événement inconnu : " + label);
    }

    @Override
    public String toString() {
        return label;
    }
}
