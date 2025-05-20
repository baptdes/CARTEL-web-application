package cartel.spring_boot_api.service;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Genre;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Book.BookFormat;
import cartel.spring_boot_api.repository.AuthorBookRepository;
import cartel.spring_boot_api.repository.BookRepository;
import cartel.spring_boot_api.repository.GenreRepository;
import cartel.spring_boot_api.repository.IllustratorRepository;
import cartel.spring_boot_api.repository.PublisherBookRepository;
import static cartel.spring_boot_api.specification.BookSpecification.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private AuthorBookRepository authorBookRepository;
    
    @Autowired
    private PublisherBookRepository publisherBookRepository;
    
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private IllustratorRepository illustratorRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> getBookByISBN(String id) {
        List<Book> books = bookRepository.findByBarcode(id);
        if (books.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(books.get(0));
        }
    }

    @Override
    public List<Book> filterBooks(int pageNumber, int pageSize, boolean asc, String sortBy,
                                 String titleBook, String publisherName,
                                 String authorFirstName, String authorSurname,
                                 String illustratorFirstName, String illustratorSurname,
                                 BookFormat category, String serieName, String genreName) {
        Pageable page = asc ? 
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) : 
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
                
        Specification<Book> filters = ((titleBook == null) ? titleLike("") : titleLike(titleBook))
                .and((serieName == null) ? null : fromSerieByName(serieName))
                .and((category == null) ? null : categoryEqual(category))
                .and((publisherName == null) ? null : fromPublisherByName(publisherName))
                .and((authorSurname == null) ? null : fromAuthorBySurname(authorSurname))
                .and((authorFirstName == null) ? null : fromAuthorByFirstName(authorFirstName))
                .and((illustratorSurname == null) ? null : fromIllustratorBySurname(illustratorSurname))
                .and((illustratorFirstName == null) ? null : fromIllustratorByFirstName(illustratorFirstName))
                .and((genreName == null) ? null : fromGenreByName(genreName));
                
        Page<Book> pageBook = bookRepository.findAll(filters, page);
        return pageBook.getContent();
    }

    @Override
    public List<AuthorBook> getAllAuthors() {
        return authorBookRepository.findAll();
    }

    @Override
    public AuthorBook addAuthor(String firstname, String surname) {
        List<AuthorBook> existingAuthors = authorBookRepository.findByFirstnameAndSurnameIgnoreCase(firstname, surname);
        if (!existingAuthors.isEmpty()) {
            throw new RuntimeException("Author already exists with name: " + firstname + " " + surname);
        }
        AuthorBook newAuthor = new AuthorBook(surname, firstname); 
        return authorBookRepository.save(newAuthor);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Genre addGenre(String name) {
        List<Genre> existingGenres = genreRepository.findByNameIgnoreCase(name);
        if (!existingGenres.isEmpty()) {
            throw new RuntimeException("Genre already exists with name: " + name);
        }
        Genre newGenre = new Genre(name);
        return genreRepository.save(newGenre);
    }

    @Override
    public List<PublisherBook> getAllPublishers() {
        return publisherBookRepository.findAll();
    }

    @Override
    public PublisherBook addPublisher(String name) {
        List<PublisherBook> existingPublishers = publisherBookRepository.findByNameIgnoreCase(name);
        if (!existingPublishers.isEmpty()) {
            throw new RuntimeException("Publisher already exists with name: " + name);
        }
        PublisherBook newPublisher = new PublisherBook(name);
        return publisherBookRepository.save(newPublisher);
    }

    @Override
    public List<Illustrator> getAllIllustrators() {
        return illustratorRepository.findAll();
    }

    @Override
    public Illustrator addIllustrator(String firstname, String surname) {
        List<Illustrator> existingIllustrators = illustratorRepository.findByFirstnameAndSurnameIgnoreCase(firstname, surname);
        if (!existingIllustrators.isEmpty()) {
            throw new RuntimeException("Illustrator already exists with name: " + firstname + " " + surname);
        }
        Illustrator newIllustrator = new Illustrator(surname, firstname); 
        return illustratorRepository.save(newIllustrator);
    }

    @Override
    public Book addBook(Book book) {
        // Check if the book already exists in the database
        List<Book> existingBooks = bookRepository.findByBarcode(book.getBarcode());
        if (!existingBooks.isEmpty()) {
            return existingBooks.get(0);
        }
        
        // Save the new book to the database
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(String isbn) {
        List<Book> books = bookRepository.findByBarcode(isbn);
        
        if (books.isEmpty()) {
            throw new RuntimeException("Book not found with ISBN: " + isbn);
        }
        
        Book bookToDelete = books.get(0);
        
        try {
            // Delete the book
            bookRepository.delete(bookToDelete);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting book: " + e.getMessage(), e);
        }
    }
    
    @Override
    public AuthorBook findOrCreateAuthor(String firstname, String surname) {
        List<AuthorBook> author = authorBookRepository.findByFirstnameAndSurnameIgnoreCase(firstname, surname);
        if (author.isEmpty()) {
            AuthorBook newAuthor = new AuthorBook(surname, firstname); 
            return authorBookRepository.save(newAuthor);
        } else {
            if (author.size() == 1) {
                return author.getFirst();
            } else {
                throw new RuntimeException("Error: Found multiple authors with same name");
            }
        }
    }

    //TODO : A remplacer avec le vrai code de Tom
    @Override
    public Book getBookWithISBN(String isbn) {
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

            // Check if we have any books find
            NodeList records = document.getElementsByTagName("srw:numberOfRecords");
            if (records.getLength() > 0) {
                String numberOfRecordsStr = records.item(0).getTextContent();
                if ("0".equals(numberOfRecordsStr)) {
                    throw new RuntimeException("No books found for ISBN: " + isbn);
                }
            }

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // Déclare les namespaces utilisés
            xpath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    switch (prefix) {
                        case "srw":
                            return "http://www.loc.gov/zing/srw/";
                        case "mxc":
                            return "info:lc/xmlns/marcxchange-v2";
                        default:
                            return XMLConstants.NULL_NS_URI;
                    }
                }

                public String getPrefix(String uri) {
                    return null;
                }

                public Iterator<String> getPrefixes(String uri) {
                    return null;
                }
            });

             // Create a new book object
            Book book = new Book();
            //book.setBarcode(isbn); 

            // XPath pour le titre (datafield tag="200", subfield code="a")
            String titlePath = "//mxc:datafield[@tag='200']/mxc:subfield[@code='a']";
            Node titleNode = (Node) xpath.evaluate(titlePath, document, XPathConstants.NODE);
            String title = titleNode != null ? titleNode.getTextContent() : "Titre non trouvé";

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
            String description = descriptionNode != null ? descriptionNode.getTextContent() : "";

            // XPath pour le nom de l'éditeur (datafield tag="210", subfield code="c")
            String languesPath = "//mxc:datafield[@tag='801']/mxc:subfield[@code='a']";
            Node languesNode = (Node) xpath.evaluate(languesPath, document, XPathConstants.NODE);
            String langues = languesNode.getTextContent();

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
            System.out.println("description :" + langues);  


            
            // Extract record identifier for cover image
            NodeList recordIdentifiers = document.getElementsByTagName("srw:recordIdentifier");
            String arkId = "";
            if (recordIdentifiers.getLength() > 0) {
                arkId = recordIdentifiers.item(0).getTextContent();
                String coverImage = "https://catalogue.bnf.fr/couverture?&appName=NE&idArk=" + arkId + "&couverture=1";
            }
            
            //process xml
            
            
            // Set the extracted values to the book object
            /*book.setName(title);
            book.setAuthor(listauth);
            book.setDescription(description);
            book.setPublicationYear(publicationYear);
            book.setCategory(category);
            book.setLangue(Langues.EN);
            book.setCoverImage(coverImage);*/
            
            return book;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching book details from API: " + e.getMessage(), e);
        }
    }

    private PublisherBook findOrCreatePublisher(String name) {
        List<PublisherBook> publisher = publisherBookRepository.findByNameIgnoreCase(name);
        if (publisher.isEmpty()) {
            PublisherBook newPublisher = new PublisherBook(name);
            return publisherBookRepository.save(newPublisher);
        } else {
            if (publisher.size() == 1) {
                return publisher.getFirst();
            } else {
                throw new RuntimeException("Error: Found multiple publishers with same name");
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
