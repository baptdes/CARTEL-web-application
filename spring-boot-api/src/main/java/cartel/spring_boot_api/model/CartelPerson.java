package cartel.spring_boot_api.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;

@Entity
@Table(name="membre")
public class CartelPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstname;

    @Column
    private String surname;

    @Column
    private String contact;

    @Column
    private Integer caution;

    @Column
    @OneToMany(mappedBy = "itemBorrower")
    private List<LoanByCartel> loanToPerson;

    @Column
    @OneToMany(mappedBy = "itemOwner")
    private List<LoanToCartel> loanByPerson;

    public CartelPerson(String name, String surname,String contact) {
        this.firstname = name;
        this.surname = surname;
        this.contact = contact;
    }

    public CartelPerson(String name, String surname,String contact, Integer caution) {
        this.firstname = name;
        this.surname = surname;
        this.contact = contact;
        this.caution = caution;
    }

    public CartelPerson(){
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Integer getCaution() {
        return caution;
    }

    public void setCaution(Integer caution) {
        this.caution = caution;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<LoanByCartel> getLoanToPerson() {
        return this.loanToPerson;
    }

    public void setLoanToPerson(List<LoanByCartel> loans) {
        this.loanToPerson = loans;
    }

    public List<LoanToCartel> getLoanByPerson() {
        return this.loanByPerson;
    }

    public void setLoanByPerson(List<LoanToCartel> loans) {
        this.loanByPerson = loans;
    }

    public Integer getLoanToCartelNumber() {
        return (loanToPerson != null) ? loanToPerson.size() : 0;
    }

    public Integer getLoanByCartelNumber() {
        return (loanByPerson != null) ? loanByPerson.size() : 0;
    }
}
