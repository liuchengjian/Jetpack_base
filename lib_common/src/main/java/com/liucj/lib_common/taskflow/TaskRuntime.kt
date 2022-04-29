package com.liucj.lib_common.taskflow

import android.text.TextUtils
import android.util.Log
import com.liucj.lib_common.BuildConfig
import com.liucj.lib_common.executor.HiExecutor
import com.liucj.lib_common.utils.MainHandler
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap

/**
 * taskFlow运行时 的任务调度器
 */
internal object TaskRuntime {
    //通过addBlockTask(String name)指定启动节点 需要阻塞完成的任务
    //只有当blockTasksId当中的任务执行完了，
    // 才会释放application的阻塞,才会拉起launchActivity
    val blockTasksId: MutableList<String> = mutableListOf()

    //如果blockTasksId集合中的任务还没有完成，那么在主线程中执行的任务
    //会被添加到waitingTasks中去
    //目的是为了保证阻塞任务的优先完成,尽可能早的拉起launchActivity
    val waitingTasks: MutableList<Task> = mutableListOf()

    val taskRunTimeInfos: MutableMap<String, TaskRunTimeInfo> = HashMap()

    val taskComparator = Comparator<Task> { task1, task2 ->Util.compareTask(task1,task2) }


    @JvmStatic
    fun addBlockTask(id: String) {
        if (!TextUtils.isEmpty(id)) {
            blockTasksId.add(id)
        }
    }

    @JvmStatic
    fun addBlockTasks(vararg ids: String) {
        if (ids.isNotEmpty()) {
            for (id in ids) {
                addBlockTask(id)
            }
        }
    }

    @JvmStatic
    fun hasWaitingTasks(): Boolean {
        return waitingTasks.iterator().hasNext()
    }

    fun runWaitingTasks(){
        if(hasWaitingTasks()){
            if(waitingTasks.size>1){
               Collections.sort(waitingTasks,taskComparator)
            }
            if(hasWaitingTasks()){
                val head = waitingTasks.removeAt(0);
                head.run()
            }else{
               for (waitingTask in  waitingTasks){
                   MainHandler.postDelay(waitingTask.delayMills,waitingTask)
               }
                waitingTasks.clear()
            }
        }
    }

    @JvmStatic
    fun removeBlockTask(id: String) {
        blockTasksId.remove(id)
    }

    @JvmStatic
    fun hasBlockTasks(): Boolean {
        return blockTasksId.iterator().hasNext()
    }

    @JvmStatic
    fun setThreadName(task: Task, threadName: String?) {
        val taskRunTimeInfo = getTaskRunTimeInfo(task.id)
        taskRunTimeInfo?.threadName = threadName
    }

    @JvmStatic
    fun setStateInfo(task: Task) {
        val taskRunTimeInfo = getTaskRunTimeInfo(task.id)
        taskRunTimeInfo?.setStateTime(task.state, System.currentTimeMillis())
    }

    @JvmStatic
    fun getTaskRunTimeInfo(id: String): TaskRunTimeInfo? {
        return taskRunTimeInfos.get(id)
    }

    /**
     * 根据task的属性 以不同的策略 调度task
     */
    @JvmStatic
    fun executeTask(task: Task) {
        if (task.isAsyncTask) {
            HiExecutor.execute(runnable = task)
        } else {
            //主线程
            if (task.delayMills > 0 && !hasBlockBehindTask(task)) {
                //延时任务 ，若延时任务存在后置任务
                MainHandler.postDelay(task.delayMills, task)
                return
            }
            if (!hasBlockTasks()) {
                task.run()
            } else {
                addWaitingTask(task)
            }
        }

    }

    /**
     * 把一个主线程上需要执行的任务，但又不是紧急的任务，添加到等待队列
     */
    private fun addWaitingTask(task: Task) {
        if (!waitingTasks.contains(task)) {
            waitingTasks.add(task)
        }
    }

    /**
     * 检测延时任务中是否有后置 的阻塞任务
     */
    private fun hasBlockBehindTask(task: Task): Boolean {
        if (task is Project.CriticalTask) {
            return false
        }

        val behindTasks = task.behindTasks
        for (behindTask in behindTasks) {
            //需要判断每一个task 是不是阻塞任务
            val behindTaskInfo = getTaskRunTimeInfo(behindTask.id)
            if (behindTaskInfo != null && behindTaskInfo.isBlockTask) {
                return true
            } else {
                return hasBlockBehindTask(behindTask)
            }
        }
        return false
    }

    //校验 依赖树中是否存在环形依赖校验 ，依赖树中是否存在taskId相同的任务 初始化task
    @JvmStatic
    fun traversalDependencyTreeAndInit(task: Task) {
        val traversalVisitor = linkedSetOf<Task>()
        traversalVisitor.add(task)
        innerTraversalDependencyTreeAndInit(task, traversalVisitor)
        val iterator = blockTasksId.iterator()
        while (iterator.hasNext()) {
            val taskId = iterator.next()
            //用这个阻塞任务 是否存在依赖树中
            if (!taskRunTimeInfos.containsKey(taskId)) {
                throw RuntimeException("block task ${task.id} not in dependency tree")
            } else {
                val task = getTaskRunTimeInfo(taskId)?.task
                traversalDependencyPriority(task)
            }
        }
    }

    private fun traversalDependencyPriority(task: Task?) {
        if (task == null) return
        task.priority = 0
    }

    private fun innerTraversalDependencyTreeAndInit(
            task: Task,
            traversalVisitor: LinkedHashSet<Task>) {
        var taskRunTimeInfo = getTaskRunTimeInfo(task.id)
        if (taskRunTimeInfo == null) {
            //初始化 taskRunTimeInfo 并校验是否存在相同的任务名称 task.ID
            taskRunTimeInfo = TaskRunTimeInfo(task)
            if (blockTasksId.contains(task.id)) {
                taskRunTimeInfo!!.isBlockTask = true
            }
            taskRunTimeInfos[task.id] = taskRunTimeInfo
        } else {
            if (!taskRunTimeInfo.isSameTask(task)) {
                throw RuntimeException("not allow to contain the same id ${task.id}")
            }
        }
        //校验环形依赖
        for (behindTask in task.behindTasks) {
            if (!traversalVisitor.contains(behindTask)) {
                traversalVisitor.add(behindTask)
            } else {
                throw RuntimeException("not allow loopback dependency ,task id ${task.id}")
            }
            //对环形后面的依赖任务再次进行环形依赖检查
            if (BuildConfig.DEBUG && behindTask.behindTasks.isEmpty()) {
                //behindTask = end

                val iterator = traversalVisitor.iterator()
                val builder: StringBuilder = StringBuilder()
                while (iterator.hasNext()) {
                    builder.append(iterator.next().id)
                    builder.append(" --> ")
                }
                Log.e(TaskRunTimeListener.TAG, builder.toString())
            }
            innerTraversalDependencyTreeAndInit(behindTask, traversalVisitor)
            traversalVisitor.remove(behindTask)
        }
    }


}