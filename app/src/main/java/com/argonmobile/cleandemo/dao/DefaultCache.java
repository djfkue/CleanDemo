package com.argonmobile.cleandemo.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanni on 15/4/22.
 */
public class DefaultCache {

    private String mItemName = "";
    private String mDir = "";
    private String mWildcards = "";
    private boolean mShouldRemoveDir = false;
    private boolean mIsRegular = true;
    private List<String> mTargetDir = new ArrayList<String>();

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String itemName) {
        this.mItemName = itemName;
    }

    public String getDir() {
        return mDir;
    }

    public void setDir(String dir) {
        this.mDir = dir;
    }

    public String getWildcards() {
        return mWildcards;
    }

    public void setWildcards(String wildcards) {
        this.mWildcards = wildcards;
    }

    public boolean isRemoveDir() {
        return mShouldRemoveDir;
    }

    public void flagRemoveDir() {
        this.mShouldRemoveDir = true;
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