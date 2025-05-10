package cartel.spring_boot_api.model;

import java.time.LocalDateTime;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "extension")
@PrimaryKeyJoinColumn( name = "barcode" )
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
    
    //jeu associé
    @JoinColumn(nullable = false)
    @JsonIgnore
    @ManyToOne
    private JDS game;

    public Extension() {
    }

    public Extension(String name, Collection<Creator> creator, PublisherJDS publisher,
            Integer publicationYear, Langues langue, JDS game,String barcode) {
        super(barcode,name,publicationYear,langue);
        this.creator = creator;
        this.publisher = publisher;
        this.game = game;
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

    public JDS getGame() {
        return game;
    }

    public void setGame(JDS game) {
        this.game = game;
    }
}
