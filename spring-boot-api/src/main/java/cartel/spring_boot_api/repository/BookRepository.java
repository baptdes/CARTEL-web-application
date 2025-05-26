package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Series;
import cartel.spring_boot_api.model.Book.BookFormat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
    List<Book> findByNameContainingIgnoreCase(String title);
    List<Book> findByAuthors(AuthorBook author);
    List<Book> findByFormat(BookFormat category);
    List<Book> findByIllustrator(Illustrator illustrator);
    List<Book> findByPublisher(PublisherBook publisher);
    List<Book> findBySeries(Series series);
    List<Book> findByBarcode(String isbn);
}
