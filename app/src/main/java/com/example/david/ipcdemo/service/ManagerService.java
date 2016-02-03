package com.example.david.ipcdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.david.ipcdemo.MyConstants;

/**
 * @author WeiDeng
 * @FileName com.example.david.ipcdemo.service.ManagerService.java
 * @date 2016-01-31 00:20
 * @describe
 */
public class ManagerService extends Service {

    public final String TAG = getClass().getSimpleName();

    private int i = 0;
    private Messenger messenger = new Messenger(new ManagerServiceHandler());

    private class ManagerServiceHandler extends Handler {

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch(what) {
                case MyConstants.MANAGER_SERVICE_TOAST_MESSAGE:
                    final Bundle data = msg.getData();
                    String str = data.getString("data");
                    Toast.makeText(ManagerService.this, str, Toast.LENGTH_SHORT).show();
                    final Messenger replyTo = msg.replyTo;
                    if(replyTo != null) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while(true) {
                                    i++;
                                    Message message = Message.obtain(null, MyConstants.MAIN_ACTIVITY_REPLY_MESSAGE);
                                    data.putString("data", String.valueOf(i));
                                    message.setData(data);

                                    try {
                                        replyTo.send(message);
                                        Thread.sleep(3000);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    }
                    break;
            }
        }
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
        return messenger.getBinder();
    }
}
