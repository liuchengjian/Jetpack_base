package com.liucj.jetpack_base.ui.detail

import com.liucj.jetpack_base.R
import com.liucj.lib_common.item.HiDataItem
import com.liucj.lib_common.item.HiViewHolder

class SimilarTitleItem:HiDataItem<Any,HiViewHolder>() {
    override fun onBindData(holder: HiViewHolder, position: Int) {
    }

    override fun getItemLayoutRes(): Int {
        return R.layout.layout_detail_item_simliar_title
    }
}