package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.LoanByCartel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanByCartelRepository extends JpaRepository<LoanByCartel, Long>, JpaSpecificationExecutor<LoanByCartel> {
	
	public List<LoanByCartel> findAll();
	
	public List<LoanByCartel> findById(long id);
	
	public void deleteById(long id);
}
