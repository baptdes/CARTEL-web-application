package cartel.spring_boot_api.model;

import java.util.Collection;

import org.springframework.security.core.Transient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "suggestion")
public class Suggestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    public enum TypeSuggestion {LIVRE, MANGA, BD, JDS, AUTRE};

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private TypeSuggestion type;

    public Suggestion() {
    }

    public Suggestion(String name, TypeSuggestion type) {
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeSuggestion getType() {
        return type;
    }

    public void setType(TypeSuggestion type) {
        this.type = type;
    }
    
}
