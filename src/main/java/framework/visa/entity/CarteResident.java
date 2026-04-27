package framework.visa.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Carte_resident")
public class CarteResident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Column(name = "date_donnation", nullable = false)
    private LocalDate dateDonnation;

    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    @ManyToOne
    @JoinColumn(name = "id_demandeur")
    private Demandeur demandeur;

    @ManyToOne
    @JoinColumn(name="id_etat")
    private Etat etat;

    public Etat getEtat() {
        return etat;
    }
    public void setEtat(Etat etat) {
        this.etat = etat;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public LocalDate getDateDonnation() { return dateDonnation; }
    public void setDateDonnation(LocalDate dateDonnation) { this.dateDonnation = dateDonnation; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    public Demandeur getDemandeur() { return demandeur; }
    public void setDemandeur(Demandeur demandeur) { this.demandeur = demandeur; }
}
