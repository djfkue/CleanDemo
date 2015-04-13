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
    private static StorageScanModel mInstance;

    private Context mContext;

    private ArrayList<StorageScanObserver> mStorageScanObservers = new ArrayList<>();

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

        }
    };

    private void notifyCacheScanStart() {
        Log.d(TAG, "notifyCacheScanStart");
        for (StorageScanObserver observer : mStorageScanObservers) {
            observer.onScanStart();
        }
    }

    private void notifyCacheScanEnd() {
        Log.d(TAG, "notifyCacheScanEnd");
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

    public void startScan() {
        mCacheScanTask = new CacheScanTask(mContext);
        mCacheScanTask.setOnScanListener(mOnScanListener);
        mCacheScanTask.execute();
    }

    public interface StorageScanObserver {
        public void onScanStart();
        public void onScanEnd();
    }
}
