package com.liucj.jetpack_base.ui.sofa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

import com.liucj.jetpack_base.model.ppjor.Feed;
import com.liucj.jetpack_base.ui.adapter.FeedAdapter;
import com.liucj.lib_common.fragment.BaseJetPackListFragment;
import com.liucj.lib_common.livedata.LiveDataBus;
import com.liucj.lib_navannotation.FragmentDestination;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SofaTapFragment extends BaseJetPackListFragment<Feed,SofaViewModel> {

    private String feedType;

    public static SofaTapFragment newInstance(String feedType) {
        Bundle args = new Bundle();
        args.putString("feedType", feedType);
        SofaTapFragment fragment = new SofaTapFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void afterCreateView() {
        mViewModel.getCacheLiveData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                submitList(feeds);
            }
        });
        mViewModel.setFeedType(feedType);
//        mViewModel.getDataSource().invalidate();
    }

    @Override
    public PagedListAdapter getAdapter() {
        feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(getContext(),feedType);
    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        mViewModel.getDataSource().invalidate();
    }
}