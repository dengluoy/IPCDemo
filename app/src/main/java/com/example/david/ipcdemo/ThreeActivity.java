package com.example.david.ipcdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.ipcdemo.service.AIDLManagerService;

import java.util.List;

/**
 * @author WeiDeng
 * @FileName com.example.david.ipcdemo.ThreeActivity.java
 * @date 2016-01-28 22:14
 * @describe
 */
public class ThreeActivity extends Activity {

    public final String TAG = getClass().getSimpleName();


    private TextView mContentTv;
    private TextView mBookListNameTv;

    private IBookManager iBookManager;
    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == AIDLManagerService.MESSAGE_NEW_BOOK_ARRIVED) {
                Book newBook = (Book) msg.obj;
                StringBuffer sb = new StringBuffer();
                String textContent = mBookListNameTv.getText().toString().trim();
                sb.append(textContent);
                sb.append(newBook.toString());
                mBookListNameTv.setText(sb.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        mContentTv = (TextView) findViewById(R.id.content_name_tv);
        mBookListNameTv = (TextView) findViewById(R.id.book_list_name);
        mContentTv.setText(TAG);

        mContentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ThreeActivity.this, "ThreeAcitvity.End Point", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onAIDLManager(View view) {
        Intent intent = new Intent(this, AIDLManagerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBookManager = IBookManager.Stub.asInterface(service);
            if (iBookManager != null) {
                try {
                    iBookManager.registerOnNewBookListener(onNewBookListener);
                    iBookManager.addBook(new Book(3, "Android_开发艺术"));
                    new Thread(new PerformGetBookMethodTask()).start();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        if(iBookManager != null && iBookManager.asBinder().isBinderAlive()) {
            try {
                iBookManager.unRegisterOnNewBookListener(onNewBookListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        unbindService(serviceConnection);
        super.onDestroy();
    }

    IOnNewBookListener onNewBookListener = new IOnNewBookListener.Stub() {
        @Override
        public void onNewBook(Book book) throws RemoteException {
            try {
                mHandler.obtainMessage(AIDLManagerService.MESSAGE_NEW_BOOK_ARRIVED, book).sendToTarget();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    class PerformGetBookMethodTask implements Runnable {

        @Override
        public void run() {

            if (iBookManager != null) {
                try {
                    List<Book> bookList = iBookManager.getBookList();
                    for (Book book : bookList) {
                        Log.d(TAG, book.getBookName());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}
