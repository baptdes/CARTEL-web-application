package cartel.spring_boot_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.Date;

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
@Table(name = "loanToCartel")
public class LoanToCartel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    private CartelPerson itemOwner;

    @Column(nullable = false)
    private Date loanDate;

    @Column(nullable = true)
    private Date endDate;

    @OneToOne
    @JoinColumn(nullable = true)  // Changed to nullable=true as itemShared may be null after completion
    private ItemCopy itemShared;

    public LoanToCartel() {
    }

    public LoanToCartel(CartelPerson itemOwner, ItemCopy itemShared) {
        this.itemOwner = itemOwner;
        this.itemShared = itemShared;
        this.loanDate = new Date(System.currentTimeMillis());
    }

    public LoanToCartel(CartelPerson itemOwner, ItemCopy itemShared, Date loanDate) {
        this.itemOwner = itemOwner;
        this.itemShared = itemShared;
        this.loanDate = loanDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CartelPerson getItemOwner() {
        return this.itemOwner;
    }

    public void setItemOwner(CartelPerson itemOwner) {
        this.itemOwner = itemOwner;
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
}
