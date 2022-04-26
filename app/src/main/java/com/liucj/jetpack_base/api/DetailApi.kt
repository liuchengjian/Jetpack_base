package com.liucj.jetpack_base.api

import com.liucj.jetpack_base.model.DetailModel
import com.liucj.lib_network.restful_kt.HiCall
import com.liucj.lib_network.restful_kt.annotation.GET
import com.liucj.lib_network.restful_kt.annotation.Path

interface DetailApi {
    @GET("goods/detail/{id}")
    fun  queryDetail(@Path("id") goodsId:String):HiCall<DetailModel>
}