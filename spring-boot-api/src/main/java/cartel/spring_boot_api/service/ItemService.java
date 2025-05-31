package cartel.spring_boot_api.service;

import cartel.spring_boot_api.dto.CopyDTO;
import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.model.ItemCopy;

import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemService {
    
    /**
     * Recherche des items par nom avec pagination
     * 
     * @param name Le nom (ou partie du nom) de l'item à rechercher
     * @param pageNumber Le numéro de la page
     * @param pageSize Le nombre d'éléments par page
     * @param asc Ordre de tri croissant (true) ou décroissant (false)
     * @param sortBy Champ sur lequel effectuer le tri
     * @return Une page d'items correspondant aux critères
     */
    Page<Item> searchItemsByName(String name, int pageNumber, int pageSize, boolean asc, String sortBy);
    
    /**
     * Récupérer un item par son ID (barcode)
     * 
     * @param itemId L'ID de l'item à récupérer
     * @return L'item si trouvé
     */
    Optional<Item> getItemById(String itemId);
    
    /**
     * Récupérer tous les items (avec pagination)
     * 
     * @param pageNumber Le numéro de la page
     * @param pageSize Le nombre d'éléments par page
     * @param asc Ordre de tri croissant ou décroissant
     * @param sortBy Champ sur lequel effectuer le tri
     * @return Une page d'items
     */
    Page<Item> getAllItems(int pageNumber, int pageSize, boolean asc, String sortBy);

    /**
     * Récupérer les copies d'un item par son ID
     * @param itemId L'ID de l'item dont on veut les copies
     * @return Une page de copies d'items
     */
    Collection<CopyDTO> getItemCopiesByItemId(String itemId);
}
