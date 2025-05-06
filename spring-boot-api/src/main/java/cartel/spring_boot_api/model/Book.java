package cartel.spring_boot_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "books")
@PrimaryKeyJoinColumn( name = "idobject" )
public class Book extends Item{

    //énumération des différents format d'un livre
    @Transient
    public enum FormatBook {MANGA,BD,LIVRE}; 

    //énumération des différents langues d'un livre
    @Transient
    public enum Langues {FR,EN,JA}; 

    //liste des attributs
    //identifiant
    @Column(nullable = false)
    private String isbn;
    
    //titre du livre
    @Column(nullable = false)
    private String title;
   
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
    
    //description petit resumé
    @Column(length = 2000)
    private String description;
    
    //image de couverture
    private String coverImage;
    
    //année de publication
    @Column(nullable = false)
    private Integer publicationYear;
    
    //format du livre
    @Column(nullable = false)
    private FormatBook format;

    //volume
    private Integer tome;

    //langue
    @Column(nullable = false)
    private Langues langue;

    //serie
    @JsonIgnore
    @ManyToOne
    private Serie serie;


    //date de création de l'entité
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    //date de dérniére modification de l'entité
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public Book() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with required fields
    public Book(String isbn, String title, Collection<AuthorBook> author, PublisherBook publisher, Integer publicationYear,
    FormatBook format,Langues lang) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.format = format;
        this.langue = lang;
        this.createdAt = LocalDateTime.now(); 
}


    // Pre-update method
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public FormatBook getFormat() {
        return format;
    }

    public void setFormat(FormatBook format) {
        this.format = format;
    }

    public Integer getTome() {
        return tome;
    }

    public void setTome(Integer tome) {
        this.tome = tome;
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

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Langues getLangue() {
        return langue;
    }

    public void setLangue(Langues langue) {
        this.langue = langue;
    }
}
