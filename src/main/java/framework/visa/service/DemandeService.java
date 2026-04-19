package framework.visa.service;

import framework.visa.entity.Demande;
import framework.visa.repository.DemandeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DemandeService {
    private final DemandeRepository repository;
    public DemandeService(DemandeRepository repository) {
        this.repository = repository;
    }
    public List<Demande> findAll() {
        return repository.findAll();
    }
}
