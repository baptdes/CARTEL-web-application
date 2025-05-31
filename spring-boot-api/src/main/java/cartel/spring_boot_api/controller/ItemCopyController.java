package cartel.spring_boot_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import cartel.spring_boot_api.dto.CopyDTO;
import cartel.spring_boot_api.service.ItemCopyService;

@RestController
@RequestMapping("/api/public/copies")
public class ItemCopyController {

    @Autowired
    private ItemCopyService itemCopyService;

    /**
     * Créer un nouvel exemplaire associé à un item par son ID
     * 
     * @param itemId L'ID de l'item pour lequel on créé un exemplaire
     * @return L'exemplaire créé
     */
    @PostMapping("/create")
    public ResponseEntity<CopyDTO> createItemCopy(@RequestParam String itemId) {
        itemCopyService.createItemCopy(itemId);
        // Assuming the created copy can be fetched by ID or similar logic
        // Adjust as necessary to return the created CopyDTO
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    /**
     * Récupérer un exemplaire par son ID
     * 
     * @param copyId L'ID de l'exemplaire à récupérer
     * @return L'exemplaire si il existe
     */
    @GetMapping("/{copyId}")
    public ResponseEntity<CopyDTO> getItemCopyById(@PathVariable Long copyId) {
        CopyDTO copy = itemCopyService.getItemCopyById(copyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Item copy not found with ID: " + copyId));
        return ResponseEntity.ok(copy);
    }

    /**
     * Supprimer un exemplaire par son ID
     * 
     * @param copyId L'ID de l'exemplaire à supprimer
     * @return Confirmation de la suppression
     */
    @DeleteMapping("/{copyId}")
    public ResponseEntity<String> deleteItemCopy(@PathVariable Long copyId) {
        itemCopyService.deleteItemCopy(copyId);
        return ResponseEntity.ok("Item copy deleted successfully");
    }

    /**
     * Recherche des exemplaires d'items avec pagination, avec ou sans recherche par nom
     * 
     * @param itemName (Optionnel) Le nom (ou partie du nom) de l'item à rechercher
     * @param pageNumber Le numéro de la page (0-based)
     * @param pageSize Le nombre d'éléments par page
     * @param asc Ordre de tri croissant (true) ou décroissant (false)
     * @param sortBy Champ sur lequel effectuer le tri
     * @return Une page d'exemplaires d'items correspondant aux critères
     */
    @GetMapping()
    public Page<CopyDTO> searchItemCopies(
            @RequestParam(required = false) String itemName,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "true") boolean asc,
            @RequestParam(defaultValue = "id") String sortBy) {
        if (itemName == null || itemName.isBlank()) {
            return itemCopyService.getAllItemCopies(pageNumber, pageSize, asc, sortBy);
        } else {
            return itemCopyService.searchItemCopiesByItemName(itemName, pageNumber, pageSize, asc, sortBy);
        }
    }

    /**
     * Est-ce que cet exemplaire est empruntable ?
     * @return true si l'exemplaire est empruntable, false sinon
     */
    @GetMapping("/{copyId}/isAvailable")
    public ResponseEntity<Boolean> isItemCopyBorrowable(@PathVariable Long copyId) {
        boolean isBorrowable = itemCopyService.isItemCopyBorrowable(copyId);
        return ResponseEntity.ok(isBorrowable);
    }

    /**
     * Est-ce que cet exemplaire est consultable ?
     * @return true si l'exemplaire est consultable, false sinon
     */
    @GetMapping("/{copyId}/isBorrowable")
    public ResponseEntity<Boolean> isItemCopyConsultable(@PathVariable Long copyId) {
        boolean isConsultable = itemCopyService.isItemCopyConsultable(copyId);
        return ResponseEntity.ok(isConsultable);
    }
}
