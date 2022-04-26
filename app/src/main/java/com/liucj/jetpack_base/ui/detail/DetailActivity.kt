package com.liucj.jetpack_base.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liucj.jetpack_base.R
import com.liucj.jetpack_base.databinding.ActivityDetailBinding
import com.liucj.jetpack_base.databinding.ActivityLoginBinding
import com.liucj.jetpack_base.model.DetailModel
import com.liucj.jetpack_base.model.GoodsModel
import com.liucj.jetpack_base.model.selectPrice
import com.liucj.lib_common.item.HiAdapter
import com.liucj.lib_common.item.HiDataItem
import com.liucj.lib_common.utils.StatusBarKt
import com.liucj.lib_common.view.EmptyView1
import com.liucj.lib_common.view.IconFontTextView

class DetailActivity : AppCompatActivity() {
    private lateinit var viewModel: DetailViewModel
    private var emptyView: EmptyView1? = null
    private lateinit var binding: ActivityDetailBinding
    private lateinit var rootContainer: ConstraintLayout
    var goodsId: String? = null

    var goodsModel: GoodsModel? = null

    private lateinit var actionBack: IconFontTextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarKt.setStatusBar(this, true, statusBarColor = Color.TRANSPARENT, translucent = true)
        //断言，如果为空，则抛出异常
        val bundle:Bundle =intent.extras as Bundle;
        goodsId = bundle.getString("goodsId")
        goodsModel = bundle.getParcelable("goodsModel")
        assert(!TextUtils.isEmpty(goodsId)) { "goodsId must bot no null" }
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        if(goodsModel==null)return
        val hiAdapter = recyclerView.adapter as HiAdapter
        hiAdapter.addItemAt(0,
                HeaderItem(
                        goodsModel!!.sliderImages,
                        selectPrice(goodsModel!!.groupPrice, goodsModel!!.marketPrice),
                        goodsModel!!.completedNumText,
                        goodsModel!!.goodsName
                ),false)

    }

    private fun bindData(detailModel: DetailModel) {
        recyclerView.visibility = View.VISIBLE
        emptyView?.visibility = View.GONE
        val hiAdapter = recyclerView.adapter as HiAdapter
        val dataItems = mutableListOf<HiDataItem<*, *>>()
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
        hiAdapter.clearItems()
        hiAdapter.addItems(dataItems,true)

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
            rootContainer.addView(emptyView)
        }
        recyclerView.visibility = View.GONE
        emptyView!!.visibility = View.VISIBLE
    }

    private fun initView() {
        actionBack = binding.actionBack
        recyclerView = binding.recyclerView
        rootContainer = binding.rootContainer
        actionBack.setOnClickListener {
            onBackPressed()
        }
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = HiAdapter(this)
    }
}