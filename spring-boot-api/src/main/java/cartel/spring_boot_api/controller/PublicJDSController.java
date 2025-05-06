package cartel.spring_boot_api.controller;


import cartel.spring_boot_api.model.Creator;
import cartel.spring_boot_api.model.JDS;
import cartel.spring_boot_api.model.PublisherJDS;
import cartel.spring_boot_api.repository.CreatorRepository;
import cartel.spring_boot_api.repository.ExtensionRepository;
import cartel.spring_boot_api.repository.JDSRepository;
import cartel.spring_boot_api.repository.PublisherJDSRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/public/jds")
public class PublicJDSController {

    @Autowired
    private JDSRepository jdsRepository;
    @Autowired
    private CreatorRepository creatorRepository;
    @Autowired
    private ExtensionRepository extensionRepository;
    @Autowired
    private PublisherJDSRepository publisherjdsRepository;

    @GetMapping
    public List<JDS> getAllJDS() {
        return jdsRepository.findAll();
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<JDS> getBookById(@PathVariable String id) {
        Optional<JDS> jds = jdsRepository.findById(id);
        return jds.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // Update a book
    @PutMapping("/update/{id}")
    public ResponseEntity<JDS> updateJDS(@PathVariable String id, @RequestBody JDS jdsDetails) {
        return jdsRepository.findById(id)
                .map(existingjds -> {
                    existingjds.setName(jdsDetails.getName());
                    existingjds.setCreator(jdsDetails.getCreator());
                    existingjds.setDescription(jdsDetails.getDescription());
                    existingjds.setCoverImage(jdsDetails.getCoverImage());
                    existingjds.setPublicationYear(jdsDetails.getPublicationYear());
                    existingjds.setLangue(jdsDetails.getLangue());
                    existingjds.setPublisher(jdsDetails.getPublisher());
                    existingjds.setUpdatedAt(LocalDateTime.now());
                    
                    return ResponseEntity.ok(jdsRepository.save(existingjds));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a book
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        return jdsRepository.findById(id)
                .map(book -> {
                    jdsRepository.delete(book);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<JDS> searchBooks(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) Long creatorId,
    @RequestParam(required = false) Long publisherId
) {
    if (name != null && !name.isEmpty()) {
        return jdsRepository.findByNameContainingIgnoreCase(name);
    } else if (creatorId != null) {
        Optional<Creator> author = creatorRepository.findById(creatorId);
        return author.map(jdsRepository::findByCreator).orElse(List.of());
    } else if (publisherId != null) {
        Optional<PublisherJDS> publisher = publisherjdsRepository.findById(publisherId);
        return publisher.map(jdsRepository::findByPublisher).orElse(List.of());
    }

    return jdsRepository.findAll();
}
}
