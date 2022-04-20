package com.liucj.lib_common.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liucj.lib_common.R;
import com.liucj.lib_common.databinding.LayoutRefreshViewBinding;
import com.liucj.lib_common.item.HiAdapter;
import com.liucj.lib_common.item.HiDataItem;
import com.liucj.lib_common.view.EmptyView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public  class BaseListFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {
    protected LayoutRefreshViewBinding binding;
    protected RecyclerView mRecyclerView;
    protected SmartRefreshLayout mRefreshLayout;
    protected EmptyView mEmptyView;
    protected DividerItemDecoration decoration;
    private HiAdapter adapter;
    public int pageIndex = 1;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = LayoutRefreshViewBinding.inflate(inflater, container, false);
        binding.getRoot().setFitsSystemWindows(true);
        mRecyclerView = binding.recyclerView;
        mRefreshLayout = binding.refreshLayout;
        mEmptyView = binding.emptyView;

        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadMore(true);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        adapter = getAdapter();
        if (adapter == null) {
            adapter = new HiAdapter(getContext());
        }
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(null);

        //默认给列表中的Item 一个 10dp的ItemDecoration
        decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        mRecyclerView.addItemDecoration(decoration);

        return binding.getRoot();
    }

    public void finishRefresh(List<? extends HiDataItem<?, ? extends RecyclerView.ViewHolder>> dataItems) {
        boolean success = !dataItems.isEmpty();
        //光真么判断还是不行的，我们还需要别的措施。。。因为可能会出现 下拉单时候，有执行了删上拉分页
        boolean refresh = pageIndex == 1;
        if (refresh) {
            mRefreshLayout.finishRefresh();
            if (success) {
                mEmptyView.setVisibility(View.GONE);
                adapter.clearItems();
                adapter.addItems(dataItems, true);
            } else {
                //此时就需要判断列表上是否已经有数据，如果么有，显示出空页面转态
                if (adapter.getItemCount() <= 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (success) {
                adapter.addItems(dataItems, true);
            }
            mRefreshLayout.finishLoadMore(success);
        }
    }


    /**
     * 因而 我们在 onCreateView的时候 创建了 PagedListAdapter
     * 所以，如果arguments 有参数需要传递到Adapter 中，那么需要在getAdapter()方法中取出参数。
     *
     * @return
     */
    public HiAdapter getAdapter() {
        return null;
    }


    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
        finish();
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        finish();
    }

    public void finish() {
        RefreshState state = mRefreshLayout.getState();
        if (state.isFooter && state.isOpening) {
            mRefreshLayout.finishLoadMore();
            pageIndex++;

        } else if (state.isHeader && state.isOpening) {
            mRefreshLayout.finishRefresh();
            pageIndex = 1;
        }
    }
}
