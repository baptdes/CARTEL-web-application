package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    
    /**
     * Recherche des items par nom contenant une chaîne spécifique (insensible à la casse)
     * 
     * @param name La chaîne à rechercher dans le nom
     * @param pageable Les informations de pagination
     * @return Une page d'items correspondant au critère
     */
    Page<Item> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
