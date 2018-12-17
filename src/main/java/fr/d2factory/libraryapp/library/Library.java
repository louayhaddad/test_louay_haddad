package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.Member;

import java.time.LocalDate;

/**
 * The library class is in charge of stocking the books and managing the return delays and members
 *
 * The books are available via the {@link fr.d2factory.libraryapp.book.BookRepository}
 */

public class Library {

    private ISBN isbn;

    private Member member;

    private LocalDate borrowedAt;

    public Library() {
    }

    public Library(ISBN isbn, Member member, LocalDate borrowedAt) {
        this.isbn = isbn;
        this.member = member;
        this.borrowedAt = borrowedAt;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    public void setIsbn(ISBN isbn) {
        this.isbn = isbn;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDate getBorrowedAt() {
        return borrowedAt;
    }

    public void setBorrowedAt(LocalDate borrowedAt) {
        this.borrowedAt = borrowedAt;
    }
}
