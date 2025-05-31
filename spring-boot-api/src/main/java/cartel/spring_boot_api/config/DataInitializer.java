package cartel.spring_boot_api.config;

import cartel.spring_boot_api.model.*;
import cartel.spring_boot_api.repository.*;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.Date; // Changed from java.util.Date to java.sql.Date
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
    private GameRepository jdsRepository;
    @Autowired
    private AuthorGameRepository creatorRepository;
    @Autowired
    private ExtensionRepository extensionRepository;
    @Autowired
    private PublisherGameRepository publisherjdsRepository;
    @Autowired
    private ItemCopyRepository itemCopyRepository;
    @Autowired 
    private ItemRepository itemRepository;
    @Autowired
    private CartelPersonRepository cartelPersonRepository;
    @Autowired
    private SuggestionRepository suggestionRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private LoanByCartelRepository loanByCartelRepository;
    @Autowired
    private LoanToCartelRepository loanToCartelRepository;

    // Number of entities to generate
    private final int numAuthors = 20;
    private final int numIllustrators = 15;
    private final int numPublishersBook = 10;
    private final int numSeries = 8;
    private final int numBooks = 30;
    private final int numCreators = 12;
    private final int numPublishersJDS = 8;
    private final int numJDS = 30;
    private final int numExtensions = 10;
    private final int numCartelPersons = 25;
    private final int numSuggestions = 10;
    private final int numItemCopies = 40;
    private final int numGenres = 15;
    private final int numLoanByCartel = 12;
    private final int numLoanToCartel = 12;



    @Bean
    @Profile("dev") // Only run in development mode
    public CommandLineRunner initData() {
        return args -> {
            System.out.println("Seeding database for development environment...");
            
            System.out.println("Loading Genre data...");
            List<Genre> genres = loadGenreData(numGenres);
            
            System.out.println("Loading Author Book data...");
            List<AuthorBook> authors = loadAuthorBookData(numAuthors);
            
            System.out.println("Loading Illustrator data...");
            List<Illustrator> illustrators = loadIllustratorData(numIllustrators);
            
            System.out.println("Loading Publisher Book data...");
            List<PublisherBook> publishersBook = loadPublisherBookData(numPublishersBook);
            
            System.out.println("Loading Creator data...");
            List<AuthorGame> creators = loadCreatorData(numCreators);
            
            System.out.println("Loading Publisher JDS data...");
            List<PublisherGame> publishersJDS = loadPublisherJDSData(numPublishersJDS);
            
            System.out.println("Loading Book data...");
            List<Book> books = loadBookData(numBooks, authors, illustrators, publishersBook, null, genres); // Pass null for series
            
            System.out.println("Loading JDS data...");
            List<Game> jdsList = loadJDSData(numJDS, creators, publishersJDS);
            
            System.out.println("Loading Cartel Person data...");
            List<CartelPerson> cartelPersons = loadCartelPersonData(numCartelPersons);
            
            System.out.println("Loading Item Copy data...");
            List<ItemCopy> itemCopies = loadItemCopyData(numItemCopies);
            
            System.out.println("Loading LoanByCartel data...");
            List<LoanByCartel> loanByCartel = loadLoanByCartelData(numLoanByCartel);

            System.out.println("Loading LoanToCartel data...");
            List<LoanToCartel> loanToCartel = loadLoanToCartelData(numLoanToCartel);

            System.out.println("Loading Suggestion data...");
            loadSuggestionData(numSuggestions);
            
            System.out.println("Seeding completed!");
        };
    }
    
    private List<Genre> loadGenreData(int count) {
        String[] genreNames = {
            "Fantasy", "Science Fiction", "Mystery", "Thriller", "Romance", 
            "Horror", "Adventure", "Historical Fiction", "Biography", "Self-Help",
            "Dystopian", "Young Adult", "Comic", "Manga", "Crime", "Philosophy",
            "Poetry", "Drama", "Cookbook", "Children's"
        };
        
        List<Genre> genres = new ArrayList<>();
        
        // Use the predefined names first
        for (int i = 0; i < Math.min(count, genreNames.length); i++) {
            Genre genre = new Genre();
            genre.setName(genreNames[i]);
            genreRepository.save(genre);
            genres.add(genre);
        }
        
        // If we need more genres than predefined names, generate random ones
        for (int i = genreNames.length; i < count; i++) {
            Genre genre = new Genre();
            genre.setName(faker.book().genre());
            genreRepository.save(genre);
            genres.add(genre);
        }
        
        return genres;
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
    
    private List<AuthorGame> loadCreatorData(int count) {
        List<AuthorGame> creators = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AuthorGame creator = new AuthorGame(
                faker.name().lastName(),
                faker.name().firstName()
            );
            creatorRepository.save(creator);
            creators.add(creator);
        }
        return creators;
    }
    
    private List<PublisherGame> loadPublisherJDSData(int count) {
        List<PublisherGame> publishers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PublisherGame publisher = new PublisherGame(
                faker.company().name()
            );
            publisherjdsRepository.save(publisher);
            publishers.add(publisher);
        }
        return publishers;
    }
    
    private List<Book> loadBookData(int count, List<AuthorBook> authors, List<Illustrator> illustrators, 
                                   List<PublisherBook> publishers, List<Series> series, List<Genre> genres) {
        List<Book> books = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            // Create random collections
            Collection<AuthorBook> bookAuthors = getRandomSubList(authors, 1, 3);
            Collection<Illustrator> bookIllustrators = getRandomSubList(illustrators, 0, 2);
            Collection<Genre> bookGenres = getRandomSubList(genres, 1, 3);
            
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
                bookGenres
            );
            
            if (bookIllustrators.size() > 0) {
                book.setIllustrator(bookIllustrators);
            }
            
            // 70% chance to be part of a series
            if (random.nextInt(100) < 70 && series != null) {
                book.setSeries(getRandomElement(series));
                book.setVolumeNumber(random.nextInt(10) + 1);
            }
            
            book.setDescription(faker.lorem().paragraph(3));
            book.setCoverImage("https://covers.openlibrary.org/b/id/" + (random.nextInt(10000000) + 1) + "-L.jpg");
            
            itemRepository.save(book);
            books.add(book);
        }
        
        return books;
    }
    
    private List<Game> loadJDSData(int count, List<AuthorGame> creators, List<PublisherGame> publishers) {
        List<Game> jdsList = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Collection<AuthorGame> gameCreators = getRandomSubList(creators, 1, 3);
            Collection<Game.GameCategories> categories = getRandomCategories();
            
            String barcode = IntStream.range(0, 13)
                .mapToObj(n -> String.valueOf(random.nextInt(10)))
                .collect(Collectors.joining());
            
            int avgPlaytime = random.nextInt(4) + 1;
            int minPlayers = random.nextInt(3) + 1;
            int maxPlayers = minPlayers + random.nextInt(8);
            
            Game game = new Game(
                avgPlaytime,
                minPlayers,
                maxPlayers,
                faker.music().genre() + " " + faker.overwatch().hero(),
                gameCreators,
                getRandomElement(publishers),
                faker.number().numberBetween(1995, 2023),
                getRandomLangue(),
                barcode,
                categories
            );
            
            game.setDescription(faker.lorem().paragraph(2));
            game.setCoverImage("https://picsum.photos/id/" + (random.nextInt(1000) + 1) + "/200/300");
            
            itemRepository.save(game);
            jdsList.add(game);
        }
        
        return jdsList;
    }
    
    private List<CartelPerson> loadCartelPersonData(int count) {
        List<CartelPerson> persons = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            CartelPerson person = new CartelPerson(
                faker.leagueOfLegends().champion(),
                faker.leagueOfLegends().rank(),
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

    private List<LoanByCartel> loadLoanByCartelData(int count) {
        List<LoanByCartel> loanBy = new ArrayList<>();
        List<ItemCopy> allItemCopies = itemCopyRepository.findAll();
        List<CartelPerson> allPerson = cartelPersonRepository.findAll();
        List<ItemCopy> borrowItems = getRandomListAmong(allItemCopies, count, count);

        for (int i = 0; i < borrowItems.size(); i++) {
            ItemCopy item = borrowItems.get(i);

            // Ensure the item is borrowable
            if (!item.isBorrowable()) {
                continue;
            }

            CartelPerson borrower = getRandomElement(allPerson);
            LoanByCartel loan = new LoanByCartel(borrower, item);

            // Make some loans completed
            if (i % 3 == 0) {  // Every third loan is completed
                long loanTimeMillis = System.currentTimeMillis() - ((random.nextInt(20) + 10) * 24 * 60 * 60 * 1000L);
                long endTimeMillis = loanTimeMillis + ((random.nextInt(10) + 1) * 24 * 60 * 60 * 1000L);

                loan.setLoanDate(new Date(loanTimeMillis));
                loan.setEndDate(new Date(endTimeMillis));
            }

            loanByCartelRepository.save(loan);
            loanBy.add(loan);
        }
        return loanBy;
    }

    private List<LoanToCartel> loadLoanToCartelData(int count) {
        List<LoanToCartel> loanTo = new ArrayList<>();
        List<ItemCopy> allItemCopies = itemCopyRepository.findAll();
        List<CartelPerson> allPerson = cartelPersonRepository.findAll();
        List<ItemCopy> lentItems = getRandomListAmong(allItemCopies, count, count);

        for (int i = 0; i < lentItems.size(); i++) {
            ItemCopy item = lentItems.get(i);

            // Ensure the item is available
            if (!item.isAvailable()) {
                continue;
            }

            CartelPerson owner = getRandomElement(allPerson);
            LoanToCartel loan = new LoanToCartel(owner, item);

            // Make some loans completed
            if (i % 3 == 0) {  // Every third loan is completed
                long loanTimeMillis = System.currentTimeMillis() - ((random.nextInt(20) + 10) * 24 * 60 * 60 * 1000L);
                long endTimeMillis = loanTimeMillis + ((random.nextInt(10) + 1) * 24 * 60 * 60 * 1000L);

                loan.setLoanDate(new Date(loanTimeMillis));
                loan.setEndDate(new Date(endTimeMillis));
            }

            loanToCartelRepository.save(loan);
            loanTo.add(loan);
        }
        return loanTo;
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
                getRandomSuggestionType(),
                faker.lorem().paragraph(2)
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

    private <T> List<T> getRandomListAmong(List<T> list, int minSize, int maxSize) {
        int size = random.nextInt(maxSize - minSize + 1) + minSize;
        List<T> result = new ArrayList<>();

        while (result.size() < size) {
            T item = getRandomElement (list);
            if (!result.contains(item)) {
                result.add(item);
            }
        }
        System.out.println(result);
        return result;
    }
    
    private Collection<Game.GameCategories> getRandomCategories() {
        Game.GameCategories[] allCategories = Game.GameCategories.values();
        int numCategories = random.nextInt(3) + 1;
        Collection<Game.GameCategories> categories = new ArrayList<>();
        
        for (int i = 0; i < numCategories; i++) {
            Game.GameCategories category;
            do {
                category = allCategories[random.nextInt(allCategories.length)];
            } while (categories.contains(category));
            
            categories.add(category);
        }
        
        return categories;
    }
    
    private Book.BookFormat getRandomFormatBook() {
        Book.BookFormat[] formats = Book.BookFormat.values();
        return formats[random.nextInt(formats.length)];
    }
    
    private Item.Languages getRandomLangue() {
        Item.Languages[] langues = Item.Languages.values();
        return langues[random.nextInt(langues.length)];
    }
    
    private Suggestion.TypeSuggestion getRandomSuggestionType() {
        Suggestion.TypeSuggestion[] types = Suggestion.TypeSuggestion.values();
        return types[random.nextInt(types.length)];
    }
}
