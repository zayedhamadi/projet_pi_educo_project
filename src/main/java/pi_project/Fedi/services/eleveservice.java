package pi_project.Fedi.services;

import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.interfaces.idsevice;
import pi_project.db.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class eleveservice implements idsevice<eleve>  {


    @Override
    public void add(eleve eleve) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void update(eleve eleve) {

    }

    @Override
    public List<eleve> getAll() {
        return List.of();
    }

    @Override
    public eleve getOne(int id) {
        return null;
    }
}
