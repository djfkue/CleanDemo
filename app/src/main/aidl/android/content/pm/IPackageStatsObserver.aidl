package android.content.pm;

import android.content.pm.PackageStats;

oneway interface IPackageStatsObserver {
    void onGetStatsCompleted(in android.content.pm.PackageStats pStats, boolean succeeded);
}