package cartel.spring_boot_api.dto;

import cartel.spring_boot_api.model.LoanByCartel;
import java.sql.Date;

public class LoanByCartelDTO {
    private Long id;
    private Long borrowerId;
    private String borrowerName;
    private String borrowerContact;
    private String itemName;
    private String itemBarcode;
    private String itemCoverImage;
    private Date loanDate;
    private Date endDate;
    private boolean active;

    public LoanByCartelDTO(Long id, Long borrowerId, String borrowerName, String borrowerContact, String itemName, String itemBarcode, Date loanDate, Date endDate, boolean active) {
        this.id = id;
        this.borrowerId = borrowerId;
        this.borrowerName = borrowerName;
        this.borrowerContact = borrowerContact;
        this.itemName = itemName;
        this.itemBarcode = itemBarcode;
        this.loanDate = loanDate;
        this.endDate = endDate;
        this.active = active;
        this.itemCoverImage = null;
    }

    // Constructor to convert LoanByCartel to LoanByCartelDTO
    public LoanByCartelDTO(LoanByCartel loan) {
        this.id = loan.getId();
        this.borrowerId = loan.getBorrower() != null ? loan.getBorrower().getId() : null;
        this.borrowerName = loan.getBorrower() != null ? loan.getBorrower().getFirstname() + " " + loan.getBorrower().getSurname() : null;
        this.borrowerContact = loan.getBorrower() != null ? loan.getBorrower().getContact() : null; // Set contact
        this.itemName = loan.getItemShared() != null ? loan.getItemShared().getObjet().getName() : null;
        this.itemBarcode = loan.getItemShared() != null ? loan.getItemShared().getObjet().getBarcode() : null;
        this.loanDate = loan.getLoanDate();
        this.endDate = loan.getEndDate();
        this.active = loan.isActive();
        this.itemCoverImage = loan.getItemShared() != null && loan.getItemShared().getObjet() != null ? loan.getItemShared().getObjet().getCoverImage() : null;
    }

    public Long getId() {
        return id;
    }

    public Long getBorrowerId() {
        return borrowerId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public String getBorrowerContact() {
        return borrowerContact;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemBarcode() {
        return itemBarcode;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return active;
    }

    public String getItemCoverImage() {
        return itemCoverImage;
    }

    public void setItemCoverImage(String itemCoverImage) {
        this.itemCoverImage = itemCoverImage;
    }
}
