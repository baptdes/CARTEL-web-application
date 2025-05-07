package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.Item;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Serie;
import cartel.spring_boot_api.model.Book.FormatBook;
import cartel.spring_boot_api.model.Item.Langues;
import cartel.spring_boot_api.repository.AuthorBookRepository;
import cartel.spring_boot_api.repository.BookRepository;
import cartel.spring_boot_api.repository.IllustratorRepository;
import cartel.spring_boot_api.repository.ItemRepository;
import cartel.spring_boot_api.repository.PublisherBookRepository;
import cartel.spring_boot_api.repository.SerieRepository;

import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
                    existingBook.setName(bookDetails.getName());
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
        return bookRepository.findByNameContainingIgnoreCase(title);
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
        String bnfApiUrl = "https://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=bib.isbn=" + isbn + "";
        
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
            

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            //Elements of book
            // Default title in case none is found
            String title = "Book with ISBN: " + isbn;
            String coverImage = "";
            Integer publicationYear = null;
            String category = "";

            // Déclare les namespaces utilisés
            xpath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    switch (prefix) {
                        case "srw": return "http://www.loc.gov/zing/srw/";
                        case "mxc": return "info:lc/xmlns/marcxchange-v2";
                        default: return XMLConstants.NULL_NS_URI;
                    }
                }

                public String getPrefix(String uri) { return null; }
                public Iterator<String> getPrefixes(String uri) { return null; }
            });

            // Create a new book object
            Book book = new Book();
            //book.setBarcode(isbn); 

            // XPath pour le titre (datafield tag="200", subfield code="a")
            String titlePath = "//mxc:datafield[@tag='200']/mxc:subfield[@code='a']";
            Node titleNode = (Node) xpath.evaluate(titlePath, document, XPathConstants.NODE);
            title = titleNode != null ? titleNode.getTextContent() : "Titre non trouvé";

            // XPath pour le nom de l'auteur (datafield tag="700", subfield code="a")
            String authorlastnamePath = "//mxc:datafield[@tag='700']/mxc:subfield[@code='a']";
            Node authorlastnameNode = (Node) xpath.evaluate(authorlastnamePath, document, XPathConstants.NODE);
            String authorlastname = authorlastnameNode.getTextContent();

            // XPath pour le prénom de l'auteur (datafield tag="700", subfield code="b")
            String authorfirstnamePath = "//mxc:datafield[@tag='700']/mxc:subfield[@code='b']";
            Node authorfirstnameNode = (Node) xpath.evaluate(authorfirstnamePath, document, XPathConstants.NODE);
            String authorfirstname = authorfirstnameNode != null ? authorfirstnameNode.getTextContent() : "";

            // XPath pour le nom de l'auteur (datafield tag="700", subfield code="a")
            String authormorePath = "//mxc:datafield[@tag='701']";
            NodeList authormoreNode = (NodeList) xpath.evaluate(authormorePath, document, XPathConstants.NODESET);
            List<String> authors = new ArrayList<String>();
            for (int i = 0; i < authormoreNode.getLength(); i++) {
                Element datafield = (Element) authormoreNode.item(i);
                String nom = getSubfield(datafield, "a");
                String prenom = getSubfield(datafield, "b");
                authors.add(prenom);
                authors.add(nom);
            }

            //récupération de l'auteur
            //Collection<AuthorBook> listauth = new ArrayList<AuthorBoo
            //AuthorBook author = findTheAuthor(authorfirstname, authorlastname);
            //listauth.add(author);
            /*
            for (int index = 0; index < authors.size(); index+=2) {
                AuthorBook author = findTheAuthor( authors.get(index),  authors.get(index+1));
                listauth.add(author);
            }
            */

            // XPath pour le nom de l'éditeur (datafield tag="210", subfield code="c")
            String publishernamePath = "//mxc:datafield[@tag='210']/mxc:subfield[@code='c']";
            Node publishernameNode = (Node) xpath.evaluate(publishernamePath, document, XPathConstants.NODE);
            String publishername = publishernameNode.getTextContent();

            // XPath pour le nom de l'éditeur (datafield tag="210", subfield code="c")
            String PublicationYearPath = "//mxc:datafield[@tag='210']/mxc:subfield[@code='d']";
            Node PublicationYearNode = (Node) xpath.evaluate(PublicationYearPath, document, XPathConstants.NODE);
            String PublicationYear = PublicationYearNode.getTextContent();
            String[] pubyr = PublicationYear.split(" ");

            // XPath pour le nom de l'éditeur (datafield tag="210", subfield code="c")
            String descriptionPath = "//mxc:datafield[@tag='330']/mxc:subfield[@code='a']";
            Node descriptionNode = (Node) xpath.evaluate(descriptionPath, document, XPathConstants.NODE);
            String description = descriptionNode.getTextContent();

            // XPath pour le nom de l'éditeur (datafield tag="210", subfield code="c")
            String descriptionPath = "//mxc:datafield[@tag='330']/mxc:subfield[@code='a']";
            Node descriptionNode = (Node) xpath.evaluate(descriptionPath, document, XPathConstants.NODE);
            String description = descriptionNode.getTextContent();

            //récupération de l'éditeur
            //PublisherBook publisher = findThePublisher(publishername);

            System.out.println("Titre : " + title);
            System.out.println("Auteur : " + authorfirstname + " " + authorlastname);
            for (int index = 0; index < authors.size(); index+=2) {
                System.out.println("Auteur "+ (index/2)+1 +": " + authors.get(index) + " " + authors.get(index+1));
            }
            System.out.println("publisher : " + publishername);  
            System.out.println("Date : " + pubyr[1]);  
            System.out.println("description :" + description);  

            
            // Extract record identifier for cover image
            NodeList recordIdentifiers = document.getElementsByTagName("srw:recordIdentifier");
            String arkId = "";
            if (recordIdentifiers.getLength() > 0) {
                arkId = recordIdentifiers.item(0).getTextContent();
                coverImage = "https://catalogue.bnf.fr/couverture?&appName=NE&idArk=" + arkId + "&couverture=1";
            }
            
            //process xml
            
            
            // Set the extracted values to the book object
            //book.setName(title);
            //book.setAuthor(listauth);
            //book.setDescription(description);
            //book.setPublicationYear(publicationYear);
            //book.setCategory(category);
            //book.setLangue(Langues.EN);
            //book.setCoverImage(coverImage);
            
            return book;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching book details from BnF API: " + e.getMessage(), e);
        }
    }

    private AuthorBook findTheAuthor(String firstname,String surname){
        List<AuthorBook> author = authorBookRepository.findByFirstnameAndSurnameIgnoreCase(firstname,surname);
        if(author.isEmpty()){
            return new AuthorBook(surname,firstname);
        }else{
            if(author.size()==1){
                return author.getFirst();
            } else {
                throw new RuntimeException("Error doubling author");
            }
        }
    }

    private PublisherBook findThePublisher(String name){
        List<PublisherBook> publisher = publisherBookRepository.findByNameIgnoreCase(name);
        if(publisher.isEmpty()){
            return new PublisherBook(name);
        }else{
            if(publisher.size()==1){
                return publisher.getFirst();
            } else {
                throw new RuntimeException("Error double publisher");
            }
        }
    }

    private static String getSubfield(Element datafield, String code) {
        NodeList subfields = datafield.getElementsByTagNameNS("info:lc/xmlns/marcxchange-v2", "subfield");
        for (int i = 0; i < subfields.getLength(); i++) {
            Element sub = (Element) subfields.item(i);
            if (code.equals(sub.getAttribute("code"))) {
                return sub.getTextContent();
            }
        }
        return "";
    }
}

