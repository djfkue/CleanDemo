package com.argonmobile.cleandemo.scanengine;

import com.argonmobile.cleandemo.data.WJAppCacheScanResult;

import java.util.ArrayList;

/**
 * Created by yanni on 15/4/21.
 */
public interface AppCacheScanTaskCallback {

    public void onScanResult(ArrayList<WJAppCacheScanResult> scanResult);
    public void onCleanResult();
}
