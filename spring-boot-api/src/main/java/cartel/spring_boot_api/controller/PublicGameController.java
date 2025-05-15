package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.Game;
import cartel.spring_boot_api.model.Game.GameCategories;
import cartel.spring_boot_api.service.GameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/games")
public class PublicGameController {

    @Autowired
    private GameService gameService;
    
    @GetMapping("/{barcode}")
    public ResponseEntity<Game> getGameByBarcode(@PathVariable String barcode) {
        return gameService.getGameByBarcode(barcode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Game> filterGames(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean asc,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String titleGame,
            @RequestParam(required = false) String publisherName,
            @RequestParam(required = false) String creatorFirstName,
            @RequestParam(required = false) String creatorSurname,
            @RequestParam(required = false) Integer minPlayers,
            @RequestParam(required = false) Integer maxPlayers,
            @RequestParam(required = false) Integer minPlaytime,
            @RequestParam(required = false) Integer maxPlaytime,
            @RequestParam(required = false) GameCategories category) {
        
        return gameService.filterGames(pageNumber, pageSize, asc, sortBy, titleGame, 
                publisherName, creatorFirstName, creatorSurname, minPlayers, 
                maxPlayers, minPlaytime, maxPlaytime, category);
    }

    @GetMapping("/creators")
    public ResponseEntity<List<Map<String, String>>> getAllCreators() {
        return ResponseEntity.ok(gameService.getAllCreators());
    }

    @GetMapping("/categories")
    public List<GameCategories> getAllGameCategories() {
        return gameService.getAllGameCategories();
    }
}

