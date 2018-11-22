package com.andreapetreti.android_utils.connectivity;

import androidx.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;

import com.annimon.stream.Objects;
import com.annimon.stream.Optional;
import com.annimon.stream.function.ToBooleanFunction;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class ConnectionLiveData extends LiveData<Boolean> {

    private Context mContext;
    private ConnectivityManager mConnectivityManager;

    private NetworkBroadcastReceiver mNetworkBroadcastReceiver;
    private ConnectivityManager.NetworkCallback mNetworkCallback;

    // fix to twice call
    private boolean mLastConnectStatus;

    private Handler mHandler;

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public ConnectionLiveData(Context context) {
        mContext = context;
        mHandler = new Handler();
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNetworkCallback = new NetworkCallbackImpl();
        } else {
            mNetworkBroadcastReceiver = new NetworkBroadcastReceiver();
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        // when livedata is active, post value for check if it is connected!
        boolean connected = isWifiMobileConnected();
        postValue(connected);
        mLastConnectStatus = connected;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mConnectivityManager.registerDefaultNetworkCallback(mNetworkCallback);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .build();
            mConnectivityManager.registerNetworkCallback(networkRequest, mNetworkCallback);
        } else {
            mContext.registerReceiver(mNetworkBroadcastReceiver, new IntentFilter(CONNECTIVITY_ACTION));
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
        } else {
            mContext.unregisterReceiver(mNetworkBroadcastReceiver);
        }
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mHandler.postDelayed(() -> {
                boolean connected = isWifiMobileConnected();
                if(mLastConnectStatus && !connected) {
                    postValue(false);
                    mLastConnectStatus = false;
                } else if(!mLastConnectStatus && connected) {
                    postValue(true);
                    mLastConnectStatus = true;
                }
            }, 2000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            /* Trigger post value if previously is not connected */
            if(!mLastConnectStatus) {
                postValue(true);
                mLastConnectStatus = true;
            }
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            mHandler.postDelayed(() -> {
                /* Trigger post value if both wifi and 3g is not connected,
                    and if previously is connected */
                if(!isWifiMobileConnected() && mLastConnectStatus) {
                    postValue(false);
                    mLastConnectStatus = false;
                }
            }, 2000);
        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
        }
    }

    private boolean isWifiMobileConnected() {
        final NetworkInfo mobileInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        final NetworkInfo wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean mobileIsConnected = Objects.nonNull(mobileInfo) && mobileInfo.isConnectedOrConnecting();
        boolean wifiIsConnected = Objects.nonNull(wifiInfo) && wifiInfo.isConnectedOrConnecting();
        return wifiIsConnected || mobileIsConnected;
    }
}
