package cartel.spring_boot_api.dto;

import cartel.spring_boot_api.model.ItemCopy;

public class CopyDTO {
    
    private Long id;
    private String itemName;
    private String itemBarcode;
    private boolean available;
    private boolean borrowable;

    public CopyDTO() {
        // Constructeur par défaut requis pour la sérialisation JSON
    }

    public CopyDTO(ItemCopy copy) {
        this.id = copy.getId();
        this.itemName = copy.getObjet().getName();
        this.itemBarcode = copy.getObjet().getBarcode();
        this.available = copy.isAvailable();
        this.borrowable = copy.isBorrowable();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getItemBarcode() {
        return itemBarcode;
    }
    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }
    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public boolean isBorrowable() {
        return borrowable;
    }
    public void setBorrowable(boolean borrowable) {
        this.borrowable = borrowable;
    }
}
