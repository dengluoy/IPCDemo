package com.example.david.ipcdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.david.ipcdemo.service.TCPServerService;
import com.example.david.ipcdemo.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author WeiDeng
 * @FileName com.example.david.ipcdemo.SocketActivity.java
 * @date 2016-02-19 22:34
 * @describe
 */
public class SocketActivity extends Activity {

    public final String TAG = getClass().getSimpleName();

    public static final int MESSAGE_SOCKET_CONNECTION_SUCCEED = 0x4;
    public static final int MESSAGE_RECEIVE_NEW_MESSAGE = 0x5;

    private TextView mReplyContentTv;
    private EditText mChatContentEdit;
    private Button mSendChatBt;

    private Socket mSocket;
    private PrintWriter mPrintWriter;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case MESSAGE_SOCKET_CONNECTION_SUCCEED:
                    mSendChatBt.setEnabled(true);
                    Log.d(TAG, "连接成功");
                    break;
                case MESSAGE_RECEIVE_NEW_MESSAGE:
                    String text = (String) msg.obj;
                    String content = mReplyContentTv.getText().toString().trim();
                    mReplyContentTv.setText(content +  ", " + text);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatContentEdit = (EditText) findViewById(R.id.chat_content_edit);
        mReplyContentTv = (TextView) findViewById(R.id.reply_content_tv);
        mSendChatBt = (Button) findViewById(R.id.send_chat_button);
        mSendChatBt.setEnabled(false);
        Intent intent = new Intent(this,TCPServerService.class);
        startService(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectionServer();
//                connectTCPServer();
            }

        }).start();
    }

    public void sendChatClick(View view) {
        String sendContent = mChatContentEdit.getText().toString().trim();
        if(!TextUtils.isEmpty(sendContent) && mPrintWriter != null) {
            mPrintWriter.println(sendContent);
            mChatContentEdit.setText("");
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String formatDateTime(long time) {
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }

    private void connectTCPServer() {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 8688);
                mSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTION_SUCCEED);
                System.out.println("connect server success");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                System.out.println("connect tcp server failed, retry...");
            }
        }

        try {
            // 接收服务器端的消息
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            while (!SocketActivity.this.isFinishing()) {
                String msg = br.readLine();
                System.out.println("receive :" + msg);
                if (msg != null) {
                    String time = formatDateTime(System.currentTimeMillis());
                    final String showedMsg = "server " + time + ":" + msg
                            + "\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MESSAGE, showedMsg)
                            .sendToTarget();
                }
            }
            System.out.println("quit...");
            Utils.streamClose(mPrintWriter);
            Utils.streamClose(br);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectionServer() {

        while (mSocket == null && !SocketActivity.this.isFinishing()) {
            try {
                mSocket = new Socket("localhost", 8688);
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())),true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTION_SUCCEED);
            } catch (IOException e) {
                SystemClock.sleep(1000);
                e.printStackTrace();
            }
        }

        if(mSocket != null) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                Log.d(TAG, "开始接收");
                while(!SocketActivity.this.isFinishing()) {
                    String msg = br.readLine();
                    Log.d(TAG, "读取信息 ： " + msg);
                    if(msg != null) {
                        mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MESSAGE, msg).sendToTarget();
                    }
                }

                Utils.streamClose(br);
                Utils.streamClose(mPrintWriter);
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {

        if(mSocket != null) {
            try {
                mSocket.shutdownInput();
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
