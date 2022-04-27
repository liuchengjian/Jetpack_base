package com.liucj.jetpack_base.ui.detail

import android.accounts.AccountManager
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liucj.jetpack_base.R
import com.liucj.jetpack_base.databinding.ActivityDetailBinding
import com.liucj.jetpack_base.databinding.ActivityLoginBinding
import com.liucj.jetpack_base.model.DetailModel
import com.liucj.jetpack_base.model.GoodsModel
import com.liucj.jetpack_base.model.selectPrice
import com.liucj.jetpack_base.ui.home.GoodsItem
import com.liucj.lib_common.executor.HiExecutor
import com.liucj.lib_common.item.HiAdapter
import com.liucj.lib_common.item.HiDataItem
import com.liucj.lib_common.utils.MainHandler
import com.liucj.lib_common.utils.StatusBarKt
import com.liucj.lib_common.view.EmptyView1
import com.liucj.lib_common.view.IconFontTextView
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var viewModel: DetailViewModel
    private var emptyView: EmptyView1? = null
    private lateinit var binding: ActivityDetailBinding

    //    private lateinit var rootContainer: ConstraintLayout
    var goodsId: String? = null

    var goodsModel: GoodsModel? = null

    private lateinit var actionBack: IconFontTextView
//    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarKt.setStatusBar(this, true, statusBarColor = Color.TRANSPARENT, translucent = true)
        //断言，如果为空，则抛出异常
        val bundle: Bundle = intent.extras as Bundle;
        goodsId = bundle.getString("goodsId")
        goodsModel = bundle.getParcelable("goodsModel")
        assert(!TextUtils.isEmpty(goodsId)) { "goodsId must bot no null" }
//        binding = ActivityDetailBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        setContentView(R.layout.activity_detail)
        initView()
        //预渲染
        preBindData()
        viewModel = DetailViewModel.get(goodsId, this)
        viewModel.queryDetailData().observe(this, Observer {
            if (it == null) {
                showEmptyView()
            } else {
                bindData(it)
            }
        })
    }

    private fun preBindData() {
        if (goodsModel == null) return
        val hiAdapter = recycler_view.adapter as HiAdapter
        hiAdapter.addItemAt(0,
                HeaderItem(
                        goodsModel!!.sliderImages,
                        selectPrice(goodsModel!!.groupPrice, goodsModel!!.marketPrice),
                        goodsModel!!.completedNumText,
                        goodsModel!!.goodsName
                ), false)

    }

    private fun bindData(detailModel: DetailModel) {
        recycler_view.visibility = View.VISIBLE
        emptyView?.visibility = View.GONE
        val hiAdapter = recycler_view.adapter as HiAdapter
        val dataItems = mutableListOf<HiDataItem<*, *>>()
        //轮播图
        dataItems.add(
                HeaderItem(
                        detailModel.sliderImages,
                        selectPrice(detailModel.groupPrice, detailModel.marketPrice),
                        detailModel.completedNumText,
                        detailModel.goodsName
                ))

        //评论
        dataItems.add(CommentItem(detailModel))
        //店铺

        //图库
        detailModel.gallery?.forEach {
            dataItems.add(GalleryItem(it))
        }
        //相似商品
        detailModel.similarGoods?.let {
            dataItems.add(SimilarTitleItem())
            it.forEach { goodsModel ->
                dataItems.add(GoodsItem(goodsModel, false))
            }
        }
        hiAdapter.clearItems()


        hiAdapter.addItems(dataItems, true)
        updateFavoriteActionFace(detailModel.isFavorite)
        updateOrderActionFace(detailModel)
    }

    private fun updateOrderActionFace(detailModel: DetailModel) {
        action_order.text = selectPrice(detailModel.groupPrice,detailModel.marketPrice)+"\n立即购买"
    }

    private fun updateFavoriteActionFace(favorite: Boolean) {
        action_favorite.setOnClickListener {
            toggleFavorite()
        }
        action_favorite.setTextColor(ContextCompat.getColor(this, if (favorite) R.color.color_dd2 else R.color.color_999))
    }

    private fun toggleFavorite() {
//        if(!AccountManager.isL)
        action_favorite.isClickable = false
        viewModel.toggleFavorite().observe(this, Observer { success ->
            if (success != null) {
                //网络成功
                updateFavoriteActionFace(success)
                val message =
                        if (success)
                            "收藏成功"
                        else
                            "取消收藏成功"
                showToast(message)

            } else {

            }
            action_favorite.isClickable = true
        })
    }

    private fun showToast(message: String) {
        MainHandler.sendAtFrontOfQueue(runnable = Runnable {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun showEmptyView() {
        if (emptyView == null) {
            emptyView = EmptyView1(this)
            emptyView!!.setIcon(R.string.if_empty3)
            emptyView!!.setDesc(getString(R.string.list_empty_desc))
            emptyView!!.layoutParams = ConstraintLayout.LayoutParams(-1, -1)
            emptyView!!.setBackgroundColor(Color.WHITE)
            emptyView!!.setButton(getString(R.string.list_empty_action), View.OnClickListener {
                viewModel.queryDetailData()
            })
            root_container.addView(emptyView)
        }
        recycler_view.visibility = View.GONE
        emptyView!!.visibility = View.VISIBLE
    }

    private fun initView() {
//        actionBack = binding.actionBack
//        recyclerView = binding.recyclerView
//        rootContainer = binding.rootContainer
        action_back.setOnClickListener {
            onBackPressed()
        }
        recycler_view.layoutManager = GridLayoutManager(this, 2)
        recycler_view.adapter = HiAdapter(this)
        recycler_view.addOnScrollListener(TitleScrollListener(callback = {
            title_bar.setBackgroundColor(it)
        }))
    }
}