package framework.visa.repository;

import framework.visa.entity.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DossierRepository extends JpaRepository<Dossier, Integer> {
}
