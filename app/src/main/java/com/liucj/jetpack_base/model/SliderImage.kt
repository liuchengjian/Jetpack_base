package com.liucj.jetpack_base.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@Parcelize
data class SliderImage(
    val type: Int,
    val url: String
):Serializable,Parcelable