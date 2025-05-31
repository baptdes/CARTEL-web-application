package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.LoanByCartel;
import cartel.spring_boot_api.model.LoanToCartel;

import cartel.spring_boot_api.service.LoanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

/**
 * REST controller for managing loans through public endpoints
 */
@RestController
@RequestMapping("/api/public/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    /**
     * Deletes a loan to cartel by its ID
     * @param loanToCartelId
     */
    @DeleteMapping("/toCartel/{loanToCartelId}")
    public void removeLoanToCartel(@PathVariable long loanToCartelId) {
        loanService.removeLoanToCartel(loanToCartelId);
    }

    /**
     * Deletes a loan by cartel by its ID
     * @param loanByCartel
     */
    @DeleteMapping("/byCartel/{loanByCartelId}")
    public void removeLoanByCartel(@PathVariable long loanByCartelId) {
        loanService.removeLoanByCartel(loanByCartelId);
    }

    /**
     * Crée un prêt au Cartel en utilisant l'id de la personne et l'id de l'item
     * 
     * @param personId L'ID de la personne propriétaire
     * @param itemCopyId L'ID de la copie de l'item à prêter
     * @return Une confirmation
     */
    @PostMapping("/toCartel/addById")
    public ResponseEntity<String> createLoanToCartelById(
            @RequestParam Long personId,
            @RequestParam Long itemCopyId) {
        
        loanService.createLoanToCartelById(personId, itemCopyId);
        return ResponseEntity.ok("Loan created successfully");
    }

    /**
     * Crée un prêt au Cartel en utilisant l'id de l'item (pas de copy)
     * Une nouvelle copy de l'item sera créée et associée au prêt
     * 
     * @param personId L'ID de la personne propriétaire
     * @param itemId L'ID (barcode) de l'item à prêter
     * @return Une confirmation
     */
    @PostMapping("/toCartel/addByItemId")
    public ResponseEntity<String> createLoanToCartelByItemId(
            @RequestParam Long personId,
            @RequestParam String itemId) {
        
        loanService.createLoanToCartelWithItemId(personId, itemId);
        return ResponseEntity.ok("Loan created successfully with new item copy");
    }

    /**
     * Filters loan to cartel by various criteria with pagination (enhanced version)
     * 
     * @param pageNumber The page number (0-based)
     * @param pageSize Number of items per page
     * @param asc Whether to sort in ascending order
     * @param sortBy Field to sort by
     * @param itemName Filter by the name of the shared item
     * @param ownerFirstName Filter by owner first name
     * @param ownerSurname Filter by owner surname
     * @param startDateBefore Filter loans starting before this date (format: yyyy-MM-dd)
     * @param startDateAfter Filter loans starting after this date (format: yyyy-MM-dd)
     * @param endDateBefore Filter loans ending before this date (format: yyyy-MM-dd)
     * @param endDateAfter Filter loans ending after this date (format: yyyy-MM-dd)
     * @param active Filter by active (true) or completed (false) loans
     * @return List of loans matching the criteria
     */
    @GetMapping("/toCartel")
    public List<LoanToCartel> filterLoanToCartelsEnhanced(
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

        return loanService.filterLoanToCartel(pageNumber, pageSize, asc, sortBy, itemName,
                ownerFirstName, ownerSurname, startDateBefore, startDateAfter, 
                endDateBefore, endDateAfter, active);
    }

    /**
     * Filters loan by cartel by various criteria with pagination (enhanced version)
     * 
     * @param pageNumber The page number (0-based)
     * @param pageSize Number of items per page
     * @param asc Whether to sort in ascending order
     * @param sortBy Field to sort by
     * @param itemName Filter by the name of the shared item
     * @param borrowerFirstName Filter by borrower first name
     * @param borrowerSurname Filter by borrower surname
     * @param startDateBefore Filter loans starting before this date (format: yyyy-MM-dd)
     * @param startDateAfter Filter loans starting after this date (format: yyyy-MM-dd)
     * @param endDateBefore Filter loans ending before this date (format: yyyy-MM-dd)
     * @param endDateAfter Filter loans ending after this date (format: yyyy-MM-dd)
     * @param active Filter by active (true) or completed (false) loans
     * @return List of loans matching the criteria
     */
    @GetMapping("/byCartel")
    public List<LoanByCartel> filterLoanByCartelsEnhanced(
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

        return loanService.filterLoanByCartel(pageNumber, pageSize, asc, sortBy, itemName,
                borrowerFirstName, borrowerSurname, startDateBefore, startDateAfter, 
                endDateBefore, endDateAfter, active);
    }

    /**
     * Crée un emprunt par le Cartel en utilisant l'id de la personne et l'id de l'item
     * 
     * @param personId L'ID de la personne empruntant
     * @param itemCopyId L'ID de la copie de l'item à emprunter
     * @return Une confirmation
     */
    @PostMapping("/byCartel/addById")
    public ResponseEntity<String> createLoanByCartelById(
            @RequestParam Long personId,
            @RequestParam Long itemCopyId) {
        
        loanService.createLoanByCartelById(personId, itemCopyId);
        return ResponseEntity.ok("Loan created successfully");
    }

    /**
     * Marque un prêt au Cartel comme terminé
     * 
     * @param loanId L'ID du prêt à terminer
     * @return Une confirmation
     */
    @PostMapping("/toCartel/{loanId}/complete")
    public ResponseEntity<String> completeLoanToCartel(@PathVariable long loanId) {
        loanService.completeLoanToCartel(loanId);
        return ResponseEntity.ok("Loan completed successfully");
    }
    
    /**
     * Marque un emprunt par le Cartel comme terminé
     * 
     * @param loanId L'ID de l'emprunt à terminer
     * @return Une confirmation
     */
    @PostMapping("/byCartel/{loanId}/complete")
    public ResponseEntity<String> completeLoanByCartel(@PathVariable long loanId) {
        loanService.completeLoanByCartel(loanId);
        return ResponseEntity.ok("Loan completed successfully");
    }
}
