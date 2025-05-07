package cartel.spring_boot_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.util.Collection;

import org.springframework.security.core.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "books")
@PrimaryKeyJoinColumn( name = "barcode" )
public class Book extends Item{

    //énumération des différents format d'un livre
    @Transient
    public enum FormatBook {MANGA,BD,LIVRE}; 

    //énumération des genres d'un livre
    @Transient
    public enum GenreBook {ROMAN, POESIE, THEATRE, ESSAI, BD, MANGA, FANTASY, SCIENCE_FICTION, FANTASTIQUE, POLAR, POLICIER, THRILLER, 
                            HISTORIQUE, BIOGRAPHIE, AUTOBIOGRAPHIE, HUMOUR, HORREUR, AVENTURE, JEUNESSE, CONTES,
                            NOUVELLES, DRAME, PHILOSOPHIE, RELIGION, EDUCATIF, WESTERN, DYSTOPIE, SHONEN, SEINEN, SHOJO, COMICS, FRANCOBELGE}

   
    //auteurs du livre
    @Column(nullable = false)
    @ManyToMany
    private Collection<AuthorBook> author;

    //éditeur du livre
    @JoinColumn(nullable = false)
    @ManyToOne
    private PublisherBook publisher;

    //illustrateur du livre
    @ManyToMany
    private Collection<Illustrator> illustrator;
    
    
    //format du livre
    @Column(nullable = false)
    private FormatBook format;

    //genre du livre
    private Collection<GenreBook> genre;

    //volume
    private Integer tome;

    //serie
    @JsonIgnore
    @ManyToOne
    private Serie serie;

    public Book(){

    }
    
    // Constructor with required fields
    public Book(String isbn, String title, Collection<AuthorBook> author, PublisherBook publisher, Integer publicationYear,
    FormatBook format,Langues lang) {
        super(isbn,title,publicationYear,lang);
        this.author = author;
        this.publisher = publisher;
        this.format = format;  
    }

    public Collection<AuthorBook> getAuthor() {
        return author;
    }

    public void setAuthor(Collection<AuthorBook> author) {
        this.author = author;
    }

    public PublisherBook getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherBook publisher) {
        this.publisher = publisher;
    }

    public Collection<Illustrator> getIllustrator() {
        return illustrator;
    }

    public void setIllustrator(Collection<Illustrator> illustrator) {
        this.illustrator = illustrator;
    }

    public FormatBook getFormat() {
        return format;
    }

    public void setFormat(FormatBook format) {
        this.format = format;
    }

    public Collection<GenreBook> getGenre() {
        return genre;
    }

    public void setGenre(Collection<GenreBook> genre) {
        this.genre = genre;
    }

    public Integer getTome() {
        return tome;
    }

    public void setTome(Integer tome) {
        this.tome = tome;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

}
