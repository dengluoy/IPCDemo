package com.example.david.ipcdemo.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author WeiDeng
 * @FileName com.example.david.ipcdemo.utils.Utils.java
 * @date 2016-02-19 22:31
 * @describe
 */
public class Utils {

    public static void streamClose(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
