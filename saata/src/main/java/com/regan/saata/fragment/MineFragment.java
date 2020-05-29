package com.regan.saata.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.regan.saata.BuildConfig;
import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.activity.WebViewActivity;
import com.regan.saata.adapter.MineAdapter;
import com.regan.saata.bean.BaseItemInfo;
import com.regan.saata.util.LogUtils;
import com.regan.saata.view.MyContactDialog;
import com.regan.saata.view.NiceImageView;

import java.util.ArrayList;
import java.util.List;


public class MineFragment extends Fragment implements View.OnClickListener {
    private RecyclerView rlFunc;
    private NiceImageView ivIcon;

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
    }

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);   //设置参数
        return fragment;
    }

    private void init(View root) {
        ivIcon = root.findViewById(R.id.iv_icon);
        rlFunc = root.findViewById(R.id.rl_func);


        List<BaseItemInfo> list = new ArrayList<>();
        BaseItemInfo custom = new BaseItemInfo();
        custom.name = "联系客服";
        custom.iconSrc = R.drawable.mine_cs;
        list.add(custom);
        final BaseItemInfo agreement = new BaseItemInfo();
        agreement.name = "用户协议";
        agreement.iconSrc = R.drawable.mine_proc;
        list.add(agreement);
        BaseItemInfo safe = new BaseItemInfo();
        safe.name = "隐私政策";
        safe.iconSrc = R.drawable.mine_safe;
        list.add(safe);
        BaseItemInfo star = new BaseItemInfo();
        star.name = "给个好评";
        star.iconSrc = R.drawable.mine_star;
        list.add(star);
        BaseItemInfo version = new BaseItemInfo();
        version.name = "当前版本";
        version.iconSrc = R.drawable.mine_version;
        list.add(version);
        MineAdapter mineAdapter = new MineAdapter(mActivity, list);
        mineAdapter.setItemClickListener(new MineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                LogUtils.d(Constant.TAG, " position : " + position);
                switch (position) {
                    case 0:
                        Dialog myContactDialog = new MyContactDialog().getContactDialog(mActivity);
                        myContactDialog.show();
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
//                        try {
//                            PackageManager packageManager = mActivity.getPackageManager();
//                            PackageInfo packageInfo = packageManager.getPackageInfo(mActivity.getPackageName(), 0);
//                            String version = packageInfo.versionName;
//                            Toast.makeText(mActivity, "当前版本：" + version, Toast.LENGTH_LONG).show();
//                        } catch (PackageManager.NameNotFoundException e) {
//                            e.printStackTrace();
//                        }
                        try {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("market://details?id=" + "com.tencent.mm"));
                            startActivity(i);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "您的手机没有安装Android应用市场", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
//                        try{
//                            Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
//                            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        }catch(Exception e){
//                            Toast.makeText(getActivity(), "您的手机没有安装Android应用市场", Toast.LENGTH_SHORT).show();
//                            e.printStackTrace();
//                        }
                        break;
                    case 4:
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

        rlFunc.setAdapter(mineAdapter);
        rlFunc.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));

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
        }
    }

    void goRate() {
        String market = "market://details?id=" + BuildConfig.APPLICATION_ID;
        Uri uri = Uri.parse(market);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            String url = "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url)));
        }
    }

    /**
     * 获取已安装应用商店的包名列表
     * 获取有在AndroidManifest 里面注册<category android:name="android.intent.category.APP_MARKET" />的app
     *
     * @param context
     * @return
     */
    public ArrayList<String> getInstallAppMarkets(Context context) {
        //默认的应用市场列表，有些应用市场没有设置APP_MARKET通过隐式搜索不到
        ArrayList<String> pkgList = new ArrayList<>();
        pkgList.add("com.xiaomi.market");
        pkgList.add("com.qihoo.appstore");
        pkgList.add("com.wandoujia.phoenix2");
        pkgList.add("com.tencent.android.qqdownloader");
        pkgList.add("com.taptap");
        ArrayList<String> pkgs = new ArrayList<String>();
        if (context == null)
            return pkgs;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        if (infos == null || infos.size() == 0)
            return pkgs;
        int size = infos.size();
        for (int i = 0; i < size; i++) {
            String pkgName = "";
            try {
                ActivityInfo activityInfo = infos.get(i).activityInfo;
                pkgName = activityInfo.packageName;


            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(pkgName))
                pkgs.add(pkgName);

        }
        //取两个list并集,去除重复
        pkgList.removeAll(pkgs);
        pkgs.addAll(pkgList);
        return pkgs;
    }
}
