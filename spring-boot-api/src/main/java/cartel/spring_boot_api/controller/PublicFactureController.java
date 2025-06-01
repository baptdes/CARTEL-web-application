package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.Facture;
import cartel.spring_boot_api.model.Game;
import cartel.spring_boot_api.repository.BookRepository;
import cartel.spring_boot_api.repository.FactureRepository;
import cartel.spring_boot_api.repository.GameRepository;

import cartel.spring_boot_api.model.Item;

import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Game;
import cartel.spring_boot_api.repository.BookRepository;
import cartel.spring_boot_api.repository.GameRepository;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.time.LocalDateTime;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/factures")
public class PublicFactureController {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GameRepository gameRepository;
    
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

@PostMapping
public ResponseEntity<?> createFacture(@RequestBody Map<String, Object> payload) {
    try {
        // Extract data from the request payload
        String filename = (String) payload.get("filename");
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) payload.get("items");
        
        // Create new facture
        Facture facture = new Facture();
        facture.setFilename(filename);
        facture.setFilepath(""); // Set default or leave empty
        facture.setUpdatedAt(LocalDateTime.now());
        
        // Initialize items collection if null
        if (facture.getItems() == null) {
            facture.setItems(new ArrayList<>());
        }
        
        // For debugging
        System.out.println("Creating facture with name: " + filename);
        System.out.println("Items data: " + itemsData);
        
        // Save facture first to generate ID
        facture = factureRepository.save(facture);
        
        // Process items - you'll need to add the relevant repositories here
        if (itemsData != null && !itemsData.isEmpty()) {
            Collection<Item> items = new ArrayList<>();
            
            for (Map<String, Object> itemData : itemsData) {
                String itemId = (String) itemData.get("itemId");
                String itemType = (String) itemData.get("itemType");
                
                System.out.println("Processing item: " + itemId + " of type " + itemType);
                
                // Find the item based on type and ID
                Item item = null;
                if ("BOOK".equals(itemType)) {
                    Optional<Book> bookOpt = bookRepository.findById(itemId);
                    if (bookOpt.isPresent()) {
                        item = bookOpt.get();
                        System.out.println("Found book: " + item.getName() + " with barcode: " + item.getBarcode());
                    } else {
                        System.out.println("Book with ID " + itemId + " not found");
                    }
                } else if ("GAME".equals(itemType)) {
                    Optional<Game> gameOpt = gameRepository.findById(itemId);
                    if (gameOpt.isPresent()) {
                        item = gameOpt.get();
                        System.out.println("Found game: " + item.getName() + " with barcode: " + item.getBarcode());
                    } else {
                        System.out.println("Game with ID " + itemId + " not found");
                    }
                }
                
                if (item != null) {
                    items.add(item);
                }
            }
            
            // Set items and save again
            facture.setItems(items);
            facture = factureRepository.save(facture);
        }
        
        return ResponseEntity.ok().body(facture);
    } catch (Exception e) {
        e.printStackTrace(); // More detailed logging
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to create facture: " + e.getMessage());
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<String> deleteFacture(@PathVariable Long id) {
    try {
        if (!factureRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body("Facture not found with id: " + id);
        }
        
        factureRepository.deleteById(id);
        return ResponseEntity.ok().body("Facture deleted successfully");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to delete facture: " + e.getMessage());
    }
}
}