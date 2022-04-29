package com.liucj.lib_common.taskflow

import android.util.SparseArray

/**
 * 运行时task信息的封装
 */
class TaskRunTimeInfo(val task: Task) {

    val stateTime = SparseArray<Long>()
    var isBlockTask = false
    var threadName: String? = null

    fun setStateTime(@TaskState state: Int, time: Long) {
        stateTime.put(state, time)
    }

    fun isSameTask(task: Task?): Boolean {
        return task != null && this.task === task
    }

    override fun toString(): String {
        return "TaskRunTimeInfo{task=$task, stateTime=$stateTime, isBlockTask=$isBlockTask, threadName=$threadName}"
    }


}