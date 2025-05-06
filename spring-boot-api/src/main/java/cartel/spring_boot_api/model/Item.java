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
@Table( name = "Items" )
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class Item {

    //énumération des différents langues d'un livre
    @Transient
    public enum Langues {FR,EN,JA}; 

    @Id
    private String barcode;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "objet")
    private Collection<ItemCopy> copie;

    public int getCopienumb(){
        return this.copie.size()+1;
    }

    //description petit resumé
    @Column(length = 2000)
    private String description;
    
    //image de couverture
    private String coverImage;
    
    //année de publication
    @Column(nullable = false)
    private Integer publicationYear;

    //langue
    @Column(nullable = false)
    private Langues langue;

    @OneToOne(mappedBy ="item")
    Exchange statut;

    //date de création de l'entité
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    //date de dérniére modification de l'entité
    private LocalDateTime updatedAt;

    public Item(String barcode, String name, Integer publicationYear, Langues langue) {
        this.barcode = barcode;
        this.name = name;
        this.publicationYear = publicationYear;
        this.langue = langue;
        this.createdAt = LocalDateTime.now();
    }

    public Item(){

    }

    public Exchange getStatut() {
        return statut;
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

    public Collection<ItemCopy> getCopie() {
        return copie;
    }

    public void setCopie(Collection<ItemCopy> copie) {
        this.copie = copie;
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

    public Langues getLangue() {
        return langue;
    }

    public void setLangue(Langues langue) {
        this.langue = langue;
    }

    public void setStatut(Exchange statut) {
        this.statut = statut;
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
}
