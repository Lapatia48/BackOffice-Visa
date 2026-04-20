package framework.visa.service;

import framework.visa.entity.Dossier;
import framework.visa.entity.TypeDemande;
import framework.visa.repository.DossierRepository;
import framework.visa.repository.DossierTypeVisaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DossierService {
    private final DossierRepository dossierRepository;
    private final DossierTypeVisaRepository dossierTypeVisaRepository;

    public DossierService(DossierRepository dossierRepository, DossierTypeVisaRepository dossierTypeVisaRepository) {
        this.dossierRepository = dossierRepository;
        this.dossierTypeVisaRepository = dossierTypeVisaRepository;
    }

    public List<Dossier> findAll() {
        return dossierRepository.findAll();
    }

    public List<Dossier> findCommonDossiers() {
        return dossierTypeVisaRepository.findCommonDossiers();
    }

    public List<Dossier> findDossiersByType(Integer typeId) {
        return dossierTypeVisaRepository.findDossiersByTypeId(typeId);
    }

    public List<TypeDemande> findAvailableTypes() {
        return dossierTypeVisaRepository.findDistinctTypes();
    }
}
