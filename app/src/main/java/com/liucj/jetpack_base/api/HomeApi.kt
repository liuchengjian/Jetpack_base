package com.liucj.jetpack_base.api

import com.liucj.jetpack_base.model.HomeModel
import com.liucj.jetpack_base.model.TabCategory
import com.liucj.lib_network.restful_kt.HiCall
import com.liucj.lib_network.restful_kt.annotation.Filed
import com.liucj.lib_network.restful_kt.annotation.GET
import com.liucj.lib_network.restful_kt.annotation.Path

interface HomeApi {
    @GET("category/categories")
    fun queryTabList(): HiCall<List<TabCategory>>


    @GET("home/{categoryId}")
    fun queryTabCategoryList(
            @Path("categoryId") categoryId: String,
            @Filed("pageIndex") pageIndex: Int,
            @Filed("pageSize") pageSize: Int
    ): HiCall<HomeModel>
}