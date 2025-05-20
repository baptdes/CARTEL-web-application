package cartel.spring_boot_api.controller;

import java.time.LocalDateTime;

import cartel.spring_boot_api.model.LoanByCartel;
import cartel.spring_boot_api.model.LoanToCartel;
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
     * Filters loan by cartel by various criteria with pagination
     *
     * @param pageNumber The page number (0-based)
     * @param pageSize Number of items per page
     * @param asc Whether to sort in ascending order
     * @param sortBy Field to sort by
     * @param itemName Filter by the name of the shared item
     * @param borrowerFirstName Filter by borrower first name
     * @param borrowerSurname Filter by borrower surname
     * @param loanAfterDate Filter by loan date superior than loanAfterDate
     * @param loanBeforeDate Filter by loan date prior than loanBeforeDate
     * @return List of books matching the criteria
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
}
