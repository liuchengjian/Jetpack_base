package com.liucj.jetpack_base.model

import android.os.Parcelable
import android.text.TextUtils
import androidx.databinding.BaseObservable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@Parcelize
data class GoodsModel (
        val categoryId: String,
        val completedNumText: String,
        val createTime: String,
        val goodsId: String,
        val goodsName: String,
        val groupPrice: String,
        val hot: Boolean,
        val joinedAvatars: List<SliderImage>?,
        val marketPrice: String,
        val sliderImage: String,
        val sliderImages: List<SliderImage>?,
        val tags: String): BaseObservable(),Serializable, Parcelable

fun selectPrice(groupPrice:String?,marketPrice:String?):String{
    var price :String?= if(TextUtils.isEmpty(marketPrice)) groupPrice else marketPrice
    if(price?.startsWith("¥")!=true){
        price = "¥".plus(price)
    }
    return price;
}
