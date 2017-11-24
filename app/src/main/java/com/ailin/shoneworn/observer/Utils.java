package com.ailin.shoneworn.observer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dalvik.system.DexFile;

public class Utils {

    /*
	 * 小米系统相关参数，用于判断是否是小米系统
	 */
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    public static final String TAG = Utils.class.getCanonicalName();

    /**
     * 数据加密的可?
     */
    private static final String ENCRYPT_KEY = "GUANGSU";

    /**
     * 根据Apk的路径获取包?
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static String getApkPackageName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info == null) {
            return null;
        }

        ApplicationInfo appInfo = info.applicationInfo;
        String appName = pm.getApplicationLabel(appInfo).toString();
        String packageName = appInfo.packageName; // 得到安装包名?
        String version = info.versionName; // 得到版本信息
        // Drawable icon = pm.getApplicationIcon(appInfo);//得到图标信息

        return packageName;
    }

    /**
     * 获取本应用的包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 得到当前版本?
     *
     * @param context -- 上下?
     * @return 当前版本?
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            synchronized (context){

                // 根据包名获取版本信息?表示获取版本信息?
                packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            }

            return packageInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * 得到当前版本名称
     *
     * @param context -- 上下?
     * @return 当前版本?
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {

            synchronized (context){

                // 根据包名获取版本信息?表示获取版本信息?
                packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            }

            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
        }

        return "";
    }

    /**
     * 安装apk
     *
     * @param context
     * @param apkPath
     */
    public static void installApk(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                "application/vnd.android.package-archive");

