package com.liucj.lib_network.cache_kt

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object HiStorage {
    fun <T> saveCache(key: String, body: T) {
        val cache = CacheKt()
        cache.key = key
        cache.data = toByteArray(body)
        CacheDatabaseKt.get().cacheDao.saveCache(cache)
    }

    fun <T> getCache(key: String): T? {
        var cache = CacheDatabaseKt.get().cacheDao.getCache(key)
        return (if (cache?.data != null) {
            toByteArray(cache.data)
        } else null) as? T
    }

    fun deleteCache(key: String) {
        val cache = CacheKt()
        cache.key = key
        CacheDatabaseKt.get().cacheDao.deleteCache(cache)
    }

    private fun <T> toByteArray(body: T): ByteArray? {
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(body)
            oos.flush()
            return baos.toByteArray()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            baos?.close()
            oos?.close()
        }
        return ByteArray(0)
    }

    private fun toObject(data: ByteArray?): Any? {
        var bais: ByteArrayInputStream? = null
        var ois: ObjectInputStream? = null
        try {
            bais = ByteArrayInputStream(data)
            ois = ObjectInputStream(bais)

            return ois.readObject()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bais?.close()
            ois?.close()
        }
        return null
    }
}