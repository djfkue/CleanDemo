package com.argonmobile.cleandemo.model.applicationjunk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.argonmobile.cleandemo.data.WJAppCacheScanResult;
import com.argonmobile.cleandemo.data.WJPackageInfo;
import com.argonmobile.cleandemo.scanengine.AppCacheCleanTask;
import com.argonmobile.cleandemo.scanengine.AppCacheScanTaskCallback;
import com.argonmobile.cleandemo.scanengine.CleanTask;
import com.argonmobile.cleandemo.scanengine.DefaultCacheCleanTask;

import java.util.ArrayList;

/**
 * Created by argon on 4/24/15.
 */
public class ApplicationCacheModel {

    private static final String TAG = "ApplicationCacheModel";

    private static final int STATE_IDLE = 0x00;
    private static final int STATE_SCANING = 0x01;

    private static ApplicationCacheModel mInstance;

    private Context mContext;
    private PackageManager mPackageManager;

    private int mScanState = STATE_IDLE;

    private ArrayList<WJAppCacheScanResult> mAppCacheScanResults = new ArrayList<>();

    private ArrayList<AppScanObserver> mAppScanObservers = new ArrayList<>();

    private long mTotalCacheJunkSize;

    private boolean mAppScanEnd;
    private AppCacheScanTaskCallback mAppCacheScanTaskCallback = new AppCacheScanTaskCallback() {
        @Override
        public void onScanResult(ArrayList<WJAppCacheScanResult> scanResults) {
            synchronized (mAppCacheScanResults) {
                //mAppCacheScanResults.clear();
                for (WJAppCacheScanResult scanResult : scanResults) {
                    scanResult.mApplicationInfo = getApplicationInfo(scanResult.mPackageName);
                    mAppCacheScanResults.add(scanResult);
                }
            }
            mAppScanEnd = true;
            notifyAppCacheScanEnd();
        }

        @Override
        public void onCleanResult() {

        }
    };

    private boolean mDefaultScanEnd;
    private AppCacheScanTaskCallback mDefaultCacheScanTaskCallback = new AppCacheScanTaskCallback() {
        @Override
        public void onScanResult(ArrayList<WJAppCacheScanResult> scanResults) {
            synchronized (mAppCacheScanResults) {
                //mAppCacheScanResults.clear();
                for (WJAppCacheScanResult scanResult : scanResults) {
                    scanResult.mApplicationInfo = getApplicationInfo(scanResult.mPackageName);
                    mAppCacheScanResults.add(scanResult);
                }
            }
            mDefaultScanEnd = true;
            notifyAppCacheScanEnd();
        }

        @Override
        public void onCleanResult() {

        }
    };

    private ApplicationCacheModel(Context context) {
        mContext = context;
        mPackageManager = mContext.getPackageManager();
    }

    public static ApplicationCacheModel getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApplicationCacheModel(context);
        }
        return mInstance;
    }

    public void registerStorageScanObserver(AppScanObserver storageScanObserver) {
        if (!mAppScanObservers.contains(storageScanObserver)) {
            mAppScanObservers.add(storageScanObserver);
        }
    }

    public void unRegisterStorageScanObserver(AppScanObserver storageScanObserver) {
        if (mAppScanObservers.contains(storageScanObserver)) {
            mAppScanObservers.remove(storageScanObserver);
        }
    }

    public long getTotalCacheJunkSize() {
        return mTotalCacheJunkSize;
    }

    public ArrayList<WJAppCacheScanResult> getAppCacheScanResults() {
        return mAppCacheScanResults;
    }

    public void startScan() {
        if (mScanState == STATE_SCANING) {
            return;
        }
        mScanState = STATE_SCANING;
        synchronized (this) {
            mAppCacheScanResults.clear();
        }

        mTotalCacheJunkSize = 0;

        mAppCacheScanResults.clear();

        AppCacheCleanTask agent = new AppCacheCleanTask(mContext, mAppCacheScanTaskCallback);
        agent.execute(CleanTask.TASK_SCAN);

        mAppScanEnd = false;

        DefaultCacheCleanTask defaultCacheCleanTask = new DefaultCacheCleanTask(mContext, mDefaultCacheScanTaskCallback);
        defaultCacheCleanTask.execute(CleanTask.TASK_SCAN);

        mDefaultScanEnd = false;
        notifyAppCacheScanStart();

    }

    private ApplicationInfo getApplicationInfo(String packageName) {
        try {
            return mPackageManager.getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (Throwable e) {
            return null;
        }
    }

    private void notifyAppCacheScanStart() {
        Log.d(TAG, "notifyCacheScanStart");
        for (AppScanObserver observer : mAppScanObservers) {
            observer.onScanStart();
        }
    }

    private void notifyAppCacheScanEnd() {
        Log.d(TAG, "notifyCacheScanEnd");
        if (mAppScanEnd && mDefaultScanEnd) {
            mScanState = STATE_IDLE;
            for (AppScanObserver observer : mAppScanObservers) {
                observer.onScanEnd();
            }
        }
    }

    public interface AppScanObserver {
        public void onScanStart();
        public void onScanEnd();
        public void onScanProgress(WJPackageInfo packageInfo);
    }
}
