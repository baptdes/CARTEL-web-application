package cartel.spring_boot_api.service;

import cartel.spring_boot_api.dto.CopyDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ItemCopyService {

    /**
     * Créer un nouvel exemplaire associé à un item par son ID
     * 
     * @param itemId L'ID de l'item pour lequel on créé un exemplaire
     * @return L'exemplaire créé
     */
    void createItemCopy(String itemId);

    /**
     * Récupérer un exemplaire par son ID
     * 
     * @param copyId L'ID de l'exemplaire à récupérer
     * @return L'exemplaire si il existe
     */
    Optional<CopyDTO> getItemCopyById(Long copyId);

    /**
     * Supprimer un exemplaire par son ID
     * 
     * @param copyId L'ID de l'exemplaire à supprimer
     */
    void deleteItemCopy(Long copyId);

    /**
     * Recherche des exemplaires d'items par le nom de l'item avec pagination
     * 
     * @param itemName Le nom (ou partie du nom) de l'item à rechercher
     * @param pageNumber Le numéro de la page (0-based)
     * @param pageSize Le nombre d'éléments par page
     * @param asc Ordre de tri croissant (true) ou décroissant (false)
     * @param sortBy Champ sur lequel effectuer le tri
     * @return Une page d'exemplaires d'items correspondant aux critères
     */
    Page<CopyDTO> searchItemCopiesByItemName(String itemName, int pageNumber, int pageSize, boolean asc, String sortBy);

    /**
     * Récupérer tous les exemplaires d'items avec pagination
     * 
     * @param pageNumber Le numéro de la page (0-based)
     * @param pageSize Le nombre d'éléments par page
     * @param asc Ordre de tri croissant (true) ou décroissant (false)
     * @param sortBy Champ sur lequel effectuer le tri
     * @return Une page d'exemplaires d'items
     */
    Page<CopyDTO> getAllItemCopies(int pageNumber, int pageSize, boolean asc, String sortBy);

    /**
     * Vérifie si un exemplaire est empruntable
     * 
     * @param copyId L'ID de l'exemplaire
     * @return true si l'exemplaire est empruntable, false sinon
     */
    boolean isItemCopyBorrowable(Long copyId);

    /**
     * Vérifie si un exemplaire est consultable
     * 
     * @param copyId L'ID de l'exemplaire
     * @return true si l'exemplaire est consultable, false sinon
     */
    boolean isItemCopyConsultable(Long copyId);
}
