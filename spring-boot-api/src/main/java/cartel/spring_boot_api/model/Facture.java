package cartel.spring_boot_api.model;

import java.time.LocalDateTime;
import java.util.Collection;

import jakarta.persistence.*;

@Entity
@Table(name = "facture")
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Column(nullable = false)
    private String filepath;

    private LocalDateTime updatedAt;
    
    @ManyToMany
    private Collection<ItemCopy> copies;  // Changed from Item to ItemCopy

    public Facture() {
    }

    public Facture(String filename, String filepath) {
        this.filename = filename;
        this.filepath = filepath;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { 
        return id; 
    }
    
    public String getFilename() { 
        return filename;
    }
    
    public void setFilename(String filename) {
         this.filename = filename; 
    }

    public Collection<ItemCopy> getCopies() {  // Changed from getItems
        return copies;
    }

    public void setCopies(Collection<ItemCopy> copies) {  // Changed from setItems
        this.copies = copies;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}