package com.argonmobile.cleandemo.present;

import android.content.Context;

import com.argonmobile.cleandemo.data.WJAppCacheScanResult;
import com.argonmobile.cleandemo.data.WJPackageInfo;
import com.argonmobile.cleandemo.model.applicationjunk.ApplicationCacheModel;
import com.argonmobile.cleandemo.model.systemcache.StorageScanModel;
import com.argonmobile.cleandemo.view.IJunkView;

/**
 * Created by argon on 4/13/15.
 */
public class JunkPresent {

    private IJunkView mJunkView;
    private StorageScanModel mStorageScanModel;

    private StorageScanModel.StorageScanObserver mStorageScanObserver = new StorageScanModel.StorageScanObserver() {
        @Override
        public void onScanStart() {
            notifySystemCacheScanStart();
        }

        @Override
        public void onScanEnd() {
            notifySystemCacheScanEnd();
        }

        @Override
        public void onScanProgress(WJPackageInfo packageInfo) {
            mJunkView.updateStorageJunk(packageInfo);
            mJunkView.showTotalJunk(mStorageScanModel.getTotalCacheJunkSize());
        }
    };

    private ApplicationCacheModel mAppCacheModel;

    private ApplicationCacheModel.AppScanObserver mAppScanObserver = new ApplicationCacheModel.AppScanObserver() {
        @Override
        public void onScanStart() {
            notifyAppScanStart();
        }

        @Override
        public void onScanEnd() {
            notifyAppScanEnd();
        }

        @Override
        public void onScanProgress(WJPackageInfo packageInfo) {

        }
    };

    public JunkPresent(Context context) {
        mStorageScanModel = StorageScanModel.getInstance(context);
        mStorageScanModel.registerStorageScanObserver(mStorageScanObserver);

        mAppCacheModel = ApplicationCacheModel.getInstance(context);
        mAppCacheModel.registerStorageScanObserver(mAppScanObserver);
    }

    public void bindJunkView(IJunkView junkView) {
        mJunkView = junkView;
    }

    public void startScan() {
        mStorageScanModel.startScan();
        mAppCacheModel.startScan();
    }

    public void quickClean() {
        notifyCleanStart();
    }

    private void notifyCleanStart() {
        if (mJunkView != null) {
            mJunkView.startCleaning();
        }
    }

    private void notifySystemCacheScanStart() {
        if (mJunkView != null) {
            mJunkView.startSystemCacheScanning();
        }
    }

    private void notifySystemCacheScanEnd() {
        if (mJunkView != null) {
            mJunkView.stopSystemCacheScanning();
            mJunkView.updateSystemCacheListView(mStorageScanModel.getPackageInfos());
        }
    }


    private void notifyAppScanEnd() {
        if (mJunkView != null) {
            mJunkView.stopAppCacheSanning();
            mJunkView.updateApplicationCacheListView(mAppCacheModel.getAppCacheScanResults());

            long totalJunk = 0;

            for (WJAppCacheScanResult scanResult : mAppCacheModel.getAppCacheScanResults()) {
                totalJunk += scanResult.mJunkTotalSize;
            }

            mJunkView.showTotalJunk(mStorageScanModel.getTotalCacheJunkSize() + totalJunk);
        }
    }

    private void notifyAppScanStart() {
        if (mJunkView != null) {
            mJunkView.startAppCacheSanning();
        }
    }
}
