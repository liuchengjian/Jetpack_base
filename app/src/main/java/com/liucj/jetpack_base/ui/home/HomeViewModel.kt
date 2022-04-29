package com.liucj.jetpack_base.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.liucj.jetpack_base.api.ApiFactory
import com.liucj.jetpack_base.api.HomeApi
import com.liucj.jetpack_base.model.HomeModel
import com.liucj.jetpack_base.model.TabCategory
import com.liucj.lib_network.restful_kt.HiCallback
import com.liucj.lib_network.restful_kt.HiResponse

class HomeViewModel(private val savedState:SavedStateHandle) : ViewModel() {
     fun queryCategoryTabs(): MutableLiveData<List<TabCategory>?> {
         val liveData = MutableLiveData <List<TabCategory>?>()
//        val memCache = savedState.get<List<TabCategory>?>("categoryTabs")
//         if(memCache!=null){
//             liveData.postValue(memCache)
//             return liveData
//         }
         ApiFactory.create(HomeApi::class.java)
                .queryTabList()
                .enqueue(object : HiCallback<List<TabCategory>>{
                    override fun onSuccess(response: HiResponse<List<TabCategory>>) {
                    val  data  = response.data
                        if(response.successful()&&data!=null){
                            liveData.postValue(data!!)
//                            savedState.set("categoryTabs",data)
                        }else{
                            liveData.postValue(null)
                        }
                    }

                    override fun onFailed(throwable: Throwable) {
                        liveData.postValue(null)
                    }
                })
         return liveData
    }

     fun queryTabCategoryList(categoryId:String,pageIndex:Int):LiveData<HomeModel?> {
         val liveData = MutableLiveData <HomeModel?>()
//         val memCache = savedState.get<HomeModel?>("categoryList")
//         if(memCache!=null){
//             liveData.postValue(memCache)
//             return liveData
//         }
         ApiFactory.create(HomeApi::class.java)
                .queryTabCategoryList(categoryId!!, pageIndex, 10)
                .enqueue(object : HiCallback<HomeModel> {
                    override fun onSuccess(response: HiResponse<HomeModel>) {
                        if (response.successful() && response.data != null) {
                            liveData.postValue(response.data)
//                            savedState.set("categoryList",response.data)

                        } else {
                            liveData.postValue(null)
                        }
                    }

                    override fun onFailed(throwable: Throwable) {
                        //空数据页面
                        liveData.postValue(null)
                    }
                })
         return  liveData
    }
}