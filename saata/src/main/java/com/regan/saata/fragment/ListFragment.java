package com.regan.saata.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.adapter.MediaListAdapter;
import com.regan.saata.bean.MediaInfo;
import com.regan.saata.util.FileManager;
import com.regan.saata.util.FileSizeUtil;
import com.regan.saata.util.ListDataSave;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.SharedPrefrencesUtil;
import com.regan.saata.view.MyDelDialog;
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
    private LinearLayout llAudio;
    private LinearLayout llGif;
    private LinearLayout llVideo;
    private LinearLayout llEmpty;
    private TextView tvAudio;
    private TextView tvGif;
    private TextView tvVideo;
    private View vAudio;
    private View vGif;
    private View vVideo;
    private RecyclerView rvAudio;
    private RecyclerView rvGif;
    private RecyclerView rvVideo;
    private ImageView ivDel;
    private FFmpeg fFmpeg;
    private ListDataSave videolListDataSave;
    private ListDataSave giflListDataSave;
    private ListDataSave audioListDataSave;
    private MediaListAdapter videoListAdapter;
    private MediaListAdapter gifListAdapter;
    private MediaListAdapter audioListAdapter;
    private boolean stilGetInfo = true;
    private boolean refreshing = true;
    private boolean fromSuccess = true;
    private int currentTab = 1;//当前tab 1 video  2 GIF 3 audio
    private List<MediaInfo> videoList;
    private List<MediaInfo> gifList;
    private List<MediaInfo> audioList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        rvVideo = root.findViewById(R.id.rv_video);
        rvGif = root.findViewById(R.id.rv_gif);
        rvAudio = root.findViewById(R.id.rv_audio);
        llVideo = root.findViewById(R.id.ll_video);
        llEmpty = root.findViewById(R.id.ll_empty);
        llGif = root.findViewById(R.id.ll_gif);
        llAudio = root.findViewById(R.id.ll_audio);
        tvVideo = root.findViewById(R.id.tv_video);
        tvGif = root.findViewById(R.id.tv_gif);
        tvAudio = root.findViewById(R.id.tv_audio);
        vVideo = root.findViewById(R.id.v_video);
        vGif = root.findViewById(R.id.v_gif);
        vAudio = root.findViewById(R.id.v_audio);
        ivDel = root.findViewById(R.id.iv_del);
        llVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAudio.setTextColor(getActivity().getResources().getColor(R.color.text_666));
                vAudio.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                tvGif.setTextColor(getActivity().getResources().getColor(R.color.text_666));
                vGif.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                tvVideo.setTextColor(getActivity().getResources().getColor(R.color.list_tab_selected));
                vVideo.setBackgroundColor(getActivity().getResources().getColor(R.color.list_tab_selected));
                rvAudio.setVisibility(View.GONE);
                rvGif.setVisibility(View.GONE);
                currentTab = 1;
//                LogUtils.d(Constant.TAG, "llVideo");
                if (videoListAdapter.getItemCount() == 0) {
                    rvVideo.setVisibility(View.GONE);
                    llEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvVideo.setVisibility(View.VISIBLE);
                    llEmpty.setVisibility(View.GONE);
                }
            }
        });
        llGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAudio.setTextColor(getActivity().getResources().getColor(R.color.text_666));
                vAudio.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                tvGif.setTextColor(getActivity().getResources().getColor(R.color.list_tab_selected));
                vGif.setBackgroundColor(getActivity().getResources().getColor(R.color.list_tab_selected));
                tvVideo.setTextColor(getActivity().getResources().getColor(R.color.text_666));
                vVideo.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                rvAudio.setVisibility(View.GONE);
                rvVideo.setVisibility(View.GONE);
                currentTab = 2;
                LogUtils.d(Constant.TAG, "llGif currentTab " + currentTab);
                if (gifListAdapter.getItemCount() == 0) {
                    rvGif.setVisibility(View.GONE);
                    llEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvGif.setVisibility(View.VISIBLE);
                    llEmpty.setVisibility(View.GONE);
                }
            }
        });
        llAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAudio.setTextColor(getActivity().getResources().getColor(R.color.list_tab_selected));
                vAudio.setBackgroundColor(getActivity().getResources().getColor(R.color.list_tab_selected));
                tvGif.setTextColor(getActivity().getResources().getColor(R.color.text_666));
                vGif.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                tvVideo.setTextColor(getActivity().getResources().getColor(R.color.text_666));
                vVideo.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                rvGif.setVisibility(View.GONE);
                rvVideo.setVisibility(View.GONE);
                currentTab = 3;
