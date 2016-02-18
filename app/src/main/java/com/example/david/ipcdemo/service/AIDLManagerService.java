package com.example.david.ipcdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.david.ipcdemo.Book;
import com.example.david.ipcdemo.IBookManager;
import com.example.david.ipcdemo.IOnNewBookListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author WeiDeng
 * @date 16/2/1
 * @description
 */
public class AIDLManagerService extends Service {

    private final String TAG = getClass().getSimpleName();

    public static final int MESSAGE_NEW_BOOK_ARRIVED = 1 << 3;

    private CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookListener> mOnNewBookListeners = new RemoteCallbackList<>();

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBooks;
        }

        public void addBook(Book book) throws RemoteException {
            mBooks.add(book);
            Log.d(TAG, Thread.currentThread().getName());
            Log.d(TAG, "addBook");
            onNewBookBroacast(book);
        }

        @Override
        public void registerOnNewBookListener(IOnNewBookListener onNewBookListener) throws RemoteException {
            mOnNewBookListeners.register(onNewBookListener);
                Log.d(TAG, "unRegisterOnNewBookListener");
        }

        @Override
        public void unRegisterOnNewBookListener(IOnNewBookListener onNewBookListener) throws RemoteException {
            mOnNewBookListeners.unregister(onNewBookListener);
            Log.d(TAG, "unRegisterOnNewBookListener");
        }
    };

    public void onNewBookBroacast(Book book) throws RemoteException {

        int size = mOnNewBookListeners.beginBroadcast();
        for(int i = 0; i < size; i++) {
            IOnNewBookListener iOnNewBookListener = mOnNewBookListeners.getBroadcastItem(i);
            iOnNewBookListener.onNewBook(book);
        }
        mOnNewBookListeners.finishBroadcast();
    }

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
