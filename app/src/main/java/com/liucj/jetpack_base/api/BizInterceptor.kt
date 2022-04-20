package com.liucj.jetpack_base.api

import com.liucj.lib_common.utils.SPUtil
import com.liucj.lib_network.restful_kt.HiInterceptor
import com.liucj.lib_network.restful_kt.HiRequest

class BizInterceptor : HiInterceptor {
    override fun intercept(chain: HiInterceptor.Chain): Boolean {
        val request = chain.request()
        val response = chain.response()
        if (chain.isRequestPeriod) {
            val boardingPass = SPUtil.getString("boarding-pass") ?: ""
//            request.addHeader("boarding-pass", boardingPass)
            request.addHeader("boarding-pass", "42EAAE0D371D4EF4210B5AFFD759D6EF")
            request.addHeader("auth-token", "MjAyMC0wNi0yMyAwMzoyNTowMQ==")
        } else if (response != null) {
            var outputBuilder = StringBuilder()
            val httpMethod: String =
                if (request.httpMethod == HiRequest.METHOD.GET) "GET" else "POST"
            val requestUrl: String = request.endPointUrl()
            outputBuilder.append("\n$requestUrl==>$httpMethod\n")


            if (request.headers != null) {
                outputBuilder.append("【headers\n")
                request.headers!!.forEach(action = {
                    outputBuilder.append(it.key + ":" + it.value)
                    outputBuilder.append("\n")
                })
                outputBuilder.append("headers】\n")
            }

            if (request.parameters != null && request.parameters!!.isNotEmpty()) {
                outputBuilder.append("【parameters==>\n")
                request.parameters!!.forEach(action = {
                    outputBuilder.append(it.key + ":" + it.value + "\n")
                })
                outputBuilder.append("parameters】\n")
            }

            outputBuilder.append("【response==>\n")
            outputBuilder.append(response.rawData + "\n")
            outputBuilder.append("response】\n")

//            HiLog.dt("BizInterceptor Http:", outputBuilder.toString())
        }

        return false
    }

}