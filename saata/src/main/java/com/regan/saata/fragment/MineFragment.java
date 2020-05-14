package com.regan.saata.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.activity.VideoShowActivity;
import com.regan.saata.activity.WebViewActivity;
import com.regan.saata.adapter.FuncAdapter;
import com.regan.saata.bean.FuncInfo;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.SharedPrefrencesUtil;
import com.regan.saata.view.NiceImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MineFragment extends Fragment implements View.OnClickListener {
    private RecyclerView rlFunc;
    private TextView tvLogout;
    private TextView tvName;
    private TextView tvVipName, tvVipDec, tvOpenDec;
    private ImageView ivOpenVip;
    private NiceImageView ivIcon;
    private String userId;
    private String memberType;
    private LinearLayout llOpenVip;
    private Dialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mine, container, false);

        init(root);
        return root;
    }

    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
//        mParam = getArguments().getString(ARG_PARAM);  //获取activity穿过来的参数
    }

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        Bundle bundle = new Bundle();
//        bundle.putString(ARG_PARAM, str);
        fragment.setArguments(bundle);   //设置参数
        return fragment;
    }

    private void init(View root) {
        tvName = root.findViewById(R.id.tv_name);
        ivIcon = root.findViewById(R.id.iv_icon);
        tvVipName = root.findViewById(R.id.tv_vip_name);
        tvVipDec = root.findViewById(R.id.tv_vip_dec);
        tvOpenDec = root.findViewById(R.id.tv_open_dec);
        llOpenVip = root.findViewById(R.id.ll_open_vip);
        ivOpenVip = root.findViewById(R.id.iv_open_vip);
        rlFunc = root.findViewById(R.id.rl_func);
        tvLogout = root.findViewById(R.id.tv_logout);
        tvLogout.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        tvLogout.setOnClickListener(this);
        ivIcon.setOnClickListener(this);
        llOpenVip.setOnClickListener(null);

        String loginTkoen = SharedPrefrencesUtil.getStringByKey(mActivity, SharedPrefrencesUtil.LOGIN_TOKEN);
        if (!TextUtils.isEmpty(loginTkoen)) {
            Constant.loginChange(true);
        }

        List<FuncInfo> list = new ArrayList<>();
        FuncInfo custom = new FuncInfo();
        custom.name = "联系客服";
        custom.iconSrc = R.drawable.mine_custom;
        list.add(custom);
        final FuncInfo agreement = new FuncInfo();
        agreement.name = "用户协议";
        agreement.iconSrc = R.drawable.mine_agreement;
        list.add(agreement);
        FuncInfo safe = new FuncInfo();
        safe.name = "隐私政策";
        safe.iconSrc = R.drawable.mine_safe;
        list.add(safe);
        FuncInfo version = new FuncInfo();
        version.name = "当前版本";
        version.iconSrc = R.drawable.mine_version;
        list.add(version);
        FuncAdapter funcAdapter = new FuncAdapter(mActivity, list);
        funcAdapter.setItemClickListener(new FuncAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                LogUtils.d(Constant.TAG, " position : " + position);
                switch (position) {
                    case 0:
                        startActivity(new Intent(mActivity, VideoShowActivity.class));
                        break;
                    case 1:
                        Intent agr = new Intent(mActivity, WebViewActivity.class);
                        agr.putExtra("url", Constant.USER_PROTOCOL);
                        mActivity.startActivity(agr);
                        break;
                    case 2:
                        Intent sa = new Intent(mActivity, WebViewActivity.class);
                        sa.putExtra("url", Constant.SAFE_PROTOCOL);
                        mActivity.startActivity(sa);
                        break;
                    case 3:
//                        run();
                        try {
                            PackageManager packageManager = mActivity.getPackageManager();
                            PackageInfo packageInfo = packageManager.getPackageInfo(mActivity.getPackageName(), 0);
                            String version = packageInfo.versionName;
                            Toast.makeText(mActivity, "当前版本：" + version, Toast.LENGTH_LONG).show();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

        rlFunc.setAdapter(funcAdapter);
        rlFunc.setLayoutManager(new GridLayoutManager(mActivity, 3));

//        httpManager = new HttpManager(mActivity);

//        httpManager.authCheck(new HttpRequest.ILoadFinish<String>() {
//            @Override
//            public void success(String message) {
//                LogUtils.d(Constant.TAG, " authCheck : " + message);
//            }
//
//            @Override
//            public void fail(Throwable e) {
//
//            }
//
//            @Override
//            public void fail(String errorMsg) {
//
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private static final int SUCCESS = 1;
    private static final int FALL = 2;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //加载网络成功进行UI的更新,处理得到的图片资源
                case SUCCESS:
                    //通过message，拿到字节数组
                    byte[] Picture = (byte[]) msg.obj;
                    //使用BitmapFactory工厂，把字节数组转化为bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(Picture, 0, Picture.length);
                    //通过imageview，设置图片
                    ivIcon.setImageBitmap(bitmap);

                    break;
                //当加载网络失败执行的逻辑代码
                case FALL:
                    Toast.makeText(mActivity, "网络出现了问题", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_logout:
                logout();
                break;
            case R.id.iv_icon:
                break;
        }
    }

    private void logout() {
    }

}
