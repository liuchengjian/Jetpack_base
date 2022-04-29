package com.liucj.lib_network.restful_kt.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
@Target(AnnotationTarget.FUNCTION)
@Retention(RetentionPolicy.RUNTIME)
annotation class PUT(val value: String, val formPost: Boolean = false)