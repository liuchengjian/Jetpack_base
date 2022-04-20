package com.liucj.jetpack_base.ui.home

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.liucj.jetpack_base.model.HomeBanner
import com.liucj.lib_common.banner.HiBanner
import com.liucj.lib_common.banner.core.HiBannerMo
import com.liucj.lib_common.item.HiDataItem
import com.liucj.lib_common.item.HiViewHolder
import com.liucj.lib_common.utils.HiDisplayUtil
import com.liucj.lib_common.view.loadUrl

class BannerItem(val list: List<HomeBanner>) :
    HiDataItem<List<HomeBanner>, HiViewHolder>(list) {
    override fun onBindData(holder:HiViewHolder, position: Int) {
        val context = holder.itemView.context
        val banner = holder.itemView as HiBanner

        val models = mutableListOf<HiBannerMo>()
        list.forEachIndexed { index, homeBanner ->
            val bannerMo = object : HiBannerMo() {}
            bannerMo.url = homeBanner.cover
            models.add(bannerMo)
        }
        banner.setOnBannerClickListener { viewHolder, bannerMo, position ->
            val homeBanner = list[position]
            if (TextUtils.equals(homeBanner.type, HomeBanner.TYPE_GOODS)) {
                //跳详情页....
                Toast.makeText(context, "you  touch me:$position", Toast.LENGTH_SHORT).show()
            } else {
//                HiRoute.startActivity4Browser(homeBanner.url)
            }

        }
        banner.setBannerData(models)
        banner.setBindAdapter { viewHolder, mo, position ->
            ((viewHolder.rootView) as ImageView).loadUrl(mo.url)
        }
    }

    override fun getItemView(parent: ViewGroup): View? {
        val context = parent.context
        val banner = HiBanner(context)
        val params = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            HiDisplayUtil.dp2px(160f)
        )
        params.bottomMargin = HiDisplayUtil.dp2px(10f)
        banner.layoutParams = params
        banner.setBackgroundColor(Color.WHITE)
        return banner
    }
}