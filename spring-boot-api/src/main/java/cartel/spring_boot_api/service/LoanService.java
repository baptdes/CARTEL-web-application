package cartel.spring_boot_api.service;

import cartel.spring_boot_api.model.LoanByCartel;
import cartel.spring_boot_api.model.LoanToCartel;
import cartel.spring_boot_api.model.CartelPerson;
import cartel.spring_boot_api.model.ItemCopy;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoanService {
// 	    List<LoanToCartel> getAllLoanToCartel();
//
// 	    List<LoanByCartel> getAllLoanByCartel();
//
// 	    void removeLoanToCartel(long loanToCartelId);
//
// 	    void removeLoanByCartel(long loanByCartelId);
//
// 	    void createLoanToCartel(CartelPerson itemOwner, ItemCopy itemShared);
//
// 	    void createLoanToCartel(CartelPerson itemOwner, List<ItemCopy> itemsShared);
//
// 	    void createLoanByCartel(CartelPerson itemBorrower, ItemCopy itemShared);
//
// 	    void createLoanToCartel(CartelPerson itemOwner, List<ItemCopy> itemsShared);
//
// 	    List<LoanToCartel> filterLoanToCartel(int pageNumber, int pageSize,
// 	    		boolean asc, String sortBy, String itemName,
//                 String ownerFirstName, String ownerSurname,
//                 LocalDateTime loanBefore, LocalDateTime loanAfter);
	    
	    List<LoanByCartel> filterLoanByCartel(int pageNumber, int pageSize, 
	    		boolean asc, String sortBy, String itemName,
                String borrowerFirstName, String borrowerSurname);
}
