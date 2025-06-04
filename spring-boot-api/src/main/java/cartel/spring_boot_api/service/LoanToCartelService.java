package cartel.spring_boot_api.service;

import cartel.spring_boot_api.dto.LoanToCartelDTO;

import java.sql.Date;
import java.util.List;

public interface LoanToCartelService {

    /**
     * Récupère un prêt à partir de son ID.
     *
     * @param loanToCartelId l'ID du prêt
     * @return le prêt correspondant à l'ID
     */
    LoanToCartelDTO getLoanToCartel(long loanToCartelId);

    /**
     * Supprime un prêt à partir de son ID.
     *
     * @param loanToCartelId l'ID du prêt
     */
    void removeLoanToCartel(long loanToCartelId);

    /**
     * Crée un nouveau prêt pour une personne et un objet.
     *
     * @param personId l'ID de la personne
     * @param itemId l'ID de l'objet
     */
    void createLoanToCartel(Long personId, String itemId);

    /**
     * Filtre les prêts selon différents critères.
     *
     * @param pageNumber numéro de la page pour la pagination
     * @param pageSize taille de la page
     * @param asc tri croissant ou décroissant
     * @param sortBy champ utilisé pour le tri
     * @param itemName nom de l'objet
     * @param ownerFirstName prénom du propriétaire
     * @param ownerSurname nom de famille du propriétaire
     * @param startDateBefore filtre les prêts commençant avant cette date
     * @param startDateAfter filtre les prêts commençant après cette date
     * @param endDateBefore filtre les prêts se terminant avant cette date
     * @param endDateAfter filtre les prêts se terminant après cette date
     * @return une liste de prêts correspondant aux critères
     */
    List<LoanToCartelDTO> filterLoanToCartel(
            int pageNumber, int pageSize,
            boolean asc, String sortBy,
            String itemName, String ownerFirstName,
            String ownerSurname, Date startDateBefore,
            Date startDateAfter, Date endDateBefore,
            Date endDateAfter);
}
