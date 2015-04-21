package com.argonmobile.cleandemo.model.memory;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sean on 4/21/15.
 */
public class MemoryModel {
    // async messages
    public final static int MSG_ON_GET_STARTED = 0;
    public final static int MSG_ON_GET_APP = 1;
    public final static int MSG_ON_GET_FINISHED = 2;

    // keys
    public static final String PKG_NAME = "pkgName";
    public static final String APP_NAME = "appName";
    public static final String APP_ICON = "appIcon";
    public static final String APP_UID = "appUid";
    public static final String APP_TOTAL_PSS = "appTotalPss";
    public static final String APP_PROCS = "appProcs";
    public static final String APP_PROC_MEM = "appProcMem";
    public static final String APP_RECOMMEND_CLEAN = "appRecommendClean";

    // Singleton module
    public final static String TAG = "MemoryModel";
    public final static MemoryModel defaultModel = new MemoryModel();
    private MemoryModel() {};

    private ActivityManager activityManager;
    private PackageManager packageManager;
    private Context ctx;

    private IAppFilter systemAppFilter;
    private IAppFilter notRecommendedFilter;
    private GetRunningAppsThread getRunningAppThread;

    /**
     * Memory model must be initialized before using
     * @param ctx
     */
    public void init(Context ctx) {
        if(ctx == null) throw new IllegalArgumentException("Context be null!");
        activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = ctx.getPackageManager();
    }

    /**
     * Set not recommend app filter
     * @param notRecommendedFilter
     */
    public void setSystemAppFilter(IAppFilter notRecommendedFilter) {
        this.notRecommendedFilter = notRecommendedFilter;
    }

    /**
     * Set system app filter
     * @param systemAppFilter
     */
    public void setNotRecommendedFilter(IAppFilter systemAppFilter) {
        this.systemAppFilter = systemAppFilter;
    }

    /**
     * Get running apps asynchronize
     * @param handler
     */
    public void getRunningAppsAsync(Handler handler) {
        if(handler == null) throw new IllegalArgumentException("Handler be null!");
        if(ctx == null) throw new IllegalStateException("Model must be initialized before using! @see init(Context ctx)");
        if(getRunningAppThread != null) {
            getRunningAppThread.requestStop();
            getRunningAppThread = null;
        }
        getRunningAppThread = new GetRunningAppsThread(handler);
        getRunningAppThread.start();
    }

    private final class GetRunningAppsThread extends Thread {
        private Handler mHandler;
        public GetRunningAppsThread(Handler handler) {
            mHandler = handler;
        }
        public void requestStop() {
            synchronized (this) {
                mHandler = null;
            }
        }
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            synchronized (this) {
                if(mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_ON_GET_STARTED);
                } else {
                    return;
                }
            }

            // get all running app processes
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            if(runningAppProcesses == null) {
                synchronized (this) {
                    if(mHandler != null) {
                        mHandler.sendEmptyMessage(MSG_ON_GET_FINISHED);
                    } else {
                        return;
                    }
                }
            }

            // init filters
            final IAppFilter sysAppFilter = (systemAppFilter == null) ?
                    SystemAppFilter.defaultFilter : systemAppFilter;
            final IAppFilter notRecommended = (notRecommendedFilter == null) ?
                    NotRecommendedFilter.defaultFilter : notRecommendedFilter;

            // sort all running app processes
            Collections.sort(runningAppProcesses, new Comparator<ActivityManager.RunningAppProcessInfo>() {
                @Override
                public int compare(ActivityManager.RunningAppProcessInfo lhs, ActivityManager.RunningAppProcessInfo rhs) {
                    String lhsPkgName = lhs.processName.split(":")[0];
                    String rhsPkgName = rhs.processName.split(":")[0];
                    return lhsPkgName.compareToIgnoreCase(rhsPkgName);
                }
            });

            // add current package name
            sysAppFilter.addPackage(ctx.getApplicationInfo().packageName);

            // add current launcher app package
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            String currentHomePackage = resolveInfo.activityInfo.packageName;
            sysAppFilter.addPackage(currentHomePackage);

