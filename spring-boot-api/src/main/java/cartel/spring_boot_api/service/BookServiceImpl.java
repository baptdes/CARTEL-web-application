package cartel.spring_boot_api.service;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Genre;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Series;
import cartel.spring_boot_api.model.Book.BookFormat;
import cartel.spring_boot_api.model.Item.Languages;
import cartel.spring_boot_api.repository.AuthorBookRepository;
import cartel.spring_boot_api.repository.BookRepository;
import cartel.spring_boot_api.repository.GenreRepository;
import cartel.spring_boot_api.repository.IllustratorRepository;
import cartel.spring_boot_api.repository.PublisherBookRepository;
import cartel.spring_boot_api.repository.SerieRepository;

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

    @Autowired
    private SerieRepository serieRepository;

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
                          String authorFirstName, String authorSurname,String authorFullName,
                          String illustratorFirstName, String illustratorSurname, String illustratorFullName,
                          String titleBook, String publisherName, BookFormat category, String serieName, String genreName) {

        Pageable page = asc ? 
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)) : 
                PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());

        Specification<Book> filters = ((titleBook == null) ? titleLike("") : titleLike(titleBook))
                .and((serieName == null) ? null : fromSerieByName(serieName))
                .and((category == null) ? null : categoryEqual(category))
                .and((publisherName == null) ? null : fromPublisherByName(publisherName))
                .and((authorSurname == null) ? null : fromAuthorBySurname(authorSurname))
                .and((authorFirstName == null) ? null : fromAuthorByFirstName(authorFirstName))
                .and((authorFullName == null) ? null : fromAuthorByCompleteName(authorFullName))
                .and((illustratorSurname == null) ? null : fromIllustratorBySurname(illustratorSurname))
                .and((illustratorFirstName == null) ? null : fromIllustratorByFirstName(illustratorFirstName))
                .and((illustratorFullName == null) ? null : fromIllustratorByCompleteName(illustratorFullName))
                .and((genreName == null) ? null : fromGenreByName(genreName));
        Page<Book> pageBook = bookRepository.findAll(filters, page);
        return pageBook.getContent();
    }

    @Override
    public List<AuthorBook> getAllAuthors() {
        return authorBookRepository.findAll();
    }

    @Override
    public List<AuthorBook> getAuthorsByName(String nameLike){
        return authorBookRepository.findAll(authorsByCompleteName(nameLike));
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
    public List<PublisherBook> getPublishersByName(String nameLike){
        return publisherBookRepository.findByNameContainingIgnoreCase(nameLike);
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
    public List<Illustrator> getIllustratorsByName(String nameLike){
        return illustratorRepository.findAll(illustratorsByCompleteName(nameLike));
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

    @Override
    public Optional<Book> getBookFromBNF(String isbn) {
        // Quand ISBN-13, le 4ème caractère est le code du pays
        // Quand ISBN-10, le 1er caractère est le code du pays
        // int language = isbn.length() == 13 ? isbn.charAt(3) : isbn.charAt(0) ;

        // On crée un objet Book vide pour y stocker les informations récupérées
        Book book = new Book();
        book.setBarcode(isbn);

        // Utiliser l'API BnF pour récupérer les informations du livre
        // Uniquement pour les livres français (ISBN-13 commençant par 2)

        book.setLanguage(Languages.FR);

        // Use Dublin Core schema for easier parsing
        String bnfApiUrl = "https://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=bib.isbn=\"" + isbn + "\"";
    
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
                    return Optional.empty(); // No records found
                }
            }

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

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

            // Numéro de volume
            String volumePath = "//mxc:datafield[@tag='461']/mxc:subfield[@code='v']";
            Node volumeNode = (Node) xpath.evaluate(volumePath, document, XPathConstants.NODE);
            if (volumeNode != null) {
                String volumeS = volumeNode.getTextContent();
                book.setVolumeNumber(Integer.valueOf(volumeS));
            }

            // Série
            String seriePath = "//mxc:datafield[@tag='461']/mxc:subfield[@code='t']";
            Node serieNode = (Node) xpath.evaluate(seriePath, document, XPathConstants.NODE);

            // Titre
            String titlePath = "//mxc:datafield[@tag='200']/mxc:subfield[@code='a']";
            Node titleNode = (Node) xpath.evaluate(titlePath, document, XPathConstants.NODE);
            String titlePartPath = "//mxc:datafield[@tag='200']/mxc:subfield[@code='i']";
            Node titlePartNode = (Node) xpath.evaluate(titlePartPath, document, XPathConstants.NODE);
            StringBuilder titleBuilder = new StringBuilder();
            if (serieNode != null) {
                titleBuilder.append(serieNode.getTextContent());
            }
            if (titleBuilder.length() > 0 && book.getVolumeNumber() != null) {
                titleBuilder.append(" - Tome ").append(book.getVolumeNumber()).append(" - ");
            }
            if (titleNode != null) {
                titleBuilder.append(titleNode.getTextContent());
            }
            if (titlePartNode != null) {
                if (titleBuilder.length() > 0) {
                    titleBuilder.append(" - ");
                }
                titleBuilder.append(titlePartNode.getTextContent());
            }
            if (titleBuilder.length() > 0) {
                book.setName(titleBuilder.toString());
            }

            // Auteur Principal
            String authorlastnamePath = "//mxc:datafield[@tag='700']/mxc:subfield[@code='a']";
            Node authorlastnameNode = (Node) xpath.evaluate(authorlastnamePath, document, XPathConstants.NODE);
            String authorfirstnamePath = "//mxc:datafield[@tag='700']/mxc:subfield[@code='b']";
            Node authorfirstnameNode = (Node) xpath.evaluate(authorfirstnamePath, document, XPathConstants.NODE);
            if (authorlastnameNode != null && authorfirstnameNode != null) {
                AuthorBook author = findOrCreateAuthor(authorfirstnameNode.getTextContent(), authorlastnameNode.getTextContent());
                book.addAuthor(author);
            }

            // Editeur
            String publishernamePath = "//mxc:datafield[@tag='210']/mxc:subfield[@code='c']";
            Node publishernameNode = (Node) xpath.evaluate(publishernamePath, document, XPathConstants.NODE);
            if (publishernameNode == null) {
                publishernamePath = "//mxc:datafield[@tag='214']/mxc:subfield[@code='c']";
                publishernameNode = (Node) xpath.evaluate(publishernamePath, document, XPathConstants.NODE);
            }
            if (publishernameNode != null) {
                book.setPublisher(findOrCreatePublisher(publishernameNode.getTextContent()));
            }

            // Année de publication
            String PublicationYearPath = "//mxc:datafield[@tag='210']/mxc:subfield[@code='d']";
            Node PublicationYearNode = (Node) xpath.evaluate(PublicationYearPath, document, XPathConstants.NODE);
            if (PublicationYearNode == null) {
                PublicationYearPath = "//mxc:datafield[@tag='214']/mxc:subfield[@code='d']";
                PublicationYearNode = (Node) xpath.evaluate(PublicationYearPath, document, XPathConstants.NODE);
            }
            if (PublicationYearNode != null) {
                // Exemple de format récupéré : "DL 2020"
                String[] yearParts = PublicationYearNode.getTextContent().split(" ");
                if (yearParts.length > 1) {
                    book.setPublicationYear(Integer.valueOf(yearParts[1]));
                } else {
                    book.setPublicationYear(Integer.valueOf(PublicationYearNode.getTextContent()));
                }
            }
            

            // Description
            String descriptionPath = "//mxc:datafield[@tag='330']/mxc:subfield[@code='a']";
            Node descriptionNode = (Node) xpath.evaluate(descriptionPath, document, XPathConstants.NODE);
            if (descriptionNode == null || descriptionNode.getTextContent().isEmpty()) {
                descriptionPath = "//mxc:datafield[@tag='329']/mxc:subfield[@code='a']";
                descriptionNode = (Node) xpath.evaluate(descriptionPath, document, XPathConstants.NODE);
            }
            if (descriptionNode != null) {
                book.setDescription(descriptionNode.getTextContent());
            }

            // Couverture
            NodeList recordIdentifiers = document.getElementsByTagName("srw:recordIdentifier");
            if (recordIdentifiers.getLength() > 0) {
                String arkId = recordIdentifiers.item(0).getTextContent();
                book.setCoverImage("https://catalogue.bnf.fr/couverture?&appName=NE&idArk=" + arkId + "&couverture=1");
            }

            // Langue
            String languagePath = "//mxc:datafield[@tag='101']/mxc:subfield[@code='a']";
            Node languageNode = (Node) xpath.evaluate(languagePath, document, XPathConstants.NODE);
            if (languageNode != null) {
                String languageCode = languageNode.getTextContent();
                switch (languageCode) {
                    case "fre":
                        book.setLanguage(Languages.FR);
                        break;
                    case "eng":
                        book.setLanguage(Languages.EN);
                        break;
                    case "jpn":
                        book.setLanguage(Languages.JA);
                        break;
                    default:
                        break;
                }
            }

            /*
|               ||||||||||||||||||||| Pour plus tard |||||||||||||||||||||
            // Série
            String seriePath = "//mxc:datafield[@tag='461']/mxc:subfield[@code='t']";
            Node serieNode = (Node) xpath.evaluate(seriePath, document, XPathConstants.NODE);
            String serieName = serieNode != null ? serieNode.getTextContent() : "";
            Series serie = findOrCreateSerie(serieName);*/
        } catch (Exception e) {
            throw new RuntimeException("Error fetching book details from BnF API: " + e.getMessage(), e);
        }
        
        return Optional.of(book);
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

    private Series findOrCreateSerie(String name) {
        List<Series> series = serieRepository.findByNameIgnoreCase(name);
        if (series.isEmpty()) {
            Series serie = new Series(name);
            return serieRepository.save(serie);
        } else {
            if (series.size() == 1) {
                return series.getFirst();
            } else {
                throw new RuntimeException("Error: Found multiple publishers with same name");
            }
        }
    }
}