//                LogUtils.d(Constant.TAG, "llAudio");
                if (audioListAdapter.getItemCount() == 0) {
                    rvAudio.setVisibility(View.GONE);
                    llEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvAudio.setVisibility(View.VISIBLE);
                    llEmpty.setVisibility(View.GONE);
                }
            }
        });
        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyDelDialog myDelDialog = new MyDelDialog(mActivity, R.style.my_del_dialog);
                ImageView ivCancle = myDelDialog.findViewById(R.id.iv_cancle);
                ImageView ivConfirm = myDelDialog.findViewById(R.id.iv_confirm);
                ivCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDelDialog.dismiss();
                    }
                });
                ivConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<MediaInfo> delList = new ArrayList<>();
                        List<MediaInfo> newList = new ArrayList<>();
                        MediaListAdapter delAdapter;
                        if (currentTab == 1) {
                            delList = videoList;
                            delAdapter = videoListAdapter;
                        } else if (currentTab == 2) {
                            delList = gifList;
                            delAdapter = gifListAdapter;
                        } else {
                            delList = audioList;
                            delAdapter = audioListAdapter;
                        }
                        int delListSize = delList.size();
                        newList = FileManager.deleteFile(mActivity, delList);
                        if (newList.size() != delListSize) {
                            if (currentTab == 1) {
                                videoList = newList;
//                                videolListDataSave.setDataList(Constant.VIDEO, videoList);
                                videolListDataSave.clearDataList();
                                llEmpty.setVisibility(View.VISIBLE);
                            } else if (currentTab == 2) {
                                gifList = newList;
//                                giflListDataSave.setDataList(Constant.GIF, gifList);
                                giflListDataSave.clearDataList();
                                llEmpty.setVisibility(View.VISIBLE);
                            } else {
                                audioList = newList;
//                                audioListDataSave.setDataList(Constant.AUDIO, audioList);
                                audioListDataSave.clearDataList();
                                llEmpty.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(mActivity, "删除成功", Toast.LENGTH_SHORT).show();
                            SharedPrefrencesUtil.saveLongByKey(mActivity, "lastModifiedTime", new File(Constant.getFilePath()).lastModified());
                            delAdapter.refresh(newList);
//                            delAdapter.notifyDataSetChanged();
//                            onResume();
                        }
//                        FileManager.deletefile(mActivity, Constant.getFilePath(), currentTab);
                        myDelDialog.dismiss();
                    }
                });
                myDelDialog.show();
            }
        });
