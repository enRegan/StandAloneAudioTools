package com.regan.saata.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.bean.MediaInfo;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.PxUtils;
import com.regan.saata.util.SharedPrefrencesUtil;

import java.io.File;
import java.util.List;

public class MediaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //上下文环境
    private Context mContext;
    //数据集合
    private List<MediaInfo> mData;
    //    private int TYPE_HEADER = 1001;//去除掉，诺挪外面了
    private int TYPE_CENTER = 1002;
    private int TYPE_EMPTY = 1003;

    public MediaListAdapter(Context context, List<MediaInfo> data) {
        this.mContext = context;
        this.mData = data;
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void refresh(List<MediaInfo> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if(viewType == TYPE_HEADER){
//            View view = LayoutInflater.from(mContext).inflate(R.layout.list_header, parent, false);
//            return new HeaderHolder(view);
//        }
        if (viewType == TYPE_CENTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            return new AudioHolder(view);
        }
        if (viewType == TYPE_EMPTY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_empty, parent, false);
            return new EmptyHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AudioHolder) {
            MediaInfo mediaInfo = mData.get(position);
            ((AudioHolder) holder).bind(mediaInfo, position);
            holder.itemView.setTag(position);
        }
//        if(holder instanceof HeaderHolder){
//            ((HeaderHolder)holder).bind("内部存储目录/audiotools/music");
//            holder.itemView.setTag(position);
//        }
        if (holder instanceof EmptyHolder) {
            ((EmptyHolder) holder).bind("暂无记录");
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //在第一个位置添加头
        if (mData.size() == 0) {
            return TYPE_EMPTY;
        }
//        else {
//            if (position==0){
//                return TYPE_HEADER;
//            }
//        }
        return TYPE_CENTER;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        TextView tvSrc;

        private HeaderHolder(@NonNull View itemView) {
            super(itemView);
            tvSrc = itemView.findViewById(R.id.tv_src);
        }

        private void bind(String src) {
            tvSrc.setText(src);
        }
    }

    class EmptyHolder extends RecyclerView.ViewHolder {
        TextView tvEmpty;

        private EmptyHolder(@NonNull View itemView) {
            super(itemView);
            tvEmpty = itemView.findViewById(R.id.tv_empty);
        }

        private void bind(String src) {
            tvEmpty.setText(src);
        }
    }

    class AudioHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvSize;
        TextView tvTime;
        TextView tvType;
        ImageView ivMore;
        private MediaInfo info;
        private int position;
        View all;

        private AudioHolder(@NonNull View itemView) {
            super(itemView);
            all = itemView;
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvSize = itemView.findViewById(R.id.tv_item_size);
            tvTime = itemView.findViewById(R.id.tv_item_time);
            tvType = itemView.findViewById(R.id.tv_item_type);
            ivMore = itemView.findViewById(R.id.iv_more);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFile(mContext, info);
                }
            });
            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initPopupWindow(view, info, position);
                }
            });
        }


        private void bind(MediaInfo info, int position) {
//            if (position == 0) {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtils.dp2px(mContext, 100));
//                params.topMargin = PxUtils.dp2px(mContext, 80);
//                params.bottomMargin = PxUtils.dp2px(mContext, 20);
//                all.setLayoutParams(params);
//            } else {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtils.dp2px(mContext, 100));
//                params.topMargin = PxUtils.dp2px(mContext, 0);
//                params.bottomMargin = PxUtils.dp2px(mContext, 20);
//                all.setLayoutParams(params);
//            }
            this.info = info;
            this.position = position;
            tvName.setText(info.name);
            tvSize.setText(info.size);
            tvTime.setText(info.time);
            tvType.setText("格式：" + info.type);
        }
    }

    private void initPopupWindow(View v, final MediaInfo info, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_list, null, false);
        int viewHeight = PxUtils.dp2px(mContext, 165);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        DisplayMetrics dm = new DisplayMetrics();
        dm = mContext.getResources().getDisplayMetrics();
        float height = dm.heightPixels;
        float width = dm.widthPixels;
        LogUtils.d(Constant.TAG, "getLocationOnScreen x :" + location[0] + " y : " + location[1]
                + "  viewHeight:" + viewHeight
                + "  height:" + height);
        if (height - location[1] > viewHeight) {
            view.setBackgroundResource(R.drawable.popup);
        } else {
            view.setBackgroundResource(R.drawable.popupup);
        }
        LinearLayout llShare = view.findViewById(R.id.ll_share);
        LinearLayout llSrc = view.findViewById(R.id.ll_src);
        LinearLayout llRename = view.findViewById(R.id.ll_rename);
        LinearLayout llDel = view.findViewById(R.id.ll_del);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(android.R.anim.fade_in);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        if (height - location[1] > viewHeight) {
            popupWindow.showAsDropDown(v, -50, 0);
        } else {
            popupWindow.showAsDropDown(v, -50, -(viewHeight + 50));
        }
