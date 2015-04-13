package com.argonmobile.cleandemo.present;

import android.content.Context;

import com.argonmobile.cleandemo.model.StorageScanModel;
import com.argonmobile.cleandemo.view.IJunkView;

/**
 * Created by argon on 4/13/15.
 */
public class JunkPresent {

    private IJunkView mJunkView;
    private StorageScanModel mStorageScanModel;

    public JunkPresent(Context context) {
        mStorageScanModel = StorageScanModel.getInstance(context);
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
}
