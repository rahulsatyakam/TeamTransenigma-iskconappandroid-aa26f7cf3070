package com.transenigma.iskconapp;

public class BooksInfo {

    String bookName, bookAuthor;

    public String getBookName() {
        return bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }
    
    public BooksInfo(String bookName, String bookAuthor) {
        this.bookAuthor = bookAuthor;
        this.bookName = bookName;

    }
}
