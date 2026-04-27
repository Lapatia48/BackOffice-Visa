package framework.visa.service;

import framework.visa.entity.Visa;
import framework.visa.repository.VisaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VisaService {
    private final VisaRepository repository;
    public VisaService(VisaRepository repository) {
        this.repository = repository;
    }
    public List<Visa> findAll() {
        return repository.findAll();
    }

    public Visa findFirstByDemandeurIdOrderByIdDesc(Integer demandeurId){
        return repository.findFirstByPasseportDemandeurIdOrderByIdDesc(demandeurId).orElse(null);
    }
}
