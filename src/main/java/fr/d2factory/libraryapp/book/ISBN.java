package fr.d2factory.libraryapp.book;

public class ISBN {
    long isbnCode;

    public ISBN() {
    }

    public ISBN(long isbnCode) {
        this.isbnCode = isbnCode;
    }

    public long getIsbnCode() {
        return isbnCode;
    }

    public void setIsbnCode(long isbnCode) {
        this.isbnCode = isbnCode;
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
        ISBN other = (ISBN) obj;
        if (this.isbnCode == other.getIsbnCode()) {
            result = true;
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int code = 31;
        int result = 1;
        result = code * result + String.valueOf(this.isbnCode).hashCode();
        return result;
    }
}
