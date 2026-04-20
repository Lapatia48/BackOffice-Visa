package framework.visa.controller;

import framework.visa.entity.Dossier;
import framework.visa.entity.TypeDemande;
import framework.visa.service.DemandeWorkflowService;
import framework.visa.service.DossierService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    private final DossierService dossierService;
    private final DemandeWorkflowService demandeWorkflowService;

    public HomeController(DossierService dossierService, DemandeWorkflowService demandeWorkflowService) {
        this.dossierService = dossierService;
        this.demandeWorkflowService = demandeWorkflowService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/nouveau-titre")
    public String nouveauTitre(Model model) {
        List<TypeDemande> types = dossierService.findAvailableTypes();
        List<Dossier> commonDossiers = dossierService.findCommonDossiers();

        Map<Integer, List<Dossier>> typedDossiers = new LinkedHashMap<>();
        for (TypeDemande type : types) {
            typedDossiers.put(type.getId(), dossierService.findDossiersByType(type.getId()));
        }

        model.addAttribute("types", types);
        model.addAttribute("commonDossiers", commonDossiers);
        model.addAttribute("typedDossiers", typedDossiers);
        return "nouveau-titre";
    }

    @PostMapping("/nouveau-titre")
    public String submitNouveauTitre(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam String lieuNaissance,
            @RequestParam String telephone,
            @RequestParam String email,
            @RequestParam String adresse,
            @RequestParam String numeroPasseport,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDelivrance,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateExpiration,
            @RequestParam String paysDelivrance,
            @RequestParam Integer typeVisaId,
            @RequestParam(required = false) List<Integer> dossierIds,
            @RequestParam(required = false) String observations,
            RedirectAttributes redirectAttributes) {
        try {
            Integer demandeId = demandeWorkflowService.submitNouveauTitre(
                    nom,
                    prenom,
                    dateNaissance,
                    lieuNaissance,
                    telephone,
                    email,
                    adresse,
                    numeroPasseport,
                    dateDelivrance,
                    dateExpiration,
                    paysDelivrance,
                    typeVisaId,
                    dossierIds,
                    observations
            );
            redirectAttributes.addFlashAttribute("message", "Demande #" + demandeId + " enregistree avec succes.");
            return "redirect:/success";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/nouveau-titre";
        }
    }

    @GetMapping("/duplicata")
    public String duplicata() {
        return "duplicata";
    }

    @GetMapping("/transfert-visa")
    public String transfertVisa() {
        return "transfert-visa";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
