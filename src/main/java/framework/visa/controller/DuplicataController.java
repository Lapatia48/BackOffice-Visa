package framework.visa.controller;

import framework.visa.entity.CarteResident;
import framework.visa.entity.Demande;
import framework.visa.entity.DemandeDossier;
import framework.visa.entity.Demandeur;
import framework.visa.entity.Nationalite;
import framework.visa.entity.Passeport;
import framework.visa.entity.Visa;
import framework.visa.service.CarteResidentService;
import framework.visa.service.DemandeDossierService;
import framework.visa.service.DemandeurService;
import framework.visa.service.NationaliteService;
import framework.visa.service.PasseportService;
import framework.visa.service.VisaService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
public class DuplicataController {
    private final DemandeDossierService demandeDossierService;
    private final NationaliteService nationaliteService;
    private final DemandeurService demandeurService;
    private final PasseportService passeportService;
    private final VisaService visaService;
    private final CarteResidentService carteService;

    public DuplicataController(
            DemandeDossierService demandeDossierService,
            NationaliteService nationaliteService,
            DemandeurService demandeurService,
            PasseportService passeportService,
            VisaService visaService,
            CarteResidentService carteService
            ) {
        this.demandeDossierService = demandeDossierService;
        this.nationaliteService = nationaliteService;
        this.demandeurService= demandeurService;
        this.passeportService=passeportService;
        this.visaService=visaService;
        this.carteService=carteService;
    }

    @PostMapping("/rechercherData")
    public String rechercherData(Model model,
    @RequestParam String nom,
    @RequestParam String prenom,
    @RequestParam Integer nationalite,
    @RequestParam String numeroPasseport,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance
    )
    {
        //liste des nationalites
        List<Nationalite> nationalites = nationaliteService.findAll();
        model.addAttribute("nationalites", nationalites);

        Demandeur demandeur=demandeurService.findDemandeurInfos(nom, prenom, dateNaissance, nationalite,numeroPasseport);
        if (demandeur == null) {
            model.addAttribute("error", "Aucune donnee anterieure trouvee pour ce demandeur.");
            return "duplicata-aucune-donnee";
        }

        Passeport p=passeportService.findFirstByDemandeurIdOrderByIdDesc(demandeur.getId());
        Visa v=visaService.findFirstByDemandeurIdOrderByIdDesc(demandeur.getId());
        CarteResident carte=carteService.findFirstByDemandeurIdOrderByIdDesc(demandeur.getId());
        Demande demande = demandeDossierService.findLatestDemandeByDemandeurId(demandeur.getId()).orElse(null);
        List<DemandeDossier> demandeDossiers = demande == null
            ? Collections.emptyList()
            : demandeDossierService.findByDemandeId(demande.getId());

        //envoi des donnees 
        model.addAttribute("demandeur",demandeur);
        model.addAttribute("visa",v);
        model.addAttribute("passeport",p);
        model.addAttribute("carte",carte);
        model.addAttribute("demande", demande);
        model.addAttribute("demandeDossiers", demandeDossiers);
        
        return "duplicata-infos";
    }

    @PostMapping("/rechercherDataTransfert")
    public String rechercherDataTransfert(Model model,
    @RequestParam String nom,
    @RequestParam String prenom,
    @RequestParam Integer nationalite,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance
    )
    {
        //liste des nationalites
        List<Nationalite> nationalites = nationaliteService.findAll();
        model.addAttribute("nationalites", nationalites);

        Demandeur demandeur=demandeurService.findDemandeurInfosTransfert(nom, prenom, dateNaissance, nationalite);
        if (demandeur == null) {
            model.addAttribute("error", "Aucune donnee anterieure trouvee pour ce demandeur.");
            return "transfert-aucune-donnee";
        }

        Passeport p=passeportService.findFirstByDemandeurIdOrderByIdDesc(demandeur.getId());
        Visa v=visaService.findFirstByDemandeurIdOrderByIdDesc(demandeur.getId());
        CarteResident carte=carteService.findFirstByDemandeurIdOrderByIdDesc(demandeur.getId());
        Demande demande = demandeDossierService.findLatestDemandeByDemandeurId(demandeur.getId()).orElse(null);
        List<DemandeDossier> demandeDossiers = demande == null
            ? Collections.emptyList()
            : demandeDossierService.findByDemandeId(demande.getId());

        //envoi des donnees 
        model.addAttribute("demandeur",demandeur);
        model.addAttribute("visa",v);
        model.addAttribute("passeport",p);
        model.addAttribute("carte",carte);
        model.addAttribute("demande", demande);
        model.addAttribute("demandeDossiers", demandeDossiers);
        
        return "transfert-visa-infos";
    }

