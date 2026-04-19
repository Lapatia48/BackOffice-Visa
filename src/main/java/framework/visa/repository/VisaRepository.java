package framework.visa.repository;

import framework.visa.entity.Visa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisaRepository extends JpaRepository<Visa, Integer> {
}
