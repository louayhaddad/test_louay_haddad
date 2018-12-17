package fr.d2factory.libraryapp.book;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookRepositoryTest {

    private BookRepository bookRepository;
    List<Book> books = new ArrayList<>();
    Book bookToBorrow = null;


    @Before
    public void setUp() throws Exception {
        bookRepository = new BookRepository();
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = Book.class.getResourceAsStream("/books.json");
        try {
            books = mapper.readValue(is, mapper.getTypeFactory().constructCollectionType(List.class, Book.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        bookToBorrow = books.get(0);
    }

    @Test
    public void can_add_books(){
        // GIVEN
        //books
        // WHEN
        bookRepository.addBooks(books);
        // THEN
        Assert.assertNotEquals(bookRepository.getAvailableBooks().size(), 0);
    }

    @Test
    public void can_find_book(){
        // GIVEN
        bookRepository.addBooks(books);
        long isbnCode = 46578964513L;
        // WHEN
        Book book = bookRepository.findBook(isbnCode);
        // THEN
        Assert.assertNotNull(book);
    }

    @Test
    public void can_save_book_Borrow(){
        // GIVEN
        //bookToBorrow
        // WHEN
        bookRepository.saveBookBorrow(bookToBorrow, LocalDate.now());
        // THEN
        Assert.assertEquals(bookRepository.getBorrowedBooks().size(), 1);
    }

    @Test
    public void can_find_borrowed_book_date(){
        // GIVEN
        //bookToBorrow
        bookRepository.saveBookBorrow(bookToBorrow, LocalDate.now());
        // WHEN
        LocalDate dateBborrowedBook = bookRepository.findBorrowedBookDate(bookToBorrow);
        // THEN
        Assert.assertEquals(dateBborrowedBook, LocalDate.now());
    }

    @After
    public void tearDown() throws Exception {
        books = new ArrayList<>();
    }
}
