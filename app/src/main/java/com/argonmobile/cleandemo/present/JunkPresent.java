package com.argonmobile.cleandemo.present;

import android.content.Context;

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
    }

    public void bindJunkView(IJunkView junkView) {
        mJunkView = junkView;
    }

    public void startScan() {
        mStorageScanModel.startScan();
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

        }
    }

    private void notifyAppScanStart() {

    }
}
