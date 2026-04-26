package framework.visa.repository;

import framework.visa.entity.CarteResident;
import framework.visa.entity.Passeport;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarteResidentRepository extends JpaRepository<CarteResident, Integer> {
    Optional<CarteResident> findFirstByOrderByIdDesc();

    Optional<CarteResident> findFirstByDemandeurIdOrderByIdDesc(Integer demandeurId);

}
