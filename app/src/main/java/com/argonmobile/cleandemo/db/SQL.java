package com.argonmobile.cleandemo.db;

/**
 * Created by yanni on 15/4/16.
 */
public class SQL {

    public static final String PRIMARY_KEY = "_id";

    public static final String TABLE_APP_CACHE = "appcache";
    public static final String APP_CACHE_ITEM_NAME = "item_name";
    public static final String APP_CACHE_PACKAGE_NAME = "package_name";
    public static final String APP_CACHE_DIR = "dir";
    public static final String APP_CACHE_SUB_DIR = "sub_dir";
    public static final String APP_CACHE_REMOVE_DIR = "remove_dir";
    public static final String APP_CACHE_REGULAR = "regular";

    public static final String TABLE_DEFAULT_CACHE = "defaultcache";
    public static final String DEFAULT_CACHE_ITEM_NAME = "item_name";
    public static final String DEFAULT_CACHE_DIR = "dir";
    public static final String DEFAULT_CACHE_WILDCARDS = "wildcards";
    public static final String DEFAULT_CACHE_REMOVE_DIR = "remove_dir";
    public static final String DEFAULT_CACHE_REGULAR = "regular";

    public static final String CREATE_TABLE_APP_CACHE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_APP_CACHE + "(" +
                    PRIMARY_KEY + " VARCHAR PRIMARY KEY, " +
                    APP_CACHE_ITEM_NAME + " VARCHAR, " +
                    APP_CACHE_PACKAGE_NAME + " VARCHAR, " +
                    APP_CACHE_DIR + " VARCHAR, " +
                    APP_CACHE_SUB_DIR + " VARCHAR, " +
                    APP_CACHE_REMOVE_DIR + " INT, " +
                    APP_CACHE_REGULAR + " INT " +
                    ")";

    public static final String CREATE_TABLE_DEFAULT_CACHE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_DEFAULT_CACHE + "(" +
                    PRIMARY_KEY + " VARCHAR PRIMARY KEY, " +
                    DEFAULT_CACHE_ITEM_NAME + " VARCHAR, " +
                    DEFAULT_CACHE_DIR + " VARCHAR, " +
                    DEFAULT_CACHE_WILDCARDS + " VARCHAR, " +
                    DEFAULT_CACHE_REMOVE_DIR + " INT, " +
                    DEFAULT_CACHE_REGULAR + " INT " +
                    ")";

    public static final String MOCK_CACHE_DATA =
            "INSERT INTO " + TABLE_APP_CACHE + "(" + PRIMARY_KEY + "," +  APP_CACHE_ITEM_NAME + "," +
                    APP_CACHE_PACKAGE_NAME + "," + APP_CACHE_DIR + "," + APP_CACHE_SUB_DIR + "," +
                    APP_CACHE_REMOVE_DIR + "," + APP_CACHE_REGULAR + ") VALUES(null,'MicroMsg'," +
                    "'com.tencent.mm','/Tencent/MicroMsg','/[0-9a-zA-Z]{32}/avatar',1,1);";
}