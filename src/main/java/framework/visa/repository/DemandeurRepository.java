package framework.visa.repository;

import framework.visa.entity.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeurRepository extends JpaRepository<Demandeur, Integer> {
}
