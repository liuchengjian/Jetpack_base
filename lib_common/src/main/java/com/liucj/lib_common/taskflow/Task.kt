package com.liucj.lib_common.taskflow

import androidx.core.os.TraceCompat
import com.liucj.lib_common.taskflow.TaskRuntime.executeTask
import com.liucj.lib_common.taskflow.TaskRuntime.taskComparator
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList

/**
 * id 任务名称
 * isAsyncTask 是否异步
 * delayMills 延时时间
 * priority 任务优先级
 */
abstract class Task @JvmOverloads constructor(
        val id: String,
        val isAsyncTask: Boolean = false,
        val delayMills: Long = 0,
        var priority: Int = 0
) : Runnable, Comparable<Task> {
    var executeTime: Long = 0
        //任务执行时间
        protected set
    var state: Int = TaskState.IDLE
        //任务状态
        protected set

    val dependTasks: MutableList<Task> = ArrayList()//当前task,依赖了那些前置任务，
    //只有当dependTasks集合中所有任务执行完，当前才可以执行

    val behindTasks: MutableList<Task> = ArrayList()//当前的task被哪些后置任务依赖，当前任务执行完，才能执行后置任务

    private val taskListeners: MutableList<TaskListener> = ArrayList()
    private var taskRunTimeListener:TaskRunTimeListener? = TaskRunTimeListener()

    //用于运行时log 输出统计
    val dependTasksName :MutableList<String> = ArrayList()

    fun addTaskListener(taskListener: TaskListener) {
        if (!taskListeners.contains(taskListener)) {
            taskListeners.add(taskListener)
        }
    }

    open fun start() {
        if (state != TaskState.IDLE) {
            throw RuntimeException("cannot run task $id again")
        }

        toStart()
        executeTime = System.currentTimeMillis()
        //执行当前任务
        executeTask(this)
    }
    override fun run() {
        //改变任务状态
        TraceCompat.beginSection(id)

        toRunning()

        run(id)//真正执行初始化任务代码的方法

        toFinish()
        //通知后置任务去执行
        notifyBehindTasks()
        //回收资源
        recycle()

        TraceCompat.endSection()
    }

    private fun recycle() {
        dependTasks.clear()
        behindTasks.clear()
        taskListeners.clear()
        taskRunTimeListener = null
    }

    private fun notifyBehindTasks() {
        //通知后置任务尝试执行
        if(behindTasks.isNotEmpty()){
            if(behindTasks.size>1){
                Collections.sort(behindTasks,taskComparator)
            }
        }
        //变量behindTasks 后置任务，通知他们，你的一个前置依赖任务已经执行完成
        for (behindTask in behindTasks){
            behindTask.dependTaskFinished(this)
        }

    }

    private fun dependTaskFinished(dependTask:Task) {
        //A behindTasks ->(B,C) A执行完成之后，B，C才可以执行
        //task->B,C ,,dependTask= A
        if(dependTasks.isEmpty()){
            return
        }
        //把A从B，C的前置依赖任务集合中移除
        dependTasks.remove(dependTask)
        //B,C的前置任务 是否都执行完成
        if(dependTasks.isEmpty()){
            start()
        }
    }
    //给当前task 添加一个前置任务
    open fun dependOn (dependTask: Task){
        var task = dependTask
        if(task!=this){
            if(dependTask is Project){
                task= dependTask.endTask
            }

            dependTasks.add(task)
            dependTasksName.add(task.id)

            //当前task 依赖了dependTask，那么我们还需要吧dependTask-里面的behindTask 添加进去当前的task
            //前置任务变当前任务，同理应该把当前任务添加到后置任务集合中
            if(!task.behindTasks.contains(this)){
                task.behindTasks.add(this)
            }
        }

    }

    open fun removeDependence(dependTask: Task){
        var task = dependTask
        //判断是不是当前任务
        if(task!=this){
            if(dependTask is Project){
                task= dependTask.endTask
            }
            dependTasks.remove(task)
            dependTasksName.remove(task.id)
            //移除前置任务，并把当前任务从后置任务中移除
            if(!task.behindTasks.contains(this)){
                task.behindTasks.remove(this)
            }
        }

    }
    //给当前任务添加后置任务
    open fun behind(behindTask:Task){
        var task = behindTask
        //判断是不是当前任务
        if(behindTask!=this){
            if(behindTask is Project){
                task= behindTask.startTask
            }
            behindTasks.add(task)
            //当前task,添加当behindTask的前面
            behindTask.dependOn(this)
        }
    }
    //给当前任务移除后置任务
    open fun removeBehind(behindTask:Task){
        var task = behindTask
        //判断是不是当前任务
        if(behindTask!=this){
            if(behindTask is Project){
                task= behindTask.startTask
            }
            behindTasks.remove(task)
            behindTask.removeDependence(this)
        }
    }

    private fun toStart() {
        state = TaskState.START
        TaskRuntime.setStateInfo(this)
        for (listener in  taskListeners){
            listener.onStart(this)
        }
        //输入日志
        taskRunTimeListener?.onStart(this)
    }
    private fun toRunning() {
        state = TaskState.RUNNING
        TaskRuntime.setStateInfo(this)
        TaskRuntime.setThreadName(this,Thread.currentThread().name)
        for (listener in  taskListeners){
            listener.onRunning(this)
        }
        //输入日志
        taskRunTimeListener?.onRunning(this)
    }

    private fun toFinish() {
        state = TaskState.FINISHED
        TaskRuntime.setStateInfo(this)
        TaskRuntime.removeBlockTask(this.id)
        for (listener in  taskListeners){
            listener.onFinished(this)
        }
        //输入日志
        taskRunTimeListener?.onFinished(this)
    }

    abstract fun run(id: String)

    override fun compareTo(other: Task): Int {
        return Util.compareTask(this,other)
    }
}