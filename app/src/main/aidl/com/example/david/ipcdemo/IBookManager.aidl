// IBookManager.aidl
package com.example.david.ipcdemo;

// Declare any non-default types here with import statements
import com.example.david.ipcdemo.Book;
import com.example.david.ipcdemo.IOnNewBookListener;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerOnNewBookListener(in IOnNewBookListener onNewBookListener);
    void unRegisterOnNewBookListener(in IOnNewBookListener onNewBookListener);
}
