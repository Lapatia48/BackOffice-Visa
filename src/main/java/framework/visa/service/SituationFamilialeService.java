package framework.visa.service;

import framework.visa.entity.SituationFamiliale;
import framework.visa.repository.SituationFamilialeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SituationFamilialeService {
    private final SituationFamilialeRepository repository;
    public SituationFamilialeService(SituationFamilialeRepository repository) {
        this.repository = repository;
    }
    public List<SituationFamiliale> findAll() {
        return repository.findAll();
    }
}
