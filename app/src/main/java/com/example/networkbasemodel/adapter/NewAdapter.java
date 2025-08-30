package com.example.networkbasemodel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networkbasemodel.R;
import com.example.networkbasemodel.bean.NewBean;

import java.util.List;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {

    private Context mContext;

    private OnItemClickListener mListener;
    private List<NewBean> mData;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public NewAdapter(Context context, @Nullable List<NewBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewBean newBean = mData.get(position);
        Glide.with(mContext)  // 绑定生命周期（Activity/Fragment/View）
                .load(newBean.getThumbnail_pic_s())  // 图片URL或资源ID
                .placeholder(R.mipmap.ic_launcher)  // 加载中占位图
                .error(R.mipmap.ic_launcher)  // 加载失败图
                .into(holder.imageView);  // 目标ImageView
        holder.titleTextView.setText(newBean.getTitle());
        holder.dataTextView.setText(newBean.getDate());
        holder.authorTextView.setText(newBean.getAuthor_name());
        if (mListener != null) {
            holder.itemView.setOnClickListener(v -> {
                mListener.onItemClick(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView, dataTextView, authorTextView;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.tv_title);
            dataTextView = view.findViewById(R.id.tv_data);
            authorTextView = view.findViewById(R.id.tv_author);
            imageView = view.findViewById(R.id.new_pic_iv);

        }
    }
}
