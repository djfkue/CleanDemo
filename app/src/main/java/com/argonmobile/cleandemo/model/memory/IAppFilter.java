package com.argonmobile.cleandemo.model.memory;

import java.util.Collection;

/**
 * Created by sean on 4/21/15.
 */
public interface IAppFilter {
    boolean isFilter(String name);
    void addPackage(String packageName);
    void addPackages(Collection<String> collection);
}
