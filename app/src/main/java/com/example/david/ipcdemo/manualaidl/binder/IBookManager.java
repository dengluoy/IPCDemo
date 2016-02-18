package com.example.david.ipcdemo.manualaidl.binder;

import android.os.RemoteException;

import java.util.List;

/**
 * @author WeiDeng
 * @FileName com.example.david.ipcdemo.manualaidl.binder.IBookManager.java
 * @date 2016-01-29 00:39
 * @describe
 */
public interface IBookManager extends android.os.IInterface {

    public static final String DESCRIPTOR = "com.example.david.ipcdemo.manualaidl.binder.IBookManager";

    public List<Book> getBookList()throws RemoteException;

    public void addBook(Book book)throws RemoteException;
}