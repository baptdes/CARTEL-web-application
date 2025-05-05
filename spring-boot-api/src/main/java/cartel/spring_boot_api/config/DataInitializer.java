package cartel.spring_boot_api.config;

import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataInitializer {

    @Autowired
    private BookRepository bookRepository;

    @Bean
    @Profile("dev") // Only run in development mode
    public CommandLineRunner initData() {
        return args -> {
            Book book1 = new Book("Le Seigneur des Anneaux", "J.R.R. Tolkien", 
                "Une épopée fantastique dans un monde imaginaire où différentes races s'allient pour détruire un anneau maléfique.");
            book1.setCategory("Fantasy");
            book1.setPublicationYear(1954);
            book1.setCoverImage("https://www.monsieurde.com/14385-home_default/trio.jpg");
            
            Book book2 = new Book("Fondation", "Isaac Asimov", 
                "Dans un futur lointain, un mathématicien développe une science permettant de prédire l'avenir de l'humanité.");
            book2.setCategory("Science Fiction");
            book2.setPublicationYear(1951);
            
            Book book3 = new Book("Dune", "Frank Herbert", 
                "Sur une planète désertique, un jeune homme doté de capacités exceptionnelles mène un peuple vers la révolution.");
            book3.setCategory("Science Fiction");
            book3.setPublicationYear(1965);

            Book book4 = new Book("Blue Lock 2", "Muneyuki Kaneshiro", 
                "Un jeune attaquant japonais se retrouve dans un camp d'entraînement où il doit rivaliser avec d'autres joueurs pour devenir le meilleur buteur du monde.");
            book4.setCategory("Manga");
            book4.setPublicationYear(2020);
            book4.setCoverImage("https://m.media-amazon.com/images/I/81K4UYM3KGL.jpg");
            
            bookRepository.save(book1);
            bookRepository.save(book2);
            bookRepository.save(book3);
            bookRepository.save(book4);
        };
    }
}
