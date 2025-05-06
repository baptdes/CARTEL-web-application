package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Book.FormatBook;
import cartel.spring_boot_api.repository.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;

import java.io.StringReader;
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
    private BookRepository bookRepository;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Book> searchBooks(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) AuthorBook author,
        @RequestParam(required = false) FormatBook category
    ) {
        if (title != null && !title.isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null && !author.isEmpty()) {
            return bookRepository.findByAuthorContainingIgnoreCase(author);
        } else if (category != null) {
            return bookRepository.findByCategoryContainingIgnoreCase(category);
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
