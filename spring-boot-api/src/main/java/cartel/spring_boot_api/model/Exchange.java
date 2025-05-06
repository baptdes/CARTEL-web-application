package cartel.spring_boot_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="non-empruntable")
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idloan;

    Exchange(Item item){
        this.item =item;
    }

    Exchange(){
        
    }

    @OneToOne
    @JsonIgnore
    private Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
