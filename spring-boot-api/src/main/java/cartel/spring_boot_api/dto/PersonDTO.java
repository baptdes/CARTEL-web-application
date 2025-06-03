package cartel.spring_boot_api.dto;

import cartel.spring_boot_api.model.CartelPerson;

public class PersonDTO {
    private Long id;
    private String firstname;
    private String surname;
    private String contact;
    private Integer caution;
    private int loanToCartelCount;
    private int loanByCartelCount;

    public PersonDTO(CartelPerson person) {
        this.id = person.getId();
        this.firstname = person.getFirstname();
        this.surname = person.getSurname();
        this.contact = person.getContact();
        this.caution = person.getCaution();
        this.loanToCartelCount = person.getLoanToCartelNumber();
        this.loanByCartelCount = person.getLoanByCartelNumber();
    }

    public Long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String getContact() {
        return contact;
    }

    public Integer getCaution() {
        return caution;
    }

    public int getLoanToCartelCount() {
        return loanToCartelCount;
    }

    public int getLoanByCartelCount() {
        return loanByCartelCount;
    }
}
