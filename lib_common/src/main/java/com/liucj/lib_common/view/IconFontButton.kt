package com.liucj.lib_common.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView


/**
 * 用以支持全局iconfont资源的引用，可以在布局中直接设置text
 */
class IconFontButton @JvmOverloads constructor(
        context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) :
    AppCompatButton(context, attributeSet, defStyle) {
    init {
        val typeface = Typeface.createFromAsset(context.assets, "fonts/iconfont.ttf");
        setTypeface(typeface)
    }

}