package cartel.spring_boot_api.config;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Serie;
import cartel.spring_boot_api.model.Book.FormatBook;
import cartel.spring_boot_api.repository.AuthorBookRepository;
import cartel.spring_boot_api.repository.BookRepository;
import cartel.spring_boot_api.repository.IllustratorRepository;
import cartel.spring_boot_api.repository.PublisherBookRepository;
import cartel.spring_boot_api.repository.SerieRepository;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataInitializer {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorBookRepository authorBookRepository;
    @Autowired
    private IllustratorRepository illustratorRepository;
    @Autowired
    private PublisherBookRepository publisherBookRepository;
    @Autowired
    private SerieRepository serieRepository;

    @Bean
    @Profile("dev") // Only run in development mode
    public CommandLineRunner initData() {
        return args -> {
            AuthorBook author1 = new AuthorBook("J.R.R", "Tolkien");
            Collection<AuthorBook> collauthor1 = new ArrayList<AuthorBook>();
            collauthor1.add(author1);
            PublisherBook publisher1 = new PublisherBook("bayard");
            Illustrator ill1 = new Illustrator("truc", "bidule");
            Collection<Illustrator> collIll1 = new ArrayList<Illustrator>();
            collIll1.add(ill1);     
            Serie ser1 = new Serie("j'aime manger");       
            Book book1 = new Book("9766786786786","c'est parti les amis",collauthor1,publisher1,2015,FormatBook.MANGA);
            book1.setIllustrator(collIll1);
            book1.setSerie(ser1);
            
            authorBookRepository.save(author1);
            illustratorRepository.save(ill1);
            publisherBookRepository.save(publisher1);
            serieRepository.save(ser1);
            bookRepository.save(book1);
        };
    }
}
