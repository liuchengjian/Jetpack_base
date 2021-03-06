package com.liucj.lib_common.taskflow

import android.util.Log
import com.liucj.lib_common.BuildConfig
import java.lang.StringBuilder

class TaskRunTimeListener : TaskListener {
    override fun onStart(task: Task) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, task.id + START_METHOD)
        }
    }

    override fun onRunning(task: Task) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, task.id + RUNNING_METHOD)
        }
    }

    override fun onFinished(task: Task) {
        LogTaskRunTimeInfo(task)
    }

    private fun LogTaskRunTimeInfo(task: Task) {
        val taskRunTimeInfo = TaskRuntime.getTaskRunTimeInfo(task.id) ?: return
        val startTime = taskRunTimeInfo.stateTime[TaskState.START]
        val runningTime = taskRunTimeInfo.stateTime[TaskState.RUNNING]
        val finishedTime = taskRunTimeInfo.stateTime[TaskState.FINISHED]

        val builder = StringBuilder()
        builder.append(WRAPPER)
        builder.append(TAG)
        builder.append(WRAPPER)

        builder.append(WRAPPER)
        builder.append(HALF_LINE)
        builder.append(if (task is Project) "Project" else "Task ${task.id}" + FINISHED_METHOD)
        builder.append(HALF_LINE)
        builder.append(WRAPPER)
        addTaskInfoLineInfo(builder, DEPENDENCIES, getTaskDepenciesInfo(task))
        addTaskInfoLineInfo(builder, IS_BLOCK_TASK, taskRunTimeInfo.isBlockTask.toString())
        addTaskInfoLineInfo(builder, THREAD_NAME, taskRunTimeInfo.threadName!!)
        addTaskInfoLineInfo(builder, START_TIME,startTime.toString()+"ms")
        addTaskInfoLineInfo(builder, WAITING_TIME,(runningTime-startTime).toString()+"ms")
        addTaskInfoLineInfo(builder, TASK_CONSUME,(finishedTime-runningTime).toString()+"ms")
        addTaskInfoLineInfo(builder, FINISHED_TIME,finishedTime.toString()+"ms")

        builder.append(HALF_LINE)
        builder.append(HALF_LINE)
        builder.append(WRAPPER)
        builder.append(WRAPPER)
        if(BuildConfig.DEBUG){
            Log.e(TAG, builder.toString())
        }

    }

    private fun getTaskDepenciesInfo(task: Task): String {
        val builder = StringBuilder()
        for (s in task.dependTasksName) {
            builder.append("$s ")
        }
        return builder.toString()
    }

    private fun addTaskInfoLineInfo(builder: StringBuilder, key: String, value: String) {
        builder.append("| $key:$value")
        builder.append(WRAPPER)

    }

    companion object {
        const val TAG: String = "TaskFlow"
        const val START_METHOD = "-- onStart --"
        const val RUNNING_METHOD = "-- onRunning --"
        const val FINISHED_METHOD = "-- onFinished --"
        const val DEPENDENCIES = "????????????"
        const val THREAD_NAME = "????????????"
        const val START_TIME = "??????????????????"
        const val WAITING_TIME = "??????????????????"
        const val TASK_CONSUME = "??????????????????"
        const val IS_BLOCK_TASK = "?????????????????????"
        const val FINISHED_TIME = "??????????????????"
        const val WRAPPER = "\n"
        const val HALF_LINE = "==================="


    }
}