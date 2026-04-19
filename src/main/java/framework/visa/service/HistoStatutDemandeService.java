package framework.visa.service;

import framework.visa.entity.HistoStatutDemande;
import framework.visa.repository.HistoStatutDemandeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HistoStatutDemandeService {
    private final HistoStatutDemandeRepository repository;
    public HistoStatutDemandeService(HistoStatutDemandeRepository repository) {
        this.repository = repository;
    }
    public List<HistoStatutDemande> findAll() {
        return repository.findAll();
    }
}
