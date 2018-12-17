package fr.d2factory.libraryapp.book;

/**
 * A simple representation of a book
 */
public class Book {
    private String title;
    private String author;
    private ISBN isbn;

    public Book(String title, String author, ISBN isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public Book() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    public void setIsbn(ISBN isbn) {
        this.isbn = isbn;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj == null) {
            return result ;
        }
        if (getClass() != obj.getClass()) {
            return result;
        }
        Book other = (Book) obj;
        if (this.getIsbn() != null && other.getIsbn() != null) {
            if (this.getIsbn().getIsbnCode() == other.getIsbn().getIsbnCode()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int code = 31;
        int result = 1;
        result = code * result + (this.getIsbn() == null ? 0 : this.getIsbn().hashCode());
        result = code * result + ((title == null) ? 0 : title.hashCode());
        result = code * result + ((author == null) ? 0 : author.hashCode());
        return result;
    }
}
