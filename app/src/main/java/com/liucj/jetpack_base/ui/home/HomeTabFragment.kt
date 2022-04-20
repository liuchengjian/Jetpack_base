package com.liucj.jetpack_base.ui.home

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.liucj.jetpack_base.api.ApiFactory
import com.liucj.jetpack_base.api.HomeApi
import com.liucj.jetpack_base.model.HomeModel
import com.liucj.lib_common.fragment.BaseListFragment
import com.liucj.lib_common.item.HiAdapter
import com.liucj.lib_common.item.HiDataItem
import com.liucj.lib_network.restful_kt.HiCallback
import com.liucj.lib_network.restful_kt.HiResponse
import com.scwang.smartrefresh.layout.api.RefreshLayout

class HomeTabFragment : BaseListFragment() {
    private var categoryId: String? = null
    val DEFAULT_HOT_TAB_CATEGORY_ID = "1"

    companion object {
        @JvmStatic
        fun newInstance(categoryId: String): HomeTabFragment {
            val args = Bundle()
            args.putString("categoryId", categoryId)
            val fragment =
                    HomeTabFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        categoryId = arguments?.getString("categoryId", DEFAULT_HOT_TAB_CATEGORY_ID)
        super.onViewCreated(view, savedInstanceState)
        queryTabCategoryList()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        queryTabCategoryList()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        queryTabCategoryList()
    }

    private fun queryTabCategoryList() {
        ApiFactory.create(HomeApi::class.java)
                .queryTabCategoryList(categoryId!!, pageIndex, 10)
                .enqueue(object : HiCallback<HomeModel> {
                    override fun onSuccess(response: HiResponse<HomeModel>) {
                        if (response.successful() && response.data != null) {
                            updateUI(response.data!!)
                        } else {
                            finishRefresh(null)
                        }
                    }

                    override fun onFailed(throwable: Throwable) {
                        //空数据页面
                        finishRefresh(null)
                    }
                })
    }

    private fun updateUI(data: HomeModel) {
        if (!isAlive) return
        val dataItems = mutableListOf<HiDataItem<*, *>>()
        data.bannerList?.let {
            dataItems.add(BannerItem(data.bannerList))
        }

        data.subcategoryList?.let {
            dataItems.add(GridItem(data.subcategoryList))
        }
//
        data.goodsList?.forEachIndexed { index, goodsModel ->
            dataItems.add(
                    GoodsItem(
                            goodsModel,
                            TextUtils.equals(categoryId, DEFAULT_HOT_TAB_CATEGORY_ID)
                    )
            )
        }
        finishRefresh(dataItems)
    }



    override fun getAdapter(): HiAdapter? {
        return HiAdapter(requireContext())
    }
}