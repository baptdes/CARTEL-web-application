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
    public LoanToCartel getLoanToCartel(long loanToCartelId) {
        // Throws an exception if the loan is not found
        return loanToCartelRepository.findById(loanToCartelId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanToCartelId));
    }

    @Override
    public List<LoanToCartel> filterLoanToCartel(
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

        // Create a pageable object for pagination and sorting
        Pageable page = asc ?
            PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) :
            PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());

        // Build the specification filters dynamically based on input parameters
        Specification<LoanToCartel> filters = Specification.where(null); // Start with empty specification
        
        // Only apply item name filter if a value is provided
        if (itemName != null) {
            filters = filters.and(filterLoanToCartelfromItemSharedByName(itemName));
        }
        
        // Apply remaining filters
        filters = filters
            .and((ownerFirstName == null) ? null : filterLoanToCartelfromOwnerByFirstName(ownerFirstName))
            .and((ownerSurname == null) ? null : filterLoanToCartelfromOwnerBySurname(ownerSurname))
            .and(filterLoanToCartelFromStartDateBefore(startDateBefore))
            .and(filterLoanToCartelFromStartDateAfter(startDateAfter))
            .and(filterLoanToCartelFromEndDateBefore(endDateBefore))
            .and(filterLoanToCartelFromEndDateAfter(endDateAfter))
            .and(filterLoanToCartelActive(active));

        // Fetch the filtered loans and return the content
        Page<LoanToCartel> pageLoanToCartel = loanToCartelRepository.findAll(filters, page);
        return pageLoanToCartel.getContent();
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