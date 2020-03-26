package com.regan.saata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.regan.saata.R;
import com.regan.saata.bean.FuncInfo;

import java.util.List;

public class FuncAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    //上下文环境
    private Context mContext;
    //数据集合
    private List<FuncInfo> mData;
    private int TYPE_CENTER = 1002;
    private OnItemClickListener mItemClickListener;

    public FuncAdapter(Context context, List<FuncInfo> data) {
        this.mContext = context;
        this.mData = data;
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void refresh(List<FuncInfo> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CENTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.func_item, parent, false);
            view.setOnClickListener(this);
            return new FuncHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FuncHolder) {
            FuncInfo funcInfo = mData.get(position);
            ((FuncHolder) holder).bind(funcInfo);
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

    class FuncHolder extends RecyclerView.ViewHolder {
        ImageView ivFunc;
        TextView tvName;

        private FuncHolder(@NonNull View itemView) {
            super(itemView);
            ivFunc = itemView.findViewById(R.id.iv_func);
            tvName = itemView.findViewById(R.id.tv_name);
        }

        private void bind(FuncInfo info) {
            ivFunc.setImageResource(info.iconSrc);
            tvName.setText(info.name);
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
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick((Integer) view.getTag());
        }
    }
}
