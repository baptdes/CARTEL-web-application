package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.Facture;
import cartel.spring_boot_api.model.ItemCopy;
import cartel.spring_boot_api.repository.BookRepository;
import cartel.spring_boot_api.repository.FactureRepository;
import cartel.spring_boot_api.repository.GameRepository;
import cartel.spring_boot_api.repository.ItemCopyRepository;
import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Collection;

@RestController
@RequestMapping("/api/public/factures")
public class PublicFactureController {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private ItemCopyRepository itemCopyRepository;
    
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
        List<Facture> factures = factureRepository.findByCopiesItemNameContainingIgnoreCase(itemName);
        return ResponseEntity.ok().body(factures);
    }
    
    @GetMapping("/search/item-description")
    public ResponseEntity<List<Facture>> searchByItemDescription(@RequestParam String itemDescription) {
        List<Facture> factures = factureRepository.findByCopiesItemDescriptionContainingIgnoreCase(itemDescription);
        return ResponseEntity.ok().body(factures);
    }

    @PostMapping
    public ResponseEntity<?> createFacture(@RequestBody Map<String, Object> payload) {
        try {
            // Extract data from the request payload
            String filename = (String) payload.get("filename");
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) payload.get("copies");
            
            // Create new facture
            Facture facture = new Facture();
            facture.setFilename(filename);
            facture.setFilepath(""); // Set default or leave empty
            facture.setUpdatedAt(LocalDateTime.now());
            
            // Initialize copies collection if null
            if (facture.getCopies() == null) {
                facture.setCopies(new ArrayList<>());
            }
            
            // For debugging
            System.out.println("Creating facture with name: " + filename);
            System.out.println("Copies data: " + itemsData);
            
            // Save facture first to generate ID
            facture = factureRepository.save(facture);
            
            // Process items and create item copies
            if (itemsData != null && !itemsData.isEmpty()) {
                Collection<ItemCopy> copies = new ArrayList<>();
                
                for (Map<String, Object> itemData : itemsData) {
                    String itemId = (String) itemData.get("itemId");
                    String itemType = (String) itemData.get("itemType");
                    Integer quantity = (Integer) itemData.getOrDefault("quantity", 1);
                    
                    System.out.println("Processing item: " + itemId + " of type " + itemType + " quantity: " + quantity);
                    
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
                        // Find existing copies or create new ones
                        List<ItemCopy> existingCopies = itemCopyRepository.findByObjetBarcode(item.getBarcode());
                        
                        // If we don't have enough existing copies, create new ones
                        int copiesNeeded = quantity - existingCopies.size();
                        for (int i = 0; i < copiesNeeded; i++) {
                            ItemCopy newCopy = new ItemCopy(item);
                            newCopy = itemCopyRepository.save(newCopy);
                            existingCopies.add(newCopy);
                        }
                        
                        // Add the necessary number of copies to our facture
                        for (int i = 0; i < quantity && i < existingCopies.size(); i++) {
                            copies.add(existingCopies.get(i));
                        }
                    }
                }
                
                // Set copies and save again
                facture.setCopies(copies);
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


    @GetMapping("/search-by-item")
    public ResponseEntity<List<Facture>> searchFacturesByItemName(@RequestParam String query) {
        List<Facture> matchingFactures = factureRepository.findByCopiesItemNameContainingIgnoreCase(query);
        return ResponseEntity.ok(matchingFactures);
    }
}