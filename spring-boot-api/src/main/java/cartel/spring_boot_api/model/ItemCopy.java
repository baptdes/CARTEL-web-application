package cartel.spring_boot_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;

@Entity
@Table(name="copies")
public class ItemCopy{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Item objet;

    @OneToOne(mappedBy = "itemShared")
    private LoanByCartel loanToPerson;

    @OneToOne(mappedBy = "itemShared")
    private LoanToCartel loanByPerson;

    public ItemCopy() {
    }


    public ItemCopy(Item objet) {
        this.objet = objet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getObjet() {
        return objet;
    }

    public void setObjet(Item objet) {
        this.objet = objet;
    }
    
    public boolean isAvailable() {
        return loanToPerson == null;
    }

    public boolean isBorrowable() {
        return loanByPerson == null && loanToPerson == null;
    }

    public LoanByCartel getLoanToPerson() {
        return loanToPerson;
    }

    public void setLoanToPerson(LoanByCartel loanToPerson) {
        this.loanToPerson = loanToPerson;
    }
}
