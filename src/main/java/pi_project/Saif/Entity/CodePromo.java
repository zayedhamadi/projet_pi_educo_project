package pi_project.Saif.Entity;

import java.time.LocalDate;

public class CodePromo {
    private String code;
    private double remisePourcent;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public CodePromo(String code, double remisePourcent, LocalDate dateDebut, LocalDate dateFin) {
        this.code = code;
        this.remisePourcent = remisePourcent;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public String getCode() { return code; }
    public double getRemisePourcent() { return remisePourcent; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }

    public void setCode(String code) { this.code = code; }
    public void setRemisePourcent(double remisePourcent) { this.remisePourcent = remisePourcent; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}
