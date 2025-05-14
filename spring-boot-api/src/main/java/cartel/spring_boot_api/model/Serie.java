package cartel.spring_boot_api.model;

import java.util.Collection;
import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "series")
public class Serie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serieName;

    @OneToMany(mappedBy = "serie")
    private List<Book> seriesItem;

    public Serie() {
    }

    public Serie(String name) {
        this.serieName = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerieName() {
        return serieName;
    }

    public void setSerieName(String name) {
        this.serieName = name;
    }

    public List<Book> getSerieItem() {
        return seriesItem;
    }

    public void setSerieItem(List<Book> seriesItem) {
        this.seriesItem = seriesItem;
    }

}
