package com.liucj.jetpack_base.ui.sofa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedListAdapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.liucj.jetpack_base.R;
import com.liucj.jetpack_base.model.ppjor.Feed;
import com.liucj.jetpack_base.ui.adapter.Feed1Adapter;
import com.liucj.jetpack_base.ui.adapter.FeedAdapter;
import com.liucj.jetpack_base.ui.dashboard.DashboardViewModel;
import com.liucj.jetpack_base.view.ListPlayerView;
import com.liucj.jetpack_base.view.exoplayer.PageListPlayDetector;
import com.liucj.lib_common.fragment.BaseJetPackList1Fragment;
import com.liucj.lib_common.fragment.BaseJetPackListFragment;
import com.liucj.lib_common.livedata.LiveDataBus;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SofaTap1Fragment extends BaseJetPackList1Fragment<Feed, BaseViewHolder> {
    private String feedType;
    private SofaViewModel sofaViewModel;
    private PageListPlayDetector playDetector;
    private List<Feed> feedList = new ArrayList<>();

    public static SofaTap1Fragment newInstance(String feedType) {
        Bundle args = new Bundle();
        args.putString("feedType", feedType);
        SofaTap1Fragment fragment = new SofaTap1Fragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void afterCreateView() {
        playDetector = new PageListPlayDetector(this, mRecyclerView);
        sofaViewModel =
                new ViewModelProvider(this).get(SofaViewModel.class);
        sofaViewModel.setFeedType(feedType);
        sofaViewModel.loadDatas(true);
        LiveDataBus.get().with("feedList"+feedType).observe(this, new Observer<List<Feed> >() {
            @Override
            public void onChanged(List<Feed> feeds) {
                feedList.addAll(feeds);
                adapter.setList(feedList);
                finishRefresh(feedList.size() > 0);
            }
        });
    }

    @Override
    public BaseQuickAdapter<Feed, BaseViewHolder> getAdapter() {
        feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        Feed1Adapter adapter = new Feed1Adapter(feedList, feedType) {
            @Override
            public void onViewAttachedToWindow(@NotNull BaseViewHolder holder) {
                if (holder.getItemViewType() == Feed.TYPE_VIDEO) {
                    ListPlayerView listPlayerView = holder.getView(R.id.list_player_view);
                    playDetector.addTarget(listPlayerView);
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
                if (holder.getItemViewType() == Feed.TYPE_VIDEO) {
                    ListPlayerView listPlayerView = holder.getView(R.id.list_player_view);
                    playDetector.removeTarget(listPlayerView);
                }
            }
        };
        return adapter;
    }


    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
        sofaViewModel.loadDatas(false);
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        feedList.clear();
        sofaViewModel.loadDatas(true);
    }
}