package framework.visa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Dossier_type_visa")
public class DossierTypeVisa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_dossier", nullable = false)
    private Dossier dossier;

    @ManyToOne
    @JoinColumn(name = "id_type_visa")
    private TypeDemande typeVisa;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Dossier getDossier() { return dossier; }
    public void setDossier(Dossier dossier) { this.dossier = dossier; }
    public TypeDemande getTypeVisa() { return typeVisa; }
    public void setTypeVisa(TypeDemande typeVisa) { this.typeVisa = typeVisa; }
}
