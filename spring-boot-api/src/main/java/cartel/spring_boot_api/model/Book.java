package cartel.spring_boot_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    //énumération des différents format d'un livre
    public enum FormatBook {MANGA,BD,LIVRE}; 

    //liste des attributs
    //identifiant
    @Id
    private String isbn;
    
    //titre du livre
    @Column(nullable = false)
    private String title;
   
    //auteurs du livre
    @Column(nullable = false)
    @ManyToMany
    private AuthorBook author;

    //éditeur du livre
    @Column(nullable = false)
    @ManyToOne
    private PublisherBook publisher;

    //illustrateur du livre
    @ManyToMany
    private Illustrator illustrator;
    
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
    public Book(String isbn, String title, AuthorBook author, PublisherBook publisher, Integer publicationYear,
    FormatBook format) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.format = format;
        this.createdAt = LocalDateTime.now(); 
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

    public AuthorBook getAuthor() {
        return author;
    }

    public void setAuthor(AuthorBook author) {
        this.author = author;
    }

    public PublisherBook getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherBook publisher) {
        this.publisher = publisher;
    }

    public Illustrator getIllustrator() {
        return illustrator;
    }

    public void setIllustrator(Illustrator illustrator) {
        this.illustrator = illustrator;
    }
        
    public Integer getTome() {
        return tome;
    }

    public void setTome(Integer tome) {
        this.tome = tome;
    }

    public FormatBook getFormat() {
        return format;
    }

    public void setFormat(FormatBook format) {
        this.format = format;
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

    // Pre-update method
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
