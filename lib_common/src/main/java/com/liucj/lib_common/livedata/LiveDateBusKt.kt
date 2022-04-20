package com.liucj.lib_common.livedata

import androidx.lifecycle.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 事件总线分发 kotlin版
 */
object LiveDateBusKt {

    private val eventMap = ConcurrentHashMap<String, StickyLiveData<*>>()
    fun <T> with(eventName: String): StickyLiveData<T> {
        //基于事件名称、订阅、分发消息
        //由于一个livedata只能发送 一种数据类型
        //所以不同event事件，需要使用不同的livedata实例去分发
        var liveData = eventMap[eventName]
        if (liveData == null) {
            liveData = StickyLiveData<T>(eventName)
            eventMap[eventName] = liveData;
        }
        return liveData as StickyLiveData<T>
    }
    /**
     * 实际上liveData黏性事件总线的实现方式 还有另外一套实现方式。
     * 一堆反射 获取LiveData的mVersion字段，来控制数据的分发与否，不够优雅。
     * <p>
     * 但实际上 是不需要那么干的。请看我们下面的实现方式。
     *
     * @param <T>
     */
    class StickyLiveData<T>(private val eventName: String) : LiveData<T>() {
        var mStickyData: T? = null
        var mVersion = 0

        fun setStickyDate(stickyData: T) {
            mStickyData = stickyData
            setValue(stickyData)
            //只能在主线程去发送数据
        }

        fun postStickyDate(stickyData: T) {
            mStickyData = stickyData
            postValue(stickyData)
            //可以在子线程发送数据
        }

        override fun setValue(value: T) {
            mVersion++
            super.setValue(value)
        }

        override fun postValue(value: T) {
            mVersion++
            super.postValue(value)
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)
        }

        fun observerSticky(owner: LifecycleOwner, sticky: Boolean, observer: Observer<in T>) {
            //运行指定注册的观察者 是否关系粘性数据
            //sticky = true ,如果之前已发送的数据，这个observer就会受到粘性事件
            owner.lifecycle.addObserver(LifecycleEventObserver { source, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    eventMap.remove(eventName)
                }
            })
            super.observe(owner, StickyObserver(this, sticky, observer))
        }

    }

    class StickyObserver<T>(val stickyLiveData: StickyLiveData<T>,
                            val sticky: Boolean,
                            val observer: Observer<in T>) : Observer<T> {
        private var lastVersion = stickyLiveData.mVersion
        override fun onChanged(t: T) {
            if (lastVersion >= stickyLiveData.mVersion) {
                //没有更新的数据发送
                if (sticky && stickyLiveData.mStickyData != null) {
                    observer.onChanged(stickyLiveData.mStickyData)
                }
                return
            }
            lastVersion = stickyLiveData.mVersion
            observer.onChanged(t)
        }

    }
}




