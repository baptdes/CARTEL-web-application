package cartel.spring_boot_api.model;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "authorbook")
public class AuthorBook {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String firstname;

    @JsonIgnore
    @ManyToMany(mappedBy = "author")
    private Collection<Book> writtenBook;

    public AuthorBook() {
    }

    public AuthorBook(String surname, String firstname) {
        this.surname = surname;
        this.firstname = firstname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Collection<Book> getWrittenBook() {
        return writtenBook;
    }

    public void setWrittenBook(Collection<Book> writtenBook) {
        this.writtenBook = writtenBook;
    }
    
}
