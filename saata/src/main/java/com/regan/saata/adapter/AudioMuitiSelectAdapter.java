package com.regan.saata.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.regan.saata.R;
import com.regan.saata.bean.AudioInfo;
import com.regan.saata.util.FileSizeUtil;
import com.regan.saata.util.TimeUtils;

import java.util.ArrayList;

public class AudioMuitiSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //上下文环境
    private Context mContext;
    //数据集合
    private ArrayList<AudioInfo> mData;
    private ArrayList<AudioInfo> selectedList;
    private int TYPE_CENTER = 1002;
    private int TYPE_EMPTY = 1003;
    private SparseBooleanArray mCheckStates = new SparseBooleanArray();
    private TextView tvConfirm;

    public AudioMuitiSelectAdapter(Context context, final ArrayList<AudioInfo> data, TextView tvConfirm, final CheckDone checkDone) {
        this.mContext = context;
        this.mData = data;
        this.tvConfirm = tvConfirm;
        selectedList = new ArrayList<>();
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDone.done(selectedList);
            }
        });
    }

    public interface CheckDone {
        void done(ArrayList<AudioInfo> data);
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void refresh(ArrayList<AudioInfo> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CENTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.muiti_select_item, parent, false);
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
            AudioInfo audioInfo = mData.get(position);
            ((AudioHolder) holder).bind(audioInfo, position);
            holder.itemView.setTag(position);
        }
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
        return TYPE_CENTER;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 1 : mData.size();
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
        CheckBox cbChoice;
        View itemView;
        RelativeLayout rlChoice;
        private AudioInfo info;
        private int position;

        private AudioHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvSize = itemView.findViewById(R.id.tv_item_size);
            tvTime = itemView.findViewById(R.id.tv_item_time);
            cbChoice = itemView.findViewById(R.id.cb_choice);
            rlChoice = itemView.findViewById(R.id.rl_choice);
        }


        private void bind(final AudioInfo info, final int position) {
            this.info = info;
            this.position = position;
            tvName.setText(info.getName());
            tvSize.setText(FileSizeUtil.getAutoFileOrFilesSize(info.getPath()));
            if (info.getTime() != null) {
                tvTime.setText(TimeUtils.secondToTime(Long.parseLong(info.getTime()) / 1000));
            }
            cbChoice.setTag(position);
            rlChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    LogUtils.d(Constant.TAG, "rlChoice position : " + position + " cbChoice.isChecked() " + cbChoice.isChecked());
                    cbChoice.performClick();
                }
            });
            cbChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    LogUtils.d(Constant.TAG, "cbChoice position : " + position + " cbChoice.isChecked() " + cbChoice.isChecked());
                    int pos = (int) cbChoice.getTag();
                    if (cbChoice.isChecked()) {
                        mCheckStates.put(pos, true);
                        selectedList.add(info);
                        //do something
                    } else {
                        mCheckStates.delete(pos);
                        selectedList.remove(info);
                        //do something else
                    }
                    if (mCheckStates.size() == 0) {
                        tvConfirm.setVisibility(View.GONE);
                    } else {
                        tvConfirm.setVisibility(View.VISIBLE);
                    }
                }
            });
            cbChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });
            cbChoice.setChecked(mCheckStates.get(position, false));
//            cbChoice.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(mContext, " position : " + position, Toast.LENGTH_SHORT).show();
//                }
//            });
//            rbChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(isChecked){
//                        Toast.makeText(mContext, " position : " + position, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }
    }

}
