package com.liucj.jetpack_base.model

/**
 * {
"subcategoryId": "1",
"groupName": null,
"categoryId": "1",
"subcategoryName": "限时秒杀",
"subcategoryIcon": "https://o.devio.org/images/as/images/2018-05-16/26c916947489c6b2ddd188ecdb54fd8d.png",
"showType": "1"
}
 */
data class Subcategory(
    val categoryId: String,
    val groupName: String,
    val showType: String,
    val subcategoryIcon: String,
    val subcategoryId: String,
    val subcategoryName: String
)