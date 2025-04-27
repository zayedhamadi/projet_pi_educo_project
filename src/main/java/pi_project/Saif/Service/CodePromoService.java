package pi_project.Saif.Service;

import pi_project.Saif.Entity.CodePromo;
import pi_project.db.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodePromoService {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(CodePromoService.class.getName());

    public CodePromoService() {
        this.connection = DataSource.getInstance().getConn();
    }

    public void ajouterCode(CodePromo codePromo) {
        String sql = "INSERT INTO code_promo (code, remise, date_debut, date_fin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codePromo.getCode());
            ps.setDouble(2, codePromo.getRemisePourcent());
            ps.setDate(3, Date.valueOf(codePromo.getDateDebut()));
            ps.setDate(4, Date.valueOf(codePromo.getDateFin()));
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ajout du code promo", e);
        }
    }

    public void modifierCode(CodePromo codePromo) {
        String sql = "UPDATE code_promo SET remise = ?, date_debut = ?, date_fin = ? WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, codePromo.getRemisePourcent());
            ps.setDate(2, Date.valueOf(codePromo.getDateDebut()));
            ps.setDate(3, Date.valueOf(codePromo.getDateFin()));
            ps.setString(4, codePromo.getCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la modification du code promo", e);
        }
    }

    public void supprimerCode(String code) {
        String sql = "DELETE FROM code_promo WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la suppression du code promo", e);
        }
    }

    public CodePromo getCodePromo(String code) {
        String sql = "SELECT * FROM code_promo WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new CodePromo(
                        rs.getString("code"),
                        rs.getDouble("remise"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate()
                );
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération du code promo", e);
        }
        return null;
    }

    public List<CodePromo> getAllCodes() {
        List<CodePromo> codes = new ArrayList<>();
        String sql = "SELECT * FROM code_promo";
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                codes.add(new CodePromo(
                        rs.getString("code"),
                        rs.getDouble("remise"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de tous les codes promo", e);
        }
        return codes;
    }

    public boolean verifierCodePromo(String codeEntre) {
        CodePromo codePromo = getCodePromo(codeEntre);
        LocalDate aujourdHui = LocalDate.now();
        return codePromo != null &&
                (aujourdHui.isEqual(codePromo.getDateDebut()) || aujourdHui.isAfter(codePromo.getDateDebut())) &&
                (aujourdHui.isEqual(codePromo.getDateFin()) || aujourdHui.isBefore(codePromo.getDateFin()));
    }
}
