package com.liucj.lib_common.utils

import android.graphics.Color

object ColorUtil {
    //根据百分比 计算start - end 之间的中间色转换
     fun getCurrentColor(startColor: Int,endColor:Int,fraction:Float):Int{
        val redStart = Color.red(startColor)
        val blueStart = Color.blue(startColor)
        val greenStart = Color.green(startColor)
        val alphaStart = Color.alpha(startColor)

        val redEnd = Color.red(endColor)
        val blueEnd = Color.blue(endColor)
        val greenEnd = Color.green(endColor)
        val alphaEnd= Color.alpha(endColor)

        val  redDiff = redEnd - redStart
        val  blueDiff = blueEnd - blueStart
        val  greenDiff = greenEnd - greenStart
        val  alphaDiff = alphaEnd - alphaStart

        val redCurrent  = (redStart+fraction*redDiff).toInt()
        val blueCurrent  = (blueStart+fraction*blueDiff).toInt()
        val greenCurrent  = (greenStart+fraction*greenDiff).toInt()
        val alphaCurrent  = (alphaStart+fraction*alphaDiff).toInt()

        return Color.argb(alphaCurrent,redCurrent,greenCurrent,blueCurrent)

    }
}