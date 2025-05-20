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
import cartel.spring_boot_api.repository.LoanByCartelRepository;
import cartel.spring_boot_api.repository.LoanToCartelRepository;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanByCartelRepository loanByCartelRepository;
    
    // @Autowired
    // private LoanToCartelReopsitory loanToCartelRepository;
    //
    //  List<LoanByCartel> getAllLoanByCartel();
    //
    //  void removeLoanByCartel(long loanByCartelId);
    //
    //  void createLoanByCartel(CartelPerson itemBorrower, ItemCopy itemShared);
    //
    //  void createLoanBYCartel(CartelPerson itemBorrower, List<ItemCopy> itemsShared);

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
}
