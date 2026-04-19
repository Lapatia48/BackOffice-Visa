package framework.visa.service;

import framework.visa.entity.StatutDemande;
import framework.visa.repository.StatutDemandeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StatutDemandeService {
    private final StatutDemandeRepository repository;
    public StatutDemandeService(StatutDemandeRepository repository) {
        this.repository = repository;
    }
    public List<StatutDemande> findAll() {
        return repository.findAll();
    }
}
