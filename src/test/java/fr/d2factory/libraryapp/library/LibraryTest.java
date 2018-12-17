package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.member.TypeMemberEnum;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Member;
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
import java.util.Map;

public class LibraryTest {

    private BookRepository bookRepository;

    private LibraryRepository libraryRepository;

    List<Book> books = new ArrayList<>();
    Member member = new Member();
    private int numberDaysToBorrow ;

    @Before
    public void setUp() throws Exception {
        // add some test books (use BookRepository#addBooks) ==> voir BookRepositoryTest.java
        bookRepository = new BookRepository();
        libraryRepository = new LibraryRepository();
        libraryRepository.setBookRepository(bookRepository);

        // Read books.json in src/test/resources
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = Book.class.getResourceAsStream("/books.json");
        try {
            books = mapper.readValue(is, mapper.getTypeFactory().constructCollectionType(List.class, Book.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // instantiate availableBooks
        bookRepository.addBooks(books);
        // instantiate borrowedBooks
        bookRepository.saveBookBorrow(books.get(0), LocalDate.now());
        // initialize Member
        member.setId(1);
        member.setName("Louay");

        numberDaysToBorrow = 0;
    }

    @Test
    public void member_can_borrow_a_book_if_book_is_available(){
        // GIVEN
        long isbnCode = 465789453149L;
        // WHEN
        Book book = bookRepository.findBook(isbnCode);
        // THEN
        Assert.assertNotNull(book);
    }

    @Test
    public void borrowed_book_is_no_longer_available(){
        // GIVEN
        Book bookBorrow = bookRepository.getBorrowedBooks().entrySet().stream().map(Map.Entry::getKey).findFirst().orElse(null);
        // WHEN
        LocalDate borrowDate = bookRepository.findBorrowedBookDate(bookBorrow);
        // THEN
        Assert.assertEquals(borrowDate, LocalDate.now());
    }

    @Test
    public void residents_are_taxed_10cents_for_each_day_they_keep_a_book(){
        // GIVEN
        //type of member = RESIDENT
        numberDaysToBorrow = 45;
        // WHEN
        Double tarif = libraryRepository.tarifBook(numberDaysToBorrow, TypeMemberEnum.RESIDENT.getValue());
        // THEN
        //tarif = 0.10 * 45 days = 4.5 euro
        Assert.assertEquals(tarif, Double.valueOf(4.5));
    }

    @Test
    public void students_pay_10_cents_the_first_30days(){
        // GIVEN
        //type of member = STUDENT_NOT_FIRST_YEAR
        numberDaysToBorrow = 30;
        // WHEN
        Double tarif = libraryRepository.tarifBook(numberDaysToBorrow, TypeMemberEnum.STUDENT_NOT_FIRST_YEAR.getValue());
        // THEN
        //tarif = 0.10 * 30 days = 3 euro
        Assert.assertEquals(tarif, Double.valueOf(3));
    }

    @Test
    public void students_in_1st_year_are_not_taxed_for_the_first_15days(){
        // GIVEN
        //type of member = STUDENT_FIRST_YEAR
        numberDaysToBorrow = 15;
        // WHEN
        Double tarif = libraryRepository.tarifBook(numberDaysToBorrow, TypeMemberEnum.STUDENT_FIRST_YEAR.getValue());
        // THEN
        //tarif = 0 euro
        Assert.assertEquals(tarif, Double.valueOf(0));
    }

    @Test
    public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days(){

        // GIVEN
        //type of member = STUDENT_NOT_FIRST_YEAR || STUDENT_FIRST_YEAR
        numberDaysToBorrow = 35;
        // WHEN
        Double tarifStudentNotFirstYear = libraryRepository.tarifBook(numberDaysToBorrow, TypeMemberEnum.STUDENT_NOT_FIRST_YEAR.getValue());
        Double tarifStudentFirstYear = libraryRepository.tarifBook(numberDaysToBorrow, TypeMemberEnum.STUDENT_FIRST_YEAR.getValue());
        // THEN
        //tarif = ( 0.10  * 30 days ) + (0.15 * 5 days ) = 3.75 euro
        Assert.assertEquals(tarifStudentNotFirstYear, Double.valueOf(3.75));
        //tarif = ( 0.10  * 15 days ) + (0.15 * 5 days ) = 2.25 euro
        Assert.assertEquals(tarifStudentFirstYear, Double.valueOf(2.25));
    }

    @Test
    public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days(){
        // GIVEN
        //type of member = RESIDENT
        numberDaysToBorrow = 65;
        // WHEN
        Double tarif = libraryRepository.tarifBook(numberDaysToBorrow, TypeMemberEnum.RESIDENT.getValue());
        // THEN
        //tarif = ( 0.10  * 60 days ) + (0.20 * 5 days ) = 7 euro
        Assert.assertEquals(tarif, Double.valueOf(7));
    }

    @Test
    public void member_can_borrow_book(){
        // GIVEN
        long isbnCode = 3326456467846L;
        member.setType(TypeMemberEnum.RESIDENT.getValue()); //  member
        // WHEN
        Book bookBorrowed = libraryRepository.borrowBook(isbnCode, member, LocalDate.now());
        // THEN
        Assert.assertNotNull(bookBorrowed);
        Assert.assertEquals(bookBorrowed.getIsbn().getIsbnCode(), isbnCode);
    }

    @Test
    public void members_cannot_borrow_book_if_they_have_late_books() throws Exception {
        // GIVEN
        long isbnCode = 46578964513L;
        long isbnCodeOfBookBorrowed = 3326456467846L;
        member.setType(TypeMemberEnum.RESIDENT.getValue()); //member

        Book bookBorrowed = libraryRepository.borrowBook(isbnCodeOfBookBorrowed, member, LocalDate.now().minusDays(75));
        // WHEN
        Book bookToBorrowed = libraryRepository.borrowBook(isbnCode, member, LocalDate.now());
        // THEN
        Assert.assertNull(bookToBorrowed);
    }


    @After
    public void tearDown() throws Exception {
        books = new ArrayList<>();
        numberDaysToBorrow = 0;
    }

}
