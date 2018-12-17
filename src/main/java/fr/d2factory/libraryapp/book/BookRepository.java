package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {
    private Map<ISBN, Book> availableBooks = new HashMap<>();
    private Map<Book, LocalDate> borrowedBooks = new HashMap<>();

    public void addBooks(List<Book> books){
        Map<ISBN, Book> newBooks = books
                .stream()
                .collect(Collectors.toMap(book -> book.getIsbn(), book -> book));
        availableBooks.putAll(newBooks);
    }

    public void addBook(Book book){
        availableBooks.put(book.getIsbn(), book);
    }

    public Book findBook(long isbnCode) {
        return availableBooks.get(new ISBN(isbnCode));
    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt){
        availableBooks.remove(book.getIsbn());
        borrowedBooks.put(book, borrowedAt);
    }

    public void deleteBookBorrow(Book book){
        borrowedBooks.remove(book);
    }

    public LocalDate findBorrowedBookDate(Book book) {
        LocalDate result = null;
        if (book != null && book.getIsbn() != null) {
            result = borrowedBooks.get(book);
        }
        return result;
    }

    public Map<ISBN, Book> getAvailableBooks() {
        return availableBooks;
    }

    public Map<Book, LocalDate> getBorrowedBooks() {
        return borrowedBooks;
    }
}
