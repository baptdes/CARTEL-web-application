package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.LoanByCartel;
import cartel.spring_boot_api.model.LoanToCartel;
import cartel.spring_boot_api.model.CartelPerson;
import cartel.spring_boot_api.model.ItemCopy;

import cartel.spring_boot_api.service.LoanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing loans through public endpoints
 */
@RestController
@RequestMapping("/api/public/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    /**
     * Check if an item can be borrowed from Cartel
     *
     * @param barcode the item barcode
     * @return true if their is an available copy of the item owned by the cartel
     */
    @GetMapping("/check/sharable/{itemId}")
    public boolean checkItemSharable(@PathVariable String itemId){
        return loanService.checkItemBorrowable(itemId);
    }

    /**
     * Check if an item can be consulted in the Cartel
     *
     * @param barcode the item barcode
     * @return true if their is an readable copy of the item shared by somebody to the cartel
     */
    @GetMapping("/check/readable/{itemId}")
    public boolean checkItemReadable(@PathVariable String itemId){
        return loanService.checkItemReadable(itemId);
    }

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
     * Recherche une personne par son nom complet (prénom ou nom)
     * 
     * @param fullname Le nom ou prénom à rechercher
     * @param pageNumber Le numéro de la page
     * @param pageSize La taille de la page
     * @return Une page de personnes correspondant aux critères
     */
    @GetMapping("/persons/search")
    public Page<CartelPerson> searchPersons(
            @RequestParam String fullname,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        return loanService.searchPersonByFullname(fullname, pageNumber, pageSize);
    }

    /**
     * Ajoute une nouvelle personne
     * 
     * @param personData Les données de la personne (firstname, surname, contact)
     * @return La personne créée
     */
    @PostMapping("/persons/add")
    public CartelPerson addPerson(@RequestBody Map<String, String> personData) {
        String firstname = personData.get("firstname");
        String surname = personData.get("surname");
        String contact = personData.get("contact");
        Integer caution = Integer.valueOf(personData.get("caution"));
        return loanService.createPerson(firstname, surname, contact, caution);
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
