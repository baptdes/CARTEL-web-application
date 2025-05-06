package cartel.spring_boot_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="copy")
@PrimaryKeyJoinColumn( name = "idobject" )
public class ItemCopy extends Item{

    @ManyToOne
    private Item objet;

    public ItemCopy() {
    }

    public ItemCopy(Item objet) {
        this.objet = objet;
    }


    public Item getObjet() {
        return objet;
    }

    public void setObjet(Item objet) {
        this.objet = objet;
    }
    
}
