package framework.visa.repository;

import framework.visa.entity.Dossier;
import framework.visa.entity.DossierTypeVisa;
import framework.visa.entity.TypeDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import framework.visa.entity.*;

import java.util.List;

public interface DossierTypeVisaRepository extends JpaRepository<DossierTypeVisa, Integer> {
    @Query("select dtv.dossier from DossierTypeVisa dtv where dtv.categorieVisa is null")
    List<Dossier> findCommonDossiers();

    @Query("select dtv.dossier from DossierTypeVisa dtv where dtv.categorieVisa.id = :typeId")
    List<Dossier> findDossiersByTypeId(@Param("typeId") Integer typeId);

    @Query("select distinct dtv.categorieVisa from DossierTypeVisa dtv where dtv.categorieVisa is not null")
    List<CategorieVisa> findDistinctCategory();
}
