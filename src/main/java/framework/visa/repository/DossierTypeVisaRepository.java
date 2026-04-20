package framework.visa.repository;

import framework.visa.entity.Dossier;
import framework.visa.entity.DossierTypeVisa;
import framework.visa.entity.TypeDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DossierTypeVisaRepository extends JpaRepository<DossierTypeVisa, Integer> {
    @Query("select dtv.dossier from DossierTypeVisa dtv where dtv.typeVisa is null")
    List<Dossier> findCommonDossiers();

    @Query("select dtv.dossier from DossierTypeVisa dtv where dtv.typeVisa.id = :typeId")
    List<Dossier> findDossiersByTypeId(@Param("typeId") Integer typeId);

    @Query("select distinct dtv.typeVisa from DossierTypeVisa dtv where dtv.typeVisa is not null")
    List<TypeDemande> findDistinctTypes();
}