        /**
         * Context中有?startActivity方法，Activity继承自Context，重载了startActivity方法。如果使?
         * Activity的startActivity方法
         * ，不会有任何限制，?如果使用Context的startActivity方法的话，就???新的task
         * ，遇到上面那个异常的，都是因为使用了Context的startActivity方法。解决办法是，加?flag?
         * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         */
        if (!Activity.class.isAssignableFrom(context.getClass())) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);

    }

    /**
     * 根据包名启动应用
     *
     * @param context
     * @param packageName
     */
    public static void startApk(Context context, String packageName) {
        List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);
        if ((matches != null) && (matches.size() > 0)) {
            ResolveInfo resolveInfo = matches.get(0);
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            startApk(context, activityInfo.packageName, activityInfo.name);
        }
    }

    /**
     * 根据包名、activity名启动应?
     *
     * @param context
     * @param packageName
     * @param className
     */
    public static void startApk(Context context, String packageName, String className) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);

        if (!Activity.class.isAssignableFrom(context.getClass())) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    /**
     * 根据包名，设置应用的启动页面的Intent，然后获取这个Intent的信息?
     *
     * @param context
     * @param packageName
     * @return
     */
    public static List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(packageName);
        final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        return apps != null ? apps : new ArrayList<ResolveInfo>();
    }

    /**
     * 判读apk是否已经安装过了
     *
     * @param context
     * @param apkPackageName
     * @return
     */
    public static boolean isInstalledApk(Context context, String apkPackageName) {

        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> appList = packageManager.getInstalledPackages(0);

        for (int i = 0; i < appList.size(); i++) {
            PackageInfo pinfo = appList.get(i);

            if (pinfo.applicationInfo.packageName.equals(apkPackageName)) {
                return true;
            }

        }

        return false;
    }

    /**
     * 获取已安装的apk列表
     *
     * @param context
     * @return
     */
    /*
    public static HashMap<String, InstalledApkInfo> getInstalledApkMap(Context context) {

		HashMap<String, InstalledApkInfo> installedApkMap = new HashMap<String, InstalledApkInfo>();

		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> appList = packageManager.getInstalledPackages(0);

		for (int i = 0; i < appList.size(); i++) {
			PackageInfo pinfo = appList.get(i);
			if (pinfo != null) {
				ApplicationInfo appInfo = pinfo.applicationInfo;

				InstalledApkInfo installedApkInfo = new InstalledApkInfo();
				installedApkInfo.setAppName(packageManager.getApplicationLabel(appInfo).toString());
				installedApkInfo.setPackageName(appInfo.packageName);
				installedApkInfo.setVersionName(pinfo.versionName);
				installedApkInfo.setVersionCode(pinfo.versionCode);

				// LogUtil.log(TAG, installedApkInfo.toString());

				installedApkMap.put(appInfo.packageName, installedApkInfo);
			}

		}

		return installedApkMap;
	}
	*/

    /**
     * 遍历本apk下指定的包，返回遍历到的类名列表
     *
     * @param context
     * @param packageName
     * @return 类名列表
     */
    public static List<String> traversePackage(Context context, String packageName) {
        List<String> classNameList = new ArrayList<String>();

        try {
            String path = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    0).sourceDir;
            DexFile dexfile = new DexFile(path);

            Enumeration<String> entries = dexfile.entries();
            while (entries.hasMoreElements()) {
                String name = (String) entries.nextElement();
                if (name.indexOf(packageName) >= 0) {
                    classNameList.add(name);
                }
            }

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classNameList;
    }

    /**
     * ?震动
     *
     * @param
     * @param milliseconds 震动多长时间，单位：毫秒?
     * @return
     */
    public static void vibrate(final Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * 重复震动
     *
     * @param
     * @param pattern  自定义震动模?。数组中数字的含义依次是[静止时长，震动时长]时长的单位是毫秒
     * @param isRepeat 是否反复震动
     * @return
     */
    public static void vibrate(final Context context, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wifi.getConnectionInfo();

        return info.getMacAddress();
    }

    /**
     * 获取IMEI?
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 获取AndroidID
     *
     * @param context
     * @return
     */
    public static String getAndroidID(Context context) {
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * 判断指定的类名是否是当前顶部Activity?
     *
     * @return 是，返回ture；否，返回false?
     */
    public static boolean isTopActivity(Context context, String classCanonicalName) {

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
        String className = cn.getClassName();

        return className.equals(classCanonicalName);
    }

    /**
     * 用来判断服务是否后台运行
     *
     * @param
     * @param className 判断的服务名?
     * @return true 在运?false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean IsRunning = false;

        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                IsRunning = true;
                break;
            }
        }
        return IsRunning;
    }

    /**
     * MD5加密?2?
     *
     * @param str
     * @return
     */
    public static String toMD5(String str) {
        if (str == null || str.equals("")) {
            return null;
        }

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return null;
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * MD5加密?6?
     *
     * @param str
     * @return
     */
    public static String to16MD5(String str) {
        String md5Str = toMD5(str);
        if (md5Str == null) {
            return null;
        }

        return md5Str.substring(8, 24);
    }

    /**
     * 获取状?栏高?
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Class<?> localClass;
        try {
            localClass = Class.forName("com.android.internal.R$dimen");
            Object localObject = localClass.newInstance();
            int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject)
                    .toString());
            statusHeight = context.getResources().getDimensionPixelSize(i5);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return statusHeight;
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断wifi是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断3G/4G是否可用
     *
     * @param context
     * @return
     */
    public static boolean is3G4GConnected(Context context) {
        boolean is3G4G = false;
        if (context != null) {

            NetState netCode = isConnected(context);
            switch (netCode) {
                case NET_NO:
                    // 没有网络连接
                    break;
                case NET_2G:
                    // 2g网络
                    break;
                case NET_3G:
                    // 3g网络
                    is3G4G = true;
                    break;
                case NET_4G:
                    // 4g网络
                    is3G4G = true;
                    break;
                case NET_WIFI:
                    // WIFI网络
                    break;
                case NET_UNKNOWN:
                    // 未知网络
                    break;
                default:
                    // 不知道什么情况~>_<~
                    break;
            }
        }
        return is3G4G;
    }

    /**
     * 判断当前是否网络连接
     *
     * @param context
     * @return 状??
     */
    /**
     * 枚举网络状? NET_NO：没有网?NET_2G:2g网络 NET_3G?g网络 NET_4G?g网络 NET_WIFI：wifi
     * NET_UNKNOWN：未知网?
     */
    public static enum NetState {
        NET_NO, NET_2G, NET_3G, NET_4G, NET_WIFI, NET_UNKNOWN
    }

    ;

    public static NetState isConnected(Context context) {
        NetState stateCode = NetState.NET_NO;
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (ni != null && ni.isAvailable()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    stateCode = NetState.NET_WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联?2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            stateCode = NetState.NET_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            stateCode = NetState.NET_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            stateCode = NetState.NET_4G;
                            break;
                        default:
                            stateCode = NetState.NET_UNKNOWN;
                    }
                    break;
                default:
                    stateCode = NetState.NET_UNKNOWN;
            }

        }
        return stateCode;
    }

    /**
     * 判断Mobile网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取当前连接的网络类?
     *
     * @param context
     * @return
     */
    public static int getConnectedNetType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取系统版本
     *
     * @return
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取当前wifiip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
    try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
    }

    /**
     * 获取手机ip地址
     *
     * @return
     */
    public static String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        if ("10.0.2.15".equals(inetAddress.getHostAddress().toString())) {
                            continue;
                        }
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取渠道名称
     *
     * @param context
     * @return
     */
    public static String getChannelName(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);

            String channelNumber = appInfo.metaData.getString("TencentInstallChannel");
            return channelNumber;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取手机号码
     *
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }

    /**
     * 测量能够画在指定宽度内字符串的字体大?
     *
     * @param text        字符?
     * @param width       指定的宽?
     * @param maxTextSize ?的字体大?
     * @return
     */

    public static float measureTextSize(String text, int width, float maxTextSize) {
        if (TextUtils.isEmpty(text)) {
            return maxTextSize;
        }

        float textSize = maxTextSize;
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        float textWidth = paint.measureText(text);

        while (textWidth > width) {
            textSize--;
            paint.setTextSize(textSize);
            textWidth = paint.measureText(text);
        }

        return textSize;
    }

    /**
     * 对字节数据进行加密：加密算法是异或，?加密和解密是?的?
     *
     * @param buff
     * @return
     */
    public static byte[] decryptOrEncryptByteData(byte[] buff) {
        if (buff == null) {
            return buff;
        }

        byte[] encryptKey = ENCRYPT_KEY.getBytes();
        int keyIndex = 0;
        for (int i = 0; i < buff.length; i++) {
            buff[i] = (byte) (buff[i] ^ encryptKey[keyIndex]);
            keyIndex++;
            if (keyIndex >= encryptKey.length) {
                keyIndex = 0;
            }
        }

        return buff;
    }

    /**
     * 正则表达?判断是否是手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        // Pattern p =
        // Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern.compile("^[1][3-5|7-8]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 正则表达?判断是否是email
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String stre = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(stre);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 正则表达式判断是否为汉字
     *
     * @param text
     * @return
     */
    public static boolean isChineseCharacters(String text) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(text);
        return m.matches();

    }

    /**
     * 正则表达式判断是否为数字
     */
    public final static String REG_DIGIT = "[0-9]*";

    public static boolean isNumDigit(String str) {
        return str.matches(REG_DIGIT);
    }

    public final static String REG_CHAR = "[a-zA-Z]*";

    public static boolean isChar(String str) {
        return str.matches(REG_CHAR);
    }

    /**
     * 2.判断?字符串的首字符是否为字母
     *
     * @param s
     * @return
     */
    public static boolean startIsChar(String s) {
        char c = s.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }


    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static Bitmap decodeSampledBitmapFromFileFor24(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;

//		return BitmapFactory.decodeFile(path, options); // 进一步得到目标大小的缩略图
//		return compressBySize(BitmapFactory.decodeFile(path), reqWidth, reqHeight);
        return BitmapFactory.decodeFile(path, options);

    }

    //t图片压缩
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;

