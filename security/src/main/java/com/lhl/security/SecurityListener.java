package com.lhl.security;

import android.app.Activity;

public interface SecurityListener {
    void onRoot(Activity activity);
    void onAdb(Activity activity);
    void onProxy();
    void onNetworkChange();
}
