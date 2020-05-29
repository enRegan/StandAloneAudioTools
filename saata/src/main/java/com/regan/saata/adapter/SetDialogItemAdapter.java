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
import com.regan.saata.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class SetDialogItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    //上下文环境
    private Context mContext;
    //数据集合
    private List<String> mData;
    private List<SetHolder> viewList;
    private int TYPE_CENTER = 1002;
    private OnItemClickListener mItemClickListener;
    private int position = 0;

    public SetDialogItemAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.mData = data;
        viewList = new ArrayList<>();
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void refresh(List<String> data, int position) {
        this.mData = data;
        this.position = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CENTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.set_item, parent, false);
            return new SetHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LogUtils.d(Constant.TAG, " SetDialogItemAdapter refresh");
        if (holder instanceof SetHolder) {
            String string = mData.get(position);
            ((SetHolder) holder).bind(string);
            ((SetHolder) holder).ivSet.setTag(position);
            viewList.add((SetHolder) holder);
            if (this.position == position) {
                ((SetHolder) holder).ivSet.setSelected(true);
                ((SetHolder) holder).ivSet.callOnClick();
                ((SetHolder) holder).tvText.setTextColor(mContext.getResources().getColor(R.color.white));
            }
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

    class SetHolder extends RecyclerView.ViewHolder {
        ImageView ivSet;
        TextView tvText;

        private SetHolder(@NonNull View itemView) {
            super(itemView);
            ivSet = itemView.findViewById(R.id.iv_set);
            tvText = itemView.findViewById(R.id.tv_text);
        }

        private void bind(String text) {
            tvText.setText(text);
            ivSet.setOnClickListener(SetDialogItemAdapter.this);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        for (int i = 0; i < viewList.size(); i++) {
            if (view.getTag() == viewList.get(i).ivSet.getTag()) {
                viewList.get(i).ivSet.setSelected(true);
                viewList.get(i).tvText.setTextColor(mContext.getResources().getColor(R.color.white));
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick((Integer) view.getTag());
                }
            } else {
                viewList.get(i).ivSet.setSelected(false);
                viewList.get(i).tvText.setTextColor(mContext.getResources().getColor(R.color.text_b6b));
            }
        }
//        view.setSelected(true);
//        if (mItemClickListener != null) {
//            mItemClickListener.onItemClick((Integer) view.getTag());
//        }
    }


}