    @PostMapping("/creerDuplicata")
    public String creerDuplicata(@RequestParam Integer idDemandeur, RedirectAttributes redirectAttributes)
    {
        Demandeur demandeur = demandeurService.findById(idDemandeur);
        Visa visa = visaService.findFirstByDemandeurIdOrderByIdDesc(idDemandeur);

        if (demandeur == null || visa == null) {
            redirectAttributes.addFlashAttribute("error", "Impossible de creer le duplicata: demandeur ou visa introuvable.");
            return "redirect:/duplicata/aucune-donnee";
        }

        CarteResident carte = carteService.creerDuplicata(demandeur, visa);
    demandeDossierService.appendActionHistoriqueByDemandeurId(
        idDemandeur,
        "Action duplicata: creation de la carte resident " + carte.getNumero() + "."
    );
        redirectAttributes.addFlashAttribute("message", "Duplicata cree avec succes: " + carte.getNumero());

        return "redirect:/duplicata/resultat?idDemandeur=" + idDemandeur;
    }

    @GetMapping("/duplicata/aucune-donnee")
    public String aucuneDonneeDuplicata() {
        return "duplicata-aucune-donnee";
    }

    @GetMapping("/duplicata/resultat")
    public String afficherResultatDuplicata(@RequestParam Integer idDemandeur, Model model) {
        Demandeur demandeur = demandeurService.findById(idDemandeur);

        if (demandeur == null) {
            model.addAttribute("error", "Demandeur introuvable.");
            return "duplicata-resultat";
        }

        Passeport passeport = passeportService.findFirstByDemandeurIdOrderByIdDesc(idDemandeur);
        Visa visa = visaService.findFirstByDemandeurIdOrderByIdDesc(idDemandeur);
        CarteResident carte = carteService.findFirstByDemandeurIdOrderByIdDesc(idDemandeur);
        Demande demande = demandeDossierService.findLatestDemandeByDemandeurId(idDemandeur).orElse(null);

        model.addAttribute("demandeur", demandeur);
        model.addAttribute("passeport", passeport);
        model.addAttribute("visa", visa);
        model.addAttribute("carte", carte);
        model.addAttribute("demande", demande);
        return "duplicata-resultat";
    }

    @GetMapping("/transfert-visa/formulaire")
    public String afficherFormulaireTransfert(@RequestParam Integer idDemandeur, Model model) {
        Demandeur demandeur = demandeurService.findById(idDemandeur);
        Passeport ancienPasseport = passeportService.findFirstByDemandeurIdOrderByIdDesc(idDemandeur);

        if (demandeur == null) {
            model.addAttribute("error", "Demandeur introuvable pour le transfert.");
            return "transfert-aucune-donnee";
        }

        model.addAttribute("demandeur", demandeur);
        model.addAttribute("ancienPasseport", ancienPasseport);
        return "transfert-visa-formulaire";
    }

    @PostMapping("/transfert-visa/formulaire")
    public String soumettreFormulaireTransfert(
            @RequestParam Integer idDemandeur,
            @RequestParam String nouveauNumeroPasseport,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nouvelleDateDelivrance,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nouvelleDateExpiration,
            @RequestParam String nouveauPaysDelivrance,
            RedirectAttributes redirectAttributes) {
        try {
            Demandeur demandeur = demandeurService.findById(idDemandeur);
            Visa visa = visaService.findFirstByDemandeurIdOrderByIdDesc(idDemandeur);

            if (demandeur == null || visa == null) {
                redirectAttributes.addFlashAttribute("error", "Transfert impossible: demandeur ou visa introuvable.");
                return "redirect:/transfert-visa/aucune-donnee";
            }

            String ancienneReferenceVisa = visa.getReference();
            String ancienNumeroPasseport = visa.getPasseport() == null
                    ? "inconnu"
                    : visa.getPasseport().getNumeroPasseport();

            Passeport nouveauPasseport = passeportService.transfererVisaVersNouveauPasseport(
                    demandeur,
                    visa,
                    nouveauNumeroPasseport,
                    nouvelleDateDelivrance,
                    nouvelleDateExpiration,
                    nouveauPaysDelivrance
            );

            demandeDossierService.appendActionHistoriqueByDemandeurId(
                    idDemandeur,
                    "Action transfert visa: visa transfere du passeport "
                            + ancienNumeroPasseport
                            + " vers " + nouveauPasseport.getNumeroPasseport()
                            + ", reference visa " + ancienneReferenceVisa
                            + " -> " + visa.getReference() + "."
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Transfert effectue: visa bascule vers le nouveau passeport "
                            + nouveauPasseport.getNumeroPasseport()
                            + " (ref visa: " + visa.getReference() + ")"
            );
            return "redirect:/transfert-visa/formulaire?idDemandeur=" + idDemandeur;
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/transfert-visa/formulaire?idDemandeur=" + idDemandeur;
        }
    }

    @GetMapping("/transfert-visa/aucune-donnee")
    public String aucuneDonneeTransfert() {
        return "transfert-aucune-donnee";
    }
}