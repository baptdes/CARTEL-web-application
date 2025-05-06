package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.ItemCopy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCopyRepository extends JpaRepository<ItemCopy, Long> {
}
