package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.AuthorBook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorBookRepository extends JpaRepository<AuthorBook, Long>, JpaSpecificationExecutor<AuthorBook>  {
    List<AuthorBook> findBySurnameContainingIgnoreCase(String surname);
    List<AuthorBook> findByFirstnameContainingIgnoreCase(String firstname);
    List<AuthorBook> findByFirstnameAndSurnameIgnoreCase(String firstname,String surname);
}
