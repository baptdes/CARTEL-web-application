package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.Facture;
import cartel.spring_boot_api.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/factures")
public class PublicFactureController {

    @Autowired
    private FactureRepository factureRepository;
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFacture(@RequestParam("file") MultipartFile file, @RequestParam("filepath") String filepath) {
        try {
            String filename = file.getOriginalFilename();
            
            // Create a new Facture with the given filename and filepath
            Facture facture = new Facture(filename, filepath);
            factureRepository.save(facture);
            
            return ResponseEntity.ok().body("Facture metadata saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save facture: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getFacture(@PathVariable Long id) {
        Optional<Facture> factureData = factureRepository.findById(id);
        
        if (factureData.isPresent()) {
            return ResponseEntity.ok().body(factureData.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Facture not found with id: " + id);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Facture>> getAllFactures() {
        List<Facture> factures = factureRepository.findAll();
        return ResponseEntity.ok().body(factures);
    }
    
    @GetMapping("/search/filename")
    public ResponseEntity<List<Facture>> searchByFilename(@RequestParam String filename) {
        List<Facture> factures = factureRepository.findByFilenameContainingIgnoreCase(filename);
        return ResponseEntity.ok().body(factures);
    }
    
    
    @GetMapping("/search/item-name")
    public ResponseEntity<List<Facture>> searchByItemName(@RequestParam String itemName) {
        List<Facture> factures = factureRepository.findByItemsNameContainingIgnoreCase(itemName);
        return ResponseEntity.ok().body(factures);
    }
    
    @GetMapping("/search/item-description")
    public ResponseEntity<List<Facture>> searchByItemDescription(@RequestParam String itemDescription) {
        List<Facture> factures = factureRepository.findByItemsDescriptionContainingIgnoreCase(itemDescription);
        return ResponseEntity.ok().body(factures);
    }
}