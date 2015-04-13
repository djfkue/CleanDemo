package com.argonmobile.cleandemo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.argonmobile.cleandemo.present.JunkPresent;
import com.argonmobile.cleandemo.view.IJunkView;


public class MainActivity extends ActionBarActivity implements IJunkView {

    private JunkPresent mJunkPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mJunkPresent = new JunkPresent(this);
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

    }

    @Override
    public void stopScanning() {

    }

    @Override
    public void showTotalJunk() {

    }

    @Override
    public void updateMemoryJunk() {

    }

    @Override
    public void updateStorageJunk() {

    }
}
