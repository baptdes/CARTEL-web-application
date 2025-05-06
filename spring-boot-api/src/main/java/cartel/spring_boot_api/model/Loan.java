package cartel.spring_boot_api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="emprunt")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Item item;
    
    @ManyToOne
    private CartelPerson borrower;

    private LocalDateTime loanDate;

    public Loan(Item item, CartelPerson borrower) {
        this.item = item;
        this.borrower = borrower;
        this.loanDate = LocalDateTime.now();
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public CartelPerson getBorrower() {
        return borrower;
    }

    public void setBorrower(CartelPerson borrower) {
        this.borrower = borrower;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

}
