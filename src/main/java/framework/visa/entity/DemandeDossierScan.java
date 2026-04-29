package framework.visa.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "Demande_dossier_scan")
public class DemandeDossierScan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_demande_dossier", nullable = false)
    private DemandeDossier demandeDossier;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "chemin_fichier_absolu", length = 1000)
    private String cheminFichierAbsolu;

    @Column(name = "nom_fichier", length = 255)
    private String nomFichier;

    @Column(name = "type_mime", length = 100)
    private String typeMime;

    @Column(name = "date_scan", nullable = false)
    private LocalDateTime dateScan;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DemandeDossier getDemandeDossier() {
        return demandeDossier;
    }

    public void setDemandeDossier(DemandeDossier demandeDossier) {
        this.demandeDossier = demandeDossier;
    }

    public String getCheminFichierAbsolu() {
        return cheminFichierAbsolu;
    }

    public void setCheminFichierAbsolu(String cheminFichierAbsolu) {
        this.cheminFichierAbsolu = cheminFichierAbsolu;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getTypeMime() {
        return typeMime;
    }

    public void setTypeMime(String typeMime) {
        this.typeMime = typeMime;
    }

    public LocalDateTime getDateScan() {
        return dateScan;
    }

    public void setDateScan(LocalDateTime dateScan) {
        this.dateScan = dateScan;
    }
}
