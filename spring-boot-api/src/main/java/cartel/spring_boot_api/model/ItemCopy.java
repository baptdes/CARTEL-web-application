package cartel.spring_boot_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="copy")
public class ItemCopy{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcopy;

    public Long getIdcopy() {
        return idcopy;
    }

    public void setIdcopy(Long idcopy) {
        this.idcopy = idcopy;
    }

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
