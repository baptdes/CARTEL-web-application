package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Serie;
import cartel.spring_boot_api.model.Book.FormatBook;
import cartel.spring_boot_api.repository.AuthorBookRepository;
import cartel.spring_boot_api.repository.BookRepository;
import cartel.spring_boot_api.repository.IllustratorRepository;
import cartel.spring_boot_api.repository.ItemRepository;
import cartel.spring_boot_api.repository.PublisherBookRepository;
import cartel.spring_boot_api.repository.SerieRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@RestController
@RequestMapping("/api/public/books")
public class PublicBookController {

    @Autowired
    private ItemRepository itemRepository;
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


    @GetMapping
    public List<Item> getAllBooks() {
        return itemRepository.findAll();
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // Update a book
    @PutMapping("/update/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable String id, @RequestBody Book bookDetails) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(bookDetails.getTitle());
                    existingBook.setAuthor(bookDetails.getAuthor());
                    existingBook.setDescription(bookDetails.getDescription());
                    existingBook.setCoverImage(bookDetails.getCoverImage());
                    existingBook.setPublicationYear(bookDetails.getPublicationYear());
                    existingBook.setFormat(bookDetails.getFormat());
                    existingBook.setSerie(bookDetails.getSerie());
                    existingBook.setTome(bookDetails.getTome());
                    existingBook.setIllustrator(bookDetails.getIllustrator());
                    existingBook.setPublisher(bookDetails.getPublisher());
                    existingBook.setUpdatedAt(LocalDateTime.now());
                    
                    return ResponseEntity.ok(bookRepository.save(existingBook));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a book
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Book> searchBooks(
    @RequestParam(required = false) String title,
    @RequestParam(required = false) String authorFirstName,
    @RequestParam(required = false) String authorSurname,
    @RequestParam(required = false) FormatBook category,
    @RequestParam(required = false) Long illustratorId,
    @RequestParam(required = false) Long publisherId,
    @RequestParam(required = false) Long serieId
) {
    if (title != null && !title.isEmpty()) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    } else if (authorFirstName != null) {
        List<Book> authorL = new ArrayList<Book>();
        List<AuthorBook> authorF = authorBookRepository.findByFirstnameContainingIgnoreCase(authorFirstName);
        if(authorSurname != null){
            List<AuthorBook> authorS  = authorBookRepository.findBySurnameContainingIgnoreCase(authorSurname);
            for(AuthorBook aF : authorF){
                for (AuthorBook aS : authorS) {
                    if(aS.getId()==aF.getId()){
                        for(Book b : aS.getWrittenBook()){
                            if(!authorL.contains(b)){
                                authorL.add(b);
                            }
                        }
                    }
                }
            }
            return authorL;            
        } else {
            for(AuthorBook aF : authorF){
                for(Book b : aF.getWrittenBook()){
                    if(!authorL.contains(b)){
                        authorL.add(b);
                    }
                }
            }
        }
        return authorL;
    } else if (category != null) {
        return bookRepository.findByFormat(category);
    } else if (illustratorId != null) {
        Optional<Illustrator> illustrator = illustratorRepository.findById(illustratorId);
        return illustrator.map(bookRepository::findByIllustrator).orElse(List.of());
    } else if (publisherId != null) {
        Optional<PublisherBook> publisher = publisherBookRepository.findById(publisherId);
        return publisher.map(bookRepository::findByPublisher).orElse(List.of());
    } else if (serieId != null) {
        Optional<Serie> serie = serieRepository.findById(serieId);
        return serie.map(bookRepository::findBySerie).orElse(List.of());
    }
    return bookRepository.findAll();
}
    
    @PostMapping("/import/isbn/{isbn}")
    public ResponseEntity<?> addBookByIsbn(@PathVariable String isbn) {
        // Example logic to fetch book details and save to the database
        try {
            // Call external API to fetch book details (e.g., using RestTemplate or WebClient)
            Book book = fetchBookDetailsFromExternalApi(isbn);
    
            // Save the book to the database
            bookRepository.save(book);
    
            return ResponseEntity.ok("Book imported successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to import book: " + e.getMessage());
        }
    }

    @Deprecated
    private Book fetchBookDetailsFromExternalApi(String isbn) {
        // Use Dublin Core schema for easier parsing
        String bnfApiUrl = "https://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=bib.isbn=%22" + isbn + "%22&recordSchema=dublincore";
        
        try {
            // Create RestTemplate for making HTTP requests
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(bnfApiUrl, String.class);
            
            // Create DocumentBuilderFactory to parse XML response
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // Enable namespace support for Dublin Core
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource source = new InputSource(new StringReader(response));
            Document document = builder.parse(source);
            
            // Check if we have any records
            NodeList records = document.getElementsByTagName("srw:numberOfRecords");
            if (records.getLength() > 0) {
                String numberOfRecordsStr = records.item(0).getTextContent();
                if ("0".equals(numberOfRecordsStr)) {
                    throw new RuntimeException("No records found for ISBN: " + isbn);
                }
            }
            
            // Create a new book object
            Book book = new Book();
            book.setIsbn(isbn);
            
            // Extract Dublin Core elements
            NodeList dcElements = document.getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "*");
            
            // Default title in case none is found
            String title = "Book with ISBN: " + isbn;
            String author = "";
            String description = "";
            String coverImage = "";
            Integer publicationYear = null;
            String category = "";
            
            // Extract record identifier for cover image
            NodeList recordIdentifiers = document.getElementsByTagName("srw:recordIdentifier");
            String arkId = "";
            if (recordIdentifiers.getLength() > 0) {
                arkId = recordIdentifiers.item(0).getTextContent();
                coverImage = "https://catalogue.bnf.fr/couverture?&appName=NE&idArk=" + arkId + "&couverture=1";
            }
            
            // Process Dublin Core elements
            for (int i = 0; i < dcElements.getLength(); i++) {
                Element element = (Element) dcElements.item(i);
                String localName = element.getLocalName();
                String content = element.getTextContent();
                
                switch (localName) {
                    case "title":
                        title = content;
                        break;
                    case "creator":
                        if (!author.isEmpty()) {
                            author += ", ";
                        }
                        author += content;
                        break;
                    case "contributor":
                        if (!author.isEmpty()) {
                            author += ", ";
                        }
                        author += content;
                        break;
                    case "date":
                        try {
                            publicationYear = Integer.parseInt(content.trim());
                        } catch (NumberFormatException e) {
                            // Try to extract a 4-digit year from the string
                            String yearPattern = "\\b(\\d{4})\\b";
                            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(yearPattern);
                            java.util.regex.Matcher matcher = pattern.matcher(content);
                            if (matcher.find()) {
                                publicationYear = Integer.parseInt(matcher.group(1));
                            }
                        }
                        break;
                    case "description":
                        if (description.isEmpty()) {
                            description = content;
                        } else {
                            description += "\n" + content;
                        }
                        break;
                    case "subject":
                        if (category.isEmpty()) {
                            category = content;
                        }
                        break;
                    case "type":
                        if (category.isEmpty()) {
                            category = content;
                        }
                        break;
                }
            }
            
            // Set the extracted values to the book object
            book.setTitle(title);
            //book.setAuthor(author);
            book.setDescription(description);
            book.setPublicationYear(publicationYear);
            //book.setCategory(category);
            book.setCoverImage(coverImage);
            
            return book;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching book details from BnF API: " + e.getMessage(), e);
        }
    }
}
