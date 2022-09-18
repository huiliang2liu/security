###  使用说明
#### 作用  
##### 在调试模式下  
        检测项目是否打开了数据备份，activity是否对外开放，存储数据是否用了SharedPreferences，并且在
    应用退到后台打印SharedPreferences中数据，并提醒私密数据加密处理 
##### 在任何情况下
        检测设备是否打开了ADB调试，检测设备是否root，检测设备是否设置了网络代理,检测设备网络变化，检测设备退到后台EditTextView中的数据是否清空
##### 查看提醒日志
        用Security过滤error日志
#### 使用 
##### 配置资源来源
```
maven { url = uri("https://gitee.com/liu-huiliang/jarlibs/raw/master") }
```
##### 配置库引用
```
implementation 'com.lhl.security:security:1.0.0'
```

##### 在application中调用
```
new Security.Builder(this).setCheckAdb(true)
                .setCheckSp(true).setCheckManifest(true)
                .setCheckRoot(true)
                .setCheckProxy(true)
                .setCheckEditTextEmpty(true)
                .setCheckNetworkChange(true)
                .setListener(new SecurityListener() {
                    @Override
                    public void onRoot(Activity activity) {
                        Log.e("======", "onRoot");
                    }

                    @Override
                    public void onAdb(Activity activity) {
                        Log.e("======", "onAdb");
                    }

                    @Override
                    public void onProxy() {
                        Log.e("======", "onProxy");
                    }

                    @Override
                    public void onNetworkChange() {
                        Log.e("======", "onNetworkChange");
                    }
                }).build();
```

#### api说明
##### SecurityListener
| 方法名 | 方法说明                         | 参数 |
| --- |------------------------------| ---  |
| onRoot | 当前设备被root了，每个activity可见时回调   | activity当前可见的activity |
| onAdb | 当前设备打开了adb调试，每个activity可见时回调 | activity当前可见的activity |
| onProxy | 当前设备网络存在代理回调，app启动回调一次       |  |
| onNetworkChange | 当前设备网络发生变化回调                 |  |

##### Security.Builder
| 方法名 | 方法说明                                       | 参数           |
| --- |--------------------------------------------|--------------|
| setCheckAdb | 设置是否检测设备是否打开adb调试                          | checkAdb是否检测 |
| setCheckManifest | 设置是否检测清单配置是否安全，比如是否允许备份数据，activity是否允许外部调用 | checkManifest是否检测 |
| setCheckRoot | 设置是否检测设备是否root                             | checkRoot是否检测 |
| setCheckSp | 设置是否使用了SharedPreferences                   | checkSp是否检测  |
| setCheckProxy | 设置是否检测代理                                   | checkProxy是否检测 |
| setCheckNetworkChange | 设置是否检测网络变化                                 | checkNetworkChange是否检测         |
| setCheckEditTextEmpty | 设置是否检测设备退出后台编辑的数据是否清空                      | checkNetworkChange是否检测         |
| setListener | 设置adb,root,网络代理检测回调，不设置或者设置为null则在日志中提醒    | listener adb和root回调 |
| build | 创建安全检测者                                    |  |