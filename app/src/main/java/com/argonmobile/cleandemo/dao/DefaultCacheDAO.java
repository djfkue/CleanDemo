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
public class DefaultCacheDAO {

    private Context mContext;
    private SQLiteDatabase mDatabase = null;

    public DefaultCacheDAO(Context context) {
        mContext = context;
        mDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
        generateDefaultData();
    }

    public List<DefaultCache> queryDefaultCache() {
        List<DefaultCache> defaultCacheList = new ArrayList<DefaultCache>();

        String sql = "SELECT * FROM " + SQL.TABLE_DEFAULT_CACHE;
        Cursor cursor = mDatabase.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            DefaultCache defaultCache = new DefaultCache();
            defaultCache.setItemName(cursor.getString(1));
            defaultCache.setDir(cursor.getString(2));
            defaultCache.setSubDir(cursor.getString(3));
            defaultCache.setWildcards(cursor.getString(4));
            if (cursor.getInt(5) == 1) {
                defaultCache.flagRemoveDir();
            }
            if (cursor.getInt(6) == 0) {
                defaultCache.flagNoRegular();
            }
            defaultCacheList.add(defaultCache);
        }
        cursor.close();
        return defaultCacheList;
    }

    public long insert(DefaultCache appCache) {
        ContentValues cv = new ContentValues();
        cv.put(SQL.DEFAULT_CACHE_ITEM_NAME, appCache.getItemName());
        cv.put(SQL.DEFAULT_CACHE_DIR, appCache.getDir());
        cv.put(SQL.DEFAULT_CACHE_SUB_DIR, appCache.getSubDir());
        cv.put(SQL.DEFAULT_CACHE_WILDCARDS, appCache.getWildcards());
        cv.put(SQL.DEFAULT_CACHE_REMOVE_DIR, appCache.isRemoveDir());
        cv.put(SQL.DEFAULT_CACHE_REGULAR, appCache.isRegular());

        long rowId = mDatabase.insert(SQL.TABLE_DEFAULT_CACHE, null, cv);
        return rowId;
    }

    private void generateDefaultData() {
        String sql = "SELECT COUNT(1) FROM " + SQL.TABLE_DEFAULT_CACHE;
        Cursor cursor = mDatabase.rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        if (count == 0) {
            DefaultCache appCache = new DefaultCache();
            appCache.setItemName("App_Log");
            appCache.setDir("/");
            appCache.setSubDir("/");
            appCache.flagNoRegular();
            appCache.setWildcards("([/s/S]*)log([/s/S]*)");
            insert(appCache);

            appCache = new DefaultCache();
            appCache.setItemName("App_Cache");
            appCache.setDir("/");
            appCache.setSubDir("/");
            appCache.flagNoRegular();
            appCache.setWildcards("([/s/S]*)cache([/s/S]*)");
            insert(appCache);

            appCache = new DefaultCache();
            appCache.setItemName("App_Log");
            appCache.setDir("/Android");
            appCache.setSubDir("/");
            appCache.flagNoRegular();
            appCache.setWildcards("([/s/S]*)log([/s/S]*)");
            insert(appCache);

            appCache = new DefaultCache();
            appCache.setItemName("App_Cache");
            appCache.setDir("/Android");
            appCache.setSubDir("/");
            appCache.flagNoRegular();
            appCache.setWildcards("([/s/S]*)cache([/s/S]*)");
            insert(appCache);

            appCache = new DefaultCache();
            appCache.setItemName("App_Log");
            appCache.setDir("/Android/data");
            appCache.setSubDir("/([/s/S]*).([/s/S]*)");
            appCache.setWildcards("([/s/S]*)log([/s/S]*)");
            insert(appCache);

            appCache = new DefaultCache();
            appCache.setItemName("App_Cache");
            appCache.setDir("/Android/data");
            appCache.setSubDir("/([/s/S]*).([/s/S]*)");
            appCache.setWildcards("([/s/S]*)cache([/s/S]*)");
            insert(appCache);
        }
    }
}