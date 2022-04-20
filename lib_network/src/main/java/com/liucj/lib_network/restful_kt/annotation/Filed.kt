package com.liucj.lib_network.restful_kt.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @BaseUrl("https://api.devio.org/as/")
 *fun test(@Filed("province") int provinceId)
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class Filed(val value: String)