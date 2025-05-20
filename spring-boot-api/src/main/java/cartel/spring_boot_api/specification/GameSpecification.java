package cartel.spring_boot_api.specification;

import cartel.spring_boot_api.model.Game;
import cartel.spring_boot_api.model.Game.GameCategories;

import org.springframework.data.jpa.domain.Specification;

public class GameSpecification {
    
    public static Specification<Game> titleLike(String title) {
        return (root, query, cb) -> 
            cb.like(cb.lower(root.get("name")), "%" + title.toLowerCase() + "%");
    }
    
    public static Specification<Game> fromPublisherByName(String publisherName) {
        return (root, query, cb) -> 
            cb.like(cb.lower(root.get("publisher").get("name")), "%" + publisherName.toLowerCase() + "%");
    }
    
    public static Specification<Game> fromCreatorByFirstName(String firstName) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.like(cb.lower(root.join("creators").get("firstname")), 
                "%" + firstName.toLowerCase() + "%");
        };
    }
    
    public static Specification<Game> fromCreatorBySurname(String surname) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.like(cb.lower(root.join("creators").get("surname")), 
                "%" + surname.toLowerCase() + "%");
        };
    }
    
    public static Specification<Game> minPlayersGreaterThanEqual(Integer minPlayers) {
        return (root, query, cb) -> 
            cb.greaterThanOrEqualTo(root.get("minPlayers"), minPlayers);
    }
    
    public static Specification<Game> maxPlayersLessThanEqual(Integer maxPlayers) {
        return (root, query, cb) -> 
            cb.lessThanOrEqualTo(root.get("maxPlayers"), maxPlayers);
    }
    
    public static Specification<Game> playtimeBetween(Integer minPlaytime, Integer maxPlaytime) {
        return (root, query, cb) -> {
            if (minPlaytime == null && maxPlaytime == null) {
                return cb.conjunction();
            } else if (minPlaytime == null) {
                return cb.lessThanOrEqualTo(root.get("avgPlaytime"), maxPlaytime);
            } else if (maxPlaytime == null) {
                return cb.greaterThanOrEqualTo(root.get("avgPlaytime"), minPlaytime);
            } else {
                return cb.between(root.get("avgPlaytime"), minPlaytime, maxPlaytime);
            }
        };
    }
    
    public static Specification<Game> categoryEqual(GameCategories category) {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.isMember(category, root.get("categories"));
        };
    }
}
