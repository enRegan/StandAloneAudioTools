package com.regan.saata.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.activity.AudioCut2Activity;
import com.regan.saata.activity.AudioCutActivity;
import com.regan.saata.activity.AudioExtractActivity;
import com.regan.saata.activity.AudioMergeActivity;
import com.regan.saata.activity.AudioSpeedActivity;
import com.regan.saata.activity.AudioTranscodeActivity;
import com.regan.saata.activity.AudioVolumeActivity;
import com.regan.saata.activity.MainActivity;
import com.regan.saata.adapter.BannerPagerAdapter;
import com.regan.saata.util.LogUtils;
import com.regan.saata.view.IndicatorView;
import com.regan.saata.view.NestViewPager;

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
    private final int PICK_AUDIO_TRANSCODE_REQUEST = 0x3;
    private final int PICK_AUDIO_CUT_REQUEST = 0x4;
    private final int PICK_AUDIO_VOLUME_REQUEST = 0x5;
    private final int PICK_AUDIO_SPEED_REQUEST = 0x6;
    private final int PICK_AUDIO_MERGE_REQUEST = 0x7;
    private LinearLayout llExtract;
    private LinearLayout llAudioTranscode;
    private LinearLayout llAudioCut;
    private LinearLayout llAudioVolume;
    private LinearLayout llAudioSpeed;
    private LinearLayout llAudioMerge;

    private NestViewPager vpBanner;// Banner
    private List<Integer> bannerList = new ArrayList<>();// Banner列表
    private BannerPagerAdapter mAdapter;
    private IndicatorView bannerIndicator;// Banner中的指示器
    private Timer bannerTimer;// 每隔一段时间自动切换Banner

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

        llExtract = root.findViewById(R.id.ll_audio_f_video);
        llAudioTranscode = root.findViewById(R.id.ll_audio_trans);
        llAudioCut = root.findViewById(R.id.ll_audio_cut);
        llAudioVolume = root.findViewById(R.id.ll_audio_volume);
        llAudioSpeed = root.findViewById(R.id.ll_audio_speed);
        llAudioMerge = root.findViewById(R.id.ll_audio_merge);

        llExtract.setOnClickListener(this);
        llAudioTranscode.setOnClickListener(this);
        llAudioCut.setOnClickListener(this);
        llAudioVolume.setOnClickListener(this);
        llAudioSpeed.setOnClickListener(this);
        llAudioMerge.setOnClickListener(this);

