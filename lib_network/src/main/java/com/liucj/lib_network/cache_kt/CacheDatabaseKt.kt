package com.liucj.lib_network.cache_kt

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.liucj.lib_common.AppGlobals

@Database(entities =[CacheKt::class],version = 1 )
abstract class CacheDatabaseKt :RoomDatabase(){
    companion object{
        private var database:CacheDatabaseKt
        fun get():CacheDatabaseKt{
            return database
        }

        init {
            val context = AppGlobals.getApplication()
            database =
                    Room.databaseBuilder(context,
                            CacheDatabaseKt::class.java,"ppjoke_cache").build()
        }
    }
    abstract val  cacheDao:CacheDaoKt
}