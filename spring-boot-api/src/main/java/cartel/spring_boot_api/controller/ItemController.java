package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.service.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller pour gérer les items
 */
@RestController
@RequestMapping("/api/public/items")
public class ItemController {

    @Autowired
    private ItemService itemService;
    
    /**
     * Recherche des items par nom avec pagination
     * 
     * @param name Le nom (ou partie du nom) de l'item à rechercher
     * @param pageNumber Le numéro de la page (0-based)
     * @param pageSize Le nombre d'éléments par page
     * @param asc Ordre de tri croissant (true) ou décroissant (false)
     * @param sortBy Champ sur lequel effectuer le tri
     * @return Une liste paginée d'items correspondant aux critères
     */
    @GetMapping("/search")
    public Page<Item> searchItems(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "true") boolean asc,
            @RequestParam(defaultValue = "name") String sortBy) {
        
        return itemService.searchItemsByName(name, pageNumber, pageSize, asc, sortBy);
    }
    
    /**
     * Récupérer un item par son ID (barcode)
     * 
     * @param itemId L'ID de l'item à récupérer
     * @return L'item si trouvé
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItemById(@PathVariable String itemId) {
        return itemService.getItemById(itemId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Récupérer tous les items (avec pagination)
     * 
     * @param pageNumber Le numéro de la page
     * @param pageSize Le nombre d'éléments par page
     * @param asc Ordre de tri croissant ou décroissant
     * @param sortBy Champ sur lequel effectuer le tri
     * @return Une liste paginée d'items
     */
    @GetMapping
    public Page<Item> getAllItems(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "true") boolean asc,
            @RequestParam(defaultValue = "name") String sortBy) {
        
        return itemService.getAllItems(pageNumber, pageSize, asc, sortBy);
    }

    /**
     * Récupérer les copies d'un item par son ID
     * 
     * @param itemId L'ID de l'item dont on veut les copies
     * @return Une liste de copies d'items
     */
    @GetMapping("/{itemId}/copies")
    public ResponseEntity<?> getItemCopiesByItemId(@PathVariable String itemId) {
        return ResponseEntity.ok(itemService.getItemCopiesByItemId(itemId));
    }
}
