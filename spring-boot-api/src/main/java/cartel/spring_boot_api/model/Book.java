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

    // Book author
    @ManyToMany
    private Collection<AuthorBook> authors;

    // Book publisher
    @ManyToOne
    private PublisherBook publisher;

    // Book illustrator
    @ManyToMany
    private Collection<Illustrator> illustrator;
    
    // Book format
    private BookFormat format;

    // Book genre
    @ManyToMany
    private Collection<Genre> genres;

    // Volume number
    private Integer volumeNumber;

    // Series
    @ManyToOne
    private Series series;

    public Book(){
        super();
    }
    
    // Constructor with required fields
    public Book(String isbn, String title, Collection<AuthorBook> author, PublisherBook publisher, Integer publicationYear,
    BookFormat format,Languages lang, Collection<Genre> genre) {
        super(isbn,title,publicationYear,lang);
        this.authors = author;
        this.publisher = publisher;
        this.format = format; 
        this.genres = genre; 
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

    public Collection<Genre> getGenres() {
        return genres;
    }

    public void setGenre(Collection<Genre> genre) {
        this.genres = genre;
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

    public void addAuthor(AuthorBook author) {
        if (this.authors == null) {
            this.authors = new java.util.ArrayList<>();
        }
        this.authors.add(author);
    }
}
