package cartel.spring_boot_api.repository;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Serie;
import cartel.spring_boot_api.model.Book.FormatBook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
    List<Book> findByNameContainingIgnoreCase(String title);
    List<Book> findByAuthor(AuthorBook author);
    List<Book> findByFormat(FormatBook category);
    List<Book> findByIllustrator(Illustrator illustrator);
    List<Book> findByPublisher(PublisherBook publisher);
    List<Book> findBySerie(Serie serie);
}
