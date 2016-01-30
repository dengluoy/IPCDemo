// IBookManager.aidl
package com.example.david.ipcdemo;

// Declare any non-default types here with import statements
import com.example.david.ipcdemo.Book;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}
