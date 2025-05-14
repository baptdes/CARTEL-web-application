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

    @Transient
    public enum BookFormat {MANGA,BD,LIVRE};

    @Transient
    public enum BookGenre {ROMAN, POESIE, THEATRE, ESSAI, FANTASY, SCIENCE_FICTION, FANTASTIQUE, POLAR, POLICIER, THRILLER, 
                            HISTORIQUE, BIOGRAPHIE, AUTOBIOGRAPHIE, HUMOUR, HORREUR, AVENTURE, JEUNESSE, CONTES,
                            NOUVELLES, DRAME, PHILOSOPHIE, RELIGION, EDUCATIF, WESTERN, DYSTOPIE, SHONEN, SEINEN, SHOJO, COMICS, FRANCOBELGE}

    // Book author
    @Column(nullable = false)
    @ManyToMany
    private Collection<AuthorBook> authors;

    // Book publisher
    @JoinColumn(nullable = false)
    @ManyToOne
    private PublisherBook publisher;

    // Book illustrator
    @ManyToMany
    private Collection<Illustrator> illustrator;
    
    // Book format
    @Column(nullable = false)
    private BookFormat format;

    // Book genre
    private Collection<BookGenre> genre;

    // Volume number
    private Integer volumeNumber;

    // Series
    @JsonIgnore
    @ManyToOne
    private Series series;

    public Book(){

    }
    
    // Constructor with required fields
    public Book(String isbn, String title, Collection<AuthorBook> author, PublisherBook publisher, Integer publicationYear,
    BookFormat format,Languages lang, Collection<BookGenre> genre) {
        super(isbn,title,publicationYear,lang);
        this.authors = author;
        this.publisher = publisher;
        this.format = format; 
        this.genre = genre; 
    }

    public Collection<AuthorBook> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<AuthorBook> author) {
        this.authors = author;
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

    public BookFormat getFormat() {
        return format;
    }

    public void setFormat(BookFormat format) {
        this.format = format;
    }

    public Collection<BookGenre> getGenre() {
        return genre;
    }

    public void setGenre(Collection<BookGenre> genre) {
        this.genre = genre;
    }

    public Integer getVolumeNumber() {
        return volumeNumber;
    }

    public void setVolumeNumber(Integer tome) {
        this.volumeNumber = tome;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }
}