            // remove all ignored packages
            Log.i(TAG, "before remove :" + runningAppProcesses.size());
            ArrayList<ActivityManager.RunningAppProcessInfo> toBeRemoved = new ArrayList<ActivityManager.RunningAppProcessInfo>();
            for(ActivityManager.RunningAppProcessInfo runningProcessInfo : runningAppProcesses) {
                Log.i(TAG, "after sort:" + runningProcessInfo.processName);
                try {
                    String appPkgname = runningProcessInfo.processName.split(":")[0];
                    if (sysAppFilter.isFilter(appPkgname)) {
                        toBeRemoved.add(runningProcessInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.w(TAG, "exception raised while remove ignore list:" + e.toString());
                }
            }
            runningAppProcesses.removeAll(toBeRemoved);
            Log.i(TAG, "removed :" + toBeRemoved.size() + " ignored packages, after remove size:" + runningAppProcesses.size());

            Map<String, Map<String, Object>> runningApps = new HashMap<String, Map<String, Object>>();
            // iterate the running app process list
            for(ActivityManager.RunningAppProcessInfo runningProcessInfo : runningAppProcesses) {
                synchronized (this) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(MSG_ON_GET_FINISHED);
                    } else {
                        return;
                    }
                }
                String[] pkgNameSections = runningProcessInfo.processName.split(":");
                Log.i(TAG, "process name:" + runningProcessInfo.processName + ", pkgNameSections length:" + pkgNameSections.length);
                if(pkgNameSections.length == 0) {
                    // TODO: process name is ""?
                    continue;
                }
                String appPkgname = pkgNameSections[0];
                String subProcessName = (pkgNameSections.length == 2) ? pkgNameSections[1] : null;
                Log.i(TAG, "appPkgname:" + appPkgname + ", subProcessName:" + subProcessName);

                // get application info from package name
                ApplicationInfo appInfo = getApplicationInfo(appPkgname);
                if(appInfo == null) appInfo = getApplicationInfo(runningProcessInfo.processName);

                // get app info by app package name
                Map<String, Object> appInfoMap = runningApps.get(appPkgname);
                if(appInfoMap == null) appInfoMap = new HashMap<String, Object>();

                // put app package name
                if(!appInfoMap.containsKey(PKG_NAME)) {
                    appInfoMap.put(PKG_NAME, appPkgname);
                }

                // put app uid
                if(!appInfoMap.containsKey(APP_UID)) {
                    appInfoMap.put(APP_UID, runningProcessInfo.uid);
                }

                // put recommend flag
                if(!appInfoMap.containsKey(APP_RECOMMEND_CLEAN)) {
                    appInfoMap.put(APP_RECOMMEND_CLEAN, !notRecommended.isFilter(appPkgname));
                }

                // only main process can get human readable name
                if(appInfo != null) {
                    appInfoMap.put(APP_NAME, appInfo.loadLabel(packageManager).toString());
                } else if(!appInfoMap.containsKey(APP_NAME)) {
                    appInfoMap.put(APP_NAME, appPkgname);
                }

                // get and put app icon
                if(appInfo != null) {
                    appInfoMap.put(APP_ICON, appInfo.loadIcon(packageManager));
                } else if(!appInfoMap.containsKey(APP_ICON)) {
                    appInfoMap.put(APP_ICON, android.R.drawable.sym_def_app_icon);
                }

                // get memory size of app process, store mem info <Key(pid), Value(Debug.MemoryInfo)>
                SparseArray<Debug.MemoryInfo> memoryInfoArray = (SparseArray<Debug.MemoryInfo>)appInfoMap.get(APP_PROC_MEM);
                if(memoryInfoArray == null) memoryInfoArray = new SparseArray<Debug.MemoryInfo>();
                Debug.MemoryInfo memInfo = activityManager.getProcessMemoryInfo(new int[] {runningProcessInfo.pid})[0];
                memoryInfoArray.put(runningProcessInfo.pid, memInfo);
                appInfoMap.put(APP_PROC_MEM, memoryInfoArray);

                // update total
                int totalPss = 0;
                if(appInfoMap.containsKey(APP_TOTAL_PSS)) totalPss = (Integer)appInfoMap.get(APP_TOTAL_PSS);
                totalPss += memInfo.getTotalPss();
                appInfoMap.put(APP_TOTAL_PSS, totalPss);

                // add process in process array
                List<ActivityManager.RunningAppProcessInfo> processes = (ArrayList<ActivityManager.RunningAppProcessInfo>)appInfoMap.get(APP_PROCS);
                if(processes == null) processes = new ArrayList<ActivityManager.RunningAppProcessInfo>();
                processes.add(runningProcessInfo);
                appInfoMap.put(APP_PROCS, processes);

                runningApps.put(appPkgname, appInfoMap);
                synchronized (this) {
                    if (mHandler != null) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_ON_GET_APP, sortMap(runningApps)));
                    } else {
                        return;
                    }
                }
            }
            //return sortMap(runningApps);

            synchronized (this) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_ON_GET_FINISHED);
                } else {
                    return;
                }
            }
        }

        private List<Map<String, Object>> sortMap(Map<String, Map<String, Object>> unsortedMap) {
            List<Map<String, Object>> list = new LinkedList<>(unsortedMap.values());
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
                    int lhsTotalPss = (Integer)lhs.get(APP_TOTAL_PSS);
                    int rhsTotalPss = (Integer)rhs.get(APP_TOTAL_PSS);
                    return lhsTotalPss < rhsTotalPss ? 1 : (lhsTotalPss == rhsTotalPss ? 0 : -1);
                }
            });
            return list;
        }
    }

    private ApplicationInfo getApplicationInfo(String packageName) {
        try {
            return packageManager.getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (Throwable e) {
            return null;
        }
    }
}
