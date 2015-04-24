package com.argonmobile.cleandemo.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanni on 15/4/16.
 */
public class AppCache {

    private String mItemName = "";
    private String mPackageName = "";
    private String mDir = "";
    private String mSubDir = "";
    private boolean mShouldRemoveDir = false;
    private boolean mIsRegular = true;
    private List<String> mTargetDir = new ArrayList<String>();

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String name) {
        mItemName = name;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public String getDir() {
        return mDir;
    }

    public void setDir(String dir) {
        this.mDir = dir;
    }

    public String getSubDir() {
        return mSubDir;
    }

    public void setSubDir(String subDir) {
        this.mSubDir = subDir;
    }

    public void flagRemoveDir() {
        this.mShouldRemoveDir = true;
    }

    public boolean isRemoveDir() {
        return mShouldRemoveDir;
    }

    public boolean isRegular() {
        return mIsRegular;
    }

    public void flagNoRegular() {
        this.mIsRegular = false;
    }

    public List<String> getTargetDir() {
        return mTargetDir;
    }

    public void addDirPath(String path) {
        mTargetDir.add(path);
    }

    public void cleanTargetDir() {
        mTargetDir.clear();
    }

    public void removeDirPath(String path) {
        mTargetDir.remove(path);
    }
}
