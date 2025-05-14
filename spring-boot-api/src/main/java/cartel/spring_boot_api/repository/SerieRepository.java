package cartel.spring_boot_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cartel.spring_boot_api.model.Serie;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long>, JpaSpecificationExecutor<Serie>{
    List<Serie> findBySerieNameContainingIgnoreCase(String serieName);
}
