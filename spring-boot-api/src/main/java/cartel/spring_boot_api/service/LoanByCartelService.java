package cartel.spring_boot_api.service;

import cartel.spring_boot_api.dto.LoanByCartelDTO;
import cartel.spring_boot_api.model.LoanByCartel;

import java.sql.Date;
import java.util.List;

public interface LoanByCartelService {

    /**
     * Récupérer un prêt par Cartel en utilisant son ID.
     * @param loanByCartelId ID du prêt.
     * @return Objet LoanByCartel correspondant.
     */
    LoanByCartelDTO getLoanByCartel(long loanByCartelId);

    /**
     * Supprimer un prêt par Cartel en utilisant son ID.
     * @param loanByCartelId ID du prêt à supprimer.
     */
    void removeLoanByCartel(long loanByCartelId);

    /**
     * Créer un nouveau prêt par Cartel.
     * @param personId ID de la personne emprunteuse.
     * @param itemCopyId ID de l'exemplaire emprunté.
     */
    void createLoanByCartel(Long personId, Long itemCopyId);

    /**
     * Filtrer les prêts par Cartel avec des paramètres avancés.
     * @param pageNumber Numéro de la page pour la pagination.
     * @param pageSize Taille de la page pour la pagination.
     * @param asc Tri ascendant ou descendant.
     * @param sortBy Champ utilisé pour le tri.
     * @param itemName Nom de l'article emprunté.
     * @param borrowerFirstName Prénom de l'emprunteur.
     * @param borrowerSurname Nom de famille de l'emprunteur.
     * @param startDateBefore Filtrer les prêts avant cette date de début.
     * @param startDateAfter Filtrer les prêts après cette date de début.
     * @param endDateBefore Filtrer les prêts avant cette date de fin.
     * @param endDateAfter Filtrer les prêts après cette date de fin.
     * @param active Statut actif ou inactif du prêt.
     * @return Liste des prêts correspondant aux critères.
     */
    List<LoanByCartelDTO> filterLoanByCartel(
            int pageNumber, int pageSize,
            boolean asc, String sortBy,
            String itemName, String borrowerFirstName,
            String borrowerSurname, Date startDateBefore,
            Date startDateAfter, Date endDateBefore,
            Date endDateAfter, Boolean active);

    /**
     * Marquer un prêt comme complété en utilisant son ID.
     * @param loanByCartelId ID du prêt à compléter.
     */
    void completeLoanByCartel(long loanByCartelId);
}
