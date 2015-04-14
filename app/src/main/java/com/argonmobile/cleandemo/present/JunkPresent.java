package com.argonmobile.cleandemo.present;

import android.content.Context;

import com.argonmobile.cleandemo.data.WJPackageInfo;
import com.argonmobile.cleandemo.model.StorageScanModel;
import com.argonmobile.cleandemo.view.IJunkView;

/**
 * Created by argon on 4/13/15.
 */
public class JunkPresent {

    private IJunkView mJunkView;
    private StorageScanModel mStorageScanModel;

    public StorageScanModel.StorageScanObserver mStorageScanObserver = new StorageScanModel.StorageScanObserver() {
        @Override
        public void onScanStart() {
            notifyScanStart();
        }

        @Override
        public void onScanEnd() {
            notifyScanEnd();
        }

        @Override
        public void onScanProgress(WJPackageInfo packageInfo) {
            mJunkView.updateStorageJunk(packageInfo);
            mJunkView.showTotalJunk(mStorageScanModel.getTotalCacheJunkSize());
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

    private void notifyScanStart() {
        if (mJunkView != null) {
            mJunkView.startScanning();
        }
    }

    private void notifyScanEnd() {
        if (mJunkView != null) {
            mJunkView.stopScanning();
            mJunkView.updateCacheListView(mStorageScanModel.getPackageInfos());
        }
    }
}
