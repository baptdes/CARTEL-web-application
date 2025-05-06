package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.Creator;
import cartel.spring_boot_api.model.JDS;
import cartel.spring_boot_api.model.PublisherJDS;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JDSRepository extends JpaRepository<JDS, String> {
    List<JDS> findByNameContainingIgnoreCase(String name);
    List<JDS> findByCreator(Creator creator);
    List<JDS> findByPublisher(PublisherJDS publisher);
}
