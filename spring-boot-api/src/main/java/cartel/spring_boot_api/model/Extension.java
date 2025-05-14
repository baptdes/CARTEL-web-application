package cartel.spring_boot_api.model;

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
    
    @Column(nullable = false)
    private String name;
   
    // Extansion creators
    @Column(nullable = false)
    @JsonIgnore
    @ManyToMany
    private Collection<AuthorGame> creators;

    @JoinColumn(nullable = false)
    @JsonIgnore
    @ManyToOne
    private PublisherGame publisher;
    
    // Game associated with the extension
    @JoinColumn(nullable = false)
    @JsonIgnore
    @ManyToOne
    private Game game;

    public Extension() {
    }

    public Extension(String name, Collection<AuthorGame> creators, PublisherGame publisher,
            Integer publicationYear, Languages langue, Game game,String barcode) {
        super(barcode,name,publicationYear,langue);
        this.creators = creators;
        this.publisher = publisher;
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<AuthorGame> getCreators() {
        return creators;
    }

    public void setCreators(Collection<AuthorGame> creators) {
        this.creators = creators;
    }

    public PublisherGame getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherGame publisher) {
        this.publisher = publisher;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
