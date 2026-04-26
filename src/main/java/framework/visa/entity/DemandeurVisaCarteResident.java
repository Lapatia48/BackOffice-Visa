package framework.visa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "Demandeur_visa_carte_resident",
    uniqueConstraints = {
        @UniqueConstraint(name = "ux_demandeur_visa_carte_resident_visa", columnNames = "id_visa"),
        @UniqueConstraint(name = "ux_demandeur_visa_carte_resident_carte", columnNames = "id_carte_resident")
    }
)
public class DemandeurVisaCarteResident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_demandeur", nullable = false)
    private Demandeur demandeur;

    @ManyToOne
    @JoinColumn(name = "id_visa", nullable = false)
    private Visa visa;

    @ManyToOne
    @JoinColumn(name = "id_carte_resident", nullable = false)
    private CarteResident carteResident;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Demandeur getDemandeur() { return demandeur; }
    public void setDemandeur(Demandeur demandeur) { this.demandeur = demandeur; }

    public Visa getVisa() { return visa; }
    public void setVisa(Visa visa) { this.visa = visa; }

    public CarteResident getCarteResident() { return carteResident; }
    public void setCarteResident(CarteResident carteResident) { this.carteResident = carteResident; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
