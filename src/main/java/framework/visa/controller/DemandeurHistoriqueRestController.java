package framework.visa.controller;

import framework.visa.entity.CarteResident;
import framework.visa.entity.Demande;
import framework.visa.entity.Demandeur;
import framework.visa.entity.HistoStatutDemande;
import framework.visa.entity.Passeport;
import framework.visa.entity.Visa;
import framework.visa.repository.CarteResidentRepository;
import framework.visa.repository.PasseportRepository;
import framework.visa.service.CarteResidentService;
import framework.visa.service.DemandeDossierService;
import framework.visa.service.PasseportService;
import framework.visa.service.VisaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandeurs")
public class DemandeurHistoriqueRestController {

    private final PasseportRepository passeportRepository;
    private final CarteResidentRepository carteResidentRepository;
    private final DemandeDossierService demandeDossierService;
    private final PasseportService passeportService;
    private final CarteResidentService carteResidentService;
    private final VisaService visaService;

    public DemandeurHistoriqueRestController(
            PasseportRepository passeportRepository,
            CarteResidentRepository carteResidentRepository,
            DemandeDossierService demandeDossierService,
            PasseportService passeportService,
            CarteResidentService carteResidentService,
            VisaService visaService) {
        this.passeportRepository = passeportRepository;
        this.carteResidentRepository = carteResidentRepository;
        this.demandeDossierService = demandeDossierService;
        this.passeportService = passeportService;
        this.carteResidentService = carteResidentService;
        this.visaService = visaService;
    }

    @GetMapping("/passeport/{numeroPasseport}")
    public ResponseEntity<?> getHistoriqueByNumeroPasseport(@PathVariable String numeroPasseport) {
        Passeport passeport = passeportRepository
                .findFirstByNumeroPasseportIgnoreCaseOrderByIdDesc(numeroPasseport)
                .orElse(null);

        if (passeport == null || passeport.getDemandeur() == null || passeport.getDemandeur().getId() == null) {
            return buildNotFound("numeroPasseport", numeroPasseport);
        }

        return ResponseEntity.ok(buildResponse("numeroPasseport", numeroPasseport, passeport.getDemandeur().getId()));
    }

    @GetMapping("/carte-resident/{numeroCarteResident}")
    public ResponseEntity<?> getHistoriqueByNumeroCarteResident(@PathVariable String numeroCarteResident) {
        CarteResident carteResident = carteResidentRepository
                .findFirstByNumeroIgnoreCaseOrderByIdDesc(numeroCarteResident)
                .orElse(null);

        if (carteResident == null || carteResident.getDemandeur() == null || carteResident.getDemandeur().getId() == null) {
            return buildNotFound("numeroCarteResident", numeroCarteResident);
        }

        return ResponseEntity.ok(buildResponse("numeroCarteResident", numeroCarteResident, carteResident.getDemandeur().getId()));
    }

    @GetMapping("/demande/{numeroDemande}")
    public ResponseEntity<?> getHistoriqueByNumeroDemande(@PathVariable Integer numeroDemande) {
        Demande demande = demandeDossierService.findDemandeById(numeroDemande).orElse(null);
        if (demande == null || demande.getDemandeur() == null || demande.getDemandeur().getId() == null) {
            return buildNotFound("numeroDemande", String.valueOf(numeroDemande));
        }

        return ResponseEntity.ok(buildResponse("numeroDemande", String.valueOf(numeroDemande), demande.getDemandeur().getId()));
    }

