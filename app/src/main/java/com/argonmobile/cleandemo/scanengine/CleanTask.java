package com.argonmobile.cleandemo.scanengine;

import android.os.AsyncTask;

/**
 * Created by yanni on 15/4/23.
 */
public class CleanTask extends AsyncTask<Integer, String, Integer> {

    public static final int TASK_SCAN = 0;
    public static final int TASK_CLEAN = 1;

    @Override
    protected Integer doInBackground(Integer... params) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Integer integer) {
        super.onCancelled(integer);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
