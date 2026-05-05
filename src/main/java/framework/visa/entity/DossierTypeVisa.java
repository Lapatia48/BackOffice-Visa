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
    @JoinColumn(name = "id_categorie_visa")
    private CategorieVisa categorieVisa;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Dossier getDossier() { return dossier; }
    public void setDossier(Dossier dossier) { this.dossier = dossier; }
    public CategorieVisa getTypeVisa() { return categorieVisa; }
    public void setTypeVisa(CategorieVisa categorieVisa) { this.categorieVisa = categorieVisa; }
}
