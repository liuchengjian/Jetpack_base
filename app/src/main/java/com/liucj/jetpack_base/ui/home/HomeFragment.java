package com.liucj.jetpack_base.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.liucj.jetpack_base.R;
import com.liucj.jetpack_base.api.ApiFactory;
import com.liucj.jetpack_base.api.HomeApi;
import com.liucj.jetpack_base.databinding.FragmentCategoryBinding;
import com.liucj.jetpack_base.databinding.FragmentHomeBinding;
import com.liucj.jetpack_base.model.HomeModel;
import com.liucj.jetpack_base.model.TabCategory;
import com.liucj.lib_common.AppGlobals;
import com.liucj.lib_common.fragment.BaseFragment;
import com.liucj.lib_common.livedata.LiveDataBus;
import com.liucj.lib_common.view.tab.common.IHiTabLayout;
import com.liucj.lib_common.view.tab.top.HiTabTopInfo;
import com.liucj.lib_navannotation.FragmentDestination;
import com.liucj.lib_network.restful_kt.HiCallback;
import com.liucj.lib_network.restful_kt.HiResponse;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.listener.OnBannerListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends BaseFragment {
    private int topTabSelectIndex = 0;
    private int DEFAULT_SELECT_INDEX = 0;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        clipBottomPadding(binding.getRoot());
//        binding.navigationBar.setNavListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().finish();
//            }
//        });
//        binding.navigationBar.setTitle("很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长");
//        binding.navigationBar.addRightTextButton("111",View.generateViewId());
//        binding.navigationBar.addRightTextButton("222",View.generateViewId());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        queryTabList();

        HomeViewModel viewModel =new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.queryCategoryTabs().observe(getViewLifecycleOwner(), new Observer<List<TabCategory>>() {
            @Override
            public void onChanged(List<TabCategory> tabCategories) {
                if(!tabCategories.isEmpty()){
                    updateUI(tabCategories);
                }
            }
        });
    }

    private void queryTabList() {
        ApiFactory.INSTANCE.create(HomeApi.class).
                queryTabList().
                enqueue(new HiCallback<List<TabCategory>>() {
                    @Override
                    public void onSuccess(@NotNull HiResponse<List<TabCategory>> response) {

                        if (response.successful() && response.getData() != null) {
                            List<TabCategory> data = response.getData();
                            updateUI(data);
                        }
                    }

                    @Override
                    public void onFailed(@NotNull Throwable throwable) {

                    }
                });
    }

    private void updateUI(List<TabCategory> data) {
        //需要小心处理  ---viewmodel+livedata
        if (!isAlive()) return;
        List<HiTabTopInfo<?>> topTabs = new ArrayList();
        for (TabCategory tabCategory : data) {
            int defaultColor = ContextCompat.getColor(getContext(), R.color.color_333);
            int selectColor = ContextCompat.getColor(getContext(), R.color.color_dd2);
            HiTabTopInfo<?> tabTopInfo = new HiTabTopInfo(tabCategory.getCategoryName(), defaultColor, selectColor);

            topTabs.add(tabTopInfo);
        }
        binding.topTabLayout.inflateInfo(topTabs);
        binding.topTabLayout.defaultSelected(topTabs.get(DEFAULT_SELECT_INDEX));
        binding.topTabLayout.addTabSelectedChangeListener(new IHiTabLayout.OnTabSelectedListener<HiTabTopInfo<?>>() {
            @Override
            public void onTabSelectedChange(int index, @Nullable @org.jetbrains.annotations.Nullable HiTabTopInfo<?> prevInfo, @NonNull @NotNull HiTabTopInfo<?> nextInfo) {
                if (binding.viewPager.getCurrentItem() != index) {
                    binding.viewPager.setCurrentItem(index, false);
                }
            }
        });
        binding.viewPager.setAdapter(new HomePagerAdapter(
                getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                data
        ));
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != topTabSelectIndex) {
                    //去通知topTabLayout进行切换
                    binding.topTabLayout.defaultSelected(topTabs.get(position));
                    topTabSelectIndex = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class HomePagerAdapter extends FragmentPagerAdapter {
       Map<Integer,Fragment> fragments = new HashMap<>();
        List<TabCategory>tabs;
        public HomePagerAdapter(@NonNull @NotNull FragmentManager fm, int behavior, List<TabCategory>tabs) {
            super(fm, behavior);
            this.tabs= tabs;
        }

        @NonNull
        @NotNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment == null) {
                fragment = HomeTabFragment.newInstance(tabs.get(position).getCategoryId());
                fragments.put(position,fragment);
            }
            return fragment;
        }


        @Override
        public int getCount() {
            return tabs.size();
        }
    }
}