package com.lhl.security;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;

public class SecurityActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {

    private static boolean root = Util.isRoot();
    private static int statusBarHeight = 0;
    private int count = 0;
    private Security.Builder builder;

    public SecurityActivityLifecycleCallback(Security.Builder builder) {
        this.builder = builder;
    }

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreStarted(activity);
    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostStarted(activity);
    }

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreResumed(activity);
    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostResumed(activity);
    }

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPrePaused(activity);
    }

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostPaused(activity);
    }

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreStopped(activity);
    }

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostStopped(activity);
    }

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreSaveInstanceState(activity, outState);
    }

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostSaveInstanceState(activity, outState);
    }

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreDestroyed(activity);
    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostDestroyed(activity);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (count == 0) {

        }
        count++;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        resumed(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        count--;
        if (count == 0) {
            checkEditText(activity);
            printSp(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    private void resumed(Activity activity) {
        checkRoot(activity);
        checkAdb(activity);
    }

    private void checkRoot(Activity activity) {
        if (!builder.checkRoot)
            return;
        if (!Util.isRoot())
            return;
        if (builder.listener == null) {
            Log.e("Security", "当前设备被root了");
            return;
        }
        builder.listener.onRoot(activity);
    }

    private void checkAdb(Activity activity) {
        if (!builder.checkAdb)
            return;
        if (!Util.isAdb(activity))
            return;
        if (builder.listener == null) {
            Log.e("Security", "当前设备正处于adb模式");
            return;
        }
        builder.listener.onAdb(activity);
    }


    private void checkEditText(Activity activity) {
        if (!Util.isDebug(activity))
            return;
        if (!builder.checkEditTextEmpty)
            return;
        new Thread() {
            WeakReference<Activity> reference = new WeakReference<>(activity);

            @Override
            public void run() {
                super.run();
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Activity a = reference.get();
                if (a == null)
                    return;
                Window window = a.getWindow();
                if (window == null)
                    return;
                if (checkEditText((ViewGroup) window.getDecorView()))
                    Log.e("Security", a.getClass().getName() + "中请确保私密数据是否清空");
            }
        }.start();
    }

    private boolean checkEditText(ViewGroup group) {
        if (group == null)
            return false;
        int count = group.getChildCount();
        if (count <= 0)
            return false;
        View view;
        for (int i = 0; i < count; i++) {
            view = group.getChildAt(i);
            if (view instanceof ViewGroup) {
                if (checkEditText((ViewGroup) view))
                    return true;
                continue;
            }
            if (!(view instanceof EditText))
                continue;
            if (!TextUtils.isEmpty(((EditText) view).getText().toString()))
                return true;
        }
        return false;
    }

    private void printSp(Context context) {
        if (!Util.isDebug(context))
            return;
        if (!builder.checkSp)
            return;
        File file = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            file = new File(context.getDataDir(), "shared_prefs");
        else
            file = new File(context.getApplicationInfo().dataDir, "shared_prefs");
        if (!file.exists() || file.isFile())
            return;
        File[] files = file.listFiles();
        if (files.length <= 0)
            return;
        Log.e("Security", "项目中使用了SharedPreferences，请确保私密数据加密了");
        for (File f : files) {
            if (!f.exists())
                continue;
            Log.e("Security", f.getAbsolutePath());
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.e("Security", line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
