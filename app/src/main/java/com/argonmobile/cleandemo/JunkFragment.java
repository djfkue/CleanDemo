package com.argonmobile.cleandemo;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.argonmobile.cleandemo.data.WJPackageInfo;
import com.argonmobile.cleandemo.present.JunkPresent;
import com.argonmobile.cleandemo.view.IJunkView;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class JunkFragment extends Fragment  implements IJunkView {

    private JunkPresent mJunkPresent;

    private static final int MSG_START_SCAN = 0x01;
    private static final int MSG_STOP_SCAN = 0x02;
    private static final int MSG_UPDATE_STORAGE_JUNK = 0x03;
    private static final int MSG_UPDATE_TOTAL_JUNK = 0x04;
    private static final int MSG_UPDATE_CACHE_LIST = 0x05;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_SCAN : {
                    //mProgressBar.setIndeterminate(true);
                    mScanInfoView.setVisibility(View.VISIBLE);
                    mCacheListView.setVisibility(View.GONE);
                    break;
                }
                case MSG_STOP_SCAN : {
                    //mProgressBar.setIndeterminate(false);
                    Log.e("SD_TRACE", "stop scan");
                    mScanInfoView.setVisibility(View.GONE);
                    mCacheListView.setVisibility(View.VISIBLE);
                    break;
                }
                case MSG_UPDATE_STORAGE_JUNK: {
                    mScanInfoView.setText((String)msg.obj);
                    break;
                }
                case MSG_UPDATE_TOTAL_JUNK: {
                    mTotalJunkSize.setText((CharSequence)msg.obj);
                    break;
                }
                case MSG_UPDATE_CACHE_LIST: {
                    mAppAdapter.notifyDataSetChanged();
                    break;
                }
                default:
                    break;
            }
        }
    };

    private TextView mTotalJunkSize;
    private TextView mScanInfoView;

    private ListView mCacheListView;
    private AppAdapter mAppAdapter;

    ArrayList<WJPackageInfo> mPackageInfoList = new ArrayList<>();


    public JunkFragment() {
        // Required empty public constructor

    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);

        mJunkPresent = new JunkPresent(getActivity());
        mJunkPresent.bindJunkView(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_junk, container, false);
        mTotalJunkSize = (TextView) rootView.findViewById(R.id.total_junk);
        mScanInfoView = (TextView) rootView.findViewById(R.id.junk_scan_progress_info);

        mCacheListView = (ListView) rootView.findViewById(R.id.app_list);
        mAppAdapter = new AppAdapter(getActivity(), R.layout.activity_main);
        mCacheListView.setAdapter(mAppAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mJunkPresent.startScan();
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
    public void showTotalJunk(long junkSize) {
        Message message = new Message();
        message.what = MSG_UPDATE_TOTAL_JUNK;

        mHandler.removeMessages(MSG_UPDATE_TOTAL_JUNK);

        if (junkSize > 1024000) {
            String junkString = junkSize / 1024000 + " M";
            message.obj = junkString;
            mHandler.sendMessage(message);
        } else {
            String junkString = junkSize / 1024 + " K";
            message.obj = junkString;
            mHandler.sendMessage(message);
        }

    }

    @Override
    public void updateMemoryJunk(long junkSize) {

    }

    @Override
    public void updateStorageJunk(WJPackageInfo packageInfo) {

        mHandler.removeMessages(MSG_UPDATE_STORAGE_JUNK);

        Message message = new Message();
        message.what = MSG_UPDATE_STORAGE_JUNK;

        String junkString = String.format(getResources().getString(R.string.junk_scan_info), packageInfo.mAppPackageName);
        message.obj = junkString;

        mHandler.sendMessage(message);
    }

    @Override
    public void updateCacheListView(ArrayList<WJPackageInfo> cacheList) {
        mPackageInfoList.clear();
        mPackageInfoList.addAll(cacheList);
        mHandler.sendEmptyMessage(MSG_UPDATE_CACHE_LIST);
    }

    class AppAdapter extends ArrayAdapter<WJPackageInfo> {

        public AppAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return mPackageInfoList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.application_info, parent, false);
            }
            ImageView appIcon = (ImageView)convertView.findViewById(R.id.app_icon);
            TextView appName = (TextView) convertView.findViewById(R.id.app_name);
            //TextView packageName = (TextView) convertView.findViewById(R.id.package_name);
            TextView packageSize = (TextView) convertView.findViewById(R.id.cache_size);

            WJPackageInfo packageInfoStruct = mPackageInfoList.get(position);
//            newInfo.icon = p.applicationInfo.loadIcon(mContext
//                    .getPackageManager());
            appIcon.setImageDrawable(packageInfoStruct.mApplicationInfo.loadIcon(getActivity().getPackageManager()));
            appName.setText(packageInfoStruct.mApplicationInfo.loadLabel(getActivity().getPackageManager()));
            //packageName.setText(packageInfoStruct.mAppPackageName);

            DecimalFormat decimalFormat=new DecimalFormat(".00");
            if (packageInfoStruct.cacheSize > 1024000) {
                packageSize.setText(decimalFormat.format(packageInfoStruct.cacheSize / 1024000.0f) + " M");
            } else {
                packageSize.setText(decimalFormat.format(packageInfoStruct.cacheSize / 1024.0f) + " K");
            }

            return convertView;
        }
    }
}
