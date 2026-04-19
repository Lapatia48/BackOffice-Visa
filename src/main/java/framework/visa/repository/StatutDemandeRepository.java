package framework.visa.repository;

import framework.visa.entity.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatutDemandeRepository extends JpaRepository<StatutDemande, Integer> {
}
