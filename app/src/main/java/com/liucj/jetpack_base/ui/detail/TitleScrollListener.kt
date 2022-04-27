package com.liucj.jetpack_base.ui.detail

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.liucj.lib_common.utils.ColorUtil
import com.liucj.lib_common.utils.HiDisplayUtil
import java.lang.Math.abs
import java.lang.Math.min

class TitleScrollListener(val thresholdDp:Float = 100f,val callback:(Int)->Unit):RecyclerView.OnScrollListener(){
    private val thresholdPx = HiDisplayUtil.dp2px(thresholdDp)
    private var lastFraction = 0f
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        //在这里判断列表滑动的距离，然后和thresholdPx进行运算得到滑动状态
        //计算一个颜色值 transpanrent->white
       val viewHolder = recyclerView.findViewHolderForAdapterPosition(0)?:return
        val top = abs(viewHolder.itemView.top).toFloat()
        //滑动的百分比
        val fraction = top/thresholdPx
        if(lastFraction>1f){
            lastFraction = fraction
            return
        }
        val newColor =ColorUtil.getCurrentColor(Color.TRANSPARENT,Color.WHITE, min(fraction,1f))
        callback(newColor)

        lastFraction = fraction

    }
}