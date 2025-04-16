package pi_project.louay.Entity;

import lombok.*;
import java.time.LocalDate;
import pi_project.louay.Entity.evenement;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class inscriptionevenement {
    private int id;
    private LocalDate dateInscription;
    private evenement evenement;
    private int enfant_id;

}
