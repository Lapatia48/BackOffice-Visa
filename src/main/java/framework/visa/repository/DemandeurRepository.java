package framework.visa.repository;

import framework.visa.entity.Demandeur;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DemandeurRepository extends JpaRepository<Demandeur, Integer> {
    
    @Query("""
        select d 
        from Demandeur d 
        where lower(trim(d.nom)) = lower(trim(:nom))
        and lower(trim(d.prenom)) = lower(trim(:prenom))
        and d.dateNaissance = :dateNaissance
        and d.nationalite.id = :nationalite
        """)
    Optional<Demandeur> findDemandeurInfos(@Param("nom") String nom,@Param("prenom") String prenom,@Param("dateNaissance") LocalDate dateNaissance,@Param("nationalite") Integer nationalite);   

}
