package pi_project.louay.Entity;


import lombok.*;
import java.time.LocalDate;
import pi_project.louay.Enum.Statut;
import pi_project.Zayed.Entity.User;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class reclamation {
    private int id;
    private String titre;
    private String description;
    private LocalDate dateDeCreation = LocalDate.now();
    private Statut statut = Statut.EN_ATTENTE;;
    private User user;

}
