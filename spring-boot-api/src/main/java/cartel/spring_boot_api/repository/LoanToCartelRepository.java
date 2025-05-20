package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.LoanToCartel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanToCartelRepository extends JpaRepository<LoanToCartel, Long>, JpaSpecificationExecutor<LoanToCartel> {
    
}
