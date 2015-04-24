package com.argonmobile.cleandemo.scanengine;

import java.util.HashMap;

/**
 * Created by yanni on 15/4/21.
 */
public interface TaskCallback {

    public void onScanResult(HashMap<String, Long> scanResult);
    public void onCleanResult();
}