//        FileManager fileManager = FileManager.getInstance(mActivity);
//        List<MediaInfo> audioInfos = fileManager.getMusics();
//        LogUtils.d(Constant.TAG, " musics : " + audioInfos.size());
//        for (MediaInfo info:audioInfos
//             ) {
//            LogUtils.d(Constant.TAG, " info : " + info.toString());
//        }

        fFmpeg = FFmpeg.getInstance(mActivity);
        videolListDataSave = new ListDataSave(mActivity, Constant.VIDEO_LIST);
        giflListDataSave = new ListDataSave(mActivity, Constant.GIF_LIST);
        audioListDataSave = new ListDataSave(mActivity, Constant.AUDIO_LIST);
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
                        refreshing = true;
                    }
                });
                File file = new File(Constant.getFilePath());
                final File[] files = file.listFiles();
                LogUtils.d(Constant.TAG, " files lastModified : " + file.lastModified());
                lastModifiedTime = SharedPrefrencesUtil.getLongByKey(mActivity, "lastModifiedTime", 0);
                LogUtils.d(Constant.TAG, " lastModified : " + lastModifiedTime);
                List<MediaInfo> oldVideoData = videolListDataSave.getDataList(Constant.VIDEO, MediaInfo.class);
                List<MediaInfo> oldGifData = giflListDataSave.getDataList(Constant.GIF, MediaInfo.class);
                List<MediaInfo> oldAudioData = audioListDataSave.getDataList(Constant.AUDIO, MediaInfo.class);
                videoList = new ArrayList<>();
                gifList = new ArrayList<>();
                audioList = new ArrayList<>();
                videoListAdapter = new MediaListAdapter(mActivity, videoList);
                gifListAdapter = new MediaListAdapter(mActivity, gifList);
                audioListAdapter = new MediaListAdapter(mActivity, audioList);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        rvVideo.setLayoutManager(new GridLayoutManager(mActivity, 2));
                        rvVideo.setAdapter(videoListAdapter);
                        rvGif.setLayoutManager(new GridLayoutManager(mActivity, 2));
                        rvGif.setAdapter(gifListAdapter);
                        rvAudio.setLayoutManager(new GridLayoutManager(mActivity, 2));
                        rvAudio.setAdapter(audioListAdapter);
                    }
                });
                refreshMediaList(file, files, oldVideoData, videoList, videoListAdapter, Constant.VIDEO, videolListDataSave, 1);
                refreshMediaList(file, files, oldGifData, gifList, gifListAdapter, Constant.GIF, giflListDataSave, 2);
                refreshMediaList(file, files, oldAudioData, audioList, audioListAdapter, Constant.AUDIO, audioListDataSave, 3);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        refreshing = false;
                    }
                });
                lastModifiedTime = file.lastModified();
                SharedPrefrencesUtil.saveLongByKey(mActivity, "lastModifiedTime", lastModifiedTime);
            }
        }).start();
    }

    private void refreshMediaList(File file, File[] files, List<MediaInfo> oldMediaInfoList, final List<MediaInfo> mediaInfoList, final MediaListAdapter mediaListAdapter, final String type, final ListDataSave listDataSave, int tab) {
        if (lastModifiedTime == file.lastModified()) {
            mediaInfoList.addAll(oldMediaInfoList);
            oldMediaInfoList.clear();
        } else {
            stilGetInfo = true;
            if (files != null && files.length > 0) {
                for (final File f : files) {
//                    LogUtils.d(Constant.TAG, " name: " + f.getName());
//                    LogUtils.d(Constant.TAG, " size: " + FileSizeUtil.getAutoFileOrFilesSize(f.getPath()));
                    if ("0B".equals(FileSizeUtil.getAutoFileOrFilesSize(f.getPath()))) {
//                                f.delete();
                        continue;
                    }
//                    if (getFileType(f.getPath()).equals("mp4")
//                            || getFileType(f.getPath()).equals("wmv")
//                            || getFileType(f.getPath()).equals("avi")) {
//                        try {
//                            int duration = FileDurationUtil.getDuration(f.getPath()) / 1000;
//                            if (TimeUtils.secondToTime(duration).contains("00:00:00")) {
////                                    f.delete();
//                                continue;
//                            }
//                        } catch (Exception e) {
////                                f.delete();
//                            continue;
//                        }
//                    }
                    if (oldMediaInfoList != null && oldMediaInfoList.size() != 0) {
//                                LogUtils.d(Constant.TAG, "oldData");
                        boolean isContains = false;
                        for (MediaInfo mediaInfo : oldMediaInfoList) {
//                                    LogUtils.d(Constant.TAG, "MediaInfo");
                            if (mediaInfo.getName().equals(f.getName())) {
//                                        LogUtils.d(Constant.TAG, "continue");
                                mediaInfoList.add(mediaInfo);
                                isContains = true;
                                break;
                            }
                        }
                        if (isContains) {
                            continue;
                        }
                    }
//                    LogUtils.d(Constant.TAG, " currentTab: " + f.getPath().substring(f.getPath().lastIndexOf(".") + 1));
                    final MediaInfo info = new MediaInfo();
//                            LogUtils.d(Constant.TAG, "yeah " + info.getName());
                    info.setPath(f.getPath());
                    info.name = f.getName();
                    info.time = "时长:" + "00:00:00";
//                    info.time = "时长:" + TimeUtils.secondToTime(FileDurationUtil.getDuration(f.getPath()) / 1000);
                    info.size = "大小:" + FileSizeUtil.getAutoFileOrFilesSize(f.getPath());
                    info.params = "参数:";
                    info.type = getFileType(f.getPath());
                    info.lastModified = f.lastModified();
//                    LogUtils.d(Constant.TAG, " currentTab :" + info.currentTab);
                    ArrayList<String> cmdd = new ArrayList<>();
                    cmdd.add("-i");
                    cmdd.add(f.getPath());
                    cmdd.add("-filter:a");
                    cmdd.add("volumedetect");
                    cmdd.add("-f");
                    cmdd.add("null");
                    cmdd.add("/dev/null");
                    final String[] cmd = cmdd.toArray(new String[0]);

                    if (type.equals(Constant.VIDEO)) {
                        if (info.type.equals("mp4")
                                || info.type.equals("wmv")
                                || info.type.equals("avi")) {
                            getFileInfo(cmd, info, mediaListAdapter, mediaInfoList, type, listDataSave);
                            mediaInfoList.add(info);
                        }
                    }
                    if (type.equals(Constant.GIF)) {
                        if (info.type.equals("gif")) {
                            getFileInfo(cmd, info, mediaListAdapter, mediaInfoList, type, listDataSave);
                            mediaInfoList.add(info);
                        }
                    }
                    if (type.equals(Constant.AUDIO)) {
                        if (info.type.equals("wav")
                                || info.type.equals("wma")
                                || info.type.equals("aac")
                                || info.type.equals("m4a")
                                || info.type.equals("mp3")
                                || info.type.equals("flac")) {
                            getFileInfo(cmd, info, mediaListAdapter, mediaInfoList, type, listDataSave);
                            mediaInfoList.add(info);
                        }
                    }
//                    if(info.currentTab.equals("mp4")
//                            || info.currentTab.equals("wmv")
//                            || info.currentTab.equals("avi")){
//                        getFileInfo(cmd, info, videoListAdapter, videoList);
//                        videoList.add(info);
//                    }else if(info.currentTab.equals("gif")){
//                        getFileInfo(cmd, info, gifListAdapter, gifList);
//                        gifList.add(info);
//                    }else{
//                        getFileInfo(cmd, info, audioListAdapter, audioList);
//                        audioList.add(info);
//                    }
//                            audioList.add(info);
                }
                Collections.sort(mediaInfoList, new FileComparator());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mediaListAdapter.refresh(mediaInfoList);
                        listDataSave.setDataList(type, mediaInfoList);
                    }
                });
            }
        }
        if (currentTab == tab) {
            if (mediaInfoList.size() != 0) {
                LogUtils.d(Constant.TAG, "tab : " + tab + "   mediaInfoList.size : " + mediaInfoList.size());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llEmpty.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    private String getFileType(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    private FFtask fFtask;

    private void getFileInfo(final String[] cmd, final MediaInfo info, final MediaListAdapter mediaListAdapter, final List<MediaInfo> data, final String type, final ListDataSave listDataSave) {
//        if (!stilGetInfo){
//            return;
//        }
        if (fFmpeg.isCommandRunning(fFtask)) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    getFileInfo(cmd, info, mediaListAdapter, data, type, listDataSave);
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
                    if (!fFmpeg.isCommandRunning(fFtask)) {
                        listDataSave.setDataList(type, data);
                        mediaListAdapter.refresh(data);
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
    class FileComparator implements Comparator<MediaInfo> {

        @Override
        public int compare(MediaInfo mediaInfo1, MediaInfo mediaInfo2) {
            if (mediaInfo1.getLastModified() < mediaInfo2.getLastModified()) {
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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switchTab(msg.what);
            return true;
        }
    });

    public void switchTab(int position) {
        LogUtils.d(Constant.TAG, " switchTab position : " + position + " fromSuccess " + fromSuccess);
        if (fromSuccess) {
            mHandler.sendEmptyMessageDelayed(position, 200);
            fromSuccess = false;
            return;
        }
        if (refreshing) {
            mHandler.sendEmptyMessageDelayed(position, 200);
            return;
        }

        LogUtils.d(Constant.TAG, " switchTab position : " + position);
        switch (position) {
            case 3:
                if (llVideo == null) {
                    mHandler.sendEmptyMessageDelayed(position, 200);
                } else {
                    llVideo.callOnClick();
                    fromSuccess = true;
                }
                break;
            case 2:
                if (llVideo == null) {
                    mHandler.sendEmptyMessageDelayed(position, 200);
                } else {
                    llGif.callOnClick();
                    fromSuccess = true;
                }
                break;
            case 1:
                if (llVideo == null) {
                    mHandler.sendEmptyMessageDelayed(position, 200);
                } else {
                    llAudio.callOnClick();
                    fromSuccess = true;
                }
                break;
        }
    }

}
