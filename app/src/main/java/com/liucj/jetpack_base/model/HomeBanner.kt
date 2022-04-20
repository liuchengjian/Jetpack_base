package com.liucj.jetpack_base.model

/**
 *  {
"id": "5",
"sticky": 1,
"type": "goods",
"title": "商品推荐",
"subtitle": "2019新款X27水滴屏6.5英寸全网通4G指纹人脸美颜游戏一体智能手机",
"url": "1574920889775",
"cover": "https://o.devio.org/images/as/goods/images/2019-11-14/8ad2ad11-afa3-4ae8-a816-860ec82f7b37.jpeg",
"createTime": "2020-03-23 11:24:57"
}
 */
data class HomeBanner(
    val cover: String,
    val createTime: String,
    val id: String,
    val sticky: Int,
    val subtitle: String,
    val title: String,
    val type: String,
    val url: String
) {
    companion object {
        const val TYPE_GOODS = "goods"
        const val TYPE_RECOMMEND = "recommend"
    }
}