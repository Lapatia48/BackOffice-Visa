package framework.visa.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "type_demande_vrai")
public class TypeDemandeVrai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="libelle")
    private String libelle;

    public String getLibelle() {
        return libelle;
    }
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    public Integer getId(){return this.id;}
    public void setId(Integer i){this.id=i;}

    public TypeDemandeVrai(){}

}