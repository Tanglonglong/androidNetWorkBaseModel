package com.example.networkbasemodel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networkbasemodel.adapter.NewAdapter;
import com.example.networkbasemodel.bean.NewBean;
import com.example.networkbasemodel.bean.NewResponse;
import com.example.networkbasemodel.network.NetWorkBus;
import com.example.networkmodel.observer.BaseObserver;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private int mPage = 1;
    private int mPageSize = 10;
    private int mIsFilter = 1;

    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private NewAdapter mNewAdapter;
    private List<NewBean> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = 1;
                mData.clear();
                getData();
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPage++;
                getData();
            }
        });
    }

    private void initListener() {
        mNewAdapter.setOnItemClickListener(new NewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                System.out.println("BBBBBBBBBBBBBBBBBBBBBBBB");
                Intent intent = new Intent(MainActivity.this, NewDetailsActivity.class);
                intent.putExtra("url", mData.get(position).getUrl());
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void initData() {
        mNewAdapter = new NewAdapter(this, mData);
        mRecyclerView.setAdapter(mNewAdapter);
        getData();
    }

    private void getData() {
        NetWorkBus.getNewsWithNetWork("top", mPage, mPageSize, mIsFilter, new BaseObserver<NewResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(NewResponse newResponse) {
                if (newResponse.getError_code() == 0) {
                    mData.addAll(newResponse.getResult().getData());
                    mNewAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "业务异常code：" + newResponse.getError_code(), Toast.LENGTH_SHORT).show();
                }
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }

            @Override
            public void onFailure(Throwable e) {
                //http 异常处理 TODO
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }
        });

    }


}