package com.liucj.jetpack_base.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liucj.jetpack_base.R
import com.liucj.jetpack_base.databinding.LayoutHomeOpGridItemBinding
import com.liucj.jetpack_base.model.Subcategory
import com.liucj.lib_common.item.HiDataItem
import com.liucj.lib_common.item.HiViewHolder
import com.liucj.lib_common.utils.HiDisplayUtil
import com.liucj.lib_common.view.loadUrl

class GridItem(val list: List<Subcategory>) :
    HiDataItem<List<Subcategory>, HiViewHolder>(list) {
    override fun onBindData(holder: HiViewHolder, position: Int) {
        val context = holder.itemView.context
        val gridView = holder.itemView as RecyclerView
        gridView.adapter = GridAdapter(context, list)
    }

    override fun getItemView(parent: ViewGroup): View? {
        val gridView = RecyclerView(parent.context)
        val params = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = HiDisplayUtil.dp2px(10f)
        gridView.layoutManager = GridLayoutManager(parent.context, 5)
        gridView.layoutParams = params
        gridView.setBackgroundColor(Color.WHITE)
        return gridView
    }


    inner class GridAdapter(val context: Context, val list: List<Subcategory>) :
        RecyclerView.Adapter<GridAdapter.GirdItemViewHolder>() {
        private var inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GirdItemViewHolder {
           // val view = inflater.inflate(R.layout.layout_home_op_grid_item, parent, false)
          val binding = LayoutHomeOpGridItemBinding.inflate(inflater,parent,false)
            return GirdItemViewHolder(binding.root,binding)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: GirdItemViewHolder, position: Int) {
            val subcategory = list[position]
            holder.binding.subcategory = subcategory;
//            holder.view.findViewById<ImageView>(R.id.item_image).loadUrl(subcategory.subcategoryIcon)
//            holder.view.findViewById<TextView>(R.id.item_title)?.text = subcategory.subcategoryName
            //holder.itemView.item_image.loadUrl(subcategory.subcategoryIcon)
            //holder.itemView.item_title.text = subcategory.subcategoryName

            holder.itemView.setOnClickListener {
                //会跳转到子分类列表上面去，，是一个单独的页面
                //Toast.makeText(context, "you touch me:" + position, Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putString("categoryId", subcategory.categoryId)
                bundle.putString("subcategoryId", subcategory.subcategoryId)
                bundle.putString("categoryTitle", subcategory.subcategoryName)
//                HiRoute.startActivity(context, bundle, GOODS_LIST)
            }
        }
        inner class GirdItemViewHolder(view:View,val binding: LayoutHomeOpGridItemBinding)
            :HiViewHolder( view )
    }

    class HMyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
    }


}