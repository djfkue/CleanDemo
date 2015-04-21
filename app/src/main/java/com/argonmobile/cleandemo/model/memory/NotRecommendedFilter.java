package com.argonmobile.cleandemo.model.memory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sean on 4/21/15.
 */
public class NotRecommendedFilter implements IAppFilter {
    public final static NotRecommendedFilter defaultFilter = new NotRecommendedFilter();
    static {
        Set<String> default_not_recommended = new HashSet<String>();
        default_not_recommended.add("com.svox.pico");
        default_not_recommended.add("com.oppo.alarmclock");
        default_not_recommended.add("android.process.contacts");
        default_not_recommended.add("com.oppo.weather");
        default_not_recommended.add("com.tencent.mobileqq");
        default_not_recommended.add("com.tencent.mm");
        default_not_recommended.add("com.qihoo360.mobilesafe");
        defaultFilter.setNotRecommendedPackages(default_not_recommended);
    }
    private Set<String> not_recommended_pkgs = new HashSet<String>();
    public void setNotRecommendedPackages(Collection<String> collection) {
        not_recommended_pkgs.clear();
        not_recommended_pkgs.addAll(collection);
    }

    @Override
    public boolean isFilter(String name) {
        return not_recommended_pkgs.contains(name);
    }
}
