package cartel.spring_boot_api.service;

import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.repository.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
}
