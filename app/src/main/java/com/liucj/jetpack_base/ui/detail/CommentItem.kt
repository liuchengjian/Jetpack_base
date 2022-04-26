package com.liucj.jetpack_base.ui.detail

import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.liucj.jetpack_base.R
import com.liucj.jetpack_base.model.DetailModel
import com.liucj.lib_common.item.HiDataItem
import com.liucj.lib_common.item.HiViewHolder
import com.liucj.lib_common.utils.HiDisplayUtil
import com.liucj.lib_common.view.loadCircle
import java.util.Collections.min
import kotlin.math.min

class CommentItem(val model: DetailModel) : HiDataItem<DetailModel, HiViewHolder>() {
    override fun onBindData(holder: HiViewHolder, position: Int) {
        val context = holder.itemView.context ?: return
        holder.itemView.findViewById<TextView>(R.id.comment_title).text = model.commentCountTitle
        val commentTag: String? = model.commentTags
        if (commentTag != null) {
            val tagsArray = commentTag.split(" ")
            if (tagsArray != null && tagsArray.isNotEmpty()) {
                for (index in tagsArray.indices) {
                    val chipGroup: ChipGroup = holder.itemView.findViewById<ChipGroup>(R.id.chip_group)
                    val chipLabel  = if(index<chipGroup.childCount){
                        chipGroup.getChildAt(index) as Chip
                    }else{
                        //有滑动复用问题，检查复用
                        val chipLabel = Chip(context)
                        chipLabel.chipCornerRadius = HiDisplayUtil.dp2px(4f).toFloat()
                        chipLabel.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_faf0))
                        chipLabel.setTextColor(ContextCompat.getColor(context, R.color.color_999))
                        chipLabel.textSize = 14f
                        chipLabel.gravity = Gravity.CENTER
                        chipLabel.isCheckedIconVisible = false
                        chipLabel.isCheckable = false
                        chipLabel.isChipIconVisible = false

                        chipGroup.addView(chipLabel)
                        chipLabel
                    }
                    chipLabel.text = tagsArray[index]
                }

                model.commentModels?.let {
                   val commentContainer=  holder.itemView.findViewById<LinearLayout>(R.id.comment_container)
                    for (index in 0..min(it.size-1,3)){
                        val comment = it[index]
                        val itemView = if(index<commentContainer.childCount){
                            commentContainer.getChildAt(index)
                        }else{
                            val view = LayoutInflater.from(context).inflate(
                                    R.layout.layout_detail_item_comment_item,commentContainer,false)
                            commentContainer.addView(view)
                            view
                        }
                        itemView.findViewById<ImageView>(R.id.user_avatar).
                        loadCircle(comment.avatar!!)
                        itemView.findViewById<TextView>(R.id.user_name).text=
                                comment.nickName
                        itemView.findViewById<TextView>(R.id.comment_content).text =comment.content

                    }
                }
            }
        }
    }

    override fun getItemLayoutRes(): Int {
        return R.layout.layout_detail_item_comment
    }
}