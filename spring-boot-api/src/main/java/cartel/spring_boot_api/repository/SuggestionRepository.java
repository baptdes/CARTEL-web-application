package cartel.spring_boot_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cartel.spring_boot_api.model.Suggestion;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long>{
    List<Suggestion> findByNameContainingIgnoreCase(String name);
    List<Suggestion> findByType(Suggestion.TypeSuggestion type);
    List<Suggestion> findByNameAndType(String name, Suggestion.TypeSuggestion type);
}