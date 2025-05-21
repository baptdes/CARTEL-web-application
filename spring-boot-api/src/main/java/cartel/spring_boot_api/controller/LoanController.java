package cartel.spring_boot_api.controller;

import java.time.LocalDateTime;

import cartel.spring_boot_api.model.LoanByCartel;
import cartel.spring_boot_api.model.LoanToCartel;
import cartel.spring_boot_api.model.CartelPerson;
import cartel.spring_boot_api.model.ItemCopy;

import cartel.spring_boot_api.service.LoanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Book entities through public endpoints
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
     * @return true if their is an available copy of the book owned by the cartel
     */
    @GetMapping("/check/sharable/{itemId}")
    public boolean checkItemSharable(@PathVariable String itemId){
        return loanService.checkItemBorrowable(itemId);
    }

    /**
     * Check if an item can be consulted in the Cartel
     *
     * @param barcode the item barcode
     * @return true if their is an readable copy of the book shared by somebody to the cartel
     */
    @GetMapping("/check/consultable/{itemId}")
    public boolean checkItemConsultable(@PathVariable String itemId){
        return loanService.checkItemReadable(itemId);
    }

    /**
     * Filters loan to cartel by various criteria with pagination
     *
     * @param pageNumber The page number (0-based)
     * @param pageSize Number of items per page
     * @param asc Whether to sort in ascending order
     * @param sortBy Field to sort by
     * @param itemName Filter by the name of the shared item
     * @param ownerFirstName Filter by owner first name
     * @param ownerSurname Filter by owner surname
     * @return List of loans matching the criteria
     */
    @GetMapping("/toCartel/")
    public List<LoanToCartel> filterLoanToCartels(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "false") boolean asc,
            @RequestParam(defaultValue = "loanDate") String sortBy,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String ownerFirstName,
            @RequestParam(required = false) String ownerSurname
            /*,
            @RequestParam(required = false) LocalDateTime loanBeforeDate,
            @RequestParam(required = false) LocalDateTime loanAfterDate)
            */) {

        return loanService.filterLoanToCartel(pageNumber, pageSize, asc, sortBy, itemName,
                ownerFirstName, ownerSurname);
    }

    /**
     * Filters loan by cartel by various criteria with pagination
     *
     * @param pageNumber The page number (0-based)
     * @param pageSize Number of items per page
     * @param asc Whether to sort in ascending order
     * @param sortBy Field to sort by
     * @param itemName Filter by the name of the shared item
     * @param borrowerFirstName Filter by borrower first name
     * @param borrowerSurname Filter by borrower surname
     * @return List of loans matching the criteria
     */
    @GetMapping("/byCartel/")
    public List<LoanByCartel> filterLoanByCartels(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "false") boolean asc,
            @RequestParam(defaultValue = "loanDate") String sortBy,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String borrowerFirstName,
            @RequestParam(required = false) String borrowerSurname
            /*,
            @RequestParam(required = false) LocalDateTime loanBeforeDate,
            @RequestParam(required = false) LocalDateTime loanAfterDate)
            */) {

        return loanService.filterLoanByCartel(pageNumber, pageSize, asc, sortBy, itemName,
                borrowerFirstName, borrowerSurname);
    }

    /**
     * @return List of all loan to Cartel
     */
    @GetMapping("/toCartel/all/")
    public List<LoanToCartel> getAllLoanToCartel(){
        return loanService.getAllLoanToCartel();
    }

    /**
     * @return List of all loan from Cartel
     */
    @GetMapping("/byCartel/all/")
    public List<LoanByCartel> getAllLoanByCartel(){
        return loanService.getAllLoanByCartel();
    }

    /**
     * @return nothing but delete an entry from the loan to Cartel database
     */
    @GetMapping("/toCartel/rm/")
    public void removeLoanToCartel(long loanToCartelId) {
        loanService.removeLoanToCartel(loanToCartelId);
    }

    /**
     * @return nothing but delete an entry from the loan by Cartel database
     */
    @GetMapping("/byCartel/rm/")
    public void removeLoanByCartel(long loanByCartelId) {
        loanService.removeLoanByCartel(loanByCartelId);
    }

    /**
     * @param itemOwner la personne qui prête un truc au Cartel.
     * @param itemCopy le truc préter au Cartel.
     */
    @GetMapping("/toCartel/add/")
    public void createLoanToCartel(CartelPerson itemOwner, ItemCopy itemShared){
        loanService.createLoanToCartel(itemOwner, itemShared);
    }

    /**
     * @param itemBorrower la personne qui emprunte un truc au Cartel.
     * @param itemCopy le truc préter au Cartel.
     */
    @GetMapping("/byCartel/add/")
    public void createLoanByCartel(CartelPerson itemBorrower, ItemCopy itemShared){
        loanService.createLoanByCartel(itemBorrower, itemShared);
    }

    // public void createLoanToCartel(CartelPerson itemOwner, List<ItemCopy> itemsShared);

    // public void createLoanToCartel(CartelPerson itemOwner, List<ItemCopy> itemsShared);

}
