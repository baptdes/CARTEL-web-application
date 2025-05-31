package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.LoanToCartel;
import cartel.spring_boot_api.service.LoanToCartelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/public/loans/toCartel")
public class LoanToCartelController {

    @Autowired
    private LoanToCartelService loanToCartelService;

    /**
     * Récupère un prêt à partir de son ID.
     *
     * @param loanToCartelId l'ID du prêt
     * @return le prêt correspondant à l'ID
     */
    @GetMapping("/{loanToCartelId}")
    public LoanToCartel getLoanToCartel(@PathVariable long loanToCartelId) {
        return loanToCartelService.getLoanToCartel(loanToCartelId);
    }

    /**
     * Supprime un prêt à partir de son ID.
     *
     * @param loanToCartelId l'ID du prêt
     */
    @DeleteMapping("/{loanToCartelId}")
    public void removeLoanToCartel(@PathVariable long loanToCartelId) {
        loanToCartelService.removeLoanToCartel(loanToCartelId);
    }

    /**
     * Crée un nouveau prêt pour une personne et un objet.
     *
     * @param personId l'ID de la personne
     * @param itemId l'ID de l'objet
     * @return une réponse indiquant que le prêt a été créé avec succès
     */
    @PostMapping
    public ResponseEntity<String> createLoanToCartelByItemId(
            @RequestParam Long personId,
            @RequestParam String itemId) {
        loanToCartelService.createLoanToCartel(personId, itemId);
        return ResponseEntity.ok("Loan created successfully with new item copy");
    }

    /**
     * Filtre les prêts selon différents critères.
     *
     * @param pageNumber numéro de la page pour la pagination
     * @param pageSize taille de la page
     * @param asc tri croissant ou décroissant
     * @param sortBy champ utilisé pour le tri
     * @param itemName nom de l'objet
     * @param ownerFirstName prénom du propriétaire
     * @param ownerSurname nom de famille du propriétaire
     * @param startDateBefore filtre les prêts commençant avant cette date
     * @param startDateAfter filtre les prêts commençant après cette date
     * @param endDateBefore filtre les prêts se terminant avant cette date
     * @param endDateAfter filtre les prêts se terminant après cette date
     * @param active filtre les prêts actifs ou inactifs
     * @return une liste de prêts correspondant aux critères
     */
    @GetMapping
    public List<LoanToCartel> filterLoanToCartels(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "false") boolean asc,
            @RequestParam(defaultValue = "loanDate") String sortBy,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String ownerFirstName,
            @RequestParam(required = false) String ownerSurname,
            @RequestParam(required = false) Date startDateBefore,
            @RequestParam(required = false) Date startDateAfter,
            @RequestParam(required = false) Date endDateBefore,
            @RequestParam(required = false) Date endDateAfter,
            @RequestParam(required = false) Boolean active) {
        return loanToCartelService.filterLoanToCartel(pageNumber, pageSize, asc, sortBy, itemName,
                ownerFirstName, ownerSurname, startDateBefore, startDateAfter, endDateBefore, endDateAfter, active);
    }

    /**
     * Marque un prêt comme terminé.
     *
     * @param loanId l'ID du prêt
     * @return une réponse indiquant que le prêt a été terminé avec succès
     */
    @PostMapping("/{loanId}/complete")
    public ResponseEntity<String> completeLoanToCartel(@PathVariable long loanId) {
        loanToCartelService.completeLoanToCartel(loanId);
        return ResponseEntity.ok("Loan completed successfully");
    }
}
