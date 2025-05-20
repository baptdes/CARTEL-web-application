package cartel.spring_boot_api.service;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Genre;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Book.BookFormat;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Book entities
 */
public interface BookService {
    /**
     * Retrieves all books in the system
     * 
     * @return List of all Item entities
     */
    List<Book> getAllBooks();
    
    /**
     * Retrieves a book by its ID
     * 
     * @param isbn The barcode/ID of the book to retrieve
     * @return An Optional containing the book if found, empty otherwise
     */
    Optional<Book> getBookByISBN(String id);
    
    /**
     * Filters books based on various criteria with pagination
     * 
     * @param pageNumber The page number to retrieve (0-based)
     * @param pageSize The number of items per page
     * @param asc Whether to sort in ascending order
     * @param sortBy The field to sort by
     * @param titleBook Filter by title containing this string
     * @param publisherName Filter by publisher name
     * @param authorFirstName Filter by author first name
     * @param authorSurname Filter by author last name
     * @param illustratorFirstName Filter by illustrator first name
     * @param illustratorSurname Filter by illustrator last name
     * @param category Filter by book format category
     * @param serieName Filter by series name
     * @param genreName Filter by genre name
     * @return List of books matching the criteria
     */
    List<Book> filterBooks(int pageNumber, int pageSize, boolean asc, String sortBy,
                          String titleBook, String publisherName,
                          String authorFirstName, String authorSurname,
                          String illustratorFirstName, String illustratorSurname,
                          BookFormat category, String serieName, String genreName);
    
    /**
     * Imports book information from an external API using ISBN
     * 
     * @param isbn The ISBN code of the book to import
     * @return The Book entity associated with the given ISBN
     * @throws RuntimeException if import fails
     */
    Book getBookWithISBN(String isbn);
    
    /**
     * Retrieves all authors in the system
     * 
     * @return List of all authors
     */
    List<AuthorBook> getAllAuthors();

    /**
     * Add a new author
     * @param firstname The author's first name
     * @param surname The author's surname
     * @return The added AuthorBook entity
     * @throws RuntimeException if the author already exists
     */
    AuthorBook addAuthor(String firstname, String surname);
    
    /**
     * Returns all available book
     * 
     * @return List of all genre
     */
    List<Genre> getAllGenres();

    /**
     * Add a new genre
     * @param name The genre name
     * @return The added Genre entity
     * @throws RuntimeException if the genre already exists
     */
    Genre addGenre(String name);

    /**
     * Returns all available publisher
     * 
     * @return List of all publisher
     */
    List<PublisherBook> getAllPublishers();

    /**
     * Add a new publisher
     * @param name The publisher name
     * @return The added PublisherBook entity
     * @throws RuntimeException if the publisher already exists
     */
    PublisherBook addPublisher(String name);

    /**
     * Returns all available illustrator
     * 
     * @return List of all illustrator
     */
    List<Illustrator> getAllIllustrators();

    /**
     * Add a new illustrator
     * @param firstname
     * @param surname
     * @return The added Illustrator entity
     * @throws RuntimeException if the illustrator already exists
     */
    Illustrator addIllustrator(String firstname, String surname);
    
    /**
     * Finds an author by name or creates a new one if not found
     * 
     * @param firstname The author's first name
     * @param surname The author's surname
     * @return The found or newly created AuthorBook entity
     * @throws RuntimeException if multiple authors found with the same name
     */
    AuthorBook findOrCreateAuthor(String firstname, String surname);

    /**
     * Adds a new book
     * 
     * @param book The Book entity to add
     * @return The added Book entity
     * @throws RuntimeException if the book already exists
     */
    Book addBook(Book book);

    /**
     * Deletes a book by its ISBN
     * 
     * @param isbn The ISBN of the book to delete
     * @throws RuntimeException if the book doesn't exist or cannot be deleted
     */
    void deleteBook(String isbn);
}
