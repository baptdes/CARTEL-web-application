package cartel.spring_boot_api.model;

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

    @Column
    private Date endDate;

    @OneToOne
    @JoinColumn(nullable = true)  // Changed to nullable=true as itemShared may be null after completion
    private ItemCopy itemShared;
    
    // Added fields to save item information after itemShared is deleted
    @Column
    @JsonIgnore
    private String savedItemName;
    
    @Column
    @JsonIgnore
    private String savedItemBarcode;

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
    
    public String getSavedItemName() {
        return savedItemName;
    }
    
    public void setSavedItemName(String savedItemName) {
        this.savedItemName = savedItemName;
    }
    
    public String getSavedItemBarcode() {
        return savedItemBarcode;
    }
    
    public void setSavedItemBarcode(String savedItemBarcode) {
        this.savedItemBarcode = savedItemBarcode;
    }
    
    /**
     * Sauvegarde les informations de l'item partagé avant sa suppression
     */
    public void saveItemInfo() {
        if (this.itemShared != null && this.itemShared.getObjet() != null) {
            this.savedItemName = this.itemShared.getObjet().getName();
            this.savedItemBarcode = this.itemShared.getObjet().getBarcode();
        }
    }

    public void completeLoan() {
        this.endDate = new Date(System.currentTimeMillis());
        // Save item information before potentially removing the reference
        saveItemInfo();
    }
    
    public boolean isActive() {
        return this.endDate == null;
    }

    public PersonDTO getOwner() {
        return itemOwner != null ? new PersonDTO(itemOwner) : null;
    }
    
    /**
     * Retourne le nom de l'item, qu'il soit toujours référencé ou sauvegardé
     * @return le nom de l'item
     */
    public String getItemName() {
        if (this.itemShared != null && this.itemShared.getObjet() != null) {
            return this.itemShared.getObjet().getName();
        }
        return this.savedItemName;
    }
    
    /**
     * Retourne le barcode de l'item, qu'il soit toujours référencé ou sauvegardé
     * @return le barcode de l'item
     */
    public String getItemBarcode() {
        if (this.itemShared != null && this.itemShared.getObjet() != null) {
            return this.itemShared.getObjet().getBarcode();
        }
        return this.savedItemBarcode;
    }
}
