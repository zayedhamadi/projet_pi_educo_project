package pi_project.Farouk.Services;

import java.sql.SQLException;
import java.util.List;

public interface IService  <T> {
    void ajouter(T t) throws SQLException;
    boolean modifier(T t) throws SQLException;
    boolean supprimer(T t) throws SQLException;
    List<T> rechercher() throws SQLException;
    List<T> recupererTous() throws SQLException;
}
