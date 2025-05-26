package cartel.spring_boot_api.service;

import cartel.spring_boot_api.model.AuthorGame;
import cartel.spring_boot_api.model.Game;
import cartel.spring_boot_api.model.PublisherGame;
import cartel.spring_boot_api.model.Game.GameCategories;
import cartel.spring_boot_api.repository.AuthorGameRepository;
import cartel.spring_boot_api.repository.GameRepository;
import cartel.spring_boot_api.repository.PublisherGameRepository;
import static cartel.spring_boot_api.specification.GameSpecification.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private AuthorGameRepository authorGameRepository;
    
    @Autowired
    private PublisherGameRepository publisherGameRepository;

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public Optional<Game> getGameByBarcode(String barcode) {
        List<Game> games = gameRepository.findByBarcode(barcode);
        if (games.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(games.get(0));
        }
    }

    @Override
    public List<PublisherGame> getPublishersByName(String nameLike){
        return publisherGameRepository.findByNameContainingIgnoreCase(nameLike);
    }

    @Override
    public List<Game> filterGames(int pageNumber, int pageSize, boolean asc, String sortBy,
                                String titleGame, String publisherName,
                                String creatorFirstName, String creatorSurname,
                                Integer minPlayers, Integer maxPlayers,
                                Integer minPlaytime, Integer maxPlaytime,
                                GameCategories category) {
        Pageable page = asc ? 
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) : 
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());

        Specification<Game> filters = ((titleGame == null) ? titleLike("") : titleLike(titleGame))
                .and((publisherName == null) ? null : fromPublisherByName(publisherName))
                .and((creatorSurname == null) ? null : fromCreatorBySurname(creatorSurname))
                .and((creatorFirstName == null) ? null : fromCreatorByFirstName(creatorFirstName))
                .and((minPlayers == null) ? null : minPlayersGreaterThanEqual(minPlayers))
                .and((maxPlayers == null) ? null : maxPlayersLessThanEqual(maxPlayers))
                .and(playtimeBetween(minPlaytime, maxPlaytime))
                .and((category == null) ? null : categoryEqual(category));

        Page<Game> pageGame = gameRepository.findAll(filters, page);
        return pageGame.getContent();
    }

    @Override
    public List<AuthorGame> getAllCreators() {
        return authorGameRepository.findAll();
    }

    @Override
    public List<AuthorGame> getAuthorsByCompleteName(String nameLike){
        return authorGameRepository.findAll(authorsGameByCompleteName(nameLike));
    }

    @Override
    public List<GameCategories> getAllGameCategories() {
        List<GameCategories> categories = new ArrayList<>();
        for (GameCategories category : GameCategories.values()) {
            categories.add(category);
        }
        return categories;
    }

}
