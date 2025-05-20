package cartel.spring_boot_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cartel.spring_boot_api.model.Illustrator;

@Repository
public interface IllustratorRepository extends JpaRepository<Illustrator, Long>{
    List<Illustrator> findBySurnameContainingIgnoreCase(String surname);
    List<Illustrator> findByFirstnameContainingIgnoreCase(String firstname);
    List<Illustrator> findByFirstnameAndSurnameIgnoreCase(String firstname, String surname);
}
