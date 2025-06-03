package cartel.spring_boot_api.specification;

import cartel.spring_boot_api.model.AuthorBook;
import cartel.spring_boot_api.model.Book;
import cartel.spring_boot_api.model.Genre;
import cartel.spring_boot_api.model.Illustrator;
import cartel.spring_boot_api.model.PublisherBook;
import cartel.spring_boot_api.model.Series;
import cartel.spring_boot_api.model.Book.BookFormat;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Expression;


public class BookSpecification {

	// Filter books with name like a specific string.
    public static Specification<Book> titleLike(String title) {
        return (root, query, cb) -> 
            cb.like(cb.lower(root.get("name")), "%" + title.toLowerCase() + "%");
    }

    // Filter books by their category.
    public static Specification<Book> categoryEqual(BookFormat category) {
        return (book, query, builder) -> builder.equal(book.get("format"), category);
    }

	// Filter by serie name.
    public static Specification<Book> fromSerieByName(String serieName) {
        return (root, query, builder) -> {
            Join<Book,Series> serieBook = root.join("serie");
            return builder.like(builder.lower(serieBook.get("serieName")), "%" + serieName.toLowerCase() + "%");
        };
    }

    // Filter by publisher name.
    public static Specification<Book> fromPublisherByName(String publisherName) {
        return (root, query, builder) -> {
            Join<Book,PublisherBook> publisherBook = root.join("publisher");
            return builder.like(builder.lower(publisherBook.get("name")), "%" + publisherName.toLowerCase() + "%");
        };
    }

    // Filter by author first name.
    public static Specification<Book> fromAuthorByFirstName(String authorFirstName) {
        return (root, query, builder) -> {
            Join<Book,AuthorBook> authorBook = root.join("authors");
            return builder.like(builder.lower(authorBook.get("firstname")), "%" + authorFirstName.toLowerCase() + "%");
        };
    }

    // Filter by author last name.
    public static Specification<Book> fromAuthorBySurname(String authorSurname) {
        return (root, query, builder) -> {
            Join<Book,AuthorBook> authorBook = root.join("authors");
            return builder.like(builder.lower(authorBook.get("surname")), "%" + authorSurname.toLowerCase() + "%");
        };
    }

    // Filter by author first name and last name.
    public static Specification<Book> fromAuthorByCompleteName(String authorName) {
        return (root, query, builder) -> {
            Join<Book,AuthorBook> authorBook = root.join("authors");
            Expression<String> expr1 = builder.lower(builder.concat(builder.concat(authorBook.get("surname"),
                    " "),authorBook.get("firstname")));
            Expression<String> expr2 = builder.lower(builder.concat(builder.concat(authorBook.get("firstname"),
                    " "),authorBook.get("surname")));
            return builder.or(builder.like(expr1, "%" + authorName.toLowerCase() + "%"),
                              builder.like(expr2, "%" + authorName.toLowerCase() + "%"));
        };
    }

    // Filter by illustrator first name.
    public static Specification<Book> fromIllustratorByFirstName(String illustratorFirstName) {
        return (root, query, builder) -> {
            Join<Book,Illustrator> illustratorBook = root.join("illustrator");
            return builder.like(builder.lower(illustratorBook.get("firstname")), "%" + illustratorFirstName.toLowerCase() + "%");
        };
    }

    // Filter by illustrator last name.
    public static Specification<Book> fromIllustratorBySurname(String illustratorSurname) {
        return (root, query, builder) -> {
            Join<Book,Illustrator> illustratorBook = root.join("illustrator");
            return builder.like(builder.lower(illustratorBook.get("surname")), "%" + illustratorSurname.toLowerCase() + "%");
        };
    }

    // Filter by illustrator first name and last name.
    public static Specification<Book> fromIllustratorByCompleteName(String illustratorName) {
        return (root, query, builder) -> {
            Join<Book, Illustrator> illustratorBook = root.join("illustrator");
            Expression<String> expr1 = builder.lower(builder.concat(builder.concat(illustratorBook.get("surname"),
                    " "),illustratorBook.get("firstname")));
            Expression<String> expr2 = builder.lower(builder.concat(builder.concat(illustratorBook.get("firstname"),
                    " "),illustratorBook.get("surname")));
            return builder.or(builder.like(expr1, "%" + illustratorName.toLowerCase() + "%"),
                              builder.like(expr2, "%" + illustratorName.toLowerCase() + "%"));
        };
    }

    // Filter books by genre name
    public static Specification<Book> fromGenreByName(String genreName) {
        return (root, query, builder) -> {
            Join<Book, Genre> genreJoin = root.join("genres");
            return builder.like(builder.lower(genreJoin.get("name")), "%" + genreName.toLowerCase() + "%");
        };
    }

    // Filter illustrators by first name and last name.
    public static Specification<Illustrator> illustratorsByCompleteName(String illustratorName) {
        return (illustrator, query, builder) -> {
            Expression<String> expr1 = builder.lower(builder.concat(builder.concat(illustrator.get("surname"),
                    " "),illustrator.get("firstname")));
            Expression<String> expr2 = builder.lower(builder.concat(builder.concat(illustrator.get("firstname"),
                    " "),illustrator.get("surname")));
            return builder.or(builder.like(expr1, "%" + illustratorName.toLowerCase() + "%"),
                              builder.like(expr2, "%" + illustratorName.toLowerCase() + "%"));
        };
    }

    // Filter authors by first name and last name.
    public static Specification<AuthorBook> authorsByCompleteName(String authorName) {
        return (author, query, builder) -> {
            Expression<String> expr1 = builder.lower(builder.concat(builder.concat(author.get("surname"), " "),author.get("firstname")));
            Expression<String> expr2 = builder.lower(builder.concat(builder.concat(author.get("firstname"), " "),author.get("surname")));
            return builder.or(builder.like(expr1, "%" + authorName.toLowerCase() + "%"),
                              builder.like(expr2, "%" + authorName.toLowerCase() + "%"));
        };
    }

}
