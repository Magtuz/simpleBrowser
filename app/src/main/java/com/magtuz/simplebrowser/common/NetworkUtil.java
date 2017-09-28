package com.magtuz.simplebrowser.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by magtuz on 9/28/2017.
 */

public class NetworkUtil {
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static String hasProtocol(final String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        return "http://" + url;
    }


}
