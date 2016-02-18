// IOnNewBookListener.aidl
package com.example.david.ipcdemo;

// Declare any non-default types here with import statements
import com.example.david.ipcdemo.Book;

interface IOnNewBookListener {

       void onNewBook(in Book book);
}
