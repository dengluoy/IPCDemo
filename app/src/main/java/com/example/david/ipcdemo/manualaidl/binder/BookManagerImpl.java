package com.example.david.ipcdemo.manualaidl.binder;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiDeng
 * @FileName com.example.david.ipcdemo.manualaidl.binder.IBookManagerImpl.java
 * @date 2016-01-29 00:46
 * @describe
 */
public abstract class BookManagerImpl extends Binder implements IBookManager {

    static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);

    public BookManagerImpl(){this.attachInterface(this,DESCRIPTOR);}


    public static IBookManager asInterface(IBinder iBinder) {

        IInterface iInterface = iBinder.queryLocalInterface(DESCRIPTOR);
        if(iInterface != null && iInterface instanceof IBookManager) {
            return (IBookManager) iInterface;
        }
        return new Proxy(iBinder);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {

        switch(code) {
            case TRANSACTION_getBookList:
                //申请需要执行
                data.enforceInterface(DESCRIPTOR);
                List<Book> bookList = this.getBookList();
                reply.writeNoException();
                reply.writeTypedList(bookList);
                return true;
            case TRANSACTION_addBook:
                //申请需要执行
                data.enforceInterface(DESCRIPTOR);
                Book book = Book.CREATOR.createFromParcel(data);
                this.addBook(book);
                reply.writeNoException();
                return true;
        }

        return super.onTransact(code, data, reply, flags);
    }

    @Override
    public abstract List<Book> getBookList() throws RemoteException;

    @Override
    public abstract void addBook(Book book) throws RemoteException ;

    @Override
    public IBinder asBinder() {
        return this;
    }

    public static class Proxy implements IBookManager {

        private IBinder mRemote;
        public Proxy(IBinder remote) {
            this.mRemote = remote;
        }

        @Override
        public List<Book> getBookList() throws RemoteException {

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            List<Book> result = null;
            try{
                data.writeInterfaceToken(DESCRIPTOR);
                mRemote.transact(TRANSACTION_getBookList, data, reply, 0);
                reply.readException();
                result = reply.createTypedArrayList(Book.CREATOR);
            }finally {
                data.recycle();
                reply.recycle();
            }

            return result;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();

            try{
                data.writeInterfaceToken(DESCRIPTOR);
                data.writeInt(book.getBookId());
                data.writeString(book.getBookName());
                mRemote.transact(TRANSACTION_addBook, data, reply, 0);
                reply.readException();
            }finally {
                data.recycle();
                reply.recycle();
            }
        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }

        public java.lang.String getInterfaceDescriptor() {
            return DESCRIPTOR;
        }
    }
}
