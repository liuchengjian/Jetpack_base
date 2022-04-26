package com.liucj.jetpack_base.ui.detail

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liucj.jetpack_base.R
import com.liucj.jetpack_base.model.DetailModel
import com.liucj.jetpack_base.model.SliderImage
import com.liucj.lib_common.banner.HiBanner
import com.liucj.lib_common.banner.core.HiBannerAdapter
import com.liucj.lib_common.banner.core.HiBannerMo
import com.liucj.lib_common.banner.indicator.HiNumIndicator
import com.liucj.lib_common.item.HiDataItem
import com.liucj.lib_common.item.HiViewHolder
import com.liucj.lib_common.view.loadUrl

class HeaderItem(val sliderImages: List<SliderImage>?, val price: String,
                 val completedNumText: String?,val goodName: String?) :
        HiDataItem<DetailModel,HiViewHolder>() {
    override fun onBindData(holder: HiViewHolder, position: Int) {
        val context = holder.itemView.context?:return
        val bannerItems = arrayListOf<HiBannerMo>()
        sliderImages?.forEach{
            val bannerMo = object :HiBannerMo(){}
            bannerMo.url = it.url
            bannerItems.add(bannerMo)
        }
        holder.itemView.findViewById<HiBanner>(R.id.hi_banner)?.setHiIndicator(
                HiNumIndicator(context))
        holder.itemView.findViewById<HiBanner>(R.id.hi_banner)?.setBannerData(bannerItems)
        holder.itemView.findViewById<HiBanner>(R.id.hi_banner)?.setBindAdapter{viewHolder:HiBannerAdapter.HiBannerViewHolder?,
            mo:HiBannerMo?,position:Int->
            val  imageView = viewHolder?.rootView as ImageView
            mo?.let { imageView?.loadUrl(mo!!.url) }
        }
        holder.itemView.findViewById<TextView>(R.id.price).text =spamPrice(price)
        holder.itemView.findViewById<TextView>(R.id.sale_desc).text = completedNumText
        holder.itemView.findViewById<TextView>(R.id.title).text =goodName
    }

    private fun spamPrice(price: String):CharSequence{
        if(TextUtils.isEmpty(price))return ""
        val ss = SpannableString(price)
        ss.setSpan(AbsoluteSizeSpan(18,true),1,ss.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ss
    }

    override fun getItemLayoutRes(): Int {
        return R.layout.layout_detail_item_header
    }
}