package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.dto.LoanByCartelDTO;
import cartel.spring_boot_api.model.LoanByCartel;
import cartel.spring_boot_api.service.LoanByCartelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/public/loans/byCartel")
public class LoanByCartelController {

    @Autowired
    private LoanByCartelService loanByCartelService;

    /**
     * Filtrer les prêts par Cartel avec des paramètres avancés.
     * @param pageNumber Numéro de la page pour la pagination.
     * @param pageSize Taille de la page pour la pagination.
     * @param asc Tri ascendant ou descendant.
     * @param sortBy Champ utilisé pour le tri.
     * @param itemName Nom de l'article emprunté.
     * @param borrowerFirstName Prénom de l'emprunteur.
     * @param borrowerSurname Nom de famille de l'emprunteur.
     * @param startDateBefore Filtrer les prêts avant cette date de début.
     * @param startDateAfter Filtrer les prêts après cette date de début.
     * @param endDateBefore Filtrer les prêts avant cette date de fin.
     * @param endDateAfter Filtrer les prêts après cette date de fin.
     * @param active Statut actif ou inactif du prêt.
     * @return Liste des prêts correspondant aux critères.
     */
    @GetMapping
    public List<LoanByCartelDTO> filterLoanByCartels(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "false") boolean asc,
            @RequestParam(defaultValue = "loanDate") String sortBy,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String borrowerFirstName,
            @RequestParam(required = false) String borrowerSurname,
            @RequestParam(required = false) Date startDateBefore,
            @RequestParam(required = false) Date startDateAfter,
            @RequestParam(required = false) Date endDateBefore,
            @RequestParam(required = false) Date endDateAfter,
            @RequestParam(required = false) Boolean active) {
        return loanByCartelService.filterLoanByCartel(pageNumber, pageSize, asc, sortBy, itemName,
                borrowerFirstName, borrowerSurname, startDateBefore, startDateAfter, endDateBefore, endDateAfter, active);
    }

    /**
     * Récupérer un prêt par Cartel en utilisant son ID.
     * @param loanByCartelId ID du prêt.
     * @return Objet LoanByCartel correspondant.
     */
    @GetMapping("/{loanByCartelId}")
    public LoanByCartelDTO getLoanByCartel(@PathVariable long loanByCartelId) {
        return loanByCartelService.getLoanByCartel(loanByCartelId);
    }

    /**
     * Supprimer un prêt par Cartel en utilisant son ID.
     * @param loanByCartelId ID du prêt à supprimer.
     */
    @DeleteMapping("/{loanByCartelId}")
    public void removeLoanByCartel(@PathVariable long loanByCartelId) {
        loanByCartelService.removeLoanByCartel(loanByCartelId);
    }

    /**
     * Créer un nouveau prêt par Cartel en utilisant les IDs de la personne et de l'exemplaire.
     * @param personId ID de la personne emprunteuse.
     * @param itemCopyId ID de l'exemplaire emprunté.
     * @return Réponse confirmant la création du prêt.
     */
    @PostMapping
    public ResponseEntity<String> createLoanByCartel(
            @RequestParam Long personId,
            @RequestParam Long itemCopyId) {
        loanByCartelService.createLoanByCartel(personId, itemCopyId);
        return ResponseEntity.ok("Loan created successfully");
    }

    /**
     * Marquer un prêt comme complété en utilisant son ID.
     * @param loanId ID du prêt à compléter.
     * @return Réponse confirmant la complétion du prêt.
     */
    @PostMapping("/{loanId}/complete")
    public ResponseEntity<String> completeLoanByCartel(@PathVariable long loanId) {
        loanByCartelService.completeLoanByCartel(loanId);
        return ResponseEntity.ok("Loan completed successfully");
    }
}
