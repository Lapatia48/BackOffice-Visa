package framework.visa.service;

import framework.visa.entity.CarteResident;
import framework.visa.entity.Demandeur;
import framework.visa.entity.DemandeurVisaCarteResident;
import framework.visa.entity.Etat;
import framework.visa.entity.Visa;
import framework.visa.repository.CarteResidentRepository;
import framework.visa.repository.EtatRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import framework.visa.repository.DemandeurVisaCarteResidentRepository;

@Service
public class CarteResidentService {
    private static final String CARTE_RESIDENT_PREFIX = "CR";
    private static final String ETAT_DUPLICATA = "duplicata";
    private final CarteResidentRepository repository;
    private final DemandeurVisaCarteResidentRepository demandeurVisaCarteResidentRepository;
    private final EtatRepository etatRepository;

    public CarteResidentService(
            CarteResidentRepository repository,
            DemandeurVisaCarteResidentRepository rep,
            EtatRepository etatRepository) {
        this.repository = repository;
        this.demandeurVisaCarteResidentRepository=rep;
        this.etatRepository = etatRepository;
    }
    public String generateNextCarteResidentNumero() {
                int nextNumber = repository.findFirstByOrderByIdDesc()
                                .map(CarteResident::getNumero)
                                .map(this::extractNumeroSequence)
                                .orElse(0) + 1;
                return CARTE_RESIDENT_PREFIX + String.format("%04d", nextNumber);
    }

    private int extractNumeroSequence(String numero) {
                if (numero == null || numero.isBlank()) {
                        return 0;
                }

                String digits = numero.replaceAll("\\D", "");
                if (digits.isEmpty()) {
                        return 0;
                }

                try {
                        return Integer.parseInt(digits);
                } catch (NumberFormatException exception) {
                        return 0;
                }
        }


    public List<CarteResident> findAll() {
        return repository.findAll();
    }

    public CarteResident findFirstByDemandeurIdOrderByIdDesc(Integer demandeurId){
        return repository.findFirstByDemandeurIdOrderByIdDesc(demandeurId).orElse(null);
    }

    public CarteResident creerDuplicata(Demandeur demandeur,Visa visa)
    {
        CarteResident carteResident = new CarteResident();
                carteResident.setNumero(generateNextCarteResidentNumero());
                carteResident.setDateDonnation(visa.getDateDebut() == null ? LocalDate.now() : visa.getDateDebut());
                carteResident.setDateExpiration(visa.getDateFin() == null ? LocalDate.now() : visa.getDateFin());
                carteResident.setDemandeur(demandeur);
                carteResident.setEtat(resolveEtat(ETAT_DUPLICATA));
                carteResident = repository.save(carteResident);

                DemandeurVisaCarteResident link = new DemandeurVisaCarteResident();
                link.setDemandeur(demandeur);
                link.setVisa(visa);
                link.setCarteResident(carteResident);
                demandeurVisaCarteResidentRepository.save(link);
            
        return carteResident;
    }

    private Etat resolveEtat(String libelle) {
        return etatRepository.findFirstByLibelleIgnoreCase(libelle)
                .orElseGet(() -> {
                    Etat etat = new Etat();
                    etat.setLibelle(libelle);
                    return etatRepository.save(etat);
                });
    }
}
