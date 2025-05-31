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

import static cartel.spring_boot_api.specification.LoanSpecification.*;

import cartel.spring_boot_api.dto.LoanToCartelDTO;
import cartel.spring_boot_api.model.CartelPerson;
import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.model.ItemCopy;
import cartel.spring_boot_api.model.LoanToCartel;
import cartel.spring_boot_api.repository.CartelPersonRepository;
import cartel.spring_boot_api.repository.ItemCopyRepository;
import cartel.spring_boot_api.repository.ItemRepository;
import cartel.spring_boot_api.repository.LoanToCartelRepository;

@Service
class LoanToCartelServiceImpl implements LoanToCartelService {

    @Autowired
    private LoanToCartelRepository loanToCartelRepository;

    @Autowired
    private CartelPersonRepository cartelPersonRepository;

    @Autowired
    private ItemCopyRepository itemCopyRepository;
    
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public LoanToCartelDTO getLoanToCartel(long loanToCartelId) {
        LoanToCartel loan = loanToCartelRepository.findById(loanToCartelId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanToCartelId));
        return new LoanToCartelDTO(loan);
    }

    @Override
    public List<LoanToCartelDTO> filterLoanToCartel(
            int pageNumber, int pageSize,
            boolean asc, String sortBy, 
            String itemName,
            String ownerFirstName, 
            String ownerSurname,
            Date startDateBefore,
            Date startDateAfter,
            Date endDateBefore,
            Date endDateAfter,
            Boolean active) {

        Pageable page = asc ?
            PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) :
            PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());

        Specification<LoanToCartel> filters = ((itemName == null) ? filterLoanToCartelfromItemSharedByName("") :
                filterLoanToCartelfromItemSharedByName(itemName))
            .and((ownerFirstName == null) ? null : filterLoanToCartelfromOwnerByFirstName(ownerFirstName))
            .and((ownerSurname == null) ? null : filterLoanToCartelfromOwnerBySurname(ownerSurname))
            .and(filterLoanToCartelFromStartDateBefore(startDateBefore))
            .and(filterLoanToCartelFromStartDateAfter(startDateAfter))
            .and(filterLoanToCartelFromEndDateBefore(endDateBefore))
            .and(filterLoanToCartelFromEndDateAfter(endDateAfter))
            .and(filterLoanToCartelActive(active));

        Page<LoanToCartel> pageLoanToCartel = loanToCartelRepository.findAll(filters, page);
        return pageLoanToCartel.getContent().stream()
            .map(LoanToCartelDTO::new)
            .toList();
    }

    @Override
    public void createLoanToCartel(Long personId, String itemId) {
        // Find the person by ID
        CartelPerson owner = cartelPersonRepository.findById(personId)
            .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + personId));
        
        // Find the item by ID
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + itemId));
        
        // Create a new copy of the item
        ItemCopy newCopy = new ItemCopy(item);
        newCopy = itemCopyRepository.save(newCopy);
        
        // Create a new loan with the new item copy
        LoanToCartel loan = new LoanToCartel(owner, newCopy);
        loanToCartelRepository.save(loan);
    }

    @Override
    public void completeLoanToCartel(long loanToCartelId) {
        // Find the loan by ID
        LoanToCartel loan = loanToCartelRepository.findById(loanToCartelId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanToCartelId));
        
        // Complete the loan - this will also save item information internally
        loan.completeLoan();

        // Save the item information before deleting the item copy
        loan.saveItemInfo();

        // Delete the item copy associated with the loan
        ItemCopy itemCopy = loan.getItemShared();
        if (itemCopy != null) {
            loan.setItemShared(null);
            loanToCartelRepository.save(loan);
            itemCopyRepository.delete(itemCopy);
        }

        loanToCartelRepository.save(loan);
    }

    @Override
    public void removeLoanToCartel(long loanToCartelId) {
        loanToCartelRepository.deleteById(loanToCartelId);
    }
}