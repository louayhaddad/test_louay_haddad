package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.member.TypeMemberEnum;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.Member;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The book repository emulates a database via 2 HashMaps
 */

public class LibraryRepository {

    private final static Logger LOGGER = Logger.getLogger(LibraryRepository.class);

    private List<Library> libraryList = new ArrayList<>();
    private BookRepository bookRepository = new BookRepository();

    private final static  double taxePerDayWithoutLate = 0.10;

    private final static  int nbDaysFreeStudent = 15;
    private final static  int nbDaysWithoutLateStudent = 30;
    private final static  double taxePerDayWithLateStudent = 0.15;

    private final static  int nbDaysWithoutLateResident = 60;
    private final static  double taxePerDayWithLateResident = 0.20;


    /**
     * A member is borrowing a book from our library.
     *
     * @param isbnCode the isbn code of the book
     * @param member the member who is borrowing the book
     * @param borrowedAt the date when the book was borrowed
     *
     * @return the book the member wishes to obtain if found
     * @throws HasLateBooksException in case the member has books that are late
     *
     * @see fr.d2factory.libraryapp.book.ISBN
     * @see Member
     */
    Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException {
        Book bookToBorrow = null;
        if (member != null && borrowedAt != null) {
            boolean eligible = eligibilityMember(member, borrowedAt);
            if (!eligible) {
                LOGGER.info("Member is late with their books they cannot borrow any new books before returning the previous ones.");
                return bookToBorrow;
            }

            bookToBorrow = bookRepository.findBook(isbnCode);
            if (null == bookToBorrow ) {
                LOGGER.info("Book does not exist. ");
            } else {
                bookRepository.saveBookBorrow(bookToBorrow, borrowedAt);
                addToLibraryList(bookToBorrow.getIsbn(), member, borrowedAt);
            }
        }
            return bookToBorrow;
    }

    public boolean eligibilityMember(Member member, LocalDate borrowedAt) {
        List<Library> booksBorrowByMember = new ArrayList<>();
        if (member != null && borrowedAt != null) {
            booksBorrowByMember = libraryList.stream()
                    .filter(isEligible(member, borrowedAt))
                    .collect(Collectors.toList());
        }
        return booksBorrowByMember.isEmpty();
    }

    public static Predicate<Library> isEligible(Member member, LocalDate borrowedAt) {
        if (TypeMemberEnum.STUDENT_FIRST_YEAR.getValue().equals(member.getType())
                || TypeMemberEnum.STUDENT_NOT_FIRST_YEAR.getValue().equals(member.getType())) {
            return library -> library.getMember() != null
                            && library.getMember().getId() == member.getId()
                            && borrowedAt.isAfter(library.getBorrowedAt().plusDays(nbDaysWithoutLateStudent));
        } else if (TypeMemberEnum.RESIDENT.getValue().equals(member.getType())) {
            return library -> library.getMember() != null
                            && library.getMember().getId() == member.getId()
                            && borrowedAt.isAfter(library.getBorrowedAt().plusDays(nbDaysWithoutLateResident));
        }
        return null;
    }

    /**
     * A member returns a book to the library.
     * We should calculate the tarif and probably charge the member
     *
     * @param book the {@link Book} they return
     * @param member the {@link Member} who is returning the book
     *
     */
    void returnBook(Book book, Member member) {
        double toPay = 0d;
        if (book != null && book.getIsbn() != null && member != null) {
            Optional<Library> optionalLibrary = libraryList.stream().filter(library -> library.getMember().getId() == member.getId()
                    && library.getIsbn().getIsbnCode() == book.getIsbn().getIsbnCode()).findFirst();
            if (optionalLibrary.isPresent()) {
                LocalDate borrowedAt = optionalLibrary.get().getBorrowedAt();
                LocalDate todayDdate = LocalDate.now();
                int numberBorrowDays = todayDdate.compareTo(borrowedAt);
                //TODO : add method to subtract the amount to payed
                toPay = tarifBook(numberBorrowDays, member.getType());

                bookRepository.deleteBookBorrow(book);
                removeFromLibraryList(book.getIsbn().getIsbnCode());
                bookRepository.addBook(book);
            }
        }
    }

    /**
     * A member returns a book to the library.
     * We should calculate the tarif and probably charge the member
     *
     * @param numberBorrowDays the number of days who the member borrow book
     * @param memberType the Type of the member who is returning the book
     *
     */
    public double tarifBook(int numberBorrowDays, String memberType) {
        double toPay = 0d;
        if (TypeMemberEnum.STUDENT_FIRST_YEAR.getValue().equals(memberType)) {
            if (numberBorrowDays > nbDaysFreeStudent && numberBorrowDays <= nbDaysWithoutLateStudent) {
                toPay = toPay + (taxePerDayWithoutLate * (numberBorrowDays - nbDaysFreeStudent));
            } else if (numberBorrowDays > nbDaysWithoutLateStudent) {
                toPay = toPay + (taxePerDayWithoutLate * 15 ) + (taxePerDayWithLateStudent * (numberBorrowDays - nbDaysWithoutLateStudent));
            }
        } else if(TypeMemberEnum.STUDENT_NOT_FIRST_YEAR.getValue().equals(memberType)) {
            if (numberBorrowDays <= nbDaysWithoutLateStudent) {
                toPay = toPay + (taxePerDayWithoutLate * numberBorrowDays);
            } else if (numberBorrowDays > nbDaysWithoutLateStudent) {
                toPay = toPay + (taxePerDayWithoutLate * nbDaysWithoutLateStudent) + (taxePerDayWithLateStudent * (numberBorrowDays - nbDaysWithoutLateStudent));
            }
        } else  if (TypeMemberEnum.RESIDENT.getValue().equals(memberType)){
            if (numberBorrowDays <= nbDaysWithoutLateResident) {
                toPay = toPay + (taxePerDayWithoutLate * numberBorrowDays);
            } else if (numberBorrowDays > nbDaysWithoutLateResident) {
                toPay = toPay + (taxePerDayWithoutLate * nbDaysWithoutLateResident) + (taxePerDayWithLateResident * (numberBorrowDays - nbDaysWithoutLateResident));
            }
        }
        return toPay;
    }

    /**
     * Add book to the list of borrowed books by Member
     *
     * @param isbn the {@link ISBN} the code of book to borrow
     * @param member the {@link Member} who is borrowing the book
     */
    public void addToLibraryList(ISBN isbn, Member member, LocalDate borrowedAt){
        libraryList.add(new Library(isbn, member, borrowedAt));
    }

    /**
     * remove book from the list of borrowed books by Member
     *
     * @param isbnCode the {@link ISBN} the code of book to set availble
     */
    public void removeFromLibraryList(long isbnCode){
        Optional<Library> optionalLibraryToRemove = libraryList.stream().filter(library -> library.getIsbn() != null && library.getIsbn().getIsbnCode() == isbnCode).findFirst();
        if (optionalLibraryToRemove.isPresent()) {
            libraryList.remove(optionalLibraryToRemove.get());
        }
    }

    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
