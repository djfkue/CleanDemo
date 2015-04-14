package com.argonmobile.cleandemo.view;

import com.argonmobile.cleandemo.data.WJPackageInfo;

import java.util.ArrayList;

/**
 * Created by argon on 4/13/15.
 */
public interface IJunkView {
    public void startCleaning();
    public void finishCleaning();

    public void startScanning();
    public void stopScanning();

    public void showTotalJunk(long junkSize);
    public void updateMemoryJunk(long junkSize);
    public void updateStorageJunk(WJPackageInfo packageInfo);

    public void updateCacheListView(ArrayList<WJPackageInfo> cacheList);
}
