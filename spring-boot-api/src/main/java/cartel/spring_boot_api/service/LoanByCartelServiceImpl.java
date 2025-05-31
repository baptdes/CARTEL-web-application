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
    public LoanByCartel getLoanByCartel(long loanByCartelId) {
        return loanByCartelRepository.findById(loanByCartelId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanByCartelId));
    }

    @Override
    public void removeLoanByCartel(long loanByCartelId){
        loanByCartelRepository.deleteById(loanByCartelId);
    }

    @Override
    public void createLoanByCartel(Long personId, Long itemCopyId) {
        CartelPerson borrower = cartelPersonRepository.findById(personId)
            .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + personId));
        
        ItemCopy itemCopy = itemCopyRepository.findById(itemCopyId)
            .orElseThrow(() -> new IllegalArgumentException("Item copy not found with id: " + itemCopyId));
        
        LoanByCartel loan = new LoanByCartel(borrower, itemCopy);
        loanByCartelRepository.save(loan);
    }

    @Override
    public List<LoanByCartel> filterLoanByCartel(
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
        return pageLoanByCartel.getContent();
    }

    @Override
    public void completeLoanByCartel(long loanByCartelId) {
        LoanByCartel loan = loanByCartelRepository.findById(loanByCartelId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanByCartelId));
        loan.completeLoan();
        loanByCartelRepository.save(loan);
    }
}