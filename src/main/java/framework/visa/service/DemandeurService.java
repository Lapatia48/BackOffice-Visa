package framework.visa.service;

import framework.visa.entity.Demandeur;
import framework.visa.repository.DemandeurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DemandeurService {
    private final DemandeurRepository repository;
    public DemandeurService(DemandeurRepository repository) {
        this.repository = repository;
    }
    public List<Demandeur> findAll() {
        return repository.findAll();
    }

    public Demandeur findById(Integer id){
        return repository.findById(id).orElse(null);
    }

    public Demandeur findDemandeurInfos(String nom,String prenom,LocalDate dateNaissance,Integer nationalite,String numero_passeport)
    {
        return repository.findDemandeurInfos(nom, prenom, dateNaissance,nationalite).orElse(null);
    }

    public Demandeur findDemandeurInfosTransfert(String nom,String prenom,LocalDate dateNaissance,Integer nationalite)
    {
        return repository.findDemandeurInfos(nom, prenom, dateNaissance,nationalite).orElse(null);
    }
}
