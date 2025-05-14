package cartel.spring_boot_api.repository;


import cartel.spring_boot_api.model.Creator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {
    List<Creator> findByFirstnameAndSurnameIgnoreCase(String firstname, String surname);
}
