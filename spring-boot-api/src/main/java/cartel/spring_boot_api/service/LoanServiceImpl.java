package cartel.spring_boot_api.service;

import java.util.List;
import java.sql.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import static cartel.spring_boot_api.specification.LoanSpecification.*;

import cartel.spring_boot_api.model.LoanByCartel;
import cartel.spring_boot_api.model.LoanToCartel;
import cartel.spring_boot_api.model.ItemCopy;
import cartel.spring_boot_api.model.CartelPerson;
import cartel.spring_boot_api.model.Item;

import cartel.spring_boot_api.repository.LoanByCartelRepository;
import cartel.spring_boot_api.repository.LoanToCartelRepository;
import cartel.spring_boot_api.repository.ItemCopyRepository;
import cartel.spring_boot_api.repository.CartelPersonRepository;
import cartel.spring_boot_api.repository.ItemRepository;


@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanByCartelRepository loanByCartelRepository;
    
    @Autowired
    private LoanToCartelRepository loanToCartelRepository;

    @Autowired
    private CartelPersonRepository cartelPersonRepository;

    @Autowired
    private ItemCopyRepository itemCopyRepository;
    
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<LoanToCartel> getAllLoanToCartel(){
        return loanToCartelRepository.findAll();
    }

    @Override
    public List<LoanByCartel> getAllLoanByCartel(){
        return loanByCartelRepository.findAll();
    }

    @Override
    public void removeLoanToCartel(long loanToCartelId) {
        loanToCartelRepository.deleteById(loanToCartelId);
    }

    @Override
    public void removeLoanByCartel(long loanByCartelId){
        loanByCartelRepository.deleteById(loanByCartelId);
    }

    @Override
    public void createLoanToCartel(CartelPerson itemOwner, ItemCopy itemShared){
        LoanToCartel loan = new LoanToCartel(itemOwner, itemShared);
        loanToCartelRepository.save(loan);
    }

    @Override
    public void createLoanByCartel(CartelPerson itemBorrower, ItemCopy itemShared){
        LoanByCartel loan = new LoanByCartel(itemBorrower, itemShared);
        loanByCartelRepository.save(loan);
    }

    @Override
    public List<LoanByCartel> filterLoanByCartel(int pageNumber, int pageSize,
        boolean asc, String sortBy, String itemName,
        String borrowerFirstName, String borrowerSurname){

        Pageable page = asc ?
            PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) :
            PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());

        Specification<LoanByCartel> filters = ((itemName == null) ? filterLoanByCartelfromItemSharedByName("") :
                filterLoanByCartelfromItemSharedByName(itemName))
            .and((borrowerFirstName == null) ? null : filterLoanByCartelfromBorrowerByFirstName(borrowerFirstName))
            .and((borrowerSurname == null) ? null : filterLoanByCartelfromBorrowerBySurname(borrowerSurname));

        Page<LoanByCartel> pageLoanByCartel = loanByCartelRepository.findAll(filters, page);
        return pageLoanByCartel.getContent();
    }

    @Override
    public List<LoanToCartel> filterLoanToCartel(int pageNumber, int pageSize,
        boolean asc, String sortBy, String itemName,
        String ownerFirstName, String ownerSurname){

        Pageable page = asc ?
            PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) :
            PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());

        Specification<LoanToCartel> filters = ((itemName == null) ? filterLoanToCartelfromItemSharedByName("") :
                filterLoanToCartelfromItemSharedByName(itemName))
            .and((ownerFirstName == null) ? null : filterLoanToCartelfromOwnerByFirstName(ownerFirstName))
            .and((ownerSurname == null) ? null : filterLoanToCartelfromOwnerBySurname(ownerSurname));

        Page<LoanToCartel> pageLoanToCartel = loanToCartelRepository.findAll(filters, page);
        return pageLoanToCartel.getContent();
    }

    @Override
    public void completeLoanByCartel(long loanByCartelId) {
        LoanByCartel loan = loanByCartelRepository.findById(loanByCartelId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanByCartelId));
        loan.completeLoan();
        loanByCartelRepository.save(loan);
    }

    @Override
    public void completeLoanToCartel(long loanToCartelId) {
        LoanToCartel loan = loanToCartelRepository.findById(loanToCartelId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanToCartelId));
        loan.completeLoan();
        loanToCartelRepository.save(loan);
    }

    @Override
    public void createLoanByCartelById(Long personId, Long itemCopyId) {
        CartelPerson borrower = cartelPersonRepository.findById(personId)
            .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + personId));
        
        ItemCopy itemCopy = itemCopyRepository.findById(itemCopyId)
            .orElseThrow(() -> new IllegalArgumentException("Item copy not found with id: " + itemCopyId));
        
        LoanByCartel loan = new LoanByCartel(borrower, itemCopy);
        loanByCartelRepository.save(loan);
    }

    @Override
    public void createLoanToCartelById(Long personId, Long itemCopyId) {
        CartelPerson owner = cartelPersonRepository.findById(personId)
            .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + personId));
        
        ItemCopy itemCopy = itemCopyRepository.findById(itemCopyId)
            .orElseThrow(() -> new IllegalArgumentException("Item copy not found with id: " + itemCopyId));
        
        LoanToCartel loan = new LoanToCartel(owner, itemCopy);
        loanToCartelRepository.save(loan);
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
        return pageLoanToCartel.getContent();
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
    public LoanToCartel createLoanToCartelWithItemId(Long personId, String itemId) {
        // Trouver la personne
        CartelPerson owner = cartelPersonRepository.findById(personId)
            .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + personId));
        
        // Trouver l'item
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + itemId));
        
        // Créer une nouvelle copie de l'item
        ItemCopy newCopy = new ItemCopy(item);
        newCopy = itemCopyRepository.save(newCopy);
        
        // Créer un nouveau prêt avec la nouvelle copie
        LoanToCartel loan = new LoanToCartel(owner, newCopy);
        return loanToCartelRepository.save(loan);
    }
}
