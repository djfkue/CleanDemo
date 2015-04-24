package com.argonmobile.cleandemo.scanengine;

import android.content.Context;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.util.Log;


import com.argonmobile.cleandemo.dao.DefaultCache;
import com.argonmobile.cleandemo.dao.DefaultCacheDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanni on 15/4/15.
 */
public class DefaultCacheCleanTask extends CleanTask {

    private static final String TAG = DefaultCacheCleanTask.class.getSimpleName();
    private static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Context mContext;
    private DefaultCacheDAO mDefaultCacheDAO;
    private TaskCallback mCallback;

    private List<DefaultCache> mDefaultCaches = new ArrayList<DefaultCache>();

    private HashMap<String, Long> mCounter = new HashMap<String, Long>();

    public DefaultCacheCleanTask(Context context, TaskCallback callback) {
        super();
        mContext = context;
        mCallback = callback;
        mDefaultCacheDAO = new DefaultCacheDAO(mContext);
    }

    @Override
    protected Integer doInBackground(Integer... params) {

        if (params == null) {
            throw new AndroidRuntimeException("params must not be null");
        }

        if (params[0] == TASK_SCAN) {
            mDefaultCaches.addAll(mDefaultCacheDAO.queryDefaultCache());
            /*for (Cache cache : mAppCaches) {
                cache.dumpInfo();
            }*/

            scanCache();
        } else if (params[0] == TASK_CLEAN) {
            for (DefaultCache cache : mDefaultCaches) {
                cleanCache(cache);
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
        return subDir.substring(1);
    }

    public void scanCache() {
        for (DefaultCache defaultCache : mDefaultCaches) {
            String path = SDCARD_PATH + defaultCache.getDir();
            File rootDir = new File(path);
            if (rootDir.exists() && rootDir.isDirectory()) {
                String[] subDirs = rootDir.list();

                if (defaultCache.isRegular()) {
                    for (String subDir : subDirs) {
                        String regular = getRegular(defaultCache.getSubDir());
                        Pattern pattern = Pattern.compile(regular);
                        Matcher matcher = pattern.matcher(subDir);
                        if (matcher.find()) {
                            File fSubDir = new File(path + "/" + subDir);
                            if (fSubDir.isDirectory()) {
                                String[] dSubDirs = fSubDir.list();
                                for (String subSubDir : dSubDirs) {
                                    pattern = Pattern.compile(defaultCache.getWildcards());
                                    matcher = pattern.matcher(subSubDir.toLowerCase());
                                    if (matcher.find()) {
                                        String target = path + "/" + subDir + "/" + subSubDir;
                                        defaultCache.addDirPath(target);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (String dir : subDirs) {
                        Pattern pattern = Pattern.compile(defaultCache.getWildcards());
                        Matcher matcher = pattern.matcher(dir.toLowerCase());
                        if (matcher.find()) {
                            String target = path + dir;
                            defaultCache.addDirPath(target);
                        }
                    }
                }
                cacheSizeCounter(defaultCache);
            }
        }
    }

    public void cleanCache(DefaultCache cache) {
        for (String path : cache.getTargetDir()) {
            // 计算目标目录大小
            File cleanDirectory = new File(path);
            // 按策略删除数据
            if (cache.isRemoveDir()) {
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

    public void cacheSizeCounter(DefaultCache appCache) {
        List<String> dirs = appCache.getTargetDir();
        for (String path : dirs) {
            // 计算目标目录大小
            File cleanDirectory = new File(path);
            long total = 0;
            long current = 0;
            String key = path.substring(0, path.lastIndexOf("/"));
            if (mCounter.containsKey(key)) {
                total = mCounter.get(key);
            }
            try {
                current = getFileSize(cleanDirectory);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (current > 0) {
                total += current;
                Log.d(TAG, "cacheSizeCounter: path=" + path + ", current=" +
                        current + ", total=" + total);
                mCounter.put(key, total);
            }
        }
    }

    private long getFileSize(File f) throws Exception {
        long size = 0;
        if (f.isDirectory()) {
            File flist[] = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size += getFileSize(flist[i]);
                } else {
                    size += flist[i].length();
                }
            }
        } else {
            size = size + f.length();
        }
        return size;
    }
}
