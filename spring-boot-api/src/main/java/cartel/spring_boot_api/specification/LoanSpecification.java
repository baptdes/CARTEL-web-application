package cartel.spring_boot_api.specification;

import java.time.LocalDateTime;

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
            return builder.like(loanWithBorrower.get("firstname"), "%" + borrowerFirstName + "%");
        };
    }

    // Filter by borrower surname.
    public static Specification<LoanByCartel> filterLoanByCartelfromBorrowerBySurname(String borrowerSurname) {
        return (root, query, builder) -> {
            Join<LoanByCartel,CartelPerson> loanWithBorrower = root.join("itemBorrower");
            return builder.like(loanWithBorrower.get("surname"), "%" + borrowerSurname + "%");
        };
    }

    // Filter by borrowed item name.
    public static Specification<LoanByCartel> filterLoanByCartelfromItemSharedByName(String itemCopyNameLike) {
        return (root, query, builder) -> {
            Join<LoanByCartel,ItemCopy> loanWithItemCopy = root.join("itemShared");
            Join<Join<LoanByCartel,ItemCopy>, Item> loanWithItem = loanWithItemCopy.join("objet");
            return builder.like(loanWithItem.get("name"), "%" + itemCopyNameLike + "%");
        };
    }

    //** FILTERING LOANTOCARTEL **//
    // Filter by owner first name.
    public static Specification<LoanToCartel> filterLoanToCartelfromOwnerByFirstName(String ownerFirstName) {
        return (root, query, builder) -> {
            Join<LoanToCartel,CartelPerson> loanWithOwner = root.join("itemOwner");
            return builder.like(loanWithOwner.get("firstname"), "%" + ownerFirstName + "%");
        };
    }

    // Filter by owner surname.
    public static Specification<LoanToCartel> filterLoanToCartelfromOwnerBySurname(String ownerSurname) {
        return (root, query, builder) -> {
            Join<LoanToCartel,CartelPerson> loanWithOwner = root.join("itemOwner");
            return builder.like(loanWithOwner.get("surname"), "%" + ownerSurname + "%");
        };
    }

    // Filter by borrowed item name.
    public static Specification<LoanToCartel> filterLoanToCartelfromItemSharedByName(String itemCopyNameLike) {
        return (root, query, builder) -> {
            Join<LoanToCartel,ItemCopy> loanWithItemCopy = root.join("itemShared");
            Join<Join<LoanToCartel,ItemCopy>, Item> loanWithItem = loanWithItemCopy.join("objet");
            return builder.like(loanWithItem.get("name"), "%" + itemCopyNameLike + "%");
        };
    }
}
    // //** FILTERING BY DATE **//
    // Pour l'implementer, il faut d'abord changer toutes les dates dans les bases de données par des java.sql.Date
    //
    // // Filter by borrowing date before a dateLimite.
    // public static Specification<LoanByCartel> filterLoanByCartelfromDateBefore(LocalDateTime dateLimite) {
    //     return (loan, query, builder) -> builder.greaterThan(dateLimite, loan.get("loanDate"));
    // }
    //
    // // Filter by borrowing date after a dateLimite.
    // public static Specification<LoanByCartel> filterLoanByCartelfromDateAfter(LocalDateTime dateLimite) {
    //     return (loan, query, builder) -> builder.greaterThan(loan.get("loanDate"), dateLimite);
    // }
