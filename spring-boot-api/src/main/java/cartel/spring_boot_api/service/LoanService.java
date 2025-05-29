package cartel.spring_boot_api.service;

import cartel.spring_boot_api.model.LoanByCartel;
import cartel.spring_boot_api.model.LoanToCartel;
import cartel.spring_boot_api.model.CartelPerson;
import cartel.spring_boot_api.model.ItemCopy;

import java.sql.Date;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface LoanService {

    public List<LoanToCartel> getAllLoanToCartel();

    public List<LoanByCartel> getAllLoanByCartel();

    public boolean checkItemBorrowable(String itemId);

    public boolean checkItemReadable(String itemId);

    public void removeLoanToCartel(long loanToCartelId);

    public void removeLoanByCartel(long loanByCartelId);

    public void createLoanToCartel(CartelPerson itemOwner, ItemCopy itemShared);

    // public void createLoanToCartel(CartelPerson itemOwner, List<ItemCopy> itemsShared);

    public void createLoanByCartel(CartelPerson itemBorrower, ItemCopy itemShared);

    // public void createLoanToCartel(CartelPerson itemOwner, List<ItemCopy> itemsShared);

	public List<LoanToCartel> filterLoanToCartel(int pageNumber, int pageSize,
		boolean asc, String sortBy, String itemName,
		String ownerFirstName, String ownerSurname);
	    
	public List<LoanByCartel> filterLoanByCartel(int pageNumber, int pageSize,
		boolean asc, String sortBy, String itemName,
		String borrowerFirstName, String borrowerSurname);
		
	public Page<CartelPerson> searchPersonByFullname(String fullname, int pageNumber, int pageSize);
	
	public CartelPerson createPerson(String firstname, String surname, String contact, Integer caution);
	
	public void createLoanToCartelById(Long personId, Long itemCopyId);
	
	// Compléter un emprunt existant (marquer comme terminé)
    public void completeLoanByCartel(long loanByCartelId);
    
    // Compléter un prêt existant (marquer comme terminé)
    public void completeLoanToCartel(long loanToCartelId);
    
    // Créer un emprunt à partir des IDs
    public void createLoanByCartelById(Long personId, Long itemCopyId);
    
    // Méthodes de filtrage améliorées
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
        Boolean active);
        
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
        Boolean active);
}
