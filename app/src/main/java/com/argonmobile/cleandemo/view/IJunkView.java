package com.argonmobile.cleandemo.view;

/**
 * Created by argon on 4/13/15.
 */
public interface IJunkView {
    public void startCleaning();
    public void finishCleaning();

    public void startScanning();
    public void stopScanning();

    public void showTotalJunk();
    public void updateMemoryJunk();
    public void updateStorageJunk(long junkSize);
}
