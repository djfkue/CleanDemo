package com.argonmobile.cleandemo.scanengine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.util.Log;


import com.argonmobile.cleandemo.dao.AppCache;
import com.argonmobile.cleandemo.dao.AppCacheDAO;
import com.argonmobile.cleandemo.data.WJAppCacheScanResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanni on 15/4/15.
 */
public class AppCacheCleanTask extends CleanTask {

    private static final String TAG = AppCacheCleanTask.class.getSimpleName();
    private static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();

    private Context mContext;
    private AppCacheDAO mAppCacheDAO;
    private AppCacheScanTaskCallback mCallback;

    private List<AppCache> mAppCaches = new ArrayList<AppCache>();

    private ArrayList<WJAppCacheScanResult> mCounter = new ArrayList<>();

    public AppCacheCleanTask(Context context, AppCacheScanTaskCallback callback) {
        super();
        mContext = context;
        mCallback = callback;
        mAppCacheDAO = new AppCacheDAO(mContext);
    }

    @Override
    protected Integer doInBackground(Integer... params) {

        if (params == null) {
            throw new AndroidRuntimeException("params must not be null");
        }

        if (params[0] == TASK_SCAN) {
            List<PackageInfo> installedApp = mContext.getPackageManager().getInstalledPackages(0);
            List<String> pkgNames = new ArrayList<String>();
            for (PackageInfo packageInfo : installedApp) {
                pkgNames.add(packageInfo.packageName);
            }
            mAppCaches.addAll(mAppCacheDAO.queryCacheByPkgName(
                    pkgNames.toArray(new String[installedApp.size()])));
            /*for (Cache cache : mAppCaches) {
                cache.dumpInfo();
            }*/

            scanCache();
        } else if (params[0] == TASK_CLEAN) {
            for (AppCache appCache : mAppCaches) {
                cleanCache(appCache);
            }
        } else {
            throw new AndroidRuntimeException("Unknown task step, please check the definition");
        }
        return params[0];
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == TASK_SCAN) {
            mCallback.onScanResult(mCounter);
        } else if (result == TASK_CLEAN) {
            mCallback.onCleanResult();
        } else {
            throw new AndroidRuntimeException("Unknown task step, please check the definition");
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values != null) {

        }
    }

    public String getRegular(String subDir) {
        String regular = subDir.substring(1);
        regular = regular.substring(0, regular.indexOf("/"));
        return regular;
    }

    public void scanCache() {
        for (AppCache appCache : mAppCaches) {
            Log.d(TAG, "package=" + appCache.getPackageName());
            appCache.cleanTargetDir();
            String path = SDCARD_PATH + appCache.getDir();
            File rootDir = new File(path);
            if (rootDir.exists() && rootDir.isDirectory()) {

                String[] subDirs = rootDir.list();
                for (String dir : subDirs) {
                    String target = null;
                    if (appCache.isRegular()) {
                        String regular = getRegular(appCache.getSubDir());
                        Pattern pattern = Pattern.compile(regular);
                        Matcher matcher = pattern.matcher(dir);
                        if (matcher.find()) {
                            target = path + appCache.getSubDir().replace(regular, dir);
                            appCache.addDirPath(target);
                        }
                    } else {
                        int index = appCache.getSubDir().lastIndexOf("/");
                        if (appCache.getSubDir().substring(index + 1).equals(dir)) {
                            target = path + appCache.getSubDir();
                            appCache.addDirPath(target);
                        }
                    }
                }
                cacheSizeCounter(appCache);
            }
        }
    }

    public void cleanCache(AppCache appCache) {
        for (String path : appCache.getTargetDir()) {
            // 计算目标目录大小
            File cleanDirectory = new File(path);
            // 按策略删除数据
            if (appCache.isRemoveDir()) {
                //cleanDirectory.delete();
            } else {
                File[] files = cleanDirectory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        //file.delete();
                    }
                }
            }
        }
    }

    public void cacheSizeCounter(AppCache appCache) {

        WJAppCacheScanResult cacheScanResult = null;
        for (WJAppCacheScanResult scanResult : mCounter) {
            if (scanResult.mPackageName.equals(appCache.getPackageName())) {
                cacheScanResult = scanResult;
            }
        }

        if (cacheScanResult == null) {
            cacheScanResult = new WJAppCacheScanResult();
            cacheScanResult.mPackageName = appCache.getPackageName();
            mCounter.add(cacheScanResult);
        }

        for (String path : appCache.getTargetDir()) {
            // 计算目标目录大小
            File cleanDirectory = new File(path);
            long total = 0;
            long current = 0;

            if (cacheScanResult != null) {
                total = cacheScanResult.mJunkTotalSize;
            }

            try {
                current = getFileSize(cleanDirectory);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            total += current;
            Log.d(TAG, "cacheSizeCounter: path=" + path + ", current=" +
                    current + ", total=" + total);
            if (cacheScanResult != null) {
                cacheScanResult.mJunkTotalSize = total;
            }
        }
    }

    private long getFileSize(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }
}
