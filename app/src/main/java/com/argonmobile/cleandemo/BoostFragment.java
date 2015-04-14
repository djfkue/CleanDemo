package com.argonmobile.cleandemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.argonmobile.cleandemo.util.MemorySizeFormatter;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class BoostFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private final static String TAG = "TaskManagerActivity";
    private ProcessHelper mProcessHelper;
    private ApplicationHelper mAppHelper;
    private HistoryHelper mHistoryHelper;
    private ActivityManager mActivityManager;

    private List<Map<String, Object>> mProcessInfos;
    private static final int LOAD_PROCESS_INFOS = -1;

    private ListView mProcessList;
    private Button mBtnClear;
    private TextView mMemInfoText;
    private TextView mMemScanningText;
    private ScanRunningAppHandler mScanHandler;

    private long startTime;

    private final static class ScanRunningAppHandler extends Handler {
        private WeakReference<BoostFragment> mActivity;
        public ScanRunningAppHandler(BoostFragment activity) {
            mActivity = new WeakReference<BoostFragment>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            BoostFragment activity = mActivity.get();
            if(activity == null) return;
            switch (msg.what) {
                case LOAD_PROCESS_INFOS:
                    activity.handleLoadProcessInfo();
                    break;
                case ProcessHelper.MSG_ON_GET_STARTED:
                    break;
                case ProcessHelper.MSG_ON_GET_APP:
                    activity.handleLoadProcessInfo((List<Map<String, Object>>)msg.obj);
                    break;
                case ProcessHelper.MSG_ON_GET_FINISHED:
                    break;
            }
        }
    }
    protected void handleLoadProcessInfo(List<Map<String, Object>> processInfos) {
        mProcessInfos = processInfos;
        handleLoadProcessInfo();
    }

    protected void handleLoadProcessInfo() {
        if ((mProcessInfos == null) || mProcessInfos.isEmpty()) {
            mProcessList.setAdapter(null);
            return;
        } else {
            Log.i(TAG, "cost:" + (System.currentTimeMillis() - startTime) + "ms");
            Log.w(TAG, "There are " + mProcessInfos.size() + " apps running now.");
            mProcessList.setAdapter(new ProcessListAdapter());
            updateButtonInfo();
        }
    }

    public BoostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_boost, container, false);

        mScanHandler = new ScanRunningAppHandler(this);
        mProcessList = (ListView) rootView.findViewById(R.id.tasks);
        mBtnClear = (Button) rootView.findViewById(R.id.button_clear);
        mMemInfoText = (TextView) rootView.findViewById(R.id.mem_info);
        mMemScanningText = (TextView) rootView.findViewById(R.id.memory_scanning);

        mActivityManager = (ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        mAppHelper = new ApplicationHelper(getActivity().getPackageManager());
        mProcessHelper = new ProcessHelper(mActivityManager, mAppHelper);
        mHistoryHelper = new HistoryHelper();

        mBtnClear.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMemoryInfo();

        //updateProcessInfoAsync();
        mProcessHelper.getRunningAppsAsync(mScanHandler);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (Integer)buttonView.getTag();
        mProcessInfos.get(position).put(ProcessHelper.APP_RECOMMEND_CLEAN, isChecked);
        updateButtonInfo();
    }

    @Override
    public void onClick(View v) {
        //Map<String, Object> processInfo;
        startTime = System.currentTimeMillis();
        for(Map<String, Object> runningAppInfo : mProcessInfos) {
            if((boolean)runningAppInfo.get(ProcessHelper.APP_RECOMMEND_CLEAN)) {
                List<ActivityManager.RunningAppProcessInfo> processes =
                        (List<ActivityManager.RunningAppProcessInfo>)runningAppInfo.get(ProcessHelper.APP_PROCS);
                for(ActivityManager.RunningAppProcessInfo processInfo : processes) {
                    mProcessHelper.killApp(processInfo.processName);
                }
            }
        }
        Log.i(TAG, "kill cost:" + (System.currentTimeMillis() - startTime) + "ms");
        startTime = System.currentTimeMillis();
        updateMemoryInfo();
        Log.i(TAG, "update memory info cost:" + (System.currentTimeMillis() - startTime) + "ms");
        //updateProcessInfoAsync();
        mProcessHelper.getRunningAppsAsync(mScanHandler);
    }

    private void updateButtonInfo() {
        int selectedProcessCount = 0;
        long cleanedMem = 0;

        for(Map<String, Object> runningAppInfo : mProcessInfos) {
            if((boolean)runningAppInfo.get(ProcessHelper.APP_RECOMMEND_CLEAN)) {
                ++selectedProcessCount;
                cleanedMem += (Integer)runningAppInfo.get(ProcessHelper.APP_TOTAL_PSS);
            }
        }
        mBtnClear.setText(selectedProcessCount == 0 ? "No process selected" : "Kill " +
                selectedProcessCount + " processes");
        mMemScanningText.setText(Formatter.formatFileSize(getActivity(), cleanedMem * 1024));
    }

    private void updateMemoryInfo() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);

        mMemInfoText.setText(Formatter.formatFileSize(getActivity(), memoryInfo.totalMem - memoryInfo.availMem) + "/" +
                Formatter.formatFileSize(getActivity(), memoryInfo.totalMem));
    }

    private class ProcessListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mProcessInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mProcessInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if(view == null) {
                holder = new ViewHolder();
                view = getActivity().getLayoutInflater().inflate(R.layout.scan_result_item_multiple_choice, parent, false);
                holder.appIcon = (ImageView)view.findViewById(R.id.app_icon);
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.subTitle = (TextView) view.findViewById(R.id.sub_title);
                holder.detail = (TextView) view.findViewById(R.id.detail);
                holder.checkBox = (CheckBox) view.findViewById(R.id.check_mark);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.checkBox.setTag(Integer.valueOf(position));
            holder.checkBox.setOnCheckedChangeListener(BoostFragment.this);
            Map<String, Object> processInfo = mProcessInfos.get(position);
            int memorySizeKb = (Integer)processInfo.get(ProcessHelper.APP_TOTAL_PSS);
            Log.i(TAG, "app name: " + (String) processInfo.get(ProcessHelper.APP_NAME));
            Drawable appIcon;
            if(processInfo.get(ProcessHelper.APP_ICON) instanceof Drawable) {
                appIcon = (Drawable)processInfo.get(ProcessHelper.APP_ICON);
            } else {
                appIcon = getActivity().getResources().getDrawable((Integer)processInfo.get(ProcessHelper.APP_ICON));
            }

            holder.title.setText((String)processInfo.get(ProcessHelper.APP_NAME));
            holder.detail.setText(MemorySizeFormatter.formatMemorySize(memorySizeKb * 1024));
            holder.appIcon.setImageDrawable(appIcon);
            holder.checkBox.setChecked((boolean)mProcessInfos.get(position).get(ProcessHelper.APP_RECOMMEND_CLEAN));

            return view;
        }
    };

    private static class ViewHolder {
        public ImageView appIcon;
        public TextView title;
        public TextView subTitle;
        public TextView detail;
        public CheckBox checkBox;
    }

}
