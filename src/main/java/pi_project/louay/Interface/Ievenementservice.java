package pi_project.louay.Interface;

import pi_project.louay.Entity.evenement;

import java.time.LocalDate;
import java.util.List;

public interface Ievenementservice<T> {
    int ajouter(T t);

    void modifier(T t);

    void supprimer(T t);

    T getById(int id);


    List<T> getAll();

    List<T> getEvenementsCetteSemaine(LocalDate startOfWeek, LocalDate endOfWeek);
}
