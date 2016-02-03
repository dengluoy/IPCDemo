package com.example.david.ipcdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
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
    private IBookManager mIBookManager;

    private TextView mContentTv;
    private TextView mButtonTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        mContentTv = (TextView) findViewById(R.id.content_name_tv);
        mContentTv.setText(TAG);
        mButtonTv = (TextView) findViewById(R.id.content_button_tv);

        mButtonTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(mIBookManager == null) {
                        Toast.makeText(TwoActivity.this, "请先绑定远程服务", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    mIBookManager.addBook(new Book(22, "影响力"));
                    List<Book> bookList = mIBookManager.getBookList();

                    System.out.println();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        mContentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(TwoActivity.this, ThreeActivity.class);
//                startActivity(intent);

                Intent intent = new Intent(TwoActivity.this, AIDLManagerService.class);
                bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mIBookManager = IBookManager.Stub.asInterface(service);
                mIBookManager.asBinder().linkToDeath(deathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
