package cartel.spring_boot_api.config;

import cartel.spring_boot_api.model.*;
import cartel.spring_boot_api.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import net.datafaker.Faker;

@Configuration
public class DataInitializer {

    private final Faker faker = new Faker();
    private final Random random = new Random();

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
    private ExchangeRepository exchangeRepository;
    @Autowired
    private SuggestionRepository suggestionRepository;

    // Number of entities to generate
    private final int numAuthors = 20;
    private final int numIllustrators = 15;
    private final int numPublishersBook = 10;
    private final int numSeries = 8;
    private final int numBooks = 30;
    private final int numCreators = 12;
    private final int numPublishersJDS = 8;
    private final int numJDS = 20;
    private final int numExtensions = 10;
    private final int numCartelPersons = 25;
    private final int numLoans = 15;
    private final int numSuggestions = 10;
    private final int numItemCopies = 40;

    @Bean
    @Profile("dev") // Only run in development mode
    public CommandLineRunner initData() {
        return args -> {
            System.out.println("Seeding database for development environment...");
            
            System.out.println("Loading Author Book data...");
            List<AuthorBook> authors = loadAuthorBookData(numAuthors);
            
            System.out.println("Loading Illustrator data...");
            List<Illustrator> illustrators = loadIllustratorData(numIllustrators);
            
            System.out.println("Loading Publisher Book data...");
            List<PublisherBook> publishersBook = loadPublisherBookData(numPublishersBook);
            
            System.out.println("Loading Series data...");
            List<Serie> series = loadSerieData(numSeries);
            
            System.out.println("Loading Creator data...");
            List<Creator> creators = loadCreatorData(numCreators);
            
            System.out.println("Loading Publisher JDS data...");
            List<PublisherJDS> publishersJDS = loadPublisherJDSData(numPublishersJDS);
            
            System.out.println("Loading Book data...");
            List<Book> books = loadBookData(numBooks, authors, illustrators, publishersBook, series);
            
            System.out.println("Loading JDS data...");
            List<JDS> jdsList = loadJDSData(numJDS, creators, publishersJDS);
            
            System.out.println("Loading Extension data...");
            List<Extension> extensions = loadExtensionData(numExtensions, creators, publishersJDS, jdsList);
            
            System.out.println("Loading Cartel Person data...");
            List<CartelPerson> cartelPersons = loadCartelPersonData(numCartelPersons);
            
            System.out.println("Loading Item Copy data...");
            List<ItemCopy> itemCopies = loadItemCopyData(numItemCopies);
            
            System.out.println("Loading Loan data...");
            List<Loan> loans = loadLoanData(numLoans, cartelPersons);
            
            System.out.println("Loading Suggestion data...");
            loadSuggestionData(numSuggestions);
            
            System.out.println("Seeding completed!");
        };
    }
    
