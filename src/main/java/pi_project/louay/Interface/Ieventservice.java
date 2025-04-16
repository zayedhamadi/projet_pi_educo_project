package pi_project.louay.Interface;

import java.util.List;


public interface Ieventservice<T> {
    void ajouter(T t);

    void modifier(T t);

    void supprimer(T t);

    T getById(int id);


    List<T> getAll();


}
