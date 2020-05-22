package com.regan.saata.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.adapter.AudioListAdapter;
import com.regan.saata.bean.AudioInfo;
import com.regan.saata.util.FileDurationUtil;
import com.regan.saata.util.FileSizeUtil;
import com.regan.saata.util.ListDataSave;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.SharedPrefrencesUtil;
import com.regan.saata.util.TimeUtils;
import com.regan.saata.view.MyLoadingDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFtask;

public class ListFragment extends Fragment {
    private RecyclerView rvAudio;
    private FFmpeg fFmpeg;
    private ListDataSave listDataSave;
    private boolean stilGetInfo = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        rvAudio = root.findViewById(R.id.rv_audio);

//        FileManager fileManager = FileManager.getInstance(mActivity);
//        List<AudioInfo> audioInfos = fileManager.getMusics();
//        LogUtils.d(Constant.TAG, " musics : " + audioInfos.size());
//        for (AudioInfo info:audioInfos
//             ) {
//            LogUtils.d(Constant.TAG, " info : " + info.toString());
//        }

        fFmpeg = FFmpeg.getInstance(mActivity);
        listDataSave = new ListDataSave(mActivity, Constant.AUDIO_LIST);
        return root;
    }

    private long lastModifiedTime = 0L;

    @Override
    public void onResume() {
        super.onResume();
        final Dialog dialog = new MyLoadingDialog().getLodingDialog(mActivity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
                File file = new File(Constant.getMusicPath());
                final File[] files = file.listFiles();
                LogUtils.d(Constant.TAG, " files lastModified : " + file.lastModified());
                lastModifiedTime = SharedPrefrencesUtil.getLongByKey(mActivity, "lastModifiedTime", 0);
                LogUtils.d(Constant.TAG, " lastModified : " + lastModifiedTime);
                List<AudioInfo> oldData = listDataSave.getDataList(Constant.AUDIO, AudioInfo.class);
                final List<AudioInfo> data = new ArrayList<>();
                final AudioListAdapter audioListAdapter = new AudioListAdapter(mActivity, data);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        rvAudio.setLayoutManager(new LinearLayoutManager(mActivity));
                        rvAudio.setAdapter(audioListAdapter);
                    }
                });
                if (lastModifiedTime == file.lastModified()) {
                    data.addAll(oldData);
                    oldData.clear();
                } else {
                    stilGetInfo = true;
//                    String[] a = file.list();
//                    if(a != null && a.length > 0){
//                        for (String s:a) {
//                            LogUtils.d(Constant.TAG, s);
//                        }
//                    }
                    if (files != null && files.length > 0) {
                        for (final File f : files) {
//                    LogUtils.d(Constant.TAG, " name: " + f.getName());
//                    LogUtils.d(Constant.TAG, " size: " + FileSizeUtil.getAutoFileOrFilesSize(f.getPath()));
                            if ("0B".equals(FileSizeUtil.getAutoFileOrFilesSize(f.getPath()))) {
//                                f.delete();
                                continue;
                            }
                            try {
                                int duration = FileDurationUtil.getDuration(f.getPath()) / 1000;
                                if (TimeUtils.secondToTime(duration).contains("00:00:00")) {
//                                    f.delete();
                                    continue;
                                }
                            } catch (Exception e) {
//                                f.delete();
                                continue;
                            }
                            if (oldData != null && oldData.size() != 0) {
//                                LogUtils.d(Constant.TAG, "oldData");
                                boolean isContains = false;
                                for (AudioInfo audioInfo : oldData) {
//                                    LogUtils.d(Constant.TAG, "AudioInfo");
                                    if (audioInfo.getName().equals(f.getName())) {
//                                        LogUtils.d(Constant.TAG, "continue");
                                        data.add(audioInfo);
                                        isContains = true;
                                        break;
                                    }
                                }
                                if (isContains) {
                                    continue;
                                }
                            }
//                    LogUtils.d(Constant.TAG, " type: " + f.getPath().substring(f.getPath().lastIndexOf(".") + 1));
                            final AudioInfo info = new AudioInfo();
//                            LogUtils.d(Constant.TAG, "yeah " + info.getName());
                            info.setPath(f.getPath());
                            info.name = f.getName();
                            info.time = "时长:" + TimeUtils.secondToTime(FileDurationUtil.getDuration(f.getPath()) / 1000);
                            info.size = "大小:" + FileSizeUtil.getAutoFileOrFilesSize(f.getPath());
                            info.params = "参数:";
                            info.lastModified = f.lastModified();
                            ArrayList<String> cmdd = new ArrayList<>();
                            cmdd.add("-i");
                            cmdd.add(f.getPath());
                            cmdd.add("-filter:a");
                            cmdd.add("volumedetect");
                            cmdd.add("-f");
                            cmdd.add("null");
                            cmdd.add("/dev/null");
                            final String[] cmd = cmdd.toArray(new String[0]);
                            getFileInfo(cmd, info, audioListAdapter, data);
                            data.add(info);
                        }
                        Collections.sort(data, new FileComparator());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                audioListAdapter.refresh(data);
                                listDataSave.setDataList(Constant.AUDIO, data);
                            }
                        });
                        listDataSave.setDataList(Constant.AUDIO, data);
                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
                lastModifiedTime = file.lastModified();
                SharedPrefrencesUtil.saveLongByKey(mActivity, "lastModifiedTime", lastModifiedTime);
            }
        }).start();
    }

    private FFtask fFtask;

    private void getFileInfo(final String[] cmd, final AudioInfo info, final AudioListAdapter audioListAdapter, final List<AudioInfo> data) {
//        if (!stilGetInfo){
//            return;
//        }
        if (fFmpeg.isCommandRunning(fFtask)) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    getFileInfo(cmd, info, audioListAdapter, data);
                }
            }, 100);
            return;
        }
        try {
            fFtask = fFmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    LogUtils.d(Constant.TAG, " onSuccess " + message);
                    String time;
                    String hz;
                    String bitrate;
                    if (message.contains("time=")) {
                        time = message.substring(message.lastIndexOf("time=") + 5, message.lastIndexOf("time=") + 17);
                        if (info.getTime().substring(info.getTime().indexOf(":") + 1).equals("00:00:00")) {
                            info.setTime("时长:" + time);
                        }
                        if (Constant.time2Float(time) <= Constant.time2Float(info.getTime().substring(info.getTime().indexOf(":") + 1))) {
                            info.setTime("时长:" + time);
                        }
                    }
                    if (message.contains("bitrate:")) {
                        bitrate = message.substring(message.indexOf("bitrate:") + 8, message.indexOf("kb/s") + 4);
                        info.setParams(info.getParams() + "" + bitrate + ",");
                    }
                    if (message.contains("Hz,")) {
                        hz = message.substring(message.indexOf("Hz,") - 6, message.indexOf("Hz,") + 2);
                        info.setParams(info.getParams() + " " + hz + ",");
                    }
                    info.setParams(info.getParams() + " 立体声");
//                            data.add(info);
//                    Constant.allAudio = data;
                    if (!fFmpeg.isCommandRunning(fFtask)) {
                        listDataSave.setDataList(Constant.AUDIO, data);
                        audioListAdapter.refresh(data);
                    }
                }

                @Override
                public void onProgress(String message) {

                }

                @Override
                public void onFailure(String message) {

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//                            isFreshFail = true;
        }
    }


    /**
     * 将文件按时间降序排列
     */
    class FileComparator implements Comparator<AudioInfo> {

        @Override
        public int compare(AudioInfo audioInfo1, AudioInfo audioInfo2) {
            if (audioInfo1.getLastModified() < audioInfo2.getLastModified()) {
                return 1;// 最后修改的文件在前
            } else {
                return -1;
            }
        }
    }

    public void setStilGetInfo(boolean isGetInfo) {
        LogUtils.d(Constant.TAG, " isGetInfo : " + isGetInfo);
        stilGetInfo = isGetInfo;
    }

    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
//        mParam = getArguments().getString(ARG_PARAM);  //获取activity穿过来的参数
    }

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
//        bundle.putString(ARG_PARAM, str);
        fragment.setArguments(bundle);   //设置参数
        return fragment;
    }

}
