package cartel.spring_boot_api.model;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(nullable = false)
    private CartelPerson itemOwner;

    @Column/*(nullable = false)*/
    private LocalDateTime loanDate;

    @OneToOne
    @JoinColumn(nullable = false)
    private ItemCopy itemShared;

    public LoanToCartel() {
    }

    public LoanToCartel(CartelPerson itemOwner, ItemCopy itemShared) {
        this.itemOwner = itemOwner;
        this.itemShared = itemShared;
        this.loanDate = LocalDateTime.now();
    }

	public LoanToCartel(CartelPerson itemOwner, ItemCopy itemShared, LocalDateTime loanDate) {
        this.itemOwner = itemOwner;
        this.itemShared = itemShared;
        this.loanDate = loanDate;
    }

    // public LoanToCartel(CartelPerson itemOwner, Collection<ItemCopy> itemShared, LocalDateTime loanDate) {
    //     this.itemOwner = itemOwner;
    //     this.itemShared = itemShared;
    //     this.loanDate = loanDate;
    // }

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
}
