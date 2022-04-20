package com.liucj.lib_network.restful_kt.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

//可以标记在标题上，也可以标记在参数上
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class CacheStrategy(val value: Int = NET_ONLY) {
    companion object {
        const val CACHE_FIRST = 0//请求接口时先读取本地缓存，再读取接口，请求成功后更新缓存，页面初始化默认
        const val NET_ONLY = 1//仅仅只请求接口，一般是分页和独立非列表界面
        const val NET_CACHE = 2//先接口，接口成功后更新缓存（一般是下拉刷新）
    }
}