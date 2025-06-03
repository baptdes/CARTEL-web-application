package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.Facture;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    List<Facture> findByFilenameContainingIgnoreCase(String filename);
    
    @Query("SELECT f FROM Facture f JOIN f.copies c JOIN c.objet i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :itemName, '%'))")
    List<Facture> findByCopiesItemNameContainingIgnoreCase(@Param("itemName") String itemName);
    
    @Query("SELECT f FROM Facture f JOIN f.copies c JOIN c.objet i WHERE i.barcode = :barcode")
    List<Facture> findByCopiesItemBarcode(@Param("barcode") String barcode);
    
    @Query("SELECT f FROM Facture f JOIN f.copies c JOIN c.objet i WHERE LOWER(i.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    List<Facture> findByCopiesItemDescriptionContainingIgnoreCase(@Param("description") String description);
}