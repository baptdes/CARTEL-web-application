package cartel.spring_boot_api.model;

import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "bookId")
public class Book extends CatalogItem {
    
    public enum BookType {
        NOVEL,
        MANGA,
        COMIC,
        OTHER
    }
    
    private String author;
    private Integer pages;
    private String isbn;
    
    @Enumerated(EnumType.STRING)
    private BookType type;
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public Integer getPages() {
        return pages;
    }
    
    public void setPages(Integer pages) {
        this.pages = pages;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public BookType getType() {
        return type;
    }
    
    public void setType(BookType type) {
        this.type = type;
    }
}
