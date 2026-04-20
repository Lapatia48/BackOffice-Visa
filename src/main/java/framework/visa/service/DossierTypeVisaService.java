package framework.visa.service;

import framework.visa.entity.DossierTypeVisa;
import framework.visa.repository.DossierTypeVisaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DossierTypeVisaService {
    private final DossierTypeVisaRepository repository;

    public DossierTypeVisaService(DossierTypeVisaRepository repository) {
        this.repository = repository;
    }

    public List<DossierTypeVisa> findAll() {
        return repository.findAll();
    }
}
