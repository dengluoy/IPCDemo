package com.example.david.ipcdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.david.ipcdemo.Book;
import com.example.david.ipcdemo.IBookManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author WeiDeng
 * @date 16/2/1
 * @description
 */
public class AIDLManagerService extends Service {

    public final String TAG = getClass().getSimpleName();

    private CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBooks;
        }

        @Override
        public void addBook(@Nullable Book book) throws RemoteException {
            mBooks.add(book);
            Log.d(TAG, Thread.currentThread().getName());
//            Toast.makeText(AIDLManagerService.this, book.getBookName(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "addBook");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
