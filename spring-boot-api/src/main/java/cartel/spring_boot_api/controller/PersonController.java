package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.dto.PersonDTO;
import cartel.spring_boot_api.model.CartelPerson;
import cartel.spring_boot_api.repository.CartelPersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing members (persons) through public endpoints
 */
@RestController
@RequestMapping("/api/public/persons")
public class PersonController {

    @Autowired
    private CartelPersonRepository cartelPersonRepository;

    /**
     * Recherche des membres par nom complet ou récupère tous les membres
     * 
     * @param fullname (Optionnel) Le nom ou prénom à rechercher
     * @param pageNumber Le numéro de la page
     * @param pageSize La taille de la page
     * @return Une page de membres correspondant aux critères
     */
    @GetMapping()
    public Page<PersonDTO> searchPersons(
            @RequestParam(required = false) String fullname,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if (fullname == null || fullname.isBlank()) {
            return cartelPersonRepository.findAll(pageable)
                    .map(PersonDTO::new); // DTO includes loan counts
        }
        return cartelPersonRepository.findByFirstnameContainingIgnoreCaseOrSurnameContainingIgnoreCase(
                fullname, fullname, pageable)
                .map(PersonDTO::new); // DTO includes loan counts
    }

    /**
     * Récupère une personne par son ID
     * 
     * @param personId L'ID de la personne
     * @return La personne correspondante
     */
    @GetMapping("/{personId}")
    public CartelPerson getPersonById(@PathVariable Long personId) {
        return cartelPersonRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));
    }

    /**
     * Ajoute une nouvelle personne
     * 
     * @param personData Les données de la personne (firstname, surname, contact)
     * @return La personne créée
     */
    @PostMapping("/add")
    public CartelPerson addPerson(@RequestBody Map<String, String> personData) {
        String firstname = personData.get("firstname");
        String surname = personData.get("surname");
        String contact = personData.get("contact");
        Integer caution = Integer.valueOf(personData.get("caution"));
        CartelPerson person = new CartelPerson(firstname, surname, contact, caution);
        return cartelPersonRepository.save(person);
    }

    /**
     * Met à jour une personne existante
     * 
     * @param personId L'ID de la personne à mettre à jour
     * @param personData Les nouvelles données de la personne
     * @return La personne mise à jour
     */
    @PutMapping("/update/{personId}")
    public CartelPerson updatePerson(
            @PathVariable Long personId,
            @RequestBody Map<String, String> personData) {
        CartelPerson person = cartelPersonRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));
        person.setFirstname(personData.get("firstname"));
        person.setSurname(personData.get("surname"));
        person.setContact(personData.get("contact"));
        person.setCaution(Integer.valueOf(personData.get("caution")));
        return cartelPersonRepository.save(person);
    }

    /**
     * Supprime une personne par son ID
     * 
     * @param personId L'ID de la personne à supprimer
     */
    @DeleteMapping("/delete/{personId}")
    public void deletePerson(@PathVariable Long personId) {
        cartelPersonRepository.deleteById(personId);
    }
}