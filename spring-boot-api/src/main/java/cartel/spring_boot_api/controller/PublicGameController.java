package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.Game;
import cartel.spring_boot_api.repository.CreatorRepository;
import cartel.spring_boot_api.repository.GameRepository;
import cartel.spring_boot_api.repository.PublisherGameRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/public/games")
public class PublicGameController {

    @Autowired
    private GameRepository jdsRepository;
    @Autowired
    private CreatorRepository creatorRepository;
    @Autowired
    private PublisherGameRepository publisherjdsRepository;

    @GetMapping
    public List<Game> getAllGames() {
        return jdsRepository.findAll();
    }
}

