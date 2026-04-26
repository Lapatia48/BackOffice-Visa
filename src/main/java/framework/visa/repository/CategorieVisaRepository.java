package framework.visa.repository;

import framework.visa.entity.CategorieVisa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategorieVisaRepository extends JpaRepository<CategorieVisa, Integer> {
	Optional<CategorieVisa> findFirstByLibelleIgnoreCase(String libelle);
}
