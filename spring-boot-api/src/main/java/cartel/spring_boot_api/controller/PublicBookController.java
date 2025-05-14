package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.model.Book.FormatBook;
import cartel.spring_boot_api.model.Book.GenreBook;
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
     * Retrieves all books
     * 
     * @return List of all books in the system
     */
    @GetMapping
    public List<Item> getAllBooks() {
        return bookService.getAllBooks();
    }

    /**
     * Retrieves a specific book by ID
     * 
     * @param id The ID/barcode of the book to retrieve
     * @return ResponseEntity containing the book if found, or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing book
     * 
     * @param id The ID of the book to update
     * @param bookDetails The updated book data
     * @return ResponseEntity with updated book if successful, or 404 Not Found
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable String id, @RequestBody Book bookDetails) {
        Book updatedBook = bookService.updateBook(id, bookDetails);
        return updatedBook != null ? ResponseEntity.ok(updatedBook) : ResponseEntity.notFound().build();
    }

    /**
     * Deletes a book by its ID
     * 
     * @param id The ID of the book to delete
     * @return ResponseEntity with 200 OK if deleted, or 404 Not Found
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        boolean deleted = bookService.deleteBook(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
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
    @GetMapping("/filterBooks")
    public List<Book> filterBooks(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean asc,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String titleBook,
            @RequestParam(required = false) String publisherName,
            @RequestParam(required = false) String authorFirstName,
            @RequestParam(required = false) String authorSurname,
            @RequestParam(required = false) String illustratorFirstName,
            @RequestParam(required = false) String illustratorSurname,
            @RequestParam(required = false) FormatBook category,
            @RequestParam(required = false) String serieName) {
        
        return bookService.filterBooks(pageNumber, pageSize, asc, sortBy, titleBook, 
                publisherName, authorFirstName, authorSurname, illustratorFirstName, 
                illustratorSurname, category, serieName);
    }

    /**
     * Get book details by ISBN code
     * 
     * @param isbn The ISBN code of the book to import
     * @return 
     */
    @PostMapping("/import/isbn/{isbn}")
    public ResponseEntity<Book> addBookByIsbn(@PathVariable String isbn) {
        Book book = bookService.getBookWithISBN(isbn);
        return book != null ? ResponseEntity.ok(book) : ResponseEntity.notFound().build();
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
     * @return List of all genre enum values
     */
    @GetMapping("/genre")
    public List<GenreBook> getAllGenreBooks() {
        return bookService.getAllGenreBooks();
    }
}
