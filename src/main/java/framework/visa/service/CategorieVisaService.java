package framework.visa.service;

import framework.visa.entity.CategorieVisa;
import framework.visa.repository.CategorieVisaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategorieVisaService {
    private final CategorieVisaRepository repository;
    public CategorieVisaService(CategorieVisaRepository repository) {
        this.repository = repository;
    }
    public List<CategorieVisa> findAll() {
        return repository.findAll();
    }
}
