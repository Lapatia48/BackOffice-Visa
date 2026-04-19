package framework.visa.repository;

import framework.visa.entity.Nationalite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NationaliteRepository extends JpaRepository<Nationalite, Integer> {
}
