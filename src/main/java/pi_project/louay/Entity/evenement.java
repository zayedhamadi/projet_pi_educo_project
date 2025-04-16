package pi_project.louay.Entity;

import lombok.*;
import java.time.LocalDate;
import pi_project.louay.Enum.EventType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class evenement {
    private int id;
    private String titre;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieu;
    private boolean inscriptionRequise;
    private Integer nombrePlaces;
    private EventType type;
}
