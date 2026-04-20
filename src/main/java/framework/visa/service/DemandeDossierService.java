package framework.visa.service;

import framework.visa.entity.DemandeDossier;
import framework.visa.repository.DemandeDossierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemandeDossierService {
    private final DemandeDossierRepository repository;

    public DemandeDossierService(DemandeDossierRepository repository) {
        this.repository = repository;
    }

    public List<DemandeDossier> findAll() {
        return repository.findAll();
    }
}
