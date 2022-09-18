package com.lhl.security;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.File;

public class Util {

    private static final String[] rootRelatedDirs = new String[]{
            "/su", "/su/bin/su", "/sbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/data/local/su",
            "/system/xbin/su",
            "/system/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su",
            "/system/bin/cufsdosck", "/system/xbin/cufsdosck", "/system/bin/cufsmgr",
            "/system/xbin/cufsmgr", "/system/bin/cufaevdd", "/system/xbin/cufaevdd",
            "/system/bin/conbb", "/system/xbin/conbb"};

    public static boolean isAdb(Context context) {
        boolean enableAdb = (Settings.Secure.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) > 0);
        return enableAdb;
    }

    public static boolean isRoot() {
        boolean hasRootDir = false;
        String[] rootDirs;
        int dirCount = (rootDirs = rootRelatedDirs).length;
        for (int i = 0; i < dirCount; ++i) {
            String dir = rootDirs[i];
            if ((new File(dir)).exists()) {
                hasRootDir = true;
                break;
            }
        }
        return Build.TAGS != null && Build.TAGS.contains("test-keys") || hasRootDir;
    }

    public static boolean isDebug(Context context) {
        return context.getApplicationContext().getApplicationInfo() != null && (context.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static boolean httpProxy(Context context) {
        String proxyAddress;
        int proxyPort;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
            if (TextUtils.isEmpty(proxyAddress) || proxyPort == -1) {
                proxyAddress = System.getProperty("https.proxyHost");
                portStr = System.getProperty("https.proxyPort");
                proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
            }
            if (TextUtils.isEmpty(proxyAddress) || proxyPort == -1) {
                proxyAddress = System.getProperty("ftp.proxyHost");
                portStr = System.getProperty("ftp.proxyPort");
                proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
            }
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return proxyAddress != null && !proxyAddress.isEmpty() && proxyPort != -1;
    }
}
