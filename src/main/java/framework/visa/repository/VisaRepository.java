package framework.visa.repository;

import framework.visa.entity.Passeport;
import framework.visa.entity.Visa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VisaRepository extends JpaRepository<Visa, Integer> {
    Optional<Visa> findFirstByPasseportDemandeurIdOrderByIdDesc(Integer demandeurId);
}
