package com.example.david.ipcdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.david.ipcdemo.service.ManagerService;

public class MainActivity extends AppCompatActivity {

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
        }
    };

    private class MainService extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int what = msg.what;
            switch (what) {
                case MyConstants.MAIN_ACTIVITY_REPLY_MESSAGE:
                    Bundle data = msg.getData();
                    String str = data.getString("data");
                    Toast.makeText(MainActivity.this, "MainActivity : "+ str, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, TwoActivity.class);
        startActivity(intent);
    }

    public void onClickService(View view) {

        Intent intent = new Intent(this, ManagerService.class);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private Messenger messenger;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);

            Message message = Message.obtain();
            message.what = MyConstants.MANAGER_SERVICE_TOAST_MESSAGE;
            Bundle bundle = new Bundle();
            bundle.putString("data", "hello Android");
            message.setData(bundle);
            message.replyTo = new Messenger(new MainService());
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
