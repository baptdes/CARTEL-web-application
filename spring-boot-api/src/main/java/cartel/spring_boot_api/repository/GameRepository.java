package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.Creator;
import cartel.spring_boot_api.model.Game;
import cartel.spring_boot_api.model.PublisherGame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, String>, JpaSpecificationExecutor<Game> {
    List<Game> findByNameContainingIgnoreCase(String name);
    List<Game> findByCreators(Creator creator);
    List<Game> findByPublisher(PublisherGame publisher);
    List<Game> findByBarcode(String barcode);
}
