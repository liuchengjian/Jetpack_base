package com.liucj.lib_network.restful_kt

import java.lang.reflect.Type

interface HiConvert {
    fun <T> convert(rawData: String, dataType: Type): HiResponse<T>
}