    private ResponseEntity<Map<String, Object>> buildNotFound(String critere, String valeur) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "message", "Aucun utilisateur trouve pour ce critere.",
                "critere", critere,
                "valeur", valeur
        ));
    }

    private HistoriqueDemandeurResponse buildResponse(String typeRecherche, String valeurRecherche, Integer demandeurId) {
        Demandeur demandeur = demandeDossierService.findLatestDemandeByDemandeurId(demandeurId)
                .map(Demande::getDemandeur)
                .orElse(null);

        Passeport latestPasseport = passeportService.findFirstByDemandeurIdOrderByIdDesc(demandeurId);
        CarteResident latestCarteResident = carteResidentService.findFirstByDemandeurIdOrderByIdDesc(demandeurId);
        Visa latestVisa = visaService.findFirstByDemandeurIdOrderByIdDesc(demandeurId);

        List<DemandeItem> demandes = demandeDossierService.findDemandesByDemandeurId(demandeurId).stream()
                .map(this::toDemandeItem)
                .toList();

        List<HistoriqueItem> historiques = demandeDossierService.findHistoriquesByDemandeurIds(List.of(demandeurId))
                .getOrDefault(demandeurId, List.of())
                .stream()
                .map(this::toHistoriqueItem)
                .toList();

        return new HistoriqueDemandeurResponse(
                new RechercheInfo(typeRecherche, valeurRecherche),
                toDemandeurItem(demandeur),
                new DetailsItem(
                        toCarteResidentItem(latestCarteResident),
                        toPasseportItem(latestPasseport),
                        toVisaItem(latestVisa)
                ),
                demandes,
                historiques
        );
    }

    private DemandeurItem toDemandeurItem(Demandeur demandeur) {
        if (demandeur == null) {
            return null;
        }

        return new DemandeurItem(
                demandeur.getId(),
                demandeur.getNom(),
                demandeur.getPrenom(),
                demandeur.getDateNaissance(),
                demandeur.getLieuNaissance(),
                demandeur.getTelephone(),
                demandeur.getEmail(),
                demandeur.getAdresse(),
                demandeur.getSituationFamiliale() == null ? null : demandeur.getSituationFamiliale().getLibelle(),
                demandeur.getNationalite() == null ? null : demandeur.getNationalite().getLibelle()
        );
    }

    private CarteResidentItem toCarteResidentItem(CarteResident carteResident) {
        if (carteResident == null) {
            return null;
        }

        return new CarteResidentItem(
                carteResident.getId(),
                carteResident.getNumero(),
                carteResident.getDateDonnation(),
                carteResident.getDateExpiration(),
                carteResident.getEtat() == null ? null : carteResident.getEtat().getLibelle()
        );
    }

    private PasseportItem toPasseportItem(Passeport passeport) {
        if (passeport == null) {
            return null;
        }

        return new PasseportItem(
                passeport.getId(),
                passeport.getNumeroPasseport(),
                passeport.getDateDelivrance(),
                passeport.getDateExpiration(),
                passeport.getPaysDelivrance()
        );
    }

    private VisaItem toVisaItem(Visa visa) {
        if (visa == null) {
            return null;
        }

        return new VisaItem(
                visa.getId(),
                visa.getReference(),
                visa.getDateDebut(),
                visa.getDateFin(),
                visa.getCategorieVisa() == null ? null : visa.getCategorieVisa().getLibelle()
        );
    }

    private DemandeItem toDemandeItem(Demande demande) {
        return new DemandeItem(
                demande.getId(),
                demande.getDateDemande(),
                demande.getDateTraitement(),
                demande.getObservations(),
                demande.getMotifRejet(),
                demande.getStatut() == null ? null : demande.getStatut().getLibelle(),
                demande.getTypeDemande() == null ? null : demande.getTypeDemande().getLibelle(),
                toVisaItem(demande.getVisa())
        );
    }

    private HistoriqueItem toHistoriqueItem(HistoStatutDemande historique) {
        return new HistoriqueItem(
                historique.getId(),
                historique.getDemande() == null ? null : historique.getDemande().getId(),
                historique.getStatut() == null ? null : historique.getStatut().getLibelle(),
                historique.getDateChangement(),
                historique.getCommentaire()
        );
    }

    public record HistoriqueDemandeurResponse(
            RechercheInfo recherche,
            DemandeurItem demandeur,
            DetailsItem details,
            List<DemandeItem> demandes,
            List<HistoriqueItem> historiques) {
    }

    public record RechercheInfo(
            String type,
            String valeur) {
    }

    public record DetailsItem(
            CarteResidentItem carteResident,
            PasseportItem passeport,
            VisaItem visa) {
    }

    public record DemandeurItem(
            Integer id,
            String nom,
            String prenom,
            LocalDate dateNaissance,
            String lieuNaissance,
            String telephone,
            String email,
            String adresse,
            String situationFamiliale,
            String nationalite) {
    }

    public record CarteResidentItem(
            Integer id,
            String numero,
            LocalDate dateDonnation,
            LocalDate dateExpiration,
            String etat) {
    }

    public record PasseportItem(
            Integer id,
            String numeroPasseport,
            LocalDate dateDelivrance,
            LocalDate dateExpiration,
            String paysDelivrance) {
    }

    public record VisaItem(
            Integer id,
            String reference,
            LocalDate dateDebut,
            LocalDate dateFin,
            String categorie) {
    }

    public record DemandeItem(
            Integer id,
            LocalDate dateDemande,
            LocalDate dateTraitement,
            String observations,
            String motifRejet,
            String statut,
            String typeDemande,
            VisaItem visa) {
    }

    public record HistoriqueItem(
            Integer id,
            Integer idDemande,
            String statut,
            LocalDateTime dateChangement,
            String commentaire) {
    }
}
