package cartel.spring_boot_api.model;

import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.Date;

import cartel.spring_boot_api.dto.PersonDTO;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;


@Entity
@Table(name = "loanByCartel")
public class LoanByCartel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    private CartelPerson itemBorrower;

    @Column(nullable = false)
    private Date loanDate;

    @Column
    private Date endDate;

    @OneToOne
    @JoinColumn(nullable = false)
    private ItemCopy itemShared;

    public LoanByCartel() {
        this.loanDate = new Date(System.currentTimeMillis());
    }

    public LoanByCartel(CartelPerson itemBorrower, ItemCopy itemShared) {
        super();
        this.itemBorrower = itemBorrower;
        this.itemShared = itemShared;
        this.loanDate = new Date(System.currentTimeMillis());
    }

    public LoanByCartel(CartelPerson itemBorrower, ItemCopy itemShared, Date loanDate) {
        this.itemBorrower = itemBorrower;
        this.itemShared = itemShared;
        this.loanDate = loanDate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CartelPerson getItemBorrower() {
        return this.itemBorrower;
    }

    public void setItemBorrower(CartelPerson itemBorrower) {
        this.itemBorrower = itemBorrower;
    }

    public ItemCopy getItemShared() {
        return this.itemShared;
    }

    public void setItemShared(ItemCopy itemShared) {
        this.itemShared = itemShared;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void completeLoan() {
        this.endDate = new Date(System.currentTimeMillis());
    }
    
    public boolean isActive() {
        return this.endDate == null;
    }

    public PersonDTO getBorrower() {
        return itemBorrower != null ? new PersonDTO(itemBorrower) : null;
    }
}
