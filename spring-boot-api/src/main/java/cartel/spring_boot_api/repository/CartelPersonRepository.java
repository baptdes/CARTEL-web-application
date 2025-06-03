package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.CartelPerson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartelPersonRepository extends JpaRepository<CartelPerson, Long> {
    List<CartelPerson> findBySurnameContainingIgnoreCase(String surname);
    List<CartelPerson> findByFirstnameContainingIgnoreCase(String firstname);
    Page<CartelPerson> findByFirstnameContainingIgnoreCaseOrSurnameContainingIgnoreCase(
        String firstname, String surname, Pageable pageable);
}
