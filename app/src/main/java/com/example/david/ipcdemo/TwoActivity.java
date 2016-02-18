package com.example.david.ipcdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
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
 * @FileName com.example.david.ipcdemo.TwoActivity.java
 * @date 2016-01-28 22:07
 * @describe
 */
public class TwoActivity extends Activity {

    public final String TAG = getClass().getSimpleName();
    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if(mIBookManager != null) {
                mIBookManager.asBinder().unlinkToDeath(this,0);
                mIBookManager = null;
            }
        }
    };

    private TextView mContentTv;
    private TextView mBookListNameTv;

    private IBookManager mIBookManager;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            System.out.println();
            if(msg.what == AIDLManagerService.MESSAGE_NEW_BOOK_ARRIVED) {
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
        mContentTv.setText(TAG);
        mBookListNameTv = (TextView) findViewById(R.id.book_list_name);
        mContentTv.setText(TAG);

        mContentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TwoActivity.this, ThreeActivity.class);
                startActivity(intent);

            }
        });
    }

    public void onAIDLManager(View view) {
        Intent intent = new Intent(this, AIDLManagerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBookManager = IBookManager.Stub.asInterface(service);
            if(mIBookManager != null) {
                try {
                    mIBookManager.asBinder().linkToDeath(deathRecipient,0);
                    mIBookManager.registerOnNewBookListener(onNewBookListener);
                    mIBookManager.addBook(new Book(55, "Android源码情景分析"));
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

    private IOnNewBookListener onNewBookListener = new IOnNewBookListener.Stub() {
        @Override
        public void onNewBook(Book book) throws RemoteException {
            try {
                mHandler.obtainMessage(AIDLManagerService.MESSAGE_NEW_BOOK_ARRIVED,book).sendToTarget();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    
    class PerformGetBookMethodTask implements Runnable{

        @Override
        public void run() {
            if(mIBookManager != null) {
                try {
                    List<Book> bookList = mIBookManager.getBookList();
                    for(Book book : bookList) {
                        Log.d(TAG, book.toString());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mIBookManager != null && mIBookManager.asBinder().isBinderAlive()) {
            try {
                mIBookManager.unRegisterOnNewBookListener(onNewBookListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        unbindService(serviceConnection);
        super.onDestroy();
    }

}
