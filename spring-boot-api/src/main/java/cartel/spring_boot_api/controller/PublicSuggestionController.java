package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Suggestion;
import cartel.spring_boot_api.repository.SuggestionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/public/suggestions")
public class PublicSuggestionController {

    @Autowired
    private SuggestionRepository suggestionRepository;

    // Create a suggestion
    @PostMapping
    public ResponseEntity<Suggestion> createSuggestion(@RequestBody Suggestion suggestion) {
        suggestion.setCreatedAt(LocalDateTime.now());
        Suggestion savedSuggestion = suggestionRepository.save(suggestion);
        return new ResponseEntity<>(savedSuggestion, HttpStatus.CREATED);
    }

    // Get all suggestions
    @GetMapping
    public ResponseEntity<Iterable<Suggestion>> getAllSuggestions() {
        Iterable<Suggestion> suggestions = suggestionRepository.findAll();
        return new ResponseEntity<>(suggestions, HttpStatus.OK);
    }

    // delete a suggestion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuggestion(@PathVariable Long id) {
        if (suggestionRepository.existsById(id)) {
            suggestionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
