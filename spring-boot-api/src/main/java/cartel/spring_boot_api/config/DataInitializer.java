package cartel.spring_boot_api.config;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.CartelPerson;
import cartel.spring_boot_api.model.Creator;
import cartel.spring_boot_api.model.Extension;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.ItemCopy;
import cartel.spring_boot_api.model.JDS;
import cartel.spring_boot_api.model.Loan;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.PublisherJDS;
import cartel.spring_boot_api.model.Serie;
import cartel.spring_boot_api.model.Exchange;
import cartel.spring_boot_api.model.Book.FormatBook;
import cartel.spring_boot_api.model.Item.Langues;
import cartel.spring_boot_api.repository.AuthorBookRepository;
import cartel.spring_boot_api.repository.BookRepository;
import cartel.spring_boot_api.repository.CartelPersonRepository;
import cartel.spring_boot_api.repository.CreatorRepository;
import cartel.spring_boot_api.repository.ExtensionRepository;
import cartel.spring_boot_api.repository.IllustratorRepository;
import cartel.spring_boot_api.repository.ItemCopyRepository;
import cartel.spring_boot_api.repository.ItemRepository;
import cartel.spring_boot_api.repository.JDSRepository;
import cartel.spring_boot_api.repository.LoanRepository;
import cartel.spring_boot_api.repository.PublisherBookRepository;
import cartel.spring_boot_api.repository.PublisherJDSRepository;
import cartel.spring_boot_api.repository.SerieRepository;
import cartel.spring_boot_api.repository.ExchangeRepository;

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
    @Autowired
    private JDSRepository jdsRepository;
    @Autowired
    private CreatorRepository creatorRepository;
    @Autowired
    private ExtensionRepository extensionRepository;
    @Autowired
    private PublisherJDSRepository publisherjdsRepository;
    @Autowired
    private ItemCopyRepository itemCopyRepository;
    @Autowired 
    private ItemRepository itemRepository;
    @Autowired
    private CartelPersonRepository cartelPersonRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ExchangeRepository ExchangeRepository;

    @Bean
    @Profile("dev") // Only run in development mode
    public CommandLineRunner initData() {
        return args -> {
            AuthorBook  author1 = new AuthorBook("J.R.R", "Tolkien");
            Collection<AuthorBook> collauthor1 = new ArrayList<AuthorBook>();
            collauthor1.add(author1);
            PublisherBook publisher1 = new PublisherBook("bayard");
            Illustrator ill1 = new Illustrator("truc", "bidule");
            Collection<Illustrator> collIll1 = new ArrayList<Illustrator>();
            collIll1.add(ill1);     
            Collection<Book.GenreBook> genre1 = new ArrayList<Book.GenreBook>();
            genre1.add(Book.GenreBook.FANTASY);
            Serie ser1 = new Serie("j'aime manger");       
            Book book1 = new Book("9766786786786","c'est parti les amis",collauthor1,publisher1,2015,FormatBook.MANGA,Langues.FR);
            book1.setIllustrator(collIll1);
            book1.setSerie(ser1);
            
            authorBookRepository.save(author1);
            illustratorRepository.save(ill1);
            publisherBookRepository.save(publisher1);
            serieRepository.save(ser1);
            itemRepository.save(book1);

            Collection<AuthorBook> collauthor2 = new ArrayList<AuthorBook>();
            collauthor2.add(author1);
            PublisherBook publisher2 = new PublisherBook("bayard");
            Illustrator ill2 = new Illustrator("truc", "bidule");
            Collection<Illustrator> collIll2 = new ArrayList<Illustrator>();
            collIll2.add(ill2);     
            Collection<Book.GenreBook> genre2 = new ArrayList<Book.GenreBook>();
            genre2.add(Book.GenreBook.AVENTURE);
            Serie ser2 = new Serie("j'aime manger");       
            Book book2 = new Book("9766786786788","c'est parti les amis 2",collauthor2,publisher2,2025,FormatBook.MANGA,Langues.FR);
            book2.setIllustrator(collIll2);
            book2.setSerie(ser2);
            
            illustratorRepository.save(ill2);
            publisherBookRepository.save(publisher2);
            serieRepository.save(ser2);
            itemRepository.save(book2);

            Creator creator1 = new Creator("Jen", "pi");
            Collection<Creator> creatorl1 = new ArrayList<Creator>();
            creatorl1.add(creator1);
            PublisherJDS publisherj1 = new PublisherJDS("bayard2");
            Collection<JDS.CategoryJDS> category1 = new ArrayList<JDS.CategoryJDS>();
            category1.add(JDS.CategoryJDS.PLATEAU);
            category1.add(JDS.CategoryJDS.BLUFF);
            category1.add(JDS.CategoryJDS.ASYMETRIQUE);
            JDS jds1 =new JDS( "1h", 1, 4,  "Heroes",creatorl1, publisherj1, 1105, Langues.EN,"9999999999999", category1);
            creatorRepository.save(creator1);
            publisherjdsRepository.save(publisherj1);
            itemRepository.save(jds1);

            Extension jds2 = new Extension( "Heroes : outside",creatorl1, publisherj1,
            1110, Langues.EN,jds1,"999999999999");
            itemRepository.save(jds2);

            ItemCopy copyj1 = new ItemCopy(jds1);
            itemCopyRepository.save(copyj1);

            CartelPerson jean = new CartelPerson("jean", "pierre","jp@gmail.com");
            jean.setCaution(50);
            cartelPersonRepository.save(jean);
            Loan loan1 = new Loan(jds1,jean);
            ExchangeRepository.save(loan1);
        };
    }
}
