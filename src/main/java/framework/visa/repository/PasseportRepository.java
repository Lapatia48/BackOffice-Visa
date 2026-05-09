package framework.visa.repository;

import framework.visa.entity.Passeport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasseportRepository extends JpaRepository<Passeport, Integer> {
	Optional<Passeport> findFirstByDemandeurIdOrderByIdDesc(Integer demandeurId);

	Optional<Passeport> findFirstByNumeroPasseportIgnoreCaseOrderByIdDesc(String numeroPasseport);
}
