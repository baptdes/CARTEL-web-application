package cartel.spring_boot_api.model;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table( name = "items" )
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class Item {

    @Transient
    public enum Languages {FR,EN,JA}; 

    @Id
    private String barcode;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "objet")
    private Collection<ItemCopy> copies;

    @Column(length = 2000)
    private String description;

    private String coverImage;
    
    @Column(nullable = false)
    private Integer publicationYear;

    @Column(nullable = false)
    private Languages language;

    // When the item was created
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    // When the item was last updated
    private LocalDateTime updatedAt;

    public Item(String barcode, String name, Integer publicationYear, Languages langue) {
        this.barcode = barcode;
        this.name = name;
        this.publicationYear = publicationYear;
        this.language = langue;
        this.createdAt = LocalDateTime.now();
    }

    public Item(){
        this.createdAt = LocalDateTime.now();
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<ItemCopy> getCopies() {
        return copies;
    }

    public void setCopies(Collection<ItemCopy> copies) {
        this.copies = copies;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
        this.language = language;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Pre-update method
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public int getCopyCount() {
    return (this.copies != null) ? this.copies.size() : 0;
    }
}
