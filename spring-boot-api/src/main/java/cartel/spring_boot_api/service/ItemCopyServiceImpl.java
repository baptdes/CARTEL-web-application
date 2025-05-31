package cartel.spring_boot_api.service;

import cartel.spring_boot_api.dto.CopyDTO;
import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.model.ItemCopy;
import cartel.spring_boot_api.repository.ItemCopyRepository;
import cartel.spring_boot_api.repository.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ItemCopyServiceImpl implements ItemCopyService {

    @Autowired
    private ItemCopyRepository itemCopyRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void createItemCopy(String itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Item not found with ID: " + itemId));
        ItemCopy newCopy = new ItemCopy(item);
        itemCopyRepository.save(newCopy);
    }

    @Override
    public Optional<CopyDTO> getItemCopyById(Long copyId) {
        return itemCopyRepository.findById(copyId)
                .map(CopyDTO::new);
    }

    @Override
    public void deleteItemCopy(Long copyId) {
        ItemCopy copy = itemCopyRepository.findById(copyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Item copy not found with ID: " + copyId));
        itemCopyRepository.delete(copy);
    }

    @Override
    public Page<CopyDTO> searchItemCopiesByItemName(String itemName, int pageNumber, int pageSize, boolean asc, String sortBy) {
        Pageable pageable = asc ?
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) :
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
        return itemCopyRepository.findByItemNameContainingIgnoreCase(itemName, pageable)
                .map(CopyDTO::new);
    }

    @Override
    public Page<CopyDTO> getAllItemCopies(int pageNumber, int pageSize, boolean asc, String sortBy) {
        Pageable pageable = asc ?
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) :
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
        return itemCopyRepository.findAll(pageable)
                .map(CopyDTO::new);
    }

    @Override
    public boolean isItemCopyBorrowable(Long copyId) {
        ItemCopy copy = itemCopyRepository.findById(copyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Item copy not found with ID: " + copyId));
        return copy.isAvailable();
    }

    @Override
    public boolean isItemCopyConsultable(Long copyId) {
        ItemCopy copy = itemCopyRepository.findById(copyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Item copy not found with ID: " + copyId));
        return copy.isBorrowable();
    }
}
