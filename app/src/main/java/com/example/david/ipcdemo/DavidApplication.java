package com.example.david.ipcdemo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import java.util.List;

/**
 * @author WeiDeng
 * @FileName com.example.david.ipcdemo.DavidApplication.java
 * @date 2016-01-28 22:22
 * @describe
 */
public class DavidApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, getCurProcessName(), Toast.LENGTH_SHORT).show();
    }

    public String getCurProcessName() {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        if(runningAppProcesses == null) {
            return "";
        }
        for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if(runningAppProcessInfo.pid == pid) {
                return runningAppProcessInfo.processName;
            }
        }

        return "";
    }
}
