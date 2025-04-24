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
            
            Book book2 = new Book("Fondation", "Isaac Asimov", 
                "Dans un futur lointain, un mathématicien développe une science permettant de prédire l'avenir de l'humanité.");
            book2.setCategory("Science Fiction");
            book2.setPublicationYear(1951);
            
            Book book3 = new Book("Dune", "Frank Herbert", 
                "Sur une planète désertique, un jeune homme doté de capacités exceptionnelles mène un peuple vers la révolution.");
            book3.setCategory("Science Fiction");
            book3.setPublicationYear(1965);
            
            bookRepository.save(book1);
            bookRepository.save(book2);
            bookRepository.save(book3);
        };
    }
}
