package com.argonmobile.cleandemo.view;

import com.argonmobile.cleandemo.data.WJAppCacheScanResult;
import com.argonmobile.cleandemo.data.WJPackageInfo;

import java.util.ArrayList;

/**
 * Created by argon on 4/13/15.
 */
public interface IJunkView {
    public void startCleaning();
    public void finishCleaning();

    public void startSystemCacheScanning();
    public void stopSystemCacheScanning();

    public void showTotalJunk(long junkSize);
    public void updateMemoryJunk(long junkSize);
    public void updateStorageJunk(WJPackageInfo packageInfo);

    public void updateSystemCacheListView(ArrayList<WJPackageInfo> cacheList);

    public void startAppCacheSanning();
    public void stopAppCacheSanning();
    public void updateApplicationCacheListView(ArrayList<WJAppCacheScanResult> appCacheList);
}
