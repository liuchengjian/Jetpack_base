package com.liucj.lib_common.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import com.liucj.lib_common.R
import com.liucj.lib_common.utils.HiDisplayUtil
import org.w3c.dom.Attr
import java.lang.IllegalStateException

class HiNavigationBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {
    //主副标题
    private var titleView: IconFontTextView? = null
    private var subTitleView: IconFontTextView? = null
    private var titleContainer: LinearLayout? = null
    private var navAttrs: Attrs

    private var mLeftLastViewId = View.NO_ID
    private var mRightLastViewId = View.NO_ID

    private val mLeftViewList = ArrayList<View>()
    private val mRightViewList = ArrayList<View>()

    init {
        navAttrs = parseNavAttrs(context, attrs, defStyleAttr)
        if (!TextUtils.isEmpty(navAttrs.navTitle)) {
            setTitle(navAttrs.navTitle!!)
        }
        if (!TextUtils.isEmpty(navAttrs.navSubTitle)) {
            setSubTitle(navAttrs.navSubTitle!!)
        }
    }

    fun setNavListener(listener: OnClickListener) {
        if (!TextUtils.isEmpty(navAttrs.navIconStr)) {
            val navBackView = addLeftTextButton(navAttrs.navIconStr!!, R.integer.id_left_back_view)
            navBackView.setOnClickListener(listener)
        }
    }

    fun setTitle(title: String) {
        ensureTitleView()
        titleView?.text = title
        titleView?.visibility = if (TextUtils.isEmpty(title)) View.GONE else View.VISIBLE
    }

    fun setSubTitle(subtitle: String) {
        ensureSubTitleView()
        updateTitleViewStyle()
        subTitleView?.text = subtitle
        subTitleView?.visibility = if (TextUtils.isEmpty(subtitle)) View.GONE else View.VISIBLE

    }

    private fun ensureTitleView() {
        if (titleView == null) {
            titleView = IconFontTextView(context, null)
            titleView?.apply {
                gravity = Gravity.CENTER
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
                setTextColor(navAttrs.titleTextColor)

                updateTitleViewStyle()
                ensureTitleContainer()
                titleContainer?.addView(titleView, 0)
            }
        }
    }

    private fun ensureSubTitleView() {
        if (subTitleView == null) {
            subTitleView = IconFontTextView(context, null)
            subTitleView?.apply {
                gravity = Gravity.CENTER
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
                setTextColor(navAttrs.subTitleTextColor)
                setTextSize(navAttrs.subTitleTextSize)
                textSize = navAttrs.subTitleTextSize
                //添加到container里
                ensureTitleContainer()
                titleContainer?.addView(subTitleView)

            }
        }
    }

