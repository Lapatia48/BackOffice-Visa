package framework.visa.service;

import framework.visa.entity.Nationalite;
import framework.visa.repository.NationaliteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NationaliteService {
    private final NationaliteRepository repository;
    public NationaliteService(NationaliteRepository repository) {
        this.repository = repository;
    }
    public List<Nationalite> findAll() {
        return repository.findAll();
    }
}
