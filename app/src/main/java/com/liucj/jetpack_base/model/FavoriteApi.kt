package com.liucj.jetpack_base.model

import com.liucj.lib_network.restful_kt.HiCall
import com.liucj.lib_network.restful_kt.annotation.POST
import com.liucj.lib_network.restful_kt.annotation.Path

interface FavoriteApi {

    @POST("favorites/{goodsId}")
    fun favorite(@Path("goodsId")goodsId:String):HiCall<Favorite>
}