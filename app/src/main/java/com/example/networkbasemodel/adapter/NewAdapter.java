package com.example.networkbasemodel.adapter;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.networkbasemodel.R;
import com.example.networkbasemodel.bean.NewBean;

import java.util.List;

public class NewAdapter extends BaseQuickAdapter<NewBean, BaseViewHolder> {

    private Context mContext;


    public NewAdapter(Context context, int layoutResId, @Nullable List<NewBean> data) {
        super(layoutResId, data);
        this.mContext = context;
    }

    public NewAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, NewBean newBean) {
        Glide.with(mContext)  // 绑定生命周期（Activity/Fragment/View）
                .load(newBean.getThumbnail_pic_s())  // 图片URL或资源ID
                .placeholder(R.mipmap.ic_launcher)  // 加载中占位图
                .error(R.mipmap.ic_launcher)  // 加载失败图
                .into((ImageView) baseViewHolder.getView(R.id.new_pic_iv));  // 目标ImageView
        baseViewHolder.setText(R.id.tv_title, newBean.getTitle());
        baseViewHolder.setText(R.id.tv_data, newBean.getDate());
        baseViewHolder.setText(R.id.tv_author, newBean.getAuthor_name());
    }
}