//		return BitmapFactory.decodeFile(path, options); // 进一步得到目标大小的缩略图
//		return compressBySize(BitmapFactory.decodeFile(path), reqWidth, reqHeight);
        return compressBySize(BitmapFactory.decodeFile(path, options), reqWidth, reqHeight);

    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高

        BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
        options.inSampleSize = calculateSpecialInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
//		Bitmap bm = BitmapFactory.decodeResource(res, resId, options);
//		Bitmap.createScaledBitmap(bm, reqWidth, reqHeight, false);
        return BitmapFactory.decodeResource(res, resId, options); // 进一步得到目标大小的缩略图
    }


    public static Bitmap decodeBitmapFromUri(Context context, Uri uri, int reqWidth, int reqHeight) throws IOException {

        InputStream is = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(is, null, onlyBoundsOptions);
        is.close();
        onlyBoundsOptions.inSampleSize = calculateInSampleSize(onlyBoundsOptions, reqWidth, reqHeight);
        //比例压缩


        return compressBySize(BitmapFactory.decodeStream(is, null, onlyBoundsOptions), reqWidth, reqHeight);//再进行质量压缩

    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static int calculateSpecialInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private static int calculateSampleSize(BitmapFactory.Options newOpts, int reqWidth, int reqHeight) {

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = reqHeight;//这里设置高度为800f
        float ww = reqWidth;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Log.e("zb", "be" + be);
        return be;//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap compressBySize(Bitmap bitmap, int targetWidth,
                                        int targetHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 20, baos);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
                baos.toByteArray().length, opts);
        // 得到图片的宽度、高度；
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        opts.inSampleSize = calculateInSampleSize(opts, targetWidth, targetHeight);
        // 设置好缩放比例后，加载图片进内存；
        opts.inJustDecodeBounds = false;
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                baos.toByteArray(), 0, baos.toByteArray().length, opts);
        recycleBitmap(bitmap);
        return compressedBitmap;
    }

    /**
     * 回收位图对象
     *
     * @param bitmap
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc();
            bitmap = null;
        }
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    public static String getWeekOfDateCopy(Date dt) {
        String[] weekDays = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    //HH:MM
    public static String getTwoHour(String st1, String st2) {
        String[] kk = null;
        String[] jj = null;
        kk = st1.split(":");
        jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
            return "0";
        else {
            double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
            double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
            if ((y - u) > 0)
                return y - u + "";
            else
                return "0";
        }
    }

    //MD5加密
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static final Bitmap grey(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap faceIconGreyBitmap = Bitmap
                .createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return faceIconGreyBitmap;
    }



    /**
     * 保存到sdcard
     */
    public static boolean savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

//    /**
//     * 截图listview
//     */
//    public static Bitmap getbBitmap(RecyclerView listView) {
//        int h = 0;
//        Bitmap bitmap = null;
//        // 获取listView实际高度
//        for (int i = 0; i < listView.getChildCount(); i++) {
//            h += listView.getChildAt(i).getHeight();
//            //			listView.getChildAt(i).setBackgroundResource(R.drawable.w_bg_duoyun_night);
//        }
//        Log.d(TAG, "实际高度:" + h);
//        Log.d(TAG, "list 高度:" + listView.getHeight());
//        // 创建对应大小的bitmap
//        bitmap = Bitmap.createBitmap(listView.getWidth(), h,
//                Bitmap.Config.ARGB_8888);
//        final Canvas canvas = new Canvas(bitmap);
//        listView.draw(canvas);
//        // 测试输出
//        savePic(bitmap, "/sdcard/screen_test.png");
//        return bitmap;
//    }

    /**
     * 获取指定Activity的截屏，保存到png文件
     */
    public static Bitmap takeScreenShot(Activity activity) {

        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
//        Rect frame = new Rect();
//        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//        int statusBarHeight = frame.top;
        int statusBarHeight = getStatusHeight(activity);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();

        //二维码图片
        Bitmap erweima = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_erweima);
        erweima = Bitmap.createScaledBitmap(erweima, width, width / 5, true);

        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height            //--------绘制底图 去除titlebar
                - statusBarHeight);
        view.destroyDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(width, height - statusBarHeight + width / 5, Bitmap.Config.ARGB_8888);        //创建行位图
        final Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#FF0000"));
        Rect rect = new Rect(0, 0, width, height - statusBarHeight + width / 5);
        canvas.drawRect(rect, bgPaint);

        int h = 0;
        canvas.drawBitmap(b, 0, h, paint);
        h += height - statusBarHeight;
        canvas.drawBitmap(erweima, 0, h, paint);

