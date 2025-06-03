package cartel.spring_boot_api.dto;

import cartel.spring_boot_api.model.LoanToCartel;
import java.sql.Date;

public class LoanToCartelDTO {
    private Long id;
    private Long ownerId;
    private String ownerName;
    private String ownerContact;
    private String itemName;
    private String itemBarcode;
    private Date loanDate;
    private Date endDate;
    private boolean active;
    private String itemCoverImage; // Optional, can be null if not available

    public LoanToCartelDTO(Long id, Long ownerId, String ownerName, String ownerContact, String itemName, String itemBarcode, Date loanDate, Date endDate, boolean active) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.itemName = itemName;
        this.itemBarcode = itemBarcode;
        this.loanDate = loanDate;
        this.endDate = endDate;
        this.active = active;
        this.itemCoverImage = null; // Initialize to null, can be set later if needed
    }

    // Constructor to convert LoanToCartel to LoanToCartelDTO
    public LoanToCartelDTO(LoanToCartel loan) {
        this.id = loan.getId();
        this.ownerId = loan.getItemOwner() != null ? loan.getItemOwner().getId() : null;
        this.ownerName = loan.getItemOwner() != null ? loan.getItemOwner().getFirstname() + " " + loan.getItemOwner().getSurname() : null;
        this.ownerContact = loan.getItemOwner() != null ? loan.getItemOwner().getContact() : null;
        this.itemName = loan.getItemName();
        this.itemBarcode = loan.getItemBarcode();
        this.loanDate = loan.getLoanDate();
        this.endDate = loan.getEndDate();
        this.active = loan.isActive();
        this.itemCoverImage = loan.getItemShared() != null && loan.getItemShared().getObjet() != null ? loan.getItemShared().getObjet().getCoverImage() : null;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerContact() {
        return ownerContact;
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
