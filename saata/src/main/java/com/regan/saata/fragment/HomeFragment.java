package com.regan.saata.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.activity.MainActivity;
import com.regan.saata.activity.Video2GifActivity;
import com.regan.saata.activity.VideoExtractActivity;
import com.regan.saata.activity.VideoTranscodeActivity;
import com.regan.saata.adapter.BannerPagerAdapter;
import com.regan.saata.adapter.HomeItemAdapter;
import com.regan.saata.bean.BaseItemInfo;
import com.regan.saata.util.LogUtils;
import com.regan.saata.view.ViewPagerIndicatorView;
import com.regan.saata.view.MyViewPager;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

/**
 * @author regan
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private final int PICK_VIDEO_REQUEST = 0x2;

    private MyViewPager myBanner;// Banner
    private List<Integer> bannerList = new ArrayList<>();// Banner列表
    private BannerPagerAdapter mAdapter;
    private ViewPagerIndicatorView bannerIndicator;// Banner中的指示器
    private Timer bannerTimer;// 每隔一段时间自动切换Banner

    private RecyclerView rvAudio;
    private RecyclerView rvGif;
    private RecyclerView rvVideo;
    private String audioType;
    private String videoType;
    private int mediaType;//发起多媒体选择类型 1转音频 2转gif 3转其他格式视频

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Activity mActivity;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(Constant.TAG, "onAttach");
        mActivity = (Activity) context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(Constant.TAG, "onCreateView");
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initBanner(root);
        rvAudio = root.findViewById(R.id.rv_audio);
        rvGif = root.findViewById(R.id.rv_gif);
        rvVideo = root.findViewById(R.id.rv_video);

        List<BaseItemInfo> list = new ArrayList<>();
        BaseItemInfo wav = new BaseItemInfo();
        wav.name = "视频转WAV";
        wav.iconSrc = R.drawable.audio_wav;
        list.add(wav);
        final BaseItemInfo wma = new BaseItemInfo();
        wma.name = "视频转WMA";
        wma.iconSrc = R.drawable.audio_wma;
        list.add(wma);
        BaseItemInfo aac = new BaseItemInfo();
        aac.name = "视频转AAC";
        aac.iconSrc = R.drawable.audio_aac;
        list.add(aac);
        BaseItemInfo m4a = new BaseItemInfo();
        m4a.name = "视频转M4A";
        m4a.iconSrc = R.drawable.audio_m4a;
        list.add(m4a);
        BaseItemInfo mp3 = new BaseItemInfo();
        mp3.name = "视频转MP3";
        mp3.iconSrc = R.drawable.audio_mp3;
        list.add(mp3);
        BaseItemInfo flac = new BaseItemInfo();
        flac.name = "视频转FLAC";
        flac.iconSrc = R.drawable.audio_flac;
        list.add(flac);
        HomeItemAdapter audioItemAdapter = new HomeItemAdapter(mActivity, list);
        audioItemAdapter.setItemClickListener(new HomeItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                LogUtils.d(Constant.TAG, " position : " + position);
                switch (position) {
                    case 0://wav
                        audioType = "wav";
                        break;
                    case 1://wma
                        audioType = "wma";
                        break;
                    case 2://aac
                        audioType = "aac";
                        break;
                    case 3://m4a
                        audioType = "m4a";
                        break;
                    case 4://mp3
                        audioType = "mp3";
                        break;
                    case 5://flac
                        audioType = "flac";
                        break;
                }
                mediaType = 1;
                Matisse.from(getActivity())
                        .choose(MimeType.ofVideo())
                        .countable(true)
                        .maxSelectable(1)
                        //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                        .showSingleMediaType(true)
//                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.8f)
                        .imageEngine(new GlideEngine())
                        .showPreview(true) // Default is `true`
                        .forResult(PICK_VIDEO_REQUEST);
            }
        });

        List<BaseItemInfo> list1 = new ArrayList<>();
        BaseItemInfo gif = new BaseItemInfo();
        gif.name = "视频转GIF";
        gif.iconSrc = R.drawable.gif_gif;
        list1.add(gif);
        HomeItemAdapter gifItemAdapter = new HomeItemAdapter(mActivity, list1);
        gifItemAdapter.setItemClickListener(new HomeItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                LogUtils.d(Constant.TAG, " position : " + position);
                mediaType = 2;
                Matisse.from(getActivity())
                        .choose(MimeType.ofVideo())
                        .countable(true)
                        .maxSelectable(1)
                        //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                        .showSingleMediaType(true)
//                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.8f)
                        .imageEngine(new GlideEngine())
                        .showPreview(true) // Default is `true`
                        .forResult(PICK_VIDEO_REQUEST);
            }
        });


        List<BaseItemInfo> list2 = new ArrayList<>();
        BaseItemInfo mp4 = new BaseItemInfo();
        mp4.name = "视频转MP4";
        mp4.iconSrc = R.drawable.video_mp4;
        list2.add(mp4);
        final BaseItemInfo avi = new BaseItemInfo();
        avi.name = "视频转AVI";
        avi.iconSrc = R.drawable.video_avi;
        list2.add(avi);
        BaseItemInfo wmv = new BaseItemInfo();
        wmv.name = "视频转WMV";
        wmv.iconSrc = R.drawable.video_wmv;
        list2.add(wmv);
        HomeItemAdapter videoItemAdapter = new HomeItemAdapter(mActivity, list2);
        videoItemAdapter.setItemClickListener(new HomeItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                LogUtils.d(Constant.TAG, " position : " + position);
                switch (position) {
                    case 0:
                        videoType = "mp4";
                        break;
                    case 1:
                        videoType = "avi";
                        break;
                    case 2:
                        videoType = "wmv";
                        break;
                }
                mediaType = 3;
                Matisse.from(getActivity())
                        .choose(MimeType.ofVideo())
                        .countable(true)
                        .maxSelectable(1)
                        //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                        .showSingleMediaType(true)
//                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.8f)
                        .imageEngine(new GlideEngine())
                        .showPreview(true) // Default is `true`
                        .forResult(PICK_VIDEO_REQUEST);
            }
        });

        rvAudio.setAdapter(audioItemAdapter);
        rvAudio.setLayoutManager(new GridLayoutManager(mActivity, 2));
        rvGif.setAdapter(gifItemAdapter);
        rvGif.setLayoutManager(new GridLayoutManager(mActivity, 2));
        rvVideo.setAdapter(videoItemAdapter);
        rvVideo.setLayoutManager(new GridLayoutManager(mActivity, 2));
        Log.d(Constant.TAG, "onCreateView done");
        return root;
    }

    public static HomeFragment newInstance() {
        Log.d(Constant.TAG, "newInstance");
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
//        bundle.putString(ARG_PARAM, str);
        fragment.setArguments(bundle);   //设置参数
        return fragment;
    }

    private void initBanner(View root) {
        // 初始化广告栏（ViewPager+指示器）
        myBanner = root.findViewById(R.id.banner_viewpager);
        bannerIndicator = root.findViewById(R.id.banner_indicator);
        bannerList.add(R.drawable.home_banner_1);
        bannerList.add(R.drawable.home_banner_2);
        bannerList.add(R.drawable.home_banner_1);
        mAdapter = new BannerPagerAdapter(getActivity(), myBanner, bannerList);
        myBanner.setAdapter(mAdapter);

        bannerIndicator.setViewPager(myBanner);
        // 广告轮播
        try {
            if (bannerTimer == null) {
                bannerTimer = new Timer();
                bannerTimer.schedule(new MTimerTask(), 0, 5000);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.ll_audio_f_video:
////                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////                intent.setType("video/*");
////                startActivityForResult(intent, PICK_VIDEO_REQUEST);
//                Matisse.from(getActivity())
//                        .choose(MimeType.ofAll())
//                        .countable(true)
//                        .maxSelectable(9)
////                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
////                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
//                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                        .thumbnailScale(0.85f)
//                        .imageEngine(new GlideEngine())
//                        .showPreview(false) // Default is `true`
//                        .forResult(PICK_VIDEO_REQUEST);
//                break;
//            case R.id.ll_audio_trans:
//                ((MainActivity) mActivity).setStilGetInfo();
//                startActivityForResult(new Intent(mActivity, AudioTranscodeActivity.class), MainActivity.CODE_TO_FUNC);
//                break;
//            case R.id.ll_audio_cut:
//                ((MainActivity) mActivity).setStilGetInfo();
//                startActivityForResult(new Intent(mActivity, AudioCut2Activity.class), MainActivity.CODE_TO_FUNC);
//                break;
//            case R.id.ll_audio_volume:
//                ((MainActivity) mActivity).setStilGetInfo();
//                startActivityForResult(new Intent(mActivity, AudioVolumeActivity.class), MainActivity.CODE_TO_FUNC);
//                break;
//            case R.id.ll_audio_speed:
//                ((MainActivity) mActivity).setStilGetInfo();
//                startActivityForResult(new Intent(mActivity, AudioSpeedActivity.class), MainActivity.CODE_TO_FUNC);
//                break;
//            case R.id.ll_audio_merge:
//                ((MainActivity) mActivity).setStilGetInfo();
//                startActivityForResult(new Intent(mActivity, AudioMergeActivity.class), MainActivity.CODE_TO_FUNC);
//                break;
        }
    }

    private Handler timerHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            myBanner.setCurrentItem(msg.what);
        }
    };

    private class MTimerTask extends TimerTask {

        @Override
        public void run() {
            int count = mAdapter.getCount();
            // 大于3项才循环轮播
            if (count > 3) {
                int currItem = myBanner.getCurrentItem();
                currItem = currItem % (count - 2) + 1;
                // 只能在主线程操作UI
                timerHandler.sendEmptyMessage(currItem);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(Constant.TAG, "onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(Constant.TAG, "onActivityCreated");
    }

    public void toFunction(int requestCode, int resultCode, @Nullable Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }

    List<Uri> mSelected;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(Constant.TAG, " homefragement onActivityResult : " + requestCode);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && null != data) {
            mSelected = Matisse.obtainResult(data);
            Uri selectedVideo = mSelected.get(0);
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

//            Cursor cursor = mActivity.getContentResolver().query(selectedVideo,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
            String path = "";
            if (DocumentsContract.isDocumentUri(mActivity, selectedVideo)) {
                // 如果是document类型的Uri，则通过document id处理
                String docId = DocumentsContract.getDocumentId(selectedVideo);
                if ("com.android.providers.media.documents".equals(selectedVideo.getAuthority())) {
                    String id = docId.split(":")[1]; // 解析出数字格式的id
                    String selection = MediaStore.Video.Media._ID + "=" + id;
                    path = getPathFromUri(mActivity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(selectedVideo.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    path = getPathFromUri(mActivity, contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(selectedVideo.getScheme())) {
                // 如果是content类型的Uri，则使用普通方式处理
                path = getPathFromUri(mActivity, selectedVideo, null);
            } else if ("file".equalsIgnoreCase(selectedVideo.getScheme())) {
                // 如果是file类型的Uri，直接获取图片路径即可
                path = selectedVideo.getPath();
            }
            String mVideoPath = path;
            String mOutPath;
//            Intent extract = new Intent(mActivity, AudioExtractActivity.class);
//            extract.putExtra("mVideoPath", mVideoPath);
//            extract.putExtra("mOutPath", mOutPath);
//            startActivityForResult(extract, MainActivity.CODE_TO_FUNC);
            switch (mediaType) {
                case 1:
                    mOutPath = Constant.getFilePath() + "音频提取_" + System.currentTimeMillis();
                    Intent extract = new Intent(mActivity, VideoExtractActivity.class);
                    extract.putExtra("mVideoPath", mVideoPath);
                    extract.putExtra("mOutPath", mOutPath);
                    extract.putExtra("mOutType", audioType);
                    startActivityForResult(extract, MainActivity.CODE_TO_FUNC);
                    break;
                case 2:
                    mOutPath = Constant.getFilePath() + "gif_" + System.currentTimeMillis();
                    Intent gif = new Intent(mActivity, Video2GifActivity.class);
                    gif.putExtra("mVideoPath", mVideoPath);
                    gif.putExtra("mOutPath", mOutPath);
                    startActivityForResult(gif, MainActivity.CODE_TO_FUNC);
                    break;
                case 3:
                    mOutPath = Constant.getFilePath() + "视频转格式_" + System.currentTimeMillis();
                    Intent video = new Intent(mActivity, VideoTranscodeActivity.class);
                    video.putExtra("mVideoPath", mVideoPath);
                    video.putExtra("mOutPath", mOutPath);
                    video.putExtra("mOutType", videoType);
                    startActivityForResult(video, MainActivity.CODE_TO_FUNC);
                    break;
            }
            ((MainActivity) mActivity).setStilGetInfo();

        }
//        if (requestCode != PICK_VIDEO_REQUEST && requestCode != PICK_AUDIO_MERGE_REQUEST && requestCode != MainActivity.CODE_TO_FUNC && resultCode == RESULT_OK && null != data) {
//            Uri selectedAudio = data.getData();
//            String[] filePathColumn = {MediaStore.Audio.Media.DATA};
//
//            Cursor cursor = mActivity.getContentResolver().query(selectedAudio,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            for (int j = 0; j < filePathColumn.length; j++) {
//                Log.d(Constant.TAG, filePathColumn[j]);
//            }
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String mAudioPath = cursor.getString(columnIndex);
//            String mAudioName = "";
//            String mOutType = "";
//            if (!TextUtils.isEmpty(mAudioPath)) {
//                Log.d(Constant.TAG, "mAudioPath  " + mAudioPath);
////                Log.d(Constant.TAG, "aaa  " + mAudioPath.substring(mAudioPath.lastIndexOf("/")));
//                mAudioName = mAudioPath.substring(mAudioPath.lastIndexOf("/") + 1, mAudioPath.lastIndexOf("."));
//                mOutType = mAudioPath.substring(mAudioPath.lastIndexOf("."));
//            }
//            cursor.close();
//            String mOutPath = Constant.getFilePath() + mAudioName;
//            Intent intent = new Intent();
//            switch (requestCode) {
//                case PICK_AUDIO_TRANSCODE_REQUEST:
//                    intent.setClass(mActivity, AudioTranscodeActivity.class);
//                    break;
//                case PICK_AUDIO_CUT_REQUEST:
//                    intent.setClass(mActivity, AudioCutActivity.class);
//                    break;
//                case PICK_AUDIO_VOLUME_REQUEST:
//                    intent.setClass(mActivity, AudioVolumeActivity.class);
//                    break;
//                case PICK_AUDIO_SPEED_REQUEST:
//                    intent.setClass(mActivity, AudioSpeedActivity.class);
//                    break;
//            }
//            intent.putExtra("mAudioPath", mAudioPath);
//            intent.putExtra("mOutPath", mOutPath);
//            intent.putExtra("mOutType", mOutType);
//            startActivityForResult(intent, MainActivity.CODE_TO_FUNC);
//        }
//        if (requestCode == PICK_AUDIO_MERGE_REQUEST && resultCode == RESULT_OK && null != data) {
//        }

        if (requestCode == MainActivity.CODE_TO_FUNC && resultCode == RESULT_OK && null != data) {
            if (data.getExtras() != null) {
                boolean toList = data.getBooleanExtra("toList", false);
                LogUtils.d(Constant.TAG, " toList : " + toList);
                if (toList) {
                    ((MainActivity) mActivity).switchList(mediaType);
                }
            }
        }
//        if (requestCode == PICK_AUDIO_CUT_REQUEST && resultCode == RESULT_OK && null != data) {
//            Uri selectedAudio = data.getData();
//            String[] filePathColumn = {MediaStore.Audio.Media.DATA};
//
//            Cursor cursor = mActivity.getContentResolver().query(selectedAudio,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            for (int j = 0; j < filePathColumn.length; j++){
//                Log.d(Constant.TAG, filePathColumn[j]);
//            }
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String mAudioPath = cursor.getString(columnIndex);
//            String mAudioName = "";
//            if(!TextUtils.isEmpty(mAudioPath)){
//                Log.d(Constant.TAG, "mAudioPath  " + mAudioPath);
//                Log.d(Constant.TAG, "aaa  " + mAudioPath.substring(mAudioPath.lastIndexOf("/")));
//                mAudioName = mAudioPath.substring(mAudioPath.lastIndexOf("/") + 1, mAudioPath.lastIndexOf("."));
//            }
//            cursor.close();
//            String mOutPath = Constant.getFilePath() + mAudioName;
//            Intent extract = new Intent(mActivity, AudioTranscodeActivity.class);
//            extract.putExtra("mAudioPath", mAudioPath);
//            extract.putExtra("mOutPath", mOutPath);
//            startActivity(extract);
//        }
    }

    /**
     * 通过Uri和selection来获取真实的图片路径
     *
     * @param act
     * @param uri
     * @param selection
     * @return
     */
    private static String getPathFromUri(Activity act, Uri uri, String selection) {
        String path = null;
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = act.getContentResolver().query(uri, projection, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
