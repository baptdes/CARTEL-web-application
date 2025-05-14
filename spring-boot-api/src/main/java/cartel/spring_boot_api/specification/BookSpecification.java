package cartel.spring_boot_api.specification;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Series;
import cartel.spring_boot_api.model.Book.BookFormat;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;


public class BookSpecification {

	// Filter books with name like a specific string.
    public static Specification<Book> titleLike(String title) {
        return (book, query, builder) -> builder.like(book.get("name"), "%" + title + "%");
    }

    // Filter books by their category.
    public static Specification<Book> categoryEqual(BookFormat category) {
        return (book, query, builder) -> builder.equal(book.get("format"), category);
    }


    // public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder){
    //         final Path<Group> group = root.<Group> get("group");
    //         return group.in(groupIds);
    //     }
    // Filter books by one of their genre.
    // public static Specification<Book> genreIn(GenreBook genre) {
    //     return (book, query, builder) -> {QEF OMVUVGHIFJ
    //     ø”AGOUGAGAK
    //         ListJoin<Book, GenreBook> genreJoin = book.join("genre");
    //         return builder.equal(genreJoin.get("");
    //     };
    // }


	// Filter by serie name.
    public static Specification<Book> fromSerieByName(String serieName) {
        return (root, query, builder) -> {
            Join<Book,Series> serieBook = root.join("serie");
            return builder.like(serieBook.get("serieName"), "%" + serieName + "%");
        };
    }

    // Filter by publisher name.
    public static Specification<Book> fromPublisherByName(String publisherName) {
        return (root, query, builder) -> {
            Join<Book,PublisherBook> publisherBook = root.join("publisher");
            return builder.like(publisherBook.get("name"), "%" + publisherName + "%");
        };
    }

    // Filter by author first name.
    public static Specification<Book> fromAuthorByFirstName(String authorFirstName) {
        return (root, query, builder) -> {
            Join<Book,AuthorBook> authorBook = root.join("author");
            return builder.like(authorBook.get("firstname"), "%" + authorFirstName + "%");
        };
    }

    // Filter by author last name.
    public static Specification<Book> fromAuthorBySurname(String authorSurname) {
        return (root, query, builder) -> {
            Join<Book,AuthorBook> authorBook = root.join("author");
            return builder.like(authorBook.get("surname"), "%" + authorSurname + "%");
        };
    }

    // Filter by illustrator first name.
    public static Specification<Book> fromIllustratorByFirstName(String illustratorFirstName) {
        return (root, query, builder) -> {
            Join<Book,Illustrator> illustratorBook = root.join("illustrator");
            return builder.like(illustratorBook.get("firstname"), "%" + illustratorFirstName + "%");
        };
    }

    // Filter by illustrator last name.
    public static Specification<Book> fromIllustratorBySurname(String illustratorSurname) {
        return (root, query, builder) -> {
            Join<Book,Illustrator> illustratorBook = root.join("illustrator");
            return builder.like(illustratorBook.get("surname"), "%" + illustratorSurname + "%");
        };
    }
}
