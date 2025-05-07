package cartel.spring_boot_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cartel.spring_boot_api.model.PublisherBook;

@Repository
public interface PublisherBookRepository extends JpaRepository<PublisherBook, Long>{
    List<PublisherBook> findByNameContainingIgnoreCase(String name);
    List<PublisherBook> findByNameIgnoreCase(String name);
}
