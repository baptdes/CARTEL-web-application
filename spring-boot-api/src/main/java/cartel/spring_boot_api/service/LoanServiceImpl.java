package cartel.spring_boot_api.service;

import java.util.List;

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


import cartel.spring_boot_api.repository.LoanByCartelRepository;
import cartel.spring_boot_api.repository.LoanToCartelRepository;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanByCartelRepository loanByCartelRepository;
    
    @Autowired
    private LoanToCartelRepository loanToCartelRepository;

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
    /*
    void createLoanToCartel(CartelPerson itemOwner, List<ItemCopy> itemsShared);

    void createLoanToCartel(CartelPerson itemOwner, List<ItemCopy> itemsShared);
    */

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
}
