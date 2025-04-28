package pi_project.Zayed.Entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cesser {
    int id;
     int idUserId;
     String motif;
     LocalDate dateMotif= LocalDate.now();
}