//        Bitmap banner1 = BitmapFactory.decodeResource(getResources(), R.drawable.banner1);
//        Bitmap banner2 = BitmapFactory.decodeResource(getResources(), R.drawable.banner2);
//        ArrayList<Bitmap> banners = new ArrayList<>();
//        banners.add(banner1);
//        banners.add(banner2);
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
        vpBanner = root.findViewById(R.id.banner_viewpager);
        bannerIndicator = root.findViewById(R.id.banner_indicator);
        bannerList.add(R.drawable.banner1);
        bannerList.add(R.drawable.banner2);
        bannerList.add(R.drawable.banner1);
        mAdapter = new BannerPagerAdapter(getActivity(), vpBanner, bannerList);
        vpBanner.setAdapter(mAdapter);

        bannerIndicator.setViewPager(vpBanner);
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
            case R.id.ll_audio_f_video:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, PICK_VIDEO_REQUEST);
                break;
            case R.id.ll_audio_trans:
                ((MainActivity) mActivity).setStilGetInfo();
                startActivityForResult(new Intent(mActivity, AudioTranscodeActivity.class), MainActivity.CODE_TO_FUNC);
                break;
            case R.id.ll_audio_cut:
                ((MainActivity) mActivity).setStilGetInfo();
                startActivityForResult(new Intent(mActivity, AudioCut2Activity.class), MainActivity.CODE_TO_FUNC);
                break;
            case R.id.ll_audio_volume:
                ((MainActivity) mActivity).setStilGetInfo();
                startActivityForResult(new Intent(mActivity, AudioVolumeActivity.class), MainActivity.CODE_TO_FUNC);
                break;
            case R.id.ll_audio_speed:
                ((MainActivity) mActivity).setStilGetInfo();
                startActivityForResult(new Intent(mActivity, AudioSpeedActivity.class), MainActivity.CODE_TO_FUNC);
                break;
            case R.id.ll_audio_merge:
                ((MainActivity) mActivity).setStilGetInfo();
                startActivityForResult(new Intent(mActivity, AudioMergeActivity.class), MainActivity.CODE_TO_FUNC);
                break;
        }
    }

    private Handler timerHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            vpBanner.setCurrentItem(msg.what);
        }
    };

    private class MTimerTask extends TimerTask {

        @Override
        public void run() {
            int count = mAdapter.getCount();
            // 大于3项才循环轮播
            if (count > 3) {
                int currItem = vpBanner.getCurrentItem();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(Constant.TAG, " homefragement onActivityResult : " + requestCode);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && null != data) {
            Uri selectedVideo = data.getData();
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
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String mVideoPath = cursor.getString(columnIndex);
//            mVideoPath = "/sdcard/Download/gamepp/KSP.mkv";
//            cursor.close();
//            String mOutPath = Constant.getMusicPath() + MediaTool.getVideoName(mVideoPath);
            String mOutPath = Constant.getMusicPath() + "音频提取_" + System.currentTimeMillis();
            Intent extract = new Intent(mActivity, AudioExtractActivity.class);
            extract.putExtra("mVideoPath", mVideoPath);
            extract.putExtra("mOutPath", mOutPath);
//            startActivity(extract);
            startActivityForResult(extract, MainActivity.CODE_TO_FUNC);
        }
        if (requestCode != PICK_VIDEO_REQUEST && requestCode != PICK_AUDIO_MERGE_REQUEST && requestCode != MainActivity.CODE_TO_FUNC && resultCode == RESULT_OK && null != data) {
            Uri selectedAudio = data.getData();
            String[] filePathColumn = {MediaStore.Audio.Media.DATA};

            Cursor cursor = mActivity.getContentResolver().query(selectedAudio,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            for (int j = 0; j < filePathColumn.length; j++) {
                Log.d(Constant.TAG, filePathColumn[j]);
            }
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String mAudioPath = cursor.getString(columnIndex);
            String mAudioName = "";
            String mOutType = "";
            if (!TextUtils.isEmpty(mAudioPath)) {
                Log.d(Constant.TAG, "mAudioPath  " + mAudioPath);
//                Log.d(Constant.TAG, "aaa  " + mAudioPath.substring(mAudioPath.lastIndexOf("/")));
                mAudioName = mAudioPath.substring(mAudioPath.lastIndexOf("/") + 1, mAudioPath.lastIndexOf("."));
                mOutType = mAudioPath.substring(mAudioPath.lastIndexOf("."));
            }
            cursor.close();
            String mOutPath = Constant.getMusicPath() + mAudioName;
            Intent intent = new Intent();
            switch (requestCode) {
                case PICK_AUDIO_TRANSCODE_REQUEST:
                    intent.setClass(mActivity, AudioTranscodeActivity.class);
                    break;
                case PICK_AUDIO_CUT_REQUEST:
                    intent.setClass(mActivity, AudioCutActivity.class);
                    break;
                case PICK_AUDIO_VOLUME_REQUEST:
                    intent.setClass(mActivity, AudioVolumeActivity.class);
                    break;
                case PICK_AUDIO_SPEED_REQUEST:
                    intent.setClass(mActivity, AudioSpeedActivity.class);
                    break;
            }
            intent.putExtra("mAudioPath", mAudioPath);
            intent.putExtra("mOutPath", mOutPath);
            intent.putExtra("mOutType", mOutType);
            startActivityForResult(intent, MainActivity.CODE_TO_FUNC);
        }
        if (requestCode == PICK_AUDIO_MERGE_REQUEST && resultCode == RESULT_OK && null != data) {
        }

        if (requestCode == MainActivity.CODE_TO_FUNC && resultCode == RESULT_OK && null != data) {
            if (data.getExtras() != null) {
                boolean toList = data.getBooleanExtra("toList", false);
                LogUtils.d(Constant.TAG, " toList : " + toList);
                if (toList) {
                    ((MainActivity) mActivity).switchList();
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
//            String mOutPath = Constant.getMusicPath() + mAudioName;
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
