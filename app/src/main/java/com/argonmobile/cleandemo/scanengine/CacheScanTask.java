package com.argonmobile.cleandemo.scanengine;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.argonmobile.cleandemo.data.AppDetails;
import com.argonmobile.cleandemo.data.WJPackageInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by argon on 4/13/15.
 */
public class CacheScanTask extends AsyncTask<Void, String, Long>{

    private Context mContext;
    private AppDetails mAppDetails;

    private OnScanListener mOnScanListener;

    LinkedHashMap<String, WJPackageInfo> mPackageInfos = new LinkedHashMap<>();

    private int mHandleCounter = 0;

    public CacheScanTask(Context context) {
        mContext = context;
        mAppDetails = new AppDetails(context);
        mHandleCounter = 0;
    }

    public void setOnScanListener(OnScanListener onScanListener) {
        mOnScanListener = onScanListener;
    }

    private HashMap<String, WJPackageInfo> getInstalledApps(boolean getSysPackages) {

        List<android.content.pm.PackageInfo> packs = mContext.getPackageManager()
                .getInstalledPackages(0);

        for (int i = 0; i < packs.size(); i++) {
            android.content.pm.PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            WJPackageInfo newInfo = new WJPackageInfo();
            newInfo.mAppName = p.applicationInfo.loadLabel(
                    mContext.getPackageManager()).toString();
            newInfo.mAppPackageName = p.packageName;
            newInfo.mDataDir = p.applicationInfo.dataDir;
            newInfo.mVersionName = p.versionName;
            newInfo.mVersionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(mContext
                    .getPackageManager());
            //mPackageInfoList.add(newInfo);
            mPackageInfos.put(newInfo.mAppPackageName, newInfo);
        }

        return mPackageInfos;
    }

    @Override
    protected void onPreExecute() {
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
        for (String key : mPackageInfos.keySet()) {
            WJPackageInfo packageInfo = mPackageInfos.get(key);
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

        Log.e("SD_TRACE", "mHandleCounter: " + mHandleCounter + " mPackageInfos: " + mPackageInfos.size());
        if (mHandleCounter == mPackageInfos.size()) {
            mOnScanListener.onScanEnd();
        }
    }

    private WJPackageInfo findPackageInfo(String packageName) {
        WJPackageInfo packageInfo = mPackageInfos.get(packageName);
        return packageInfo;
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
//            Log.d(TAG, "Package Size " + pStats.packageName + "");
//            Log.i(TAG, "Cache Size "+ pStats.cacheSize + "");
//            Log.w(TAG, "Data Size " + pStats.dataSize + "");
//            mTotalPackageCacheSize = mTotalPackageCacheSize + pStats.cacheSize + pStats.externalCacheSize;
//            Log.v(TAG, "Total Cache Size" + " " + mTotalPackageCacheSize);

            handleGetPackageState(pStats);
        }
    }
}
