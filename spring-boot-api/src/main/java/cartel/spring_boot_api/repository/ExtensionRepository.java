package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.Creator;
import cartel.spring_boot_api.model.PublisherGame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cartel.spring_boot_api.model.Extension;
import java.util.List;

@Repository
public interface ExtensionRepository extends JpaRepository<Extension, String> {
    List<Extension> findByNameContainingIgnoreCase(String name);
    List<Extension> findByCreators(Creator creator);
    List<Extension> findByPublisher(PublisherGame publisher);
}
