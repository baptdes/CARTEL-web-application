package cartel.spring_boot_api.specification;

import java.sql.Date;

import cartel.spring_boot_api.model.CartelPerson;
import cartel.spring_boot_api.model.ItemCopy;
import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.model.LoanByCartel;
import cartel.spring_boot_api.model.LoanToCartel;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;


public class LoanSpecification {
    //** FILTERING LOANBYCARTEL **//
    // Filter by borrower first name.
    public static Specification<LoanByCartel> filterLoanByCartelfromBorrowerByFirstName(String borrowerFirstName) {
        return (root, query, builder) -> {
            Join<LoanByCartel,CartelPerson> loanWithBorrower = root.join("itemBorrower");
            return builder.like(builder.lower(loanWithBorrower.get("firstname")), "%" + borrowerFirstName.toLowerCase() + "%");
        };
    }

    // Filter by borrower surname.
    public static Specification<LoanByCartel> filterLoanByCartelfromBorrowerBySurname(String borrowerSurname) {
        return (root, query, builder) -> {
            Join<LoanByCartel,CartelPerson> loanWithBorrower = root.join("itemBorrower");
            return builder.like(builder.lower(loanWithBorrower.get("surname")), "%" + borrowerSurname.toLowerCase() + "%");
        };
    }

    // Filter by shared item name.
    public static Specification<LoanByCartel> filterLoanByCartelfromItemSharedByName(String itemCopyNameLike) {
        return (root, query, builder) -> {
            Join<LoanByCartel,ItemCopy> loanWithItemCopy = root.join("itemShared");
            Join<Join<LoanByCartel,ItemCopy>, Item> loanWithItem = loanWithItemCopy.join("objet");
            return builder.like(builder.lower(loanWithItem.get("name")), "%" + itemCopyNameLike.toLowerCase() + "%");
        };
    }

    // Filter by shared item barcode.
    public static Specification<LoanByCartel> filterLoanByCartelfromItemBarcode(String barcode) {
        return (root, query, builder) -> {
            Join<LoanByCartel,ItemCopy> loanWithItemCopy = root.join("itemShared");
            Join<Join<LoanByCartel,ItemCopy>, Item> loanWithItem = loanWithItemCopy.join("objet");
            return builder.like(loanWithItem.get("barcode"), "%" + barcode + "%");
        };
    }

    //** FILTERING LOANTOCARTEL **//
    // Filter by owner first name.
    public static Specification<LoanToCartel> filterLoanToCartelfromOwnerByFirstName(String ownerFirstName) {
        return (root, query, builder) -> {
            Join<LoanToCartel,CartelPerson> loanWithOwner = root.join("itemOwner");
            return builder.like(builder.lower(loanWithOwner.get("firstname")), "%" + ownerFirstName.toLowerCase() + "%");
        };
    }

    // Filter by owner surname.
    public static Specification<LoanToCartel> filterLoanToCartelfromOwnerBySurname(String ownerSurname) {
        return (root, query, builder) -> {
            Join<LoanToCartel,CartelPerson> loanWithOwner = root.join("itemOwner");
            return builder.like(builder.lower(loanWithOwner.get("surname")), "%" + ownerSurname.toLowerCase() + "%");
        };
    }

    public static Specification<LoanToCartel> filterLoanToCartelfromItemSharedByName(String itemCopyNameLike) {
    return (root, query, builder) -> {
        String searchPattern = "%" + itemCopyNameLike.toLowerCase() + "%";
        
        return builder.and(
                builder.isNotNull(root.get("itemShared")),
                builder.like(
                    builder.lower(root.join("itemShared").join("objet").get("name")), 
                    searchPattern
                )
            );
    };
}

    // Filter by shared item barcode.
    public static Specification<LoanToCartel> filterLoanToCartelfromItemBarcode(String barcode) {
        return (root, query, builder) -> {
            String searchPattern = "%" + barcode + "%";
            
            // Handle both active loans (with itemShared) and completed loans (with savedBarcode)
            return builder.and(
                    builder.isNotNull(root.get("itemShared")),
                    builder.like(
                        root.join("itemShared").join("objet").get("barcode"), 
                        searchPattern
                    )
                );
        };
    }

    //** OTHER HELPING FUNCTIONS **//
    // Filter item copy by their barcode.
    public static Specification<ItemCopy> filterItemCopyFromBarcode(String barcode) {
        return (root, query, builder) -> {
            Join<Item,ItemCopy> itemWithItemCopy = root.join("objet");
            return builder.like(itemWithItemCopy.get("barcode"), "%" + barcode + "%");
        };
    }

    //** FILTERING BY DATE **//
    // Filter by loan date before a specific date
    public static Specification<LoanByCartel> filterLoanByCartelFromStartDateBefore(Date dateLimite) {
        return (root, query, builder) -> {
            if (dateLimite == null) return null;
            return builder.lessThan(root.get("loanDate"), dateLimite);
        };
    }

    // Filter by loan date after a specific date
    public static Specification<LoanByCartel> filterLoanByCartelFromStartDateAfter(Date dateLimite) {
        return (root, query, builder) -> {
            if (dateLimite == null) return null;
            return builder.greaterThan(root.get("loanDate"), dateLimite);
        };
    }

    // Filter by end date before a specific date
    public static Specification<LoanByCartel> filterLoanByCartelFromEndDateBefore(Date dateLimite) {
        return (root, query, builder) -> {
            if (dateLimite == null) return null;
            return builder.lessThan(root.get("endDate"), dateLimite);
        };
    }

    // Filter by end date after a specific date
    public static Specification<LoanByCartel> filterLoanByCartelFromEndDateAfter(Date dateLimite) {
        return (root, query, builder) -> {
            if (dateLimite == null) return null;
            return builder.greaterThan(root.get("endDate"), dateLimite);
        };
    }

    // Same filters for LoanToCartel
    public static Specification<LoanToCartel> filterLoanToCartelFromStartDateBefore(Date dateLimite) {
        return (root, query, builder) -> {
            if (dateLimite == null) return null;
            return builder.lessThan(root.get("loanDate"), dateLimite);
        };
    }

    public static Specification<LoanToCartel> filterLoanToCartelFromStartDateAfter(Date dateLimite) {
        return (root, query, builder) -> {
            if (dateLimite == null) return null;
            return builder.greaterThan(root.get("loanDate"), dateLimite);
        };
    }

    public static Specification<LoanToCartel> filterLoanToCartelFromEndDateBefore(Date dateLimite) {
        return (root, query, builder) -> {
            if (dateLimite == null) return null;
            return builder.lessThan(root.get("endDate"), dateLimite);
        };
    }

    public static Specification<LoanToCartel> filterLoanToCartelFromEndDateAfter(Date dateLimite) {
        return (root, query, builder) -> {
            if (dateLimite == null) return null;
            return builder.greaterThan(root.get("endDate"), dateLimite);
        };
    }
}
