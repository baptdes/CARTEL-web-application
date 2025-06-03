package cartel.spring_boot_api.service;

import cartel.spring_boot_api.dto.CopyDTO;
import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.model.ItemCopy;
import cartel.spring_boot_api.repository.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;
    
    @Override
    public Page<Item> searchItemsByName(String name, int pageNumber, int pageSize, boolean asc, String sortBy) {
        Pageable page = asc ?
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) :
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
        
        return itemRepository.findByNameContainingIgnoreCase(name, page);
    }
    
    @Override
    public Optional<Item> getItemById(String itemId) {
        return itemRepository.findById(itemId);
    }
    
    @Override
    public Page<Item> getAllItems(int pageNumber, int pageSize, boolean asc, String sortBy) {
        Pageable page = asc ?
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) :
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
        
        return itemRepository.findAll(page);
    }

    @Override
    public Collection<CopyDTO> getItemCopiesByItemId(String itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));
        return item.getCopies().stream()
                .map(CopyDTO::new)
                .toList();
    }
}
