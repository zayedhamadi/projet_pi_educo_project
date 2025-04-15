package pi_project.Saif.Interface;

import java.util.List;

public interface Service<T> {
    void ajouter(T t);
    void modifier(T t);
    void supprimer(int id);
    T getById(int id);
    List<T> getAll();
}