package com.liucj.jetpack_base.ui.sofa;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.alibaba.fastjson.TypeReference;
import com.liucj.jetpack_base.model.ppjor.Feed;
import com.liucj.lib_common.fragment.BaseViewModel;
import com.liucj.lib_common.livedata.LiveDataBus;
import com.liucj.lib_network.ApiResponse;
import com.liucj.lib_network.ApiService;
import com.liucj.lib_network.JsonCallBack;
import com.liucj.lib_network.Request;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SofaViewModel extends BaseViewModel<Feed> {

    private volatile boolean witchCache = true;
    private String mFeedType;
    private int pages = 1;
    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();
    @Override
    public DataSource createDataSource() {
        return mDataSource;
    }
    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return cacheLiveData;
    }

    public void setFeedType(String feedType) {
        mFeedType = feedType;
    }

    ItemKeyedDataSource<Integer, Feed> mDataSource = new ItemKeyedDataSource<Integer, Feed>() {
        @Override
        public void loadInitial(@NotNull LoadInitialParams<Integer> loadInitialParams, @NotNull LoadInitialCallback<Feed> callback) {
            //加载初始数据
            loadData(0, callback);
            witchCache = false;
        }

        @Override
        public void loadAfter(@NotNull LoadParams<Integer> params, @NotNull LoadCallback<Feed> callback) {
            loadData(params.key, callback);
        }

        @Override
        public void loadBefore(@NotNull LoadParams<Integer> loadParams, @NotNull LoadCallback<Feed> callback) {

        }

        @NotNull
        @Override
        public Integer getKey(@NotNull Feed feed) {
            return feed.id;
        }
    };

    public void loadData(int key, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", mFeedType)
                .addParam("userId", "1631678065")
                .addParam("feedId", key)
                .addParam("pageCount", 10)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());
        if (witchCache) {
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallBack<List<Feed>>() {
                @Override
                public void onSuccess(ApiResponse<List<Feed>> response) {
                    super.onSuccess(response);
                    Log.e("loadData", "onCacheSuccess: ");
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource<Feed>();
                    dataSource.data.addAll(response.body);

                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    cacheLiveData.postValue(pagedList);
                }
            });
        }
        try {
            Request netRequest = witchCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;

            callback.onResult(data);

            if (key > 0) {
                //上拉加载
                //通过BoundaryPageData发送数据 告诉UI层 是否应该主动关闭上拉加载分页的动画
                ((MutableLiveData) getBoundaryPageData()).postValue(data.size() > 0);
//                loadAfter.set(false);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        Log.e("loadData", "loadData: key:" + key);

    }

    public void loadDatas(boolean isRefresh) {
        pages = isRefresh ? 0 : pages+1;
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", mFeedType)
                .addParam("userId", "1631678065")
                .addParam("feedId", pages)
                .addParam("pageCount", 10)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());
        if (witchCache) {
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallBack<List<Feed>>() {
                @Override
                public void onSuccess(ApiResponse<List<Feed>> response) {
                    super.onSuccess(response);
                    Log.e("loadData", "onCacheSuccess: ");
                    List<Feed> data = response.body == null ? Collections.emptyList() : (List<Feed>) response.body;
                    LiveDataBus.get().with("feedList" + mFeedType)
                            .postValue(data);
                }
            });
        }
        try {
            Request netRequest = witchCache ? request.clone() : request;
            netRequest.cacheStrategy(pages == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            netRequest.execute(new JsonCallBack() {
                @Override
                public void onSuccess(ApiResponse response) {
                    List<Feed> data = response.body == null ? Collections.emptyList() : (List<Feed>) response.body;
                    LiveDataBus.get().with("feedList" + mFeedType)
                            .postValue(data);
                }

                @Override
                public void onError(ApiResponse response) {
                    super.onError(response);
                }
            });

//            if (key > 0) {
//                //上拉加载
//                //通过BoundaryPageData发送数据 告诉UI层 是否应该主动关闭上拉加载分页的动画
//                ((MutableLiveData) getBoundaryPageData()).postValue(data.size() > 0);
//                LiveDataBus.get().with("feedList")
//                        .postValue(data);
//            }
            Log.e("loadData", "loadData: key:" + pages);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

}