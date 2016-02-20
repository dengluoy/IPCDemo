package com.example.david.ipcdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.david.ipcdemo.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


/**
 * @author WeiDeng
 * @FileName com.example.david.ipcdemo.service.TCPServerService.java
 * @date 2016-02-19 01:06
 * @describe
 */
public class TCPServerService extends Service {

    public final String TAG = getClass().getSimpleName();

    private ServerSocket mServerSocket;

    private boolean isTCPDestroy;

    private String[] mChatContent = new String[]{
            "嗨， 你好！","觉得你人真不错","你在干嘛呢？","你丫干啥呢？"
    };

    @Override
    public void onCreate() {
        new Thread(new TCPServerTask()).start();
        Log.d(TAG, "TCP connection onCreate ");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isTCPDestroy = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class TCPServerTask implements Runnable {

        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(8688);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "serverSocket connection NULL ");
                return;
            }

            while(!isTCPDestroy) {
                final Socket socket;
                try {
                    socket = mServerSocket.accept();
                    Log.d(TAG, "TCP connection onCreate " + socket);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                responseClient(socket);
                            } catch (IOException e) {
                                Log.d(TAG, "responseClient connection NULL ");
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
        pw.println("欢迎连接聊天服务");
        Log.d(TAG, "欢迎连接聊天服务 ");
        Log.d(TAG, "isTCPDestroy  :" + isTCPDestroy);
        while(!isTCPDestroy) {
            String content = br.readLine();
            Log.d(TAG, "content : " + content);
            if(TextUtils.isEmpty(content)) {
                break;
            }

            int i = new Random().nextInt(mChatContent.length);
            String replyToContent = mChatContent[i];
            pw.println(replyToContent);
            Log.d(TAG, "replyToContent : " + replyToContent);
        }
        Utils.streamClose(br);
        Utils.streamClose(pw);
        client.close();
    }
}
