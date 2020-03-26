package com.regan.saata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.bean.AudioInfo;
import com.regan.saata.util.FileSizeUtil;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.TimeUtils;

import java.util.List;

public class AudioMergeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //上下文环境
    private Context mContext;
    //数据集合
    private List<AudioInfo> mData;
    private int TYPE_CENTER = 1002;

    public AudioMergeAdapter(Context context, List<AudioInfo> data) {
        this.mContext = context;
        this.mData = data;
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void refresh(List<AudioInfo> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CENTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.merge_item, parent, false);
            return new AudioHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AudioHolder) {
            AudioInfo audioInfo = mData.get(position);
            ((AudioHolder) holder).bind(audioInfo, position);
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_CENTER;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class AudioHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvSize;
        TextView tvTime;
        ImageView ivUp;
        ImageView ivDown;
        private AudioInfo info;
        private int position;

        private AudioHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvSize = itemView.findViewById(R.id.tv_item_size);
            tvTime = itemView.findViewById(R.id.tv_item_time);
            ivUp = itemView.findViewById(R.id.iv_up);
            ivDown = itemView.findViewById(R.id.iv_down);
        }


        private void bind(AudioInfo info, final int position) {
            this.info = info;
            this.position = position;
            if (position == 0) {
                ivUp.setClickable(false);
            } else if (position == mData.size() - 1) {
                ivDown.setClickable(false);
            } else {
                ivUp.setClickable(true);
                ivDown.setClickable(true);
            }
            tvName.setText(info.name);
            tvSize.setText(FileSizeUtil.getAutoFileOrFilesSize(info.getPath()));
            tvTime.setText(TimeUtils.secondToTime(Long.parseLong(info.getTime()) / 1000));
            ivUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        return;
                    }
                    LogUtils.d(Constant.TAG, " position : " + position);
                    mData.add(position - 1, mData.remove(position));
                    notifyDataSetChanged();
                }
            });
            ivDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == mData.size() - 1) {
                        return;
                    }
                    LogUtils.d(Constant.TAG, " position : " + position);
                    mData.add(position + 1, mData.remove(position));
                    notifyDataSetChanged();
                }
            });
        }
    }

}
