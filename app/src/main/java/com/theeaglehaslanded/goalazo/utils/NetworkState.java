package com.theeaglehaslanded.goalazo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.theeaglehaslanded.goalazo.R;

public class NetworkState {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void NetworkUnavailableToast(Context context){
        Toast.makeText(context, R.string.connectivity_toast,
                Toast.LENGTH_LONG).show();
    }
}
