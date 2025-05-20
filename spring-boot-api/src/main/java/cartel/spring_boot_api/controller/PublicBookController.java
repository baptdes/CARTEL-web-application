package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Genre;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Book.BookFormat;
import cartel.spring_boot_api.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Book entities through public endpoints
 */
@RestController
@RequestMapping("/api/public/books")
public class PublicBookController {

    @Autowired
    private BookService bookService;

    /**
     * Retrieves a specific book by ID
     * 
     * @param isnb The ID/barcode of the book to retrieve
     * @return ResponseEntity containing the book if found, or 404 Not Found
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByISBN(@PathVariable String isbn) {
        return bookService.getBookByISBN(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Filters books by various criteria with pagination
     * 
     * @param pageNumber The page number (0-based)
     * @param pageSize Number of items per page
     * @param asc Whether to sort in ascending order
     * @param sortBy Field to sort by
     * @param titleBook Filter by title
     * @param publisherName Filter by publisher name
     * @param authorFirstName Filter by author first name
     * @param authorSurname Filter by author surname
     * @param illustratorFirstName Filter by illustrator first name
     * @param illustratorSurname Filter by illustrator surname
     * @param category Filter by book format
     * @param serieName Filter by series name
     * @return List of books matching the criteria
     */
    @GetMapping
    public List<Book> filterBooks(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "true") boolean asc,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String titleBook,
            @RequestParam(required = false) String publisherName,
            @RequestParam(required = false) String authorFirstName,
            @RequestParam(required = false) String authorSurname,
            @RequestParam(required = false) String illustratorFirstName,
            @RequestParam(required = false) String illustratorSurname,
            @RequestParam(required = false) BookFormat category,
            @RequestParam(required = false) String serieName,
            @RequestParam(required = false) String genreName) {
        
        return bookService.filterBooks(pageNumber, pageSize, asc, sortBy, titleBook, 
                publisherName, authorFirstName, authorSurname, illustratorFirstName, 
                illustratorSurname, category, serieName, genreName);
    }

    /**
     * Get book details by ISBN code from BNF API
     * 
     * @param isbn The ISBN code of the book
     * @return 
     */
    @PostMapping("/bnf/{isbn}")
    public ResponseEntity<?> getBookFromBNF(@PathVariable String isbn) {
        try {
            Book bookInfo = bookService.getBookWithISBN(isbn);
            return ResponseEntity.ok(bookInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
        
    }

    /**
     * Retrieves all available genres
     * 
     * @return List of all genres
     */
    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return bookService.getAllGenres();
    }

    /**
     * Adds a new genre to the system
     * 
     * @param name The name of the genre to add
     * @return ResponseEntity with the added genre or error if genre exists
     */
    @PostMapping("/genres")
    public ResponseEntity<?> addGenre(@RequestParam String name) {
        try {
            Genre addedGenre = bookService.addGenre(name);
            return ResponseEntity.ok(addedGenre);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    /**
     * Retrieves all available authors
     * 
     * @return List of all authors
     */
    @GetMapping("/authors")
    public List<AuthorBook> getAllAuthors() {
        return bookService.getAllAuthors();
    }

    /** 
     * Adds a new author to the system
     * 
     * @param firstname The author's first name
     * @param surname The author's surname
     * @return ResponseEntity with the added author
     */
    @PostMapping("/authors")
    public ResponseEntity<?> addAuthor(@RequestParam String firstname, @RequestParam String surname) {
        try {
            AuthorBook addedAuthor = bookService.addAuthor(firstname, surname);
            return ResponseEntity.ok(addedAuthor);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    /**
     * Retrieves all available publishers
     * 
     * @return List of all publishers
     */
    @GetMapping("/publishers")
    public List<PublisherBook> getAllPublishers() {
        return bookService.getAllPublishers();
    }

    /**
     * Adds a new publisher to the system
     * 
     * @param name The name of the publisher to add
     * @return ResponseEntity with the added publisher
     */
    @PostMapping("/publishers")
    public ResponseEntity<?> addPublisher(@RequestParam String name) {
        try {
            PublisherBook addedPublisher = bookService.addPublisher(name);
            return ResponseEntity.ok(addedPublisher);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    /**
     * Retrieves all available illustrators
     * 
     * @return List of all illustrators
     */
    @GetMapping("/illustrators")
    public List<Illustrator> getAllIllustrators() {
        return bookService.getAllIllustrators();
    }

    /**
     * Adds a new illustrator to the system
     * 
     * @param firstname The illustrator's first name
     * @param surname The illustrator's surname
     * @return ResponseEntity with the added illustrator
     */
    @PostMapping("/illustrators")
    public ResponseEntity<?> addIllustrator(@RequestParam String firstname, @RequestParam String surname) {
        try {
            Illustrator addedIllustrator = bookService.addIllustrator(firstname, surname);
            return ResponseEntity.ok(addedIllustrator);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    /**
     * Adds a new book to the system
     * 
     * @param book The Book entity to add
     * @return ResponseEntity with the added book
     */
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book addedBook = bookService.addBook(book);
        return ResponseEntity.ok(addedBook);
    }

    /**
     * Deletes a book by its ISBN
     * 
     * @param isbn The ISBN of the book to delete
     * @return ResponseEntity with no content if successful
     */
    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> deleteBook(@PathVariable String isbn) {
        try {
            bookService.deleteBook(isbn);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
