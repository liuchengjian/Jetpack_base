package com.liucj.lib_network.restful_kt

import android.util.Log
import com.liucj.lib_common.executor.HiExecutor
import com.liucj.lib_common.utils.MainHandler
import com.liucj.lib_network.cache.CacheManager.getCache
import com.liucj.lib_network.cache_kt.HiStorage
import com.liucj.lib_network.restful_kt.annotation.CacheStrategy

/**
 * 代理CallFactory创建出来的call对象，从而实现拦截器的派发动作
 */
class Scheduler(
        private val callFactory: HiCall.Factory,
        private val interceptors: MutableList<HiInterceptor>
) {
    fun newCall(request: HiRequest): HiCall<*> {
        val newCall: HiCall<*> = callFactory.newCall(request)
        return ProxyCall(newCall, request)
    }


    internal inner class ProxyCall<T>(
            private val delegate: HiCall<T>,
            private val request: HiRequest
    ) : HiCall<T> {
        override fun execute(): HiResponse<T> {
            dispatchInterceptor(request, null)
            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST) {
                val cacheResponse = readCache<T>()
                if (cacheResponse.data != null) {
                    return cacheResponse
                }
            }
            val response = delegate.execute()
            saveCacheIfNeed(response)

            dispatchInterceptor(request, response)

            return response
        }

        override fun enqueue(callback: HiCallback<T>) {
            dispatchInterceptor(request, null)
            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST) {
                //开启线程，加载缓存
                HiExecutor.execute(runnable = Runnable {
                    val cacheResponse = readCache<T>()
                    if (cacheResponse.data != null) {
                        //抛到主线程里面执行
                        MainHandler.sendAtFrontOfQueue(runnable = Runnable {
                            callback.onSuccess(cacheResponse)
                        })
                        Log.d("enqueue", "cache:" + request.getCacheKey())
                    }
                })
            }
            delegate.enqueue(object : HiCallback<T> {
                override fun onSuccess(response: HiResponse<T>) {
                    dispatchInterceptor(request, response)

                    saveCacheIfNeed(response)

                    callback.onSuccess(response)

                }

                override fun onFailed(throwable: Throwable) {
                    callback.onFailed(throwable)
                }

            })
        }

        private fun saveCacheIfNeed(response: HiResponse<T>) {
            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST ||
                    request.cacheStrategy == CacheStrategy.NET_CACHE) {
                if (response.data != null) {
                    //开启线程，保存缓存
                    HiExecutor.execute(runnable = Runnable {
                        HiStorage.saveCache(request.getCacheKey(), response.data)
                    })
                }
            }
        }

        private fun <T> readCache(): HiResponse<T> {
            //查询缓存，需要提供一个cache key
            //一般使用request 的 url+参数 ->key
            val cacheKey = request.getCacheKey()
            val cache = HiStorage.getCache<T>(cacheKey)
            val cacheResponse = HiResponse<T>()
            cacheResponse.data = cache
            cacheResponse.code = HiResponse.CACHE_SUCCESS
            cacheResponse.msg = "缓存获取成功"
            return cacheResponse;

        }

        private fun dispatchInterceptor(request: HiRequest, response: HiResponse<T>?) {
            if (interceptors.size <= 0)
                return
            InterceptorChain(request, response).dispatch()
        }


        internal inner class InterceptorChain(
                private val request: HiRequest,
                private val response: HiResponse<T>?
        ) : HiInterceptor.Chain {
            //代表的是 分发的第几个拦截器
            var callIndex: Int = 0

            override val isRequestPeriod: Boolean
                get() = response == null

            override fun request(): HiRequest {
                return request
            }

            override fun response(): HiResponse<*>? {
                return response
            }


            fun dispatch() {
                val interceptor = interceptors[callIndex]
                val intercept = interceptor.intercept(this)
                callIndex++
                if (!intercept && callIndex < interceptors.size) {
                    dispatch()
                }
            }

        }
    }

}

