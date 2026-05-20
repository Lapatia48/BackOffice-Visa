package framework.visa.service;

import framework.visa.entity.Sexe;
import framework.visa.repository.SexeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SexeService {
    private final SexeRepository repository;

    public SexeService(SexeRepository repository) {
        this.repository = repository;
    }

    public List<Sexe> findAll() {
        return repository.findAll();
    }
}