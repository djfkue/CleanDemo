package com.argonmobile.cleandemo;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.argonmobile.cleandemo.data.WJAppCacheScanResult;
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

    private static final int TYPE_CACHE_JUNK = 0x01;
    private static final int TYPE_MEMORY_BOOST = 0x02;
    private static final int TYPE_ADVERTISE = 0x03;
    private static final int TYPE_UESLESS_INSTALLER = 0x04;
    private static final int TYPE_RESIDUAL_FILE = 0x05;
    private static final int TYPE_APPLICATION_JUNK = 0x06;

    private static final int MSG_START_CACHE_SCAN = 0x01;
    private static final int MSG_STOP_CACHE_SCAN = 0x02;
    private static final int MSG_UPDATE_STORAGE_JUNK = 0x03;
    private static final int MSG_UPDATE_TOTAL_JUNK = 0x04;
    private static final int MSG_UPDATE_CACHE_LIST = 0x05;
    private static final int MSG_UPDATE_APP_CACHE_LIST = 0x06;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_CACHE_SCAN: {
                    //mProgressBar.setIndeterminate(true);
                    mScanInfoView.setVisibility(View.VISIBLE);
                    //mCacheListView.setVisibility(View.GONE);
                    mIsScanning = true;
                    mJunkAdapter.notifyDataSetChanged();
                    break;
                }
                case MSG_STOP_CACHE_SCAN: {
                    //mProgressBar.setIndeterminate(false);
                    Log.e("SD_TRACE", "stop scan");
                    mScanInfoView.setVisibility(View.GONE);
                    //mCacheListView.setVisibility(View.VISIBLE);
                    mIsScanning = false;
                    mJunkAdapter.notifyDataSetChanged();
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
                    mJunkAdapter.notifyDataSetChanged();
                    break;
                }
                case MSG_UPDATE_APP_CACHE_LIST: {
                    mJunkAdapter.notifyDataSetChanged();
                }
                default:
                    break;
            }
        }
    };

    private TextView mTotalJunkSize;
    private TextView mScanInfoView;

    private ExpandableListView mCacheListView;
    private AppAdapter mAppAdapter;

    ArrayList<WJPackageInfo> mPackageInfoList = new ArrayList<>();
    ArrayList<WJAppCacheScanResult> mAppCacheScanResult = new ArrayList<>();

    private JunkAdapter mJunkAdapter;

    private ArrayList<Integer> mJunkTypeList = new ArrayList<>();

    private boolean mIsScanning = true;

    public JunkFragment() {
        //TODO just for demo
        mJunkTypeList.add(TYPE_CACHE_JUNK);
        mJunkTypeList.add(TYPE_APPLICATION_JUNK);
//        mJunkTypeList.add(TYPE_UESLESS_INSTALLER);
//        mJunkTypeList.add(TYPE_ADVERTISE);
//        mJunkTypeList.add(TYPE_RESIDUAL_FILE);
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

        mCacheListView = (ExpandableListView) rootView.findViewById(R.id.junk_list);

        mCacheListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Log.e("SD_TRACE", "onGroupClick....");
                if (mIsScanning) {
                    return false;
                }
                if (!parent.isGroupExpanded(groupPosition)) {
                    parent.expandGroup(groupPosition);
                    View expandIndicator = v.findViewById(R.id.expand_indicator);

                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(expandIndicator, "rotation", 0f, 180f);
                    objectAnimator.setDuration(300);
                    objectAnimator.start();
                } else {
                    parent.collapseGroup(groupPosition);

                    View expandIndicator = v.findViewById(R.id.expand_indicator);

                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(expandIndicator, "rotation", 180f, 0f);
                    objectAnimator.setDuration(300);
                    objectAnimator.start();
                }


                return true;
            }
        });

        mAppAdapter = new AppAdapter(getActivity(), R.layout.activity_main);
        mJunkAdapter = new JunkAdapter();
        mCacheListView.setAdapter(mJunkAdapter);

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
    public void startSystemCacheScanning() {
        mHandler.sendEmptyMessage(MSG_START_CACHE_SCAN);
    }

    @Override
    public void stopSystemCacheScanning() {
        mHandler.sendEmptyMessage(MSG_STOP_CACHE_SCAN);
    }

    @Override
    public void showTotalJunk(long junkSize) {
        Message message = new Message();
        message.what = MSG_UPDATE_TOTAL_JUNK;

        mHandler.removeMessages(MSG_UPDATE_TOTAL_JUNK);

        if (junkSize > 1048576) {
            String junkString = junkSize / 1048576 + " M";
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
    public void updateSystemCacheListView(ArrayList<WJPackageInfo> cacheList) {
        mPackageInfoList.clear();
        mPackageInfoList.addAll(cacheList);
        mHandler.sendEmptyMessage(MSG_UPDATE_CACHE_LIST);
    }

    @Override
    public void updateApplicationCacheListView(ArrayList<WJAppCacheScanResult> appCacheList) {
        mAppCacheScanResult.clear();
        mAppCacheScanResult.addAll(appCacheList);
        mHandler.sendEmptyMessage(MSG_UPDATE_APP_CACHE_LIST);
    }

    class JunkAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mJunkTypeList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mPackageInfoList.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mJunkTypeList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            final GroupViewHolder viewHolder;

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_junk_type, parent, false);
                viewHolder = new GroupViewHolder();
                viewHolder.checkMark = (CheckBox) convertView.findViewById(R.id.check_box);
                viewHolder.expandIndicator = convertView.findViewById(R.id.expand_indicator);
                viewHolder.progressIndicator = (ProgressBar) convertView.findViewById(R.id.progress_indicator);
                viewHolder.junkSzie = (TextView) convertView.findViewById(R.id.junk_size);
                viewHolder.junkTypeName = (TextView) convertView.findViewById(R.id.junk_type);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GroupViewHolder) convertView.getTag();
            }

            if (!mIsScanning) {
                viewHolder.progressIndicator.setVisibility(View.GONE);
                viewHolder.expandIndicator.setVisibility(View.VISIBLE);
                viewHolder.checkMark.setVisibility(View.VISIBLE);
                viewHolder.junkSzie.setVisibility(View.VISIBLE);
            } else {
                viewHolder.progressIndicator.setVisibility(View.VISIBLE);
                viewHolder.expandIndicator.setVisibility(View.GONE);
                viewHolder.checkMark.setVisibility(View.GONE);
                viewHolder.junkSzie.setVisibility(View.GONE);
            }

            int groupType = mJunkTypeList.get(groupPosition);

            String junkType = null;

            switch (groupType) {
                case TYPE_ADVERTISE:
                    junkType = getString(R.string.junk_type_adverts);
                    break;
                case TYPE_CACHE_JUNK:
                    junkType = getString(R.string.junk_type_files);
                    break;
                case TYPE_MEMORY_BOOST:
                    junkType = getString(R.string.junk_type_memory);
                    break;
                case TYPE_RESIDUAL_FILE:
                    junkType = getString(R.string.junk_type_residual);
                    break;
                case TYPE_UESLESS_INSTALLER:
                    junkType = getString(R.string.junk_type_useless_installer);
                    break;
                case TYPE_APPLICATION_JUNK:
                    junkType = getString(R.string.junk_type_application_junk);
                    break;
            }

            viewHolder.junkTypeName.setText(junkType);;

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.application_info, parent, false);
                holder = new ViewHolder();
                holder.appIcon = (ImageView)convertView.findViewById(R.id.app_icon);
                holder.appName = (TextView) convertView.findViewById(R.id.app_name);
                holder.packageSize = (TextView) convertView.findViewById(R.id.cache_size);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            int groupType = mJunkTypeList.get(groupPosition);

            switch (groupType) {
                case TYPE_CACHE_JUNK:
                    getSystemCacheChildView(holder, childPosition);
                    break;
                case TYPE_APPLICATION_JUNK:
                    getApplicationCacheChildView(holder, childPosition);
                    break;
            }



            return convertView;
        }

        private void getSystemCacheChildView(ViewHolder holder, int childPosition) {
            WJPackageInfo packageInfoStruct = mPackageInfoList.get(childPosition);
            holder.appIcon.setImageDrawable(packageInfoStruct.mApplicationInfo.loadIcon(getActivity().getPackageManager()));
            holder.appName.setText(packageInfoStruct.mApplicationInfo.loadLabel(getActivity().getPackageManager()));

            DecimalFormat decimalFormat=new DecimalFormat(".00");
            if (packageInfoStruct.cacheSize > 1048576) {
                holder.packageSize.setText(decimalFormat.format(packageInfoStruct.cacheSize / 1048576.0f) + " M");
            } else {
                holder.packageSize.setText(decimalFormat.format(packageInfoStruct.cacheSize / 1024.0f) + " K");
            }
        }

        private void getApplicationCacheChildView(ViewHolder holder, int childPosition) {
            WJPackageInfo packageInfoStruct = mPackageInfoList.get(childPosition);
            holder.appIcon.setImageDrawable(packageInfoStruct.mApplicationInfo.loadIcon(getActivity().getPackageManager()));
            holder.appName.setText(packageInfoStruct.mApplicationInfo.loadLabel(getActivity().getPackageManager()));

            DecimalFormat decimalFormat=new DecimalFormat(".00");
            if (packageInfoStruct.cacheSize > 1048576) {
                holder.packageSize.setText(decimalFormat.format(packageInfoStruct.cacheSize / 1048576.0f) + " M");
            } else {
                holder.packageSize.setText(decimalFormat.format(packageInfoStruct.cacheSize / 1024.0f) + " K");
            }
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private class ViewHolder {
            public ImageView appIcon;
            public TextView appName;
            public TextView packageSize;
            public CheckBox checkBox;
        }

        private class GroupViewHolder {
            public ProgressBar progressIndicator;
            public TextView junkTypeName;
            public TextView junkSzie;
            public View expandIndicator;
            public CheckBox checkMark;
        }
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
            final ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.application_info, parent, false);
                holder = new ViewHolder();
                holder.appIcon = (ImageView)convertView.findViewById(R.id.app_icon);
                holder.appName = (TextView) convertView.findViewById(R.id.app_name);
                holder.packageSize = (TextView) convertView.findViewById(R.id.cache_size);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            WJPackageInfo packageInfoStruct = mPackageInfoList.get(position);
            holder.appIcon.setImageDrawable(packageInfoStruct.mApplicationInfo.loadIcon(getActivity().getPackageManager()));
            holder.appName.setText(packageInfoStruct.mApplicationInfo.loadLabel(getActivity().getPackageManager()));

            DecimalFormat decimalFormat=new DecimalFormat(".00");
            if (packageInfoStruct.cacheSize > 1048576) {
                holder.packageSize.setText(decimalFormat.format(packageInfoStruct.cacheSize / 1048576.0f) + " M");
            } else {
                holder.packageSize.setText(decimalFormat.format(packageInfoStruct.cacheSize / 1024.0f) + " K");
            }

            return convertView;
        }

        private class ViewHolder {
            public ImageView appIcon;
            public TextView appName;
            public TextView packageSize;
            public CheckBox checkBox;
        }
    }
}
