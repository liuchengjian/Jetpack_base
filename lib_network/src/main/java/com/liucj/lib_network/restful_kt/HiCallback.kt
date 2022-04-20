package com.liucj.lib_network.restful_kt


/**
 * callbak 回调
 */
interface HiCallback<T> {
    fun onSuccess(response: HiResponse<T>)
    fun onFailed(throwable: Throwable)
}