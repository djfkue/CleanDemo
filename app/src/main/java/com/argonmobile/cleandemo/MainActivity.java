package com.argonmobile.cleandemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.argonmobile.cleandemo.present.JunkPresent;
import com.argonmobile.cleandemo.view.IJunkView;


public class MainActivity extends ActionBarActivity implements IJunkView {

    private JunkPresent mJunkPresent;

    private static final int MSG_START_SCAN = 0x01;
    private static final int MSG_STOP_SCAN = 0x02;
    private static final int MSG_UPDATE_STORAGE_JUNK = 0x03;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_SCAN : {
                    mProgressBar.setIndeterminate(true);
                    break;
                }
                case MSG_STOP_SCAN : {
                    mProgressBar.setIndeterminate(false);
                    break;
                }
                default:
                    break;
            }
        }
    };
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mJunkPresent = new JunkPresent(this);
        mJunkPresent.bindJunkView(this);

        initView();
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_indicator);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mJunkPresent.startScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startCleaning() {

    }

    @Override
    public void finishCleaning() {

    }

    @Override
    public void startScanning() {
        mHandler.sendEmptyMessage(MSG_START_SCAN);
    }

    @Override
    public void stopScanning() {
        mHandler.sendEmptyMessage(MSG_STOP_SCAN);
    }

    @Override
    public void showTotalJunk() {

    }

    @Override
    public void updateMemoryJunk() {

    }

    @Override
    public void updateStorageJunk(long junkSize) {
        Message message = new Message();
        message.what = MSG_UPDATE_STORAGE_JUNK;

    }
}
