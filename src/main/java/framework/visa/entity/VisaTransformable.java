package framework.visa.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Visa_transformable")
public class VisaTransformable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_demandeur", nullable = false)
    private Demandeur demandeur;

    @Column(nullable = false, length = 50)
    private String reference;

    @Column(name = "date_arrivee_madagascar", nullable = false)
    private LocalDate dateArriveeMadagascar;

    @Column(name = "lieu_entree_madagascar", nullable = false, length = 100)
    private String lieuEntreeMadagascar;

    @Column(name = "date_donnation", nullable = false)
    private LocalDate dateDonnation;

    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Demandeur getDemandeur() { return demandeur; }
    public void setDemandeur(Demandeur demandeur) { this.demandeur = demandeur; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public LocalDate getDateArriveeMadagascar() { return dateArriveeMadagascar; }
    public void setDateArriveeMadagascar(LocalDate dateArriveeMadagascar) { this.dateArriveeMadagascar = dateArriveeMadagascar; }

    public String getLieuEntreeMadagascar() { return lieuEntreeMadagascar; }
    public void setLieuEntreeMadagascar(String lieuEntreeMadagascar) { this.lieuEntreeMadagascar = lieuEntreeMadagascar; }

    public LocalDate getDateDonnation() { return dateDonnation; }
    public void setDateDonnation(LocalDate dateDonnation) { this.dateDonnation = dateDonnation; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }
}
