package cartel.spring_boot_api.model;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table( name = "Items" )
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idobject;

    @JsonIgnore
    @OneToMany(mappedBy = "objet")
    private Collection<ItemCopy> copie;

    public int getCopienumb(){
        return this.copie.size()+1;
    }

    @OneToOne(mappedBy ="item")
    Exchange statut;

    public Exchange getStatut() {
        return statut;
    }

    public void setStatut(Exchange statut) {
        this.statut = statut;
    }
    
}
