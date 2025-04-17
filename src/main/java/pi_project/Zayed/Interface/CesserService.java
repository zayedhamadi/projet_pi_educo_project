package pi_project.Zayed.Interface;

import pi_project.Zayed.Entity.User;


public interface CesserService {

    void cesserUser(int id, String motif);

    User ActiverUserCesser(int id);

    void SupprimerCessation(int id);
}
