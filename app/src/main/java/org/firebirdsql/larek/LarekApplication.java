package org.firebirdsql.larek;

import android.app.Application;

/**
 * Created by nazar.humeniuk on 7/16/17.
 */

public class LarekApplication extends Application {
    private static LarekApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized LarekApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectionReceiver.ConnectivityReceiverListener listener) {
        ConnectionReceiver.connectivityReceiverListener = listener;
    }
}
