package pi_project.Fedi.interfaces;

import java.util.List;

public interface idsevice<T> {
    void add(T t);
    void delete(int id);
    void update( T t);
    List<T> getAll();
    T getOne(int id);
}