    private fun ensureTitleContainer() {
        if (titleContainer == null) {
            titleContainer = LinearLayout(context)
            titleContainer?.apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER

                val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                this@HiNavigationBar.addView(titleContainer,params)
            }
        }

    }

    private fun updateTitleViewStyle() {
        if (titleView != null) {
            if (subTitleView == null || TextUtils.isEmpty(subTitleView!!.text)) {
                titleView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, navAttrs.titleTextSize)
                titleView?.typeface = Typeface.DEFAULT_BOLD //粗体
            } else {
                titleView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, navAttrs.titleTextSizeWithSubtitle)
                titleView?.typeface = Typeface.DEFAULT //正常的字体
            }
        }

    }

    fun addLeftTextButton(@StringRes stringRes: Int, viewId: Int): Button {
        return addLeftTextButton(resources.getString(stringRes), viewId)
    }

    fun addLeftTextButton(buttonText: String, viewId: Int): Button {
        val button = generateTextButton()
        button.text = buttonText
        button.id = viewId
        if (mLeftViewList.isEmpty()) {
            button.setPadding(2 * navAttrs.horPadding, 0, navAttrs.horPadding, 0)
        } else {
            button.setPadding(navAttrs.horPadding, 0, navAttrs.horPadding, 0)
        }
        addLeftView(button, generateTextButtonLayoutParams())
        return button
    }

    fun addLeftView(view: View, params: LayoutParams) {
        val viewId = view.id
        if (viewId == View.NO_ID) {
            throw IllegalStateException("left view must has an unique id")
        }
        if (mLeftLastViewId == View.NO_ID) {
            params.addRule(ALIGN_PARENT_LEFT, viewId)
        } else {
            params.addRule(RIGHT_OF, mLeftLastViewId)
        }
        mLeftLastViewId = viewId
        params.alignWithParent = true
        mLeftViewList.add(view)
        addView(view, params)
    }

    fun addRightTextButton(@StringRes stringRes: Int, viewId: Int): Button {
        return addRightTextButton(resources.getString(stringRes), viewId)
    }

    fun addRightTextButton(buttonText: String, viewId: Int): Button {
        val button = generateTextButton()
        button.text = buttonText
        button.id = viewId
        if (mRightViewList.isEmpty()) {
            button.setPadding(navAttrs.horPadding, 0, navAttrs.horPadding * 2, 0)
        } else {
            button.setPadding(navAttrs.horPadding, 0, navAttrs.horPadding, 0)
        }
        addRightView(button, generateTextButtonLayoutParams())
        return button
    }

    fun addRightView(view: View, params: LayoutParams) {
        val viewId = view.id
        if (viewId == View.NO_ID) {
            throw IllegalStateException("left view must has an unique id")
        }
        if (mRightLastViewId == View.NO_ID) {
            params.addRule(ALIGN_PARENT_RIGHT, viewId)
        } else {
            params.addRule(LEFT_OF, mRightLastViewId)
        }
        mRightLastViewId = viewId
        params.alignWithParent = true
        mRightViewList.add(view)
        addView(view, params)
    }

    private fun generateTextButtonLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
    }

    private fun generateTextButton(): Button {
        val button = IconFontButton(context)
        button.setBackgroundResource(0)//去除默认背景
        button.minWidth = 0
        button.minimumWidth = 0
        button.minHeight = 0
        button.minimumWidth = 0
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, navAttrs.btnTextSize)
        button.setTextColor(navAttrs.btnTextColor)
        button.gravity = Gravity.CENTER
        return button
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (titleContainer != null) {
            //计算出标题左侧已占用的空间
            var leftUseSpace = paddingLeft
            for (view in mLeftViewList) {
                leftUseSpace += view.measuredWidth
            }
            //计算出标题右侧侧已占用的空间
            var rightUseSpace = paddingRight
            for (view in mRightViewList) {
                rightUseSpace += view.measuredWidth
            }
            //这里是他想要的宽度
            val titleContainerWidth = titleContainer!!.measuredWidth
            //为了标题居中，左右空余一致
            val remainingSpace = measuredWidth - Math.max(leftUseSpace, rightUseSpace) * 2
            if (remainingSpace < titleContainerWidth) {
                //具体的使用距离
                val size = MeasureSpec.makeMeasureSpec(remainingSpace, MeasureSpec.EXACTLY)
                titleContainer!!.measure(size, heightMeasureSpec)
            }
        }
    }

    private fun parseNavAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int): Attrs {
//       val value = TypedValue()
//        context.theme.resolveAttribute(R.attr.navigationStyle,value,true)
//        val defStyleRes = if(value.resourceId!=0) value.resourceId else R.style.navigationStyle
        val array = context.obtainStyledAttributes(
                attrs,
                R.styleable.HiNavigationBar,
                defStyleAttr,
                R.style.navigationStyle)
        val navIcon = array.getString(R.styleable.HiNavigationBar_nav_icon)
        val navTitle = array.getString(R.styleable.HiNavigationBar_nav_title)
        val navSubTitle = array.getString(R.styleable.HiNavigationBar_nav_subtitle)
        val horPadding = array.getDimensionPixelSize(R.styleable.HiNavigationBar_hor_padding, 0)
        val btnTextSize = array.getDimensionPixelSize(R.styleable.HiNavigationBar_text_btn_text_size, HiDisplayUtil.dp2px(16f))
        val btnTextColor = array.getColorStateList(R.styleable.HiNavigationBar_text_btn_text_color)

        val titleTextSize = array.getDimensionPixelSize(R.styleable.HiNavigationBar_title_text_size, HiDisplayUtil.dp2px(17f))
        val titleTextColor = array.getColor(R.styleable.HiNavigationBar_title_text_color, resources.getColor(R.color.hi_tabtop_normal_color))

        val subTitleTextSize = array.getDimensionPixelSize(R.styleable.HiNavigationBar_subTitle_text_size, HiDisplayUtil.dp2px(14f))

        val subTitleTextColor = array.getColor(R.styleable.HiNavigationBar_subTitle_text_color, resources.getColor(R.color.hi_tabtop_normal_color))
        val titleTextSizeWithSubtitle = array.getDimensionPixelSize(R.styleable.HiNavigationBar_title_text_size_with_subTitle, HiDisplayUtil.dp2px(16f))

        array.recycle()
        return Attrs(navIcon,
                navTitle,
                navSubTitle,
                horPadding,
                btnTextSize.toFloat(),
                btnTextColor,
                titleTextSize.toFloat(),
                titleTextSizeWithSubtitle.toFloat(),
                titleTextColor,
                subTitleTextSize.toFloat(),
                subTitleTextColor)
    }

    private data class Attrs(
            val navIconStr: String?,
            val navTitle: String?,
            val navSubTitle: String?,
            val horPadding: Int,
            val btnTextSize: Float,
            val btnTextColor: ColorStateList?,
            val titleTextSize: Float,
            val titleTextSizeWithSubtitle: Float,
            val titleTextColor: Int,
            val subTitleTextSize: Float,
            val subTitleTextColor: Int)
}