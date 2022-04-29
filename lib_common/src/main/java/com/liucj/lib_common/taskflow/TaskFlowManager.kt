package com.liucj.lib_common.taskflow

import android.os.Looper
import androidx.annotation.MainThread

object TaskFlowManager {

    fun addBlockTask(taskId: String): TaskFlowManager {
        TaskRuntime.addBlockTask(taskId)
        return this
    }

    fun addBlockTasks(vararg taskIds: String): TaskFlowManager {
        TaskRuntime.addBlockTasks(*taskIds)
        return this
    }

    @MainThread
    fun start(task: Task) {
        assert(Thread.currentThread() == Looper.getMainLooper().thread) {
            "start 方法必须在主线程中执行"
        }
        val startTask = if (task is Project) task.startTask else task
        TaskRuntime.traversalDependencyTreeAndInit(startTask)
        startTask.start()
        while (TaskRuntime.hasBlockTasks()) {
            try {
                //休息10ms
                Thread.sleep(10)
            } catch (ex: Exception) {

            }
            //主线程唤醒之后，存在着等待队列的任务
            while (TaskRuntime.hasWaitingTasks()){
                TaskRuntime.runWaitingTasks()
            }
        }

    }
}