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
@Table(name = "jeu")
@PrimaryKeyJoinColumn( name = "idobject" )
public class JDS extends Item{

    //liste des attributs

    //temps de jeu moyen
    @Column(nullable = false)
    private String tempsdejeumoy;

    //nombre minimum de joueur
    @Column(nullable = false)
    private int nbrejrmin;

    //nombre maximum de joueur
    @Column(nullable = false)
    private int nbrejrmax;
    
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

    //extensions
    @OneToMany(mappedBy ="jeu")
    private Collection<Extension> extensions;


    //date de création de l'entité
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    //date de dérniére modification de l'entité
    private LocalDateTime updatedAt;

    public JDS() {
    }

    public JDS(String tempsdejeumoy, int nbrejrmin, int nbrejrmax, String name,
    Collection<Creator> creator, PublisherJDS publisher, Integer publicationYear, Langues langue) {
        this.tempsdejeumoy = tempsdejeumoy;
        this.nbrejrmin = nbrejrmin;
        this.nbrejrmax = nbrejrmax;
        this.name = name;
        this.creator = creator;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.langue = langue;
        this.createdAt = LocalDateTime.now();
    }

    // Pre-update method
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getTempsdejeumoy() {
        return tempsdejeumoy;
    }

    public void setTempsdejeumoy(String tempsdejeumoy) {
        this.tempsdejeumoy = tempsdejeumoy;
    }

    public int getNbrejrmin() {
        return nbrejrmin;
    }

    public void setNbrejrmin(int nbrejrmin) {
        this.nbrejrmin = nbrejrmin;
    }

    public int getNbrejrmax() {
        return nbrejrmax;
    }

    public void setNbrejrmax(int nbrejrmax) {
        this.nbrejrmax = nbrejrmax;
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

    public Collection<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Collection<Extension> extensions) {
        this.extensions = extensions;
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
