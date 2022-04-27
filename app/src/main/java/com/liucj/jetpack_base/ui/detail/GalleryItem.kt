package com.liucj.jetpack_base.ui.detail

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.util.Log
import com.liucj.jetpack_base.model.SliderImage
import com.liucj.lib_common.item.HiDataItem
import com.liucj.lib_common.item.HiViewHolder
import com.liucj.lib_common.utils.PixUtils
import com.liucj.lib_common.view.loadUrl
import com.google.android.exoplayer2.util.Log.e as e1

/**
 * 商品相册
 */
class GalleryItem(val sliderImage: SliderImage) : HiDataItem<SliderImage, HiViewHolder>() {
    private var parentWidth: Int =  PixUtils.getScreenWidth()

    override fun onBindData(holder: HiViewHolder, position: Int) {
        val imageView = holder.itemView as ImageView
        if (!TextUtils.isEmpty(sliderImage.url)) {
            imageView.loadUrl(sliderImage.url) {
                //需要拿到回调,
                val drawableWidth = it.intrinsicWidth
                val drawableHeight = it.intrinsicHeight
                val params = imageView.layoutParams ?: RecyclerView.LayoutParams(
                        parentWidth,
                        RecyclerView.LayoutParams.WRAP_CONTENT)
                params.width = parentWidth
                //等比例缩放
                val scale = parentWidth / (drawableWidth * 1.0f)
                params.height = (drawableHeight * scale).toInt()
                imageView.layoutParams = params
                ViewCompat.setBackground(imageView, it)
            }
            //展位图
            //根据图片的宽高值，等比例计算imageView的高度值
        }
    }

    override fun getItemView(parent: ViewGroup): View? {
        val imageView = ImageView(parent.context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setBackgroundColor(Color.WHITE)
        return imageView
    }

    override fun onViewAttachedToWindow(holder: HiViewHolder) {
        parentWidth = (holder.itemView.parent as RecyclerView).measuredWidth
        val params: ViewGroup.LayoutParams = holder.itemView.layoutParams
        if (params.width != parentWidth) {
            params.width = parentWidth
            params.height = parentWidth
            holder.itemView.layoutParams = params
        }
    }
}