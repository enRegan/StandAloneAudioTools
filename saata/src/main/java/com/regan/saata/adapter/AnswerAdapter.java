package com.regan.saata.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.regan.saata.R;
import com.regan.saata.bean.Answer;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //上下文环境
    private Context mContext;
    //数据集合
    private List<Answer> mData;
    private int TYPE_CENTER = 1002;

    public AnswerAdapter(Context context, List<Answer> data) {
        this.mContext = context;
        this.mData = data;
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void refresh(List<Answer> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CENTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.answer_item, parent, false);
            return new AnswerHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AnswerHolder) {
            Answer Answer = mData.get(position);
            ((AnswerHolder) holder).bind(Answer, position);
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_CENTER;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class AnswerHolder extends RecyclerView.ViewHolder {
        TextView tvAnswer;
        private Answer info;
        private int position;

        private AnswerHolder(@NonNull View itemView) {
            super(itemView);
            tvAnswer = itemView.findViewById(R.id.tv_answer);
        }


        private void bind(Answer info, int position) {
            this.info = info;
            this.position = position;
            String text;
            if ("暂无回复".equals(info.getAnswer())) {
                text = info.getAnswer();
                tvAnswer.setText(text);
            } else {
                text = "客服回复:" + info.getAnswer();
                SpannableStringBuilder ss = new SpannableStringBuilder(text);
                ss.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.feed_back_color_area)), 0, 5, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvAnswer.setText(ss);
            }
        }
    }

}
