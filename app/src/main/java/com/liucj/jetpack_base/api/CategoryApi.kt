package com.liucj.jetpack_base.api

import com.liucj.jetpack_base.model.Subcategory
import com.liucj.jetpack_base.model.TabCategory
import com.liucj.lib_network.restful_kt.HiCall
import com.liucj.lib_network.restful_kt.annotation.GET
import com.liucj.lib_network.restful_kt.annotation.Path


interface CategoryApi {
    @GET("category/categories")
    fun queryCategoryList(): HiCall<List<TabCategory>>


    @GET("category/subcategories/{categoryId}")
    fun querySubcategoryList(@Path("categoryId") categoryId: String): HiCall<List<Subcategory>>
}