    private List<AuthorBook> loadAuthorBookData(int count) {
        List<AuthorBook> authors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AuthorBook author = new AuthorBook(
                faker.name().lastName(),
                faker.name().firstName()
            );
            authorBookRepository.save(author);
            authors.add(author);
        }
        return authors;
    }
    
    private List<Illustrator> loadIllustratorData(int count) {
        List<Illustrator> illustrators = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Illustrator illustrator = new Illustrator(
                faker.name().lastName(),
                faker.name().firstName()
            );
            illustratorRepository.save(illustrator);
            illustrators.add(illustrator);
        }
        return illustrators;
    }
    
    private List<PublisherBook> loadPublisherBookData(int count) {
        List<PublisherBook> publishers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PublisherBook publisher = new PublisherBook(
                faker.book().publisher()
            );
            publisherBookRepository.save(publisher);
            publishers.add(publisher);
        }
        return publishers;
    }
    
    private List<Serie> loadSerieData(int count) {
        List<Serie> series = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Serie serie = new Serie(
                faker.book().title() + " Series"
            );
            serieRepository.save(serie);
            series.add(serie);
        }
        return series;
    }
    
    private List<Creator> loadCreatorData(int count) {
        List<Creator> creators = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Creator creator = new Creator(
                faker.name().lastName(),
                faker.name().firstName()
            );
            creatorRepository.save(creator);
            creators.add(creator);
        }
        return creators;
    }
    
    private List<PublisherJDS> loadPublisherJDSData(int count) {
        List<PublisherJDS> publishers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PublisherJDS publisher = new PublisherJDS(
                faker.company().name()
            );
            publisherjdsRepository.save(publisher);
            publishers.add(publisher);
        }
        return publishers;
    }
    
    private List<Book> loadBookData(int count, List<AuthorBook> authors, List<Illustrator> illustrators, 
                                   List<PublisherBook> publishers, List<Serie> series) {
        List<Book> books = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            // Create random collections
            Collection<AuthorBook> bookAuthors = getRandomSubList(authors, 1, 3);
            Collection<Illustrator> bookIllustrators = getRandomSubList(illustrators, 0, 2);
            Collection<Book.GenreBook> genres = getRandomGenres();
            
            // Generate ISBN (13 digits)
            String isbn = "978" + IntStream.range(0, 10)
                .mapToObj(n -> String.valueOf(random.nextInt(10)))
                .collect(Collectors.joining());
            
            Book book = new Book(
                isbn,
                faker.book().title(),
                bookAuthors,
                getRandomElement(publishers),
                faker.number().numberBetween(1990, 2023),
                getRandomFormatBook(),
                getRandomLangue(),
                genres
            );
            
            if (bookIllustrators.size() > 0) {
                book.setIllustrator(bookIllustrators);
            }
            
            // 70% chance to be part of a series
            if (random.nextInt(100) < 70) {
                book.setSerie(getRandomElement(series));
                book.setTome(random.nextInt(10) + 1);
            }
            
            book.setDescription(faker.lorem().paragraph(3));
            book.setCoverImage("https://covers.openlibrary.org/b/id/" + (random.nextInt(10000000) + 1) + "-L.jpg");
            
            itemRepository.save(book);
            books.add(book);
        }
        
        return books;
    }
    
    private List<JDS> loadJDSData(int count, List<Creator> creators, List<PublisherJDS> publishers) {
        List<JDS> jdsList = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Collection<Creator> gameCreators = getRandomSubList(creators, 1, 3);
            Collection<JDS.CategoryJDS> categories = getRandomCategories();
            
            String barcode = IntStream.range(0, 13)
                .mapToObj(n -> String.valueOf(random.nextInt(10)))
                .collect(Collectors.joining());
            
            String avgPlaytime = random.nextInt(4) + 1 + "h";
            int minPlayers = random.nextInt(3) + 1;
            int maxPlayers = minPlayers + random.nextInt(8);
            
            // Replace faker.game().title() with a more widely available method
            JDS jds = new JDS(
                avgPlaytime,
                minPlayers,
                maxPlayers,
                faker.app().name() + " " + faker.company().buzzword(),
                gameCreators,
                getRandomElement(publishers),
                faker.number().numberBetween(1995, 2023),
                getRandomLangue(),
                barcode,
                categories
            );
            
            jds.setDescription(faker.lorem().paragraph(2));
            jds.setCoverImage("https://picsum.photos/id/" + (random.nextInt(1000) + 1) + "/200/300");
            
            itemRepository.save(jds);
            jdsList.add(jds);
        }
        
        return jdsList;
    }
    
    private List<Extension> loadExtensionData(int count, List<Creator> creators, List<PublisherJDS> publishers, List<JDS> jdsList) {
        List<Extension> extensions = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Collection<Creator> extCreators = getRandomSubList(creators, 1, 2);
            JDS baseGame = getRandomElement(jdsList);
            
            String barcode = IntStream.range(0, 13)
                .mapToObj(n -> String.valueOf(random.nextInt(10)))
                .collect(Collectors.joining());
            
            Extension extension = new Extension(
                baseGame.getName() + ": " + faker.marketing().buzzwords(),
                extCreators,
                baseGame.getPublisher(),
                baseGame.getPublicationYear() + random.nextInt(3) + 1,
                baseGame.getLangue(),
                baseGame,
                barcode
            );
            
            extension.setDescription(faker.lorem().paragraph(1));
            extension.setCoverImage("https://picsum.photos/id/" + (random.nextInt(1000) + 1) + "/200/300");
            
            itemRepository.save(extension);
            extensions.add(extension);
        }
        
        return extensions;
    }
    
    private List<CartelPerson> loadCartelPersonData(int count) {
        List<CartelPerson> persons = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            CartelPerson person = new CartelPerson(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress()
            );
            
            person.setCaution(random.nextInt(4) * 25 + 25); // 25, 50, 75, or 100
            
            cartelPersonRepository.save(person);
            persons.add(person);
        }
        
        return persons;
    }
    
    private List<ItemCopy> loadItemCopyData(int count) {
        List<ItemCopy> copies = new ArrayList<>();
        List<Item> allItems = itemRepository.findAll();
        
        for (int i = 0; i < count; i++) {
            Item item = getRandomElement(allItems);
            ItemCopy copy = new ItemCopy(item);
            
            itemCopyRepository.save(copy);
            copies.add(copy);
        }
        
        return copies;
    }
    
    private List<Loan> loadLoanData(int count, List<CartelPerson> persons) {
        List<Loan> loans = new ArrayList<>();
        List<Item> availableItems = itemRepository.findAll().stream()
            .filter(item -> item.getStatut() == null)
            .collect(Collectors.toList());
        
        for (int i = 0; i < Math.min(count, availableItems.size()); i++) {
            Item item = availableItems.get(i);
            CartelPerson borrower = getRandomElement(persons);
            
            Loan loan = new Loan(item, borrower);
            
            // Set a random loan date in the past month
            loan.setLoanDate(LocalDateTime.now().minusDays(random.nextInt(30)));
            
            exchangeRepository.save(loan);
            loans.add(loan);
        }
        
        return loans;
    }
    
    private void loadSuggestionData(int count) {
        String[] suggestionNames = {
            "Harry Potter and the Cursed Child",
            "One Piece Tome 105",
            "Dungeons & Dragons - Player's Handbook",
            "Everdell Board Game",
            "Dune - Complete Edition",
            "Catan: Cities & Knights",
            "The Witcher - Last Wish",
            "Naruto Complete Box Set",
            "Attack on Titan Final Season Art Book",
            "Game of Thrones - Illustrated Edition",
            "Spyfall Party Game",
            "Pandemic Legacy Season 3",
            "Star Wars: Rebellion Board Game",
            "Sherlock Holmes: Consulting Detective",
            "Foundation - Isaac Asimov"
        };
        
        for (int i = 0; i < count; i++) {
            String name;
            if (i < suggestionNames.length) {
                name = suggestionNames[i];
            } else {
                name = faker.book().title();
            }
            
            Suggestion suggestion = new Suggestion(
                name,
                getRandomSuggestionType()
            );
            
            suggestionRepository.save(suggestion);
        }
    }
    
    // Helper methods
    private <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
    
    private <T> Collection<T> getRandomSubList(List<T> list, int minSize, int maxSize) {
        int size = random.nextInt(maxSize - minSize + 1) + minSize;
        Collection<T> result = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            T item;
            do {
                item = getRandomElement(list);
            } while (result.contains(item));
            
            result.add(item);
        }
        
        return result;
    }
    
    private Collection<Book.GenreBook> getRandomGenres() {
        Book.GenreBook[] allGenres = Book.GenreBook.values();
        int numGenres = random.nextInt(3) + 1;
        Collection<Book.GenreBook> genres = new ArrayList<>();
        
        for (int i = 0; i < numGenres; i++) {
            Book.GenreBook genre;
            do {
                genre = allGenres[random.nextInt(allGenres.length)];
            } while (genres.contains(genre));
            
            genres.add(genre);
        }
        
        return genres;
    }
    
    private Collection<JDS.CategoryJDS> getRandomCategories() {
        JDS.CategoryJDS[] allCategories = JDS.CategoryJDS.values();
        int numCategories = random.nextInt(3) + 1;
        Collection<JDS.CategoryJDS> categories = new ArrayList<>();
        
        for (int i = 0; i < numCategories; i++) {
            JDS.CategoryJDS category;
            do {
                category = allCategories[random.nextInt(allCategories.length)];
            } while (categories.contains(category));
            
            categories.add(category);
        }
        
        return categories;
    }
    
    private Book.FormatBook getRandomFormatBook() {
        Book.FormatBook[] formats = Book.FormatBook.values();
        return formats[random.nextInt(formats.length)];
    }
    
    private Item.Langues getRandomLangue() {
        Item.Langues[] langues = Item.Langues.values();
        return langues[random.nextInt(langues.length)];
    }
    
    private Suggestion.TypeSuggestion getRandomSuggestionType() {
        Suggestion.TypeSuggestion[] types = Suggestion.TypeSuggestion.values();
        return types[random.nextInt(types.length)];
    }
}
