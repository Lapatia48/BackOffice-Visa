package framework.visa.service;

import framework.visa.entity.Demandeur;
import framework.visa.entity.Passeport;
import framework.visa.entity.Visa;
import framework.visa.repository.PasseportRepository;
import framework.visa.repository.VisaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PasseportService {
    private static final DateTimeFormatter TRANSFERT_REFERENCE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final PasseportRepository repository;
    private final VisaRepository visaRepository;

    public PasseportService(PasseportRepository repository, VisaRepository visaRepository) {
        this.repository = repository;
        this.visaRepository = visaRepository;
    }
    public List<Passeport> findAll() {
        return repository.findAll();
    }

    public Passeport findFirstByDemandeurIdOrderByIdDesc(Integer id){
        return repository.findFirstByDemandeurIdOrderByIdDesc(id).orElse(null);
    }

    @Transactional
    public Passeport transfererVisaVersNouveauPasseport(
            Demandeur demandeur,
            Visa visa,
            String nouveauNumeroPasseport,
            LocalDate nouvelleDateDelivrance,
            LocalDate nouvelleDateExpiration,
            String nouveauPaysDelivrance) {
        if (demandeur == null || demandeur.getId() == null) {
            throw new IllegalArgumentException("Demandeur introuvable pour le transfert de visa.");
        }
        if (visa == null || visa.getId() == null) {
            throw new IllegalArgumentException("Visa introuvable pour le transfert.");
        }

        String numero = requireNonBlank(nouveauNumeroPasseport, "Numero du nouveau passeport");
        String pays = requireNonBlank(nouveauPaysDelivrance, "Pays de delivrance du nouveau passeport");
        LocalDate dateDelivrance = requireDate(nouvelleDateDelivrance, "Date de delivrance du nouveau passeport");
        LocalDate dateExpiration = requireDate(nouvelleDateExpiration, "Date d'expiration du nouveau passeport");

        Passeport nouveauPasseport = new Passeport();
        nouveauPasseport.setDemandeur(demandeur);
        nouveauPasseport.setNumeroPasseport(numero);
        nouveauPasseport.setDateDelivrance(dateDelivrance);
        nouveauPasseport.setDateExpiration(dateExpiration);
        nouveauPasseport.setPaysDelivrance(pays);
        nouveauPasseport = repository.save(nouveauPasseport);

        visa.setPasseport(nouveauPasseport);
        visa.setReference(visa.getReference());
        visaRepository.save(visa);

        return nouveauPasseport;
    }

    private String buildTransfertVisaReference(Integer visaId) {
        String timestamp = LocalDateTime.now().format(TRANSFERT_REFERENCE_FORMATTER);
        return "VISA-TV-" + visaId + "-" + timestamp;
    }

    private String requireNonBlank(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " est obligatoire.");
        }
        return value.trim();
    }

    private LocalDate requireDate(LocalDate value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " est obligatoire.");
        }
        return value;
    }
}
