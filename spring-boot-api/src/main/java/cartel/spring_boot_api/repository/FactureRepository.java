package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.Facture;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    List<Facture> findByFilenameContainingIgnoreCase(String filename);
    List<Facture> findByItemsNameContainingIgnoreCase(String itemName);
    List<Facture> findByItemsBarcode(String itemBarcode);
    List<Facture> findByItemsDescriptionContainingIgnoreCase(String itemDescription);
                 
}