package com.lhl.security;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;

public class Security {
    private static final String TAG = "Security";

    private Security(Builder builder) {
        if (builder.checkAdb || builder.checkRoot || builder.checkSp)
            ((Application) builder.context.getApplicationContext()).registerActivityLifecycleCallbacks(new SecurityActivityLifecycleCallback(builder));
        checkProxy(builder);
        checkNetworkChange(builder);
        checkManifest(builder);

    }

    private void checkNetworkChange(Builder builder) {
        if (!builder.checkNetworkChange)
            return;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            WeakReference<Builder> reference = new WeakReference<>(builder);
            private boolean init = false;

            @Override
            public void onReceive(Context context, Intent intent) {
                if (!init) {
                    init = true;
                    return;
                }
                Builder b = reference.get();
                if (b == null || b.listener == null) {
                    Log.e("Security", "网络发生了变化");
                    return;
                }
                b.listener.onNetworkChange();
            }
        };
        builder.context.registerReceiver(receiver, filter);
    }

    private void checkProxy(Builder builder) {
        if (!builder.checkProxy)
            return;
        if (!Util.httpProxy(builder.context))
            return;
        if (builder.listener == null) {
            Log.e("Security", "当前网络存在代理，请注意网络安全");
            return;
        }
        builder.listener.onProxy();
    }

    private void checkManifest(Builder builder) {
        if (!Util.isDebug(builder.context))
            return;
        if (!builder.checkManifest)
            return;
        try {
            PackageInfo applicationInfo = builder.context.getPackageManager().getPackageInfo(builder.context.getPackageName(), PackageManager.GET_ACTIVITIES);
            boolean allowBackup = (applicationInfo.applicationInfo.flags & ApplicationInfo.FLAG_ALLOW_BACKUP) == ApplicationInfo.FLAG_ALLOW_BACKUP;
            if (allowBackup)
                Log.e(TAG, "allowBackup不要设置成true，数据会有被复制的危险，请确认是否需要备份数据，如果不需要请设置成false");
            ActivityInfo[] activityInfos = applicationInfo.activities;
            if (activityInfos != null && activityInfos.length > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (ActivityInfo activityInfo : activityInfos) {
                    if (!activityInfo.exported)
                        continue;
                    stringBuilder.setLength(0);
                    stringBuilder.append(activityInfo.name).append("的exported是true,请确认是否要对外开发，不需要的话请设置成false，需要的则确认做了数据校验");
                    Log.e(TAG, stringBuilder.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class Builder {
        boolean checkManifest = false;
        boolean checkSp = false;
        boolean checkRoot = false;
        boolean checkAdb = false;
        boolean checkProxy = false;
        boolean checkNetworkChange = false;
        boolean checkEditTextEmpty = false;
        Context context;
        SecurityListener listener;


        public Builder(Context context) {
            this.context = context;
            assert context != null : "context is null";
        }

        public Builder setCheckAdb(boolean checkAdb) {
            this.checkAdb = checkAdb;
            return this;
        }

        public Builder setCheckManifest(boolean checkManifest) {
            this.checkManifest = checkManifest;
            return this;
        }

        public Builder setCheckRoot(boolean checkRoot) {
            this.checkRoot = checkRoot;
            return this;
        }

        public Builder setCheckSp(boolean checkSp) {
            this.checkSp = checkSp;
            return this;
        }

        public Security build() {
            return new Security(this);
        }

        public Builder setCheckProxy(boolean checkProxy) {
            this.checkProxy = checkProxy;
            return this;
        }

        public Builder setListener(SecurityListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setCheckNetworkChange(boolean checkNetworkChange) {
            this.checkNetworkChange = checkNetworkChange;
            return this;
        }

        public Builder setCheckEditTextEmpty(boolean checkEditTextEmpty) {
            this.checkEditTextEmpty = checkEditTextEmpty;
            return this;
        }
    }
}
