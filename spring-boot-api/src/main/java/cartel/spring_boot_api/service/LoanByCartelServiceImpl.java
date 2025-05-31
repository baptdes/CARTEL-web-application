package cartel.spring_boot_api.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import cartel.spring_boot_api.dto.LoanByCartelDTO;
import cartel.spring_boot_api.model.CartelPerson;
import cartel.spring_boot_api.model.ItemCopy;
import cartel.spring_boot_api.model.LoanByCartel;
import cartel.spring_boot_api.repository.CartelPersonRepository;
import cartel.spring_boot_api.repository.ItemCopyRepository;
import cartel.spring_boot_api.repository.LoanByCartelRepository;

import static cartel.spring_boot_api.specification.LoanSpecification.*;

@Service
public class LoanByCartelServiceImpl implements LoanByCartelService {

    @Autowired
    private LoanByCartelRepository loanByCartelRepository;
    
    @Autowired
    private CartelPersonRepository cartelPersonRepository;

    @Autowired
    private ItemCopyRepository itemCopyRepository;

    @Override
    public LoanByCartelDTO getLoanByCartel(long loanByCartelId) {
        LoanByCartel loan = loanByCartelRepository.findById(loanByCartelId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanByCartelId));
        return new LoanByCartelDTO(loan);
    }

    @Override
    public void removeLoanByCartel(long loanByCartelId) {
        LoanByCartel loan = loanByCartelRepository.findById(loanByCartelId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanByCartelId));

        // Get the ItemCopy before breaking the relationship
        ItemCopy itemCopy = loan.getItemShared();
        
        if (itemCopy != null) {
            // Break the bidirectional relationship
            // This is necessary because ItemCopy has a reference back to this loan
            // that would cause issues when deleting
            itemCopy.setLoanToPerson(null);
            itemCopyRepository.save(itemCopy);
        }
        
        // Now we can safely delete the loan
        loanByCartelRepository.delete(loan);
    }

    @Override
    public void createLoanByCartel(Long personId, Long itemCopyId) {
        CartelPerson borrower = cartelPersonRepository.findById(personId)
            .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + personId));
        
        ItemCopy itemCopy = itemCopyRepository.findById(itemCopyId)
            .orElseThrow(() -> new IllegalArgumentException("Item copy not found with id: " + itemCopyId));

        // Check if the item copy is already loaned out
        if (!itemCopy.isBorrowable()) {
            throw new IllegalArgumentException("Item copy is already loaned out: " + itemCopyId);
        }
        
        LoanByCartel loan = new LoanByCartel(borrower, itemCopy);
        loanByCartelRepository.save(loan);
    }

    @Override
    public List<LoanByCartelDTO> filterLoanByCartel(
            int pageNumber, int pageSize,
            boolean asc, String sortBy, 
            String itemName,
            String borrowerFirstName, 
            String borrowerSurname,
            Date startDateBefore,
            Date startDateAfter,
            Date endDateBefore,
            Date endDateAfter,
            Boolean active) {

        Pageable page = asc ?
            PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) :
            PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());

        Specification<LoanByCartel> filters = ((itemName == null) ? filterLoanByCartelfromItemSharedByName("") :
                filterLoanByCartelfromItemSharedByName(itemName))
            .and((borrowerFirstName == null) ? null : filterLoanByCartelfromBorrowerByFirstName(borrowerFirstName))
            .and((borrowerSurname == null) ? null : filterLoanByCartelfromBorrowerBySurname(borrowerSurname))
            .and(filterLoanByCartelFromStartDateBefore(startDateBefore))
            .and(filterLoanByCartelFromStartDateAfter(startDateAfter))
            .and(filterLoanByCartelFromEndDateBefore(endDateBefore))
            .and(filterLoanByCartelFromEndDateAfter(endDateAfter))
            .and(filterLoanByCartelActive(active));

        Page<LoanByCartel> pageLoanByCartel = loanByCartelRepository.findAll(filters, page);
        return pageLoanByCartel.getContent().stream()
            .map(LoanByCartelDTO::new)
            .toList();
    }

    @Override
    public void completeLoanByCartel(long loanByCartelId) {
        LoanByCartel loan = loanByCartelRepository.findById(loanByCartelId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanByCartelId));
        loan.completeLoan();

        // Remove the item copy from the loan
        ItemCopy itemCopy = loan.getItemShared();
        itemCopy.setLoanToPerson(null);
        itemCopyRepository.save(itemCopy);

        loanByCartelRepository.save(loan);
    }
}