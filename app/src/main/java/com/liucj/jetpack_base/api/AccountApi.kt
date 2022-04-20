package com.liucj.jetpack_base.api

import com.liucj.lib_network.restful_kt.HiCall
import com.liucj.lib_network.restful_kt.annotation.Filed
import com.liucj.lib_network.restful_kt.annotation.POST

interface AccountApi {
    @POST("user/login")
    fun login(
            @Filed("userName") userName: String,
            @Filed("password") password: String
    ): HiCall<String>
}