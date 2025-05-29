package cartel.spring_boot_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.model.ItemCopy;
import cartel.spring_boot_api.repository.ItemCopyRepository;
import cartel.spring_boot_api.repository.ItemRepository;

/**
 * REST controller pour gérer les copies d'items
 */
@RestController
@RequestMapping("/api/public/copies")
public class ItemCopyController {

    @Autowired
    private ItemCopyRepository itemCopyRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    /**
     * Créer une nouvelle copie associée à un item par son ID
     * 
     * @param itemId L'ID de l'item pour lequel on créé une copie
     * @return La copie créée
     */
    @PostMapping("/create")
    public ResponseEntity<ItemCopy> createItemCopy(@RequestParam String itemId) {
        try {
            // Trouver l'item par son ID (barcode)
            Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Item not found with ID: " + itemId));
            
            // Créer une nouvelle copie
            ItemCopy newCopy = new ItemCopy(item);
            
            // Sauvegarder la copie
            ItemCopy savedCopy = itemCopyRepository.save(newCopy);
            
            return ResponseEntity.ok(savedCopy);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error creating item copy: " + e.getMessage());
        }
    }
    
    /**
     * Récupérer une copie par son ID
     * 
     * @param copyId L'ID de la copie à récupérer
     * @return La copie si elle existe
     */
    @GetMapping("/{copyId}")
    public ResponseEntity<ItemCopy> getItemCopyById(@PathVariable Long copyId) {
        ItemCopy copy = itemCopyRepository.findById(copyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Item copy not found with ID: " + copyId));
        
        return ResponseEntity.ok(copy);
    }

    /**
     * Supprimer une copie par son ID
     * 
     * @param copyId L'ID de la copie à supprimer
     * @return Confirmation de la suppression
     */
    @DeleteMapping("/{copyId}")
    public ResponseEntity<String> deleteItemCopy(@PathVariable Long copyId) {
        ItemCopy copy = itemCopyRepository.findById(copyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Item copy not found with ID: " + copyId));
        
        itemCopyRepository.delete(copy);
        
        return ResponseEntity.ok("Item copy deleted successfully");
    }

    /**
     * Récupérer toutes les copies
     * * @return La liste de toutes les copies
     */
    @GetMapping("/all")
    public ResponseEntity<Iterable<ItemCopy>> getAllItemCopies() {
        Iterable<ItemCopy> copies = itemCopyRepository.findAll();
        return ResponseEntity.ok(copies);
    }
}
