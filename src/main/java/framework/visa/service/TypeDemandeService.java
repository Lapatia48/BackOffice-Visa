package framework.visa.service;

import framework.visa.entity.TypeDemande;
import framework.visa.repository.TypeDemandeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TypeDemandeService {
    private final TypeDemandeRepository repository;
    public TypeDemandeService(TypeDemandeRepository repository) {
        this.repository = repository;
    }
    public List<TypeDemande> findAll() {
        return repository.findAll();
    }
}
