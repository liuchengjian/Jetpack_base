package com.liucj.jetpack_base.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import com.liucj.jetpack_base.R
import com.liucj.jetpack_base.api.ApiFactory
import com.liucj.jetpack_base.api.CategoryApi
import com.liucj.jetpack_base.databinding.FragmentCategoryBinding
import com.liucj.jetpack_base.model.Subcategory
import com.liucj.jetpack_base.model.TabCategory
import com.liucj.jetpack_base.ui.detail.DetailActivity
import com.liucj.lib_common.fragment.BaseFragment
import com.liucj.lib_common.utils.HiDisplayUtil
import com.liucj.lib_common.view.EmptyView1
import com.liucj.lib_common.view.loadUrl
import com.liucj.lib_navannotation.FragmentDestination
import com.liucj.lib_network.restful_kt.HiCallback
import com.liucj.lib_network.restful_kt.HiResponse

@FragmentDestination(pageUrl = "main/tabs/category", asStarter = true)
class CategoryFragment : BaseFragment() {
    private var emptyView: EmptyView1? = null
    private var contentLoading: ContentLoadingProgressBar? = null
    private lateinit var binding: FragmentCategoryBinding
    private val subcategoryListCache = mutableMapOf<String, List<Subcategory>>()
    private val SPAN_COUNT = 3
    private val subcategoryList = mutableListOf<Subcategory>()
    private val groupSpanSizeOffset = SparseIntArray()
    private val layoutManager = GridLayoutManager(context, SPAN_COUNT)
    private val decoration = CategoryItemDecoration({ position ->
        subcategoryList[position].groupName
    }, SPAN_COUNT)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        clipBottomPadding(binding.root)
        contentLoading = binding.root.findViewById(R.id.content_loading)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queryCategoryList()

    }

    private fun queryCategoryList() {
        ApiFactory.create(CategoryApi::class.java).queryCategoryList()
                .enqueue(object : HiCallback<List<TabCategory>> {
                    override fun onSuccess(response: HiResponse<List<TabCategory>>) {
                        if (response.successful() && response.data != null) {
                            onQueryCategoryListSuccess(response.data!!)
                        } else {
                            showEmptyView()
                        }
                    }

                    override fun onFailed(throwable: Throwable) {
                        showEmptyView()
                    }

                })
    }

    private fun showEmptyView() {
        if (!isAlive) return

        if (emptyView == null) {
            emptyView = EmptyView1(requireContext())
            emptyView?.setIcon(R.string.if_empty3)
            emptyView?.setDesc(getString(R.string.list_empty_desc))
            emptyView?.setButton(getString(R.string.list_empty_action), View.OnClickListener {
                queryCategoryList()
            })

            emptyView?.setBackgroundColor(Color.WHITE)
            emptyView?.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            )
            binding.rootContainer.addView(emptyView)
        }
        contentLoading?.visibility = View.GONE
        binding.sliderView.visibility = View.GONE
        emptyView?.visibility = View.VISIBLE
    }

    private fun onQueryCategoryListSuccess(data: List<TabCategory>) {
        if (!isAlive) return
        emptyView?.visibility = View.GONE
        contentLoading?.visibility = View.GONE
        binding.sliderView.visibility = View.VISIBLE

        binding.sliderView.bindMenuView(itemCount = data.size,
                onBindView = { holder, position ->
                    val category = data[position]
                    // holder.menu_item_tilte    ??????????????????
                    //  holder.itemView.menu_item_title.  findviewbyid
                    holder.findViewById<TextView>(R.id.menu_item_title)?.text = category.categoryName
                }, onItemClick = { holder, position ->
            val category = data[position]
            val categoryId = category.categoryId
            if (subcategoryListCache.containsKey(categoryId)) {
                onQuerySubcategoryListSuccess(subcategoryListCache[categoryId]!!)
            } else {
                querySubcategoryList(categoryId)
            }
        })
    }

    private fun onQuerySubcategoryListSuccess(data: List<Subcategory>) {
        if (!isAlive) return
        decoration.clear()
        groupSpanSizeOffset.clear()
        subcategoryList.clear()
        subcategoryList.addAll(data)

        if (layoutManager.spanSizeLookup != spanSizeLookUp) {
            layoutManager.spanSizeLookup = spanSizeLookUp
        }
        binding.sliderView.bindContentView(
                itemCount = data.size,
                itemDecoration = decoration,
                layoutManager = layoutManager,
                onBindView = { holder, position ->
                    val subcategory = data[position]
                    holder.findViewById<ImageView>(R.id.content_item_image)
                            ?.loadUrl(subcategory.subcategoryIcon)
                    holder.findViewById<TextView>(R.id.content_item_title)?.text =
                            subcategory.subcategoryName
                },
                onItemClick = { holder, position ->
                    //?????????????????????????????????????????????

                })

    }

    private fun querySubcategoryList(categoryId: String) {
        ApiFactory.create(CategoryApi::class.java).querySubcategoryList(categoryId)
                .enqueue(object : HiCallback<List<Subcategory>> {
                    override fun onSuccess(response: HiResponse<List<Subcategory>>) {
                        if (response.successful() && response.data != null) {
                            onQuerySubcategoryListSuccess(response.data!!)
                            if (!subcategoryListCache.containsKey(categoryId)) {
                                subcategoryListCache[categoryId] = response.data!!
                            }
                        }
                    }

                    override fun onFailed(throwable: Throwable) {
                    }
                })
    }

    private val spanSizeLookUp = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            var spanSize = 1
            val groupName: String = subcategoryList[position].groupName
            val nextGroupName: String? =
                    if (position + 1 < subcategoryList.size) subcategoryList[position + 1].groupName else null

            if (TextUtils.equals(groupName, nextGroupName)) {
                spanSize = 1
            } else {
                //??????????????? ??????????????? ?????????????????????
                //1 .?????????????????? position ?????????????????? groupSpanSizeOffset ???????????????
                //2 .?????? ????????????????????? ????????? spansizeoffset ?????????
                //3 .????????????????????????item ?????? spansize count

                val indexOfKey = groupSpanSizeOffset.indexOfKey(position)
                val size = groupSpanSizeOffset.size()
                val lastGroupOffset = if (size <= 0) 0
                else if (indexOfKey >= 0) {
                    //??????????????????????????????????????????????????? groupSpanSizeOffset ???????????????????????????????????????
                    if (indexOfKey == 0) 0 else groupSpanSizeOffset.valueAt(indexOfKey - 1)
                } else {
                    //?????????????????????????????????????????????????????? groupSpanSizeOffset ???????????????????????? ????????????????????????
                    //???????????????????????????????????????
                    groupSpanSizeOffset.valueAt(size - 1)
                }
                //          3       -     (6     +    5               % 3  )?????????=0  ???1 ???2
                spanSize = SPAN_COUNT - (position + lastGroupOffset) % SPAN_COUNT
                if (indexOfKey < 0) {
                    //??????????????? ?????????????????????spansize ???????????????
                    val groupOffset = lastGroupOffset + spanSize - 1
                    groupSpanSizeOffset.put(position, groupOffset)
                }
            }
            return spanSize
        }
    }



}