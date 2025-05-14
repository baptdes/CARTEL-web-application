package cartel.spring_boot_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cartel.spring_boot_api.model.PublisherGame;

@Repository
public interface PublisherGameRepository extends JpaRepository<PublisherGame, Long>{
    List<PublisherGame> findByNameIgnoreCase(String name);
}