//        savePic(bitmap, "/sdcard/screen_test.png");

        return bitmap;
    }

    /**
     * 截图scrollerview
     */
    public static Bitmap shotScrollView(ScrollView scrollView, View titleView, Context context) {

        BaseApplication.screenWidth = ((Activity) context).getWindowManager().getDefaultDisplay()
                .getWidth();
        BaseApplication.screenHeight = ((Activity) context).getWindowManager().getDefaultDisplay()
                .getHeight();

        int h = 0;
        titleView.setDrawingCacheEnabled(true);
        titleView.buildDrawingCache();
        int titleViewWidth = titleView.getWidth();
        Bitmap head = titleView.getDrawingCache();
        List<Bitmap> bmps = new ArrayList<Bitmap>();
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            View child = scrollView.getChildAt(i);
            h += child.getHeight();
            bmps.add(getScrollerView(scrollView));
        }

        //二维码图片
        Bitmap erweima = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_erweima);
        erweima = Bitmap.createScaledBitmap(erweima, BaseApplication.screenWidth, BaseApplication.screenWidth / 5, true);
        h += BaseApplication.screenWidth / 5 + titleView.getHeight() + UIUtils.dip2px(40);

        Bitmap bitmap = Bitmap.createBitmap(BaseApplication.screenWidth, h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        int iHeight = 0;
        iHeight += UIUtils.dip2px(20);
        canvas.drawBitmap(head, BaseApplication.screenWidth / 2 - titleViewWidth / 2, iHeight, paint);
        iHeight += titleView.getHeight() + UIUtils.dip2px(20);
        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            canvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();
//            bmp.recycle();
            bmp = null;
        }
        canvas.drawBitmap(erweima, 0, iHeight, paint);
        titleView.destroyDrawingCache();
        return bitmap;
    }

    private static Bitmap getScrollerView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        bitmap = Bitmap.createBitmap(BaseApplication.screenWidth, h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

//    /**
//     * 截屏listview
//     * @param context
//     * @param rv
//     * @param umbrellaView
//     * @return
//     */
//    public static Bitmap getWholeListViewItemsToBitmap(Context context, ListView rv, View umbrellaView, View bgView) {
//
//        BaseApplication.screenWidth = ((Activity) context).getWindowManager().getDefaultDisplay()
//                .getWidth();
//        BaseApplication.screenHeight = ((Activity) context).getWindowManager().getDefaultDisplay()
//                .getHeight();
//
//        //背景
//        Bitmap bgBitmap = null;
//        if (bgView != null) {
//            bgView.setDrawingCacheEnabled(true);
//            bgView.buildDrawingCache();
//            bgBitmap = bgView.getDrawingCache();
//        }
//
//        //雨伞
//        Bitmap umbrella = null;
//        if (umbrellaView != null) {
//            umbrellaView.setDrawingCacheEnabled(true);
//            umbrellaView.buildDrawingCache();
//            umbrella = umbrellaView.getDrawingCache();
//        }
//
//        HeaderViewListAdapter firstAdapter = (HeaderViewListAdapter) rv.getAdapter();
//        WeatherDetailListAdapter adapter = (WeatherDetailListAdapter) firstAdapter.getWrappedAdapter();
//        int allitemsheight = 0;
//        List<Bitmap> bmps = new ArrayList<Bitmap>();
//        for (int i = 0; i < 3; i++) {
//
//            adapter.setJudgeIsNeedLoadData(2);
//            adapter.setIsShareShot(true);
//            View childView = adapter.getView(i, null, rv);
//            adapter.setIsShareShot(false);
//
//            childView.measure(View.MeasureSpec.makeMeasureSpec(rv.getWidth(), View.MeasureSpec.EXACTLY),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
//            if (i == 2) {
//                if (childView.getMeasuredWidth() >0 && childView.getMeasuredHeight() > 0) {
//                    Bitmap bitmap = Bitmap.createBitmap(childView.getMeasuredWidth(), childView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    childView.draw(canvas);
//                    bmps.add(bitmap);
//                }
//            } else {
//                childView.setDrawingCacheEnabled(true);
//                childView.buildDrawingCache();
//                bmps.add(childView.getDrawingCache());
//            }
//            allitemsheight += childView.getMeasuredHeight();
//        }
//        //二维码图片
//        Bitmap erweima = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_erweima);
//        erweima = Bitmap.createScaledBitmap(erweima, BaseApplication.screenWidth, BaseApplication.screenWidth / 5, true);
//        allitemsheight += erweima.getHeight();
//
//        final Bitmap bigbitmap = Bitmap.createBitmap(BaseApplication.screenWidth, allitemsheight, Bitmap.Config.ARGB_8888);
//        Canvas bigcanvas = new Canvas(bigbitmap);
//        Paint paint = new Paint();
//        Paint bgPaint = new Paint();
////        bgPaint.setColor(Color.parseColor("#1080c5"));
////        paint.setAntiAlias(true);
////        Rect rectd = new Rect(0, 0, BaseApplication.screenWidth, allitemsheight);
////        bigcanvas.drawRect(rectd, bgPaint);
//        if (bgBitmap != null) {
//            bigcanvas.drawBitmap(bgBitmap, 0, allitemsheight / 2 - bgBitmap.getHeight() / 2, bgPaint);
//        }
//
//        int iHeight = 0;
//
//        for (int i = 0; i < bmps.size(); i++) {
//            Bitmap bmp = bmps.get(i);
//            if(bmp == null)
//                continue;
//            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
//            iHeight += bmp.getHeight();
//            bmp.recycle();
//            bmp = null;
//        }
//
//        bigcanvas.drawBitmap(erweima, 0, iHeight, paint);
//        if (umbrella != null) {
//            bigcanvas.drawBitmap(umbrella, 0, -UIUtils.dip2px(70), paint);
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                savePic(bigbitmap, "/sdcard/screen_share_test.png");
//            }
//        }).start();
//
//        if (bgView != null) {
//            bgView.destroyDrawingCache();
//        }
//
//        if (umbrellaView != null) {
//            umbrellaView.destroyDrawingCache();
//        }
//        return bigbitmap;
//    }
//
//    /**
//     * 截屏景点城市fragment的listview（非景点二级三级页面）由于15天在listview的位置有变化
//     * @param context
//     * @param rv
//     * @param umbrellaView
//     * @param bgView
//     * @return
//     */
//    public static Bitmap getListViewItemsToBitmap(Context context, ListView rv, View umbrellaView, View bgView) {
//
//        BaseApplication.screenWidth = ((Activity) context).getWindowManager().getDefaultDisplay()
//                .getWidth();
//        BaseApplication.screenHeight = ((Activity) context).getWindowManager().getDefaultDisplay()
//                .getHeight();
//
//        //背景
//        Bitmap bgBitmap = null;
//        if (bgView != null) {
//            bgView.setDrawingCacheEnabled(true);
//            bgView.buildDrawingCache();
//            bgBitmap = bgView.getDrawingCache();
//        }
//
//        //雨伞
//        Bitmap umbrella = null;
//        if (umbrellaView != null) {
//            umbrellaView.setDrawingCacheEnabled(true);
//            umbrellaView.buildDrawingCache();
//            umbrella = umbrellaView.getDrawingCache();
//        }
//
//        HeaderViewListAdapter firstAdapter = (HeaderViewListAdapter) rv.getAdapter();
//        WeatherDetailListAdapter adapter = (WeatherDetailListAdapter) firstAdapter.getWrappedAdapter();
//        int allitemsheight = 0;
//        List<Bitmap> bmps = new ArrayList<Bitmap>();
//        for (int i = 0; i < 4; i++) {
//
//            adapter.setJudgeIsNeedLoadData(2);
//            adapter.setIsShareShot(true);
//            View childView = adapter.getView(i, null, rv);
//            adapter.setIsShareShot(false);
//
//            childView.measure(View.MeasureSpec.makeMeasureSpec(rv.getWidth(), View.MeasureSpec.EXACTLY),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
//            if (i == 3) {
//                if (childView.getMeasuredWidth() >0 && childView.getMeasuredHeight() > 0) {
//                    Bitmap bitmap = Bitmap.createBitmap(childView.getMeasuredWidth(), childView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    childView.draw(canvas);
//                    bmps.add(bitmap);
//                }
//            } else {
//                childView.setDrawingCacheEnabled(true);
//                childView.buildDrawingCache();
//                bmps.add(childView.getDrawingCache());
//            }
//            allitemsheight += childView.getMeasuredHeight();
//        }
//        //二维码图片
//        Bitmap erweima = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_erweima);
//        erweima = Bitmap.createScaledBitmap(erweima, BaseApplication.screenWidth, BaseApplication.screenWidth / 5, true);
//        allitemsheight += erweima.getHeight();
//
//        final Bitmap bigbitmap = Bitmap.createBitmap(BaseApplication.screenWidth, allitemsheight, Bitmap.Config.ARGB_8888);
//        Canvas bigcanvas = new Canvas(bigbitmap);
//        Paint paint = new Paint();
//        Paint bgPaint = new Paint();
//        //        bgPaint.setColor(Color.parseColor("#1080c5"));
//        //        paint.setAntiAlias(true);
//        //        Rect rectd = new Rect(0, 0, BaseApplication.screenWidth, allitemsheight);
//        //        bigcanvas.drawRect(rectd, bgPaint);
//        if (bgBitmap != null) {
//            bigcanvas.drawBitmap(bgBitmap, 0, allitemsheight / 2 - bgBitmap.getHeight() / 2, bgPaint);
//        }
//
//        int iHeight = 0;
//
//        for (int i = 0; i < bmps.size(); i++) {
//            Bitmap bmp = bmps.get(i);
//            if(bmp == null)
//                continue;
//            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
//            iHeight += bmp.getHeight();
//            bmp.recycle();
//            bmp = null;
//        }
//
//        bigcanvas.drawBitmap(erweima, 0, iHeight, paint);
//        if (umbrella != null) {
//            bigcanvas.drawBitmap(umbrella, 0, -UIUtils.dip2px(70), paint);
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                savePic(bigbitmap, "/sdcard/screen_share_test.png");
//            }
//        }).start();
//
//        if (bgView != null) {
//            bgView.destroyDrawingCache();
//        }
//
//        if (umbrellaView != null) {
//            umbrellaView.destroyDrawingCache();
//        }
//        return bigbitmap;
//    }
//
//    /**
//     * 截屏景点城市二级三级页面
//     * @param context
//     * @param rv
//     * @param umbrellaView
//     * @param bgView
//     * @return
//     */
//    public static Bitmap getLevelListViewItemsToBitmap(Context context, ListView rv, View umbrellaView, View bgView) {
//
//        BaseApplication.screenWidth = ((Activity) context).getWindowManager().getDefaultDisplay()
//                .getWidth();
//        BaseApplication.screenHeight = ((Activity) context).getWindowManager().getDefaultDisplay()
//                .getHeight();
//
//        //背景
//        Bitmap bgBitmap = null;
//        if (bgView != null) {
//            bgView.setDrawingCacheEnabled(true);
//            bgView.buildDrawingCache();
//            bgBitmap = bgView.getDrawingCache();
//        }
//
//        //雨伞
//        Bitmap umbrella = null;
//        if (umbrellaView != null) {
//            umbrellaView.setDrawingCacheEnabled(true);
//            umbrellaView.buildDrawingCache();
//            umbrella = umbrellaView.getDrawingCache();
//        }
//
//        HeaderViewListAdapter firstAdapter = (HeaderViewListAdapter) rv.getAdapter();
//        TouristLevel2Adapter adapter = (TouristLevel2Adapter) firstAdapter.getWrappedAdapter();
//        int allitemsheight = 0;
//        List<Bitmap> bmps = new ArrayList<Bitmap>();
//        for (int i = 0; i < 4; i++) {
//
//            adapter.setIsShareShot(true);
//            View childView = adapter.getView(i, null, rv);
//            adapter.setIsShareShot(false);
//
//            childView.measure(View.MeasureSpec.makeMeasureSpec(rv.getWidth(), View.MeasureSpec.EXACTLY),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
//            if (i == 3) {
//                if (childView.getMeasuredWidth() >0 && childView.getMeasuredHeight() > 0) {
//                    Bitmap bitmap = Bitmap.createBitmap(childView.getMeasuredWidth(), childView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    childView.draw(canvas);
//                    bmps.add(bitmap);
//                }
//            } else {
//                childView.setDrawingCacheEnabled(true);
//                childView.buildDrawingCache();
//                bmps.add(childView.getDrawingCache());
//            }
//            allitemsheight += childView.getMeasuredHeight();
//        }
//        //二维码图片
//        Bitmap erweima = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_erweima);
//        erweima = Bitmap.createScaledBitmap(erweima, BaseApplication.screenWidth, BaseApplication.screenWidth / 5, true);
//        allitemsheight += erweima.getHeight();
//
//        final Bitmap bigbitmap = Bitmap.createBitmap(BaseApplication.screenWidth, allitemsheight, Bitmap.Config.ARGB_8888);
//        Canvas bigcanvas = new Canvas(bigbitmap);
//        Paint paint = new Paint();
//        Paint bgPaint = new Paint();
//        //        bgPaint.setColor(Color.parseColor("#1080c5"));
//        //        paint.setAntiAlias(true);
//        //        Rect rectd = new Rect(0, 0, BaseApplication.screenWidth, allitemsheight);
//        //        bigcanvas.drawRect(rectd, bgPaint);
//        if (bgBitmap != null) {
//            bigcanvas.drawBitmap(bgBitmap, 0, allitemsheight / 2 - bgBitmap.getHeight() / 2, bgPaint);
//        }
//
//        int iHeight = 0;
//
//        for (int i = 0; i < bmps.size(); i++) {
//            Bitmap bmp = bmps.get(i);
//            if(bmp == null)
//                continue;
//            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
//            iHeight += bmp.getHeight();
//            bmp.recycle();
//            bmp = null;
//        }
//
//        bigcanvas.drawBitmap(erweima, 0, iHeight, paint);
//        if (umbrella != null) {
//            bigcanvas.drawBitmap(umbrella, 0, -UIUtils.dip2px(70), paint);
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                savePic(bigbitmap, "/sdcard/screen_share_test.png");
//            }
//        }).start();
//
//        if (bgView != null) {
//            bgView.destroyDrawingCache();
//        }
//
//        if (umbrellaView != null) {
//            umbrellaView.destroyDrawingCache();
//        }
//        return bigbitmap;
//    }

    public static int getCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

//    public static final String[] contellationArr = {BaseApplication.getContext().getString(R.string.aries), BaseApplication.getContext().getString(R.string.taurus), BaseApplication.getContext().getString(R.string.gemini),
//            BaseApplication.getContext().getString(R.string.cancer), BaseApplication.getContext().getString(R.string.leo), BaseApplication.getContext().getString(R.string.virgo),
//            BaseApplication.getContext().getString(R.string.libra), BaseApplication.getContext().getString(R.string.scorpio), BaseApplication.getContext().getString(R.string.sagittarius),
//            BaseApplication.getContext().getString(R.string.capricorn), BaseApplication.getContext().getString(R.string.aquarius), BaseApplication.getContext().getString(R.string.pisces)};

//    public static String getConstellation(int month, int day){
//        int point = -1;
//        int date = month * 100 + day * 1;
//        if (321 <= date && 419 >= date) {
//            point = 0;
//        } else if (420 <= date && 520 >= date) {
//            point = 1;
//        } else if (521 <= date && 621 >= date) {
//            point = 2;
//        } else if (622 <= date && 722 >= date) {
//            point = 3;
//        } else if (723 <= date && 822 >= date) {
//            point = 4;
//        } else if (823 <= date && 922 >= date) {
//            point = 5;
//        } else if (923 <= date && 1023 >= date) {
//            point = 6;
//        } else if (1024 <= date && 1122 >= date) {
//            point = 7;
//        } else if (1123 <= date && 1221 >= date) {
//            point = 8;
//        } else if (1222 <= date && 1231 >= date) {
//            point = 9;
//        } else if (101 <= date && 119 >= date) {
//            point = 9;
//        } else if (120 <= date && 218 >= date) {
//            point = 10;
//        } else if (219 <= date && 320 >= date) {
//            point = 11;
//        }
//        if(point == -1) {
//            return contellationArr[0];
//        }
//        return contellationArr[point];
//    }

//    public static final String[] umbrellaTips = {BaseApplication.getContext().getString(R.string.tips1), BaseApplication.getContext().getString(R.string.tips2), BaseApplication.getContext().getString(R.string.tips3),
//            BaseApplication.getContext().getString(R.string.tips4), BaseApplication.getContext().getString(R.string.tips5), BaseApplication.getContext().getString(R.string.tips6),};

    /**
     * 判断是否是小米系统
     *
     * @param context
     * @return
     */

//    public static boolean isMIUI(Context context) {// 获取缓存状态
//
//        String isMIUIStr = com.songheng.common.utils.cache.CacheUtils.getString(context, "isMIUI", "");
//        if (!TextUtils.isEmpty(isMIUIStr)) {
//            return isMIUIStr.equals("true");
//        }
//
//        Properties prop = new Properties();
//        boolean isMIUI;
//        try {
//            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        isMIUI = prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
//                || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
//                || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
//        com.songheng.common.utils.cache.CacheUtils.putString(context, "isMIUI", isMIUI + "");
//        return isMIUI;
//    }
//
//    //读取缓存的联盟广告
//    public static List<DSPAdBean.DataBean> getUnionAdFronLocal(Context context) {
//        try {
//            List<DSPAdBean.DataBean> unionBean = (List<DSPAdBean.DataBean>) FileUtils.getObjectFromCache(context, Constants.UNION_AD_SAVE_PATH, Constants.UNION_AD_SAVE_NAME);
//            return unionBean;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    //联盟广告缓存
//    public static void saveUnionAD(List<DSPAdBean.DataBean> unionAdBeans) {
////        if (unionAdBean == null)
////            return;
////        List<DSPAdBean.DataBean> unionAdBeans = unionAdBean.getData();
//        if (unionAdBeans == null || unionAdBeans.size() <= 0)
//            return;
//        FileUtils.saveObjectToCache(BaseApplication.getContext(), Constants.UNION_AD_SAVE_PATH, Constants.UNION_AD_SAVE_NAME, unionAdBeans);
//    }

    /**
     * 获取省份的拼音
     * @param province
     * @return
     */
    public static String getProvinceSpeiiing(String province) {
        String spelling = "";
        if ("上海".equals(province)) {
            spelling =  "shanghai";
        } else if ("云南".equals(province)) {
            spelling = "yunnan";
        } else if ("内蒙古".equals(province)) {
            spelling = "neimenggu";
        } else if ("北京".equals(province)) {
            spelling = "beijing";
        } else if ("台湾".equals(province)) {
            spelling = "taiwan";
        }else if ("吉林".equals(province)) {
            spelling = "jilin";
        }else if ("四川".equals(province)) {
            spelling = "sichuan";
        }else if ("天津".equals(province)) {
            spelling = "tianjin";
        }else if ("宁夏".equals(province)) {
            spelling = "ningxia";
        }else if ("安徽".equals(province)) {
            spelling = "anhui";
        }else if ("山东".equals(province)) {
            spelling = "shandong";
        }else if ("山西".equals(province)) {
            spelling = "shanxi";
        }else if ("广东".equals(province)) {
            spelling = "guangdong";
        }else if ("广西".equals(province)) {
            spelling = "guangxi";
        }else if ("新疆".equals(province)) {
            spelling = "xinjiang";
        }else if ("江苏".equals(province)) {
            spelling = "jiangsu";
        }else if ("江西".equals(province)) {
            spelling = "jiangxi";
        }else if ("河北".equals(province)) {
            spelling = "hebei";
        }else if ("河南".equals(province)) {
            spelling = "henan";
        }else if ("浙江".equals(province)) {
            spelling = "zhejiang";
        }else if ("海南".equals(province)) {
            spelling = "hainan";
        }else if ("湖北".equals(province)) {
            spelling = "hubei";
        }else if ("湖南".equals(province)) {
            spelling = "hunan";
        }else if ("澳门".equals(province)) {
            spelling = "aomen";
        }else if ("甘肃".equals(province)) {
            spelling = "gansu";
        }else if ("福建".equals(province)) {
            spelling = "fujian";
        }else if ("西藏".equals(province)) {
            spelling = "xicang";
        }else if ("贵州".equals(province)) {
            spelling = "guizhou";
        }else if ("辽宁".equals(province)) {
            spelling = "liaoning";
        }else if ("重庆".equals(province)) {
            spelling = "zhongqing";
        }else if ("陕西".equals(province)) {
            spelling = "shanxi";
        }else if ("青海".equals(province)) {
            spelling = "qinghai";
        }else if ("香港".equals(province)) {
            spelling = "xianggang";
        }else if ("黑龙江".equals(province)) {
            spelling = "heilongjiang";
        }
        return spelling;
    }

    private static String mUserAgent;

    //必须要在主线程中调用
    public static String getUserAgent(Context context) {
        if (context == null) {
            return "null";
        }
        if (mUserAgent == null || mUserAgent.equals("")) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mUserAgent = WebSettings.getDefaultUserAgent(UIUtils.getContext());
                } else {
                    try {
                        final Class<?> webSettingsClassicClass = Class.forName("android.webkit.WebSettingsClassic");
                        final Constructor<?> constructor = webSettingsClassicClass.getDeclaredConstructor(Context.class, Class.forName("android.webkit.WebViewClassic"));
                        constructor.setAccessible(true);
                        final Method method = webSettingsClassicClass.getMethod("getUserAgentString");
                        mUserAgent = (String) method.invoke(constructor.newInstance(UIUtils.getContext(), null));
                    } catch (final Exception e) {
                        e.printStackTrace();
                        WebView webView = new WebView(context);
                        WebSettings settings = webView.getSettings();
                        if (settings != null) {
                            mUserAgent = settings.getUserAgentString();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return mUserAgent;
        }

        return mUserAgent;
    }

    /**
     * 关键字高亮变色
     *
     * @param color 变化的色值
     * @param text 文字
     * @param keyword 文字中的关键字
     * @return
     */
    public static SpannableString matcherSearchTitle(int color, String text, String keyword) {
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    /**
     * 判断app是否已安装
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppAvilible(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        //获取手机系统的所有APP包名，然后进行一一比较
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
//
//    /**
//     * 下载apk
//     * @param activity
//     * @param mDownloadUrl
//     * @param pb
//     */
//    public static void downloadApk(final Activity activity, String mDownloadUrl, final ProgressBar pb) {
//        if(BaseApplication.isDownloading){
//            Toast.makeText(activity, "正在下载，不要重复点击啦", Toast.LENGTH_SHORT).show();
//
//            return;
//        }else{
//            BaseApplication.isDownloading= true;
//            Toast.makeText(activity, "开始下载啦", Toast.LENGTH_SHORT).show();
//
//        }
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            // XUtils github.com
//            // 文件下载成功后本地的文件路径
//            String target = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+BaseApplication.getContext().getResources().getString(R.string.app_nickname)+ ".apk";
//            //
//            // 开始下载文件
//            HttpUtils utils = new HttpUtils();
//            utils.configRequestThreadPoolSize(3);
//
//            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
//
//                /**
//                 * 下载成功 运行在主线程
//                 */
//                @Override
//                public void onSuccess(ResponseInfo<File> responseInfo) {
//                    System.out.println("下载成功!!!");
//                    BaseApplication.isDownloading= false;
//                    File result = responseInfo.result;// 下载成功后的文件对象
//                    toInstall(activity,result);
//                }
//
//                /**
//                 * 下载进度的回调方法 total: 文件总大小 current: 当前下载的大小 isUploading: 是否正在上传,
//                 * 下载时返回false
//                 *
//                 * 运行在主线程
//                 */
//                @Override
//                public void onLoading(long total, long current, boolean isUploading) {
//                    super.onLoading(total, current, isUploading);
//                    int percent = (int) (current * 100 / total);// 计算百分比
//                    //pb.setProgress(percent);
//                }
//
//                /**
//                 * 下载失败
//                 *
//                 * 运行在主线程
//                 */
//                @Override
//                public void onFailure(HttpException error, String msg) {
//                    System.out.println("下载失败!!!");
//                    error.printStackTrace();
//                }
//            });
//        } else {
//        }
//    }

    private static void toInstall(Activity activity, File result) {

        // 安装apk, 跳转到系统的安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
        activity.startActivityForResult(intent, 0);
    }

}
