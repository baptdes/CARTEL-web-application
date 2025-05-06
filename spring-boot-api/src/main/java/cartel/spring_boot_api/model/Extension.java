package cartel.spring_boot_api.model;

import java.time.LocalDateTime;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cartel.spring_boot_api.model.Book.Langues;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "extension")
@PrimaryKeyJoinColumn( name = "idobject" )
public class Extension extends Item{ 

    //liste des attributs
    
    //nom du jeu
    @Column(nullable = false)
    private String name;
   
    //auteurs du livre
    @Column(nullable = false)
    @JsonIgnore
    @ManyToMany
    private Collection<Creator> creator;

    //éditeur du livre
    @JoinColumn(nullable = false)
    @JsonIgnore
    @ManyToOne
    private PublisherJDS publisher;
    
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

    //jeu associé
    @JoinColumn(nullable = false)
    @JsonIgnore
    @ManyToOne
    private JDS jeu;

    //date de création de l'entité
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    //date de dérniére modification de l'entité
    private LocalDateTime updatedAt;

    public Extension() {
    }

    public Extension(String name, Collection<Creator> creator, PublisherJDS publisher,
            Integer publicationYear, Langues langue, JDS jeu) {
        this.name = name;
        this.creator = creator;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.langue = langue;
        this.jeu = jeu;
        this.createdAt = LocalDateTime.now();
    }

    // Pre-update method
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Creator> getCreator() {
        return creator;
    }

    public void setCreator(Collection<Creator> creator) {
        this.creator = creator;
    }

    public PublisherJDS getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherJDS publisher) {
        this.publisher = publisher;
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

    public JDS getJeu() {
        return jeu;
    }

    public void setJeu(JDS jeu) {
        this.jeu = jeu;
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

}
