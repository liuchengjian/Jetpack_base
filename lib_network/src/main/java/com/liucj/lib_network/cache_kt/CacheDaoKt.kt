package com.liucj.lib_network.cache_kt

import androidx.room.*

@Dao
interface CacheDaoKt {
    @Insert(entity = CacheKt::class, onConflict = OnConflictStrategy.REPLACE)
    fun saveCache(cache: CacheKt): Long

    @Query("select * from cache where`key`=:key")
    fun getCache(key: String): CacheKt?

    @Delete(entity = CacheKt::class)
    fun deleteCache(cache: CacheKt)
}