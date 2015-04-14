package com.argonmobile.cleandemo.model;

import android.content.Context;
import android.util.Log;

import com.argonmobile.cleandemo.data.WJPackageInfo;
import com.argonmobile.cleandemo.scanengine.CacheScanTask;

import java.util.ArrayList;

/**
 * Created by argon on 4/13/15.
 */
public class StorageScanModel {

    private static final String TAG = "StorageScanModel";

    private static final int STATE_IDLE = 0x00;
    private static final int STATE_SCANING = 0x01;

    private static StorageScanModel mInstance;

    private Context mContext;

    private int mScanState = STATE_IDLE;

    private ArrayList<StorageScanObserver> mStorageScanObservers = new ArrayList<>();

    private ArrayList<WJPackageInfo> mPackageInfos = new ArrayList<>();

    private long mTotalCacheJunkSize;

    private CacheScanTask mCacheScanTask;
    private CacheScanTask.OnScanListener mOnScanListener  = new CacheScanTask.OnScanListener() {
        @Override
        public void onScanStart() {
            notifyCacheScanStart();
        }

        @Override
        public void onScanEnd() {
            notifyCacheScanEnd();
        }

        @Override
        public void onScanProgress(WJPackageInfo packageInfo) {
            handleCacheScanProgress(packageInfo);

        }
    };

    private void handleCacheScanProgress(WJPackageInfo packageInfo) {
        synchronized (StorageScanModel.this) {

            mTotalCacheJunkSize += packageInfo.cacheSize;

            if(mPackageInfos.size() == 0) {
                mPackageInfos.add(packageInfo);
            } else {
                if (mPackageInfos.get(0).cacheSize < packageInfo.cacheSize) {
                    mPackageInfos.add(0, packageInfo);
                } else if (mPackageInfos.get(mPackageInfos.size() - 1).cacheSize > packageInfo.cacheSize) {
                    mPackageInfos.add(packageInfo);
                } else {
                    for (int i = 0; i < mPackageInfos.size(); i++) {
                        WJPackageInfo wjPackageInfo = mPackageInfos.get(i);
                        if (wjPackageInfo.cacheSize < packageInfo.cacheSize) {
                            mPackageInfos.add(i, packageInfo);
                            break;
                        }
                    }
                }
            }
        }
        notifyCacheScanProgress(packageInfo);
    }

    private void notifyCacheScanProgress(WJPackageInfo packageInfo) {
        for (StorageScanObserver observer : mStorageScanObservers) {
            observer.onScanProgress(packageInfo);
        }
    }

    private void notifyCacheScanStart() {
        Log.d(TAG, "notifyCacheScanStart");
        for (StorageScanObserver observer : mStorageScanObservers) {
            observer.onScanStart();
        }
    }

    private void notifyCacheScanEnd() {
        Log.d(TAG, "notifyCacheScanEnd");
        mScanState = STATE_IDLE;
        for (StorageScanObserver observer : mStorageScanObservers) {
            observer.onScanEnd();
        }
    }

    private StorageScanModel(Context context) {
        mContext = context;
    }

    public static StorageScanModel getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StorageScanModel(context);
        }
        return mInstance;
    }

    public void registerStorageScanObserver(StorageScanObserver storageScanObserver) {
        if (!mStorageScanObservers.contains(storageScanObserver)) {
            mStorageScanObservers.add(storageScanObserver);
        }
    }

    public void unRegisterStorageScanObserver(StorageScanObserver storageScanObserver) {
        if (mStorageScanObservers.contains(storageScanObserver)) {
            mStorageScanObservers.remove(storageScanObserver);
        }
    }

    public long getTotalCacheJunkSize() {
        return mTotalCacheJunkSize;
    }

    public ArrayList<WJPackageInfo> getPackageInfos() {
        return mPackageInfos;
    }

    public void startScan() {
        if (mScanState == STATE_SCANING) {
            return;
        }
        mScanState = STATE_SCANING;
        synchronized (this) {
            mPackageInfos.clear();
        }

        mTotalCacheJunkSize = 0;
        mCacheScanTask = new CacheScanTask(mContext);
        mCacheScanTask.setOnScanListener(mOnScanListener);
        mCacheScanTask.execute();
    }

    public interface StorageScanObserver {
        public void onScanStart();
        public void onScanEnd();
        public void onScanProgress(WJPackageInfo packageInfo);
    }
}
