package cartel.spring_boot_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cartel.spring_boot_api.model.PublisherJDS;

@Repository
public interface PublisherJDSRepository extends JpaRepository<PublisherJDS, Long>{
    List<PublisherJDS> findByNameContainingIgnoreCase(String name);
}
