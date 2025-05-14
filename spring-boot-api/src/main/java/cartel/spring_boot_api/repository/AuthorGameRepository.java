package cartel.spring_boot_api.repository;


import cartel.spring_boot_api.model.AuthorGame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorGameRepository extends JpaRepository<AuthorGame, Long> {
    List<AuthorGame> findByFirstnameAndSurnameIgnoreCase(String firstname, String surname);
}
