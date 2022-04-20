package com.liucj.lib_network.restful_kt.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @GET("/cities/all")
 *fun test(@Filed("province") int provinceId)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(RetentionPolicy.RUNTIME)
annotation class GET(val value: String)