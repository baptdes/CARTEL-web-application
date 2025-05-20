package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Genre;
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
    public ResponseEntity<Book> getBookFromBNF(@PathVariable String isbn) {
        // TODO : Wait for Tom code
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves all authors in the system
     * 
     * @return ResponseEntity with list of author information
     */
    @GetMapping("/authors")
    public ResponseEntity<List<Map<String, String>>> getAllAuthors() {
        return ResponseEntity.ok(bookService.getAllAuthors());
    }

    /**
     * Retrieves all available book genres
     * 
     * @return List of all genre names
     */
    @GetMapping("/genres")
    public List<String> getAllGenres() {
        return bookService.getAllGenres();
    }

    /**
     * Adds a new book to the system
     * 
     * @param book The Book entity to add
     * @return ResponseEntity with the added book
     */
    @PostMapping("/add")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book addedBook = bookService.addBook(book);
        return ResponseEntity.ok(addedBook);
    }
}
