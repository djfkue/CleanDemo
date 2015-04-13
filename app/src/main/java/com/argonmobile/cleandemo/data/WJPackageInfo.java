package com.argonmobile.cleandemo.data;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by argon on 4/13/15.
 */
public class WJPackageInfo {
    public CharSequence mAppName = "";
    public String mAppPackageName = "";
    public String mVersionName = "";
    public int mVersionCode = 0;
    public Drawable icon;
    public String mDataDir = "";
    public long cacheSize = 0;
    public ApplicationInfo mApplicationInfo;
}
