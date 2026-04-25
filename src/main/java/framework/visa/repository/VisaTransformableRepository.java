package framework.visa.repository;

import framework.visa.entity.VisaTransformable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisaTransformableRepository extends JpaRepository<VisaTransformable, Integer> {
    Optional<VisaTransformable> findFirstByDemandeurIdOrderByIdDesc(Integer demandeurId);
}
