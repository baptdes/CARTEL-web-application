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
@Table(name = "jeu")
@PrimaryKeyJoinColumn( name = "barcode" )
public class JDS extends Item{


    //énumération des catégories d'un jeu de société
    @Transient
    public enum CategoryJDS {CARTES, PLATEAU, BLUFF, COOPERATIF, JEUSOIREE, COMPETITIF, GRANDJEU, TRIVIA, ADRESSE, ASYMETRIQUE, SE_JOUE_PARTOUT}


    //liste des attributs

    //temps de jeu moyen
    @Column(nullable = false)
    private String avgplaytime;

    //nombre minimum de joueur
    @Column(nullable = false)
    private int nbrplmin;

    //nombre maximum de joueur
    @Column(nullable = false)
    private int nbrplmax;
    
    //nom du jeu
    @Column(nullable = false)
    private String name;

    //catégorie du jeu
    private Collection<CategoryJDS> category;
   
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


    //extensions
    @OneToMany(mappedBy ="game")
    private Collection<Extension> extensions;

    public JDS() {
    }

    public JDS(String avgplaytime, int nbrplmin, int nbrplmax, String name,
    Collection<Creator> creator, PublisherJDS publisher, Integer publicationYear, Langues langue,String barcode, Collection<CategoryJDS> category) {
        super(barcode,name,publicationYear,langue);
        this.avgplaytime = avgplaytime;
        this.nbrplmin = nbrplmin;
        this.nbrplmax = nbrplmax;
        this.creator = creator;
        this.publisher = publisher;
        this.category = category;
    }



    public String getAvgplaytime() {
        return avgplaytime;
    }

    public void setAvgplaytime(String avgplaytime) {
        this.avgplaytime = avgplaytime;
    }

    public int getNbrplmin() {
        return nbrplmin;
    }

    public void setNbrplmin(int nbrplmin) {
        this.nbrplmin = nbrplmin;
    }

    public int getNbrplmax() {
        return nbrplmax;
    }

    public void setNbrplmax(int nbrplmax) {
        this.nbrplmax = nbrplmax;
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

    public Collection<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Collection<Extension> extensions) {
        this.extensions = extensions;
    }

    public Collection<CategoryJDS> getCategory() {
        return category;
    }

    public void setCategory(Collection<CategoryJDS> category) {
        this.category = category;
    }
    

}
