package framework.visa.repository;

import framework.visa.entity.Passeport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasseportRepository extends JpaRepository<Passeport, Integer> {
}