//        popupWindow.showAtLocation(v, Gravity.RIGHT|Gravity.TOP, 50, 0);
        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allShare(info);
                popupWindow.dismiss();
            }
        });
        llSrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "内部存储目录/audiotools/music/" + info.getName(), Toast.LENGTH_SHORT).show();
//                openFile(mContext, info);
                popupWindow.dismiss();
            }
        });
        llRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1 = View.inflate(mContext, R.layout.rename_dialog, null);
                final EditText etNewName = view1.findViewById(R.id.et_new_name);
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setTitle("请输入新名字")
                        .setView(view1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newName = etNewName.getText().toString() + info.getName().substring(info.getName().lastIndexOf("."));
                                if (rename(mContext, info, newName)) {
                                    Toast.makeText(mContext, "重命名成功", Toast.LENGTH_SHORT).show();
                                    MediaInfo renameInfo = mData.get(position);
                                    renameInfo.setName(newName);
                                    mData.set(position, renameInfo);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mContext, "命名重复", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
                popupWindow.dismiss();
            }
        });
        llDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("是否确定删除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (deleteFile(mContext, info)) {
                                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                                    mData.remove(info);
                                    notifyDataSetChanged();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                popupWindow.dismiss();
            }
        });
        popupWindow.setOutsideTouchable(true);
    }

    /**
     * Android原生分享功能
     * 默认选取手机所有可以分享的APP
     */
    public void allShare(MediaInfo info) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("*/*");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "分享");//添加分享内容标题
        File cameraPhoto = new File(Constant.getFilePath() + info.getName());
        Uri photoUri = FileProvider.getUriForFile(
                mContext,
                mContext.getPackageName() + ".fileprovider",
                cameraPhoto);
        share_intent.putExtra(Intent.EXTRA_STREAM, photoUri);//添加分享内容
//        share_intent.putExtra(Intent.EXTRA_FROM_STORAGE, "分享给你一首歌" + name);//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "分享到:");
        mContext.startActivity(share_intent);
    }

    /*
     * 使用自定义方法打开文件
     */
    public void openFile(Context context, MediaInfo info) {
        Intent intent = new Intent();
        File cameraPhoto = new File(Constant.getFilePath() + info.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //  此处注意替换包名，
            Uri contentUri = FileProvider.getUriForFile(
                    context,
                    mContext.getPackageName() + ".fileprovider",
                    cameraPhoto);
            LogUtils.d(Constant.TAG, " uri   " + contentUri.getPath());
            intent.setDataAndType(contentUri, "audio/*");
//            intent.setDataAndType(contentUri, "image/*");
        } else {
            intent.setDataAndType(Uri.fromFile(cameraPhoto), "audio/*");//也可使用 Uri.parse("file://"+file.getAbsolutePath());
        }

        //以下设置都不是必须的
        intent.setAction(Intent.ACTION_VIEW);// 系统根据不同的Data类型，通过已注册的对应Application显示匹配的结果。
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//系统会检查当前所有已创建的Task中是否有该要启动的Activity的Task
        //若有，则在该Task上创建Activity；若没有则新建具有该Activity属性的Task，并在该新建的Task上创建Activity。
        intent.addCategory(Intent.CATEGORY_DEFAULT);//按照普通Activity的执行方式执行
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    public boolean rename(Context context, MediaInfo info, String newName) {
        File newFile = new File(Constant.getFilePath() + newName);
        if (newFile.exists()) {
            return false;
        }
        File oleFile = new File(Constant.getFilePath() + info.getName());
        //执行重命名
        if (oleFile.renameTo(newFile)) {
            oleFile.delete();
            return true;
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param context 上下文
     * @param info    audio信息
     * @return
     */
    public static boolean deleteFile(Context context, MediaInfo info) {
        File file = new File(Constant.getFilePath() + info.getName());
        if (file.exists()) {
            file.delete();
            SharedPrefrencesUtil.saveLongByKey(context, "lastModifiedTime", file.lastModified());
            return true;
        }
        return false;
    }

}
