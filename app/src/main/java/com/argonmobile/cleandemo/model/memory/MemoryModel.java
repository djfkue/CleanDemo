package com.argonmobile.cleandemo.model.memory;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;

/**
 * Created by sean on 4/21/15.
 */
public class MemoryModel {
    // Singleton module
    public final static MemoryModel defaultModel = new MemoryModel();
    private MemoryModel() {};

    private ActivityManager activityManager;
    private Context ctx;

    private IAppFilter systemAppFilter;
    private IAppFilter notRecommendedFilter;

    /**
     * Memory model must be initialized before using
     * @param ctx
     */
    public void init(Context ctx) {
        if(ctx == null) throw new IllegalArgumentException("Context be null!");
        activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
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

    }

}
