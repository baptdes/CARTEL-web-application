package cartel.spring_boot_api.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="emprunt")
@PrimaryKeyJoinColumn( name = "idloan" )
public class Loan extends Exchange{
    
    @ManyToOne
    private CartelPerson borrower;

    private LocalDateTime loanDate;

    public Loan(){}

    public Loan(Item item, CartelPerson borrower) {
        super(item);
        this.borrower = borrower;
        this.loanDate = LocalDateTime.now();
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
