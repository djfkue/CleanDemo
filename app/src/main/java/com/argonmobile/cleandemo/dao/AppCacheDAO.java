package com.argonmobile.cleandemo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.argonmobile.cleandemo.db.DBHelper;
import com.argonmobile.cleandemo.db.SQL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanni on 15/4/16.
 */
public class AppCacheDAO {

    private Context mContext;
    private SQLiteDatabase mDatabase = null;

    public AppCacheDAO(Context context) {
        mContext = context;
        mDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
        generateMockData();
    }

    public List<AppCache> queryCacheByPkgName(String... pkgName) {
        if (pkgName == null) {
            throw new IllegalArgumentException("package name should not be null");
        }
        List<AppCache> appCacheList = new ArrayList<AppCache>();

        String sql = "SELECT * FROM " + SQL.TABLE_APP_CACHE + " WHERE " +
                SQL.APP_CACHE_PACKAGE_NAME + "=?";
        if (pkgName.length > 1) {
            for (int i = 0; i < pkgName.length - 1; i++) {
                sql += " or " + SQL.APP_CACHE_PACKAGE_NAME + "=?";
            }
        }
        Cursor cursor = mDatabase.rawQuery(sql, pkgName);

        while (cursor.moveToNext()) {
            AppCache appCache = new AppCache();
            appCache.setPackageName(cursor.getString(2));
            appCache.setDir(cursor.getString(3));
            appCache.setSubDir(cursor.getString(4));
            if (cursor.getInt(5) == 1) {
                appCache.flagRemoveDir();
            }
            if (cursor.getInt(6) == 0) {
                appCache.flagNoRegular();
            }
            appCacheList.add(appCache);
        }
        cursor.close();
        return appCacheList;
    }

    public long insert(AppCache appCache) {
        ContentValues cv = new ContentValues();
        cv.put(SQL.APP_CACHE_ITEM_NAME, appCache.getItemName());
        cv.put(SQL.APP_CACHE_PACKAGE_NAME, appCache.getPackageName());
        cv.put(SQL.APP_CACHE_DIR, appCache.getDir());
        cv.put(SQL.APP_CACHE_SUB_DIR, appCache.getSubDir());
        cv.put(SQL.APP_CACHE_REMOVE_DIR, appCache.isRemoveDir());
        cv.put(SQL.APP_CACHE_REGULAR, appCache.isRegular());

        long rowId = mDatabase.insert(SQL.TABLE_APP_CACHE, null, cv);
        return rowId;
    }

    private void generateMockData() {
        String sql = "SELECT COUNT(1) FROM " + SQL.TABLE_APP_CACHE;
        Cursor cursor = mDatabase.rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        if (count == 0) {
            // 微信数据
            AppCache appCache = new AppCache();
            appCache.setItemName("MicroMsg_UserAvatar");
            appCache.setPackageName("com.tencent.mm");
            appCache.setDir("/tencent/MicroMsg");
            appCache.setSubDir("/[0-9a-zA-Z]{32}/avatar");
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("MicroMsg_Brandicon");
            appCache.setPackageName("com.tencent.mm");
            appCache.setDir("/tencent/MicroMsg");
            appCache.setSubDir("/[0-9a-zA-Z]{32}/brandicon");
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("MicroMsg_SNS");
            appCache.setPackageName("com.tencent.mm");
            appCache.setDir("/tencent/MicroMsg");
            appCache.setSubDir("/[0-9a-zA-Z]{32}/sns");
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("MicroMsg_Voice");
            appCache.setPackageName("com.tencent.mm");
            appCache.setDir("/tencent/MicroMsg");
            appCache.setSubDir("/[0-9a-zA-Z]{32}/voice2");
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("MicroMsg_ImageCache");
            appCache.setPackageName("com.tencent.mm");
            appCache.setDir("/tencent/MicroMsg");
            appCache.setSubDir("/[0-9a-zA-Z]{32}/image");
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("MicroMsg_DiskCache");
            appCache.setPackageName("com.tencent.mm");
            appCache.setDir("/tencent/MicroMsg");
            appCache.setSubDir("/diskcache");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("MicroMsg_Crash");
            appCache.setPackageName("com.tencent.mm");
            appCache.setDir("/tencent/MicroMsg");
            appCache.setSubDir("/crash");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("MicroMsg_XLog");
            appCache.setPackageName("com.tencent.mm");
            appCache.setDir("/tencent/MicroMsg");
            appCache.setSubDir("/xlog");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_AppInfos");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/.appinfos");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_Log");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/.log");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_Interest");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/.interest");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_Portrainew");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/.portrainew");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_Prenew");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/.prenew");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_Snggamemsg");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/.snggamemsg");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_Pic");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/.weibo_pic_new");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_Download");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/download");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_Page");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/page");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_SmallPage");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/small_page");
            appCache.flagNoRegular();
            insert(appCache);

            appCache = new AppCache();
            appCache.setItemName("Weibo_ThemeData");
            appCache.setPackageName("com.sina.weibo");
            appCache.setDir("/sina/weibo");
            appCache.setSubDir("/theme_data");
            appCache.flagNoRegular();
            insert(appCache);
        }
    }
}