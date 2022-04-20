package com.liucj.lib_common.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.liucj.lib_common.R;
import com.liucj.lib_common.databinding.LayoutRefreshViewBinding;
import com.liucj.lib_common.view.EmptyView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class BaseJetPackList1Fragment<T, M extends BaseViewHolder> extends Fragment implements OnRefreshListener, OnLoadMoreListener {
    private LayoutRefreshViewBinding binding;
    protected RecyclerView mRecyclerView;
    protected SmartRefreshLayout mRefreshLayout;
    protected EmptyView mEmptyView;
    protected BaseQuickAdapter<T, M> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutRefreshViewBinding.inflate(inflater, container, false);
        binding.getRoot().setFitsSystemWindows(true);
        mRecyclerView = binding.recyclerView;
        mRefreshLayout = binding.refreshLayout;
        mEmptyView = binding.emptyView;

        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        adapter = getAdapter();
        if (adapter != null) {
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setItemAnimator(null);//取消动画
            DividerItemDecoration decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
            decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
            mRecyclerView.addItemDecoration(decoration);
        }
        afterCreateView();
        return binding.getRoot();

    }

    public void submitList(List<T> list) {
//        if (list.size() > 0) {
//            adapter.setList(list);
//        }
        if(adapter!=null){
            adapter.setList(list);
        }
        finishRefresh(list.size() > 0);
    }

    public void finishRefresh(boolean hasData) {
        RefreshState state = mRefreshLayout.getState();
        if (state.isFooter && state.isOpening) {
            mRefreshLayout.finishLoadMore();
        } else if (state.isHeader && state.isOpening) {
            mRefreshLayout.finishRefresh();
        }
        if (hasData) {
            mEmptyView.setVisibility(View.GONE);
        } else {
//            adapter.setEmptyView(mEmptyView);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }


    protected abstract void afterCreateView();

    public abstract BaseQuickAdapter<T, M> getAdapter();
}
