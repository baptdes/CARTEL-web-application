package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.ItemCopy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCopyRepository extends JpaRepository<ItemCopy, Long>, JpaSpecificationExecutor<ItemCopy> {
    
    /**
     * Recherche des copies d'items par le nom de l'item associé (insensible à la casse)
     * 
     * @param itemName Le nom (ou partie du nom) de l'item à rechercher
     * @param pageable Les informations de pagination
     * @return Une page de copies d'items correspondant au critère
     */
    @Query("SELECT ic FROM ItemCopy ic JOIN ic.objet i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :itemName, '%'))")
    Page<ItemCopy> findByItemNameContainingIgnoreCase(@Param("itemName") String itemName, Pageable pageable);
}
