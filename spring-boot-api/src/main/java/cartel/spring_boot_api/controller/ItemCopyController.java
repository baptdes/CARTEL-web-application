package cartel.spring_boot_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    /**
     * Recherche des copies d'items par le nom de l'item avec pagination
     * 
     * @param itemName Le nom (ou partie du nom) de l'item à rechercher
     * @param pageNumber Le numéro de la page (0-based)
     * @param pageSize Le nombre d'éléments par page
     * @param asc Ordre de tri croissant (true) ou décroissant (false)
     * @param sortBy Champ sur lequel effectuer le tri
     * @return Une page de copies d'items correspondant aux critères
     */
    @GetMapping("/search")
    public Page<ItemCopy> searchItemCopiesByItemName(
            @RequestParam String itemName,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "true") boolean asc,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        Pageable pageable = asc ?
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) :
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
        
        return itemCopyRepository.findByItemNameContainingIgnoreCase(itemName, pageable);
    }

    /**
     * Est-ce que cette copie est empruntable ?
     * @return true si la copie est empruntable, false sinon
     */
    @GetMapping("/{copyId}/isAvailable")
    public ResponseEntity<Boolean> isItemCopyBorrowable(@PathVariable Long copyId) {
        ItemCopy copy = itemCopyRepository.findById(copyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Item copy not found with ID: " + copyId));
        
        boolean isBorrowable = copy.isAvailable();
        return ResponseEntity.ok(isBorrowable);
    }
}
