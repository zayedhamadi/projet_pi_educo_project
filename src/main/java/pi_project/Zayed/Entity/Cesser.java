package pi_project.Zayed.Entity;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Cesser {
    private int id;
    private int idUserId;
    private String motif;
    private LocalDate dateMotif= LocalDate.now();
}