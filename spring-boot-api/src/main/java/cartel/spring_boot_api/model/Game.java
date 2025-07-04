package cartel.spring_boot_api.model;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import org.springframework.security.core.Transient;

@Entity
@Table(name = "games")
@PrimaryKeyJoinColumn( name = "barcode" )
public class Game extends Item{

    @Transient
    public enum GameCategories {CARTES, PLATEAU, BLUFF, COOPERATIF, JEUSOIREE, COMPETITIF, GRANDJEU, TRIVIA, ADRESSE, ASYMETRIQUE, SE_JOUE_PARTOUT}

    // Average playtime (in minutes)
    @Column(nullable = false)
    private int avgPlaytime;

    // Min number of players
    @Column(nullable = false)
    private int minPlayers;

    // Max number of players
    @Column(nullable = false)
    private int maxPlayers;

    private Collection<GameCategories> categories;
    
    @Column(nullable = false)
    @JsonIgnore
    @ManyToMany
    private Collection<AuthorGame> authors;

    // Game publisher
    @JoinColumn(nullable = false)
    @JsonIgnore
    @ManyToOne
    private PublisherGame publisher;

    // Game extensions
    // One game can have multiple extensions
    @OneToMany(mappedBy ="game")
    private Collection<Extension> extensions;

    public Game() {
    }

    public Game(int avgPlaytime, int nbrplmin, int nbrplmax, String name,
    Collection<AuthorGame> creator, PublisherGame publisher, Integer publicationYear, Languages langue,String barcode, Collection<GameCategories> category) {
        super(barcode,name,publicationYear,langue);
        this.avgPlaytime = avgPlaytime;
        this.minPlayers = nbrplmin;
        this.maxPlayers = nbrplmax;
        this.authors = creator;
        this.publisher = publisher;
        this.categories = category;
    }



    public int getavgPlaytime() {
        return avgPlaytime;
    }

    public void setavgPlaytime(int avgPlaytime) {
        this.avgPlaytime = avgPlaytime;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int nbrplmin) {
        this.minPlayers = nbrplmin;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int nbrplmax) {
        this.maxPlayers = nbrplmax;
    }

    public Collection<AuthorGame> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<AuthorGame> creator) {
        this.authors = creator;
    }

    public PublisherGame getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherGame publisher) {
        this.publisher = publisher;
    }

    public Collection<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Collection<Extension> extensions) {
        this.extensions = extensions;
    }

    public Collection<GameCategories> getCategories() {
        return categories;
    }

    public void setCategories(Collection<GameCategories> category) {
        this.categories = category;
    }
}
