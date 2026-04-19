package framework.visa.service;

import framework.visa.entity.Passeport;
import framework.visa.repository.PasseportRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PasseportService {
    private final PasseportRepository repository;
    public PasseportService(PasseportRepository repository) {
        this.repository = repository;
    }
    public List<Passeport> findAll() {
        return repository.findAll();
    }
}
