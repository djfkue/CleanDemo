package com.argonmobile.cleandemo.data;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class AppDetails {
    Context mContext;
    ArrayList<PackageInfoStruct> mPackageInfoList = new ArrayList<PackageInfoStruct>();

    public AppDetails(Context context) {
        this.mContext = context;

    }

    public ArrayList<PackageInfoStruct> getPackages() {

        mPackageInfoList.clear();

        getInstalledApps(false);

        return mPackageInfoList;
    }

    private ArrayList<PackageInfoStruct> getInstalledApps(boolean getSysPackages) {

        List<PackageInfo> packs = mContext.getPackageManager()
                .getInstalledPackages(0);

        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            PackageInfoStruct newInfo = new PackageInfoStruct();
            newInfo.mAppName = p.applicationInfo.loadLabel(
                    mContext.getPackageManager()).toString();
            newInfo.mAppPackageName = p.packageName;
            newInfo.mDataDir = p.applicationInfo.dataDir;
            newInfo.mVersionName = p.versionName;
            newInfo.mVersionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(mContext
                    .getPackageManager());
            mPackageInfoList.add(newInfo);
        }
        return mPackageInfoList;
    }

    public static class PackageInfoStruct {
        String mAppName = "";
        String mAppPackageName = "";
        String mVersionName = "";
        int mVersionCode = 0;
        Drawable icon;
        String mDataDir = "";
        long cacheSize = 0;
    }
}