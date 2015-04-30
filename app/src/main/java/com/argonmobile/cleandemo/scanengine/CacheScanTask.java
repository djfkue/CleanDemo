package com.argonmobile.cleandemo.scanengine;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.argonmobile.cleandemo.data.WJPackageInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by argon on 4/13/15.
 */
public class CacheScanTask extends AsyncTask<Void, String, Long>{

    private static final String TAG = "CacheScanTask";
    private Context mContext;

    private OnScanListener mOnScanListener;

//    HashMap<String, WJPackageInfo> mPackageInfos = new HashMap<>();
    ArrayList<WJPackageInfo> mPackageInfos = new ArrayList<>();

    private int mHandleCounter = 0;

    public CacheScanTask(Context context) {
        mContext = context;
        mHandleCounter = 0;
    }

    public void setOnScanListener(OnScanListener onScanListener) {
        mOnScanListener = onScanListener;
    }

    private void getInstalledApps(boolean getSysPackages) {

        long timeStamp = System.currentTimeMillis();
        List<android.content.pm.PackageInfo> packs = mContext.getPackageManager()
                .getInstalledPackages(0);


        for (int i = 0; i < packs.size(); i++) {
            android.content.pm.PackageInfo p = packs.get(i);
//            if ((!getSysPackages) && (p.versionName == null)) {
//                continue;
//            }

            WJPackageInfo newInfo = new WJPackageInfo();
//            newInfo.mAppName = p.applicationInfo.loadLabel(
//                    mContext.getPackageManager());
            newInfo.mAppPackageName = p.packageName;
            newInfo.mDataDir = p.applicationInfo.dataDir;
            newInfo.mVersionName = p.versionName;
            newInfo.mVersionCode = p.versionCode;
            newInfo.mApplicationInfo = p.applicationInfo;
//            newInfo.icon = p.applicationInfo.loadIcon(mContext
//                    .getPackageManager());
            mPackageInfos.add(newInfo);
        }

        long timeInterval = System.currentTimeMillis() - timeStamp;
        Log.e(TAG, "getInstalledApps used: " + timeInterval);
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "start scan...");
        mHandleCounter = 0;
        if (mOnScanListener != null) {
            mOnScanListener.onScanStart();
        }
    }

    @Override
    protected Long doInBackground(Void... params) {
        // get all installed package list first;
        getInstalledApps(true);
        PackageManager pm = mContext.getPackageManager();
        for (WJPackageInfo packageInfo : mPackageInfos) {
            Method getPackageSizeInfo;
            try {
                getPackageSizeInfo = pm.getClass().getMethod(
                        "getPackageSizeInfo", String.class,
                        IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, packageInfo.mAppPackageName,
                        new CachePackageStateObserver());
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void handleGetPackageState(PackageStats packageStats) {
        WJPackageInfo packageInfo = findPackageInfo(packageStats.packageName);
        if (packageInfo != null) {
            packageInfo.cacheSize = packageStats.cacheSize + packageStats.externalCacheSize;
            mHandleCounter ++;
        }
        if (mOnScanListener != null && packageInfo != null && packageInfo.cacheSize > 0) {
            mOnScanListener.onScanProgress(packageInfo);
        }

        //Log.e("SD_TRACE", "mHandleCounter: " + mHandleCounter + " mPackageInfos: " + mPackageInfos.size());
        if (mHandleCounter == mPackageInfos.size()) {
            mOnScanListener.onScanEnd();
        }
    }

    private WJPackageInfo findPackageInfo(String packageName) {
        //WJPackageInfo packageInfo = mPackageInfos.get(packageName);
        for (WJPackageInfo packageInfo : mPackageInfos) {
            if (packageInfo.mAppPackageName.equals(packageName)) {
                return packageInfo;
            }
        }
        return null;
    }

    public interface OnScanListener {
        public void onScanStart();
        public void onScanEnd();

        public void onScanProgress(WJPackageInfo packageInfo);
    }

    private class CachePackageStateObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            Log.d(TAG, "Package Size " + pStats.packageName + "");
//            Log.i(TAG, "Cache Size "+ pStats.cacheSize + "");
//            Log.w(TAG, "Data Size " + pStats.dataSize + "");
//            mTotalPackageCacheSize = mTotalPackageCacheSize + pStats.cacheSize + pStats.externalCacheSize;
//            Log.v(TAG, "Total Cache Size" + " " + mTotalPackageCacheSize);

            handleGetPackageState(pStats);
        }
    }
}
