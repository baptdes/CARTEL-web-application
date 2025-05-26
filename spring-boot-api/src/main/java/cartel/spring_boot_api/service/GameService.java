package cartel.spring_boot_api.service;

import cartel.spring_boot_api.model.AuthorGame;
import cartel.spring_boot_api.model.PublisherGame;
import cartel.spring_boot_api.model.Game;
import cartel.spring_boot_api.model.Game.GameCategories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GameService {
    List<Game> getAllGames();

    Optional<Game> getGameByBarcode(String barcode);

    List<Game> filterGames(int pageNumber, int pageSize, boolean asc, String sortBy,
                         String titleGame, String publisherName,
                         String creatorFirstName, String creatorSurname,
                         Integer minPlayers, Integer maxPlayers,
                         Integer minPlaytime, Integer maxPlaytime,
                         GameCategories category);

    List<AuthorGame> getAllCreators();

    List<AuthorGame> getAuthorsByCompleteName(String nameLike);

    List<GameCategories> getAllGameCategories();

    List<PublisherGame> getPublishersByName(String nameLike);

    // AuthorGame findOrCreateCreator(String firstname, String surname);
}
