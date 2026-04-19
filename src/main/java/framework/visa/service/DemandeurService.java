package framework.visa.service;

import framework.visa.entity.Demandeur;
import framework.visa.repository.DemandeurRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DemandeurService {
    private final DemandeurRepository repository;
    public DemandeurService(DemandeurRepository repository) {
        this.repository = repository;
    }
    public List<Demandeur> findAll() {
        return repository.findAll();
    }
}
